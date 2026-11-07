import SwiftUI
import QuartzCore
import Darwin
import os
import shared


@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate

    @Environment(\.scenePhase)
    var scenePhase: ScenePhase

    init() {
        RootViewModelKt.koinInitialize()
    }

	var body: some Scene {
		WindowGroup {
            ContentView()
		}
	}
}


class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        HIGStressMonitor.shared.startIfRequested()
        return true
    }
}
// 실행 인자를 지정한 계측 실행에서만 동작한다.
@MainActor
final class HIGStressMonitor: NSObject {
    static let shared = HIGStressMonitor()
    private var link: CADisplayLink?
    private var observers: [NSObjectProtocol] = []
    private var phase = "startup"
    private var start = CACurrentMediaTime()
    private var previous: Double?
    private var firstCallbackDelayMs: Double?
    private var intervals: [Double] = []
    private var phases: [[String: Any]] = []
    private weak var composeLayer: CALayer?
    private var surface: ObjectIdentifier?
    private var lastSurfaceChange: Double?
    private var surfaceIntervals: [Double] = []
    private var nativeAttachments: [[String: Double]] = []
    private var lastNativeCount = -1
    private var oldIdleTimerDisabled = false
    private let writer = DispatchQueue(label: "zone.ien.hig.stress-report")
    private let traceLog = OSLog(subsystem: "zone.ien.hig.stress", category: .pointsOfInterest)

