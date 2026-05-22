# Repository Guidelines

## Project Structure & Module Organization
MiWu is a Gradle Kotlin/Android multi-module project. The runnable app lives in `app/`. Core API modules are `miot-api`, `miot-api-common`, `miot-api-impl`, and `miot-api-kmp-impl`. Widget/runtime support lives in `miwu-support`, Android UI wrappers and resources in `miwu-android`, annotations in `miwu-support-annotation`, and KSP code generators in `miwu-support-processor` plus `miwu-icon-android-processor`.

Source code follows Gradle defaults: Android code under `src/main/java` or `src/main/kotlin`, resources under `src/main/res`, unit tests under `src/test`, and instrumentation tests under `src/androidTest`. Generated files are under `build/` and should not be edited manually. Room schemas are in `app/schemas`; icon mappings are declared in `miwu-support/icons.txt`.

## Build, Test, and Development Commands
- `jdk21 && ./gradlew :app:assembleDebug` builds the debug APK.
- `jdk21 && ./gradlew :app:compileDebugKotlin` quickly validates app Kotlin, Android resources, and KSP outputs.
- `jdk21 && ./gradlew :miwu-android:compileDebugKotlin` validates Android support wrappers and generated icon code.
- `jdk21 && ./gradlew :miwu-support:compileKotlin` validates core widget support and processor-generated support code.
- `jdk21 && ./gradlew test` runs available JVM unit tests.
- `jdk21 && ./gradlew connectedDebugAndroidTest` runs instrumentation tests on a connected device or emulator.

Use JDK 21 for normal development; module Gradle files set Java/Kotlin targets accordingly.

## Coding Style & Naming Conventions
Use Kotlin idioms and the existing style: 4-space indentation, concise expression bodies where already used, and package names that mirror module ownership. Classes and objects use `PascalCase`; functions, properties, and resource names use `camelCase` or Android `snake_case` as appropriate. Drawable icons use `ic_*` names, for example `ic_power.xml`. Keep KSP processor output deterministic and avoid editing generated sources.

## Testing Guidelines
Tests currently use JUnit 4 for local tests and AndroidX JUnit/Espresso for instrumentation tests. Name test files after the subject under test, for example `DeviceViewModelTest` or `SwitchBarWrapperTest`. Add focused tests for logic changes; for UI/resource changes, at minimum run the relevant compile task and include screenshots when behavior is visual.

## Commit & Pull Request Guidelines
Recent history uses short typed prefixes such as `Feat:`, `Fix:`, and `Refactor:`. Keep commits scoped and imperative, for example `Fix: add default icon fallback`. Pull requests should describe the user-visible change, list validation commands, link related issues, and include screenshots or recordings for UI changes. Do not include unrelated generated output, local environment files, or broad formatting churn.

## Security & Configuration Tips
Do not commit credentials, tokens, or machine-specific SDK paths. Keep local configuration in ignored local files where possible. Treat `local.properties`, signing configs, and generated build artifacts as environment-specific unless intentionally documented.
