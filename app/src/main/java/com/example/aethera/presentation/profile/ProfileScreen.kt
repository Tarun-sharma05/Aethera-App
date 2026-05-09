package com.example.aethera.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

/**
 * Main entry point for the Profile Screen.
 * Responsible for collecting state from the ViewModel and passing down UI events.
 * It delegates the actual rendering to the stateless [ProfileContent] component.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    innerPadding        : PaddingValues,
    onOrderHistory      : () -> Unit,
    onWishlist          : () -> Unit,
    onShippingAddresses : () -> Unit,
    onPaymentMethods    : () -> Unit,
    onSettings          : () -> Unit,
    onLogout            : () -> Unit,
    viewModel           : ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
//
//    LaunchedEffect(uiState.isLoggedOut) {
//        if (uiState.isLoggedOut) onLogout()
//    }
//
    val user = uiState.user
//

    ProfileContent(
        state               = uiState,
        innerPadding        = innerPadding,
        onOrderHistory      = onOrderHistory,
        onWishlist          = onWishlist,
        onShippingAddresses = onShippingAddresses,
        onPaymentMethods    = onPaymentMethods,
        onSettings          = onSettings,
        onLogout            = onLogout,
        viewModel           = viewModel,
    )
}

/**
 * Displays the main UI of the profile screen.
 * Wrapped in a Scaffold for consistent TopAppBar layout and
 * uses a LazyColumn to handle scrollable profile content smoothly.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    state               : ProfileUiState,
    innerPadding        : PaddingValues,
    onOrderHistory      : () -> Unit,
    onWishlist          : () -> Unit,
    onShippingAddresses : () -> Unit,
    onPaymentMethods    : () -> Unit,
    onSettings          : () -> Unit,
    onLogout            : () -> Unit,
    viewModel           : ProfileViewModel = koinViewModel()
) {
    // Fix #5: Local dialog visibility state — keeps ViewModel free of UI concerns.
    var showAddressDialog by remember { mutableStateOf(false) }

    // Fix #5: Show address edit dialog when triggered by Shipping Addresses menu item.
    if (showAddressDialog) {
        AddressEditDialog(
            currentAddress = state.user?.address ?: "",
            onDismiss      = { showAddressDialog = false },
            onSave         = { newAddress ->
                viewModel.updateAddress(newAddress)
                showAddressDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Menu, "Drop Down menu")
                    }
                },
                title = {
                    Text(
                        "Aethera",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                },
                actions = {
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(Icons.Filled.FavoriteBorder, contentDescription = "Heart icon")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onPrimary)
            )
        }
    ) { scaffoldPadding ->
        // Display loading indicator centered on screen if data is still fetching
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(scaffoldPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        // Main scrollable content container
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Block 1: User's Profile Image, Name, and Email
            item {
//                Spacer(Modifier.height(8.dp))
                Personal_Info(state = state)

            }



            // Block 2: Quick user statistics (Orders, Saves, Points)
            // Fix #3: Values now come from Firestore via ProfileViewModel instead of being hardcoded.
            item {
                Spacer(Modifier.padding(12.dp))
                Row {
                    InfoBox(
                        title    = "ORDERS",
                        value    = state.orderCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    InfoBox(
                        title    = "SAVES",
                        value    = state.wishlistCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    // TODO: Replace "—" with real points once a points/loyalty system is implemented.
                    InfoBox(
                        title    = "POINTS",
                        value    = "—",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Block 3: Vertical list of navigation menu options
            item {
                Spacer(Modifier.padding(12.dp))
                MenuColumnBox(
                    onClick = onOrderHistory,
                    title   = "My Orders",
                    icon    = Icons.Outlined.ShoppingCart,
                    trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight
                )
                MenuColumnBox(
                    onClick = onWishlist,
                    title   = "Wishlist",
                    icon    = Icons.Outlined.FavoriteBorder,
                    trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight
                )
                // Fix #5: Shipping Addresses now opens an edit dialog to manage the user's address.
                MenuColumnBox(
                    onClick = { showAddressDialog = true },
                    title   = "Shipping Addresses",
                    icon    = Icons.Outlined.LocationOn,
                    trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                )
                // Fix #1: Payment Methods now navigates to its own destination
                MenuColumnBox(
                    onClick = onPaymentMethods,
                    title   = "Payment Methods",
                    icon    = Icons.Outlined.Payment,
                    trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight
                )
                // Fix #1: Settings now navigates to its own destination
                MenuColumnBox(
                    onClick = onSettings,
                    title   = "Settings",
                    icon    = Icons.Outlined.Settings,
                    trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight
                )
            }

            // Block 4: Sign Out button
            item {
                Spacer(Modifier.padding(16.dp))
                Button(
                    onClick  = viewModel::logout,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = MaterialTheme.shapes.medium,
                    colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) {
                    Text("Sign Out", style = MaterialTheme.typography.labelLarge)
                }
            }


        }


    }
}

/**
 * Component to display the user's personal information block.
 * Shows an avatar placeholder along with the user's name, email, and address (if set).
 * Includes a loading fallback if data is still fetching.
 */
