# iOG26 - R8/ProGuard hardening for release stability
# Target: keep critical runtime/reflection pieces used by Room, WorkManager,
# coroutines and Android entry points. This reduces device-specific crashes
# when minifyEnabled=true.

# --- Keep important class metadata for annotations/reflection ---
-keepattributes *Annotation*,Signature,EnclosingMethod,InnerClasses
# Optional (better stacktraces when obfuscated)
-keepattributes SourceFile,LineNumberTable

# --- Android app entry points in our package ---
# Activities
-keep class com.humanjuan.iog26.ui.** extends android.app.Activity { *; }
# Services (incl. CallScreeningService)
-keep class com.humanjuan.iog26.phone.** extends android.app.Service { *; }
-keep class com.humanjuan.iog26.phone.MyScreeningService { *; }
# BroadcastReceivers
-keep class com.humanjuan.iog26.boot.** extends android.content.BroadcastReceiver { *; }
# WorkManager Workers
-keep class com.humanjuan.iog26.digest.DailyDigestWorker { *; }

# --- Room (KSP) ---
# Room ships consumer rules, but we reinforce to avoid rare OEM issues.
-keep class androidx.room.** { *; }
-keep interface androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keepclassmembers class **Dao { *; }
-keep class com.humanjuan.iog26.data.** { *; }

# --- WorkManager ---
-keep class androidx.work.** { *; }
-keep interface androidx.work.** { *; }

# --- Kotlin Coroutines (keep internals referenced reflectively) ---
-keep class kotlinx.coroutines.** { *; }
-keep interface kotlinx.coroutines.** { *; }

# --- AndroidX App Startup (safe even if not used; many libs rely on it) ---
-keep class androidx.startup.** { *; }

# --- Coil image loader (usually fine, but harmless to keep) ---
-keep class coil.** { *; }

# --- libphonenumber (avoid warnings; code is safe to shrink) ---
-dontwarn com.google.i18n.phonenumbers.**

# --- Misc. common annotations used by libraries ---
-dontwarn javax.annotation.**
-dontwarn org.checkerframework.**

# You can further tighten rules later once crash stacktraces identify exact keeps.