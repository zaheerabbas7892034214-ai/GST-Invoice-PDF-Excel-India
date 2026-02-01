# GST Invoice PDF → Excel (India)

A production-ready Android application that converts GST invoice PDFs into structured Excel/CSV tables. Built with Kotlin and modern Android architecture.

## 🚀 Features

- **PDF Processing**: Support for both text-based and scanned PDFs
- **OCR Capability**: ML Kit Text Recognition for scanned invoices
- **GST Data Extraction**: Automatically extract invoice number, date, GSTIN, amounts, and more
- **Multiple Export Formats**: CSV and Excel (XLSX) export
- **Premium Model**: Free preview of first 10 rows, unlock full data with in-app purchase
- **Material 3 Design**: Modern UI with dark and light themes
- **Recent Files**: Track conversion history with Room database
- **Google Play Billing**: Integrated in-app purchase (₹299)

## 📱 Screenshots

*(Add screenshots after building the app)*

## 🏗️ Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture with clean separation of concerns:

```
app/
├── data/
│   ├── database/          # Room database (Recent files)
│   ├── model/             # Data models
│   └── repository/        # Repository layer
├── pdf/                   # PDF processing logic
├── ui/
│   ├── home/             # Home screen
│   ├── preview/          # Data preview screen
│   ├── export/           # Export screen
│   ├── billing/          # Premium purchase
│   └── settings/         # Settings screen
└── utils/                # Utilities (Export, Billing, Data Cleaning)
```

## 🛠️ Tech Stack

- **Language**: Kotlin 1.9.20
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Build System**: Gradle with Version Catalog
- **Architecture**: MVVM with LiveData/StateFlow
- **UI**: Material 3, View Binding
- **Navigation**: Navigation Component with SafeArgs
- **Database**: Room
- **Async**: Kotlin Coroutines
- **PDF**: Android PdfRenderer
- **OCR**: ML Kit Text Recognition
- **Excel**: Apache POI
- **Billing**: Google Play Billing Library 6+

## 📋 Prerequisites

- Android Studio Giraffe (2022.3.1) or later
- JDK 17
- Android SDK with API 34
- Google Play Console account (for in-app products)

## 🔧 Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/zaheerabbas7892034214-ai/GST-Invoice-PDF-Excel-India.git
cd GST-Invoice-PDF-Excel-India
```

### 2. Change Package Name (Optional)

To use your own package name:

1. In Android Studio, right-click on the package `com.gstinvoice.pdftoexcel`
2. Select "Refactor" → "Rename"
3. Choose "Rename package"
4. Enter your new package name
5. Update `applicationId` in `app/build.gradle.kts`
6. Update `namespace` in `app/build.gradle.kts`
7. Update package name in `AndroidManifest.xml`

### 3. Add Launcher Icons

The project includes placeholder icons. To add production-ready icons:

1. Right-click on `res` folder
2. Select "New" → "Image Asset"
3. Configure your launcher icon
4. Click "Next" and "Finish"

### 4. Update Privacy Policy URL

Edit `app/src/main/res/values/strings.xml`:

```xml
<string name="privacy_policy_url">https://yourdomain.com/privacy-policy</string>
```

### 5. Build the Project

```bash
./gradlew clean build
```

Or in Android Studio: **Build → Make Project**

## 🛒 Google Play Billing Setup

### Create In-App Product

1. Go to [Google Play Console](https://play.google.com/console)
2. Select your app
3. Navigate to "Monetize" → "In-app products"
4. Click "Create product"
5. Configure the product:
   - **Product ID**: `premium_unlock`
   - **Name**: Premium Unlock
   - **Description**: Unlock unlimited row extraction and export features
   - **Price**: ₹299.00 (or your preferred price)
   - **Type**: One-time purchase (Managed product)
6. Save and activate the product

### Test Billing

1. Add test accounts in Play Console under "Settings" → "License testing"
2. Use a test account to install the app from Play Store (internal testing track)
3. Test the purchase flow
4. Verify unlock functionality

**Note**: Billing will only work with a signed APK/AAB uploaded to Play Console (even for testing).

## 📦 Building Release APK

### Generate Keystore

```bash
keytool -genkey -v -keystore release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias release
```

### Sign the APK

1. In Android Studio: **Build → Generate Signed Bundle/APK**
2. Choose "Android App Bundle" (recommended) or "APK"
3. Select your keystore or create a new one
4. Choose "release" build variant
5. Click "Finish"

Output will be in: `app/release/app-release.aab` or `app/release/app-release.apk`

## 🚀 Play Store Upload

### Prepare Assets

1. **App Icon**: 512x512 PNG
2. **Feature Graphic**: 1024x500 PNG
3. **Screenshots**: At least 2 screenshots per device type
4. **App Description**: Write a compelling description highlighting features

### Upload Steps

1. Go to [Google Play Console](https://play.google.com/console)
2. Create a new app or select existing
3. Complete "Store presence" section (app details, graphics, screenshots)
4. Create a release in "Production", "Open testing", or "Internal testing"
5. Upload your AAB file
6. Fill in release notes
7. Set rollout percentage (or 100% for full release)
8. Review and roll out

### Post-Launch Checklist

- [ ] Test installation from Play Store
- [ ] Test premium purchase flow
- [ ] Monitor crash reports in Play Console
- [ ] Respond to user reviews
- [ ] Monitor ANRs and performance metrics

## 🔐 Security Notes

- Never commit `local.properties` or keystore files
- Store signing keys securely
- Enable ProGuard/R8 for release builds (already configured)
- Keep dependencies updated for security patches
- Follow Android's [security best practices](https://developer.android.com/topic/security/best-practices)

## 🐛 Troubleshooting

### Build Failures

**Issue**: Gradle sync fails
- Solution: Ensure you have JDK 17 and Android SDK 34 installed

**Issue**: Apache POI dependency conflicts
- Solution: The `packaging` block in `app/build.gradle.kts` excludes conflicting files

### Runtime Issues

**Issue**: Billing not working
- Solution: Ensure app is installed from Play Store with valid in-app product

**Issue**: OCR not extracting text
- Solution: Ensure device has Google Play Services installed

**Issue**: PDF rendering crashes
- Solution: Check PDF is valid and not corrupted, test with different PDFs

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📧 Support

For support, email: support@yourdomain.com

## 🙏 Acknowledgments

- [Apache POI](https://poi.apache.org/) for Excel export
- [ML Kit](https://developers.google.com/ml-kit) for OCR
- [Material Design](https://m3.material.io/) for UI components

---

**Made with ❤️ for Indian GST compliance**
