# OralVisApp - Session-Based Image Capture App

A modern Android application built with Kotlin and Jetpack Compose for capturing and organizing
images in sessions with metadata storage.

## Features

###  Session Management

- **Start Session**: Begin a new image capture session with auto-generated Session ID
- **Capture Images**: Take multiple photos using device camera during an active session
- **End Session**: Save session with user details (Name, Age) and captured image count

### 🗄 Data Storage

- **Metadata**: Stored in SQLite database using Room
    - Session ID (auto-generated: SES_timestamp)
    - Name and Age (user input)
    - Timestamp and image count
- **Images**: Saved to app-specific storage
    - Path: `Android/data/com.example.oralvisapp/files/Pictures/Sessions/<SessionID>/`
    - Format: `IMG_yyyyMMdd_HHmmss.jpg`

###  Search Functionality

- Search sessions by Session ID
- View complete session details (metadata + timestamps)
- Browse all captured images for the session
- Display image file information (name, size)

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **Camera**: Android Camera API with FileProvider
- **Dependency Injection**: Manual (Repository pattern)

## App Structure

```
├── data/
│   ├── Session.kt          # Room entity
│   ├── SessionDao.kt       # Database operations
│   ├── AppDatabase.kt      # Room database
│   └── SessionRepository.kt # Data layer
├── ui/
│   ├── SessionViewModel.kt # Business logic
│   ├── MainScreen.kt       # Navigation
│   ├── SessionScreen.kt    # Session management UI
│   └── SearchScreen.kt     # Search functionality UI
└── MainActivity.kt         # Entry point
```

## Permissions Required

- `CAMERA` - For image capture
- `WRITE_EXTERNAL_STORAGE` - For saving images (API ≤ 28)

## Usage Flow

1. **Start Session**: Tap "Start New Session" to begin
2. **Capture Images**: Use "Capture Image" button multiple times
3. **End Session**: Tap "End Session" and enter Name/Age
4. **Search**: Switch to Search tab, enter Session ID to find sessions

### Storage 
### Method 1: File Manager

1. Open your device's **File Manager** app
2. Navigate to: `Internal Storage` → `Android` → `data` → `com.example.oralvisapp` → `files` →
   `Pictures` → `Sessions`
3. Each session folder (e.g., SES_timestamp) contains its captured images
