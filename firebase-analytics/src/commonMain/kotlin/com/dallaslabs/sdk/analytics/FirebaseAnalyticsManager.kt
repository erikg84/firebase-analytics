package com.dallaslabs.sdk.analytics

import dev.gitlive.firebase.analytics.FirebaseAnalytics
import org.koin.core.annotation.Single

/**
 * Firebase implementation of [AnalyticsManager].
 */
@Single
public class FirebaseAnalyticsManager(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsManager {

    override suspend fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }

    override suspend fun trackScreenView(screen: AnalyticsScreen) {
        firebaseAnalytics.logEvent("screen_view") {
            param("screen_name", screen.name)
            screen.parameters.forEach { (key, value) ->
                when (value) {
                    is String -> param(key, value)
                    is Long -> param(key, value)
                    is Double -> param(key, value)
                    is Int -> param(key, value.toLong())
                    is Float -> param(key, value.toDouble())
                    else -> param(key, value.toString())
                }
            }
        }
    }

    override suspend fun trackEvent(name: String, parameters: Map<String, Any>) {
        firebaseAnalytics.logEvent(name) {
            parameters.forEach { (key, value) ->
                when (value) {
                    is String -> param(key, value)
                    is Long -> param(key, value)
                    is Double -> param(key, value)
                    is Int -> param(key, value.toLong())
                    is Float -> param(key, value.toDouble())
                    else -> param(key, value.toString())
                }
            }
        }
    }

    override suspend fun trackCtaClick(cta: AnalyticsCta) {
        firebaseAnalytics.logEvent("cta_click") {
            param("cta_name", cta.name)
            cta.parameters.forEach { (key, value) ->
                when (value) {
                    is String -> param(key, value)
                    is Long -> param(key, value)
                    is Double -> param(key, value)
                    is Int -> param(key, value.toLong())
                    is Float -> param(key, value.toDouble())
                    else -> param(key, value.toString())
                }
            }
        }
    }
}
