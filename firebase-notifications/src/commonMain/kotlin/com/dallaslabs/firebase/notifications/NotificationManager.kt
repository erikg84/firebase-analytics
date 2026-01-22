package com.dallaslabs.firebase.notifications

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single
import kotlin.random.Random

/**
 * Main API for Firebase Notifications SDK.
 *
 * Provides a unified interface for notification functionality across Android and iOS platforms.
 * Wraps the KMPNotifier library with a clean, type-safe API.
 *
 * Features:
 * - Notification permission handling
 * - Local notification display
 * - Firebase Cloud Messaging (FCM) token management
 * - Push notification reception
 * - Notification click handling
 *
 * ## Usage
 *
 * ```kotlin
 * @Composable
 * fun MyApp() {
 *     val notificationManager: NotificationManager = koinInject()
 *
 *     LaunchedEffect(Unit) {
 *         // Initialize notification system
 *         notificationManager.initialize()
 *
 *         // Request permission
 *         val granted = notificationManager.requestPermission()
 *         if (granted) {
 *             // Get FCM token
 *             val token = notificationManager.getPushToken()
 *             println("FCM Token: $token")
 *         }
 *     }
 * }
 * ```
 *
 * @property config Configuration for the notification system
 */
@Single
public class NotificationManager(
    private val config: NotificationConfig = NotificationConfig()
) {
    private val localNotifier = NotifierManager.getLocalNotifier()
    private val pushNotifier = NotifierManager.getPushNotifier()

    private val _tokenFlow = MutableStateFlow<String?>(null)
    public val tokenFlow: StateFlow<String?> = _tokenFlow.asStateFlow()

    init {
        // Listen for token updates and notification clicks
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                _tokenFlow.value = token
            }

            override fun onNotificationClicked(data: PayloadData) {
                notificationClickCallback?.invoke(data)
            }
        })
    }

    private var notificationClickCallback: ((PayloadData) -> Unit)? = null

    /**
     * Initializes the notification system with platform-specific configuration.
     *
     * This method must be called before using any other notification functionality.
     * It configures the underlying notification platform with settings from [config].
     *
     * Note: Permission handling should be done separately using KMPNotifier's permissionUtil
     * in your UI code. See README for examples.
     *
     * @throws IllegalStateException if initialization fails
     */
    public fun initialize() {
        if (!config.enabled) return

        NotifierManager.initialize(
            NotificationPlatformConfiguration.Android(
                notificationIconResId = config.notificationIconResId ?: 0,
                showPushNotification = config.showPushNotifications
            )
        )
    }

    /**
     * Gets the Firebase Cloud Messaging (FCM) token for this device.
     *
     * The FCM token uniquely identifies this app installation and is used to send
     * targeted push notifications from your backend server.
     *
     * @return The FCM token string, or null if unavailable
     */
    public suspend fun getPushToken(): String? {
        if (!config.enabled) return null
        return pushNotifier.getToken()
    }

    /**
     * Displays a local notification to the user.
     *
     * Local notifications are shown immediately by the app without requiring
     * a backend server or internet connection.
     *
     * @param title The notification title
     * @param body The notification body text
     * @param notificationId Unique identifier for this notification (auto-generated if not provided)
     * @param payloadData Custom key-value data to include with the notification (retrievable on click)
     */
    public fun sendLocalNotification(
        title: String,
        body: String,
        notificationId: Int = generateNotificationId(),
        payloadData: Map<String, String> = emptyMap()
    ) {
        if (!config.enabled) return

        localNotifier.notify {
            id = notificationId
            this.title = title
            this.body = body
            this.payloadData = payloadData
        }
    }

    /**
     * Registers a callback to handle notification click events.
     *
     * The callback receives a PayloadData object containing the notification's payload data.
     *
     * Example:
     * ```kotlin
     * notificationManager.onNotificationClicked { payload ->
     *     val itemId = payload["itemId"]
     *     // Navigate to item detail screen
     * }
     * ```
     *
     * @param callback Function to call when a notification is clicked, receives payload data
     */
    public fun onNotificationClicked(callback: (PayloadData) -> Unit) {
        notificationClickCallback = callback
    }

    /**
     * Cancels a specific notification by its ID.
     *
     * Removes the notification from the system notification tray if it's currently displayed.
     *
     * @param notificationId The ID of the notification to cancel
     */
    public fun cancelNotification(notificationId: Int) {
        localNotifier.remove(notificationId)
    }

    /**
     * Cancels all notifications posted by this app.
     *
     * Clears all notifications from the system notification tray.
     */
    public fun cancelAllNotifications() {
        localNotifier.removeAll()
    }

    /**
     * Generates a unique notification ID based on random number generation.
     *
     * @return A random integer for use as notification ID
     */
    private fun generateNotificationId(): Int {
        return Random.nextInt(0, Int.MAX_VALUE)
    }
}
