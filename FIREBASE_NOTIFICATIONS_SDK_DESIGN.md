# Firebase Notifications SDK - Architecture & Design Document

**Date:** January 22, 2026
**Purpose:** Design document for adding `firebase-notifications` module to the firebase-sdk repository using KMPNotifier library.

---

## Executive Summary

This document analyzes the existing firebase-sdk and supabase-sdk repositories to establish architectural patterns for creating a new `firebase-notifications` module. The module will wrap the KMPNotifier library to provide a unified, type-safe notification API for Android and iOS platforms.

**Key Decisions:**
- Use KMPNotifier as the underlying library (industry standard for KMP notifications)
- Follow firebase-analytics single-module pattern
- Use Koin annotations (@Single, @Module) matching firebase-analytics approach
- Publish to GitHub Packages at `com.dallaslabs.sdk`
- Support Firebase Cloud Messaging (FCM) integration
- Provide clean API for permissions, local notifications, and push notifications

---

## Repository Analysis

### 1. Firebase SDK Structure (firebase-analytics)

**Repository Type:** Single-module SDK repository
**Location:** `/Users/erikgutierrez/Documents/firebase-sdk`

#### Key Characteristics:

**Module Structure:**
```
firebase-sdk/
├── firebase-analytics/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       └── commonMain/kotlin/com/dallaslabs/firebase/analytics/
│           ├── AnalyticsTracker.kt         # Main API class
│           ├── AnalyticsConfig.kt          # Configuration
│           ├── AnalyticsEvent.kt           # Sealed interfaces
│           └── FirebaseAnalyticsModule.kt  # Koin module
├── build.gradle.kts                        # Root config
├── settings.gradle.kts
└── README.md
```

**Koin Integration Approach:**
- Uses Koin 4.1.1 with KSP annotations
- Classes annotated with `@Single`
- Module annotated with `@Module` and `@ComponentScan`
- Generated module accessed via `.module` extension
- Clean, annotation-driven approach

**Example:**
```kotlin
@Single
public class AnalyticsTracker(
    private val config: AnalyticsConfig = AnalyticsConfig()
) {
    private val analytics: FirebaseAnalytics = Firebase.analytics

    public suspend fun trackScreen(screenName: String) {
        if (!config.enabled) return
        analytics.logEvent("screen_view", mapOf("screen_name" to screenName))
    }
}

@Module
@ComponentScan("com.dallaslabs.firebase.analytics")
public class FirebaseAnalyticsModule
```

**Build Configuration:**
```kotlin
group = "com.dallaslabs.sdk"
version = findProperty("version")?.toString() ?: "1.0.0"

kotlin {
    explicitApi()
    androidTarget { publishLibraryVariants("release") }
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FirebaseAnalyticsSDK"
            isStatic = true
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspIosX64", libs.koin.ksp.compiler)
    add("kspIosArm64", libs.koin.ksp.compiler)
    add("kspIosSimulatorArm64", libs.koin.ksp.compiler)
}
```

**Publishing:**
- Direct Maven publishing configuration in module build.gradle.kts
- GitHub Packages at `https://maven.pkg.github.com/erikg84/firebase-analytics`
- Credentials via environment variables (GITHUB_ACTOR, GITHUB_TOKEN)

**Strengths:**
- ✅ Simple single-module structure
- ✅ Clean Koin annotation-driven DI
- ✅ Explicit API mode enforced
- ✅ Framework generation for iOS

**Weaknesses:**
- ⚠️ Uses GitLive Firebase (not official Firebase SDK)
- ⚠️ Publishing config duplicated if adding more modules

---

### 2. Supabase SDK Structure (Multi-Module Monorepo)

**Repository Type:** Multi-module monorepo
**Location:** `/Users/erikgutierrez/IdeaProjects/supabase-sdk`

#### Key Characteristics:

**Module Structure:**
```
supabase-sdk/
├── buildSrc/
│   └── src/main/kotlin/
│       └── BuildHelpers.kt                 # Shared publishing logic
├── supabase-core/                          # Foundation
│   └── src/commonMain/kotlin/com/dallaslabs/supabase/core/
├── supabase-db/                            # PostgREST wrapper
│   └── src/commonMain/kotlin/com/dallaslabs/supabase/db/
├── supabase-auth/                          # Auth wrapper
│   └── src/commonMain/kotlin/com/dallaslabs/supabase/auth/
├── supabase-koin/                          # DI integration
│   └── src/commonMain/kotlin/com/dallaslabs/supabase/koin/
└── supabase-auth-ui/                       # Compose UI components
```

