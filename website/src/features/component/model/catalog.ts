export type Props = Record<string, string | number | boolean>;
export type Control = {
  key: string;
  label: string;
  type: "select" | "checkbox" | "text" | "range";
  value: string | number | boolean;
  options?: string[];
  max?: number;
  step?: number;
};
export type Component = {
  id: string;
  name: string;
  group: string;
  api: string;
  description: string;
  controls: Control[];
  snippet: (props: Props) => string;
  file: string;
};

const select = (
  key: string,
  label: string,
  options: string[],
  value = options[0],
): Control => ({ key, label, type: "select", options, value });
const toggle = (key: string, label: string, value = true): Control => ({
  key,
  label,
  type: "checkbox",
  value,
});
const text = (key: string, label: string, value: string): Control => ({
  key,
  label,
  type: "text",
  value,
});
const range = (
  key: string,
  label: string,
  value = 0.5,
  max = 1,
  step = 0.01,
): Control => ({ key, label, type: "range", value, max, step });
const enabled = toggle("enabled", "활성화");
const label = text("label", "레이블", "계속");
const checked = toggle("checked", "초기 선택 상태");
const value = range("value", "초기 값");
const inputControls = [
  text("label", "플레이스홀더", "내용을 입력하세요"),
  text("text", "초기 입력값", ""),
  enabled,
  toggle("error", "오류 표시", false),
];
const quote = (value: Props[string]) =>
  JSON.stringify(String(value)).replaceAll("$", "\\$");
const entry = (
  id: string,
  name: string,
  group: string,
  api: string,
  description: string,
  controls: Control[],
  snippet: Component["snippet"],
  file = `${api}.kt`,
): Component => ({
  id,
  name,
  group,
  api,
  description,
  controls,
  snippet,
  file,
});
const state = (declaration: string, body: string) =>
  `${declaration}\n\n${body}`;
const icon =
  'CupertinoIcon(CupertinoIcons.Filled.Heart, contentDescription = "좋아요")';
