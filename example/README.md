# Compose Hig Example app

예제 앱은 iOS, Android, Desktop, Web(WasmJS)을 지원합니다.

- `shared`: 공통 UI와 플랫폼 구현, 테스트, Wasm 실행부
- `androidApp`: Android 실행 모듈
- `desktopApp`: Desktop 실행 모듈
- `iosApp`: `shared` 프레임워크를 사용하는 Xcode 프로젝트

Desktop 실행과 JVM 테스트에는 `capsule` 의존성에 맞춰 JDK 21을 사용합니다. Android 바이트코드 대상은 Java 17을 유지합니다.

Android Studio의 Run Configuration으로 각 플랫폼을 실행할 수 있습니다.

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