package com.dallaslabs.sdk.analytics

/**
 * Sealed interface representing an analytics screen that can be tracked.
 */
public sealed interface AnalyticsScreen {
    public val name: String
    public val parameters: Map<String, Any>
        get() = emptyMap()
}
