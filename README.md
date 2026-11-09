# SearchApp — Android Technical Challenge

An Android application built with Kotlin and Jetpack Compose that allows users to search for books using the Open Library API and view their details.

## Features

- Search books using a public REST API.
- Display search results with book titles and authors.
- View book details, including title, author, first publication year and edition count.
- Cancel previous searches when a new query is entered.
- Prevent outdated search results from updating the UI.
- Cache search results in memory for three minutes.
- Handle loading, empty queries and network errors.
- Unit tests for business logic, caching, repository behavior and search cancellation.

## Tech Stack

- Kotlin
- Jetpack Compose and Material 3
- MVVM and Clean Architecture
- Kotlin Coroutines and StateFlow
- Retrofit and Gson
- Hilt for dependency injection
- Navigation Compose
- JUnit, MockK and kotlinx-coroutines-test

## Architecture

The application follows Clean Architecture principles, organized into three logical layers within a single Gradle module.

**Presentation:** Jetpack Compose screens, SearchViewModel and UI state management.

**Domain:** Book model, BookRepository interface and SearchBooksUseCase.

**Data:** Retrofit API, DTOs, mappers, repository implementation and in-memory cache.

Hilt provides dependency injection and connects the repository interface with its implementation.

## API

The application uses the public Open Library Search API.

Base URL: https://openlibrary.org/

Example endpoint: https://openlibrary.org/search.json?q=kotlin&limit=20

Open Library was selected because it provides publicly accessible book information suitable for demonstrating search, asynchronous networking and detail navigation.

## Cancellation

SearchViewModel manages the active search using a coroutine Job within viewModelScope.

When the user changes the query or starts another search, the previous Job is cancelled.

An incremental search version is also checked before updating UI state, preventing outdated results from replacing newer ones.

CancellationException is propagated correctly.

## Caching

BookSearchCache provides an in-memory cache keyed by normalized search query.

- TTL: three minutes.
- Cache hit: return stored results without a new network request.
- Cache miss or expiration: fetch fresh data from the API.
- Query normalization: trim whitespace and ignore letter case.
- Thread safety: synchronized cache operations.
- Time measurement: monotonic clock.

The cache is intentionally memory-only, as persistent storage is not required for this challenge.

## Testing

The project includes 11 unit tests covering:

- Search use case validation and query normalization.
- Cache storage, retrieval, expiration and query independence.
- Repository API calls, cache hits and cache expiration.
- Cancellation of outdated searches in the ViewModel.

Run tests and build the debug APK:

```bash
./gradlew clean testDebugUnitTest assembleDebug
```

On Windows:

```powershell
.\gradlew.bat clean testDebugUnitTest assembleDebug
```

## Running the Application

1. Clone the repository.
2. Open the project in Android Studio.
3. Install Android SDK 36 if necessary.
4. Sync Gradle dependencies.
5. Run the application on an emulator or Android device with API 26 or later.

An Internet connection is required for uncached searches.

## Technical Decisions

- A single Gradle module was chosen to keep the technical challenge focused while preserving clear architectural boundaries through packages.
- Domain logic is independent of Android UI and networking frameworks.
- StateFlow provides observable UI state.
- Coroutine cancellation and search versioning protect against outdated results.
- In-memory caching avoids unnecessary API calls without introducing database complexity.
- The detail screen reuses the selected search result instead of performing an additional request.

## Potential Improvements

- Restore the selected book after process recreation using SavedStateHandle and navigation arguments.
- Add more UI tests.
- Improve accessibility and visual feedback.
- Add persistent caching for offline support.