export const catalog: Component[] = [
  entry(
    "button",
    "Button",
    "액션",
    "CupertinoButton",
    "크기와 스타일을 조절하고, 클릭에 따라 바뀌는 상태를 확인하세요.",
    [
      label,
      enabled,
      select("variant", "스타일", ["filled", "tinted", "plain", "gray"]),
      select("size", "크기", ["Regular", "Small", "Large", "ExtraLarge"]),
    ],
    (p) =>
      state(
        "var clicks by remember { mutableIntStateOf(0) }",
        `CupertinoButton(\n    onClick = { clicks++ },\n    enabled = ${p.enabled},\n    size = CupertinoButtonSize.${p.size},\n    colors = CupertinoButtonDefaults.${p.variant}ButtonColors(),\n) {\n    CupertinoText(${quote(p.label)})\n}\nCupertinoText("클릭: $clicks")`,
      ),
  ),
  entry(
    "liquid-button",
    "Liquid Button",
    "액션",
    "CupertinoLiquidButton",
    "배경을 반영하는 Liquid Glass 버튼의 눌림 효과와 스타일을 확인하세요.",
    [
      label,
      enabled,
      select("variant", "스타일", ["prominent", "glass"]),
      select("size", "크기", ["Regular", "Small", "Large", "ExtraLarge"]),
    ],
    (p) =>
      `CupertinoLiquidButton(\n    onClick = { /* 작업 실행 */ },\n    enabled = ${p.enabled},\n    size = CupertinoButtonSize.${p.size},\n    colors = CupertinoLiquidButtonDefaults.${p.variant === "glass" ? "glass" : "glassProminent"}ButtonColors(),\n    backdrop = backdrop,\n) {\n    CupertinoText(${quote(p.label)})\n}`,
  ),
  entry(
    "icon-button",
    "Icon Button",
    "액션",
    "CupertinoIconButton",
    "아이콘만 표시하는 버튼입니다. 버튼을 눌러 상태 변화를 확인하세요.",
    [enabled],
    (p) =>
      `CupertinoIconButton(onClick = { /* 작업 실행 */ }, enabled = ${p.enabled}) {\n    ${icon}\n}`,
    "CupertinoButton.kt",
  ),
  entry(
    "liquid-icon-button",
    "Liquid Icon Button",
    "액션",
    "CupertinoLiquidIconButton",
    "Liquid Glass를 사용하는 아이콘 버튼입니다.",
    [enabled],
    (p) =>
      `CupertinoLiquidIconButton(onClick = { /* 작업 실행 */ }, enabled = ${p.enabled}, backdrop = backdrop) {\n    ${icon}\n}`,
    "CupertinoLiquidButton.kt",
  ),
  entry(
    "floating-action-button",
    "Floating Action Button",
    "액션",
    "CupertinoSmallFloatingActionButton",
    "콘텐츠 위에 주요 동작을 제공하는 작은 플로팅 버튼입니다.",
    [],
    () =>
      `CupertinoSmallFloatingActionButton(onClick = { /* 작업 실행 */ }, backdrop = backdrop) {\n    ${icon}\n}`,
    "CupertinoFloatingActionButton.kt",
  ),
  entry(
    "switch",
    "Switch",
    "선택",
    "CupertinoSwitch",
    "켜짐과 꺼짐 상태, 비활성 상태를 직접 비교하세요.",
    [checked, enabled],
    (p) =>
      state(
        `var checked by remember { mutableStateOf(${p.checked}) }`,
        `CupertinoSwitch(\n    checked = checked,\n    onCheckedChange = { checked = it },\n    enabled = ${p.enabled},\n    backdrop = backdrop,\n)`,
      ),
  ),
  entry(
    "checkbox",
    "Checkbox",
    "선택",
    "CupertinoCheckBox",
    "체크 상태를 전환하는 선택 컨트롤입니다.",
    [checked, enabled],
    (p) =>
      state(
        `var checked by remember { mutableStateOf(${p.checked}) }`,
        `CupertinoCheckBox(checked = checked, onCheckedChange = { checked = it }, enabled = ${p.enabled})`,
      ),
    "CupertinoCheckbox.kt",
  ),
  entry(
    "tri-state-checkbox",
    "Tri-state Checkbox",
    "선택",
    "CupertinoTriStateCheckBox",
    "부분 선택, 선택, 해제의 세 상태를 순서대로 전환합니다.",
    [enabled, select("selected", "초기 상태", ["0", "1", "2"])],
    (p) =>
      state(
        `val states = listOf(ToggleableState.Indeterminate, ToggleableState.On, ToggleableState.Off)\nvar selected by remember { mutableIntStateOf(${p.selected}) }`,
        `CupertinoTriStateCheckBox(\n    state = states[selected],\n    onClick = { selected = (selected + 1) % 3 },\n    enabled = ${p.enabled},\n)`,
      ),
    "CupertinoCheckbox.kt",
  ),
  entry(
    "segmented-control",
    "Segmented Control",
    "선택",
    "CupertinoSegmentedControl",
    "탭을 클릭하거나 드래그해 선택 인디케이터의 움직임을 확인하세요.",
    [select("selected", "초기 탭", ["0", "1", "2"])],
    (p) =>
      state(
        `var selected by remember { mutableIntStateOf(${p.selected}) }`,
        'CupertinoSegmentedControl(selectedTabIndex = selected, backdrop = backdrop) {\n    listOf("전체", "즐겨찾기", "최근").forEachIndexed { index, title ->\n        CupertinoSegmentedControlTab(\n            isSelected = selected == index,\n            onClick = { selected = index },\n        ) { CupertinoText(title) }\n    }\n}',
      ),
  ),
  entry(
    "slider",
    "Slider",
    "입력",
    "CupertinoSlider",
    "연속 값과 단계별 값을 드래그로 선택합니다.",
    [value, range("steps", "중간 단계 수", 0, 10, 1), enabled],
    (p) =>
      state(
        `var value by remember { mutableFloatStateOf(${p.value}f) }`,
        `CupertinoSlider(\n    value = value,\n    onValueChange = { value = it },\n    enabled = ${p.enabled},\n    steps = ${p.steps},\n    backdrop = backdrop,\n)`,
      ),
  ),
  entry(
    "range-slider",
    "Range Slider",
    "입력",
    "CupertinoRangeSlider",
    "두 개의 손잡이를 드래그해 시작 값과 끝 값을 지정합니다.",
    [value, enabled],
    (p) =>
      state(
        `var range by remember { mutableStateOf(0.2f..${Math.max(0.3, Number(p.value))}f) }`,
        `CupertinoRangeSlider(value = range, onValueChange = { range = it }, enabled = ${p.enabled}, backdrop = backdrop)`,
      ),
    "CupertinoSlider.kt",
  ),
  entry(
    "text-field",
    "Text Field",
    "입력",
    "CupertinoTextField",
    "텍스트 입력, 플레이스홀더, 오류와 비활성 상태를 확인하세요.",
    inputControls,
    (p) =>
      state(
        `var text by remember { mutableStateOf(${quote(p.text)}) }`,
        `CupertinoTextField(\n    value = text,\n    onValueChange = { text = it },\n    placeholder = { CupertinoText(${quote(p.label)}) },\n    enabled = ${p.enabled},\n    isError = ${p.error},\n    singleLine = true,\n)`,
      ),
  ),
  entry(
    "bordered-text-field",
    "Bordered Text Field",
    "입력",
    "CupertinoBorderedTextField",
    "윤곽선이 있는 텍스트 입력입니다.",
    inputControls,
    (p) =>
      state(
        `var text by remember { mutableStateOf(${quote(p.text)}) }`,
        `CupertinoBorderedTextField(\n    value = text,\n    onValueChange = { text = it },\n    placeholder = { CupertinoText(${quote(p.label)}) },\n    enabled = ${p.enabled},\n    isError = ${p.error},\n    singleLine = true,\n)`,
      ),
    "CupertinoTextField.kt",
  ),
  entry(
    "search-field",
    "Search Field",
    "입력",
    "CupertinoSearchTextField",
    "검색 입력과 포커스 전환을 확인하세요.",
    [
      text("label", "플레이스홀더", "검색"),
      text("text", "초기 입력값", ""),
      enabled,
    ],
    (p) =>
      state(
        `var query by remember { mutableStateOf(${quote(p.text)}) }`,
        `CupertinoSearchTextField(\n    value = query,\n    onValueChange = { query = it },\n    enabled = ${p.enabled},\n    placeholder = { CupertinoText(${quote(p.label)}) },\n)`,
      ),
  ),
  entry(
    "wheel-picker",
    "Wheel Picker",
    "피커",
    "CupertinoWheelPicker",
    "휠을 스크롤해 항목을 선택하세요. 기존 샘플앱의 피커 예제를 사용합니다.",
    [],
    () =>
      'val items = listOf("서울", "부산", "대구", "인천", "광주", "대전", "제주")\nval state = rememberCupertinoPickerState()\nCupertinoWheelPicker(state = state, items = items, modifier = Modifier.fillMaxWidth()) {\n    CupertinoText(it)\n}',
    "CupertinoPicker.kt",
  ),
  entry(
    "time-picker",
    "Time Picker",
    "피커",
    "CupertinoTimePicker",
    "시와 분을 휠로 선택합니다. 웹에서는 Compose 구현을 실행합니다.",
    [],
    () =>
      "val state = rememberCupertinoTimePickerState()\nCupertinoTimePicker(state = state, modifier = Modifier.fillMaxWidth())",
  ),
  entry(
    "date-picker",
    "Date Picker",
    "피커",
    "CupertinoDatePicker",
    "날짜를 휠로 선택합니다. iOS 네이티브 피커와 웹 구현은 구분됩니다.",
    [],
    () =>
      "val state = rememberCupertinoDatePickerState()\nCupertinoDatePicker(state = state, modifier = Modifier.fillMaxWidth())",
  ),
  entry(
    "date-time-picker",
    "Date & Time Picker",
    "피커",
    "CupertinoDateTimePicker",
    "날짜와 시간을 함께 선택하는 Compose 피커입니다.",
    [],
    () =>
      "val state = rememberCupertinoDateTimePickerState()\nCupertinoDateTimePicker(state = state, modifier = Modifier.fillMaxWidth())",
  ),
  ...["alert-dialog", "liquid-alert-dialog", "action-sheet"].map(
    (id, index) => {
      const api = [
        "CupertinoAlertDialog",
        "CupertinoLiquidAlertDialog",
        "CupertinoActionSheet",
      ][index];
      return entry(
        id,
        ["Alert Dialog", "Liquid Alert Dialog", "Action Sheet"][index],
        "오버레이",
        api,
        "버튼을 눌러 표시하고, 작업 선택과 닫기 동작을 확인하세요.",
        [text("label", "제목 / 열기 버튼", "변경 사항 저장"), enabled],
        (p) =>
          state(
            "var visible by remember { mutableStateOf(false) }",
            `CupertinoButton(onClick = { visible = true }, enabled = ${p.enabled}) {\n    CupertinoText(${quote(p.label)})\n}\n${index === 2 ? "" : "if (visible) "}${api}(\n${index === 2 ? "    visible = visible,\n" : ""}    onDismissRequest = { visible = false },\n    title = { CupertinoText(${quote(p.label)}) },\n    message = { CupertinoText("변경 사항을 저장할까요?") },\n${index === 1 ? "    backdrop = backdrop,\n" : ""}) {\n    default(onClick = { visible = false }) { CupertinoText("저장") }\n    cancel(onClick = { visible = false }) { CupertinoText("취소") }\n}`,
          ),
        index === 1 ? "CupertinoLiquidDialogs.kt" : "CupertinoDialogs.kt",
      );
    },
  ),
  entry(
    "dropdown-menu",
    "Dropdown Menu",
    "오버레이",
    "CupertinoDropdownMenu",
    "앵커 버튼에 연결된 메뉴에서 작업을 선택하세요.",
    [text("label", "버튼 레이블", "메뉴 열기"), enabled],
    (p) =>
      state(
        "var expanded by remember { mutableStateOf(false) }",
        `Box {\n    CupertinoButton(onClick = { expanded = true }, enabled = ${p.enabled}) { CupertinoText(${quote(p.label)}) }\n    CupertinoDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, backdrop = backdrop) {\n        MenuAction(onClick = { expanded = false }) { CupertinoText("공유") }\n        MenuDivider()\n        MenuAction(onClick = { expanded = false }) { CupertinoText("삭제") }\n    }\n}`,
      ),
  ),
  entry(
    "bottom-sheet",
    "Bottom Sheet",
    "오버레이",
    "CupertinoBottomSheetScaffold",
    "하단 시트를 열고 드래그하거나 버튼으로 닫아 보세요.",
    [text("label", "시트 제목", "하단 시트 열기")],
    (p) =>
      "val state = rememberCupertinoBottomSheetScaffoldState()\nval scope = rememberCoroutineScope()\nCupertinoBottomSheetScaffold(\n    colors = CupertinoBottomSheetScaffoldDefaults.colors(containerColor = Color.Transparent),\n    scaffoldState = state,\n    sheetContent = {\n        CupertinoBottomSheetContent { padding ->\n            Column(Modifier.padding(padding).padding(24.dp)) {\n                CupertinoText(" +
      quote(p.label) +
      ')\n                CupertinoButton(onClick = { scope.launch { state.bottomSheetState.hide() } }) {\n                    CupertinoText("닫기")\n                }\n            }\n        }\n    },\n) { padding ->\n    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {\n        Image(painterResource(Res.drawable.glass_landscape), contentDescription = null, modifier = Modifier.matchParentSize().layerBackdrop(backdrop), contentScale = ContentScale.Crop)\n        CupertinoButton(onClick = { scope.launch { state.bottomSheetState.show() } }) {\n            CupertinoText(' +
      quote(p.label) +
      ")\n        }\n    }\n}",
    "CupertinoBottomSheetScaffold.kt",
  ),
  entry(
    "activity-indicator",
    "Activity Indicator",
    "콘텐츠",
    "CupertinoActivityIndicator",
    "무한 로딩과 진행률 표시를 비교하세요.",
    [toggle("determinate", "진행률 표시", false), value],
    (p) =>
      `CupertinoActivityIndicator(${p.determinate ? `progress = ${p.value}f, ` : ""}size = 32.dp)`,
  ),
  entry(
    "text",
    "Text",
    "콘텐츠",
    "CupertinoText",
    "Cupertino 타이포그래피의 크기와 계층을 비교하세요.",
    [
      text("label", "텍스트", "안녕하세요, Compose HIG"),
      select("style", "타이포그래피", [
        "body",
        "largeTitle",
        "title1",
        "caption1",
      ]),
    ],
    (p) =>
      `CupertinoText(${quote(p.label)}, style = CupertinoTheme.typography.${p.style})`,
    "Text.kt",
  ),
  entry(
    "icon",
    "Icon",
    "콘텐츠",
    "CupertinoIcon",
    "벡터 아이콘의 크기와 테마 강조색을 조절하세요.",
    [text("label", "접근성 설명", "좋아요"), value],
    (p) =>
      `CupertinoIcon(\n    imageVector = CupertinoIcons.Filled.Heart,\n    contentDescription = ${quote(p.label)},\n    modifier = Modifier.size(${24 + Number(p.value) * 48}.dp),\n    tint = CupertinoTheme.colorScheme.accent,\n)`,
  ),
  entry(
    "divider",
    "Divider",
    "레이아웃",
    "CupertinoHorizontalDivider",
    "콘텐츠 사이에 사용하는 구분선입니다.",
    [text("label", "위쪽 텍스트", "섹션 제목")],
    (p) =>
      `Column {\n    CupertinoText(${quote(p.label)})\n    CupertinoHorizontalDivider()\n    CupertinoText("콘텐츠 사이를 구분합니다.")\n}`,
    "Separator.kt",
  ),
  entry(
    "section",
    "Section",
    "레이아웃",
    "CupertinoSection",
    "설정 화면의 섹션, 행, 링크와 스위치를 함께 확인하세요.",
    [
      text("label", "섹션 제목", "설정"),
      toggle("grouped", "InsetGrouped 스타일"),
      checked,
    ],
    (p) =>
      state(
        `var checked by remember { mutableStateOf(${p.checked}) }`,
        `ProvideSectionStyle(SectionStyle.${p.grouped ? "InsetGrouped" : "Grouped"}) {\n    CupertinoSection(title = { CupertinoText(${quote(p.label)}) }) {\n        SectionItem(trailingContent = {\n            CupertinoSwitch(checked = checked, onCheckedChange = { checked = it }, backdrop = backdrop)\n        }) { CupertinoText("알림") }\n        SectionLink(onClick = { /* 상세 화면으로 이동 */ }) { CupertinoText("상세 보기") }\n    }\n}`,
      ),
    "section/Section.kt",
  ),
  ...["top-bar", "navigation-bar", "scaffold"].map((id, index) =>
    entry(
      id,
      ["Top App Bar", "Navigation Bar", "Scaffold"][index],
      "화면 구성",
      ["CupertinoTopAppBar", "CupertinoNavigationBar", "CupertinoScaffold"][
        index
      ],
      "실제 화면 안에서 바의 배치와 탐색 상태를 확인하세요.",
      [
        text("label", "화면 제목", "나의 라이브러리"),
        select("selected", "초기 탐색 탭", ["0", "1", "2"]),
      ],
      (p) => {
        const top = `CupertinoTopAppBar(title = { CupertinoText(${quote(p.label)}) }, backdrop = backdrop)`;
        const bottom =
          'CupertinoNavigationBar(\n    backdrop = backdrop,\n    selectedTabIndex = { selected },\n    onTabSelected = { selected = it },\n    tabsCount = 3,\n) {\n    listOf("홈", "즐겨찾기", "설정").forEachIndexed { index, title ->\n        CupertinoNavigationBarItem(\n            onClick = { selected = index },\n            icon = { ' +
          icon.replace('"좋아요"', "null") +
          " },\n            label = { CupertinoText(title) },\n        )\n    }\n}";
        return state(
          `var selected by remember { mutableIntStateOf(${p.selected}) }`,
          index === 0
            ? top
            : index === 1
              ? bottom
              : `CupertinoScaffold(\n    containerColor = Color.Transparent,\n    topBar = { ${top} },\n    bottomBar = {\n${bottom
                  .split("\n")
                  .map((line) => "        " + line)
                  .join(
                    "\n",
                  )}\n    },\n) { padding ->\n    Box(Modifier.fillMaxSize()) {\n        Image(painterResource(Res.drawable.glass_landscape), contentDescription = null, modifier = Modifier.matchParentSize().layerBackdrop(backdrop), contentScale = ContentScale.Crop)\n        CupertinoText("선택한 탭: $selected")\n    }\n}`,
        );
      },
      [
        "CupertinoTopBars.kt",
        "CupertinoNavigationBar.kt",
        "CupertinoScaffold.kt",
      ][index],
    ),
  ),
  entry(
    "swipe-box",
    "Swipe Box",
    "화면 구성",
    "CupertinoSwipeBox",
    "기존 최근 통화 예제를 재사용합니다. 행을 좌우로 스와이프해 동작을 확인하세요.",
    [],
    () =>
      "// 실제 실행 중인 최근 통화 예제 전체를 소스에서 확인하세요.\n// CupertinoSwipeBox의 상태, 액션 구성, 스크롤 연동을 포함합니다.\nRecentCallsSwipeChapter(scrollableState = rememberScrollState())",
    "@sample",
  ),
];

export function hasBackdrop(component: Component): boolean {
  const defaults = Object.fromEntries(
    component.controls.map((control) => [control.key, control.value]),
  );
  return (
    component.snippet(defaults).includes("backdrop") ||
    ["alert-dialog", "action-sheet", "bottom-sheet"].includes(component.id)
  );
}
