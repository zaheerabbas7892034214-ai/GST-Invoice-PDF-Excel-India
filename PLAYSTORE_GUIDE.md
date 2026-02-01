# Google Play Store Upload Guide

Complete guide for uploading the GST Invoice PDF → Excel app to Google Play Store.

## 📋 Prerequisites

- [x] Google Play Developer account ($25 one-time registration fee)
- [x] Signed release APK/AAB
- [x] App icon (512x512 PNG)
- [x] Feature graphic (1024x500 PNG)
- [x] Screenshots (Phone & Tablet)
- [x] Privacy Policy URL (hosted online)
- [x] App description and promotional text

## 🎨 Required Graphics Assets

### 1. App Icon
- **Size**: 512 x 512 px
- **Format**: 32-bit PNG
- **Color Space**: sRGB
- **Note**: Must not have rounded corners or transparency

### 2. Feature Graphic
- **Size**: 1024 x 500 px
- **Format**: JPEG or 24-bit PNG
- **Color Space**: sRGB
- **Usage**: Displayed at the top of your store listing

### 3. Screenshots

#### Phone Screenshots (Required)
- **Minimum**: 2 screenshots
- **Maximum**: 8 screenshots
- **Aspect Ratio**: 16:9 or 9:16
- **Min Dimensions**: 320 px (short edge)
- **Max Dimensions**: 3840 px (long edge)
- **Format**: JPEG or 24-bit PNG
- **Recommended**: 1080 x 1920 px (portrait) or 1920 x 1080 px (landscape)

#### Tablet Screenshots (Recommended)
- Same requirements as phone
- **Recommended**: 1920 x 1200 px (landscape) or 1200 x 1920 px (portrait)

### 4. Promotional Graphics (Optional but Recommended)

#### Promo Graphic
- **Size**: 180 x 120 px
- **Format**: JPEG or 24-bit PNG

#### TV Banner (If supporting Android TV)
- **Size**: 1280 x 720 px
- **Format**: JPEG or 24-bit PNG

## 📝 App Information

### App Title
```
GST Invoice PDF → Excel (India)
```
**Limit**: 50 characters

### Short Description
```
Convert GST invoices from PDF to Excel/CSV instantly. Extract invoice data, amounts, GSTIN automatically with OCR support.
```
**Limit**: 80 characters

### Full Description
```
📱 GST Invoice PDF → Excel Converter

Transform your GST invoice PDFs into structured Excel and CSV files with just a few taps. Perfect for Indian businesses, accountants, and tax professionals.

✨ KEY FEATURES

• PDF Processing: Support for both digital and scanned PDF invoices
• Smart OCR: Automatically extract text from scanned documents using ML Kit
• GST Data Extraction: Intelligently identifies invoice number, date, GSTIN, amounts, and more
• Multiple Formats: Export to CSV or Excel (XLSX) format
• Recent History: Keep track of your recent conversions
• Material Design: Beautiful, modern interface with dark and light themes
• Offline Support: Process invoices without internet connection (OCR requires Google Play Services)

💼 PERFECT FOR

• Small business owners
• Accountants and bookkeepers
• Tax consultants
• E-commerce sellers
• Anyone dealing with GST invoices regularly

🎯 HOW IT WORKS

1. Select your GST invoice PDF file
2. App automatically extracts key data
3. Preview the extracted information in table format
4. Export to CSV or Excel
5. Share or save to your device

🔒 PRIVACY & SECURITY

• All processing happens on your device
• No data is sent to external servers
• Secure file handling with Android's Storage Access Framework
• No personal information collected

💎 PREMIUM FEATURES

Unlock full access with a one-time purchase:
• Unlimited row extraction (free version limited to 10 rows)
• No restrictions on file size
• Priority support

🇮🇳 MADE FOR INDIA

Specifically designed for Indian GST invoice formats. Supports:
• GSTIN validation
• Indian date formats
• Rupee (₹) currency handling
• CGST, SGST, and IGST extraction

📧 SUPPORT

Need help? Contact us at: support@yourdomain.com

⭐ RATE US

Love the app? Please leave us a 5-star review!

---
Compatible with Android 7.0 (API 24) and above.
```
**Limit**: 4000 characters

## 🏪 Store Listing Setup

### Step 1: Create App in Play Console

