package com.example.aethera.presentation.shippingaddress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

// ── Colour tokens (matches the app's neutral surface palette) ─────────────────
private val BgTop    = Color(0xFFF8F8FF)
private val BgBottom = Color(0xFFEEEEF8)
private val Accent   = Color(0xFF5C6BC0)     // indigo-ish, used for the FAB / button

// ─────────────────────────────────────────────────────────────────────────────
// Stateful entry point
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Stateful entry point for the Shipping Address screen.
 *
 * Collects [ShippingAddressUiState] from [ShippingAddressViewModel] and drives
 * the [SnackbarHostState] from the one-shot [ShippingAddressUiState.isSaved] flag.
 * All rendering is delegated to the stateless [ShippingAddressContent].
 */
@Composable
fun ShippingAddressScreen(
    innerPadding : PaddingValues,
    onBack       : () -> Unit,
    viewModel    : ShippingAddressViewModel = koinViewModel(),
) {
    val uiState           by viewModel.uiState.collectAsState()
    val snackbarHostState  = remember { SnackbarHostState() }

    // One-shot: show the Snackbar once isSaved flips to true, then reset the flag.
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            snackbarHostState.showSnackbar("Address saved successfully! 🎉")
            viewModel.clearSavedFlag()
        }
    }

    ShippingAddressContent(
        state             = uiState,
        innerPadding      = innerPadding,
        snackbarHostState = snackbarHostState,
        onBack            = onBack,
        onSave            = viewModel::saveAddress,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Stateless content
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Stateless composable that renders the full Shipping Address UI.
 *
 * UI structure:
 * ```
 * Scaffold
 *   └── TopAppBar (back arrow + title)
 *   └── SnackbarHost
 *   └── Column (scrollable)
 *         ├── Hero card     (icon + tagline)
 *         ├── Address card  (labelled OutlinedTextField)
 *         └── Save button
 * ```
 *
 * @param state             Read-only snapshot of [ShippingAddressUiState].
 * @param innerPadding      Bottom padding from the host Scaffold (bottom bar height).
 * @param snackbarHostState Controlled by the stateful parent to decouple side effects.
 * @param onBack            Navigates back (pops the back stack).
 * @param onSave            Passes the typed address string to the ViewModel for persistence.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressContent(
    state             : ShippingAddressUiState,
    innerPadding      : PaddingValues,
    snackbarHostState : SnackbarHostState,
    onBack            : () -> Unit,
    onSave            : (String) -> Unit,
) {
    // Local draft text — ephemeral UI state, NOT in the ViewModel.
    // Pre-filled once when the persisted address first arrives from Firestore.
    var draftAddress by remember(state.address) { mutableStateOf(state.address) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                title = {
                    Text(
                        text       = "Shipping Address",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        containerColor = Color.Transparent,
    ) { scaffoldPadding ->

        // Full-screen gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgTop, BgBottom)))
                .padding(scaffoldPadding)
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {

            // Loading overlay — centred spinner while reading/writing Firestore
            AnimatedVisibility(
                visible = state.isLoading,
                enter   = fadeIn(),
                exit    = fadeOut(),
                modifier = Modifier.align(Alignment.Center),
            ) {
                CircularProgressIndicator(color = Accent)
            }

            // Main scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                // ── Hero card ───────────────────────────────────────────────
                HeroCard()

                // ── Address input card ──────────────────────────────────────
                AddressCard(
                    draft     = draftAddress,
                    onDraftChange = { draftAddress = it },
                    errorText = state.error,
                )

                // ── Save button ─────────────────────────────────────────────
                Button(
                    onClick  = { onSave(draftAddress) },
                    enabled  = !state.isLoading && draftAddress.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier           = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text       = "Save Address",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp,
                    )
                }

            } // Column
        } // Box
    } // Scaffold
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-components
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Decorative card displayed at the top of the screen to give context.
 */
@Composable
private fun HeroCard() {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = Accent.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier          = Modifier
                    .size(52.dp)
                    .background(Accent.copy(alpha = 0.18f), RoundedCornerShape(14.dp)),
                contentAlignment  = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Home,
                    contentDescription = null,
                    tint               = Accent,
                    modifier           = Modifier.size(28.dp),
                )
            }
            Column {
                Text(
                    text       = "Your Delivery Address",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text     = "We'll deliver your orders right to your door.",
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Card containing the address [OutlinedTextField].
 *
 * @param draft           The current in-progress text (local ephemeral state).
 * @param onDraftChange   Called on every keystroke to update the draft.
 * @param errorText       Non-null string from the ViewModel on a Firestore error.
 */
@Composable
private fun AddressCard(
    draft         : String,
    onDraftChange : (String) -> Unit,
    errorText     : String?,
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text       = "Address Details",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 13.sp,
                color      = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            OutlinedTextField(
                value          = draft,
                onValueChange  = onDraftChange,
                label          = { Text("Street, City, PIN Code") },
                placeholder    = { Text("e.g. 42 MG Road, Bangalore, 560001") },
                leadingIcon    = {
                    Icon(
                        imageVector        = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint               = Accent,
                    )
                },
                isError        = errorText != null,
                supportingText = if (errorText != null) {
                    { Text(errorText, color = MaterialTheme.colorScheme.error) }
                } else null,
                modifier       = Modifier.fillMaxWidth(),
                minLines       = 3,
                maxLines       = 5,
                shape          = RoundedCornerShape(12.dp),
                colors         = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Accent,
                    focusedLabelColor    = Accent,
                    focusedLeadingIconColor = Accent,
                ),
            )
        }
    }
}
