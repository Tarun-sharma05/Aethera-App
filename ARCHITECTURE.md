# 🏗️ Aethera — Architecture & Backend Structure

> **Purpose:** This document serves as the technical blueprint for the Aethera Android app.
> It covers the Clean Architecture layer breakdown, Navigation flow, and Firestore data schema.

---

## 1. Clean Architecture Overview

Aethera follows **Clean Architecture** with three distinct layers, ensuring separation of concerns and testability.

```
┌──────────────────────────────────────────────────┐
│              Presentation Layer                  │
│  (Jetpack Compose UI + ViewModel + Navigation)   │
├──────────────────────────────────────────────────┤
│                Domain Layer                      │
│        (Business Entities + Interfaces)          │
├──────────────────────────────────────────────────┤
│                 Data Layer                       │
│    (Firebase Implementations + DI Module)        │
└──────────────────────────────────────────────────┘
```

### Dependency Rule
> Dependencies only point **inward**. The Domain layer has **zero** knowledge of Firebase, Compose, or Koin.

---

## 2. Layer Breakdown

### 2.1 Domain Layer (`domain/`)
Pure Kotlin. No Android/Firebase imports.

| Component | File | Description |
|-----------|------|-------------|
| Entity | `Product.kt` | Core product data model |
| Entity | `Category.kt` | Product category |
| Entity | `CartItem.kt` | Item in a shopping cart |
| Entity | `Order.kt` | Placed order with status |
| Entity | `OrderItem.kt` | Line item inside an order |
| Entity | `User.kt` | Authenticated user profile |
| Interface | `ProductRepository.kt` | Product fetch contract |
| Interface | `AuthRepository.kt` | Login/Signup/Logout contract |
| Interface | `CartRepository.kt` | Cart CRUD contract |
| Interface | `OrderRepository.kt` | Order placement & history contract |
| Interface | `UserRepository.kt` | User profile read/write contract |
| Interface | `WishlistRepository.kt` | Wishlist add/remove/fetch contract |

---

### 2.2 Data Layer (`data/`)
Implements domain interfaces using Firebase SDK.

| Component | Description |
|-----------|-------------|
| `ProductRepositoryImpl` | Fetches products & categories from Firestore using `callbackFlow` + `snapshotListener` |
| `AuthRepositoryImpl` | Wraps `FirebaseAuth` for Email/Password login, signup, and session check |
| `CartRepositoryImpl` | Manages `USERS/{uid}/CART` sub-collection |
| `OrderRepositoryImpl` | Reads/writes to `ORDERS` top-level collection |
| `UserRepositoryImpl` | Reads/writes `USERS/{uid}` document |
| `WishlistRepositoryImpl` | Manages `USERS/{uid}/WISHLIST` sub-collection |
| `AppModule.kt` | Koin DI module — registers Firebase singletons, repositories, and ViewModels |

#### State Handling Pattern
All async operations return a `ResultState<T>` sealed class:
```kotlin
sealed class ResultState<out T> {
    data object Loading : ResultState<Nothing>()
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val message: String) : ResultState<Nothing>()
}
```

---

### 2.3 Presentation Layer (`presentation/`)
Feature-based partitioning. Each feature follows the **Parent/Content Pattern**:

```
Feature/
├── FeatureScreen.kt      ← Parent: collects ViewModel state, handles events
├── FeatureViewModel.kt   ← Holds StateFlow<UiState>, calls repository
└── FeatureUiState.kt     ← Data class holding all UI state
```

#### Feature Modules

| Feature | Screens | ViewModel |
|---------|---------|-----------|
| `auth` | `LoginScreen`, `SignupScreen` | `LoginViewModel`, `SignupViewModel` |
| `home` | `HomeScreen` | `HomeViewModel` |
| `product` | `ProductDetailScreen` | `ProductDetailViewModel` |
| `search` | `SearchScreen` | `SearchViewModel` |
| `cart` | `CartScreen` | `CartViewModel` |
| `checkout` | `CheckoutScreen` | `CheckoutViewModel` |
| `orders` | `OrderHistoryScreen`, `OrderDetailScreen` | `OrderViewModel`, `OrderDetailViewModel` |
| `wishlist` | `WishlistScreen` | `WishlistViewModel` |
| `profile` | `ProfileScreen` | `ProfileViewModel` |
| `shippingaddress` | `ShippingAddressScreen` | `ShippingAddressViewModel` |
| `settings` | `SettingsScreen` | `SettingsViewModel` |
| `splash` | `SplashScreen`, `OnboardingScreen` | _(no ViewModel)_ |

---

## 3. App Navigation Flow

Built with **Navigation 3** (`androidx.navigation3`). Routes are `@Serializable` data objects for type-safety.

### Route Definitions
```
Route (sealed interface)
├── Splash
├── Onboarding
├── Login
├── Signup
├── Home                   ← Bottom Nav
├── Search                 ← Bottom Nav
├── Cart                   ← Bottom Nav
├── Profile                ← Bottom Nav
├── ProductDetail(productId: String)
├── Checkout
├── OrderHistory
├── OrderDetail(orderId: String)
├── Wishlist
├── ShippingAddress
└── Settings
```

### Navigation Flow Diagram

