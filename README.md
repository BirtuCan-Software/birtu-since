# BirtuSince

**BirtuSince** is an open-source, 100% offline elapsed-time tracker for Android. It helps track habits, recurring chores, milestones, and intervals with live upward-counting timers, state thresholds, home screen widgets, and zero cloud telemetry.

Developed by [Birtucan Software](https://birtucansoftware.com).

---

## Highlights

- **Privacy by Design:** Zero internet permissions in the manifest. All data stays strictly on your device.
- **Modern Jetpack Compose & Material 3:** Dynamic UI with smooth animations, custom themes, and light/dark modes.
- **Home Screen AppWidget & Quick Settings Tile:** Instant access to start-over and quick capture directly from the system shade and launcher.
- **Biometric Security:** Optional fingerprint and face unlock protection via AndroidX Biometric.
- **Local JSON Portability:** Encrypted or plaintext backup and restore.

---

## Developing with Android Studio

We recommend using **[Android Studio](https://developer.android.com/studio)** for developing, testing, and building BirtuSince.

### Prerequisites
- **Android Studio:** Meerkat / Ladybug / Otter (or newer)
- **JDK:** Java 21 (bundled automatically with Android Studio)
- **Android SDK:** API 36 (`compileSdk`), with `minSdk` 26 (Android 8.0+)

### Setup
1. Launch Android Studio.
2. Select **Open** and choose this project root folder (`birtu-since`).
3. Android Studio will automatically sync the project Gradle configuration and download required SDK components.

### Running the App
1. Connect an Android physical device with USB debugging enabled, or start an Android Virtual Device (AVD) from Device Manager.
2. Select the `app` run configuration in the top toolbar.
3. Click the green **Run** button (or press `Shift + F10`).

### Running Tests
- In the project navigation pane, expand `app/src/test`.
- Right-click the test package and select **Run 'Tests in 'birtusince''** (or press `Ctrl + Shift + F10`).

---

## Building for Google Play Store

You can generate the production Android App Bundle (AAB) directly through Android Studio:

1. In Android Studio, go to the menu bar and select **Build > Generate Signed Bundle / APK...**
2. Choose **Android App Bundle** and click **Next**.
3. Select your release keystore (`.jks`), enter your key credentials, and select **Next**.
4. Choose the `release` build variant and click **Finish**.
5. Once completed, a notification will appear with a link to locate your generated `.aab` file ready for upload to Google Play Console.

---

## Contributing

Contributions, issues, and feature suggestions are welcome! Feel free to check the issues tab and submit pull requests.

---

## License

This project is open-source software licensed under the [MIT License](LICENSE).