    func startIfRequested() {
        guard ProcessInfo.processInfo.arguments.contains("--hig-stress") else { return }
        oldIdleTimerDisabled = UIApplication.shared.isIdleTimerDisabled
        UIApplication.shared.isIdleTimerDisabled = true
        os_signpost(.begin, log: traceLog, name: "HIGStressPhase", "%{public}@", phase)
        observers.append(NotificationCenter.default.addObserver(forName: .init("HIGStressPhase"), object: nil, queue: .main) { [weak self] note in
            MainActor.assumeIsolated {
                guard let self, let name = note.userInfo?["case"] as? String else { return }
                self.closePhase()
                self.phase = name
                self.start = CACurrentMediaTime()
                os_signpost(.begin, log: self.traceLog, name: "HIGStressPhase", "%{public}@", name)
                self.previous = nil
                self.firstCallbackDelayMs = nil
                self.intervals = []
                self.surfaceIntervals = []
                self.lastSurfaceChange = nil
                self.nativeAttachments = []
                self.lastNativeCount = -1
                self.save(false)
            }
        })
        observers.append(NotificationCenter.default.addObserver(forName: .init("HIGStressComplete"), object: nil, queue: .main) { [weak self] _ in
            MainActor.assumeIsolated {
                guard let self else { return }
                self.closePhase()
                self.link?.invalidate()
                self.link = nil
                if !ProcessInfo.processInfo.arguments.contains("--hig-keep-awake") {
                    UIApplication.shared.isIdleTimerDisabled = self.oldIdleTimerDisabled
                }
                self.save(true)
            }
        })
        link = CADisplayLink(target: self, selector: #selector(frame(_:)))
        link?.add(to: .main, forMode: .common)
        save(false)
    }

    @objc private func frame(_ sender: CADisplayLink) {
        let now = CACurrentMediaTime()
        if firstCallbackDelayMs == nil { firstCallbackDelayMs = (now - start) * 1000 }
        if let previous { intervals.append((now - previous) * 1000) }
        previous = now
        if phase == "focused/native-pickers" && lastNativeCount < 3 {
            func countPickers(_ view: UIView) -> Int {
                if view is UIDatePicker { return 1 }
                return view.subviews.reduce(0) { $0 + countPickers($1) }
            }
            let count = UIApplication.shared.connectedScenes.compactMap { $0 as? UIWindowScene }
                .flatMap { $0.windows }.reduce(0) { $0 + countPickers($1) }
            if count != lastNativeCount {
                nativeAttachments.append(["count": Double(count), "elapsedMs": (now - start) * 1000])
                lastNativeCount = count
            }
        }
        if composeLayer == nil {
            func find(_ layer: CALayer) -> CALayer? {
                if NSStringFromClass(type(of: layer)) == "CMPMetalLayer" { return layer }
                for child in layer.sublayers ?? [] { if let found = find(child) { return found } }
                return nil
            }
            for scene in UIApplication.shared.connectedScenes.compactMap({ $0 as? UIWindowScene }) {
                for window in scene.windows { if let found = find(window.layer) { composeLayer = found } }
            }
        }
        if let contents = composeLayer?.contents {
            let next = ObjectIdentifier(contents as AnyObject)
            if next != surface {
                if let lastSurfaceChange { surfaceIntervals.append((now - lastSurfaceChange) * 1000) }
                surface = next
                lastSurfaceChange = now
            }
        }
    }

    private func closePhase() {
        os_signpost(.end, log: traceLog, name: "HIGStressPhase", "%{public}@", phase)
        var info = mach_task_basic_info()
        var count = mach_msg_type_number_t(MemoryLayout<mach_task_basic_info>.size) / 4
        let status = withUnsafeMutablePointer(to: &info) { pointer in
            pointer.withMemoryRebound(to: integer_t.self, capacity: Int(count)) {
                task_info(mach_task_self_, task_flavor_t(MACH_TASK_BASIC_INFO), $0, &count)
            }
        }
        phases.append(["name": phase, "duration": CACurrentMediaTime() - start,
                       "firstCallbackDelayMs": firstCallbackDelayMs ?? 0, "intervalsMs": intervals, "surfaceChangeIntervalsMs": surfaceIntervals,
                       "composeLayerFound": composeLayer != nil, "residentBytes": status == KERN_SUCCESS ? info.resident_size : 0,
                       "thermalState": ProcessInfo.processInfo.thermalState.rawValue,
                       "applicationState": UIApplication.shared.applicationState.rawValue, "nativePickerAttachments": nativeAttachments])
    }

    private func save(_ completed: Bool) {
        var hardware = utsname()
        uname(&hardware)
        let machine = withUnsafePointer(to: &hardware.machine) {
            $0.withMemoryRebound(to: CChar.self, capacity: 1) { String(cString: $0) }
        }
        let report: [String: Any] = ["completed": completed, "activeCase": phase,
            "hardware": machine, "systemVersion": UIDevice.current.systemVersion,
            "arguments": ProcessInfo.processInfo.arguments, "phases": phases]
        let url = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0].appendingPathComponent("hig-stress.json")
        guard let data = try? JSONSerialization.data(withJSONObject: report, options: [.sortedKeys]) else { return }
        writer.async { do { try data.write(to: url, options: .atomic) } catch { NSLog("계측 결과 저장 실패: %@", String(describing: error)) } }
    }
}

// Compose/라이브러리 코드를 거치지 않는 UIKit 대조군.
@MainActor
final class PickerReferenceController: UIViewController {
    private var link: CADisplayLink?
    private var scrollView: UIScrollView?
    private var clippers: [UIView] = []
    private var components: [UIView] = []
    private var mode = -1
    private var start: Double?
    private var frame = 0

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        link = CADisplayLink(target: self, selector: #selector(step))
        link?.add(to: .main, forMode: .common)
    }

