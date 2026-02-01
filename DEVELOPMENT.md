# Development Guide

Developer reference for building, testing, and maintaining the GST Invoice PDF → Excel app.

## 🚀 Quick Start

```bash
# Clone repository
git clone https://github.com/zaheerabbas7892034214-ai/GST-Invoice-PDF-Excel-India.git
cd GST-Invoice-PDF-Excel-India

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run app
adb shell am start -n com.gstinvoice.pdftoexcel/.ui.MainActivity
```

## 🏗️ Build Variants

### Debug Build
```bash
./gradlew assembleDebug
```
- Output: `app/build/outputs/apk/debug/app-debug.apk`
- Debuggable: Yes
- Minification: Disabled
- Use for: Development and testing

### Release Build
```bash
./gradlew assembleRelease
```
- Output: `app/build/outputs/apk/release/app-release-unsigned.apk`
- Debuggable: No
- Minification: Enabled (ProGuard/R8)
- Use for: Production release
- **Note**: Requires signing

### Build AAB (Recommended for Play Store)
```bash
./gradlew bundleRelease
```
- Output: `app/build/outputs/bundle/release/app-release.aab`
- Smaller download size for users
- Google Play's preferred format

## 🔐 Signing Configuration

### Option 1: Command Line Signing

```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore /path/to/your-keystore.jks \
  app-release-unsigned.apk your-key-alias
```

Then zipalign:
```bash
zipalign -v 4 app-release-unsigned.apk app-release-signed.apk
```

### Option 2: Gradle Signing Config

Create `keystore.properties` in project root:
```properties
storeFile=/path/to/your-keystore.jks
storePassword=your_store_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

Add to `app/build.gradle.kts`:
```kotlin
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... other config
        }
    }
}
```

**⚠️ Security**: Add `keystore.properties` to `.gitignore`!

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
# Requires connected device or emulator
./gradlew connectedAndroidTest
```

### Lint Checks
```bash
./gradlew lint
```
- Report: `app/build/reports/lint-results.html`

## 🔍 Code Quality

### Static Analysis
```bash
# Run lint
./gradlew lintDebug

# Check for security vulnerabilities (if configured)
./gradlew dependencyCheckAnalyze
```

### Code Formatting
Follow Kotlin coding conventions:
```bash
# Format code (if using ktlint plugin)
./gradlew ktlintFormat
```

## 📦 Dependencies

### Update Dependencies

Check for updates:
```bash
./gradlew dependencyUpdates
```

### Add New Dependency

1. Open `gradle/libs.versions.toml`
2. Add version in `[versions]`:
```toml
newLibVersion = "1.0.0"
```
3. Add library in `[libraries]`:
```toml
new-lib = { group = "com.example", name = "lib", version.ref = "newLibVersion" }
```
4. Add to `app/build.gradle.kts`:
```kotlin
dependencies {
    implementation(libs.new.lib)
}
```

## 🐛 Debugging

### Enable Debug Logging

Add to `app/build.gradle.kts`:
```kotlin
android {
    buildTypes {
        debug {
            buildConfigField("Boolean", "DEBUG_MODE", "true")
        }
    }
}
```

### View Logs
```bash
# View all logs
adb logcat

# Filter by tag
adb logcat -s "GSTInvoice"

# Clear logs
adb logcat -c
```

### Debug APK
```bash
# Install and launch in debug mode
./gradlew installDebug
adb shell am start -D -n com.gstinvoice.pdftoexcel/.ui.MainActivity

# Attach debugger from Android Studio
```

## 📱 Testing on Device

### Install APK
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Install and Grant Permissions
```bash
adb install -g app/build/outputs/apk/debug/app-debug.apk
```

### Uninstall
```bash
adb uninstall com.gstinvoice.pdftoexcel
```

### Clear App Data
```bash
adb shell pm clear com.gstinvoice.pdftoexcel
```

## 🎨 UI Development

### Preview Layouts

Open layout XML files in Android Studio and use the Design/Split view.

### Test Different Screen Sizes

