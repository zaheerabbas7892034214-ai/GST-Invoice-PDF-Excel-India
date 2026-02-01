# 🚀 Quick Start Guide

Get started with the GST Invoice PDF → Excel app in 5 minutes!

## ⚡ Fastest Way to Get Running

### 1. Open Project
```bash
# Open in Android Studio Giraffe or later
# File → Open → Select GST-Invoice-PDF-Excel-India folder
```

### 2. Sync Gradle
Android Studio will automatically sync. If not:
- Click "Sync Now" banner at the top
- Or: File → Sync Project with Gradle Files

### 3. Run on Device/Emulator
- Click the green ▶ Play button
- Or: Shift+F10 (Windows/Linux) or Ctrl+R (Mac)

**That's it!** The app will build and launch.

---

## 📋 Before You Build

### Check Prerequisites
- ✅ Android Studio Giraffe (2022.3.1) or later
- ✅ JDK 17
- ✅ Android SDK with API 34
- ✅ Emulator or physical device (Android 7.0+)

### If Build Fails
```bash
# Clean and rebuild
./gradlew clean build

# Or in Android Studio:
# Build → Clean Project
# Build → Rebuild Project
```

---

## 🎨 Customize Before Launch

### 1. Add Launcher Icon (2 minutes)
1. Right-click `app/src/main/res`
2. New → Image Asset
3. Select your 512x512 PNG icon
4. Click "Next" → "Finish"

### 2. Update App Name (30 seconds)
Edit `app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">Your App Name</string>
```

### 3. Set Privacy Policy URL (30 seconds)
Edit `app/src/main/res/values/strings.xml`:
```xml
<string name="privacy_policy_url">https://yourdomain.com/privacy</string>
```

### 4. Change Package Name (Optional, 2 minutes)
1. Right-click package `com.gstinvoice.pdftoexcel`
2. Refactor → Rename
3. Enter new package name
4. Update in `app/build.gradle.kts`:
   ```kotlin
   applicationId = "your.new.package"
   namespace = "your.new.package"
   ```

---

## 🧪 Quick Test Checklist

Run through these to verify everything works:

### Basic Flow
1. ✅ Launch app
2. ✅ Click "Select PDF Invoice"
3. ✅ Choose a GST invoice PDF
4. ✅ See data preview in table
5. ✅ Click "Export Data"
6. ✅ Export as CSV or Excel
7. ✅ Check exported file

### Premium Features
1. ✅ See blur overlay after row 10
2. ✅ Click "Unlock Premium"
3. ✅ See billing screen
   - Note: Billing only works with signed APK from Play Store

### Other Features
1. ✅ Check recent files list on home
2. ✅ Toggle dark theme (device settings)
3. ✅ Visit settings screen

---

## 🏗️ Build Commands

### Debug Build
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Install on Device
```bash
./gradlew installDebug
# Installs directly to connected device/emulator
```

### Release Build (for Play Store)
```bash
./gradlew bundleRelease
# Output: app/build/outputs/bundle/release/app-release.aab
# Note: Requires signing configuration
```

---

## 🔧 Common Issues & Quick Fixes

### "SDK location not found"
**Fix**: Create `local.properties`:
```properties
sdk.dir=/path/to/your/Android/Sdk
```

### Gradle sync failed
**Fix**:
```bash
./gradlew clean
./gradlew --refresh-dependencies
```

### "Unresolved reference"
**Fix**: File → Invalidate Caches → Invalidate and Restart

### Build too slow
**Fix**: Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m
org.gradle.parallel=true
org.gradle.caching=true
```

---

## 📱 Testing on Physical Device

### Enable USB Debugging
1. Open device Settings
2. About Phone → Tap "Build number" 7 times
3. Back → Developer Options
4. Enable "USB Debugging"
5. Connect device via USB
6. Accept debugging prompt on device

### Install & Run
```bash
./gradlew installDebug
adb shell am start -n com.gstinvoice.pdftoexcel/.ui.MainActivity
```

---

## 🎯 What to Test First

### Essential Tests
1. **PDF Selection**: Try different PDF files
   - Text-based PDF
   - Scanned PDF (OCR)
   - Invalid/corrupted PDF

2. **Data Extraction**: Check extracted data
   - Invoice number
   - Date (should be normalized)
   - GSTIN
   - Amounts

3. **Export**: Export and open files
   - CSV in spreadsheet app
   - Excel in Excel/Sheets

4. **UI/UX**: Check visual appearance
   - Light theme
   - Dark theme (enable in device settings)
   - Different screen sizes

### Edge Cases
- Empty PDF
- Very large PDF (50+ pages)
- PDF with no GST data
- No storage permission
- No internet (for OCR)

---

## 📦 Next Steps After Testing

### 1. Prepare for Release
- [ ] Generate release keystore
- [ ] Sign release build
- [ ] Test signed APK/AAB
- [ ] Create Play Store listing
- [ ] Set up in-app product (premium_unlock)

### 2. Upload to Play Store
Follow **PLAYSTORE_GUIDE.md** for:
- Asset requirements
- Store listing
- In-app products
- Release process

### 3. Post-Launch
- Monitor crash reports
- Respond to reviews
- Plan updates

---

## 📚 Full Documentation

For detailed information, see:

- **README.md** - Main project documentation
- **PLAYSTORE_GUIDE.md** - Play Store upload (10,500+ words)
- **DEVELOPMENT.md** - Developer reference (9,500+ words)
- **FILE_STRUCTURE.md** - Project structure
- **PROJECT_SUMMARY.md** - Completion report

---

## 💡 Pro Tips

### Faster Builds
1. Use Gradle daemon
2. Enable build cache
3. Use incremental compilation
4. Connect to fast WiFi for dependencies

### Better Testing
1. Create multiple test GST invoices
2. Test on different Android versions
3. Test on different screen sizes
4. Test with slow network

### Debugging
1. Use Android Studio Profiler
2. Enable verbose logging in debug build
3. Use Layout Inspector for UI issues
4. Monitor logcat for errors

---

## ✅ Verification Checklist

Before considering it "done":

- [ ] App builds without errors
- [ ] App runs on emulator
- [ ] App runs on physical device
- [ ] All screens accessible
- [ ] PDF selection works
- [ ] Data extraction works
- [ ] Export functionality works
- [ ] Dark theme looks good
- [ ] No crashes during basic usage
- [ ] Custom launcher icon added
- [ ] App name updated (if needed)

---

## 🚨 Emergency Troubleshooting

### Nothing works!
1. Restart Android Studio
2. Invalidate Caches and Restart
3. Delete .gradle and .idea folders
4. Reimport project

### Still stuck?
1. Check Android Studio's Build output
2. Read error messages carefully
3. Google the specific error
4. Check Stack Overflow
5. Review DEVELOPMENT.md

---

## 🎓 Learning Resources

While you're getting started:
- [Android Developer Guides](https://developer.android.com/guide)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Material Design 3](https://m3.material.io/)

---

## 🎉 You're Ready!

Everything you need is in place. The app is complete and ready to:
- ✅ Build
- ✅ Test
- ✅ Customize
- ✅ Release

**Time to launch your GST Invoice app! 🚀**

---

**Quick Start Version**: 1.0
**Last Updated**: February 1, 2026
**Status**: ✅ Production Ready
