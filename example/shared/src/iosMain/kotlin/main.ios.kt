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



import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.UIKit.addChildViewController
import platform.UIKit.didMoveToParentViewController
import platform.UIKit.removeFromParentViewController
import platform.UIKit.willMoveToParentViewController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import zone.ien.navigation.NativeNavigationViewController
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import zone.ien.hig.enableCupertinoParallelRenderingFix
import platform.Foundation.NSProcessInfo

actual val IsIos: Boolean
    get() = true

@OptIn(ExperimentalComposeUiApi::class)
fun MainViewController(): UIViewController {
    if (!NSProcessInfo.processInfo.arguments.contains("--hig-original-renderer")) {
        check(enableCupertinoParallelRenderingFix()) { "Compose 병렬 렌더링 보정을 적용하지 못했습니다" }
    }
    if (!NSProcessInfo.processInfo.arguments.contains("--hig-stress")) {
        return NavigationSampleController()
    }
    return ComposeUIViewController(
        configure = {
//            onFocusBehavior = OnFocusBehavior.DoNothing
            parallelRendering = !NSProcessInfo.processInfo.arguments.contains("--hig-serial-renderer")
        },
    ) {
        DeviceStressScreen()
    }
}

private class NavigationSampleController : UIViewController(nibName = null, bundle = null) {
    private val viewModel = RootViewModel()
    private val backStack = NavBackStack<NavKey>(RootRoute.Cupertino)
    private var nativeNavigation: Boolean? = null
    private var currentController: UIViewController? = null
    private val cmpController by lazy { createController(useNative = false) }
    private val nativeController by lazy { createController(useNative = true) }

    override fun viewDidLoad() {
        super.viewDidLoad()
        showNavigation(viewModel.uiState.item.useNativeNavigation)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    private fun createController(useNative: Boolean): UIViewController {
        val content: @Composable () -> Unit = {
            val requested = viewModel.uiState.item.useNativeNavigation
            LaunchedEffect(requested) {
                dispatch_async(dispatch_get_main_queue()) {
                    if (viewModel.uiState.item.useNativeNavigation == requested) showNavigation(requested)
                }
            }
            if (requested == useNative) {
                App(viewModel = viewModel, backStack = backStack, nativeNavigation = useNative)
            }
        }
        return if (useNative) {
            NativeNavigationViewController(content = content)
        } else {
            ComposeUIViewController(configure = {
                parallelRendering = !NSProcessInfo.processInfo.arguments.contains("--hig-serial-renderer")
            }, content = content)
        }
    }

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    private fun showNavigation(useNative: Boolean) {
        if (nativeNavigation == useNative) return
        nativeNavigation = useNative
        val next = if (useNative) nativeController else cmpController
        currentController?.let { previous ->
            previous.willMoveToParentViewController(null)
            previous.view.removeFromSuperview()
            previous.removeFromParentViewController()
        }
        addChildViewController(next)
        next.view.setFrame(view.bounds)
        next.view.autoresizingMask = UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight
        view.addSubview(next.view)
        next.didMoveToParentViewController(this)
        currentController = next
    }
}
