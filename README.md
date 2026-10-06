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

## Important Notes

- Signup, login, profile edits, and logout use local demo data on the device.
- Demo login: `student@university.edu` / `password`.
- Local account storage is for prototyping only; there is no backend authentication.
- Catalog and reservation screens still use mock repositories and sample data.
- The package name is `com.example.libreserve`.
- The app name is configured as `ReserveX` in the resources.

## License

This project is currently provided as a local development/app prototype without a published license.