**Dependency Graph:**
```
supabase-auth-ui
    ↓
supabase-auth
    ↓
supabase-core ← supabase-db
    ↓              ↓
supabase-koin ──────┘
```

**Koin Integration Approach:**
- Uses programmatic Koin DSL (no annotations)
- Dedicated `supabase-koin` module for DI
- Factory functions creating Koin modules
- More flexible, manual approach

**Example:**
```kotlin
// supabase-koin/SupabaseModule.kt
public fun supabaseModule(
    projectUrl: String,
    anonKey: String,
    authScheme: String? = null,
    authHost: String? = null
): Module = module {
    single<SupabaseCoreClient> {
        SupabaseCore.initialize {
            this.projectUrl = projectUrl
            this.anonKey = anonKey
        }
    }

    single<SupabaseAuth> { SupabaseAuth(get()) }
    single<SupabaseDatabase> { SupabaseDatabase(get()) }
}
```

**Build Configuration:**
- Shared publishing logic in `buildSrc/BuildHelpers.kt`
- Version catalog in `gradle/libs.versions.toml`
- Each module calls `configurePublishing("module-name")`
- Consistent across all modules

**Example:**
```kotlin
// In each module's build.gradle.kts
configurePublishing("supabase-core")

// BuildHelpers.kt
fun Project.configurePublishing(moduleName: String) {
    extensions.configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/erikg84/supabase-sdk")
                credentials {
                    username = findProperty("gpr.user")?.toString() ?: System.getenv("GITHUB_ACTOR")
                    password = findProperty("gpr.token")?.toString() ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}
```

**Strengths:**
- ✅ Excellent separation of concerns
- ✅ Shared build configuration reduces duplication
- ✅ Clear layered architecture
- ✅ Scalable for adding more modules

**Weaknesses:**
- ⚠️ More complex for single-feature SDKs
- ⚠️ Manual Koin module creation (more verbose)

---

## Comparison Summary

| Aspect | Firebase SDK | Supabase SDK |
|--------|-------------|--------------|
| **Structure** | Single module | Multi-module monorepo |
| **Koin Approach** | Annotations (@Single, @Module) | Programmatic DSL |
| **Build Config** | Per-module | Shared via buildSrc |
| **Complexity** | Simple, straightforward | More complex, scalable |
| **Publishing** | Direct in build.gradle.kts | Via BuildHelpers function |
| **Best For** | Single-purpose SDKs | Feature-rich SDK suites |
| **Namespace** | `com.dallaslabs.firebase.*` | `com.dallaslabs.supabase.*` |

**Recommendation for firebase-notifications:**
**Follow firebase-analytics pattern** - Single module with Koin annotations. The firebase-sdk repository is not a monorepo like supabase-sdk, and adding complex shared build infrastructure for just two modules would be over-engineering.

---

## KMPNotifier Overview

