/*
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

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

@Composable
internal actual fun rememberCupertinoPickerTickSound(): () -> Unit {
    val context = LocalContext.current
    val view = LocalView.current
    val audioManager = remember(context) { context.getSystemService(AudioManager::class.java) }
    val track = remember {
        val sampleRate = 44100
        val samples = ShortArray(441) { index ->
            val time = index.toDouble() / sampleRate
            val envelope = (1.0 - exp(-time * 12000)) * exp(-time * 650)
            val tone = sin(2 * PI * 2200 * time) + .35 * sin(2 * PI * 4400 * time)
            (tone * envelope * 9500).toInt().toShort()
        }
        AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
            .setAudioFormat(AudioFormat.Builder().setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
            .setTransferMode(AudioTrack.MODE_STATIC)
            .setBufferSizeInBytes(samples.size * 2)
            .build().apply { write(samples, 0, samples.size) }
    }
    DisposableEffect(track) { onDispose { track.release() } }
    return remember(track, view, audioManager) {
        {
            if (view.isSoundEffectsEnabled && audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL &&
                Settings.System.getInt(context.contentResolver, Settings.System.SOUND_EFFECTS_ENABLED, 1) != 0 &&
                track.state == AudioTrack.STATE_INITIALIZED
            ) {
                track.stop()
                track.reloadStaticData()
                track.play()
            }
        }
    }
}
