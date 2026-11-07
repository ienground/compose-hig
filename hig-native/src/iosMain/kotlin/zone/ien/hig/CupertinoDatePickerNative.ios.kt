/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
 * Copyright (c) 2026. IENGROUND of IENLAB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package zone.ien.hig

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.RememberObserver
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.uikit.LocalUIViewController
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import zone.ien.hig.theme.CupertinoTheme
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.cValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeZero
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.WeakReference
import platform.Foundation.NSMapTable
import platform.Foundation.NSRunLoop
import platform.Foundation.NSRunLoopCommonModes
import platform.Foundation.NSThread
import platform.Foundation.localeIdentifier
import platform.Foundation.NSDate
import platform.Foundation.NSLocale
import platform.Foundation.NSSelectorFromString
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.currentLocale
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.timeZoneWithName
import platform.QuartzCore.CADisplayLink
import platform.QuartzCore.CACurrentMediaTime
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.UIKit.UIControlEventValueChanged
import platform.UIKit.UIDatePicker
import platform.UIKit.UIEdgeInsets
import platform.UIKit.UIEdgeInsetsMake
import platform.UIKit.UIDatePickerMode
import platform.UIKit.UIDatePickerStyle
import platform.UIKit.UIView

/**
 * An iOS implementation of the date picker native component.
 *
 * @param state the date picker state
 * @param modifier the [Modifier] to be applied to this date picker
 * @param style the date picker style, either wheel or pager
 * @param containerColor the color to apply to the background of the date picker
 */
@OptIn(InternalCupertinoApi::class)
@Composable
@ExperimentalCupertinoApi
actual fun CupertinoDatePickerNative(
    state: CupertinoDatePickerState,
    modifier: Modifier,
    style: DatePickerStyle,
    containerColor: Color,
) {
    LaunchedEffect(0) {
        state.isManual = true
    }

    CupertinoDatePickerNativeImpl(
        millis = state.selectedDateMillis,
        onChange = {
            state.setSelection(it)
        },
        modifier = modifier,
        mode = UIDatePickerMode.UIDatePickerModeDate,
        style = style,
        containerColor = containerColor,
        yearRange = state.yearRange,
    )
}

/**
 * Composable that creates a native iOS UIDatePicker for use inside a [UIKitView].
 *
 * 같은 컨트롤러에서 휠 여러 개를 준비할 때는 측정된 크기를 유지하며 후속 생성을 표시 프레임에 나눕니다.
 *
 * @param millis the initial date in milliseconds since epoch
 * @param mode the UIDatePickerMode
 * @param onChange callback when the date changed
 * @param modifier the [Modifier] to be applied to this date picker
 * @param style the date picker style, either wheel or pager
 * @param containerColor the color to apply to the background of the date picker
 * @param yearRange 허용되는 연도 범위. `null`이면 날짜 범위를 제한하지 않습니다.
 */
@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun CupertinoDatePickerNativeImpl(
    millis: Long,
    mode: UIDatePickerMode,
    onChange: (Long) -> Unit,
    modifier: Modifier,
    style: DatePickerStyle,
    containerColor: Color,
    yearRange: IntRange? = null,
) {
    val updatedOnChange by rememberUpdatedState(onChange)

    val controller = LocalUIViewController.current
    val budget = remember(controller) { nativePickerBudget(controller) }
    val nativeStyle = style.toNativeStyle()
    val sizeKey = NSLocale.currentLocale.localeIdentifier + "/" + controller.traitCollection.preferredContentSizeCategory
    val dark = CupertinoTheme.colorScheme.isDark
    val createPicker = {
        val picker = DatePicker(millis, mode, style, yearRange) { updatedOnChange(it) }
        val size = picker.measureSizeForStyle(nativeStyle)
        if (nativeStyle == UIDatePickerStyle.UIDatePickerStyleWheels) budget.cacheWheelSize(sizeKey, size)
        NativePickerReady(picker, nativeStyle, size)
    }
    val latestFactory by rememberUpdatedState(createPicker)
    val initialization = remember(controller) {
        val cached = if (nativeStyle == UIDatePickerStyle.UIDatePickerStyleWheels) budget.cachedWheelSize(sizeKey) else null
        val slot = budget.beginImmediately(force = cached == null)
        if (slot != null) {
            try {
                NativePickerInitialization(budget, createPicker(), slot, null)
            } catch (error: Throwable) {
                budget.complete(slot)
                throw error
            }
        } else NativePickerInitialization(budget, null, null, checkNotNull(cached))
    }
    LaunchedEffect(initialization) {
        if (initialization.ready == null) {
            val slot = budget.awaitSlot()
            try {
                coroutineContext.ensureActive()
                val ready = latestFactory()
                coroutineContext.ensureActive()
                initialization.ready = ready
            } finally {
                budget.complete(slot)
            }
        }
    }
    val ready = initialization.ready
    if (ready == null) {
        Box(modifier.size(checkNotNull(initialization.placeholderSize)).background(containerColor))
        return
    }
    val datePicker = ready.picker
    val size = remember(datePicker, nativeStyle) {
        if (nativeStyle == ready.measuredStyle) ready.size else datePicker.measureSizeForStyle(nativeStyle)
    }

    UIKitView<UIDatePicker>(
        factory = {
            datePicker.apply {
                applyTheme(dark)
            }
        },
        modifier =
            modifier
                .size(size),
        update = {
            it.preferredDatePickerStyle = nativeStyle
            datePicker.updateRangeAndSelection(yearRange, millis)
            (it.subviews.firstOrNull() as UIView?)?.backgroundColor = containerColor.toUIColor()
            it.applyTheme(dark)
        },
        properties =
            UIKitInteropProperties(
                interactionMode = UIKitInteropInteractionMode.NonCooperative,
                isNativeAccessibilityEnabled = true,
            ),
    )
}

