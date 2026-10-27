# 아키텍처 점검 이슈

소비자 빌드에 실제 영향을 주는 의존성 선언 문제만 기록했다. 취향에 따른 구조 변경이나 일반적인 코드 정리는 제외했다.

| ID | 우선순위 | 이슈 이름 | 위치 | 해결 방향 | 상태 |
| --- | --- | --- | --- | --- | --- |
| ARCH-001 | P2 | 공개 API의 외부 타입이 implementation 의존성에 숨겨짐 | [hig/build.gradle.kts:40](../../hig/build.gradle.kts#L40)<br>[hig-core/build.gradle.kts:30](../../hig-core/build.gradle.kts#L30)<br>[hig-native/build.gradle.kts:31](../../hig-native/build.gradle.kts#L31)<br>[hig/src/commonMain/kotlin/zone/ien/hig/utils/Backdrop.kt:50](../../hig/src/commonMain/kotlin/zone/ien/hig/utils/Backdrop.kt#L50)<br>[hig/src/commonMain/kotlin/zone/ien/hig/CupertinoPicker.kt:111](../../hig/src/commonMain/kotlin/zone/ien/hig/CupertinoPicker.kt#L111) | 수정: 공개 시그니처에 노출된 Compose UI/Foundation/Runtime·Capsule·Backdrop·직렬화 및 hig 프로젝트 의존성만 api로 전환했다. core·hig·native 단독 의존의 Java 소비자 컴파일 3개가 통과했다. hig-native만 의존하는 별도 iOS KMP 소비자의 Compose 테마·ActionSheet framework 최종 링크와 앱 호스트 실행도 성공했다. 구현 전용 의존성은 유지한다. | 해결 완료 |
