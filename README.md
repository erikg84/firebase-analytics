# Firebase Analytics SDK for Kotlin Multiplatform

A Kotlin Multiplatform (KMP) Firebase Analytics SDK for Android and iOS, built with Koin dependency injection and GitLive Firebase.

## Features

- 🔥 Firebase Analytics integration using GitLive Firebase KMP
- 💉 Dependency injection with Koin 4.1.1 and KSP annotations
- 📱 Support for Android (minSdk 24) and iOS (arm64, simulator)
- 🎯 Type-safe analytics tracking with sealed interfaces
- 🚀 Built with Kotlin 2.2 and Compose Multiplatform 1.9
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
        implementation("com.dallaslabs.sdk:firebase-analytics:1.0.0")
    }
}
```

## Setup

### Initialize Koin Module

In your application initialization:

```kotlin
import com.dallaslabs.firebase.analytics.FirebaseAnalyticsModule
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun initializeKoin() {
    startKoin {
        modules(FirebaseAnalyticsModule().module)
    }
}
```

### Inject AnalyticsManager

```kotlin
import com.dallaslabs.firebase.analytics.AnalyticsManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MyViewModel : KoinComponent {
    private val analytics: AnalyticsManager by inject()
    
    suspend fun trackUserAction() {
        analytics.trackEvent("user_action", mapOf("action" to "button_click"))
    }
}
```

## Usage

### Set User ID

```kotlin
analytics.setUserId("user123")
```

### Set User Property

```kotlin
analytics.setUserProperty("user_type", "premium")
```

### Track Screen View

```kotlin
// Using predefined screens
analytics.trackScreenView(AnalyticsScreen.Home.name, AnalyticsScreen.Home.screenClass)

// Or use the screen object directly with trackCtaClick
```

### Track Custom Event

```kotlin
analytics.trackEvent("purchase", mapOf(
    "item_id" to "SKU123",
    "price" to 19.99,
    "currency" to "USD"
))
```

### Track CTA Click

```kotlin
import com.dallaslabs.firebase.analytics.AnalyticsScreen
import com.dallaslabs.firebase.analytics.AnalyticsCta

analytics.trackCtaClick(
    cta = AnalyticsCta.SignIn,
    screen = AnalyticsScreen.Auth
)
```

## Predefined Screens

The SDK provides common screen types:

- `AnalyticsScreen.Home`
- `AnalyticsScreen.Auth`
- `AnalyticsScreen.Settings`
- `AnalyticsScreen.Profile`
- `AnalyticsScreen.Custom(name, screenClass, parameters)`

## Predefined CTAs

The SDK provides common CTA types:

- `AnalyticsCta.SignIn`
- `AnalyticsCta.SignUp`
- `AnalyticsCta.Submit`
- `AnalyticsCta.Cancel`
- `AnalyticsCta.Custom(name, parameters)`

## Testing

Use `NoOpAnalyticsManager` for testing or when analytics is disabled:

```kotlin
import com.dallaslabs.firebase.analytics.NoOpAnalyticsManager

val testAnalytics = NoOpAnalyticsManager()
// All methods are no-ops
```

## Architecture

The SDK follows clean architecture principles:

```
com.dallaslabs.firebase.analytics
├── AnalyticsManager (interface)
├── FirebaseAnalyticsManager (Firebase implementation)
├── NoOpAnalyticsManager (No-op implementation)
├── AnalyticsScreen (sealed interface)
├── AnalyticsCta (sealed interface)
└── FirebaseAnalyticsModule (Koin module)
```

## Requirements

- Kotlin 2.2.0
- Android minSdk 24
- iOS 12+
- Gradle 8.11

## Dependencies

- GitLive Firebase Analytics: 2.4.0
- Koin Core: 4.1.1
- Koin Annotations: 2.0.0
- Compose Multiplatform Runtime: 1.9.0

## ProGuard

ProGuard rules are included automatically. If you need to add custom rules, they are located in `proguard-rules.pro`.

## Publishing

The library is automatically published to GitHub Packages when a release tag is created:

```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

## License

Apache License 2.0

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