In Android Studio:
1. Tools → AVD Manager
2. Create virtual devices for different sizes
3. Test app on each

### Test Dark Theme
```bash
# Enable dark mode
adb shell "cmd uimode night yes"

# Disable dark mode
adb shell "cmd uimode night no"
```

## 🔄 Version Management

### Update Version

Edit `app/build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        versionCode = 2        // Increment for every release
        versionName = "1.0.1"  // User-visible version
    }
}
```

### Version Naming Convention
- **Major.Minor.Patch** (e.g., 1.0.0)
- **Major**: Breaking changes
- **Minor**: New features, backward compatible
- **Patch**: Bug fixes

## 📊 Performance Optimization

### Reduce APK Size

1. **Enable R8**:
```kotlin
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
}
```

2. **Remove unused resources**:
```bash
./gradlew :app:bundleRelease
```

3. **Use vector drawables** instead of PNG when possible

4. **Compress images** before adding to project

### Profile App Performance

1. **CPU Profiler**: Tools → Profiler in Android Studio
2. **Memory Profiler**: Monitor memory usage
3. **Network Profiler**: Check network calls

### Optimize Database Queries

Use Room query execution time measurement:
```kotlin
@Dao
interface RecentFileDao {
    @Query("SELECT * FROM recent_files WHERE ...")
    suspend fun getFiles(): List<RecentFileEntity>
}
```

## 🚨 Error Handling

### Crash Reporting

Add Firebase Crashlytics (optional):

1. Add to `app/build.gradle.kts`:
```kotlin
plugins {
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

dependencies {
    implementation("com.google.firebase:firebase-crashlytics-ktx")
}
```

2. Initialize in Application class:
```kotlin
FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
```

### Log Custom Events
```kotlin
try {
    // risky operation
} catch (e: Exception) {
    Log.e("GSTInvoice", "Error processing PDF", e)
    // Report to crashlytics if configured
}
```

## 🔧 Common Issues

### Issue: Gradle Sync Failed
**Solution**: 
```bash
./gradlew clean
./gradlew --refresh-dependencies
```

### Issue: Build Cache Corruption
**Solution**:
```bash
./gradlew clean
rm -rf ~/.gradle/caches
./gradlew build
```

### Issue: Out of Memory
**Solution**: Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m
```

### Issue: Duplicate Classes
**Solution**: Check for conflicting dependencies and exclude:
```kotlin
implementation("com.example:lib") {
    exclude(group = "com.example", module = "conflicting-module")
}
```

## 📝 Code Documentation

### Document Classes
```kotlin
/**
 * Manages billing operations and premium feature unlock.
 * 
 * @param context Application context
 * @param scope CoroutineScope for async operations
 */
class BillingManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    // ...
}
```

### Generate KDoc
```bash
./gradlew dokkaHtml
```
Output: `app/build/dokka/html/index.html`

## 🔐 Security Best Practices

1. **Never commit**:
   - `local.properties`
   - `keystore.properties`
   - `.jks` or `.keystore` files
   - API keys

2. **Use BuildConfig for secrets**:
```kotlin
android {
    buildTypes {
        release {
            buildConfigField("String", "API_KEY", "\"${System.getenv("API_KEY")}\"")
        }
    }
}
```

3. **Validate all inputs**:
```kotlin
fun processUri(uri: Uri?) {
    uri ?: return
    // Validate and process
}
```

4. **Use HTTPS** for all network calls

5. **Enable app signing** by Google Play

## 🎯 CI/CD (Optional)

### GitHub Actions Example

Create `.github/workflows/android.yml`:
```yaml
name: Android CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    - name: Build with Gradle
      run: ./gradlew build
    - name: Run tests
      run: ./gradlew test
```

## 📚 Resources

- [Android Developers](https://developer.android.com/)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Material Design 3](https://m3.material.io/)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Navigation Component](https://developer.android.com/guide/navigation)
- [Google Play Billing](https://developer.android.com/google/play/billing)

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

---

**Happy Coding! 💻**
