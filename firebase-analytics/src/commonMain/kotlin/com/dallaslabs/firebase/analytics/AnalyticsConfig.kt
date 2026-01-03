package com.dallaslabs.firebase.analytics

import org.koin.core.annotation.Single

@Single
public data class AnalyticsConfig(
    val enabled: Boolean = true,
    val debugMode: Boolean = false
)

public class AnalyticsConfigBuilder {
    public var enabled: Boolean = true
    public var debugMode: Boolean = false

    public fun build(): AnalyticsConfig = AnalyticsConfig(
        enabled = enabled,
        debugMode = debugMode
    )
}

public fun analyticsConfig(block: AnalyticsConfigBuilder.() -> Unit): AnalyticsConfig {
    return AnalyticsConfigBuilder().apply(block).build()
}
