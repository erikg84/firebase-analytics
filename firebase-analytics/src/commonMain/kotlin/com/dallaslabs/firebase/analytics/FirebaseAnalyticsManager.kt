package com.dallaslabs.firebase.analytics

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

    override suspend fun setUserProperty(name: String, value: String?) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    override suspend fun trackScreenView(screenName: String, screenClass: String?) {
        firebaseAnalytics.logEvent("screen_view") {
            param("screen_name", screenName)
            screenClass?.let { param("screen_class", it) }
        }
    }

    override suspend fun trackEvent(eventName: String, params: Map<String, Any>?) {
        firebaseAnalytics.logEvent(eventName) {
            params?.forEach { (key, value) ->
                when (value) {
                    is String -> param(key, value)
                    is Long -> param(key, value)
                    is Double -> param(key, value)
                    is Int -> param(key, value.toLong())
                    is Float -> param(key, value.toDouble())
                    is Boolean -> param(key, if (value) 1L else 0L)
                    else -> param(key, value.toString())
                }
            }
        }
    }

    override suspend fun trackCtaClick(cta: AnalyticsCta, screen: AnalyticsScreen) {
        firebaseAnalytics.logEvent("cta_click") {
            param("cta_name", cta.name)
            param("screen_name", screen.name)
            screen.screenClass?.let { param("screen_class", it) }
            
            cta.parameters.forEach { (key, value) ->
                when (value) {
                    is String -> param(key, value)
                    is Long -> param(key, value)
                    is Double -> param(key, value)
                    is Int -> param(key, value.toLong())
                    is Float -> param(key, value.toDouble())
                    is Boolean -> param(key, if (value) 1L else 0L)
                    else -> param(key, value.toString())
                }
            }
        }
    }
}
