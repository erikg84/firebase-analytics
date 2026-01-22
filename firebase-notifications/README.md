# Firebase Notifications SDK for Kotlin Multiplatform

A Kotlin Multiplatform (KMP) Firebase Notifications SDK for Android and iOS, built with Koin dependency injection and KMPNotifier.

## Features

- 🔔 Unified notification API for Android and iOS
- 🔐 Built-in permission handling for both platforms
- 📲 Local notification support
- 🚀 Firebase Cloud Messaging (FCM) integration
- 🎯 Type-safe notification models with sealed interfaces
- 💉 Dependency injection with Koin 4.1.1 and KSP annotations
- 📱 Support for Android (minSdk 24) and iOS (arm64, simulator)
- 🔧 Built with Kotlin 2.2.20 and Compose Multiplatform ready
- 📦 Published to GitHub Packages

## Installation

Add the GitHub Packages repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/erikg84/firebase-analytics")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

Add the dependency to your module's `build.gradle.kts`:

```kotlin
commonMain {
    dependencies {
        implementation("com.dallaslabs.sdk:firebase-notifications:1.0.0")
    }
}
```

## Setup

### Initialize Koin Module

In your application initialization:

```kotlin
import com.dallaslabs.firebase.notifications.FirebaseNotificationsModule
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun initializeKoin() {
    startKoin {
        modules(FirebaseNotificationsModule().module)
    }
}
```

### Inject NotificationManager

```kotlin
import com.dallaslabs.firebase.notifications.NotificationManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MyViewModel : KoinComponent {
    private val notifications: NotificationManager by inject()

    suspend fun setupNotifications() {
        // Initialize notification system
        notifications.initialize()

        // Request permission
        val granted = notifications.requestPermission()
        if (granted) {
            // Get FCM token
            val token = notifications.getPushToken()
            println("FCM Token: $token")
        }
    }
}
```

## Usage

### Request Notification Permission

```kotlin
suspend fun requestNotificationPermission() {
    val granted = notifications.requestPermission()
    if (granted) {
        println("Notification permission granted")
    } else {
        println("Notification permission denied")
    }
}
```

**Platform Behavior:**
- **Android 13+ (API 33+)**: Shows system permission dialog
- **Android 12 and below**: Returns true immediately (no runtime permission needed)
- **iOS**: Shows system notification permission dialog

### Check Permission Status

```kotlin
suspend fun checkPermission() {
    val granted = notifications.isPermissionGranted()
    if (granted) {
        // Proceed with notifications
    }
}
```

### Send Local Notification

```kotlin
suspend fun sendNotification() {
    notifications.sendLocalNotification(
        title = "New Message",
        body = "You have a new message from John",
        payloadData = mapOf("messageId" to "12345", "senderId" to "john_doe")
    )
}
```

### Handle Notification Clicks

```kotlin
fun setupNotificationClickHandler() {
    notifications.onNotificationClicked { payloadJson ->
        // Parse payload and handle click
        val data = Json.decodeFromString<Map<String, String>>(payloadJson)
        val messageId = data["messageId"]
        // Navigate to message screen
        navigateToMessage(messageId)
    }
}
```

### Get FCM Push Token

```kotlin
suspend fun getToken() {
    val token = notifications.getPushToken()
    // Send token to your backend server
    sendTokenToServer(token)
}
```

### Observe Token Changes

FCM tokens can be refreshed by the system. Subscribe to token changes:

```kotlin
fun observeTokenChanges() {
    lifecycleScope.launch {
        notifications.tokenFlow.collect { token ->
            token?.let {
                println("New FCM Token: $it")
                sendTokenToServer(it)
            }
        }
    }
}
```

### Cancel Notifications

```kotlin
// Cancel specific notification
notifications.cancelNotification("notification_id_123")

// Cancel all notifications
notifications.cancelAllNotifications()
```

## Platform Setup

### Android Setup

#### 1. Add Firebase Configuration

Download `google-services.json` from Firebase Console and add it to your Android app:

```
app/
└── google-services.json
```

#### 2. Apply Google Services Plugin

In your app's `build.gradle.kts`:

```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.0"
}
```

In your project's root `build.gradle.kts`:

```kotlin
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
    }
}
```

#### 3. Add Notification Icon (Optional)

Create a notification icon in `res/drawable`:

```
res/
└── drawable/
    └── ic_notification.xml
```

Pass to config:

```kotlin
val notificationManager = NotificationManager(
    NotificationConfig(
        notificationIconResId = R.drawable.ic_notification
    )
)
```

#### 4. Initialize in Application

```kotlin
class MyApp : Application(), KoinComponent {
    private val notificationManager: NotificationManager by inject()

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
        initializeKoin()

        // Initialize notifications
        lifecycleScope.launch {
            notificationManager.initialize()
        }
    }
}
```

### iOS Setup

#### 1. Enable Push Notifications Capability

In Xcode:
1. Select your target
2. Go to **Signing & Capabilities**
3. Click **"+ Capability"**
4. Add **"Push Notifications"**

Or in `project.yml` (if using XcodeGen):

```yaml
targets:
  MyApp:
    capabilities:
      Push Notifications: {}
```

#### 2. Add Background Modes (Optional)

For background notification handling, add Background Modes capability:

```yaml
capabilities:
  Push Notifications: {}
  Background Modes:
    - remote-notification
```

Or in Xcode:
1. **Signing & Capabilities** → **+ Capability** → **Background Modes**
2. Enable **"Remote notifications"**

