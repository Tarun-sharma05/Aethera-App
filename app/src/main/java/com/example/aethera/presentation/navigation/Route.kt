package com.example.aethera.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {

    // ── Auth flow (no bottom bar) ───────────────────────
    @Serializable data object Splash      : Route
    @Serializable data object Onboarding  : Route
    @Serializable data object Login       : Route
    @Serializable data object Signup      : Route

    // ── Bottom Nav tabs ─────────────────────────────────
    @Serializable data object Home        : Route
    @Serializable data object Search      : Route
    @Serializable data object Cart        : Route
    @Serializable data object Profile     : Route

    // ── Detail screens (no bottom bar) ──────────────────
    @Serializable data class  ProductDetail(val productId: String) : Route
    @Serializable data object Checkout    : Route
    @Serializable data object OrderHistory : Route
    @Serializable data class  OrderDetail(val orderId: String) : Route
    @Serializable data object Wishlist    : Route
}