private data class NativePickerReady(val picker: DatePicker, val measuredStyle: UIDatePickerStyle, val size: DpSize)

private class NativePickerInitialization(
    private val budget: NativePickerCreationBudget,
    initial: NativePickerReady?,
    private val initialSlot: Long?,
    val placeholderSize: DpSize?,
) : RememberObserver {
    var ready by mutableStateOf(initial)
    private var retained = true
    init { budget.retainClient() }
    override fun onRemembered() { initialSlot?.let(budget::complete) }
    override fun onForgotten() = dispose()
    override fun onAbandoned() = dispose()
    private fun dispose() {
        if (retained) {
            retained = false
            initialSlot?.let(budget::complete)
            budget.releaseClient()
        }
    }
}

private val nativePickerBudgets = NSMapTable.weakToStrongObjectsMapTable()

private fun nativePickerBudget(controller: UIViewController): NativePickerCreationBudget =
    (nativePickerBudgets.objectForKey(controller) as? NativePickerCreationBudget)
        ?: NativePickerCreationBudget().also { nativePickerBudgets.setObject(it, forKey = controller) }

@OptIn(ExperimentalNativeApi::class, ExperimentalForeignApi::class)
internal class NativePickerCreationBudget(private val now: () -> Double = { CACurrentMediaTime() }) {
    private val sizes = mutableMapOf<String, DpSize>()
    private val pending = mutableListOf<WeakReference<CancellableContinuation<Long>>>()
    private val inFlight = mutableSetOf<Long>()
    private var clients = 0
    private var nextSlot = 0L
    private var notBefore = Double.NEGATIVE_INFINITY
    private var lastFrame = Double.NEGATIVE_INFINITY
    private var displayLink: CADisplayLink? = null

    fun cachedWheelSize(key: String): DpSize? = sizes[key]
    fun cacheWheelSize(key: String, size: DpSize) { sizes[key] = size }
    fun retainClient() { clients++ }
    fun releaseClient() {
        check(clients > 0)
        clients--
        if (clients == 0) {
            val abandoned = pending.mapNotNull { it.get() }
            pending.clear()
            inFlight.clear()
            stop()
            abandoned.forEach { it.cancel() }
        }
    }

    fun beginImmediately(force: Boolean = false): Long? {
        if (!force && (displayLink != null || inFlight.isNotEmpty() || pending.isNotEmpty())) return null
        return (++nextSlot).also { inFlight += it }
    }

    fun complete(slot: Long) {
        if (!inFlight.remove(slot)) return
        notBefore = now()
        if (clients > 0 || pending.isNotEmpty()) start() else stop()
    }

    suspend fun awaitSlot(): Long = suspendCancellableCoroutine { continuation ->
        val entry = WeakReference(continuation)
        pending += entry
        continuation.invokeOnCancellation {
            onMain {
                pending.remove(entry)
                if (clients == 0 && inFlight.isEmpty() && pending.isEmpty()) stop()
            }
        }
        if (continuation.isActive) start()
    }

    fun advanceFrame(timestamp: Double) {
        if (timestamp <= notBefore || timestamp <= lastFrame || inFlight.isNotEmpty()) return
        var continuation: CancellableContinuation<Long>? = null
        while (pending.isNotEmpty() && continuation == null) {
            continuation = pending.removeAt(0).get()?.takeIf { it.isActive }
        }
        if (continuation == null) {
            stop()
            return
        }
        lastFrame = timestamp
        val slot = checkNotNull(beginImmediately(force = true))
        continuation.resume(slot, onCancellation = { _, value, _ -> onMain { complete(value) } })
    }

