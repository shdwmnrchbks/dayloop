# Local build setup (Windows)

How this machine is configured to build dayloop without Android Studio.

## Toolchain locations

| Tool | Location | Version |
|---|---|---|
| JDK (Temurin 17) | `C:\Users\airenz1202\.dsh-tools\jdk-17` | 17.0.20.1 |
| Gradle (bootstrap only) | `C:\Users\airenz1202\.dsh-tools\gradle-8.10.2` | 8.10.2 |
| Android SDK | `C:\Users\airenz1202\AppData\Local\Android\Sdk` | — |
| Git (MinGit) | `C:\Users\airenz1202\.dsh-tools\mingit` | 2.55.0 |

The Gradle wrapper (`gradlew.bat`) is the canonical way to build; the full
Gradle distribution above was only used to generate the wrapper.

## Environment variables

Set at **user level** (effective in newly opened terminals):

- `JAVA_HOME = C:\Users\airenz1202\.dsh-tools\jdk-17`
- `ANDROID_HOME = C:\Users\airenz1202\AppData\Local\Android\Sdk`
- `PATH` += `%USERPROFILE%\.dsh-tools\mingit\cmd` (for `git`)
- `PATH` += `%LOCALAPPDATA%\Android\Sdk\platform-tools` (for `adb`)

`local.properties` in the repo root pins `sdk.dir` for Gradle (gitignored).

## Installed SDK packages

- `platforms;android-35` (compile/target SDK)
- `build-tools;35.0.0`
- `platform-tools` (adb, fastboot)

Licenses were pre-accepted via the standard license-hash files in
`<sdk>\licenses\` (same mechanism CI images use).

## Building

```powershell
cd C:\Users\airenz1202\Desktop\dayloop
.\gradlew.bat assembleDebug     # debug APK -> app\build\outputs\apk\debug\
.\gradlew.bat test              # unit tests
.\gradlew.bat lint              # Android lint
```

First build downloads all dependencies (several hundred MB) and takes a
while; later builds are incremental and much faster.

## Notes

- The toolchain lives outside the repo; nothing here except `local.properties`
  (gitignored) is machine-specific.
- If Android Studio is installed later, point it at the SDK path above and it
  will reuse everything.
- An emulator/AVD is **not** installed yet — `adb install` the debug APK to a
  physical device with USB debugging enabled, or ask for an AVD setup.
