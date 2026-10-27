# 아키텍처 점검 이슈

소비자 빌드에 실제 영향을 주는 의존성 선언 문제만 기록했다. 취향에 따른 구조 변경이나 일반적인 코드 정리는 제외했다.

| ID | 우선순위 | 이슈 이름 | 위치 | 해결 방향 | 상태 |
| --- | --- | --- | --- | --- | --- |
| ARCH-001 | P2 | 공개 API의 외부 타입이 implementation 의존성에 숨겨짐 | [hig/build.gradle.kts:40](../../hig/build.gradle.kts#L40)<br>[hig-core/build.gradle.kts:30](../../hig-core/build.gradle.kts#L30)<br>[hig-native/build.gradle.kts:31](../../hig-native/build.gradle.kts#L31)<br>[hig/src/commonMain/kotlin/zone/ien/hig/utils/Backdrop.kt:50](../../hig/src/commonMain/kotlin/zone/ien/hig/utils/Backdrop.kt#L50)<br>[hig/src/commonMain/kotlin/zone/ien/hig/CupertinoPicker.kt:111](../../hig/src/commonMain/kotlin/zone/ien/hig/CupertinoPicker.kt#L111) | 공개 타입에 Foundation의 ScrollableState와 Backdrop/LayerBackdrop 등이 노출되지만 소비자 API 클래스패스에는 hig·hig-core·Kotlin만 포함된다. 실제 소비자 javac에서 ScrollableState not found를 재현했다. 공개 시그니처에 노출된 라이브러리와 프로젝트 의존성을 api로 선언하고 순수 구현 의존성은 유지한다. 별도 소비자에서 Backdrop API·Picker·hig-native 단독 의존을 컴파일해 검증한다. | 미해결 |
