# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android application called "TransmissionConnect" that allows remote control of Transmission BitTorrent client servers. The app is forked from the original TransmissionRemote project and integrates with the ShareConnect application.

- **Package**: `com.shareconnect.transmissionconnect`
- **Base Package**: `net.yupol.transmissionremote.app`
- **Language**: Primarily Java with some Kotlin components
- **Architecture**: MVVM with Dependency Injection (Hilt)

## Build Commands

### Standard Gradle Commands
```bash
# Build debug version
./gradlew assembleDebug

# Build release version
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Lint check
./gradlew lint
```

### Key Build Configuration
- **Compile SDK**: 33
- **Min SDK**: 26
- **Target SDK**: 33
- **Kotlin Version**: 2.2.0
- **Hilt Version**: 2.50
- **Java Version**: 11

## Application Architecture

### Core Components

1. **TransmissionRemote.kt** - Main application class managing:
   - Server configurations and active server state
   - Torrent filtering and sorting
   - Background notifications
   - Speed limit controls
   - Preference management

2. **MainActivity.java** - Primary entry point providing:
   - Torrent list management
   - Server switching interface
   - Torrent actions (start, stop, pause, remove)
   - File/magnet link handling
   - Search functionality

3. **Transport Layer** (`app/transport/`):
   - **BaseSpiceActivity.kt** - Base for network-enabled activities
   - **OkHttpTransportManager.kt** - HTTP communication with Transmission daemon
   - **SessionIdInterceptor.kt** - Handles X-Transmission-Session-Id headers
   - **BasicAuthenticator.kt** - Authentication handling

### Key Activities

- **MainActivity** - Main torrent management interface
- **TorrentDetailsActivity** - Detailed torrent information and management
- **AddServerActivity** - Server configuration
- **PreferencesActivity** - Application settings
- **ServersActivity** - Multiple server management

### Dependency Injection (Hilt)

The app uses Hilt for dependency injection with modules in `app/di/`:
- **DataStoreModule** - Preferences and data storage
- **FirebaseModule** - Analytics and crash reporting

### Data Layer

- **Server.java** - Server configuration model
- **Torrent.java** - Torrent data model (in `model/json/`)
- **PreferencesRepository.kt** - Centralized preferences management

## Key Features

1. **Multiple Server Support** - Connect to multiple Transmission servers
2. **Torrent Management** - Full CRUD operations on torrents
3. **File Handling** - Import .torrent files and magnet links
4. **Background Updates** - Notification system for completed torrents
5. **Filtering & Sorting** - Comprehensive torrent organization
6. **Theme Support** - Night mode and theme customization

## Development Notes

- The codebase mixes Java and Kotlin - maintain consistency with existing file types
- Uses RoboSpice for background network operations (legacy pattern)
- Firebase integration for analytics and crash reporting
- Material Design components throughout the UI
- Supports Android 8.0+ (API 26+)

## Test Configuration

- **Unit Tests**: JUnit 4 with Mockito and Truth assertions
- **Instrumentation Tests**: Espresso with AndroidX Test orchestrator
- **Mock Server**: Custom mock server module for testing

## Networking

- **Primary**: OkHttp with custom interceptors
- **Legacy**: RoboSpice with Google HTTP client
- **Authentication**: Basic Auth and session management
- **SSL**: Network security config for HTTPS connections