package com.dallaslabs.firebase.notifications

/**
 * Configuration for the Firebase Notifications SDK.
 *
 * @property enabled Master switch to enable/disable all notification functionality
 * @property showPushNotifications Whether to automatically display incoming FCM push notifications
 * @property notificationIconResId Android notification icon resource ID (optional, uses app icon if null)
 */
public data class NotificationConfig(
    val enabled: Boolean = true,
    val showPushNotifications: Boolean = true,
    val notificationIconResId: Int? = null
)
