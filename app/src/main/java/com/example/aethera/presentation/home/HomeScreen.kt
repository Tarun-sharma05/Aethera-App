package com.example.aethera.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.aethera.domain.models.Category
import com.example.aethera.domain.models.Product
import com.example.aethera.ui.theme.AetheraTheme
import org.koin.androidx.compose.koinViewModel

// ── Parent ──────────────────────────────────────────────────
@Composable
fun HomeScreen(
    innerPadding    : PaddingValues,
    onProductClick  : (String) -> Unit,
    onWishlistClick : () -> Unit,
    viewModel       : HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        innerPadding    = innerPadding,
        isLoading       = uiState.isLoading,
        products        = uiState.products,
        categories      = uiState.categories,
        selectedCategory = uiState.selectedCategory,
        onProductClick  = onProductClick,
        onCategoryClick = viewModel::selectCategory,
        onWishlistClick = onWishlistClick,
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

// ── Product Card ─────────────────────────────────────────────
@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
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
