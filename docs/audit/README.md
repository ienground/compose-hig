# 프로젝트 품질 점검

마지막 점검 날짜: **2026-10-27** (Asia/Seoul).

최초 감사에서 등록한 **44건을 모두 수정했다**. 이전 단계에서 10건, 이번 단계에서 남은 34건을 처리했다. 상태는 해결 완료 44건, 미해결 0건, 검증 필요 0건이다. `해결 완료`는 각 행에 기록한 수정과 검증을 완료했다는 뜻이며 모든 OS·기기·보조기기 조합을 검증했다는 뜻은 아니다. 남은 검증 범위는 아래에 구분했다.

## 카테고리별 이슈

| 카테고리 | 전체 | 해결 완료 | 문서 |
| --- | ---: | ---: | --- |
| 버그 | 19 | 19 | [bugs.md](bugs.md) |
| 보안 | 5 | 5 | [security.md](security.md) |
| UI/UX 및 그래픽 | 13 | 13 | [ui-ux.md](ui-ux.md) |
| 성능 | 1 | 1 | [performance.md](performance.md) |
| 호환성 및 deprecated API | 5 | 5 | [compatibility.md](compatibility.md) |
| 아키텍처 | 1 | 1 | [architecture.md](architecture.md) |

## 우선순위별 현황

| 우선순위 | 전체 | 미해결 | 검증 필요 | 해결 완료 |
| --- | ---: | ---: | ---: | ---: |
| P0 | 0 | 0 | 0 | 0 |
| P1 | 10 | 0 | 0 | 10 |
| P2 | 30 | 0 | 0 | 30 |
| P3 | 4 | 0 | 0 | 4 |

## 우선 처리 결과

- **P1**: 날짜·시각 손실, Pager 예외, 음수 범위 훼손, 빈 리스트 예외, iOS 피커 및 다이얼로그 상태, 슬라이더 접근성을 수정했다. COMP-005는 iPad 앱 호스트의 실제 action sheet 표시·앵커·닫기·재표시까지 확인했다.
- **P2**: 재구성 후 Slider 입력·네이티브 버튼/탭/메뉴 동기화·취소 슬롯·월 경계·Android 비트맵 크기를 수정했다. Haze·고대비·날짜 포맷·색상 및 semantics 계약을 보정했다. 공개 타입 의존성은 소비자에게 노출하고 대비 분석은 작은 레이어만 읽는다.
- **보안·P3**: 취약 버전이 포함된 npm 도구 의존성을 갱신했다. deprecated API·지원 종료 속성을 제거하고 기존 로고 기반 monochrome 아이콘을 연결했다.
- 문서화된 이슈 중 수정 대기 항목은 없다. 추가 출시 검증에서는 iPad 회전·구형 OS 메뉴·Android Haze·실제 보조기기 조합을 먼저 확인한다.

## 점검 범위 및 검증 결과

