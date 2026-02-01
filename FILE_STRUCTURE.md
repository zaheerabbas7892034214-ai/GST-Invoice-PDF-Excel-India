# Project File Structure

```
GST-Invoice-PDF-Excel-India/
│
├── app/                                    # Main application module
│   ├── src/
│   │   └── main/
│   │       ├── java/com/gstinvoice/pdftoexcel/
│   │       │   ├── data/                   # Data layer
│   │       │   │   ├── database/           # Room database
│   │       │   │   │   ├── AppDatabase.kt
│   │       │   │   │   ├── RecentFileDao.kt
│   │       │   │   │   └── RecentFileEntity.kt
│   │       │   │   ├── model/              # Data models
│   │       │   │   │   ├── ExportData.kt
│   │       │   │   │   └── GSTInvoiceData.kt
│   │       │   │   └── repository/         # Repository layer
│   │       │   │       └── InvoiceRepository.kt
│   │       │   │
│   │       │   ├── pdf/                    # PDF processing
│   │       │   │   └── PDFProcessor.kt
│   │       │   │
│   │       │   ├── ui/                     # Presentation layer
│   │       │   │   ├── billing/
│   │       │   │   │   └── BillingFragment.kt
│   │       │   │   ├── export/
│   │       │   │   │   ├── ExportFragment.kt
│   │       │   │   │   └── ExportViewModel.kt
│   │       │   │   ├── home/
│   │       │   │   │   ├── HomeFragment.kt
│   │       │   │   │   ├── HomeViewModel.kt
│   │       │   │   │   └── RecentFilesAdapter.kt
│   │       │   │   ├── preview/
│   │       │   │   │   ├── DataTableAdapter.kt
│   │       │   │   │   ├── PreviewFragment.kt
│   │       │   │   │   └── PreviewViewModel.kt
│   │       │   │   ├── settings/
│   │       │   │   │   └── SettingsFragment.kt
│   │       │   │   └── MainActivity.kt
│   │       │   │
│   │       │   ├── utils/                  # Utilities
│   │       │   │   ├── BillingManager.kt
│   │       │   │   ├── DataCleaner.kt
│   │       │   │   └── ExportUtils.kt
│   │       │   │
│   │       │   └── GSTInvoiceApplication.kt
│   │       │
│   │       ├── res/                        # Resources
│   │       │   ├── drawable/               # Vector drawables
│   │       │   │   ├── ic_back.xml
│   │       │   │   ├── ic_check_circle.xml
│   │       │   │   ├── ic_delete.xml
│   │       │   │   ├── ic_export.xml
│   │       │   │   ├── ic_pdf.xml
│   │       │   │   ├── ic_settings.xml
│   │       │   │   └── premium_overlay_bg.xml
│   │       │   │
│   │       │   ├── layout/                 # XML layouts
│   │       │   │   ├── activity_main.xml
│   │       │   │   ├── fragment_billing.xml
│   │       │   │   ├── fragment_export.xml
│   │       │   │   ├── fragment_home.xml
│   │       │   │   ├── fragment_preview.xml
│   │       │   │   ├── fragment_settings.xml
│   │       │   │   ├── item_recent_file.xml
│   │       │   │   ├── item_table_header.xml
│   │       │   │   └── item_table_row.xml
│   │       │   │
│   │       │   ├── menu/                   # Menu resources
│   │       │   │   └── home_menu.xml
│   │       │   │
│   │       │   ├── mipmap-*/               # Launcher icons (all densities)
│   │       │   │   ├── ic_launcher.png
│   │       │   │   ├── ic_launcher_round.png
│   │       │   │   └── ic_launcher_foreground.png
│   │       │   │
│   │       │   ├── navigation/             # Navigation graph
│   │       │   │   └── nav_graph.xml
│   │       │   │
│   │       │   ├── values/                 # Default values
│   │       │   │   ├── colors.xml
│   │       │   │   ├── ic_launcher_background.xml
│   │       │   │   ├── strings.xml
│   │       │   │   └── themes.xml
│   │       │   │
│   │       │   ├── values-night/           # Dark theme
│   │       │   │   └── themes.xml
│   │       │   │
│   │       │   └── xml/                    # XML configurations
│   │       │       ├── backup_rules.xml
│   │       │       └── data_extraction_rules.xml
│   │       │
│   │       └── AndroidManifest.xml         # App manifest
│   │
│   ├── build.gradle.kts                    # App module build config
│   └── proguard-rules.pro                  # ProGuard rules
│
├── gradle/                                 # Gradle wrapper and configs
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties
│   └── libs.versions.toml                  # Version catalog
│
├── DEVELOPMENT.md                          # Developer guide
├── PLAYSTORE_GUIDE.md                      # Play Store upload guide
├── README.md                               # Project documentation
├── build.gradle.kts                        # Root build config
├── gradle.properties                       # Gradle properties
├── gradlew                                 # Gradle wrapper (Unix)
├── gradlew.bat                             # Gradle wrapper (Windows)
├── settings.gradle.kts                     # Gradle settings
└── .gitignore                              # Git ignore rules
```

## Key Files Description

### Root Level
- `build.gradle.kts` - Project-level build configuration
- `settings.gradle.kts` - Defines project modules and repository sources
- `gradle.properties` - Gradle build properties (JVM args, build optimizations)
- `gradle/libs.versions.toml` - Centralized dependency version management

### App Module
- `app/build.gradle.kts` - App-specific build configuration, dependencies
- `app/proguard-rules.pro` - Code obfuscation and shrinking rules
- `AndroidManifest.xml` - App permissions, components, and metadata

### Source Code (`app/src/main/java/`)

#### Data Layer (`data/`)
- **database/** - Room database, DAOs, and entities
- **model/** - Data classes and domain models
- **repository/** - Repository pattern implementation

#### PDF Processing (`pdf/`)
- PDF rendering and OCR text extraction

#### UI Layer (`ui/`)
- **MainActivity.kt** - Single activity, hosts Navigation
- **home/** - Home screen with PDF picker and recent files
- **preview/** - Data preview with table and premium blur
- **export/** - CSV/Excel export functionality
- **billing/** - Google Play Billing integration
- **settings/** - App settings and info

#### Utilities (`utils/`)
- **BillingManager.kt** - In-app purchase management
- **DataCleaner.kt** - Data normalization and cleaning
- **ExportUtils.kt** - CSV and Excel export logic

#### Application (`GSTInvoiceApplication.kt`)
- Application class, initializes singletons

### Resources (`app/src/main/res/`)

#### Layouts (`layout/`)
- Activity and fragment layouts
- RecyclerView item layouts
- Material 3 components

#### Drawables (`drawable/`)
- Vector icons (Material Icons)
- Custom shapes and backgrounds

#### Navigation (`navigation/`)
- Navigation graph with SafeArgs

#### Values (`values/`, `values-night/`)
- Colors (Material 3 color system)
- Strings (all user-facing text)
- Themes (Light and Dark)

#### XML (`xml/`)
- Backup rules
- Data extraction rules

## Build Outputs

After building, outputs will be in:
```
app/build/outputs/
├── apk/
│   ├── debug/
│   │   └── app-debug.apk
│   └── release/
│       └── app-release.apk
└── bundle/
    └── release/
        └── app-release.aab
```

## Generated Files (Not in Git)

```
.gradle/                 # Gradle cache
.idea/                   # Android Studio config
app/build/               # Build outputs
local.properties         # SDK location (machine-specific)
*.iml                    # IntelliJ module files
```

---

**Total Files**: ~60+ source files (Kotlin, XML, resources)
**Lines of Code**: ~3,000+ LOC (approximate)
