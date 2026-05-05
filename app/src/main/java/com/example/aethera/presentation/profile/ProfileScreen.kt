package com.example.aethera.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

        Row(){
            InfoBox(title = "ORDERS", value = "2", modifier= Modifier.weight(1f))
              Spacer(modifier = Modifier.width(12.dp))

            InfoBox(title = "SAVES", value = "2", modifier= Modifier.weight(1f))
            Spacer(modifier = Modifier.width(12.dp))

            InfoBox(title = "POINTS", value = "2.5K", modifier= Modifier.weight(1f))
        }



        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onOrderHistory,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            Text("My Orders", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        Button(

            onClick = onWishlist,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingBag,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.padding(start = 4.dp, end = 16.dp))
            Text("Wishlist", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary )
        }
        Button(
            onClick = onWishlist,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ) {

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text("Shipping Addresses", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary )
        }
        Button(
            onClick = onWishlist,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ) {

            Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text("Payment Methods", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary )
        }
        Button(
            onClick = onWishlist,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Text("Settings", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary )
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