**Library:** [KMPNotifier by mirzemehdi](https://github.com/mirzemehdi/KMPNotifier)
**Version:** Latest stable (check before implementation)
**Platforms:** Android, iOS

### Key Features:
- ✅ **Unified Notification API** - Single API for both platforms
- ✅ **Permission Handling** - Built-in permission request flow
- ✅ **FCM Integration** - Firebase Cloud Messaging support
- ✅ **Local Notifications** - Schedule local notifications
- ✅ **Notification Clicks** - Handle notification tap events
- ✅ **Push Token Management** - FCM token retrieval and refresh
- ✅ **Compose Integration** - Works seamlessly with Compose Multiplatform

### Core Classes:
```kotlin
// From KMPNotifier library
interface NotifierManager {
    suspend fun initialize(configuration: NotificationPlatformConfiguration)
    suspend fun requestPermission(): Boolean
    suspend fun getToken(): String?
    suspend fun sendLocalNotification(notification: LocalNotification)
    fun onNotificationClicked(callback: (String) -> Unit)
}

data class LocalNotification(
    val title: String,
    val body: String,
    val notificationId: String,
    val payloadData: Map<String, String> = emptyMap()
)
```

---

## Firebase Notifications SDK Design

### Module Name: `firebase-notifications`

### Namespace: `com.dallaslabs.firebase.notifications`

### iOS Framework: `FirebaseNotificationsSDK`

---

## Architecture Design

### 1. Module Structure

```
firebase-notifications/
├── build.gradle.kts
├── proguard-rules.pro
└── src/
    ├── commonMain/kotlin/com/dallaslabs/firebase/notifications/
    │   ├── NotificationManager.kt              # Main API class
    │   ├── NotificationConfig.kt               # Configuration
    │   ├── model/
    │   │   ├── NotificationPriority.kt         # Sealed interface
    │   │   ├── NotificationChannel.kt          # Android channels
    │   │   ├── NotificationStyle.kt            # BigText, Inbox, etc.
    │   │   └── PushNotification.kt             # Data class
    │   ├── permission/
    │   │   └── NotificationPermissionHandler.kt # Permission wrapper
    │   ├── local/
    │   │   └── LocalNotificationScheduler.kt   # Local notifications
    │   ├── push/
    │   │   ├── PushTokenProvider.kt            # FCM token management
    │   │   └── PushNotificationReceiver.kt     # Push message handler
    │   └── FirebaseNotificationsModule.kt      # Koin module
    ├── androidMain/kotlin/com/dallaslabs/firebase/notifications/
    │   ├── NotificationChannelManager.kt       # Android-specific channels
    │   └── AndroidNotificationExtensions.kt    # Platform helpers
    └── iosMain/kotlin/com/dallaslabs/firebase/notifications/
        └── IOSNotificationExtensions.kt        # Platform helpers
```

### 2. Core API Design

#### NotificationManager (Main API)

```kotlin
package com.dallaslabs.firebase.notifications

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Single

@Single
public class NotificationManager(
    private val config: NotificationConfig = NotificationConfig()
) {
    private val notifierManager: NotifierManager = NotifierManager

    /**
     * Initialize notification system with configuration.
     * Must be called before any other methods.
     */
    public suspend fun initialize() {
        notifierManager.initialize(
            NotificationPlatformConfiguration(
                notificationIconResId = config.iconResId,
                showPushNotification = config.showPushNotifications
            )
        )
    }

    /**
     * Request notification permission from user.
     * @return true if granted, false otherwise
     */
    public suspend fun requestPermission(): Boolean {
        return notifierManager.requestPermission()
    }

    /**
     * Check if notification permission is granted.
     */
    public suspend fun isPermissionGranted(): Boolean {
        return notifierManager.isNotificationPermissionGranted()
    }

    /**
     * Get FCM push token for this device.
     * @return Token string or null if unavailable
     */
    public suspend fun getPushToken(): String? {
        return notifierManager.getToken()
    }

    /**
     * Observe FCM token changes (useful for token refresh).
     */
    public val tokenFlow: StateFlow<String?> = notifierManager.tokenFlow

    /**
     * Send a local notification.
     */
    public suspend fun sendLocalNotification(
        title: String,
        body: String,
        notificationId: String = generateNotificationId(),
        payloadData: Map<String, String> = emptyMap()
    ) {
        notifierManager.sendLocalNotification(
            LocalNotification(
                title = title,
                body = body,
                notificationId = notificationId,
                payloadData = payloadData
            )
        )
    }

    /**
     * Register callback for notification clicks.
     */
    public fun onNotificationClicked(callback: (String) -> Unit) {
        notifierManager.onNotificationClicked(callback)
    }

    /**
     * Cancel a notification by ID.
     */
    public fun cancelNotification(notificationId: String) {
        notifierManager.cancelNotification(notificationId)
    }

    /**
     * Cancel all notifications.
     */
    public fun cancelAllNotifications() {
        notifierManager.cancelAllNotifications()
    }

    private fun generateNotificationId(): String {
        return System.currentTimeMillis().toString()
    }
}
```

#### NotificationConfig

```kotlin
package com.dallaslabs.firebase.notifications

/**
 * Configuration for notification system.
 *
 * @property enabled Master switch for notifications
 * @property iconResId Android notification icon resource ID
 * @property showPushNotifications Whether to automatically display FCM push notifications
 * @property channelId Default Android notification channel ID
 * @property channelName Default Android notification channel name
 */
public data class NotificationConfig(
    val enabled: Boolean = true,
    val iconResId: Int? = null,
    val showPushNotifications: Boolean = true,
    val channelId: String = "default_channel",
    val channelName: String = "Default Notifications"
)
```

#### Model Classes

```kotlin
package com.dallaslabs.firebase.notifications.model

/**
 * Notification priority levels.
 */
public sealed interface NotificationPriority {
    public data object High : NotificationPriority
    public data object Default : NotificationPriority
    public data object Low : NotificationPriority
}

/**
 * Android notification channel configuration.
 */
public data class NotificationChannel(
    val id: String,
    val name: String,
    val description: String? = null,
    val importance: NotificationPriority = NotificationPriority.Default,
    val enableVibration: Boolean = true,
    val enableSound: Boolean = true
)

/**
 * Push notification data model.
 */
public data class PushNotification(
    val title: String,
    val body: String,
    val imageUrl: String? = null,
    val data: Map<String, String> = emptyMap(),
    val priority: NotificationPriority = NotificationPriority.Default
)
```

#### Koin Module

```kotlin
package com.dallaslabs.firebase.notifications

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin module for Firebase Notifications SDK.
 *
 * Provides:
 * - [NotificationManager] - Main notification API
 *
 * ## Usage
 * ```kotlin
 * import com.dallaslabs.firebase.notifications.FirebaseNotificationsModule
 * import org.koin.core.context.startKoin
 * import org.koin.ksp.generated.module
 *
 * fun initializeKoin() {
 *     startKoin {
 *         modules(FirebaseNotificationsModule().module)
 *     }
 * }
 * ```
 */
@Module
@ComponentScan("com.dallaslabs.firebase.notifications")
public class FirebaseNotificationsModule
```

---

## Build Configuration

### build.gradle.kts

```kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    id("maven-publish")
}

group = "com.dallaslabs.sdk"
version = findProperty("version")?.toString()?.takeIf { it != "unspecified" } ?: "1.0.0"

kotlin {
    explicitApi()

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishLibraryVariants("release")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FirebaseNotificationsSDK"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            // KMPNotifier - main dependency
            api("io.github.mirzemehdi:kmpnotifier:VERSION") // TODO: Check latest version

            // Koin
            api(libs.koin.core)
            api(libs.koin.annotations)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            // FCM for Android
            implementation("com.google.firebase:firebase-messaging:23.4.0")
        }

        iosMain.dependencies {
            // iOS notification dependencies (if any)
        }
    }
}

android {
    namespace = "com.dallaslabs.firebase.notifications"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspIosX64", libs.koin.ksp.compiler)
    add("kspIosArm64", libs.koin.ksp.compiler)
    add("kspIosSimulatorArm64", libs.koin.ksp.compiler)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_DEFAULT_MODULE", "false")
}

publishing {
    publications {
        withType<MavenPublication> {
            groupId = "com.dallaslabs.sdk"
            artifactId = when (name) {
                "kotlinMultiplatform" -> "firebase-notifications"
                else -> "firebase-notifications-${name.lowercase()}"
            }

            pom {
                name.set("Firebase Notifications SDK")
                description.set("Kotlin Multiplatform Firebase Notifications SDK using KMPNotifier")
                url.set("https://github.com/erikg84/firebase-analytics")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("erikg84")
                        name.set("Erik G")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/erikg84/firebase-analytics.git")
                    developerConnection.set("scm:git:ssh://github.com/erikg84/firebase-analytics.git")
                    url.set("https://github.com/erikg84/firebase-analytics")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/erikg84/firebase-analytics")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: findProperty("gpr.user")?.toString()
                password = System.getenv("GITHUB_TOKEN") ?: findProperty("gpr.token")?.toString()
            }
        }
    }
}
```

### Update settings.gradle.kts

```kotlin
rootProject.name = "firebase-sdk"

include(":firebase-analytics")
include(":firebase-notifications")  // ADD THIS
```

### Update gradle/libs.versions.toml

```toml
[versions]
kmpnotifier = "1.x.x"  # Check latest version
firebase-messaging = "23.4.0"

[libraries]
kmpnotifier = { module = "io.github.mirzemehdi:kmpnotifier", version.ref = "kmpnotifier" }
firebase-messaging = { module = "com.google.firebase:firebase-messaging", version.ref = "firebase-messaging" }
```

---

## Usage Guide (README.md Content)

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

Add the dependency to your module's `build.gradle.kts`:

```kotlin
commonMain {
    dependencies {
        implementation("com.dallaslabs.sdk:firebase-notifications:1.0.0")
    }
}
```

### Setup

#### 1. Initialize Koin Module

```kotlin
import com.dallaslabs.firebase.notifications.FirebaseNotificationsModule
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun initializeKoin() {
    startKoin {
        modules(
            FirebaseNotificationsModule().module
        )
    }
}
```

#### 2. Inject NotificationManager

```kotlin
import com.dallaslabs.firebase.notifications.NotificationManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MyViewModel : KoinComponent {
    private val notifications: NotificationManager by inject()

    suspend fun setupNotifications() {
        // Initialize
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

### Basic Usage

#### Request Permission

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

#### Send Local Notification

```kotlin
suspend fun sendNotification() {
    notifications.sendLocalNotification(
        title = "New Message",
        body = "You have a new message from John",
        payloadData = mapOf("messageId" to "12345")
    )
}
```

#### Handle Notification Clicks

```kotlin
fun setupNotificationClickHandler() {
    notifications.onNotificationClicked { payloadJson ->
        // Parse payload and navigate
        val data = Json.decodeFromString<Map<String, String>>(payloadJson)
        val messageId = data["messageId"]
        // Navigate to message screen
    }
}
```

#### Get FCM Token

```kotlin
suspend fun getToken() {
    val token = notifications.getPushToken()
    // Send token to your backend server
    sendTokenToServer(token)
}

// Or observe token changes
fun observeTokenChanges() {
    notifications.tokenFlow.collect { token ->
        token?.let { sendTokenToServer(it) }
    }
}
```

### Android Setup

#### 1. Add Firebase Configuration

Add `google-services.json` to your Android app:
```
app/
└── google-services.json
```

#### 2. Apply Google Services Plugin

In app's `build.gradle.kts`:
```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.0"
}
```

#### 3. Add Notification Icon (Optional)

Place notification icon in `res/drawable`:
```
res/
└── drawable/
    └── ic_notification.xml
