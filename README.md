# Nexoft  Phone Contacts

A modern Android contacts management application built with Jetpack Compose and Clean Architecture principles.

## 📱 Features

### Contact Management
- ✅ Create, read, update, and delete contacts
- ✅ Add contact photos from camera or gallery
- ✅ Real-time photo preview with proper FileProvider URI handling
- ✅ Save contacts to device contacts
- ✅ Track which contacts are saved to device

### User Interface
- ✅ Modern Material Design 3 UI
- ✅ Swipe-to-edit and swipe-to-delete gestures
- ✅ Alphabetically grouped contact list
- ✅ Search functionality with history
- ✅ Pull-to-refresh
- ✅ Empty states and loading indicators
- ✅ Success/Error snackbar notifications
- ✅ Lottie animation success screen
- ✅ Seamless screen transitions (no jarring UI jumps)

### Search & Filter
- ✅ Real-time search by name
- ✅ Search history management
- ✅ Grouped search results display

### Photo Management
- ✅ Capture photos using camera
- ✅ Select photos from gallery
- ✅ Photo preview before saving
- ✅ Image validation (PNG/JPG only)
- ✅ Coil image loading with caching

## 🏗️ Architecture

This project follows **Clean Architecture** principles with clear separation of concerns, making the codebase scalable, testable, and maintainable.

### Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Layer                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Screens    │  │  ViewModels  │  │  Components  │  │
│  │   (Compose)  │  │   (State)    │  │     (UI)     │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                    Domain Layer                         │
│  ┌──────────────┐  ┌──────────────┐                     │
│  │  Use Cases   │  │ Repositories │                     │
│  │  (Business)  │  │ (Interfaces) │                     │
│  └──────────────┘  └──────────────┘                     │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                     Data Layer                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ Repositories │  │  API Service │  │     Room     │  │
│  │    (Impl)    │  │   (Remote)   │  │   (Local)    │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### Directory Structure

```
app/
├── data/
│   ├── local/                   # Local database (Room)
│   │   ├── ContactDao.kt        # Database operations
│   │   ├── ContactDatabase.kt   # Room database
│   │   └── ContactEntity.kt     # Database entities
│   ├── remote/                  # Network layer
│   │   ├── ContactApiService.kt # Retrofit API interface
│   │   ├── NetworkModule.kt     # Retrofit configuration
│   │   ├── dto/                 # Data Transfer Objects
│   │   └── mapper/              # DTO to Domain mappers
│   ├── repository/              # Repository implementations
│   │   ├── ContactRepositoryImpl.kt
│   │   └── PhotoRepositoryImpl.kt
│   └── model/                   # Data models
│       └── Contact.kt
│
├── domain/
│   ├── repository/              # Repository interfaces
│   │   ├── ContactRepository.kt
│   │   └── PhotoRepository.kt
│   └── usecase/                 # Business logic use cases
│       ├── GetContactsUseCase.kt
│       ├── CreateContactUseCase.kt
│       ├── UpdateContactUseCase.kt
│       ├── DeleteContactUseCase.kt
│       └── PhotoPickerUseCase.kt
│
└── presentation/
    ├── screens/                 # Main app screens
    │   ├── ContactsScreen.kt
    │   ├── ContactDetailsScreen.kt
    │   ├── AddContactScreen.kt
    │   └── ContactSuccessScreen.kt
    ├── components/              # Reusable UI components
    │   ├── ContactRow.kt
    │   ├── ContactList.kt
    │   ├── SearchBar.kt
    │   ├── PhotoSection.kt
    │   └── ... (25+ components)
    ├── navigation/              # Navigation & Screen management
    │   └── ContactsNavigation.kt
    ├── permissions/             # Permission handling
    │   └── PermissionHandler.kt
    ├── launcher/                # Camera/Gallery launchers
    │   └── LauncherSetup.kt
    ├── viewmodel/               # ViewModels
    │   └── ContactViewModel.kt
    ├── state/                   # UI State definitions
    │   ├── ContactUiState.kt
    │   └── ContactOperationState.kt
    ├── event/                   # UI Events
    │   └── ContactEvent.kt
    └── theme/                   # UI Theme
        ├── Color.kt
        ├── Type.kt
        └── Theme.kt
```

### Key Architectural Patterns

