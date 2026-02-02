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



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.w3c.dom.Document

@OptIn(ExperimentalComposeUiApi::class)
fun WasmApp() {
    koinInitialize()

    ComposeViewport(document.body!!) {
        var mobile by remember {
            mutableStateOf(true)
        }

        Column(
            Modifier
                .let {
                    if (mobile) {
                        it.widthIn(max = 400.dp)
                    } else {
                        it
                    }
                }.fillMaxSize()
                .padding(24.dp),
        ) {
            Row(
                Modifier
                    .align(Alignment.End),
            ) {
                Checkbox(
                    checked = mobile,
                    onCheckedChange = {
                        mobile = it
                    },
                )
                Text("Mobile")
            }

            App()
        }
    }
}

@JsFun("(document) => document.visibilityState")
private external fun visibilityState(document: Document): String
