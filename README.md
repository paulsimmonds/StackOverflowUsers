# StackOverflow User List App: Architectural Overview

This document outlines the key architectural decisions and patterns used in this application. The architecture is designed to be robust, scalable, testable, and easy to maintain, following modern Android development best practices.

## Project Requirements

The goal was to build a simple Android application in Kotlin that:
- Connects to the public StackExchange API to retrieve user data.
- Supports Android API level 21 and above.
- Provides a responsive, non-blocking user experience through asynchronous operations.
- Follows a clean, testable architecture where all business logic is unit tested.
- Successfully compiles and passes all checks via the `./gradlew lint test assembleDebug` command.

### Screen Specifications:

1.  **Main Screen**: Displays a list of up to 20 users, sorted by reputation descending. Includes a manual search field to filter users by name via the external API, and a clear button to reset the results.
2.  **Detail Screen**: Accessed by tapping a user. Displays the user's Avatar, Name, Reputation, Top Tags, Badges (Gold/Silver/Bronze), Location, and Creation Date.

## Core Principles

- **Separation of Concerns**: Each part of the app has a distinct responsibility. This makes the code easier to understand, test, and modify without affecting other parts.
- **Unidirectional Data Flow (UDF) & MVI**: State flows down from the data layer to the UI, and user interactions are handled as discrete events (`UserListEvent`) flowing up to the ViewModel.
- **Dependency Injection**: Koin is used to manage dependencies, which promotes loose coupling and makes components easier to test and replace.

## 1. Clean Architecture: Data, Domain, and Presentation Layers

The project is structured into three main layers:

- **Presentation**: The UI layer (Jetpack Compose) responsible for displaying data and capturing user input. It uses an MVI-style pattern where the UI emits events and the ViewModel updates a single `StateFlow` of UI state.
- **Domain**: This is the core business logic of the app. It contains use cases (e.g., `GetUserListUseCase`) and the primary data models (`User`, `UserDetail`). Use cases are implemented using the `invoke` operator for idiomatic Kotlin usage.
- **Data**: This layer is responsible for providing data to the domain layer. It contains repositories and data sources (Retrofit) and handles implementation details like HTML entity decoding and data mapping.

## 2. Manual Search & Reactive Triggers

The search functionality is explicitly manual to reduce unnecessary API calls and provide a predictable user experience.
- **Trigger Mechanism**: The `UserListViewModel` uses a `MutableSharedFlow<Unit>(replay = 1)` as a trigger for search operations. This allows the `uiState` to react to "SEARCH" button clicks or "Clear" actions while ignoring intermediate typing in the text field.
- **Race Condition Protection**: Using `flatMapLatest` ensures that if multiple searches are triggered in rapid succession, only the latest one is processed, and previous ones are automatically cancelled.

## 3. Data Mapping & Sanitization

The application handles the transition from API DTOs to Domain Models in a dedicated mapping layer:
- **HTML Decoding**: Since the StackOverflow API can return HTML entities (e.g., `&#243;` for `ó`), the mappers use `HtmlCompat.fromHtml` to ensure all strings are display-ready before reaching the UI.
- **Extension Properties**: Common conversions, such as Unix timestamps to milliseconds, are handled via Kotlin extension properties (`asEpochMilliseconds`) for readability.

## 4. Abstracting the ImageLoader

Directly using an image loading library like Coil or Glide in Composables creates a hard dependency and mixes concerns. To avoid this, I've abstracted the image loading functionality behind an `ImageLoader` interface.
This allows for easy testing with a `FakeImageLoader` in Compose Previews and automated tests without requiring a network connection.

## 5. UI State Management: Loading, Success, Error

The UI models its state explicitly using sealed interfaces:
- **`Loading`**: Triggers a progress indicator.
- **`Success`**: Provides the data to display.
- **`Error`**: Displays a user-friendly error message.

By using `stateIn` with `SharingStarted.WhileSubscribed(5000)`, the application preserves state during configuration changes (like screen rotation) while efficiently releasing resources when the app is in the background.

## 6. Build & Test

To ensure code quality and correctness, the project can be fully built and verified by running:

```bash
./gradlew lint test assembleDebug
```

## 7. Future Improvements & Scalability

While the current implementation meets the core requirements, several areas could be enhanced for a production-grade application:

- **Offline Support & API Caching**: Implementing a local database (e.g., Room) would allow the app to cache user data and work offline. This would significantly reduce API calls and improve the user experience in low-connectivity areas.
- **Pagination**: Currently, the app only fetches the first page of results. Implementing "Infinite Scroll" or pagination logic in the `Repository` and `ViewModel` would allow users to browse more than 20 users.
- **Landscape & Large Screen Layouts**: The UI is currently optimized for portrait phone usage. Adding Adaptive Layouts or specific landscape `Composables` (e.g., using a Master-Detail view on tablets) would provide a better multi-device experience.
- **Advanced Error Handling**: Moving from simple error strings to a localized Error UI with "Retry" capabilities and more granular error types (Network vs. API vs. Parsing).
- **Navigation Animation**: Adding transition animations between the List and Detail screens using the Navigation Compose animation library.
