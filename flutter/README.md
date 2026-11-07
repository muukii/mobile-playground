# Flutter Playground

An independently runnable Flutter app for iOS and Android. It opens a catalog of
small experiments, following the native playgrounds in this repository.
The app uses Material 3 and follows the system light/dark appearance.

## Setup and run

Install [Flutter](https://docs.flutter.dev/install) **3.47.9** (Dart **3.13.7**)
and add its `bin/` directory to your `PATH`. The app's dependency lockfile is
committed. No third-party runtime packages are needed.

From this `flutter/` directory:

```sh
flutter doctor
flutter pub get
flutter devices
flutter run -d <device-id>
```

- **iOS:** Use macOS with Xcode and an installed iOS Simulator runtime. Start a
  simulator in Xcode, then choose its ID from `flutter devices`. For a physical
  device, open `ios/Runner.xcworkspace` and select your signing team.
- **Android:** Install Android Studio and the Android SDK, accept licenses with
  `flutter doctor --android-licenses`, and start an emulator or connect a device.
  Follow `flutter doctor` for any missing Java / Android SDK components.
- Open this directory, rather than the repository root or the native
  `../android/` playground, in an IDE with Flutter support.
- While `flutter run` is attached, press `r` for hot reload or `R` for hot restart.

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
dart format --output=none --set-exit-if-changed lib test
flutter analyze
flutter test
flutter build ios --simulator --debug
flutter build apk --debug
```

Widget tests cover catalog navigation, toolbar and system Back, all alignment
positions, animation interruption and duration, modal-sheet dismissal, draggable
sheet expansion and scrolling, state isolation, and narrow-screen / large-text /
dark-mode layouts. Native builds require the corresponding platform toolchains.
