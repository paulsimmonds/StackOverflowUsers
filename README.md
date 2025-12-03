# StackOverflow User List App: Architectural Overview

This document outlines the key architectural decisions and patterns used in this application. The architecture is designed to be robust, scalable, testable, and easy to maintain, following modern Android development best practices.

## Core Principles

- **Separation of Concerns**: Each part of the app has a distinct responsibility. This makes the code easier to understand, test, and modify without affecting other parts.
- **Unidirectional Data Flow (UDF)**: State flows down from the data layer to the UI, and events flow up from the UI to the business logic. This creates a predictable and debuggable system.
- **Dependency Injection**: Koin is used to manage dependencies, which promotes loose coupling and makes components easier to test and replace.

## 1. Clean Architecture: Data, Domain, and Presentation Layers

The project is structured into three main layers:

- **Presentation**: The UI layer (Jetpack Compose) responsible for displaying data and capturing user input. It is kept as "dumb" as possible, reacting to state changes and forwarding events to the ViewModel.
- **Domain**: This is the core business logic of the app. It contains use cases and the primary data models. The domain layer is pure Kotlin and has no knowledge of the Android framework, making it highly testable and reusable.
- **Data**: This layer is responsible for providing data to the domain layer. It contains repositories and data sources (both network and local) and handles the implementation details of data fetching and storage.

This separation ensures that changes in one layer (e.g., swapping a network library or a database) have minimal impact on the others.

## 2. The Repository Pattern and the Single Source of Truth

The `UserRepository` acts as a mediator between the domain layer and the various data sources. Its key responsibility is to provide a single, consistent source of truth for user data.

### Why does the Local Data Source sit in the Data Layer?

A core requirement of the app is to display a list of users from the StackOverflow API while also showing a "follow" status that is decided locally and must persist between sessions. This presents a classic architectural challenge: how to combine ephemeral data from a network with persistent state from a local source.

The chosen design addresses this by making the `UserRepository` a **single source of truth** that intelligently combines data from two distinct sources:

1. **Network Data Source (`StackOverflowApiService`)**: Fetches the raw list of users from the API.
2. **Local Data Source (`FollowLocalDataSource`)**: Manages the set of followed user IDs, using `DataStore` to ensure this state persists across app sessions.

Placing the `FollowLocalDataSource` in the data layer is a deliberate choice. It treats local persistence as an **implementation detail**. The rest of the app doesn't need to know—or care—whether the follow status is stored in `DataStore`, `SharedPreferences`, or a `Room` database.

The repository's job is to abstract this complexity. It fetches the user list from the API, fetches the set of followed IDs from the local data source, and then merges them. The result is a single, clean `Flow<List<User>>` where each `User` object is a complete and consistent model, already containing the correct `isFollowing` status.

This design has several key advantages:

- **Meets Requirements Directly**: It provides a robust mechanism for persisting follow status between sessions.
- **Simplifies the UI**: The UI doesn't have to juggle two different data streams. It receives a simple `List<User>` and just needs to check the `user.isFollowing` property to decide whether to show a "Follow" or "Unfollow" button.
- **Promotes Clean Architecture**: The logic for combining data sources is neatly contained within the data layer, keeping the domain and presentation layers clean and focused on their own responsibilities.

## 3. Abstracting the ImageLoader

Directly using an image loading library like Coil or Glide in Composeables creates a hard dependency and mixes concerns. To avoid this, I've abstracted the image loading functionality behind an `ImageLoader` interface.

- **The Interface ('presentation/image/ImageLoader.kt')**: Defines a generic contract for loading an image.
- **The Implementation (`platform/CoilImageLoader.kt`)**: Provides a concrete implementation of the interface using Coil.

This approach allows easy swapping of the image loading library in the future without changing a single line of UI code. It also makes the UI components more testable, as you can provide a fake `ImageLoader` in the previews and tests.

## 4. UI State Management: Loading, Success, Error

The UI should be able to handle all possible states of a data-loading operation. A sealed class, `UserListUiState`, is used to model these states explicitly:

- **`Loading`**: Tells the UI to show a progress indicator.
- **`Success`**: Provides the UI with the data to display.
- **`Error`**: Gives the UI an error message to show to the user.

Using a sealed class ensures that all possible states are handled  in the UI code, which prevents bugs and makes the code more robust. The `ViewModel` is responsible for creating and exposing this state to the UI based on the results from the data layer, providing a clean and predictable way to manage the UI's appearance.
