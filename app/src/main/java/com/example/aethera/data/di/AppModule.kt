package com.example.aethera.data.di

import com.example.aethera.data.repository.AuthRepositoryImpl
import com.example.aethera.data.repository.CartRepositoryImpl
import com.example.aethera.data.repository.OrderRepositoryImpl
import com.example.aethera.data.repository.ProductRepositoryImpl
import com.example.aethera.data.repository.UserRepositoryImpl
import com.example.aethera.data.repository.WishlistRepositoryImpl
import com.example.aethera.domain.repository.AuthRepository
import com.example.aethera.domain.repository.CartRepository
import com.example.aethera.domain.repository.OrderRepository
import com.example.aethera.domain.repository.ProductRepository
import com.example.aethera.domain.repository.UserRepository
import com.example.aethera.domain.repository.WishlistRepository
import com.example.aethera.presentation.auth.LoginViewModel
import com.example.aethera.presentation.auth.SignupViewModel
import com.example.aethera.presentation.cart.CartViewModel
import com.example.aethera.presentation.checkout.CheckoutViewModel
import com.example.aethera.presentation.home.HomeViewModel
import com.example.aethera.presentation.orders.OrderDetailViewModel
import com.example.aethera.presentation.orders.OrderViewModel
import com.example.aethera.presentation.product.ProductDetailViewModel
import com.example.aethera.presentation.profile.ProfileViewModel
import com.example.aethera.presentation.search.SearchViewModel
import com.example.aethera.presentation.settings.SettingsViewModel
import com.example.aethera.presentation.shippingaddress.ShippingAddressViewModel
import com.example.aethera.presentation.wishlist.WishlistViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // ── Firebase ────────────────────────────────────────
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }

    // ── Repositories ─────────────────────────────────────
    single<ProductRepository>  { ProductRepositoryImpl(get()) }
    single<AuthRepository>     { AuthRepositoryImpl(get(), get()) }
    single<CartRepository>     { CartRepositoryImpl(get()) }
    single<OrderRepository>    { OrderRepositoryImpl(get()) }
    single<UserRepository>     { UserRepositoryImpl(get()) }
    single<WishlistRepository> { WishlistRepositoryImpl(get()) }

    // ── ViewModels ───────────────────────────────────────
    viewModel { LoginViewModel(get()) }
    viewModel { SignupViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { (productId: String) -> ProductDetailViewModel(get(), get(), get(), productId) }
    viewModel { CartViewModel(get(), get()) }
    viewModel { CheckoutViewModel(get(), get(), get()) }
    viewModel { OrderViewModel(get()) }
    viewModel { (orderId: String) -> OrderDetailViewModel(get(), orderId) }
    viewModel { WishlistViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get()) } // Fix #3: inject orderRepository + wishlistRepository
    viewModel { SearchViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }              // UserRepository + AuthRepository
    viewModel { ShippingAddressViewModel(get()) }

}
