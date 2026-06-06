# Compose Multiplatform basic rules
-keepattributes Signature,Annotation*
-dontwarn androidx.compose.**
-dontwarn org.jetbrains.compose.**

# SQLDelight rules
-keep class com.one.task.data.db.** { *; }
-keep interface app.cash.sqldelight.** { *; }

# Koin rules
-keep class org.koin.** { *; }

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** {
    @kotlinx.serialization.SerialName <fields>;
}

# Navigation & Common Android library warnings in JVM
-dontwarn androidx.navigation.**
-dontwarn androidx.lifecycle.**
-dontwarn androidx.savedstate.**
-dontwarn androidx.core.**
-dontwarn android.os.**
-dontwarn android.util.**
-dontwarn android.security.**
-dontwarn android.net.**

# OkHttp/Coil warnings
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# General
-dontnote **
-ignorewarnings