```

Pass to config:
```kotlin
NotificationConfig(
    iconResId = R.drawable.ic_notification
)
```

### iOS Setup

#### 1. Enable Push Notifications Capability

In Xcode:
1. Select target → Signing & Capabilities
2. Click "+ Capability"
3. Add "Push Notifications"

#### 2. Add Background Modes (Optional)

For background notification handling:
```yaml
# project.yml
capabilities:
  Push Notifications: {}
  Background Modes:
    - remote-notification
```

#### 3. Upload APNs Certificate

1. Generate certificate in Apple Developer Portal
2. Upload to Firebase Console → Project Settings → Cloud Messaging

---

## Implementation Roadmap

### Phase 1: Basic Setup ✅
- [ ] Create `firebase-notifications` module directory
- [ ] Add module to `settings.gradle.kts`
- [ ] Create `build.gradle.kts` with KMPNotifier dependency
- [ ] Add Koin KSP configuration
- [ ] Create basic package structure

### Phase 2: Core API 🚧
- [ ] Implement `NotificationManager` class with @Single
- [ ] Create `NotificationConfig` data class
- [ ] Define model classes (NotificationPriority, NotificationChannel, etc.)
- [ ] Implement `FirebaseNotificationsModule` with @Module

### Phase 3: Permission Handling 📋
- [ ] Wrap KMPNotifier permission APIs
- [ ] Test permission flow on Android 13+
- [ ] Test permission flow on iOS
- [ ] Add permission status checking

### Phase 4: Local Notifications 📋
- [ ] Implement local notification sending
- [ ] Add notification click handling
- [ ] Test notification display on both platforms
- [ ] Add notification cancellation

### Phase 5: Push Notifications (FCM) 📋
- [ ] Integrate FCM token retrieval
- [ ] Add token refresh flow
- [ ] Test push notification reception
- [ ] Implement custom notification handling

### Phase 6: Android-Specific Features 📋
- [ ] Create NotificationChannelManager
- [ ] Add notification channel creation
- [ ] Support notification styles (BigText, Inbox)
- [ ] Add custom notification actions

### Phase 7: Testing & Documentation 📋
- [ ] Create sample app using the SDK
- [ ] Write comprehensive README.md
- [ ] Add KDoc comments to all public APIs
- [ ] Test on physical devices (Android & iOS)

### Phase 8: Publishing 📋
- [ ] Configure GitHub Packages publishing
- [ ] Test local Maven publishing
- [ ] Create GitHub release workflow
- [ ] Publish v1.0.0

---

## Testing Strategy

### Unit Tests
```kotlin
class NotificationManagerTest {
    @Test
    fun `test permission request returns boolean`() = runTest {
        // Mock KMPNotifier
        // Test permission flow
    }

