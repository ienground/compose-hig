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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.interop.LocalUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CompletableDeferred
import zone.ien.hig.theme.CupertinoTheme
import platform.UIKit.UIAlertController
import platform.UIKit.UIPresentationController
import platform.UIKit.UIViewController
import platform.UIKit.UIViewControllerAnimatedTransitioningProtocol
import platform.UIKit.UIViewControllerInteractiveTransitioningProtocol
import platform.UIKit.UIViewControllerTransitioningDelegateProtocol
import platform.UIKit.transitioningDelegate
import platform.UIKit.popoverPresentationController
import platform.darwin.NSObject
import platform.darwin.NSUInteger

private val EmptyTransitioningDelegate =
    object :
        NSObject(), UIViewControllerTransitioningDelegateProtocol {
        override fun hash(): NSUInteger = 123u
    }

@Suppress("UNUSED")
private class ControllerHolder<T: UIViewController>(
    val controller: T,
    val transitioningDelegateProtocol: UIViewControllerTransitioningDelegateProtocol,
    val dismissRequestGuard: PresentationControllerDismissGuard,
) {
    var isPresentationRequested: Boolean = false
}

internal class PresentationControllerDismissGuard(
    private val onDismissRequest: () -> Unit,
) {
    private var isDisposed = false
    private var wasRequested = false

    fun request() {
        if (!isDisposed && !wasRequested) {
            wasRequested = true
            onDismissRequest()
        }
    }

    fun dispose() {
        isDisposed = true
    }
}

internal class PresentationControllerSession {
    private var pendingDismissal: CompletableDeferred<Unit>? = null

    suspend fun awaitPendingDismissal() {
        pendingDismissal?.await()
    }

    fun beginDismissal(): CompletableDeferred<Unit> {
        val current = pendingDismissal
        if (current != null && !current.isCompleted) return current
        return CompletableDeferred<Unit>().also { pendingDismissal = it }
    }

    fun completeDismissal(completion: CompletableDeferred<Unit>) {
        completion.complete(Unit)
        if (pendingDismissal === completion) pendingDismissal = null
    }
}

@Composable
internal fun <T: UIViewController> PresentationController(
    factory: () -> T,
    update: T.() -> Unit,
    onDismissRequest: () -> Unit,
    vararg updateKeys: Any?,
    session: PresentationControllerSession,
    configurePresentation: T.(UIViewController) -> Unit = {},
) {
    val dark = CupertinoTheme.colorScheme.isDark
    val controller = LocalUIViewController.current
    val tController = remember(controller) { factory() }

    val updatedOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val dismissRequestGuard =
        remember(tController) {
            PresentationControllerDismissGuard {
                updatedOnDismissRequest()
            }
        }

    val currentDelegate =
        tController.transitioningDelegate
            ?: EmptyTransitioningDelegate

    val delegate =
        remember(tController, currentDelegate) {
            object: NSObject(), UIViewControllerTransitioningDelegateProtocol {
                override fun animationControllerForPresentedController(
                    presented: UIViewController,
                    presentingController: UIViewController,
                    sourceController: UIViewController,
                ): UIViewControllerAnimatedTransitioningProtocol? =
                    currentDelegate
                        .animationControllerForPresentedController(presented, presentingController, sourceController)

                override fun interactionControllerForPresentation(
                    animator: UIViewControllerAnimatedTransitioningProtocol,
                ): UIViewControllerInteractiveTransitioningProtocol? = currentDelegate.interactionControllerForPresentation(animator)

                override fun presentationControllerForPresentedViewController(
                    presented: UIViewController,
                    presentingViewController: UIViewController?,
                    sourceViewController: UIViewController,
                ): UIPresentationController? =
                    currentDelegate.presentationControllerForPresentedViewController(
                        presented,
                        presentingViewController,
                        sourceViewController,
                    )

                override fun animationControllerForDismissedController(
                    dismissed: UIViewController,
                ): UIViewControllerAnimatedTransitioningProtocol? {
                    if (dismissed == tController) dismissRequestGuard.request()
                    return currentDelegate.animationControllerForDismissedController(dismissed)
                }

                override fun hash(): NSUInteger = 1u
            }
        }

    val controllerHolder =
        remember(tController) {
            tController.transitioningDelegate = delegate

            ControllerHolder(tController, delegate, dismissRequestGuard)
        }

    LaunchedEffect(controllerHolder.controller, controller, dark, *updateKeys) {
        session.awaitPendingDismissal()
        controllerHolder.controller.applyTheme(dark)
        update(controllerHolder.controller)
        if (!controllerHolder.isPresentationRequested && controller.presentedViewController != controllerHolder.controller) {
            controllerHolder.controller.configurePresentation(controller)
            controllerHolder.isPresentationRequested = true
            controller.presentViewController(controllerHolder.controller, true) {}
        }
    }

    DisposableEffect(controller, controllerHolder) {
        onDispose {
            controllerHolder.dismissRequestGuard.dispose()
            if (controller.presentedViewController == controllerHolder.controller) {
                val dismissalCompletion = session.beginDismissal()
                controller.dismissViewControllerAnimated(true) {
                    session.completeDismissal(dismissalCompletion)
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun UIAlertController.anchorPopoverTo(presenter: UIViewController) {
    popoverPresentationController?.apply {
        sourceView = presenter.view
        sourceRect = presenter.view.bounds
    }
}