@Composable
fun Personal_Info(state: ProfileUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.Person,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (state.user != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                state.user.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                state.user.email,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // Fix #5: Show saved address below email so users know their current address at a glance.
            if (state.user.address.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector          = Icons.Outlined.LocationOn,
                        contentDescription   = "Address",
                        modifier             = Modifier.size(14.dp),
                        tint                 = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text  = state.user.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        } else if (state.isLoading) {
            CircularProgressIndicator()
        }
    }
}


/**
 * Reusable card component for displaying a specific statistic.
 * For example: "ORDERS" - "2".
 * 
 * @param title The label or title of the statistic (e.g., "POINTS").
 * @param value The actual value to display (e.g., "2.5K").
 * @param modifier Modifier for external styling and layout configuration.
 */
@Composable
fun InfoBox(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(90.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
//            .border(1.dp, Color.Black, RoundedCornerShape(8.dp)) // Added border for visibility
            .padding(15.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.DarkGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = value,
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}


/**
 * Reusable component for displaying a menu option row.
 * Includes a leading icon, a text label, and a trailing navigation icon.
 * Acts as a full-width clickable button.
 *
 * @param onClick Action to perform when the item is clicked.
 * @param title The label text for this menu option.
 * @param icon The leading icon indicating the option's purpose.
 * @param trailingIcon The icon at the end (usually an arrow) to indicate navigation.
 */
@Composable
fun MenuColumnBox(
    onClick: () -> Unit,
    title: String,
    icon: ImageVector,
    trailingIcon: ImageVector, modifier: Modifier = Modifier
) {
//    Box(
//        modifier = modifier
//            .height(90.dp)
//            .background(Color.White, shape = RoundedCornerShape(8.dp))
////            .border(1.dp, Color.Black, RoundedCornerShape(8.dp)) // Added border for visibility
////            .padding(15.dp)
//    ) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(60.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.White, // White background
            contentColor = Color.Black // Text/Icon color
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp) // No border/rounded corners
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.width(0.dp))
            Icon(imageVector = icon, contentDescription = null,  modifier = Modifier.padding(start = 12.dp))

            Text(
                text = title,
                color = Color.DarkGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
             Spacer(Modifier.padding(start = 24.dp, end = 0.dp))
            Icon(imageVector = trailingIcon, contentDescription = null,   modifier = Modifier.padding(end = 12.dp))
        }
    }
//    }
}


/**
 * Fix #5: Dialog that lets the user view and edit their saved shipping address.
 * Pre-fills with the current address from Firestore.
 * On save, the trimmed address is passed up to [ProfileViewModel.updateAddress].
 *
 * @param currentAddress The address currently stored in Firestore for this user.
 * @param onDismiss      Called when the user cancels without saving.
 * @param onSave         Called with the new address string when the user taps Save.
 */
@Composable
fun AddressEditDialog(
    currentAddress : String,
    onDismiss      : () -> Unit,
    onSave         : (String) -> Unit
) {
    var address by remember(currentAddress) { mutableStateOf(currentAddress) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon  = {
            Icon(
                imageVector        = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("Shipping Address") },
        text  = {
            OutlinedTextField(
                value         = address,
                onValueChange = { address = it },
                label         = { Text("Enter your full address") },
                placeholder   = { Text("e.g. 123 Main St, New Delhi") },
                leadingIcon   = {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null)
                },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = false,
                minLines      = 2,
            )
        },
        confirmButton = {
            TextButton(
                onClick  = { onSave(address) },
                enabled  = address.isNotBlank()
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