1. Go to [Google Play Console](https://play.google.com/console)
2. Click "Create app"
3. Fill in:
   - **App name**: GST Invoice PDF → Excel (India)
   - **Default language**: English (United States) or English (India)
   - **App or game**: App
   - **Free or paid**: Free
4. Check declarations and click "Create app"

### Step 2: Store Presence

#### Main Store Listing

1. Navigate to **Store presence** → **Main store listing**
2. Fill in all required fields:
   - App name
   - Short description
   - Full description
   - App icon (512x512 PNG)
   - Feature graphic (1024x500 PNG)
   - Phone screenshots (at least 2)
   - Tablet screenshots (recommended)
3. **App category**: Business or Productivity
4. **Content rating**: Click "Start questionnaire"
   - Select "Utility, Productivity, Communication, or Others"
   - Answer all questions honestly
   - Submit for rating
5. **Target audience and content**: 
   - Target age: All ages
   - Include ads: No (unless you plan to add ads)
6. **Privacy policy**: Enter your privacy policy URL
7. Click "Save"

### Step 3: App Content

#### Privacy Policy
Create a simple privacy policy and host it online. Example template:

```markdown
# Privacy Policy for GST Invoice PDF → Excel

Last updated: [Date]

## Data Collection
This app does not collect any personal data. All PDF processing happens locally on your device.

## File Access
The app requires permission to access files only to read PDF files you select and save exported files.

## Google Play Services
The app uses Google Play Services for OCR (text recognition) functionality.

## In-App Purchases
Purchase information is handled securely through Google Play Billing.

## Contact
For questions, email: support@yourdomain.com
```

Host on:
- GitHub Pages (free)
- Your website
- Google Sites (free)
- Any web hosting service

#### Data Safety Section

1. Navigate to **App content** → **Data safety**
2. Complete the questionnaire:
   - Does your app collect or share user data? **No**
   - Is all of the user data collected by your app encrypted in transit? **Yes**
   - Do you provide a way for users to request data deletion? **Not applicable**
3. Submit

### Step 4: Production Release

#### Create Release

1. Navigate to **Release** → **Production**
2. Click "Create new release"
3. **App signing by Google Play**:
   - If first release: Opt-in (recommended)
   - Google will manage your app signing key
4. **Upload App Bundle**:
   - Upload your `app-release.aab` file
   - Wait for processing
5. **Release name**: "1.0.0" (matches versionName)
6. **Release notes**: 
```
Initial release of GST Invoice PDF → Excel

• Convert GST invoice PDFs to Excel/CSV
• Support for scanned PDFs with OCR
• Extract invoice details automatically
• Material 3 design with dark theme
• Recent conversions history
• Premium unlock available
```
7. Click "Next"

#### Review and Rollout

1. Review all information
2. **Rollout percentage**: Start with 20% for safer release, or 100% for full release
3. Click "Start rollout to Production"

## 💎 In-App Products Setup

### Create Premium Unlock Product

1. Navigate to **Monetize** → **In-app products**
2. Click "Create product"
3. Fill in product details:
   - **Product ID**: `premium_unlock`
   - **Name**: Premium Unlock
   - **Description**: Unlock unlimited row extraction and export all your GST invoice data without restrictions.
   - **Status**: Active
   - **Price**: Set to ₹299 (India)
     - Click "Set price"
     - Select "India (INR)"
     - Enter "299"
     - Click "Apply prices"
4. Click "Save"
5. **Activate**: Toggle to "Active"

**Note**: Product must be activated and app must be published (even to internal testing) before testing purchases.

## 🧪 Testing Before Release

### Internal Testing Track

1. Navigate to **Release** → **Testing** → **Internal testing**
2. Create a new release
3. Upload your AAB
4. Add testers:
   - Create a tester list
   - Add email addresses
   - Testers will receive invitation link
5. Share the testing link with your testers
6. Test all features including billing

### Closed Testing (Alpha)

Use for broader testing with selected users:
1. Navigate to **Release** → **Testing** → **Closed testing**
2. Create closed testing track
3. Add testers or create opt-in link
4. Upload AAB
5. Start rollout

## 📊 Post-Launch

### Monitor Analytics

1. **Dashboard**: Monitor installs, crashes, and ANRs
2. **Statistics**: Track user acquisition and engagement
3. **Crashes & ANRs**: Fix critical issues immediately
4. **Reviews**: Respond to user reviews promptly

### Update Release

When releasing updates:
1. Increment `versionCode` in `build.gradle.kts`
2. Update `versionName` if needed
3. Create new release in Production
4. Upload new AAB
5. Add release notes describing changes
6. Roll out update

## ⚠️ Common Rejections & Solutions

### Rejection: Missing Privacy Policy
**Solution**: Add a valid, accessible privacy policy URL

### Rejection: Misleading Content
**Solution**: Ensure screenshots and descriptions accurately represent the app

### Rejection: Data Safety Section Incomplete
**Solution**: Complete all questions in Data Safety section

### Rejection: Target API Level
**Solution**: Ensure `targetSdk = 34` (or latest required version)

### Rejection: Permissions Not Justified
**Solution**: Add permission usage explanation in manifest and store listing

## 📱 App Updates

### Release Cycle

1. **Fix critical bugs**: Release hotfix immediately
2. **Minor updates**: Every 2-4 weeks
3. **Major updates**: Every 2-3 months

### Version Numbering

Follow semantic versioning:
- **Major.Minor.Patch** (e.g., 1.0.0)
- Increment `versionCode` for every release
- Increment `versionName` following semantic versioning

Example:
```kotlin
versionCode = 2        // Incremented
versionName = "1.0.1"  // Bug fix
```

## 🎯 Marketing Tips

1. **ASO (App Store Optimization)**:
   - Use relevant keywords in title and description
   - Update screenshots regularly
   - Respond to all reviews

2. **Promote Your App**:
   - Share on social media
   - Create a website
   - Write blog posts about GST invoice management
   - Reach out to accounting communities

3. **Collect Feedback**:
   - Add feedback option in app
   - Monitor reviews closely
   - Implement user suggestions

## 🔐 Security Checklist

Before releasing:
- [x] ProGuard/R8 enabled for release builds
- [x] No hardcoded API keys or secrets
- [x] Proper permission handling
- [x] Secure file handling
- [x] Input validation
- [x] Error handling
- [x] Crash reporting setup (optional: Firebase Crashlytics)

## 📧 Support

Questions about Play Store upload?
- Email: support@yourdomain.com
- Play Console Help: https://support.google.com/googleplay/android-developer

---

**Good luck with your launch! 🚀**
