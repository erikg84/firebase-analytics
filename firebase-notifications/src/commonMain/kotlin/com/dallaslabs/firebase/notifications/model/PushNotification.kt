package com.dallaslabs.firebase.notifications.model

/**
 * Represents a push notification message received from Firebase Cloud Messaging (FCM).
 *
 * This data class encapsulates all the information contained in a push notification,
 * including the visible content (title, body) and any custom data payload.
 *
 * @property title Notification title displayed to the user
 * @property body Notification body text displayed to the user
 * @property imageUrl Optional URL to an image to display in the notification (Android only)
 * @property data Custom key-value data payload sent with the notification
 * @property priority Notification priority level controlling display behavior
 */
public data class PushNotification(
    val title: String,
    val body: String,
    val imageUrl: String? = null,
    val data: Map<String, String> = emptyMap(),
    val priority: NotificationPriority = NotificationPriority.Default
)