    @Test
    fun `test local notification with valid data`() = runTest {
        // Test notification creation
    }
}
```

### Integration Tests
- Test in QuakeAlert app (existing DollarMonoRepo app)
- Verify notifications display correctly
- Test permission flow UX
- Verify FCM token retrieval

### Platform-Specific Tests
**Android:**
- Test on Android 12 and below (no permission)
- Test on Android 13+ (runtime permission)
- Test notification channels
- Test foreground/background reception

**iOS:**
- Test permission dialog
- Test notification display
- Test background notification handling
- Test APNs token retrieval

---

## Migration Path from Current Implementation

### Current State (in QuakeAlert)
- Manual expect/actual wrappers for permissions
- Accompanist Permissions on Android
- UNUserNotificationCenter on iOS
- No push notification support

### Migration Steps

1. **Add firebase-notifications dependency** to QuakeAlert:
```kotlin
commonMain.dependencies {
    implementation("com.dallaslabs.sdk:firebase-notifications:1.0.0")
}
```

2. **Initialize Koin module**:
```kotlin
startKoin {
    modules(
        FirebaseNotificationsModule().module,
        // ... other modules
    )
}
```

3. **Replace manual permission handling**:
```kotlin
// BEFORE
val notificationPermissionState = rememberNotificationPermissionState()

