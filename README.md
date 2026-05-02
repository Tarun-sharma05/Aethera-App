<p align="center">
  <img src="ic_launcher (1)/res/mipmap-xxxhdpi/ic_launcher.png" width="120" height="120" />
</p>

# Aethera — Premium E-Commerce Experience

Aethera is a modern, high-performance customer-facing Android application built with a focus on rich aesthetics and a robust, scalable architecture. It serves as the companion app to AetheraAdmin, offering a seamless shopping experience powered by Firebase and the latest Jetpack Compose technologies.

---

## 🎨 Visual Identity (Stitch Indigo)

Aethera follows the **Stitch Indigo** design system, utilizing Material 3 to create a premium, high-contrast, and intuitive user interface.

| Home Screen | Product Detail | Shopping Bag | Profile |
| :---: | :---: | :---: | :---: |
| ![Home](stitch_aethera_e_commerce_app_ui/stitch_aethera_e_commerce_app_ui/home_screen_2/screen.png) | ![Detail](stitch_aethera_e_commerce_app_ui/stitch_aethera_e_commerce_app_ui/product_detail_screen_2/screen.png) | ![Cart](stitch_aethera_e_commerce_app_ui/stitch_aethera_e_commerce_app_ui/cart_screen/screen.png) | ![Profile](stitch_aethera_e_commerce_app_ui/stitch_aethera_e_commerce_app_ui/profile_screen/screen.png) |

---

## 🚀 Key Features

- **Real-time Sync:** Powered by Firestore `snapshotListener`, ensuring product prices, availability, and categories are always up-to-date without manual refreshes.
- **Advanced Navigation:** Built with **Navigation 3**, utilizing polymorphic `@Serializable` routes for type-safe navigation and state preservation.
- **Secure Authentication:** Complete Email/Password authentication flow with persistent login sessions and user profile management.
- **Smart Cart & Wishlist:** Automated management of user-specific sub-collections with optimistic UI updates for a snappy experience.
- **Detailed Checkout:** A structured checkout process that generates unique order IDs and tracks order history in real-time.
- **Product Discovery:** Category-based filtering and instant client-side search across the product catalog.

---

## 🛠 Tech Stack & Tools

- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3.
- **Architecture:** MVVM + Clean Architecture with Feature-based partitioning.
- **Dependency Injection:** [Koin 4.0](https://insert-koin.io/) — Managed via `AppModule.kt` for repositories and ViewModels.
- **Navigation:** [Navigation 3](https://developer.android.com/guide/navigation/navigation-3) — High-level, type-safe navigation with `rememberNavBackStack`.
- **Backend:** [Firebase](https://firebase.google.com/) (Firestore, Auth, Storage).
- **Image Loading:** [Coil 3](https://coil-kt.github.io/coil/) — For efficient, coroutine-powered image rendering.
- **Serialization:** [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) for route definitions.
- **Concurrency:** Kotlin Coroutines & StateFlow for reactive data streams.

---

## 🏗 Holistic Architecture

The project adheres to **Clean Architecture** principles, ensuring a decoupled and testable codebase.

### 1. Domain Layer (Pure Kotlin)
Contains the core business logic and entities.
- **Entities:** `Product`, `Category`, `CartItem`, `Order`, `User`.
- **Repository Interfaces:** Defining the contract for data operations.

### 2. Data Layer
Handles all external data interactions.
- **Firestore Repositories:** Implements real-time data fetching using Kotlin Coroutines `callbackFlow`.
- **ResultState Wrapper:** A sealed class pattern (`Loading`, `Success`, `Error`) used across the app for consistent state handling.

### 3. Presentation Layer (Compose)
A fully reactive UI layer.
- **Parent/Content Pattern:** Decouples Logic/DI from UI components, making Composables easily previewable.
- **Navigation 3 Graph:** A polymorphic graph implementation in `AetheraNavGraph.kt` handling complex transitions and bottom bar logic.

---

## ⚙️ Getting Started

To run this project, you need to set up a Firebase project:

1.  Create a project on the [Firebase Console](https://console.firebase.google.com/).
2.  Add an Android App with the package name `com.example.aethera`.
3.  Download the `google-services.json` and place it in the `app/` directory.
4.  Enable **Email/Password** Authentication.
5.  Create a **Firestore Database** and set up the following collections: `CATEGORY`, `PRODUCTS`, `USERS`, `CART`, `ORDERS`, `WISHLIST`.
6.  (Optional) Add sample data to `PRODUCTS` and `CATEGORY` collections to see them in the app.

---

## 📁 Project Structure

```text
app/src/main/java/com/example/aethera/
├── common/             # Constants, ResultState, and Utility classes
├── data/
│   ├── di/             # Koin Modules (AppModule)
│   └── repository/     # Firestore & Auth Implementations
├── domain/
│   ├── models/         # Business Entities (Product, Order, etc.)
│   └── repository/     # Interface Definitions
├── presentation/       # Feature-based UI
│   ├── auth/           # Login & Signup screens
│   ├── home/           # Home & Category browsing
│   ├── cart/           # Cart management
│   ├── checkout/       # Order placement flow
│   ├── navigation/     # NavGraph and Route definitions
│   └── theme/          # Stitch Indigo Design Tokens (M3)
└── BaseApplication     # Koin & Global Initialization
```

---

## 🤝 Developed By
**Tarun Sharma** — [GitHub](https://github.com/Tarun-sharma05)
