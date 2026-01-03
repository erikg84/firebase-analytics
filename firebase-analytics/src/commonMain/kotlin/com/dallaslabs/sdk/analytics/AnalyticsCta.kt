package com.dallaslabs.sdk.analytics

/**
 * Sealed interface representing a Call-to-Action (CTA) that can be tracked.
 */
public sealed interface AnalyticsCta {
    public val name: String
    public val parameters: Map<String, Any>
        get() = emptyMap()
}
