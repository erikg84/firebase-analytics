package com.dallaslabs.firebase.analytics

/**
 * Interface for managing analytics tracking operations.
 */
public interface AnalyticsManager {
    /**
     * Sets the user ID for analytics tracking.
     *
     * @param userId The unique identifier for the user, or null to clear the user ID.
     */
    public suspend fun setUserId(userId: String?)

    /**
     * Sets a user property for analytics.
     *
     * @param name The name of the user property.
     * @param value The value of the user property, or null to clear it.
     */
    public suspend fun setUserProperty(name: String, value: String?)

    /**
     * Tracks a screen view event.
     *
     * @param screenName The name of the screen being viewed.
     * @param screenClass The class of the screen (optional).
     */
    public suspend fun trackScreenView(screenName: String, screenClass: String? = null)

    /**
     * Tracks a custom analytics event.
     *
     * @param eventName The name of the event.
     * @param params Optional parameters associated with the event.
     */
    public suspend fun trackEvent(eventName: String, params: Map<String, Any>? = null)

    /**
     * Tracks a Call-to-Action (CTA) click event.
     *
     * @param cta The CTA that was clicked.
     * @param screen The screen where the CTA was clicked.
     */
    public suspend fun trackCtaClick(cta: AnalyticsCta, screen: AnalyticsScreen)
}
