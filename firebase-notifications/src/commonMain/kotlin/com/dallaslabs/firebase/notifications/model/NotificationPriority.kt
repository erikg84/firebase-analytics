package com.dallaslabs.firebase.notifications.model

/**
 * Notification priority levels for controlling how prominently notifications are displayed.
 *
 * On Android:
 * - [High]: Appears as heads-up notification with sound/vibration
 * - [Default]: Standard notification behavior
 * - [Low]: Appears in notification shade without sound/vibration
 *
 * On iOS:
 * - All priorities trigger standard notification behavior
 * - Priority mainly affects Android behavior
 */
public sealed interface NotificationPriority {
    /**
     * High priority - shows as heads-up notification on Android.
     * Use for time-sensitive or important notifications.
     */
    public data object High : NotificationPriority

    /**
     * Default priority - standard notification behavior.
     * Recommended for most notifications.
     */
    public data object Default : NotificationPriority

    /**
     * Low priority - minimal interruption on Android.
     * Use for informational updates that aren't urgent.
     */
    public data object Low : NotificationPriority
}