#### 1. MVVM (Model-View-ViewModel)
- **View (Compose)**: Observes state and renders UI
- **ViewModel**: Manages UI state and handles business logic
- **Model**: Data layer with repositories and data sources

```kotlin
// Unidirectional Data Flow
UI Event → ViewModel → Use Case → Repository → Data Source
                ↓
         State Update
                ↓
           UI Recomposition
```

#### 2. Modular Composables
Separation of concerns with specialized composable functions:

**PermissionHandler.kt**
- Centralized permission management
- Camera and Contacts permission handling
- Permission denied dialog with user-friendly messages
- Reusable across the app

**LauncherSetup.kt**
- Camera launcher configuration
- Gallery picker integration
- Photo repository initialization
- Automatic launcher lifecycle management

**ContactsNavigation.kt**
- Screen navigation logic
- State coordination between screens
- Seamless screen transitions
- Success/Error state handling

**MainActivity.kt** (Minimal)
- Clean activity setup with delegated responsibilities to specialized components

#### 3. Repository Pattern
Abstracts data sources (API + Local DB) from business logic:

```kotlin
interface ContactRepository {
    suspend fun getAllContacts(forceRefresh: Boolean): Result<List<Contact>>
    suspend fun createUser(contact: Contact): Result<Contact>
    // ...
}

class ContactRepositoryImpl(
    private val apiService: ContactApiService,
    private val contactDao: ContactDao
) : ContactRepository {
    // Implements caching strategy
    // Handles network and database operations
}
```

#### 4. Use Cases (Single Responsibility)
Each use case handles one specific business operation:

```kotlin
class CreateContactUseCase(private val repository: ContactRepository) {
    suspend operator fun invoke(contact: Contact): Result<Contact> {
        return repository.createUser(contact)
    }
}
```

#### 5. Dependency Injection (Hilt)
Automatic dependency management:

```kotlin
@HiltViewModel
class ContactViewModel @Inject constructor(
    private val repository: ContactRepository,
    private val getContactsUseCase: GetContactsUseCase,
    // ... other dependencies
) : ViewModel()
```

#### 6. State Management
Centralized state with Kotlin Flow:

```kotlin
// UI State
data class ContactUiState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// In ViewModel
private val _uiState = MutableStateFlow(ContactUiState())
val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()

// In Composable
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

### Data Flow Example: Creating a Contact

```
1. User fills form and clicks "Done"
   ↓
2. AddContactScreen sends event: ContactEvent.AddContact
   ↓
3. ContactViewModel receives event
   ↓
4. ViewModel calls CreateContactUseCase
   ↓
5. Use Case calls ContactRepository.createUser()
   ↓
6. Repository:
   - Validates image format (PNG/JPG only)
   - Uploads image to API (if exists)
   - Creates contact via API
   - Caches to Room database
   ↓
7. Repository returns Result<Contact>
   ↓
8. ViewModel updates operationState (isSuccess = true)
   ↓
9. ContactsNavigation detects success state
   ↓
10. AddContactScreen remains open (with loading state)
   ↓
11. ContactSuccessScreen shows on top (Lottie animation)
   ↓
12. After 2 seconds, both screens dismiss
   ↓
13. ContactsScreen shows with updated contact list
```

### Permission Flow Example

```
1. User taps camera button
   ↓
2. PermissionHandler.requestCameraPermission() called
   ↓
3. Check if permission already granted
   ├─ Yes → Execute onGranted callback
   └─ No → Request permission via launcher
          ↓
          ├─ Granted → Execute callback
          └─ Denied → Show AlertDialog with explanation
