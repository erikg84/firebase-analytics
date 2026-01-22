package com.dallaslabs.firebase.notifications.model

/**
 * Configuration for an Android notification channel.
 *
 * Notification channels allow users to control notification behavior per category.
 * Required on Android 8.0 (API 26) and above.
 *
 * On iOS, channels are not supported and this configuration is ignored.
 *
 * @property id Unique identifier for this channel
 * @property name User-visible name displayed in notification settings
 * @property description User-visible description of the channel's purpose (optional)
 * @property importance Notification importance level
 * @property enableVibration Whether to vibrate for notifications in this channel
 * @property enableSound Whether to play sound for notifications in this channel
 */
public data class NotificationChannel(
    val id: String,
    val name: String,
    val description: String? = null,
    val importance: NotificationPriority = NotificationPriority.Default,
    val enableVibration: Boolean = true,
    val enableSound: Boolean = true
)
