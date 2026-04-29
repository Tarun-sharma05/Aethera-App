package com.example.aethera.presentation.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.aethera.ui.theme.AmberGold
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    innerPadding : PaddingValues,
    productId    : String,
    onBack       : () -> Unit,
    onAddToCart  : () -> Unit,
    viewModel    : ProductDetailViewModel = koinViewModel(parameters = { parametersOf(productId) }),
) {
    val uiState by viewModel.uiState.collectAsState()
    val product = uiState.product

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleWishlist) {
                        Icon(Icons.Outlined.FavoriteBorder, "Wishlist",
                            tint = if (uiState.isWishlisted) AmberGold else MaterialTheme.colorScheme.onSurface)
                    }
                },
            )
        },
        bottomBar = {
            Button(
                onClick  = { viewModel.addToCart(); onAddToCart() },
                enabled  = product != null && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                shape    = MaterialTheme.shapes.medium,
            ) { Text("Add to Cart", style = MaterialTheme.typography.labelLarge) }
        },
        modifier = Modifier.padding(innerPadding),
    ) { pad ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            product == null   -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Product not found") }
            else -> Column(modifier = Modifier.padding(pad).fillMaxSize()) {
                AsyncImage(
                    model = product.imageUrl, contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                )
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(product.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
                    Text("₹${"%.0f".format(if (product.finalPrice > 0) product.finalPrice else product.price)}",
                        style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(product.description, style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