- `hig`, `hig-core`, `hig-native`, `hig-adaptive`, `hig-icons-extended`, 공통/Android/iOS/Desktop/Wasm 샘플, Gradle·공개 의존성·CI·플랫폼 설정과 기존 테스트를 조사했다. 최초 감사의 추적 Kotlin 파일은 1,026개이며 아이콘 확장이 766개다.
- Kotlin 2.4.21, Compose 1.12.2, Material3 1.12.0-alpha03, kotlinx-datetime 0.8.0, AGP 9.3.1, Gradle 9.7.0 기준이다. JDK 21·Xcode 27.1로 검증했다. 라이브러리 런타임 버전 카탈로그는 유지했다. 회귀 테스트 의존성과 JS/Wasm 도구의 전이 의존성 해석·잠금 파일을 변경했다.
- Desktop·Android·iOS Simulator·JS·Wasm·macOS 소스 컴파일, Android debug APK와 샘플 lint가 성공했다. 최종 lint는 **오류 0개·경고 0개**다. 전체 KMP 소스를 Android lint로 검사했다는 뜻은 아니다.
- Compose 상태·픽셀·semantics·제스처, UTC/로캘, UIKit 객체 회귀 테스트를 추가했다. JS는 Skiko 초기화 모듈을 공유하고 Wasm은 테스트 실행 번들을 생성하도록 설정했다. 브라우저 테스트는 Chrome Headless, Asia/Seoul 시간대에서 실행했으며 UTC 음수 시간대의 포맷도 별도로 재현했다. 설정은 [Kotlin UI 테스트 안내](https://kotlinlang.org/docs/multiplatform/compose-test.html)와 [Karma 파일 설정](https://karma-runner.github.io/6.4/config/files.html)을 대조했다.
- 최종 자동 테스트는 **412회 실행, 실패·오류·건너뜀 0회**다. `—`는 현재 소스에 실행할 테스트가 없다는 뜻이다. Desktop으로 옮긴 Core 테스트의 예전 다른 타깃 결과는 합산하지 않는다.

| 모듈 | Desktop | iOS Simulator | macOS | JS 브라우저 | Wasm 브라우저 |
| --- | ---: | ---: | ---: | ---: | ---: |
| hig | 76 | 74 | 74 | 77 | 77 |
| hig-core | 2 | — | — | — | — |
| hig-adaptive | 3 | 3 | 3 | 3 | 3 |
| hig-native | — | 17 | — | — | — |

- 주요 Gradle 검증 대상: `desktopTest`, `compileAndroidMain`, `compileKotlinIosSimulatorArm64`, `compileKotlinJs`, `compileKotlinWasmJs`, `compileKotlinMacosArm64`, `:hig:iosSimulatorArm64Test`, `:hig-native:iosSimulatorArm64Test`, `:hig-adaptive:iosSimulatorArm64Test`, `:hig:macosArm64Test`, `:hig-adaptive:macosArm64Test`, `:hig:jsBrowserTest`, `:hig-adaptive:jsBrowserTest`, `:hig:wasmJsBrowserTest`, `:hig-adaptive:wasmJsBrowserTest`, `:example:androidApp:assembleDebug`, `lint`. iOS 테스트에는 대상 Simulator의 `--device`를 지정했고 브라우저 테스트에는 설치된 Chrome의 `CHROME_BIN`을 지정했다.
- **Android 실기**: Pixel 7a의 저장소 밖 임시 검증 APK로 BUG-019의 1×1·2×10·5×5 크기를 확인했다. 수정 전 실패와 수정 후 3개 사례 통과를 각각 확인했다. 일반 Android 계측 테스트 전체를 실행한 것은 아니다.
- **iPad 앱 호스트**: iPad Air 11-inch (M4), iOS 27.0 Simulator의 별도 앱에서 action sheet 표시·앵커·버튼 enabled 전환·제목/스타일 교체·외부 상태로 닫기·재표시를 확인했다. 정의 교체에 따른 사용자 닫기 콜백은 0회였다. 회전 요청은 현재 Simulator 창 모드가 거부했다. 임시 검증 앱은 실행 후 제거했다.
- **공개 API 소비자**: core·hig·native 단독 의존의 Java 소비자 컴파일 3개가 통과했다. `hig-native`만 의존하는 별도 iOS KMP 소비자가 Compose 테마와 action sheet를 사용하는 framework를 최종 링크하고 위 앱 호스트에서 실행했다.
- **보안 재조회**: 최종 npm 고유 패키지·버전 **435개**, 선택한 Android/Desktop 런타임·테스트 Maven 고유 좌표·버전 **340개**를 OSV에 조회해 일치 권고 **0개**를 확인했다. [security.md](security.md)의 조회 범위와 한계를 함께 읽어야 한다.
- **성능·자산**: 대비 분석의 1000×500 원본 관심 영역을 5×5 샘플로 읽는 경로와 명도를 픽셀 테스트로 확인했다. 원본 런처 로고와 새 monochrome 벡터의 렌더링 모양을 대조했다. 프레임 시간·GPU 비용 감소량이나 실제 런처 테마 표시까지 측정한 것은 아니다.
- 기본·확장 아이콘은 SVG 833개와 공개 ImageVector 속성 833개다. 최초 구조 대조에서 832쌍이 일치했다. 대응 벡터를 찾지 못한 `hig-icons-extended/svg/filled/smallcircle.filled.circle.fill.svg` 1개와 SVG 짝이 없는 기본 MagnifyingGlass 1개는 자산 구성 의도를 확인할 근거가 없어 확정 결함으로 세지 않았다.

## 분석하지 못했거나 검증하지 못한 영역과 이유

- **실제 화면·보조기기 전수 검사**: 모든 밝은/어두운 테마·글꼴 크기·키보드·VoiceOver/TalkBack 조합을 실행하지 않았다. 픽셀/semantics 및 자동 상호작용 검증은 실제 보조기기 낭독을 대신하지 않는다. 고대비 OS 설정 UI 전환, Android 테마 런처 아이콘과 날짜 피커 큰 글꼴 실물 검사는 남아 있다.
- **OS·기기 매트릭스**: 최소 지원 iOS/Android, iOS 17.4 이전 메뉴 fallback의 실기 상호작용, Android API 29/31 Haze 비교, iPad 실제 회전·멀티윈도우, Duo 힌지·RTL을 해당 기기 조합에서 확인하지 않았다. 구형 iOS의 비공개 API 경로는 제거했고 iPad 표시 자체는 앱 호스트에서 확인했다.
- **성능 실측**: Instruments·Android 프로파일러·브라우저 프레임/메모리 계측은 수행하지 않았다. 전체 레이어 readback 제거와 실제 샘플 크기를 확인했으나 지연·할당 감소량이나 누수 부재를 주장하지 않는다.
- **대형 내부 구현의 모든 분기**: `CupertinoScaffold`, `CupertinoBottomSheetScaffold`, `CupertinoNavigationBar`, `CupertinoTextField`, `SwipeableV2`, `Swipable`의 관련 호출은 조사했으나 모든 레이아웃·제스처 인터리빙을 실행해 증명하지는 못했다. 모든 아이콘의 육안 렌더링 검사도 미실행이다.
- **배포 검증**: 저장소의 전체 iOS 샘플 Xcode 배포 빌드·iOS 실기·Wasm 배포 번들·릴리스 서명/배포는 실행하지 않았다. iOS 임시 소비자 framework/앱 링크와 Simulator 실행은 수행했다. Android commonTest 호스트 테스트는 구성상 비활성화되어 있으며 Android 테스트 전체 통과로 보고하지 않는다.
- **보안 검증의 경계**: 실제 공격 입력 도달, 네이티브/Skiko 바이너리 내부, 모든 빌드 플러그인 전이 의존성·GitHub 원격 권한/Secrets·배포 환경·Git 전체 이력은 검증하지 않았다. 인증/서버/민감정보 저장 계층은 이 UI 라이브러리에 없어 적용 대상이 아니다.
- **공개 의존성의 나머지 범위·경고**: 모든 플랫폼의 게시 메타데이터와 외부 저장소 설치는 미검증이다. 문서화된 deprecated 항목은 제거했으나 기존 `LocalUIViewController` 이동 안내·일부 직렬화/expect-actual 관련 경고·Gradle 10 호환 경고가 남아 있어 전체 경고 0개로 보고하지 않는다.

## 문서 유지 규칙

이슈의 상태를 바꿀 때 실제 수정 위치·적용 조건·검증 근거를 함께 갱신한다. 같은 원인으로 묶은 진입점은 모두 확인한다. 후속 검증에서 문제가 재현되면 해당 행을 `미해결` 또는 `검증 필요`로 되돌리고 재현 조건을 추가한다.
