# Mobile Playground

Small, independently runnable experiments for Apple platforms and Android.

```text
mobile-playground/
├── apple/
│   ├── apple-playground.xcodeproj/
│   └── apple-playground/
└── android/
    ├── app/
    ├── gradle/
    └── gradlew
```

## Apple

Open the Xcode project and run the `apple-playground` scheme:

```sh
open apple/apple-playground.xcodeproj
```

The StorybookKit entry point opens the experiments in `apple/apple-playground/`.
The Xcode project and its source directory move together so their relative paths
remain valid.

## Android

Open `android/` as the project directory in Android Studio and run `app`.
The app starts with a catalog of Compose experiments.

From the repository root, build and run the unit tests with:

```sh
./android/gradlew -p android :app:assembleDebug :app:testDebugUnitTest
```

See [the Android README](android/README.md) for SDK setup, sample registration,
device tests, and screenshots.

## Repository structure

Both platforms share one Git repository and retain their own build systems.
Platform-specific ignore rules live in `apple/.gitignore` and
`android/.gitignore`. The original Apple and Android commit histories are
preserved in this repository.
