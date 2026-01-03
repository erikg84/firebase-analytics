package com.dallaslabs.firebase.analytics

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

    override suspend fun setUserProperty(name: String, value: String?) {
        // No-op
    }

    override suspend fun trackScreenView(screenName: String, screenClass: String?) {
        // No-op
    }

    override suspend fun trackEvent(eventName: String, params: Map<String, Any>?) {
        // No-op
    }

    override suspend fun trackCtaClick(cta: AnalyticsCta, screen: AnalyticsScreen) {
        // No-op
    }
}
