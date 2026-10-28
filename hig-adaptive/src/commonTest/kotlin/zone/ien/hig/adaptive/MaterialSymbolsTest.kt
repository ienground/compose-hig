package zone.ien.hig.adaptive

import kotlin.test.Test
import kotlin.test.assertTrue
import zone.ien.hig.adaptive.icons.MaterialSymbols

class MaterialSymbolsTest {
    @Test
    fun everyMaterialSymbolHasRenderableVectorPaths() {
        val symbols = listOf(
            MaterialSymbols.AccountBox,
            MaterialSymbols.AccountCircle,
            MaterialSymbols.Add,
            MaterialSymbols.AddCircle,
            MaterialSymbols.Build,
            MaterialSymbols.Call,
            MaterialSymbols.Check,
            MaterialSymbols.CheckCircle,
            MaterialSymbols.Clear,
            MaterialSymbols.Close,
            MaterialSymbols.Create,
            MaterialSymbols.DateRange,
            MaterialSymbols.Delete,
            MaterialSymbols.Done,
            MaterialSymbols.Edit,
            MaterialSymbols.Email,
            MaterialSymbols.ExitToApp,
            MaterialSymbols.Face,
            MaterialSymbols.Favorite,
            MaterialSymbols.FavoriteBorder,
            MaterialSymbols.Home,
            MaterialSymbols.Info,
            MaterialSymbols.KeyboardArrowDown,
            MaterialSymbols.KeyboardArrowLeft,
            MaterialSymbols.KeyboardArrowRight,
            MaterialSymbols.KeyboardArrowUp,
            MaterialSymbols.List,
            MaterialSymbols.LocationOn,
            MaterialSymbols.Lock,
            MaterialSymbols.MailOutline,
            MaterialSymbols.Menu,
            MaterialSymbols.MoreVert,
            MaterialSymbols.Notifications,
            MaterialSymbols.Person,
            MaterialSymbols.Phone,
            MaterialSymbols.Place,
            MaterialSymbols.PlayArrow,
            MaterialSymbols.Refresh,
            MaterialSymbols.Search,
            MaterialSymbols.Send,
            MaterialSymbols.Settings,
            MaterialSymbols.Share,
            MaterialSymbols.ShoppingCart,
            MaterialSymbols.Star,
            MaterialSymbols.ThumbUp,
            MaterialSymbols.Warning,
        )

        symbols.forEach { symbol ->
            assertTrue(symbol.root.size > 0, symbol.name)
        }
    }
}
