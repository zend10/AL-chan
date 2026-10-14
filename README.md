<p align="center">
  <img alt="Tokyo's — Anime and Manga Tracker" src="docs/images/tokyos-logo.svg" width="100%" />
</p>

<p align="center"><strong>An unofficial Android client for AniList, customized by Tokyo for anime and manga tracking.</strong></p>

<p align="center">
  <a href="https://github.com/tokyo720022-create/AL-chan">Source code</a> ·
  <a href="https://anilist.co/">AniList</a>
</p>

## About

**Tokyo's** is a personal customization of the open-source AL-chan Android app. It focuses on tracking anime and manga through AniList, with a customized interface and ongoing maintenance of the Android build toolchain.

Tokyo's is unofficial and is not affiliated with, endorsed by, or sponsored by AniList.

## Features

- Track anime and manga lists, progress, statuses, and scores.
- Discover trending, popular, seasonal, and upcoming anime.
- Browse information about anime, manga, characters, staff, and studios.
- Check anime airing schedules and upcoming episodes.
- Explore personal statistics and recommendations.
- Customize list layouts and the app's appearance.

## Project status

- **Current provider:** AniList.
- **Platform:** Android.
- **App version:** 2.1.3 (build version code 200008).
- **Minimum Android API:** 23.
- **Target Android API:** 35.
- **Compile SDK:** 36.

Multi-provider two-way synchronization with MyAnimeList and Simkl is a planned development goal, not a released feature. Until it is implemented and tested, this app should be treated as an AniList client.

## Build environment

Use the following versions for a build environment matching the current GitHub Actions configuration:

| Component | Version |
| --- | --- |
| JDK | 17 (GitHub Actions uses Eclipse Temurin 17) |
| Android Gradle Plugin | 8.11.1 |
| Gradle Wrapper | 8.14.6 |
| Kotlin Gradle Plugin | 2.2.21 |
| Android SDK Platform | 36 |
| Android SDK Build Tools | 35.0.0 |

The app's Java source and target compatibility are set to Java 17, and Kotlin bytecode targets JVM 17. These are **build and bytecode settings**; they do not mean the installed app runs a desktop JDK on the phone. Android runs the app using the Android runtime on the device.

### About `.java-version`

The `.java-version` file is a local development-tool hint, not an Android runtime setting. Some Java version managers read it to choose a JDK for commands run in the project directory. The current file pins an older Oracle JDK 17.0.8 label, while CI explicitly selects Temurin JDK 17. It does not override the GitHub Actions JDK selection and does not change the Java version inside the installed app. If a local version manager honors this file, align its value with the JDK actually installed on that machine.

## Build locally

1. Install JDK 17 and Android SDK Platform 36 with Build Tools 35.0.0.
2. Create `apikey.properties` in the repository root. This file is intentionally excluded from Git. For a compile-only local build, it can contain empty Java string literals:

   ```properties
   YOUTUBE_API_KEY=""
   SPOTIFY_API_KEY=""
   ```

   Some features that depend on API credentials will not work with empty values. Do not commit real API keys. A key bundled into an APK can be extracted, so restrict any YouTube key in the provider console and monitor its usage.

3. From the repository root, run:

   ```bash
   ./gradlew assembleDebug
   ```

4. The debug APK is generated under `app/build/outputs/apk/debug/`.

On Windows, use `gradlew.bat assembleDebug` instead of `./gradlew assembleDebug`.

## License

See [LICENSE](LICENSE) for license terms and upstream attribution.
