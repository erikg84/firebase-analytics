# Firebase Notifications SDK ProGuard Rules

# Keep all public API classes and methods
-keep public class com.dallaslabs.firebase.notifications.** {
    public protected *;
}

# Keep Koin annotations
-keep class org.koin.core.annotation.** { *; }
-keepattributes *Annotation*

# Keep KMPNotifier classes
-keep class com.mmk.kmpnotifier.** { *; }

# Keep Firebase Messaging classes (for FCM functionality)
-keep class com.google.firebase.messaging.** { *; }
-dontwarn com.google.firebase.messaging.**

# Keep data classes and sealed interfaces
-keepclassmembers class com.dallaslabs.firebase.notifications.model.** {
    *;
}

# Keep Kotlin metadata for reflection
-keep class kotlin.Metadata { *; }
-keepclassmembers class ** {
    @kotlin.Metadata *;
}

# Keep NotificationManager methods
-keepclassmembers class com.dallaslabs.firebase.notifications.NotificationManager {
    public <methods>;
}

# Keep coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**
