package cupertino

enum class ComponentCategory(val title: String) {
    Actions("Actions"),
    Selection("Selection"),
    Input("Input"),
    Feedback("Feedback"),
    Presentation("Presentation"),
    Layout("Layout"),
}

enum class CatalogComponent(
    val title: String,
    val category: ComponentCategory,
    val description: String,
    val apiNames: String,
) {
    Button("Button", ComponentCategory.Actions, "Buttons with filled, tinted and plain styles.", "CupertinoButton CupertinoIconButton"),
    LiquidButton("Liquid Button", ComponentCategory.Actions, "Interactive glass buttons with background refraction.", "CupertinoLiquidButton CupertinoLiquidIconButton"),
    FloatingActionButton("Floating Action Button", ComponentCategory.Actions, "Prominent actions in four sizes.", "CupertinoSmallFloatingActionButton CupertinoMediumFloatingActionButton CupertinoLargeFloatingActionButton CupertinoExtraLargeFloatingActionButton"),
    Switch("Switch", ComponentCategory.Selection, "Toggle a value with a tap or drag.", "CupertinoSwitch"),
    Checkbox("Checkbox", ComponentCategory.Selection, "Individual, grouped and indeterminate selections.", "CupertinoCheckBox CupertinoTriStateCheckBox"),
    Slider("Slider", ComponentCategory.Selection, "Continuous values, discrete steps and ranges.", "CupertinoSlider CupertinoRangeSlider"),
    Stepper("Stepper", ComponentCategory.Selection, "Increase or decrease a bounded value.", "CupertinoStepper"),
    SegmentedControl("Segmented Control", ComponentCategory.Selection, "Switch between related views.", "CupertinoSegmentedControl CupertinoSegmentedControlTab"),
    PageControl("Page Control", ComponentCategory.Selection, "Navigate a sequence of pages.", "CupertinoPageControl"),
    TextField("Text Field", ComponentCategory.Input, "Plain, bordered and secure text entry.", "CupertinoTextField CupertinoBorderedTextField CupertinoSecureTextField CupertinoBorderedSecureTextField"),
    SearchField("Search Field", ComponentCategory.Input, "Search, clear and cancel.", "CupertinoSearchTextField rememberCupertinoSearchTextFieldState"),
    WheelPicker("Wheel Picker", ComponentCategory.Input, "Scroll through a finite or repeating list.", "CupertinoWheelPicker rememberCupertinoPickerState"),
    DatePicker("Date Picker", ComponentCategory.Input, "Choose a date with a wheel or calendar.", "CupertinoDatePicker CupertinoDatePickerNative"),
    TimePicker("Time Picker", ComponentCategory.Input, "Choose a time in 12- or 24-hour format.", "CupertinoTimePicker CupertinoTimePickerNative"),
    DateTimePicker("Date & Time Picker", ComponentCategory.Input, "Choose a date and time together.", "CupertinoDateTimePicker CupertinoDateTimePickerNative"),
    Progress("Progress", ComponentCategory.Feedback, "Determinate progress and activity indicators.", "CupertinoActivityIndicator CupertinoProgressIndicator"),
    Badge("Badge", ComponentCategory.Feedback, "Compact counts and status labels.", "CupertinoBadge"),
    EmptyState("Empty State", ComponentCategory.Feedback, "Explain an empty collection and offer an action.", "CupertinoEmptyState"),
    AlertDialog("Alert Dialog", ComponentCategory.Presentation, "Present a decision with standard or glass alerts.", "CupertinoAlertDialog CupertinoLiquidAlertDialog CupertinoAlertDialogNative"),
    ActionSheet("Action Sheet", ComponentCategory.Presentation, "Present contextual and destructive actions.", "CupertinoActionSheet CupertinoActionSheetNative"),
    Menu("Menu", ComponentCategory.Presentation, "Grouped, selectable and destructive menu actions.", "CupertinoDropdownMenu CupertinoDropdownMenuNative MenuAction MenuPickerAction MenuSection"),
    PopupButton("Popup Button", ComponentCategory.Presentation, "Choose an option from an anchored menu.", "CupertinoPopupButton"),
    Popover("Popover", ComponentCategory.Presentation, "Present content attached to an anchor.", "CupertinoPopover CupertinoPopoverEdge"),
    BottomSheet("Bottom Sheet", ComponentCategory.Presentation, "Resize, scroll and dismiss a modal sheet.", "CupertinoBottomSheetScaffold CupertinoBottomSheetContent PresentationDetent"),
    SwipeBox("Swipe Actions", ComponentCategory.Layout, "Reveal contextual actions by swiping a row.", "CupertinoSwipeBox CupertinoSwipeBoxItem"),
    NavigationBar("Navigation Bar", ComponentCategory.Layout, "Switch tabs with an interactive glass indicator.", "CupertinoNavigationBar CupertinoNavigationBarItem CupertinoNavigationBarNative"),
    TopBar("Top & Bottom Bars", ComponentCategory.Layout, "Navigation titles, subtitles and toolbar actions.", "CupertinoTopAppBar CupertinoNavigationTitle CupertinoBottomAppBar CupertinoScaffold"),
    Foundation("Text & Surfaces", ComponentCategory.Layout, "Typography, semantic colors and dividers.", "CupertinoText CupertinoIcon CupertinoSurface CupertinoHorizontalDivider CupertinoVerticalDivider CupertinoTheme"),
}

internal fun filteredComponents(query: String, category: ComponentCategory?): List<CatalogComponent> {
    val terms = query.trim().split(Regex("\\s+")).filter(String::isNotEmpty)
    return CatalogComponent.entries.filter { component ->
        (category == null || component.category == category) && terms.all { term ->
            listOf(component.title, component.description, component.apiNames, component.category.title)
                .any { it.contains(term, ignoreCase = true) }
        }
    }
}