#### 3. Upload APNs Certificate

1. Generate APNs authentication key or certificate in [Apple Developer Portal](https://developer.apple.com/account/resources/certificates/list)
2. Upload to Firebase Console:
   - Go to **Project Settings** → **Cloud Messaging** → **Apple app configuration**
   - Upload your APNs certificate or authentication key

#### 4. Initialize in App

```swift
import SwiftUI
import FirebaseNotificationsSDK

@main
struct MyApp: App {
    init() {
        // Initialize Koin
        KoinKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

## Configuration

### NotificationConfig

Customize notification behavior:

```kotlin
val config = NotificationConfig(
    enabled = true,                           // Master switch
    showPushNotifications = true,             // Auto-display FCM push notifications
    notificationIconResId = R.drawable.ic_notification  // Android icon (optional)
)

val notificationManager = NotificationManager(config)
```

## Model Classes

### NotificationPriority

Control notification display behavior:

```kotlin
sealed interface NotificationPriority {
    object High : NotificationPriority      // Heads-up notification on Android
    object Default : NotificationPriority   // Standard behavior
    object Low : NotificationPriority       // Minimal interruption
}
```

### NotificationChannel (Android Only)

Configure Android notification channels:

```kotlin
data class NotificationChannel(
    val id: String,
    val name: String,
    val description: String? = null,
    val importance: NotificationPriority = NotificationPriority.Default,
    val enableVibration: Boolean = true,
    val enableSound: Boolean = true
)
```

### PushNotification

Represents an FCM push notification:

```kotlin
data class PushNotification(
    val title: String,
    val body: String,
    val imageUrl: String? = null,
    val data: Map<String, String> = emptyMap(),
    val priority: NotificationPriority = NotificationPriority.Default
)
```

## Architecture

The SDK follows clean architecture principles:

```
com.dallaslabs.firebase.notifications
├── NotificationManager (interface)        # Main API
├── NotificationConfig                     # Configuration
├── model/
│   ├── NotificationPriority              # Sealed interface
│   ├── NotificationChannel               # Android channels
│   └── PushNotification                  # Push data model
└── FirebaseNotificationsModule           # Koin module
```

## Under the Hood

This SDK wraps [KMPNotifier](https://github.com/mirzemehdi/KMPNotifier) (version 1.6.1), the industry-standard Kotlin Multiplatform notification library. KMPNotifier provides:

- Unified notification API across platforms
- Firebase Cloud Messaging integration
- Permission handling
- Local notification scheduling
- Push token management

## Requirements

- Kotlin 2.2.20
- Android minSdk 24
- iOS 12+
- Gradle 8.5+

## Dependencies

- KMPNotifier: 1.6.1
- Koin Core: 4.1.1
- Koin Annotations: 2.0.0
- Kotlinx Coroutines: 1.10.2

## ProGuard

ProGuard rules are included automatically. If you need to add custom rules, they are located in `proguard-rules.pro`.

## Publishing

The library is automatically published to GitHub Packages when a release tag is created:

```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

## Testing

### Local Testing

Test the SDK in your local environment using Maven Local:

```bash
# Publish to Maven Local
./gradlew :firebase-notifications:publishToMavenLocal

# Use in your app
repositories {
    mavenLocal()
}
```

### Integration Testing

1. **Android**: Test on physical device with FCM
2. **iOS**: Test on physical device with APNs
3. **Permission Flow**: Verify permission dialogs appear correctly
4. **Notifications**: Verify notifications display with correct title/body/icon
5. **Click Handling**: Verify notification clicks trigger callback with correct payload
6. **Token Retrieval**: Verify FCM token is retrieved and refreshed

## Example: Complete Integration

```kotlin
@Composable
fun MyApp() {
    val notificationManager: NotificationManager = koinInject()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Initialize
        notificationManager.initialize()

        // Request permission
        val granted = notificationManager.requestPermission()

        if (granted) {
            // Get FCM token
            val token = notificationManager.getPushToken()
            sendTokenToServer(token)

            // Setup click handler
            notificationManager.onNotificationClicked { payloadJson ->
                handleNotificationClick(payloadJson)
            }
        }
    }

    Button(onClick = {
        scope.launch {
            notificationManager.sendLocalNotification(
                title = "Test Notification",
                body = "This is a test!",
                payloadData = mapOf("action" to "test")
            )
        }
    }) {
        Text("Send Test Notification")
    }
}
```

## Troubleshooting

### Android: Notifications not showing

1. Check notification permission is granted
2. Verify `google-services.json` is in place
3. Check Android 13+ requires runtime permission
4. Verify notification icon is valid (white, transparent background)

### iOS: Permission dialog not appearing

1. Verify Push Notifications capability is enabled
2. Check `initialize()` was called before `requestPermission()`
3. Test on physical device (simulator may have limitations)

### FCM Token null

1. Verify Firebase project is configured correctly
2. Check `google-services.json` (Android) or `GoogleService-Info.plist` (iOS) is present
3. Ensure device has internet connectivity
4. Check Firebase Console for project configuration issues

## License

MIT License

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues, questions, or feature requests, please open an issue on GitHub.

## Links

- [GitHub Repository](https://github.com/erikg84/firebase-analytics)
- [KMPNotifier](https://github.com/mirzemehdi/KMPNotifier)
- [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging)
- [Koin Documentation](https://insert-koin.io/)

---

**Version:** 1.0.0
**Last Updated:** January 22, 2026
