# Expiry Tracker

Expiry Tracker is an Android application built with Jetpack Compose that helps users track the expiry dates of their products.

## Features

*   Track expiry dates of products.
*   User authentication using Firebase.
*   Data persistence with Firestore.
*   Modern UI built with Jetpack Compose.

## Tech Stack and Dependencies

*   [Kotlin](https://kotlinlang.org/)
*   [Jetpack Compose](https://developer.android.com/jetpack/compose) for UI
*   [Firebase Authentication](https://firebase.google.com/docs/auth) for user management
*   [Firebase Firestore](https://firebase.google.com/docs/firestore) for data storage
*   [AndroidX Libraries](https://developer.android.com/jetpack/androidx) (Core, Lifecycle, Activity)
*   [Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation) for in-app navigation

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

*   Android Studio Iguana | 2023.2.1 or later.
*   Android SDK 36 or later.
*   A Firebase project.

### Installation

1.  Clone the repo
    ```sh
    git clone https://github.com/your_username_/ExpiryTracker.git
    ```
2.  Open the project in Android Studio.
3.  Set up your Firebase project:
    *   Create a new Firebase project at [https://console.firebase.google.com/](https://console.firebase.google.com/).
    *   Add an Android app to your Firebase project with the package name `com.dev.expirytracker`.
    *   Download the `google-services.json` file and place it in the `app` directory of the project.
4.  Sync the project with Gradle files.

## Building the APK

To build the APK for the app, you can use either Android Studio or the Gradle command line.

### Using Android Studio

1.  Open the project in Android Studio.
2.  Go to **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
3.  The APK will be generated in the `app/build/outputs/apk/debug` directory.

### Using Gradle

1.  Open a terminal in the root of the project.
2.  Run the following command:
    ```sh
    ./gradlew assembleDebug
    ```
3.  The APK will be generated in the `app/build/outputs/apk/debug` directory.

## License

Distributed under the MIT License. See `LICENSE` for more information.
