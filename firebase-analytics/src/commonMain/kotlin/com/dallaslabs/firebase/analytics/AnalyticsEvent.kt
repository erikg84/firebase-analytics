package com.dallaslabs.firebase.analytics

public sealed class AnalyticsEvent {
    public abstract val eventName: String
    public abstract val parameters: Map<String, Any>

    public data class ScreenView(
        val screenName: String,
        val screenClass: String? = null
    ) : AnalyticsEvent() {
        override val eventName: String = "screen_view"
        override val parameters: Map<String, Any>
            get() = buildMap {
                put("screen_name", screenName)
                screenClass?.let { put("screen_class", it) }
            }
    }

    public data class Search(
        val searchTerm: String,
        val resultsCount: Int? = null
    ) : AnalyticsEvent() {
        override val eventName: String = "search"
        override val parameters: Map<String, Any>
            get() = buildMap {
                put("search_term", searchTerm)
                resultsCount?.let { put("results_count", it) }
            }
    }

    public data class Login(val method: String) : AnalyticsEvent() {
        override val eventName: String = "login"
        override val parameters: Map<String, Any> = mapOf("method" to method)
    }

    public data class SignUp(val method: String) : AnalyticsEvent() {
        override val eventName: String = "sign_up"
        override val parameters: Map<String, Any> = mapOf("method" to method)
    }

    public data class SelectContent(
        val contentType: String,
        val itemId: String,
        val itemName: String? = null
    ) : AnalyticsEvent() {
        override val eventName: String = "select_content"
        override val parameters: Map<String, Any>
            get() = buildMap {
                put("content_type", contentType)
                put("item_id", itemId)
                itemName?.let { put("item_name", it) }
            }
    }

    public data class SelectItem(
        val itemId: String,
        val itemName: String? = null,
        val itemCategory: String? = null
    ) : AnalyticsEvent() {
        override val eventName: String = "select_item"
        override val parameters: Map<String, Any>
            get() = buildMap {
                put("item_id", itemId)
                itemName?.let { put("item_name", it) }
                itemCategory?.let { put("item_category", it) }
            }
    }

    public data class ViewItem(
        val itemId: String,
        val itemName: String? = null,
        val itemCategory: String? = null
    ) : AnalyticsEvent() {
        override val eventName: String = "view_item"
        override val parameters: Map<String, Any>
            get() = buildMap {
                put("item_id", itemId)
                itemName?.let { put("item_name", it) }
                itemCategory?.let { put("item_category", it) }
            }
    }

    public data class Share(
        val contentType: String,
        val itemId: String,
        val method: String
    ) : AnalyticsEvent() {
        override val eventName: String = "share"
        override val parameters: Map<String, Any> = mapOf(
            "content_type" to contentType,
            "item_id" to itemId,
            "method" to method
        )
    }

    public data class ButtonClick(
        val buttonName: String,
        val screenName: String,
        val additionalParams: Map<String, String> = emptyMap()
    ) : AnalyticsEvent() {
        override val eventName: String = "cta_click"
        override val parameters: Map<String, Any>
            get() = buildMap {
                put("cta_name", buttonName)
                put("screen_name", screenName)
                additionalParams.forEach { (key, value) -> put(key, value) }
            }
    }

    public data class Error(
        val errorCode: String,
        val errorMessage: String,
        val screenName: String? = null
    ) : AnalyticsEvent() {
        override val eventName: String = "app_error"
        override val parameters: Map<String, Any>
            get() = buildMap {
                put("error_code", errorCode)
                put("error_message", errorMessage)
                screenName?.let { put("screen_name", it) }
            }
    }

    public data object TutorialBegin : AnalyticsEvent() {
        override val eventName: String = "tutorial_begin"
        override val parameters: Map<String, Any> = emptyMap()
    }

    public data object TutorialComplete : AnalyticsEvent() {
        override val eventName: String = "tutorial_complete"
        override val parameters: Map<String, Any> = emptyMap()
    }

    public data object AppOpen : AnalyticsEvent() {
        override val eventName: String = "app_open"
        override val parameters: Map<String, Any> = emptyMap()
    }

    public data class Custom(
        override val eventName: String,
        override val parameters: Map<String, Any> = emptyMap()
    ) : AnalyticsEvent()
}
