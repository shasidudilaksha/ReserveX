# ReserveX

ReserveX is an Android library reservation app designed to help users discover resources, book study spaces, reserve meeting rooms, and manage upcoming bookings from a single mobile experience.

## Overview

This project is a Kotlin-based Android application built with the Android Jetpack stack. It combines a modern Material 3 interface with navigation-driven screens for onboarding, authentication, library exploration, reservations, and confirmation flows.

## Features

- User onboarding and authentication screens
- Home dashboard with quick access to core actions
- Book discovery and detail views
- Library seat reservation flow
- Meeting room booking and time slot selection
- Reservation management with modify/cancel workflows
- Confirmation screens and notifications support
- Mock data-driven experience for app demos and prototyping

## Tech Stack

- Kotlin
- Android SDK
- ViewBinding
- Navigation Component
- ViewModel + LiveData
- RecyclerView + ViewPager2
- Material Components
- Coroutines

## Project Structure

```text
ReserveX/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/libreserve/
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

## Main App Flow

The app is organized around these core user journeys:

1. Sign in or sign up
2. Browse the home dashboard
3. Reserve books, seats, or meeting rooms
4. Review reservation details
5. Confirm and track bookings in the reservation list

## Getting Started

### Prerequisites

- Android Studio
- JDK 17
- Android SDK with API 34 or newer

### Run the project

```bash
git clone <repository-url>
cd ReserveX
./gradlew assembleDebug
```

Then open the project in Android Studio and run it on an emulator or physical device.

### Build and run from a terminal

From the project root:

```bash
chmod +x gradlew                 # only needed once on Linux/macOS
./gradlew :app:assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

If an Android device or emulator is connected and authorized, install and launch it with:

```bash
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.libreserve/.SplashActivity
```

### Connect a phone over wireless debugging

Use the phone and computer on the same Wi-Fi network. On Android 11 or newer, open **Developer options → Wireless debugging**, enable it, and choose **Pair device with pairing code**. Keep the pairing dialog open, then run the commands shown by the phone (the ports are temporary):

```bash
adb pair PHONE_IP:PAIRING_PORT
# enter the six-digit pairing code shown on the phone
adb connect PHONE_IP:ADB_PORT
adb devices
```

The device should appear with a `device` status. If it shows `unauthorized`, accept the debugging prompt on the phone. If pairing or connection fails, confirm both devices are on the same network, turn Wireless debugging off and on again, and use the current ports displayed by the phone. To disconnect later:

```bash
adb disconnect PHONE_IP:ADB_PORT
```

### Test on the phone with hot reload-like iteration

ReserveX is a traditional XML/ViewBinding Android app, so it does not have Expo/React Native-style JavaScript hot reload. Android Studio provides the closest workflow:

1. Pair/connect the phone using the commands above, then select the wireless device in Android Studio.
2. Click **Run** once to install and launch the debug build.
3. For Kotlin or XML changes, use **Apply Changes** (the lightning-bolt button) to push changes without a full reinstall when Android Studio supports that change. For manifest, dependency, resource, or structural changes, use **Run** again.
4. Use **Logcat** filtered to `com.example.libreserve` while exercising onboarding, login, book discovery, seat reservation, meeting-room booking, confirmation, and reservation management.

You can also repeat the terminal cycle after each change:

```bash
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.libreserve/.SplashActivity
```

Keep the phone unlocked during installation and leave USB debugging/wireless debugging enabled. Wireless debugging and Apply Changes are development-only workflows; they do not represent a release build or production deployment.

## Important Notes

- Signup, login, profile edits, and logout use local demo data on the device.
- Demo login: `student@university.edu` / `password`.
- Local account storage is for prototyping only; there is no backend authentication.
- Catalog and reservation screens still use mock repositories and sample data.
- The package name is `com.example.libreserve`.
- The app name is configured as `ReserveX` in the resources.

## License

This project is currently provided as a local development/app prototype without a published license.
