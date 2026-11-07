import UIKit
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
        
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        if ProcessInfo.processInfo.arguments.contains("--hig-uikit-reference") {
            PickerReferenceView().ignoresSafeArea()
        } else {
            ComposeView().ignoresSafeArea()
        }
    }
}



struct PickerReferenceView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController { PickerReferenceController() }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