    private fun start() {
        if (displayLink == null) {
            displayLink = CADisplayLink.displayLinkWithTarget(NativePickerFrameTarget(this), NSSelectorFromString("frame:"))
                .also { it.addToRunLoop(NSRunLoop.mainRunLoop, NSRunLoopCommonModes) }
        }
    }
    private fun stop() { displayLink?.invalidate(); displayLink = null }
    private fun onMain(block: () -> Unit) {
        if (NSThread.isMainThread) block() else dispatch_async(dispatch_get_main_queue(), block)
    }
}

@OptIn(ExperimentalNativeApi::class)
private class NativePickerFrameTarget(budget: NativePickerCreationBudget) : NSObject() {
    private val budget = WeakReference(budget)
    @OptIn(BetaInteropApi::class)
    @ObjCAction
    fun frame(sender: CADisplayLink) {
        val owner = budget.get()
        if (owner == null) sender.invalidate() else owner.advanceFrame(sender.timestamp)
    }
}

private fun DatePickerStyle.toNativeStyle(): UIDatePickerStyle =
    when (this) {
        is DatePickerStyle.Wheel -> UIDatePickerStyle.UIDatePickerStyleWheels
        is DatePickerStyle.Pager -> UIDatePickerStyle.UIDatePickerStyleInline
    }

@OptIn(ExperimentalForeignApi::class)
internal fun UIDatePicker.measureSizeForStyle(style: UIDatePickerStyle): DpSize {
    preferredDatePickerStyle = style
    return sizeThatFits(cValue { CGSizeZero })
        .useContents { DpSize(width.dp, height.dp) }
}

/**
 * A UIDatePicker subclass that handles date selection and UI configuration.
 *
 * @param millis the initial date in milliseconds since epoch
 * @param mode the UIDatePickerMode
 * @param style the date picker style, either wheel or pager
 * @param onChange callback when the date changed
 */
@OptIn(ExperimentalForeignApi::class)
private class DatePicker(
    millis: Long,
    mode: UIDatePickerMode,
    style: DatePickerStyle,
    yearRange: IntRange?,
    private val onChange: (Long) -> Unit,
): UIDatePicker(CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    // Compose가 처리한 inset을 클리핑 중인 휠 내부에 다시 적용하면 매 프레임 행을 재배치한다.
    override fun safeAreaInsets(): CValue<UIEdgeInsets> = UIEdgeInsetsMake(0.0, 0.0, 0.0, 0.0)

    private var appliedYearRange = yearRange
    private var isUpdatingSelection = false

    init {
        timeZone = NSTimeZone.timeZoneWithName("UTC")
        locale = NSLocale.currentLocale
        setDate(
            date =
                NSDate.dateWithTimeIntervalSince1970(
                    millis / 1000.0,
                ),
            animated = false,
        )
        datePickerMode = mode
        preferredDatePickerStyle =
            when (style) {
                is DatePickerStyle.Wheel -> UIDatePickerStyle.UIDatePickerStyleWheels
                is DatePickerStyle.Pager -> UIDatePickerStyle.UIDatePickerStyleInline
            }
        applyYearRange(yearRange)
        addTarget(
            target = this,
            action = NSSelectorFromString("changed:"),
            forControlEvents = UIControlEventValueChanged,
        )
    }

    fun updateRangeAndSelection(
        yearRange: IntRange?,
        millis: Long,
    ) {
        isUpdatingSelection = true
        try {
            if (appliedYearRange != yearRange) {
                applyYearRange(yearRange)
                appliedYearRange = yearRange
            }
            setDate(NSDate.dateWithTimeIntervalSince1970(millis / 1000.0), animated = false)
        } finally {
            isUpdatingSelection = false
        }
    }

    @OptIn(BetaInteropApi::class)
    @ObjCAction
    @Suppress("UNUSED")
    fun changed(v: DatePicker) {
        if (!isUpdatingSelection) onChange((v.date.timeIntervalSince1970 * 1000).toLong())
    }
}

/** UIDatePicker에 UTC 기준 연도 경계를 적용합니다. `null`이면 기존 날짜 제한을 해제합니다. */
@OptIn(ExperimentalForeignApi::class)
internal fun UIDatePicker.applyYearRange(yearRange: IntRange?) {
    minimumDate = null
    maximumDate = null

    if (yearRange != null) {
        val minimumMillis = LocalDate(yearRange.first, 1, 1).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        val maximumMillis =
            LocalDate(yearRange.last, 12, 31)
                .atTime(23, 59, 59, 999_000_000)
                .toInstant(TimeZone.UTC)
                .toEpochMilliseconds()
        minimumDate = NSDate.dateWithTimeIntervalSince1970(minimumMillis / 1000.0)
        maximumDate = NSDate.dateWithTimeIntervalSince1970(maximumMillis / 1000.0)
    }
}
