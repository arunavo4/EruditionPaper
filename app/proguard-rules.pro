# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#Crashlytics
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
#To let Crashlytics automatically upload the ProGuard or DexGuard mapping file
-printmapping mapping.txt
#Exclude Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

#RenderScript
-keep class android.support.v8.renderscript.** { *; }

#Retrofit
-keep class retrofit2.** { *; }
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontnote retrofit2.Platform
-dontwarn retrofit2.** # Also keeps Twitter at bay as long as they keep using Retrofit
-dontnote retrofit2.**

#okhttp
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

#okhttp3 extra
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontnote okhttp3.**

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

#Gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

#To Safely enable code obfuscation for Gson
-keepclassmembers class com.codepath.models** { <fields>; }

#retrolambda
-dontwarn java.lang.invoke.*

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

#FireBase Ads
-keep public class com.google.firebase.analytics.FirebaseAnalytics {
    public *;
}

-keep public class com.google.android.gms.measurement.AppMeasurement {
    public *;
}

#Android-Image-Cropper  at https://github.com/ArthurHub/Android-Image-Cropper
-keep class android.support.v7.widget.** { *; }

#Apache Legacy Lib
-keep class org.apache.http.legacy.** { *; }

##app
#-dontobfuscate
#-dontoptimize
#-optimizations !code/allocation/variable

#Facebook
-dontnote com.facebook.**
-keepnames class com.facebook.login.LoginManager
-dontwarn com.facebook.**

#Keep my models feild names
-keepclassmembers class in.co.erudition.paper.data.model.** { <fields>; }

#Using reflection
-keep class in.co.erudition.paper.util.LoginUtils
-dontnote in.co.erudition.paper.util.LoginUtils

#dont warn
-dontwarn in.co.erudition.paper.**
-dontwarn iammert.com.view.scalinglib.**
-dontwarn com.rd.**
-dontwarn com.allattentionhere.fabulousfilter.**
-dontwarn com.github.clans.fab.**
-dontwarn com.firebase.ui.auth.**
-dontwarn in.co.erudition.avatar.**
-dontwarn org.apache.http.legacy.**