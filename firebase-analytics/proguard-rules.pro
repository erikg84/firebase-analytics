# Firebase Analytics SDK ProGuard Rules

# Keep all Firebase Analytics classes
-keep class com.dallaslabs.firebase.analytics.** { *; }

# Keep Koin annotations
-keep class org.koin.core.annotation.** { *; }

# Keep Firebase Analytics from GitLive
-keep class dev.gitlive.firebase.analytics.** { *; }

# Keep parameter names for better debugging
-keepparameternames

# Keep attributes for better stack traces
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keepattributes SourceFile,LineNumberTable

# Keep sealed interfaces and their implementations
-keep interface com.dallaslabs.firebase.analytics.AnalyticsScreen
-keep interface com.dallaslabs.firebase.analytics.AnalyticsCta
-keep class * implements com.dallaslabs.firebase.analytics.AnalyticsScreen { *; }
-keep class * implements com.dallaslabs.firebase.analytics.AnalyticsCta { *; }
