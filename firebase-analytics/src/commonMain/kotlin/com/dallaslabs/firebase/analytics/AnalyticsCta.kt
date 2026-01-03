package com.dallaslabs.firebase.analytics

/**
 * Sealed interface representing a Call-to-Action (CTA) that can be tracked.
 */
public sealed interface AnalyticsCta {
    public val name: String
    public val parameters: Map<String, Any>
        get() = emptyMap()

    /**
     * Sign In CTA
     */
    public data object SignIn : AnalyticsCta {
        override val name: String = "sign_in"
    }

    /**
     * Sign Up CTA
     */
    public data object SignUp : AnalyticsCta {
        override val name: String = "sign_up"
    }

    /**
     * Submit CTA
     */
    public data object Submit : AnalyticsCta {
        override val name: String = "submit"
    }

    /**
     * Cancel CTA
     */
    public data object Cancel : AnalyticsCta {
        override val name: String = "cancel"
    }

    /**
     * Custom CTA with custom name and parameters
     */
    public data class Custom(
        override val name: String,
        override val parameters: Map<String, Any> = emptyMap()
    ) : AnalyticsCta
}
