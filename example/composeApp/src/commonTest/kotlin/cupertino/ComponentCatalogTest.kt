package cupertino

import RootRoute
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComponentCatalogTest {
    @Test
    fun searchesDescriptionsAndApiNamesTogether() {
        assertEquals(listOf(CatalogComponent.Stepper), filteredComponents("  STEPPER bounded  ", null))
        assertEquals(listOf(CatalogComponent.TextField), filteredComponents("secure", ComponentCategory.Input))
        assertTrue(filteredComponents("Stepper", ComponentCategory.Input).isEmpty())
    }

    @Test
    fun clearingFiltersRestoresAllComponents() {
        assertTrue(filteredComponents("no-such-component", null).isEmpty())
        assertEquals(CatalogComponent.entries.toList(), filteredComponents("  ", null))
    }

    @Test
    fun everyComponentRouteCanBeRestored() {
        CatalogComponent.entries.forEach { component ->
            val route = RootRoute.Component(component)
            val encoded = Json.encodeToString(RootRoute.Component.serializer(), route)
            assertEquals(route, Json.decodeFromString(RootRoute.Component.serializer(), encoded))
        }
    }
}
