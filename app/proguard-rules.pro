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

-keep public class com.zen.alchan.data.entity.**{
    *;
}
-keep public class com.zen.alchan.data.response.**{
    *;
}
-keep public class com.zen.alchan.helper.pojo.**{
    *;
}
-keepclassmembers enum * {
    public *;
}

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

 # Keep generic signature of RxJava3 (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Flowable
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Maybe
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Observable
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Single


-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type