```

### Caching Strategy

- **Local First**: Always show cached data immediately
- **Background Sync**: Fetch from API in background
- **Cache Timeout**: 5 minutes cache validity
- **Offline Support**: Works without internet using cached data

```kotlin
override suspend fun getAllContacts(forceRefresh: Boolean): Result<List<Contact>> {
    val shouldFetchFromApi = forceRefresh || 
                             System.currentTimeMillis() - cacheTimestamp > CACHE_TIMEOUT
    
    if (shouldFetchFromApi) {
        // Fetch from API and update cache
    } else {
        // Return cached data
    }
}
```

### Error Handling

Centralized error handling with custom exceptions:

```kotlin
sealed class ApiException : Exception() {
    data class NetworkError(override val message: String) : ApiException()
    data class NotFound(override val message: String) : ApiException()
    data class ServerError(override val message: String) : ApiException()
    // ...
}
```

### Testing Strategy

The architecture is designed for testability:

- **ViewModels**: Testable with fake repositories
- **Use Cases**: Pure business logic testing
- **Repositories**: Mockable API and DAO
- **UI Components**: Compose UI testing

### Benefits of This Architecture

✅ **Separation of Concerns**: Each layer has a single responsibility  
✅ **Testability**: Easy to write unit and integration tests  
✅ **Scalability**: Easy to add new features without affecting existing code  
✅ **Maintainability**: Clear structure makes code easy to understand  
✅ **Flexibility**: Easy to swap implementations (e.g., different API)  
✅ **Reusability**: Components and use cases can be reused  
✅ **Offline First**: Works without internet connection

## 🛠️ Tech Stack

### Core
- **Kotlin**: 100% Kotlin
- **Jetpack Compose**: Modern declarative UI
- **Coroutines & Flow**: Asynchronous programming
- **Hilt**: Dependency injection

### Networking
- **Retrofit**: REST API client
- **OkHttp**: HTTP client
- **Gson**: JSON serialization

### Image Loading
- **Coil**: Image loading and caching
- **FileProvider**: Secure file URI handling

### Architecture Components
- **ViewModel**: UI state management
- **StateFlow**: Reactive state
- **Navigation**: Compose navigation
- **Lifecycle**: Lifecycle-aware components

### UI/UX
- **Material Design 3**: Modern UI components
- **Palette**: Dynamic color extraction
- **Activity Result API**: Photo capture and selection

## 📦 Key Dependencies

```gradle
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui-tooling-preview")

// Hilt Dependency Injection
implementation("com.google.dagger:hilt-android:2.50")
kapt("com.google.dagger:hilt-compiler:2.50")

// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Image Loading
implementation("io.coil-kt:coil-compose:2.5.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

## 🚀 Getting Started

1. Clone the repository
```bash
git clone https://github.com/yourusername/nexoft-contacts.git
```

2. Open the project in Android Studio

3. Sync Gradle dependencies

4. Run the app

### API Configuration

**Base URL**: `http://146.59.52.68:11235/`

#### Endpoints

```
GET    /api/User/GetAll
GET    /api/User/{id}
POST   /api/User
PUT    /api/User/{id}
DELETE /api/User/{id}
POST   /api/User/UploadImage
```

## 📸 Screenshots

[Add your app screenshots here]

## 🎯 Key Features Implementation

### Modular Architecture
- Clean separation of concerns with specialized components
- Centralized permission management
- Reusable photo picker and camera integration
- Coordinated screen navigation and state management

### Camera Integration
- Runtime permission handling for CAMERA permission
- FileProvider URI generation for secure photo storage
- ImageRequest.Builder for proper URI handling with Coil
- Real-time photo preview after capture
- Automatic launcher lifecycle management

### Permission Management
- Centralized permission handling composable
- User-friendly permission denied dialogs
- Permission state tracking
- Callback-based permission flow

### Swipe Gestures
- Swipe right → Edit contact (opens in edit mode)
- Swipe left → Delete contact (shows confirmation dialog)
- Smooth animations and reset behavior

### Contact Sync
- Track contacts saved to device using SharedPreferences
- Visual indicator for synced contacts
- One-tap save to device contacts
- Runtime WRITE_CONTACTS permission handling

### State Management
- Centralized state using StateFlow
- Separate UI state and operation state
- Loading, success, and error states
- Optimistic UI updates
- Seamless screen transitions

### Success Flow UX
- Add Contact screen stays open during save operation
- Success screen (Lottie animation) shows on top
- No jarring transitions to main screen
- Auto-dismiss after 2 seconds
- Clean state cleanup after completion

## 📝 Code Quality

### Best Practices
- ✅ Single Responsibility Principle
- ✅ Dependency Injection
- ✅ Proper error handling
- ✅ Code documentation
- ✅ Consistent naming conventions
- ✅ Clean code principles

### Design Patterns
- **Repository Pattern**: Data abstraction layer
- **Observer Pattern**: Flow/StateFlow for reactive state
- **Factory Pattern**: Hilt modules for dependency creation
- **Strategy Pattern**: Use cases for business logic
- **Composable Wrappers**: Reusable permission and launcher logic
- **State Hoisting**: Unidirectional data flow
- **Separation of Concerns**: Modular component design