```
[App Launch]
     │
     ▼
[SplashScreen]
     │
     ├── User logged in ──────────────────────► [HomeScreen]
     │                                               │
     └── Not logged in                               ├── Product tap ──► [ProductDetailScreen]
              │                                      │                        │
              ▼                                      │                   Add to Cart
         [LoginScreen]                               │
              │                                      ├── Wishlist icon ──► [WishlistScreen]
              ├── Success ──► [HomeScreen]            │
              │                                      ├── [SearchScreen]
              └── No account ──► [SignupScreen]       │
                                      │              ├── [CartScreen]
                                      └──► [Home]    │        │
                                                     │    Checkout ──► [CheckoutScreen]
                                           [ProfileScreen]         │
                                                │              Order placed ──► [OrderHistoryScreen]
                                                ├── My Orders ──► [OrderHistoryScreen]           │
                                                │                      │                    [OrderDetailScreen]
                                                ├── Wishlist ──► [WishlistScreen]
                                                │
                                                ├── Shipping Addresses ──► [ShippingAddressScreen]
                                                │
                                                ├── Payment Methods ──► (Coming Soon)
                                                │
                                                └── Settings ──► [SettingsScreen]
                                                                      │
                                                                  Logout ──► [LoginScreen]
```

### Bottom Navigation Bar
Visible **only** on these 4 routes: `Home`, `Search`, `Cart`, `Profile`.

### Back Stack Behaviour
- **Single-top**: Bottom nav items remove existing copy before pushing (no duplicates).
- **Clear on Auth**: Login/Logout clears the entire back stack before navigating.

---

## 4. Firestore Backend Structure

### Collection Schema

```
Firestore Root
│
├── PRODUCTS/                          ← Top-level product catalog
│   └── {productId}/
│       ├── id            : String
│       ├── name          : String
│       ├── description   : String
│       ├── categoryName  : String
│       ├── price         : Double
│       ├── finalPrice    : Double
│       ├── stockQuantity : Int
│       ├── imageUrl      : String
│       ├── isActive      : Boolean
│       ├── createdAt     : Long (epoch ms)
│       └── updatedAt     : Long (epoch ms)
│
├── CATEGORY/                          ← Product categories
│   └── {categoryId}/
│       ├── id       : String
│       ├── name     : String
│       └── imageUrl : String
│
├── USERS/                             ← User profiles
│   └── {uid}/
│       ├── uid       : String
│       ├── name      : String
│       ├── email     : String
│       ├── phone     : String
│       ├── address   : String
│       └── createdAt : Long
│
├── CART/                              ← Per-user cart items (sub-collection)
│   └── {userId}/
│       └── items/
│           └── {productId}/
│               ├── productId : String
│               ├── name      : String
│               ├── imageUrl  : String
│               ├── price     : Double
│               └── quantity  : Int
│
├── ORDERS/                            ← Top-level order history
│   └── {orderId}/
│       ├── orderId       : String
│       ├── userId        : String
│       ├── totalAmount   : Double
│       ├── status        : String  (Pending | Processing | Shipped | Delivered | Cancelled)
│       ├── paymentStatus : String  (Unpaid | Paid)
│       ├── createdAt     : Long
│       └── items         : List<OrderItem>
│           └── { productId, name, imageUrl, price, quantity }
│
└── WISHLIST/                          ← Per-user saved products
    └── {userId}/
        └── items/
            └── {productId}/
                └── { ...product fields }
```

### Real-time Listeners
The following collections use Firestore `snapshotListener` for live updates (no manual refresh needed):
- `PRODUCTS` — Product catalog on `HomeScreen`
- `CART/{uid}/items` — Cart badge count + `CartScreen`
- `ORDERS` — Order history on `OrderHistoryScreen`
- `WISHLIST/{uid}/items` — Wishlist count on `ProfileScreen`

---

## 5. Dependency Injection (Koin)

All DI is managed in a **single module** (`AppModule.kt`).

```
appModule
 ├── single: FirebaseFirestore.getInstance()
 ├── single: FirebaseAuth.getInstance()
 │
 ├── single<ProductRepository>  → ProductRepositoryImpl(firestore)
 ├── single<AuthRepository>     → AuthRepositoryImpl(firestore, auth)
 ├── single<CartRepository>     → CartRepositoryImpl(firestore)
 ├── single<OrderRepository>    → OrderRepositoryImpl(firestore)
 ├── single<UserRepository>     → UserRepositoryImpl(firestore)
 ├── single<WishlistRepository> → WishlistRepositoryImpl(firestore)
 │
 ├── viewModel: LoginViewModel(auth)
 ├── viewModel: SignupViewModel(auth)
 ├── viewModel: HomeViewModel(product, auth)
 ├── viewModel: ProductDetailViewModel(product, cart, wishlist, productId)
 ├── viewModel: CartViewModel(cart, auth)
 ├── viewModel: CheckoutViewModel(cart, order, auth)
 ├── viewModel: OrderViewModel(order)
 ├── viewModel: OrderDetailViewModel(order, orderId)
 ├── viewModel: WishlistViewModel(wishlist, auth)
 ├── viewModel: ProfileViewModel(user, auth, order, wishlist)
 ├── viewModel: SearchViewModel(product)
 ├── viewModel: SettingsViewModel(user, auth)
 └── viewModel: ShippingAddressViewModel(user)
```

---

## 6. Concurrency Model

| Tool | Usage |
|------|-------|
| **Kotlin Coroutines** | All repository calls are `suspend` functions |
| **`callbackFlow`** | Bridges Firestore `snapshotListener` callbacks to Kotlin Flow |
| **`StateFlow`** | ViewModels expose `StateFlow<UiState>` collected by Compose |
| **`collectAsState()`** | Compose side collects StateFlow in the UI thread |

---

*Last updated: May 2026 | Developed by Tarun Sharma*
