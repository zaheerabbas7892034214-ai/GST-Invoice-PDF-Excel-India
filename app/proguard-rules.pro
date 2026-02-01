# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep data classes and entities
-keep class com.gstinvoice.pdfexcel.data.model.** { *; }
-keep class com.gstinvoice.pdfexcel.data.entity.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Apache POI
-dontwarn org.apache.poi.**
-dontwarn org.apache.xmlbeans.**
-dontwarn org.openxmlformats.schemas.**
-keep class org.apache.poi.** { *; }

# PDFBox
-dontwarn org.apache.pdfbox.**
-keep class org.apache.pdfbox.** { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }

# Billing
-keep class com.android.billingclient.** { *; }
