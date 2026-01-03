package com.dallaslabs.sdk.analytics

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
     * Tracks a screen view event.
     *
     * @param screen The screen to track.
     */
    public suspend fun trackScreenView(screen: AnalyticsScreen)

    /**
     * Tracks a custom analytics event.
     *
     * @param name The name of the event.
     * @param parameters Optional parameters associated with the event.
     */
    public suspend fun trackEvent(name: String, parameters: Map<String, Any> = emptyMap())

    /**
     * Tracks a Call-to-Action (CTA) click event.
     *
     * @param cta The CTA that was clicked.
     */
    public suspend fun trackCtaClick(cta: AnalyticsCta)
}
