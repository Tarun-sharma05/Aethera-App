package com.example.aethera.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    innerPadding   : PaddingValues,
    onOrderHistory : () -> Unit,
    onWishlist     : () -> Unit,
    onLogout       : () -> Unit,
    viewModel      : ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) onLogout()
    }

    val user = uiState.user

    Column(
        modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TopAppBar(title = { Text("Profile", style = MaterialTheme.typography.titleLarge) })

        Spacer(Modifier.height(8.dp))
        Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)

        if (user != null) {
            Text(user.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
            Text(user.email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(onClick = onOrderHistory, modifier = Modifier.fillMaxWidth().height(52.dp), shape = MaterialTheme.shapes.medium) {
            Text("My Orders", style = MaterialTheme.typography.labelLarge)
        }
        OutlinedButton(onClick = onWishlist, modifier = Modifier.fillMaxWidth().height(52.dp), shape = MaterialTheme.shapes.medium) {
            Text("Wishlist", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(Modifier.weight(1f))

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
