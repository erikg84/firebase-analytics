package com.dallaslabs.firebase.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.FirebaseAnalytics
import dev.gitlive.firebase.analytics.analytics
import org.koin.core.annotation.Single

@Single
public class AnalyticsTracker(
    private val config: AnalyticsConfig = AnalyticsConfig()
) {
    private val analytics: FirebaseAnalytics = Firebase.analytics

    public suspend fun trackScreen(screenName: String, screenClass: String? = null) {
        if (!config.enabled) return

        val params = mutableMapOf<String, Any>(
            "screen_name" to screenName
        )
        screenClass?.let { params["screen_class"] = it }
        analytics.logEvent("screen_view", params)
    }

    public suspend fun trackCtaClick(
        ctaName: String,
        screenName: String,
        additionalParams: Map<String, String> = emptyMap()
    ) {
        if (!config.enabled) return

        val params = mutableMapOf<String, Any>(
            "cta_name" to ctaName,
            "screen_name" to screenName
        )
        additionalParams.forEach { (key, value) -> params[key] = value }
        analytics.logEvent("cta_click", params)
    }

    public suspend fun trackSearch(query: String, resultsCount: Int) {
        if (!config.enabled) return

        analytics.logEvent("search", mapOf(
            "search_term" to query,
            "results_count" to resultsCount
        ))
    }

    public suspend fun trackCustomEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
        if (!config.enabled) return
        analytics.logEvent(eventName, params)
    }

    public suspend fun trackEvent(event: AnalyticsEvent) {
        if (!config.enabled) return
        analytics.logEvent(event.eventName, event.parameters)
    }

    public suspend fun trackLogin(method: String) {
        if (!config.enabled) return
        analytics.logEvent("login", mapOf("method" to method))
    }

    public suspend fun trackLogout() {
        if (!config.enabled) return
        analytics.logEvent("logout")
    }

    public suspend fun trackSignUp(method: String) {
        if (!config.enabled) return
        analytics.logEvent("sign_up", mapOf("method" to method))
    }

    public suspend fun trackSelectContent(
        contentType: String,
        itemId: String,
        itemName: String? = null
    ) {
        if (!config.enabled) return

        val params = mutableMapOf<String, Any>(
            "content_type" to contentType,
            "item_id" to itemId
        )
        itemName?.let { params["item_name"] = it }
        analytics.logEvent("select_content", params)
    }

    public suspend fun trackShare(contentType: String, itemId: String, method: String) {
        if (!config.enabled) return

        analytics.logEvent("share", mapOf(
            "content_type" to contentType,
            "item_id" to itemId,
            "method" to method
        ))
    }

    public fun setUserId(userId: String?) {
        analytics.setUserId(userId)
    }

    public suspend fun setUserProperty(name: String, value: String) {
        analytics.setUserProperty(name, value)
    }

    public suspend fun setAnalyticsCollectionEnabled(enabled: Boolean) {
        analytics.setAnalyticsCollectionEnabled(enabled)
    }

    public suspend fun resetAnalyticsData() {
        analytics.resetAnalyticsData()
    }
}
