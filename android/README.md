# Android Playground

The Kotlin / Jetpack Compose app in `mobile-playground`.
Open this `android/` directory as a project in Android Studio and run the `app` configuration.
The app starts with a sample catalog. Each card opens an independent experiment;
the toolbar back button or Android's system Back returns to the catalog.

## Samples

| Sample | What to try |
| --- | --- |
| Scrollable segments | Change selection, insert/remove tabs, and narrow the viewport. |
| Alignment | Change both axes to inspect the nine logical positions inside a Box. |
| Bottom sheet | Open/dismiss a native modal sheet and change partial expansion behavior. |
| Animation | Toggle size and color, including while an animation is running. |

The catalog and each new sample include Compose previews. The app follows the
system light/dark appearance and uses Navigation Compose for its back stack.

## Add an experiment

1. Add a self-contained `@Composable` in `app/src/main/java/app/muukii/androidplayground/samples/`.
2. Add a `Sample` entry to `playgroundSamples` in `Sample.kt` with a unique, stable `id`.
3. The catalog and navigation destination are registered automatically.

Keep experimental state inside the sample; use `rememberSaveable` when it should
survive activity recreation. The app supplies the toolbar and consumes system
insets before presenting the sample. `SampleLayout` provides a scrollable,
width-limited container for controls on small windows or with large text.

## Scrollable segmented control

- Short labels share the available width; overflowing labels scroll horizontally.
- The selection capsule moves between tabs, and the selected tab is brought into
  view when selection, content, or viewport size changes.
- Selection belongs to the caller. Scrolling alone never selects a different tab.
- Item identifiers must be unique and stable; the caller handles selection when
  removing items.
- Android tab semantics, right-to-left placement, system font scaling, and a
  minimum 48 dp touch target are supported.

The sample includes two tabs, a growing collection, First/Middle/Last selection,
tab insertion, tab removal, and a narrow viewport. `SegmentedControlPreview`
also provides a large-font preview.

```kotlin
ScrollableSegmentedControl(
    items = listOf(SegmentItem("likes", "いいね！"), SegmentItem("visitors", "足あと")),
    selectedId = selectedId,
    onSelected = { selectedId = it },
)
```

## Build and verify

Use JDK 21 and the Android SDK configured in your local `local.properties`.
The pinned Gradle wrapper and dependency catalog are included. Create the local
SDK configuration if Android Studio has not already written it:

```properties
sdk.dir=/absolute/path/to/Android/sdk
```

Then build and run the checks:

```sh
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
./gradlew :app:connectedDebugAndroidTest
./gradlew :app:installDebug
```

For command-line launch after starting an AVD in Android Studio's Device Manager:

```sh
"$ANDROID_HOME/platform-tools/adb" shell am start -W \
  -n app.muukii.androidplayground/.MainActivity
```

Set `ANDROID_HOME` to your SDK directory. On macOS the usual location is
`$HOME/Library/Android/sdk`. `local.properties`, build outputs, and IDE caches are
excluded from Git.

Robolectric tests on API 36 cover equal-width sizing, taps, external selection,
inserting the selected tab, resizing, scroll-only gestures, and RTL scrolling.
They also cover every catalog destination, toolbar Back, alignment controls, and
animation toggling. On-device smoke tests cover system Back, sheet dismissal,
and the existing segmented-control interactions.

Visual reference: [Ubank on Mobbin](https://mobbin.com/explore/screens/8859286f-85f4-478f-8c0a-c08b2a7812a7).

The Android 17 smoke test uses public accessibility actions to exercise the
installed demo. Espresso 3.7.0 cannot initialize its legacy InputManager
reflection on that runtime, so Compose component tests run on Robolectric API 36.

## Verification — 2026-10-12

- Debug APK assembled successfully.
- All 8 Robolectric tests passed on API 36.
- Both device smoke tests passed on Pixel 10 Pro / Android 17 (API 37), including
  catalog navigation, system Back, sheet dismissal by Back and its close button,
  and the original segmented-control interactions.
- Lint completed with 0 errors and 8 warnings, covering existing dependency/SDK
  update suggestions and template resources.
- All four experiment screens were opened and visually inspected. Screenshots:
  [catalog](docs/screenshots/catalog.png),
  [scrollable segments](docs/screenshots/segmented-control.png),
  [alignment](docs/screenshots/alignment.png),
  [bottom sheet](docs/screenshots/bottom-sheet.png),
  [animation](docs/screenshots/animation.png).

The initial emulator session, loaded from an existing snapshot, stopped
responding during screenshot capture. The cause was not established. The device
was cold-booted without clearing its data, and the device tests passed again;
the final app launch and animation interaction were verified after that boot.
