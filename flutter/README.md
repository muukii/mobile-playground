# Flutter Playground

An independently runnable Flutter app for iOS and Android. It opens a catalog of
small experiments, following the native playgrounds in this repository.
The app uses Material 3 and follows the system light/dark appearance.

## Setup and run

Install [mise](https://mise.jdx.dev/getting-started.html), then run the following
from the repository root (these tasks also work from this `flutter/` directory):

```sh
mise trust
mise run flutter:setup
mise run flutter:doctor
mise run flutter:devices
mise run flutter:run -d <device-id>
```

The root `mise.toml` pins **Flutter 3.47.9** and its bundled **Dart 3.13.7**.
`mise run` installs missing tools automatically; `flutter:setup` then fetches
the app dependencies. A separate Flutter / Dart install or shell activation is
not required for the tasks. The app's dependency lockfile is committed.
Machine-specific overrides belong in the ignored `mise.local.toml`.

On macOS, prepare the Flutter iOS artifacts with:

```sh
mise run flutter:setup-ios
```

- **iOS:** Install Xcode and an iOS Simulator runtime manually, complete Xcode's
  first-launch setup, and select its command-line tools. `flutter:setup-ios`
  checks Xcode and caches Flutter's iOS artifacts; it does not install Xcode,
  accept licenses, or boot a simulator. Start a simulator in Xcode, then choose
  its ID from `flutter:devices`. For a physical device, open
  `ios/Runner.xcworkspace` and select your signing team.
- **CocoaPods:** Not required by this app, which uses Flutter's default Swift
  Package Manager integration. Plugins added later might require CocoaPods if
  they do not support Swift Package Manager. `flutter:doctor` may still warn
  about missing CocoaPods even though this app builds without it.
- **Android:** Install Android Studio and the Android SDK, accept licenses with
  `mise exec -- flutter doctor --android-licenses`, and start an emulator or
  connect a device. Follow `flutter:doctor` for any missing Java / Android SDK
  components. These prerequisites are not installed by the mise tasks.
- Open this directory, rather than the repository root or the native
  `../android/` playground, in an IDE with Flutter support.
- While `flutter run` is attached, press `r` for hot reload or `R` for hot restart.

`flutter:run` passes additional arguments to `flutter run` and connects stdin
directly for hot reload. To configure your IDE's Flutter SDK path, use
`mise where flutter`. For other Flutter commands, use `mise exec -- flutter ...`.

The generated host projects in `ios/` and `android/` belong to this Flutter app;
they do not depend on the native playgrounds. SDK paths, generated Flutter files,
IDE caches, signing keys, and build outputs are ignored locally.

## Samples

| Sample | What to try |
| --- | --- |
| Alignment | Change horizontal and vertical placement across nine positions. |
| Animation | Toggle size, color, and radius, reverse in flight, and change duration. |
| Bottom sheet | Open/dismiss a modal sheet; enable the draggable, scrollable variant. |

Use the toolbar Back button or system Back to return to the catalog. Each visit
starts with fresh experiment state.

## Add an experiment

1. Add a self-contained widget in `lib/samples/`.
2. Register a `PlaygroundSample` in `lib/samples.dart` with a unique, stable `id`
   and a `builder` that creates the widget.
3. The catalog and its navigation entry are generated from that registration.

The app supplies the scaffold, toolbar, Back navigation, and safe-area padding.
Keep state inside each experiment. `SampleLayout` provides a scrollable,
width-limited container for controls on small screens and with large text.

## Verify

```sh
mise run flutter:check
mise run flutter:build-ios
mise exec -- flutter build apk --debug
```

`flutter:check` runs the formatting check, static analysis, and widget tests
sequentially and stops on the first failure. `flutter:build-ios` prepares iOS
artifacts before building. Neither task deploys or uploads an app.

Widget tests cover catalog navigation, toolbar and system Back, all alignment
positions, animation interruption and duration, modal-sheet dismissal, draggable
sheet expansion and scrolling, state isolation, and narrow-screen / large-text /
dark-mode layouts. Native builds require the corresponding platform toolchains.
