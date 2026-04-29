package com.example.aethera.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.example.aethera.presentation.auth.LoginScreen
import com.example.aethera.presentation.auth.SignupScreen
import com.example.aethera.presentation.cart.CartScreen
import com.example.aethera.presentation.checkout.CheckoutScreen
import com.example.aethera.presentation.home.HomeScreen
import com.example.aethera.presentation.orders.OrderDetailScreen
import com.example.aethera.presentation.orders.OrderHistoryScreen
import com.example.aethera.presentation.product.ProductDetailScreen
import com.example.aethera.presentation.profile.ProfileScreen
import com.example.aethera.presentation.search.SearchScreen
import com.example.aethera.presentation.splash.SplashScreen
import com.example.aethera.presentation.splash.OnboardingScreen
import com.example.aethera.presentation.wishlist.WishlistScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun AetheraNavGraph(startRoute: Route = Route.Splash) {

    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Splash::class,         Route.Splash.serializer())
                    subclass(Route.Onboarding::class,     Route.Onboarding.serializer())
                    subclass(Route.Login::class,          Route.Login.serializer())
                    subclass(Route.Signup::class,         Route.Signup.serializer())
                    subclass(Route.Home::class,           Route.Home.serializer())
                    subclass(Route.Search::class,         Route.Search.serializer())
                    subclass(Route.Cart::class,           Route.Cart.serializer())
                    subclass(Route.Profile::class,        Route.Profile.serializer())
                    subclass(Route.ProductDetail::class,  Route.ProductDetail.serializer())
                    subclass(Route.Checkout::class,       Route.Checkout.serializer())
                    subclass(Route.OrderHistory::class,   Route.OrderHistory.serializer())
                    subclass(Route.OrderDetail::class,    Route.OrderDetail.serializer())
                    subclass(Route.Wishlist::class,       Route.Wishlist.serializer())
                }
            }
        },
        startRoute
    )

    val currentRoute = backStack.last()

    val bottomNavRoutes: Set<Route> = setOf(
        Route.Home, Route.Search, Route.Cart, Route.Profile
    )
    val showBottomBar = currentRoute in bottomNavRoutes

    BackHandler(enabled = backStack.size > 1) { backStack.removeLastOrNull() }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute as Route,
                    onNavigate   = { route ->
                        // Single-top behaviour: pop existing copy before pushing
                        backStack.removeAll { it == route }
                        backStack.add(route)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = { key ->
                when (key) {
                    is Route.Splash -> NavEntry(key) {
                        SplashScreen(
                            onNavigateToHome  = {
                                backStack.clear()
                                backStack.add(Route.Home)
                            },
                            onNavigateToLogin = {
                                backStack.clear()
                                backStack.add(Route.Login)
                            }
                        )
                    }
                    is Route.Onboarding -> NavEntry(key) {
                        OnboardingScreen(
                            onGetStarted = {
                                backStack.removeLastOrNull()
                                backStack.add(Route.Login)
                            }
                        )
                    }
                    is Route.Login -> NavEntry(key) {
                        LoginScreen(
                            innerPadding   = innerPadding,
                            onLoginSuccess = {
                                backStack.clear()
                                backStack.add(Route.Home)
                            },
                            onNavigateToSignup = { backStack.add(Route.Signup) }
                        )
                    }
                    is Route.Signup -> NavEntry(key) {
                        SignupScreen(
                            innerPadding    = innerPadding,
                            onSignupSuccess = {
                                backStack.clear()
                                backStack.add(Route.Home)
                            },
                            onNavigateToLogin = { backStack.removeLastOrNull() }
                        )
                    }
                    is Route.Home -> NavEntry(key) {
                        HomeScreen(
                            innerPadding       = innerPadding,
                            onProductClick     = { productId -> backStack.add(Route.ProductDetail(productId)) },
                            onWishlistClick    = { backStack.add(Route.Wishlist) },
                        )
                    }
                    is Route.Search -> NavEntry(key) {
                        SearchScreen(
                            innerPadding   = innerPadding,
                            onProductClick = { productId -> backStack.add(Route.ProductDetail(productId)) }
                        )
                    }
                    is Route.Cart -> NavEntry(key) {
                        CartScreen(
                            innerPadding    = innerPadding,
                            onCheckout      = { backStack.add(Route.Checkout) },
                            onProductClick  = { productId -> backStack.add(Route.ProductDetail(productId)) }
                        )
                    }
                    is Route.Profile -> NavEntry(key) {
                        ProfileScreen(
                            innerPadding    = innerPadding,
                            onOrderHistory  = { backStack.add(Route.OrderHistory) },
                            onWishlist      = { backStack.add(Route.Wishlist) },
                            onLogout        = {
                                backStack.clear()
                                backStack.add(Route.Login)
                            }
                        )
                    }
                    is Route.ProductDetail -> NavEntry(key) {
                        ProductDetailScreen(
                            innerPadding = innerPadding,
                            productId    = key.productId,
                            onBack       = { backStack.removeLastOrNull() },
                            onAddToCart  = { backStack.removeLastOrNull() }
                        )
                    }
                    is Route.Checkout -> NavEntry(key) {
                        CheckoutScreen(
                            innerPadding    = innerPadding,
                            onOrderPlaced   = {
                                backStack.removeLastOrNull()
                                backStack.add(Route.OrderHistory)
                            },
                            onBack          = { backStack.removeLastOrNull() }
                        )
                    }
                    is Route.OrderHistory -> NavEntry(key) {
                        OrderHistoryScreen(
                            innerPadding   = innerPadding,
                            onOrderClick   = { orderId -> backStack.add(Route.OrderDetail(orderId)) },
                            onBack         = { backStack.removeLastOrNull() }
                        )
                    }
                    is Route.OrderDetail -> NavEntry(key) {
                        OrderDetailScreen(
                            innerPadding = innerPadding,
                            orderId      = key.orderId,
                            onBack       = { backStack.removeLastOrNull() }
                        )
                    }
                    is Route.Wishlist -> NavEntry(key) {
                        WishlistScreen(
                            innerPadding   = innerPadding,
                            onProductClick = { productId -> backStack.add(Route.ProductDetail(productId)) },
                            onBack         = { backStack.removeLastOrNull() }
                        )
                    }
                    else -> error("Unknown route: $key")
                }
            }
        )
    }
}
