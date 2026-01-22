package com.dallaslabs.firebase.notifications

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin module for Firebase Notifications SDK.
 *
 * Provides dependency injection configuration for notification functionality.
 * Uses Koin KSP annotations to automatically generate module definitions.
 *
 * ## Provided Dependencies
 *
 * - [NotificationManager] - Main notification API (singleton)
 *
 * ## Usage
 *
 * Initialize Koin with this module in your application setup:
 *
 * ```kotlin
 * import com.dallaslabs.firebase.notifications.FirebaseNotificationsModule
 * import org.koin.core.context.startKoin
 * import org.koin.ksp.generated.module
 *
 * fun initializeKoin() {
 *     startKoin {
 *         modules(
 *             FirebaseNotificationsModule().module,
 *             // ... other modules
 *         )
 *     }
 * }
 * ```
 *
 * Then inject [NotificationManager] anywhere you need it:
 *
 * ```kotlin
 * @Composable
 * fun MyScreen() {
 *     val notificationManager: NotificationManager = koinInject()
 *
 *     LaunchedEffect(Unit) {
 *         notificationManager.initialize()
 *     }
 * }
 * ```
 *
 * Or in a ViewModel:
 *
 * ```kotlin
 * class MyViewModel(
 *     private val notificationManager: NotificationManager
 * ) : ViewModel() {
 *     suspend fun requestNotifications() {
 *         notificationManager.requestPermission()
 *     }
 * }
 * ```
 */
@Module
@ComponentScan("com.dallaslabs.firebase.notifications")
public class FirebaseNotificationsModule
