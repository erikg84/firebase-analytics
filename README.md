# Firebase SDK Collection for Kotlin Multiplatform

A collection of Kotlin Multiplatform (KMP) Firebase SDKs for Android and iOS, built with Koin dependency injection.

## Available SDKs

### 📊 firebase-analytics
Firebase Analytics integration using GitLive Firebase KMP

**Features:**
- Type-safe analytics tracking with sealed interfaces
- Screen view and CTA tracking
- User properties and custom events
- No-op implementation for testing

[View firebase-analytics documentation →](firebase-analytics/README.md)

### 🔔 firebase-notifications
Firebase Notifications SDK using KMPNotifier

**Features:**
- Unified notification API for Android and iOS
- Built-in permission handling
- Local notification support
- Firebase Cloud Messaging (FCM) integration
- Push token management

[View firebase-notifications documentation →](firebase-notifications/README.md)

## Quick Start

### Installation

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

### Add Dependencies

```kotlin
commonMain {
    dependencies {
        // Firebase Analytics
        implementation("com.dallaslabs.sdk:firebase-analytics:1.0.0")

        // Firebase Notifications
        implementation("com.dallaslabs.sdk:firebase-notifications:1.0.0")
    }
}
```

### Initialize Koin Modules

```kotlin
import com.dallaslabs.firebase.analytics.FirebaseAnalyticsModule
import com.dallaslabs.firebase.notifications.FirebaseNotificationsModule
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun initializeKoin() {
    startKoin {
        modules(
            FirebaseAnalyticsModule().module,
            FirebaseNotificationsModule().module
        )
    }
}
```

### Usage Example

```kotlin
@Composable
fun MyApp() {
    val analytics: AnalyticsTracker = koinInject()
    val notifications: NotificationManager = koinInject()

    LaunchedEffect(Unit) {
        // Initialize notifications
        notifications.initialize()

        // Request permission
        val granted = notifications.requestPermission()
        if (granted) {
            // Track analytics event
            analytics.trackScreen("home", "HomeScreen")

            // Send test notification
            notifications.sendLocalNotification(
                title = "Welcome!",
                body = "App notifications are enabled"
            )
        }
    }
}
```

## Repository Structure

```
firebase-sdk/
├── firebase-analytics/          # Analytics SDK
│   ├── src/commonMain/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── README.md
├── firebase-notifications/      # Notifications SDK
│   ├── src/commonMain/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── README.md
├── gradle/
│   └── libs.versions.toml       # Version catalog
├── .github/workflows/
│   └── publish.yml              # GitHub Actions publishing
├── build.gradle.kts
├── settings.gradle.kts
└── README.md                    # This file
```

## Common Features

All SDKs in this collection share:

- ✅ **Kotlin 2.2.20** - Latest Kotlin Multiplatform
- ✅ **Koin 4.1.1** - Dependency injection with KSP annotations
- ✅ **Android (minSdk 24)** - Wide device compatibility
- ✅ **iOS (arm64, simulator)** - Full iOS support
- ✅ **Explicit API Mode** - Type-safe, well-documented APIs
- ✅ **GitHub Packages** - Automated publishing
- ✅ **ProGuard Rules** - Optimized for release builds

## Requirements

- Kotlin 2.2.20
- Android minSdk 24
- iOS 12+
- Gradle 8.5+
- JDK 11+

## Development

### Local Testing

Publish to Maven Local for testing:

```bash
# Publish all modules
./gradlew publishToMavenLocal

# Publish specific module
./gradlew :firebase-analytics:publishToMavenLocal
./gradlew :firebase-notifications:publishToMavenLocal
```

Then use in your app:

```kotlin
repositories {
    mavenLocal()
}
```

### Building

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :firebase-analytics:build
./gradlew :firebase-notifications:build
```

## Publishing

The SDKs are automatically published to GitHub Packages when a release tag is created:

```bash
# Create and push a version tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

GitHub Actions will automatically:
1. Build all modules
2. Run tests
3. Publish to GitHub Packages

## Version Catalog

Dependencies are managed centrally in `gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "2.2.20"
koin = "4.1.1"
kmpnotifier = "1.6.1"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
kmpnotifier = { module = "io.github.mirzemehdi:kmpnotifier", version.ref = "kmpnotifier" }
```

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Run tests (`./gradlew test`)
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## License

MIT License

## Links

- [GitHub Repository](https://github.com/erikg84/firebase-analytics)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Koin Documentation](https://insert-koin.io/)

---

**Maintained by:** Erik G (@erikg84)
**Last Updated:** January 22, 2026
