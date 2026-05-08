package com.example.aethera.presentation.profile

import android.R.attr.bottom
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
//
//    LaunchedEffect(uiState.isLoggedOut) {
//        if (uiState.isLoggedOut) onLogout()
//    }
//
//    val user = uiState.user
//


       ProfileContent(
           state = uiState,
           innerPadding =  innerPadding,
           onOrderHistory = onOrderHistory,
           onWishlist = onWishlist,
           onLogout = onLogout,
           viewModel = viewModel,

       )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
state: ProfileUiState,
innerPadding   : PaddingValues,
onOrderHistory : () -> Unit,
onWishlist     : () -> Unit,
onLogout       : () -> Unit,
viewModel: ProfileViewModel = koinViewModel()
){
//    val uiState by viewModel.uiState.collectAsState()
//
//    LaunchedEffect(uiState.isLoggedOut) {
//        if (uiState.isLoggedOut) onLogout()
//    }
//
//    val user = state.user
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
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(scaffoldPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
//                Spacer(Modifier.height(8.dp))
                Personal_Info(state = state)

            }



            item {
                Spacer(Modifier.padding(12.dp))
                Row{
                    InfoBox(title = "ORDERS", value = "2", modifier= Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(12.dp))

                    InfoBox(title = "SAVES", value = "2", modifier= Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(12.dp))

                    InfoBox(title = "POINTS", value = "2.5K", modifier= Modifier.weight(1f))
                }
            }

            item {
                Spacer(Modifier.padding(12.dp))
                MenuColumnBox(
                    onClick = onOrderHistory,
                    title = "My Orders",
                    Icons.Default.ShoppingCart,
                    Icons.AutoMirrored.Filled.KeyboardArrowRight
                )
                MenuColumnBox(
                    onClick = onWishlist,
                    title = "Wishlist",
                    Icons.Filled.FavoriteBorder,
                    Icons.AutoMirrored.Filled.KeyboardArrowRight
                )
                MenuColumnBox(
                    onClick = onOrderHistory,
                    title = "Shipping Addresses",
                    Icons.Filled.LocationOn,
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                )
                MenuColumnBox(
                    onClick = onOrderHistory,
                    title = "Payment Methods",
                    Icons.Default.Payment,
                    Icons.AutoMirrored.Filled.KeyboardArrowRight
                )
                MenuColumnBox(
                    onClick = onOrderHistory,
                    title = "Settings",
                    Icons.Default.ShoppingCart,
                    Icons.AutoMirrored.Filled.KeyboardArrowRight
                )
            }

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

@Composable
fun Personal_Info(state: ProfileUiState){
    Column(
        modifier = Modifier.height(60.dp),
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
        } else if (state.isLoading) {
            CircularProgressIndicator()
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

