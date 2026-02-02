package navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RootRoute: NavKey {
//    @Serializable data object
}