# GST Invoice PDF → Excel (India)

A production-ready Android application for extracting GST invoice data from PDF files and exporting to CSV/Excel format, built with Kotlin and Jetpack Compose.

## Features

### Core Functionality
- **PDF Import**: Select one or multiple invoice PDFs using Storage Access Framework (SAF)
- **OCR Extraction**: Advanced text recognition using ML Kit for scanned PDFs
- **Data Extraction**: Automatically extracts:
  - Invoice Number and Date
  - Supplier Name and GSTIN
  - Buyer GSTIN
  - Line Items (Description, HSN/SAC, Quantity, Rate, Taxable Value, CGST, SGST, IGST, Total)
- **Smart Data Cleaning**: 
  - Handles multiple invoice formats
  - Skips repeated headers/footers
  - Normalizes dates and currencies
  - Deduplicates content
  - Gracefully handles missing fields

### Premium Features (₹399 One-Time)
- **Unlimited Extraction**: Process unlimited line items across all invoices
- **Export Functionality**: 
  - CSV export with all invoice data
  - Excel (XLSX) export with consolidated and individual sheets
- **Sharing**: Share exported files via Android system
- **No Ads**: Ad-free experience forever

### Free Tier
- Preview up to 15 line items across selected invoices
- Full data extraction (limited preview)
- Access to all app features (with limitations)

## Technology Stack

### Core Technologies
- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel)

### Key Libraries
- **Room Database**: Local data persistence
- **Google Play Billing Library 6+**: In-app purchases
- **ML Kit Text Recognition**: OCR for scanned PDFs
- **PDFBox Android**: PDF rendering and processing
- **Apache POI**: Excel file generation
- **Kotlin Coroutines**: Asynchronous operations
- **Navigation Compose**: App navigation
- **DataStore**: Preferences storage

## App Architecture

### MVVM Pattern
```
Presentation Layer (UI + ViewModels)
    ↓
Domain Layer (Use Cases + Business Logic)
    ↓
Data Layer (Repository + DAO + Entities)
```

### Database Schema
- **InvoiceEntity**: Stores invoice header information
- **LineItemEntity**: Stores line item details (1-to-many with Invoice)
- **PurchaseEntity**: Stores billing purchase information with retry logic

## App Screens

1. **Splash Screen**: 
   - Performs entitlement check
   - Validates premium status
   - Smooth transition to home

2. **Home Screen**:
   - Recent imports list
   - Quick access to file picker
   - Premium upgrade button (for free users)
   - Search and filter functionality

3. **Import Screen**:
   - Multi-file PDF picker
   - Real-time import progress
   - Error handling with retry options

4. **Preview Screen**:
   - Two tabs: Summary and Line Items
   - Search/filter by supplier or invoice number
   - Free tier: Shows first 15 items with blur effect on locked items
   - Premium tier: Full access to all items
   - Inline paywall banner for free users

5. **Export Screen**:
   - CSV export option
   - Excel (XLSX) export with multiple worksheets
   - Premium-gated functionality

6. **Paywall Modal**:
   - Clear pricing (₹399)
   - Feature comparison
   - Purchase flow integration
   - Restore purchases option

7. **Settings Screen**:
   - Premium status indicator
   - Restore purchases
   - About, Privacy Policy, Terms of Service

## Billing Integration

### Google Play Billing
- **Product ID**: `gst_pro_unlock`
- **Type**: INAPP (one-time purchase)
- **Price**: ₹399

### Features
- Automatic purchase acknowledgment
- Offline retry mechanism (up to 5 attempts)
- Purchase persistence in Room DB
- Restore purchases functionality
- Entitlement check on app launch

## File Handling

### Storage Access Framework (SAF)
- All file operations use SAF for security and privacy
- No direct file system access
- User-controlled file selection
- Secure output file creation

## Data Extraction Algorithm

### PDF Processing
1. Render PDF pages as bitmaps using PdfRenderer
2. Process each bitmap with ML Kit Text Recognition
3. Extract text with high-resolution rendering (2x scale)
4. Combine text from all pages

### Intelligent Parsing
1. **Header Extraction**: Pattern matching for invoice number, date, supplier details
2. **GSTIN Recognition**: Regex-based GST number validation
3. **Line Item Detection**:
   - Skip header rows (keyword matching)
   - Skip footer rows (total/subtotal detection)
   - Extract structured data (description, HSN, quantities, amounts)
   - Deduplicate items
4. **Tax Calculation**: Aggregate CGST, SGST, IGST across line items

## Build and Run

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

### Setup
```bash
# Clone the repository
git clone https://github.com/zaheerabbas7892034214-ai/GST-Invoice-PDF-Excel-India.git

# Open in Android Studio
# Sync Gradle files
# Run on emulator or device
```

### Build Commands
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

## Project Structure
```
app/
├── src/main/java/com/gstinvoice/pdfexcel/
│   ├── billing/              # Billing manager
│   ├── data/                 # Data layer
│   │   ├── dao/             # Room DAOs
│   │   ├── entity/          # Room entities
│   │   ├── model/           # Data models
│   │   └── repository/      # Repositories
│   ├── presentation/         # UI layer
│   │   ├── ui/              # Compose screens
│   │   │   ├── home/
│   │   │   ├── import/
│   │   │   ├── preview/
│   │   │   ├── export/
│   │   │   ├── paywall/
│   │   │   ├── settings/
│   │   │   └── splash/
│   │   └── viewmodel/       # ViewModels
│   ├── util/                # Utilities
│   │   ├── PdfExtractor.kt
│   │   └── ExportManager.kt
│   ├── GSTInvoiceApplication.kt
│   └── MainActivity.kt
└── src/main/res/            # Resources
```

## Security & Privacy

- **No Cloud Storage**: All data stored locally on device
- **SAF Integration**: User controls all file access
- **ProGuard Rules**: Code obfuscation for release builds
- **Secure Billing**: Google Play Billing integration
- **No Permissions**: Minimal permission requirements

## Testing

### Manual Testing Checklist
- [ ] Import single PDF
- [ ] Import multiple PDFs
- [ ] Search and filter invoices
- [ ] Preview with free tier limit
- [ ] Purchase premium
- [ ] Export CSV
- [ ] Export Excel
- [ ] Restore purchases
- [ ] Offline purchase retry

## Future Enhancements

- Batch processing improvements
- Cloud sync (optional)
- Advanced filtering and sorting
- Custom export templates
- Multi-language support
- Invoice validation rules

## License

Copyright © 2024 GST Invoice PDF Excel India
All rights reserved.

## Support

For issues or questions, please open an issue on GitHub.

## Acknowledgments

- ML Kit by Google for OCR capabilities
- Apache POI for Excel generation
- PDFBox for PDF processing
- Material Design 3 for beautiful UI
