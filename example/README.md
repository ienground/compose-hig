# Compose Hig Example app

예제 앱은 iOS, Android, Desktop, Web(WasmJS)을 지원합니다.

- `shared`: 공통 UI와 플랫폼 구현, 테스트, Wasm 실행부
- `androidApp`: Android 실행 모듈
- `desktopApp`: Desktop 실행 모듈
- `iosApp`: `shared` 프레임워크를 사용하는 Xcode 프로젝트

Desktop 실행과 JVM 테스트에는 `capsule` 의존성에 맞춰 JDK 21을 사용합니다. Android 바이트코드 대상은 Java 17을 유지합니다.

Android Studio의 Run Configuration으로 각 플랫폼을 실행할 수 있습니다.

## 내비게이션 전환

카탈로그의 **내비게이션 → 네이티브 내비게이션** 토글로 iOS의 UIKit 내비게이션과 기존 CMP Navigation 3를 전환합니다. 기본값은 CMP이며 다른 플랫폼에서는 항상 CMP를 사용합니다.

전환해도 백스택과 외형 설정은 유지하지만, 화면 composition은 다시 생성하므로 검색·스크롤·데모의 화면 내부 상태는 초기화됩니다. iOS 네이티브 모드는 단일 스택이며, CMP 모드의 넓은 화면 목록·상세 배치는 CMP 모드에서 확인할 수 있습니다. 샘플 iOS 앱의 최소 버전은 18.2입니다.

`zone.ien.navigation:navigation:0.0.0`은 `example/shared`에서만 사용합니다. 로컬 버전은 다음 명령으로 먼저 배포합니다. 저장소 설정의 `mavenLocal`은 `zone.ien.navigation` 그룹에만 적용됩니다.

```shell
../compose-swift-navigation3/gradlew -p ../compose-swift-navigation3 :library:publishToMavenLocal
```

## Running on Desktop

```shell
:example:desktopApp:run
```

## Running on Web

```shell
:example:shared:wasmJsBrowserDevelopmentRun
```

## Running on iOS

Ensure that you have a Run Configuration that looks similar to this:

TODO insert image

```
<configuration name="example iOS App" type="KmmRunConfiguration" factoryName="iOS Application" CONFIG_VERSION="1" EXEC_TARGET_ID="$TARGET_ID" XCODE_PROJECT="$PROJECT_DIR$/example/iosApp/iosApp.xcodeproj" XCODE_CONFIGURATION="Debug" XCODE_SCHEME="iosApp">
  <method v="2">
    <option name="com.jetbrains.kmm.ios.BuildIOSAppTask" enabled="true" />
  </method>
</configuration>
```

## Running on Android