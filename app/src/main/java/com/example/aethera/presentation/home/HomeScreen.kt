package com.example.aethera.presentation.home

import android.R.attr.label
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.aethera.domain.models.Category
import com.example.aethera.domain.models.Product
import com.example.aethera.ui.theme.AetheraTheme
import org.koin.androidx.compose.koinViewModel

private const val TAG = "HomeScreen"

// ── Parent ──────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    innerPadding    : PaddingValues,
    onProductClick  : (String) -> Unit,
    onWishlistClick : () -> Unit,
    viewModel       : HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // ── UI-layer state observation logs ───────────────────
    LaunchedEffect(uiState.isLoading) {
        Log.d(TAG, "[UiState] isLoading=${uiState.isLoading}")
    }
    LaunchedEffect(uiState.products) {
        Log.d(TAG, "[UiState] products updated → count=${uiState.products.size}")
        uiState.products.forEachIndexed { i, p ->
            Log.v(TAG, "  [$i] id=${p.id}, name=${p.name}, category=${p.categoryName}, price=${p.price}")
        }
    }
    LaunchedEffect(uiState.categories) {
        Log.d(TAG, "[UiState] categories updated → count=${uiState.categories.size}")
    }
    LaunchedEffect(uiState.error) {
        uiState.error?.let { Log.e(TAG, "[UiState] error received → $it") }
    }

    HomeContent(
        innerPadding     = innerPadding,
        isLoading        = uiState.isLoading,
        products         = uiState.products,
        categories       = uiState.categories,
        selectedCategory = uiState.selectedCategory,
        onProductClick   = onProductClick,
        onCategoryClick  = viewModel::selectCategory,
        onWishlistClick  = onWishlistClick,
    )
}

// ── Content (pure UI, @Preview-able) ────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    innerPadding     : PaddingValues,
    isLoading        : Boolean,
    products         : List<Product>,
    categories       : List<Category>,
    selectedCategory : String,
    onProductClick   : (String) -> Unit,
    onCategoryClick  : (String) -> Unit,
    onWishlistClick  : () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding),
    ) {
        // ── Top App Bar ───────────────────────────────────
        TopAppBar(
            title = {
                Text(
                    text       = "AETHERA",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified,
                )
            },
            actions = {
                IconButton(onClick = onWishlistClick) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Wishlist")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        )

        // ── Category Chips ────────────────────────────────
        LazyRow(
            modifier            = Modifier.fillMaxWidth(),
            contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                CategoryChip(
                    name     = "All",
                    selected = selectedCategory == "All",
                    onClick  = { onCategoryClick("All") },
                )
            }
            items(categories) { cat ->
                CategoryChip(
                    name     = cat.name,
                    selected = selectedCategory == cat.name,
                    onClick  = { onCategoryClick(cat.name) },
                )
            }
        }

        //Ad Banner
        Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            ProductBannerCard()
        }

        // ── Product Grid ──────────────────────────────────
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            products.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No products found", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns                = GridCells.Fixed(2),
                    modifier               = Modifier.fillMaxSize(),
                    contentPadding         = PaddingValues(16.dp),
                    horizontalArrangement  = Arrangement.spacedBy(12.dp),
                    verticalArrangement    = Arrangement.spacedBy(16.dp),
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductCard(product = product, onClick = { onProductClick(product.id) })
                    }
                }
            }
        }
    }
}

// ── Banner Card ──────────────────────────────────────────────
@Composable
fun ProductBannerCard(
    label: String = "NEW SEASON",
    title: String = "Essential Minimalism",
    modifier: Modifier = Modifier
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape     = MaterialTheme.shapes.extraLarge,
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),

    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ){
            Text(
                text = label,
                style = TextStyle(
                    color = Color(0xFF9CA3AF), // Muted gray for label
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            )
        }
    }

}



// ── Product Card ─────────────────────────────────────────────
@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Log.v(TAG, "ProductCard → rendering id=${product.id}, name=${product.name}")
    Card(
        modifier  = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape     = MaterialTheme.shapes.large,
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            AsyncImage(
                model             = product.imageUrl,
                contentDescription = product.name,
                contentScale      = ContentScale.Crop,
                modifier          = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(MaterialTheme.shapes.large),

            )
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text     = product.name,
                    style    = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color    = MaterialTheme.colorScheme.onSurface,
                )
                val displayPrice = if (product.finalPrice > 0.0) product.finalPrice else product.price
                Text(
                    text  = "₹${"%.0f".format(displayPrice)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

// ── Category Chip ─────────────────────────────────────────────
@Composable
fun CategoryChip(name: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick  = onClick,
        label    = { Text(name, style = MaterialTheme.typography.labelLarge) },
        shape    = CircleShape,
    )
}

// ── Previews ──────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    AetheraTheme {
        val sampleProducts = listOf(
            Product(id = "1", name = "Structured Wool Coat", price = 340.0, finalPrice = 299.0, categoryName = "Clothing"),
            Product(id = "2", name = "Artisan Ceramic Vase", price = 85.0, finalPrice = 0.0, categoryName = "Home"),
        )
        val sampleCategories = listOf(
            Category(id = "c1", name = "Clothing"),
            Category(id = "c2", name = "Home"),
        )
        HomeContent(
            innerPadding     = PaddingValues(),
            isLoading        = false,
            products         = sampleProducts,
            categories       = sampleCategories,
            selectedCategory = "All",
            onProductClick   = {},
            onCategoryClick  = {},
            onWishlistClick  = {},
        )
    }
}
