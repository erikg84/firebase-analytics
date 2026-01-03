package com.dallaslabs.firebase.analytics

/**
 * Sealed interface representing an analytics screen that can be tracked.
 */
public sealed interface AnalyticsScreen {
    public val name: String
    public val screenClass: String?
        get() = null
    public val parameters: Map<String, Any>
        get() = emptyMap()

    /**
     * Home screen
     */
    public data object Home : AnalyticsScreen {
        override val name: String = "home"
        override val screenClass: String = "HomeScreen"
    }

    /**
     * Authentication screen
     */
    public data object Auth : AnalyticsScreen {
        override val name: String = "auth"
        override val screenClass: String = "AuthScreen"
    }

    /**
     * Settings screen
     */
    public data object Settings : AnalyticsScreen {
        override val name: String = "settings"
        override val screenClass: String = "SettingsScreen"
    }

    /**
     * Profile screen
     */
    public data object Profile : AnalyticsScreen {
        override val name: String = "profile"
        override val screenClass: String = "ProfileScreen"
    }

    /**
     * Custom screen with custom name and parameters
     */
    public data class Custom(
        override val name: String,
        override val screenClass: String? = null,
        override val parameters: Map<String, Any> = emptyMap()
    ) : AnalyticsScreen
}
