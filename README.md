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

1.  **Main Screen**: Displays a list of up to 20 users, sorted alphabetically, with their reputation. Includes a search field to filter users by name.
2.  **Detail Screen**: Accessed by tapping a user. Displays the user's Avatar, Name, Reputation, Top Tags, Badges (Gold/Silver/Bronze), Location, and Creation Date.

## Core Principles

- **Separation of Concerns**: Each part of the app has a distinct responsibility. This makes the code easier to understand, test, and modify without affecting other parts.
- **Unidirectional Data Flow (UDF)**: State flows down from the data layer to the UI, and events flow up from the UI to the business logic. This creates a predictable and debuggable system.
- **Dependency Injection**: Koin is used to manage dependencies, which promotes loose coupling and makes components easier to test and replace.

## 1. Clean Architecture: Data, Domain, and Presentation Layers

The project is structured into three main layers:

- **Presentation**: The UI layer (Jetpack Compose) responsible for displaying data and capturing user input. It is kept as "dumb" as possible, reacting to state changes and forwarding events to the ViewModel.
- **Domain**: This is the core business logic of the app. It contains use cases (e.g., `GetUserListUseCase`) and the primary data models (`User`, `UserDetail`). The domain layer is pure Kotlin and has no knowledge of the Android framework, making it highly testable and reusable.
- **Data**: This layer is responsible for providing data to the domain layer. It contains repositories and data sources (network) and handles the implementation details of data fetching.

This separation ensures that changes in one layer (e.g., swapping a network library) have minimal impact on the others.

## 2. The Repository Pattern

The `UserRepository` acts as a mediator between the domain layer and the data sources. Its key responsibility is to provide a single, consistent source of truth for user data.

For the detail screen, the repository abstracts the complexity of fetching data from multiple API endpoints. It fetches user details and top tags concurrently and merges them into a single, clean `UserDetail` model for the domain layer.

## 3. Abstracting the ImageLoader

Directly using an image loading library like Coil or Glide in Composables creates a hard dependency and mixes concerns. To avoid this, I've abstracted the image loading functionality behind an `ImageLoader` interface.

- **The Interface (`presentation/image/ImageLoader.kt`)**: Defines a generic contract for loading an image.
- **The Implementation (`platform/CoilImageLoader.kt`)**: Provides a concrete implementation of the interface using Coil.

This approach allows easy swapping of the image loading library in the future without changing a single line of UI code. It also makes the UI components more testable, as you can provide a fake `ImageLoader` in the previews and tests.

## 4. UI State Management: Loading, Success, Error

The UI should be able to handle all possible states of a data-loading operation. A sealed class, `UserListUiState`, is used to model these states explicitly:

- **`Loading`**: Tells the UI to show a progress indicator.
- **`Success`**: Provides the UI with the data to display.
- **`Error`**: Gives the UI an error message to show to the user.

Using a sealed class ensures that all possible states are handled in the UI code, which prevents bugs and makes the code more robust. The `ViewModel` is responsible for creating and exposing this state to the UI based on the results from the data layer.

## 5. Build & Test

To ensure code quality, compliance, and correctness, the project can be fully built and verified by running the following command from the project root:

```bash
./gradlew lint test assembleDebug
```
