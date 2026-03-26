[![Maven Central](https://maven-badges.sml.io/sonatype-central/zone.ien.hig/hig/badge.svg?style=flat&subject=Sonatype%20Central&color=blue)](https://maven-badges.sml.io/sonatype-central/zone.ien.hig/hig)

[![Main Workflow](https://github.com/ienground/compose-hig/actions/workflows/publish.yml/badge.svg)](https://github.com/ienground/compose-hig/actions/workflows/publish.yml)

# Compose-HIG

This is a fork of [alexzhirkevich/compose-cupertino](https://github.com/alexzhirkevich/compose-cupertino) and [slanos/compose-cupertino](https://github.com/slanos/compose-cupertino).

Additionally, this repo has automated builds to enable faster releases, to take advantage of new compose multiplatform features as they become available.

## New features (compared to the OG `compose-cupertino`)

- Reflect the Human Interface Guidelines for iOS 26 and later, incorporating Liquid Glass
- Add a Backdrop using [Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass)
-  Attempting to remove `icons-extended`.

# Usage

This package is published to Maven Central Repository: [cupertino-core on Maven Central](https://central.sonatype.com/artifact/zone.ien.hig/hig-core)

Depend on the [latest version](https://github.com/ienground/compose-hig/releases) by declaring this in libs.versions.toml:

```
hig = "$latestVersion"

hig = { group = "zone.ien.hig", name = "hig", version.ref = "hig" }
hig-adaptive = { group = "zone.ien.hig", name = "hig-adaptive", version.ref = "hig" }
hig-native = { group = "zone.ien.hig", name = "hig-native", version.ref = "hig" }
```

## Try it

Wanna see what the library feels like? The latest version builds and deploys the Kotlin/WasmJS target to github pages: https://ienground.github.io/compose-hig/

# To-do
Compared to the previous library, we have made efforts to update many components from the Human Interface Guidelines to match the latest design, but there is still much left to be done. Please help complete the library with your contributions.

[ ] BottomBar

[ ] Search Bar

[ ] Dropdown Native

[ ] Dialog

[ ] Segmented Control

...

Additionally, some comments remain unchanged from the previous library. We are doing our best to revise them, but we need your help.

# License
```
Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
Copyright (c) 2025. Scott Lanoue.
Copyright (c) 2026. IENGROUND of IENLAB.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

# Contributions

Contributions are always appreciated! Since builds to maven central go through GitHub Actions, we can get your changes in quickly.

Ensure that the copyright information is included in your file(s):

```
/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
 * Copyright (c) 2026. IENGROUND of IENLAB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```

Ideally you set up Android Studio / IDE to handle copyright notices for you: https://stackoverflow.com/a/48718711/1730421