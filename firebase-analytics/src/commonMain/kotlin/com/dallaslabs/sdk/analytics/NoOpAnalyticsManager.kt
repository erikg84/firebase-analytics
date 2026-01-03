package com.dallaslabs.sdk.analytics

import org.koin.core.annotation.Single

/**
 * No-op implementation of [AnalyticsManager] that does nothing.
 * Useful for testing or when analytics is disabled.
 */
@Single
public class NoOpAnalyticsManager : AnalyticsManager {
    override suspend fun setUserId(userId: String?) {
        // No-op
    }

    override suspend fun trackScreenView(screen: AnalyticsScreen) {
        // No-op
    }

    override suspend fun trackEvent(name: String, parameters: Map<String, Any>) {
        // No-op
    }

    override suspend fun trackCtaClick(cta: AnalyticsCta) {
        // No-op
    }
}
