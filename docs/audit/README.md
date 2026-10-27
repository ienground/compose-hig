# 프로젝트 품질 점검

마지막 점검 날짜: **2026-10-27** (Asia/Seoul). 최초 감사는 소스 변경 없이 수행했다. 이후 수정 단계에서는 P1 10건을 먼저 처리했으며, 날짜 상태 검증에 직접 연결된 P2 BUG-005도 함께 수정했다. 런타임 의존성 버전은 유지하고 회귀 검증용 테스트 의존성만 추가했다.

총 **44건**: **해결 완료 10건**, 미해결 26건, 검증 필요 8건. P1은 9건 해결 완료, 1건(COMP-005)은 코드 수정 후 실제 화면 검증 대기다. `미해결`은 코드/호출 관계 또는 재현으로 결함을 확인했다는 뜻이며 화면 검증 완료를 의미하지 않는다. `검증 필요`는 관찰된 위험 경로의 실제 영향·재현 조건을 먼저 확인해야 한다.

## 카테고리별 이슈

| 카테고리 | 개수 | 문서 |
| --- | ---: | --- |
| 버그 | 19 | [bugs.md](bugs.md) |
| 보안 | 5 | [security.md](security.md) |
| UI/UX 및 그래픽 | 13 | [ui-ux.md](ui-ux.md) |
| 성능 | 1 | [performance.md](performance.md) |
| 호환성 및 deprecated API | 5 | [compatibility.md](compatibility.md) |
| 아키텍처 | 1 | [architecture.md](architecture.md) |

## 우선순위별 현황

| 우선순위 | 전체 | 미해결 | 검증 필요 | 해결 완료 |
| --- | ---: | ---: | ---: | ---: |
| P0 | 0 | 0 | 0 | 0 |
| P1 | 10 | 0 | 1 | 9 |
| P2 | 30 | 22 | 7 | 1 |
| P3 | 4 | 4 | 0 | 0 |

## 우선 처리 결과와 다음 순서

- **P1 BUG-001~BUG-004·BUG-006·BUG-009~BUG-011·UI-001**: 날짜/시각 손실, Pager 예외, 음수 범위, 빈 리스트 예외, 비어 있는 iOS 피커, 다이얼로그 닫기 및 슬라이더 접근성 경로를 수정했다.
- **P2 BUG-005**: P1 날짜 검증과 연결되어 함께 수정했다. Material·iOS 렌더러가 공통 연도 범위를 사용한다.
- **P1 COMP-005**: iPad popover 앵커 코드를 추가했다. 실제 앱 호스트에서 action sheet 표시를 확인하기 전까지 `검증 필요`를 유지한다.
- 다음 수정은 **P2 BUG-007·BUG-008·BUG-012~BUG-019**의 상태/이벤트·데이터 오류를 먼저 처리하고, 이어 **UI-002~UI-012·ARCH-001**을 진행한다. **SEC-001~SEC-005·PERF-001·COMP-004**는 문서에 적힌 실제 노출/재현 조건을 확인한 뒤 수정한다.

## 점검 범위 및 검증 결과

- `hig`, `hig-core`, `hig-native`, `hig-adaptive`, `hig-icons-extended`, 공통/Android/iOS/Desktop/Wasm 샘플, Gradle·공개 의존성·CI·플랫폼 설정과 기존 테스트를 조사했다. 최초 감사 시점의 추적 Kotlin 파일은 1,026개이며 그중 아이콘 확장 파일이 766개다.
- Kotlin 2.4.21, Compose 1.12.2, Material3 1.12.0-alpha03, kotlinx-datetime 0.8.0, AGP 9.3.1, Gradle 9.7.0 기준이다. 로컬 검증은 JDK 21·Xcode 27.1을 사용했다.
- 최초 감사의 기존 테스트는 Desktop·iOS Simulator·macOS 각각 27개가 통과했다. 수정 단계에서는 기존 테스트에 상태·Compose UI·UIKit 객체 회귀 검증을 추가했다.
- 수정 검증 명령: `./gradlew desktopTest compileAndroidMain compileKotlinIosSimulatorArm64 compileKotlinWasmJs compileKotlinJs compileKotlinMacosArm64 :hig:iosSimulatorArm64Test --device BC3AEF56-33AE-4E88-8AE0-DB030C6D1AD0 :hig-native:iosSimulatorArm64Test --device BC3AEF56-33AE-4E88-8AE0-DB030C6D1AD0 :hig-adaptive:iosSimulatorArm64Test --device BC3AEF56-33AE-4E88-8AE0-DB030C6D1AD0 :hig:macosArm64Test :hig-adaptive:macosArm64Test :example:androidApp:assembleDebug lint --continue --no-daemon --console=plain --max-workers=3`.
- 최신 코드의 Desktop·Android·iOS Simulator·JS·Wasm·macOS 소스 컴파일과 Android debug APK·lint가 성공했다. 통합 실행에서는 UIKit 뷰를 Kotlin 래퍼 참조로 비교한 앵커 테스트 1개만 실패했다. 같은 Objective-C 객체의 동등성을 검사하도록 테스트를 수정한 뒤 `:hig-native:iosSimulatorArm64Test --device BC3AEF56-33AE-4E88-8AE0-DB030C6D1AD0`를 재실행하여 성공했다. 제품 코드의 앵커 설정은 변경하지 않았다.
- 최종 테스트 결과는 아래와 같으며 **177회 실행, 실패·오류·건너뜀 0회**다. iOS 테스트 기기는 iPad Air 11-inch (M4), iOS 27.0 Simulator다. `—`는 테스트 실행 대상이 없다는 뜻이다.

