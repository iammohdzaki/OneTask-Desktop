# Compose Multiplatform basic rules
-keepattributes Signature,Annotation*
-dontwarn androidx.compose.**
-dontwarn org.jetbrains.compose.**

# SQLDelight rules
-keep class com.one.task.data.db.** { *; }
-keep interface app.cash.sqldelight.** { *; }

# SQLite JDBC Driver
-keep class org.sqlite.** { *; }
-keep class * implements java.sql.Driver { *; }

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

# OkHttp/Coil warnings & rules
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okio.** { *; }
-keep class okhttp3.** { *; }
-keep class coil3.** { *; }
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# General
-dontnote **
-ignorewarnings