    private func configure(_ next: Int) {
        view.subviews.forEach { $0.removeFromSuperview() }
        scrollView = nil
        clippers = []
        components = []
        frame = 0
        let names = ["uikit-scroll", "uikit-clipped", "uikit-contained", "uikit-zero-insets", "uikit-zero-safe-area", "uikit-zero-margins"]
        NotificationCenter.default.post(name: .init("HIGStressPhase"), object: nil, userInfo: ["case": "reference/" + names[next]])
        if next == 0 {
            let scroll = UIScrollView(frame: view.bounds)
            scroll.autoresizingMask = [.flexibleWidth, .flexibleHeight]
            scroll.contentSize = CGSize(width: view.bounds.width, height: 2500)
            view.addSubview(scroll)
            scrollView = scroll
        }
        for (index, pickerMode) in [UIDatePicker.Mode.date, .time, .dateAndTime].enumerated() {
            let picker: UIDatePicker = (next == 3 || next == 4)
                ? ZeroInsetsDatePicker(frame: CGRect(x: 0, y: 0, width: 320, height: 216))
                : UIDatePicker(frame: CGRect(x: 0, y: 0, width: 320, height: 216))
            picker.timeZone = TimeZone(secondsFromGMT: 0)
            picker.preferredDatePickerStyle = .wheels
            picker.datePickerMode = pickerMode
            var calendar = Calendar(identifier: .gregorian)
            calendar.timeZone = TimeZone(secondsFromGMT: 0)!
            let year = calendar.component(.year, from: Date())
            if index != 1 {
                let lower = index == 0 ? (year - 30) / 10 * 10 : year - 1
                let upper = index == 0 ? (year + 30) / 10 * 10 : year + 1
                picker.minimumDate = calendar.date(from: DateComponents(year: lower, month: 1, day: 1))
                picker.maximumDate = calendar.date(from: DateComponents(year: upper + 1, month: 1, day: 1))!.addingTimeInterval(-0.001)
            }
            if next == 3 || next == 5 { picker.insetsLayoutMarginsFromSafeArea = false }
            if let scrollView {
                picker.frame.origin = CGPoint(x: 16, y: 49 + index * 228)
                scrollView.addSubview(picker)
            } else {
                let clipper = UIView()
                clipper.clipsToBounds = true
                view.addSubview(clipper)
                clippers.append(clipper)
                if next == 2 {
                    let host = UIView(frame: picker.frame)
                    picker.autoresizingMask = [.flexibleWidth, .flexibleHeight]
                    host.addSubview(picker)
                    clipper.addSubview(host)
                    components.append(host)
                } else {
                    clipper.addSubview(picker)
                    components.append(picker)
                }
            }
        }
    }

    @objc private func step() {
        let now = CACurrentMediaTime()
        if start == nil { start = now }
        let elapsed = now - start!
        if elapsed < 5 { return }
        let cases = ProcessInfo.processInfo.arguments.contains("--hig-uikit-reference-focus") ? [3, 4, 5] : Array(0..<6)
        let nextIndex = Int((elapsed - 5) / 10)
        if nextIndex >= cases.count {
            link?.invalidate()
            link = nil
            NotificationCenter.default.post(name: .init("HIGStressComplete"), object: nil)
            return
        }
        let next = cases[nextIndex]
        if mode != next { mode = next; configure(next) }
        let step = frame % 120
        frame += 1
        let offset = CGFloat(step < 60 ? step : 120 - step) * 4
        if let scrollView {
            scrollView.setContentOffset(CGPoint(x: 0, y: offset), animated: false)
        } else {
            UIView.performWithoutAnimation {
                for index in components.indices {
                    let full = CGRect(x: 16, y: 49 + CGFloat(index * 228) - offset, width: 320, height: 216)
                    var clipped = full.intersection(view.bounds)
                    if clipped.isNull { clipped = CGRect(x: 16, y: 0, width: 320, height: 0) }
                    let relative = CGRect(x: full.minX - clipped.minX, y: full.minY - clipped.minY, width: 320, height: 216)
                    if clippers[index].frame != clipped { clippers[index].frame = clipped }
                    if components[index].frame != relative { components[index].frame = relative }
                }
            }
        }
    }
}


@MainActor
private final class ZeroInsetsDatePicker: UIDatePicker {
    override var safeAreaInsets: UIEdgeInsets { .zero }
}