| 모듈 | Desktop | iOS Simulator | macOS |
| --- | ---: | ---: | ---: |
| hig | 56 | 56 | 56 |
| hig-adaptive | 1 | 1 | 1 |
| hig-native | — | 6 | — |

- 위 `lint`는 Android 샘플의 debug 분석을 실행했고 `MonochromeLauncherIcon` 경고 2개를 UI-013으로 통합했다. 전체 KMP 라이브러리 소스가 Android lint로 검증됐다는 뜻은 아니다.
- 저장소 밖 임시 JVM 진단으로 BUG-001~BUG-003·BUG-005의 상태 반환을 재현했다. UTC 음수 시간대 및 ar-EG/th-TH 포맷은 Node로 확인했다. 별도 소비자 API 클래스패스의 javac 실패로 ARCH-001을 확인했다. 해당 최초 감사 진단은 제품 코드를 변경하지 않았다. 수정 단계에서 BUG-001~BUG-003·BUG-005에는 저장소 내 회귀 테스트를 추가했다.
- npm 잠금 파일 2개와 선택한 Android/Desktop Maven 해석 결과·버전 카탈로그를 공개 OSV 권고와 대조했다. 자세한 노출 범위와 한계는 [security.md](security.md)에 기록했다.
- 기본·확장 아이콘은 SVG 833개와 공개 ImageVector 속성 833개다. viewport·path·fill 구조를 대조해 832쌍이 일치했다. 대응 벡터를 찾지 못한 `hig-icons-extended/svg/filled/smallcircle.filled.circle.fill.svg` 1개와 SVG 짝이 없는 기본 MagnifyingGlass 1개는 공개 자산 구성 의도를 확인할 근거가 없어 확정 결함으로 세지 않았다.

## 분석하지 못했거나 검증하지 못한 영역과 이유

- **실제 화면/보조기기 전수 검사**: iOS·Android 실기, 웹 상호작용, 모든 밝은/어두운 테마·글꼴 크기·키보드·VoiceOver/TalkBack 조합을 실행하지 않았다. Compose semantics·키보드 및 상태 테스트는 추가했으나 실제 보조기기 낭독·픽셀 검증을 대신하지 않는다.
- **OS·기기 매트릭스**: 최소 지원 iOS/Android, iPad popover의 실제 표시, 구형 iOS 메뉴, Duo 힌지 열림/닫힘·RTL, 회전·멀티윈도우는 해당 런타임 조합에서 재현하지 않았다. COMP-004·COMP-005는 이 때문에 검증 필요다.
- **성능 실측**: Instruments·Android 프로파일러·브라우저 프레임/메모리 계측을 수행하지 않았다. PERF-001은 반복 할당 경로만 확인했으며 실제 지연·누수로 단정하지 않았다.
- **대형 내부 구현의 모든 분기**: `CupertinoScaffold`, `CupertinoBottomSheetScaffold`, `CupertinoNavigationBar`, `CupertinoTextField`, `SwipeableV2`, `Swipable`의 진입·상태·관련 호출은 조사했으나 모든 레이아웃·제스처 인터리빙을 완독/실행하여 증명하지는 못했다. 아이콘도 전체 육안 렌더링 검증은 하지 않았다.
- **추가 빌드/테스트**: iOS 앱 framework 최종 링크·Xcode 앱 빌드/기기 실행, Wasm 배포 번들·JS/Wasm 브라우저 테스트, Android 계측 테스트·릴리스 서명/배포는 실행하지 않았다. Android 타깃은 commonTest 호스트 테스트가 활성화되지 않았다는 구성 경고가 있어 Android 테스트 통과로 보고하지 않는다.
- **보안 검증의 경계**: npm 취약 버전의 실제 공격 입력 도달, 네이티브/Skiko 등 바이너리 내부, 모든 빌드 플러그인 전이 의존성·GitHub 원격 권한/Secrets·배포 환경·Git 전체 이력은 검증하지 않았다. 인증/서버/민감정보 저장 계층은 이 UI 라이브러리에 없어 적용 대상이 아니다.
- **공개 의존성 확인 범위**: ARCH-001의 실행 재현은 Desktop API 클래스패스와 javac 기준이다. 별도 Kotlin Multiplatform 소비자와 모든 플랫폼의 배포 메타데이터 검증은 남아 있다.

## 문서 유지 규칙

이슈를 수정할 때 표의 위치와 발생 조건을 다시 확인하고, 검증 후 상태를 갱신한다. 하나의 원인으로 묶은 행은 기재한 모든 플랫폼/진입점을 확인한 뒤 해결 완료로 바꾼다. `해결 완료`는 해당 행에 적힌 자동 검증을 통과했다는 뜻이며 모든 기기의 화면·보조기기 검증 완료를 의미하지 않는다. 실제 표시가 핵심인 COMP-005는 코드 수정 후에도 `검증 필요`로 남긴다.
