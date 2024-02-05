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
-keepattributes SourceFile,LineNumberTable
-dontobfuscate

# If you keep the line number information, uncomment this to
# hide the original source file name.

-renamesourcefileattribute SourceFile
-keepattributes Signature
-keep class com.github.miwu.logic.** { *; }
-keep class androidx.databinding.**{ *; }
-keep class com.github.miwu.databinding.**{ *; }
-keep class com.github.miwu.ui.**{ *; }
-keep class com.github.miwu.miot.**{ *; }
-keep class com.github.miwu.viewmodel.**{ *; }
-keep class androidx.lifecycle.**{ *; }
-keep class kndroidx.**{ *; }
