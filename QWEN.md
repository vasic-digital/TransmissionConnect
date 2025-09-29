# TransmissionConnect - Android App

## Project Overview

TransmissionConnect is an Android application that allows users to remotely control the [Transmission BitTorrent client](https://www.transmissionbt.com) through its web interface. The app is designed to work with remote access enabled in Transmission preferences and provides integration with the [ShareConnect](https://github.com/vasic-digital/ShareConnect) application.

The main codebase is forked from [https://github.com/y-polek/TransmissionRemote](https://github.com/y-polek/TransmissionRemote).

**Project Structure:**
- Root project name: `TransmissionConnect`
- Main modules: `:app` (main Android application) and `:mockserver` (for testing)
- Package name: `com.shareconnect.transmissionconnect`

**Key Technologies & Architecture:**
- Written in Java and Kotlin
- Uses modern Android architecture with Hilt for dependency injection
- Uses Material Design components and MaterialDrawer library
- Implements RoboSpice for network requests
- Uses OkHttp for HTTP communication
- Implements ViewBinding and DataBinding
- Follows Android best practices with lifecycle awareness

**Key Features:**
- Connect to multiple Transmission BitTorrent servers
- View and manage torrents (start, stop, remove, etc.)
- Add torrents via file, magnet links, or HTTP/HTTPS URLs
- Support for torrent details viewing
- Sorting and filtering capabilities
- Speed limit controls (turtle mode)
- Search functionality
- Push notifications for finished torrents
- Background update service
- Dark/light theme support
- Server management with multiple server support

## Building and Running

### Prerequisites
- Android Studio (latest version recommended)
- Android SDK with API level 33 (compileSdk)
- Android SDK with minSdk 26 and targetSdk 33
- Java 11 (as specified in build configuration)

### Build Commands
```bash
# Clean the project
./gradlew clean

# Build the application
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

### Running Tests
```bash
# Run unit tests
./gradlew test

# Run Android instrumented tests
./gradlew connectedAndroidTest
```

### Known Build Issues and Solutions

The project may encounter Kotlin metadata compatibility issues with newer Android Gradle Plugin versions. If you encounter errors like:
```
Provided Metadata instance has version X.X.X, while maximum supported version is X.X.X
```

You may need to:
1. Clear all Gradle caches: `./gradlew --stop && rm -rf ~/.gradle/caches/ && rm -rf .gradle/`
2. Consider updating Kotlin and Hilt dependencies to compatible versions
3. Temporarily disable data binding if needed for debugging
4. Use Android Studio's Gradle wrapper settings to ensure consistent build environment

Current configuration uses:
- Kotlin version: 2.2.20
- Hilt version: 2.55
- Android Gradle Plugin: 8.2.2

### Migration to KSP (Kotlin Symbol Processing)

To migrate from KAPT to KSP for better performance and compatibility with newer Kotlin versions:

1. Add KSP plugin in settings.gradle:
   ```kotlin
   plugins {
       id 'com.google.devtools.ksp' version '[compatible_ksp_version]' apply false
   }
   ```

2. In app/build.gradle, replace:
   ```kotlin
   plugins {
       id 'org.jetbrains.kotlin.kapt'  // Remove this
       id 'com.google.devtools.ksp'    // Add this
   }
   ```

3. Replace dependencies:
   ```kotlin
   // Replace this:
   kapt "com.google.dagger:hilt-compiler:$hilt_version"
   // With this:
   ksp "com.google.dagger:hilt-compiler:$hilt_version"
   ```

Note: KSP version should match your Kotlin version pattern [kotlin_version]-[ksp_plugin_version].

### Development Conventions

**Code Structure:**
- The main application class is `TransmissionRemote.kt` which extends `Application`
- `MainActivity.java` serves as the primary activity with comprehensive UI components
- Code is organized by feature packages (e.g., `torrentlist`, `torrentdetails`, `server`, `preferences`, `notifications`)
- Uses Java for activities and fragments, Kotlin for application logic and repositories

**Architecture:**
- Uses Hilt for dependency injection
- Implements application-level state management in `TransmissionRemote` class
- Implements custom networking layer using RoboSpice and OkHttp
- Follows Android's recommended architecture patterns with proper lifecycle management
- Implements custom UI components and themes

**Key Design Patterns:**
- Observer pattern for state changes and updates
- Singleton pattern through `TransmissionRemote.instance`
- Factory pattern for job creation
- Custom listeners for event handling (e.g., `OnActiveServerChangedListener`, `OnTorrentsUpdatedListener`)

**Permissions:**
- `INTERNET` - for network communication with Transmission server
- `ACCESS_NETWORK_STATE` - to check network status
- `RECEIVE_BOOT_COMPLETED` - for background services
- `VIBRATE` - for notifications
- `POST_NOTIFICATIONS` - for push notifications (Android 13+)

## Project Configuration

The project uses Gradle as the build system with:
- Kotlin version 1.9.21
- Android Gradle Plugin version 8.1.4 (recommended for compatibility)
- Hilt version 2.50
- Compile SDK version 33
- Min SDK version 26
- Target SDK version 33
- Java version 11

### Known Compatibility Issue

If you encounter Kotlin metadata compatibility errors (like "Provided Metadata instance has version X.X.X, while maximum supported version is X.X.X"), use AGP version 8.1.4 instead of newer versions like 8.13.0. Newer AGP versions pull in dependencies compiled with newer Kotlin versions that are incompatible with the project's configured Kotlin version.

**Notable Dependencies:**
- AndroidX libraries for modern Android development
- Material Design components
- Hilt for dependency injection
- Firebase for crashlytics and analytics
- OkHttp for networking
- RoboSpice for REST communication
- MaterialDrawer by Mike Penz
- Commons IO for file operations
- Gson for JSON handling