
# SearchApp — Android Technical Challenge

SearchApp is a native Android application built with Kotlin and Jetpack Compose. It allows users to search for books using the public Open Library API, explore search results and view detailed bibliographic information.

The project focuses on clean architecture, reliable asynchronous operations, cancellation handling, in-memory caching, testability and maintainable code.

## Features

- Search books by title, author or keyword.
- Display book titles, authors, publication years and edition counts.
- Navigate to a detail screen with bibliographic information.
- Classify books by their first publication year.
- Cancel ongoing requests when the search query changes.
- Prevent outdated responses from overwriting newer results.
- Cache search results in memory for three minutes.
- Handle loading, empty results and network failures.
- Display specific messages for connection errors, timeouts and HTTP failures.
- Retry failed searches.
- Restore the selected book using SavedStateHandle.
- Support light and dark themes.
- Provide accessible loading feedback.

## Tech Stack

| Area | Technology |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | Clean Architecture, MVVM |
| Asynchronous operations | Kotlin Coroutines |
| State management | StateFlow |
| Networking | Retrofit, OkHttp, Gson |
| Dependency injection | Hilt |
| Navigation | Navigation Compose |
| Testing | JUnit, MockK, kotlinx-coroutines-test |
| CI/CD | GitHub Actions |
| Build system | Gradle |

## Architecture

The application follows Clean Architecture principles with three logical layers organized within a single Gradle module.

### Presentation

Contains Jetpack Compose screens, navigation, SearchViewModel and SearchUiState.

Responsibilities:
- Render application state.
- Handle user interactions.
- Manage search cancellation.
- Prevent outdated results from updating the UI.
- Classify network failures for user-facing feedback.
- Preserve the selected book across supported state restoration scenarios.

### Domain

Contains:
- Book and BookCategory models.
- BookRepository interface.
- SearchBooksUseCase.

Responsibilities:
- Define business models and repository contracts.
- Validate and normalize search input.
- Derive book classifications independently of the UI and networking implementation.

### Data

Contains:
- OpenLibraryApi.
- API DTOs and domain mappers.
- BookRepositoryImpl.
- BookSearchCache.
- Network configuration and dependency injection.

Responsibilities:
- Retrieve book information.
- Transform API responses into domain models.
- Reuse valid cached results.
- Avoid caching cancelled requests.

Dependency injection is handled by Hilt.

## Public API

SearchApp uses the Open Library Search API.

Base URL:

https://openlibrary.org/

Example request:

https://openlibrary.org/search.json?q=kotlin&limit=20

The application retrieves up to 20 results per search request.

Open Library was selected because it provides public bibliographic data without requiring authentication or API credentials.

## Search Cancellation and Concurrency

SearchViewModel manages the active search using a coroutine Job launched in viewModelScope.

When the user changes the query or starts another search:

1. The previous search Job is cancelled.
2. A search version counter is incremented.
3. A new search is started when appropriate.
4. Results are applied only if their search version is still current.

CancellationException is rethrown rather than treated as a regular network error.

The repository also checks coroutine cancellation before writing newly retrieved results to the cache.

This approach combines structured concurrency with explicit protection against outdated UI updates.

## In-Memory Cache

BookSearchCache stores search results using normalized queries as keys.

Configuration:
- Time-to-live: 3 minutes.
- Storage: in memory.
- Query normalization: trim whitespace and ignore letter case.
- Expiration measurement: monotonic clock.
- Thread safety: synchronized cache operations.

Behavior:
- Valid cache entry: return results without a network request.
- Missing entry: request data from Open Library.
- Expired entry: fetch fresh results and update the cache.
- Cancelled request: do not cache an incomplete result.

The cache is intentionally memory-only to avoid introducing unnecessary persistence complexity.

## Error Handling

The application distinguishes between:

- Connection and network failures.
- Request timeouts.
- HTTP response errors.
- Unexpected errors.

Each category is mapped to a user-facing message through Android string resources.

The interface also provides a retry action.

Network requests use explicit connection and read timeouts.

## Book Classification

SearchApp includes a small domain-level enrichment rule based on the first publication year:

| Publication year | Category |
| --- | --- |
| Before 1950 | Classic |
| 1950 or later | Contemporary |
| Unknown | Unclassified |

This is an application-defined demonstration rule, not an official literary classification.

The category is calculated from the existing domain model without additional API requests or stored fields.

## Testing

The project includes unit tests covering:

- Search query validation and normalization.
- In-memory cache hits and expiration.
- Cache behavior at the TTL boundary.
- Independent and normalized cache keys.
- Repository networking and cache integration.
- Cancellation without populating the cache.
- Cancellation of previous searches.
- Protection against outdated results.
- Selected book restoration.
- Blank search queries.
- Network, timeout, HTTP and unexpected error classification.
- Book classification and boundary conditions.

Run the unit tests:

```bash
./gradlew testDebugUnitTest
```

Build the debug APK:

```bash
./gradlew assembleDebug
```

Run both:

```bash
./gradlew testDebugUnitTest assembleDebug
```

On Windows:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

## Continuous Integration

GitHub Actions automatically validates the project using the Android CI workflow.

The workflow:
1. Checks out the repository.
2. Configures JDK 17.
3. Installs the required Android SDK components.
4. Runs the unit test suite.
5. Builds the debug APK.
6. Uploads test reports if validation fails.

Workflow file:

`.github/workflows/android-ci.yml`

## Running the Application

Requirements:
- Android Studio with Android SDK 36 installed.
- JDK 17.
- Android emulator or physical device running API 26 or later.
- Internet connection for uncached searches.

Steps:

1. Clone the repository:

   ```bash
   git clone https://github.com/unai01/SearchApp.git
   ```

2. Open the project in Android Studio.
3. Allow Gradle to synchronize dependencies.
4. Select an emulator or connected Android device.
5. Run the `app` configuration.

No API key or additional configuration is required.

## Technical Decisions

### Single Gradle module

The project uses one Gradle module with clearly separated architectural packages.

This keeps the technical challenge focused while preserving the responsibilities of each layer.

### MVVM and StateFlow

SearchViewModel exposes observable UI state through StateFlow, keeping the Compose screens focused on rendering and user interactions.

### Cancellation and stale-result protection

Coroutine Job cancellation is combined with a search version counter to prevent outdated requests from affecting the current UI.

### Memory-only caching

A short-lived cache reduces redundant network calls without introducing Room or persistent storage that the assignment does not require.

### Reusing selected search data

The detail screen uses the selected book instead of making an unnecessary additional API request.

Selected book fields are saved through SavedStateHandle.

### Domain-level enrichment

Book classification is calculated from existing publication data and can be tested independently of Android components.

### Scope management

Authentication, biometric access, encrypted storage and persistent offline caching were intentionally excluded because the application handles public bibliographic data and does not store sensitive user information.

## Possible Future Improvements

- Add Compose UI and navigation tests.
- Introduce pagination for larger result sets.
- Add persistent offline caching if product requirements justify it.
- Extend error handling with more specific HTTP status behavior.
- Introduce additional accessibility and localization testing.