// AFTER
val notifications: NotificationManager = koinInject()
LaunchedEffect(Unit) {
    notifications.initialize()
}
```

4. **Update AlertsScreen logic**:
```kotlin
// Request permission
onToggle = { enabled ->
    if (enabled) {
        scope.launch {
            val granted = notifications.requestPermission()
            if (granted) {
                onAction(AlertsIntent.SetNotificationsEnabled(true))
            }
        }
    }
}
```

5. **Replace WorkManager notifications**:
```kotlin
// In EarthquakeMonitorWorker
class EarthquakeMonitorWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val notifications: NotificationManager by inject()

    override suspend fun doWork(): Result {
        // ... fetch earthquakes

        // Show notification using SDK
        notifications.sendLocalNotification(
            title = "Earthquake Alert",
            body = "M${earthquake.magnitude} - ${earthquake.place}",
            payloadData = mapOf("earthquakeId" to earthquake.id)
        )

        return Result.success()
    }
}
```

6. **Remove old implementations**:
- Delete `shared/permissions/NotificationPermissionState.kt`
- Delete `shared/permissions/PermissionHelper.ios.kt`
- Remove Accompanist Permissions dependency

---

## Benefits of This Approach

### For DollarMonoRepo Apps
✅ **Unified API** - Same code for Android and iOS notifications
✅ **Less Boilerplate** - No more expect/actual wrappers
✅ **FCM Support** - Ready for push notifications
✅ **Tested & Maintained** - KMPNotifier is industry standard
✅ **Reusable** - Use in all 21 apps via single dependency

### For SDK Development
✅ **Clean Architecture** - Follows firebase-analytics patterns
✅ **Type-Safe** - Sealed interfaces and data classes
✅ **Explicit API** - All public APIs clearly defined
✅ **Publishable** - Ready for GitHub Packages or Maven Central
✅ **Documentable** - Clear documentation and examples

### For Future Development
✅ **Extensible** - Easy to add notification channels, styles, etc.
✅ **Testable** - Clear interfaces for mocking and testing
✅ **Scalable** - Can grow into notification-ui module if needed

---

## Next Steps

1. **Review this document** - Ensure architecture aligns with requirements
2. **Check KMPNotifier version** - Get latest stable release
3. **Create module structure** - Set up files and directories
4. **Implement Phase 1-2** - Basic setup and core API
5. **Test in QuakeAlert** - Integrate and verify functionality
6. **Iterate** - Refine API based on usage experience

---

## Questions to Resolve Before Implementation

1. **KMPNotifier Version**: What's the latest stable version?
2. **FCM Setup**: Do we need Firebase BOM in dependencies?
3. **Icon Resources**: How to handle notification icons in shared code?
4. **Testing**: Should we create a dedicated sample app or test in QuakeAlert?
5. **Versioning**: Start at 1.0.0 or align with firebase-analytics version?

---

## References

- **KMPNotifier GitHub**: https://github.com/mirzemehdi/KMPNotifier
- **Firebase Cloud Messaging**: https://firebase.google.com/docs/cloud-messaging
- **Koin Documentation**: https://insert-koin.io/docs/reference/koin-core/modules
- **Firebase SDK Repository**: `/Users/erikgutierrez/Documents/firebase-sdk`
- **Supabase SDK Repository**: `/Users/erikgutierrez/IdeaProjects/supabase-sdk`

---

**Document Status:** ✅ Ready for Review
**Author:** Claude (claude-sonnet-4-5-20250929)
**Last Updated:** January 22, 2026
