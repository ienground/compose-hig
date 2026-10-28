import type { IenLanguage } from "@/shared/lib/ien-theme";

const english: Record<string, string> = {
  "클릭: $clicks": "Clicks: $clicks",
  "선택한 탭: $selected": "Selected tab: $selected",
  서울: "Seoul",
  부산: "Busan",
  대구: "Daegu",
  인천: "Incheon",
  광주: "Gwangju",
  대전: "Daejeon",
  제주: "Jeju",
  "본문으로 건너뛰기": "Skip to content",
  "컴포넌트 라이브러리": "Component library",
  "주요 메뉴": "Main navigation",
  컴포넌트: "Components",
  샘플앱: "Sample app",
  "다크 모드로 전환": "Switch to dark mode",
  "라이트 모드로 전환": "Switch to light mode",
  "컴포넌트 탐색": "Browse components",
  "컴포넌트 검색": "Search components",
  "컴포넌트 목록": "Component list",
  "검색 결과가 없습니다.": "No components found.",
  "샘플앱 & iPhone Duo": "Sample app & iPhone Duo",
  "Apple HIG를 위한 Compose Multiplatform 컴포넌트":
    "Compose Multiplatform components for Apple HIG",
  액션: "Actions",
  선택: "Selection",
  입력: "Inputs",
  피커: "Pickers",
  오버레이: "Overlays",
  콘텐츠: "Content",
  레이아웃: "Layout",
  "화면 구성": "App structure",
  "소스 보기": "View source",
  "샘플앱 소스 보기": "View sample source",
  "실제 Compose · Wasm": "Live Compose · Wasm",
  미리보기: "Preview",
  너비: "Width",
  휴대폰: "Phone",
  태블릿: "Tablet",
  전체: "All",
  "전체 너비": "Full width",
  초기화: "Reset",
  "컴포넌트 상태를 초기화했습니다.": "Component state reset.",
  "실시간 미리보기": "Live preview",
  "미리보기 안에서 직접 클릭하거나 드래그해 보세요.":
    "Click or drag inside the preview to try it out.",
  "컴포넌트 속성": "Component properties",
  속성: "Properties",
  "다크 모드": "Dark mode",
  강조색: "Accent color",
  "사용 예제": "Usage example",
  "현재 Playground 설정에 맞춘 Kotlin 코드입니다.":
    "Kotlin code for the current playground settings.",
  "코드 복사": "Copy code",
  복사됨: "Copied",
  "Kotlin 코드를 복사했습니다.": "Kotlin code copied.",
  "복사하지 못했습니다. 코드 영역에서 직접 선택해 복사하세요.":
    "Copy failed. Select the code and copy it manually.",
  "예제는 CupertinoTheme 안에서 실행합니다. backdrop을 사용하는 컴포넌트는 배경 콘텐츠에 layerBackdrop을 연결하세요.":
    "Run examples inside CupertinoTheme. For components using backdrop, attach layerBackdrop to the background content.",
  "배경 이미지는 샘플앱의 glass_landscape 리소스입니다. 프로젝트 이미지로 바꿔 사용할 수 있습니다.":
    "The background uses the sample app's glass_landscape resource. Replace it with an image from your project.",
  샘플: "Samples",
  "전체 샘플앱을 탐색하고, iPhone Duo에서 실행한 iOS 화면을 확인하세요.":
    "Explore the full sample app and see it running on iPhone Duo in iOS.",
  "샘플앱과 iPhone Duo": "Sample app and iPhone Duo",
  "샘플 보기": "Sample view",
  "웹 샘플앱": "Web sample app",
  "iPhone Duo · iOS 캡처": "iPhone Duo · iOS captures",
  "iOS 시뮬레이터": "iOS Simulator",
  "실제 iOS 샘플앱을 캡처한 화면입니다. 펼친 화면의 탐색 구조와 하단 시트 배치를 확인할 수 있습니다. 웹 샘플은 UIKit 힌지 API를 실행하지 않습니다.":
    "Captured from the iOS sample app. Explore navigation and sheet placement on the expanded display. The web sample does not run UIKit hinge APIs.",
  "펼친 화면": "Expanded",
  "접힌 화면": "Folded",
  "하단 시트": "Bottom sheet",
  "펼친 iPhone Duo의 Compose HIG 샘플앱":
    "Compose HIG on an expanded iPhone Duo",
  "접힌 iPhone Duo의 Compose HIG 샘플앱": "Compose HIG on a folded iPhone Duo",
  "펼친 iPhone Duo에서 표시한 하단 시트":
    "Bottom sheet on an expanded iPhone Duo",
  "iPhone Duo 샘플앱 시연 영상": "iPhone Duo sample app video",
  "이 브라우저는 영상을 재생하지 못합니다.":
    "Your browser cannot play this video.",
  "iPhone Duo 시연": "iPhone Duo demo",
  "영상 파일 열기": "Open video",
  "화면 너비": "Viewport width",
  "Compose HIG 전체 샘플앱": "Full Compose HIG sample app",
  "앱 안에서 테마, 화면 이동, 컴포넌트 동작을 확인할 수 있습니다.":
    "Explore themes, navigation, and component interactions inside the app.",
  레이블: "Label",
  활성화: "Enabled",
  스타일: "Style",
  크기: "Size",
  "초기 선택 상태": "Initially checked",
  "초기 상태": "Initial state",
  "초기 탭": "Initial tab",
  "초기 값": "Initial value",
  "중간 단계 수": "Intermediate steps",
  플레이스홀더: "Placeholder",
  "초기 입력값": "Initial text",
  "오류 표시": "Error state",
  "제목 / 열기 버튼": "Title / trigger label",
  "버튼 레이블": "Button label",
  "시트 제목": "Sheet title",
  "진행률 표시": "Determinate",
  텍스트: "Text",
  타이포그래피: "Typography",
  "접근성 설명": "Accessible description",
  "위쪽 텍스트": "Heading text",
  "섹션 제목": "Section title",
  "InsetGrouped 스타일": "InsetGrouped style",
  "화면 제목": "Screen title",
  "초기 탐색 탭": "Initial navigation tab",
  계속: "Continue",
  "내용을 입력하세요": "Enter text",
  검색: "Search",
  "변경 사항 저장": "Save changes",
  "메뉴 열기": "Open menu",
  "하단 시트 열기": "Open bottom sheet",
  "안녕하세요, Compose HIG": "Hello, Compose HIG",
  좋아요: "Like",
  설정: "Settings",
  "나의 라이브러리": "My library",
  켜짐: "On",
  꺼짐: "Off",
  선택됨: "Checked",
  "선택 안 됨": "Unchecked",
  즐겨찾기: "Favorites",
  최근: "Recent",
  "콘텐츠 사이를 구분합니다.": "Separates content.",
  알림: "Notifications",
  "상세 보기": "View details",
  공유: "Share",
  삭제: "Delete",
  저장: "Save",
  취소: "Cancel",
  "실행할 작업을 선택하세요.": "Choose an action.",
  "변경 사항을 저장할까요?": "Would you like to save your changes?",
  저장됨: "Saved",
  "아래로 드래그하거나 버튼으로 닫을 수 있습니다.":
    "Drag down or use the button to dismiss.",
  닫기: "Close",
  홈: "Home",
  "바를 눌러 화면 상태를 변경하세요.": "Use the bar to switch screens.",
  "컴포넌트를 찾을 수 없습니다.": "Component not found.",
  "크기와 스타일을 조절하고, 클릭에 따라 바뀌는 상태를 확인하세요.":
    "Adjust size and style, then click to see state changes.",
  "배경을 반영하는 Liquid Glass 버튼의 눌림 효과와 스타일을 확인하세요.":
    "Explore Liquid Glass styles and press effects that reflect the background.",
  "아이콘만 표시하는 버튼입니다. 버튼을 눌러 상태 변화를 확인하세요.":
    "An icon-only button. Click to see its state change.",
  "Liquid Glass를 사용하는 아이콘 버튼입니다.":
    "An icon button with Liquid Glass.",
  "콘텐츠 위에 주요 동작을 제공하는 작은 플로팅 버튼입니다.":
    "A small floating button for a primary action above content.",
  "켜짐과 꺼짐 상태, 비활성 상태를 직접 비교하세요.":
    "Compare on, off, and disabled states.",
  "체크 상태를 전환하는 선택 컨트롤입니다.":
    "A selection control that toggles its checked state.",
  "부분 선택, 선택, 해제의 세 상태를 순서대로 전환합니다.":
    "Cycle through indeterminate, checked, and unchecked states.",
  "탭을 클릭하거나 드래그해 선택 인디케이터의 움직임을 확인하세요.":
    "Click or drag tabs to explore the selection indicator.",
  "연속 값과 단계별 값을 드래그로 선택합니다.":
    "Drag to select continuous or stepped values.",
  "두 개의 손잡이를 드래그해 시작 값과 끝 값을 지정합니다.":
    "Drag both handles to select the start and end of a range.",
  "텍스트 입력, 플레이스홀더, 오류와 비활성 상태를 확인하세요.":
    "Try text input, placeholders, error states, and disabled states.",
  "윤곽선이 있는 텍스트 입력입니다.": "A text input with an outline.",
  "검색 입력과 포커스 전환을 확인하세요.":
    "Explore search input and focus transitions.",
  "휠을 스크롤해 항목을 선택하세요. 기존 샘플앱의 피커 예제를 사용합니다.":
    "Scroll the wheel to select an item, using the sample app's picker example.",
  "시와 분을 휠로 선택합니다. 웹에서는 Compose 구현을 실행합니다.":
    "Select hours and minutes with wheels. The web preview runs the Compose implementation.",
  "날짜를 휠로 선택합니다. iOS 네이티브 피커와 웹 구현은 구분됩니다.":
    "Select a date with wheels. The web implementation differs from the native iOS picker.",
  "날짜와 시간을 함께 선택하는 Compose 피커입니다.":
    "A Compose picker for selecting a date and time together.",
  "버튼을 눌러 표시하고, 작업 선택과 닫기 동작을 확인하세요.":
    "Open the overlay, choose an action, or dismiss it.",
  "앵커 버튼에 연결된 메뉴에서 작업을 선택하세요.":
    "Choose an action from a menu attached to its trigger.",
  "하단 시트를 열고 드래그하거나 버튼으로 닫아 보세요.":
    "Open the bottom sheet, then drag or use the button to dismiss it.",
  "무한 로딩과 진행률 표시를 비교하세요.":
    "Compare indeterminate loading and progress indicators.",
  "Cupertino 타이포그래피의 크기와 계층을 비교하세요.":
    "Compare sizes and hierarchy in Cupertino typography.",
  "벡터 아이콘의 크기와 테마 강조색을 조절하세요.":
    "Adjust vector icon size and the theme accent color.",
  "콘텐츠 사이에 사용하는 구분선입니다.": "A divider for separating content.",
  "설정 화면의 섹션, 행, 링크와 스위치를 함께 확인하세요.":
    "Explore settings sections, rows, links, and switches together.",
  "실제 화면 안에서 바의 배치와 탐색 상태를 확인하세요.":
    "Explore bar placement and navigation state in a full screen.",
  "기존 최근 통화 예제를 재사용합니다. 행을 좌우로 스와이프해 동작을 확인하세요.":
    "Swipe rows in the sample app's recent calls example to reveal actions.",
};

export function translate(text: string, language: IenLanguage = "en"): string {
  return language === "ko" ? text : (english[text] ?? text);
}
