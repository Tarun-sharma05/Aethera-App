package com.example.aethera.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.aethera.domain.models.CartItem
import com.example.aethera.ui.theme.AetheraTheme
import com.example.aethera.ui.theme.AmberGold
import org.koin.androidx.compose.koinViewModel

// ── Parent ──────────────────────────────────────────────────
@Composable
fun CartScreen(
    innerPadding   : PaddingValues,
    onCheckout     : () -> Unit,
    onProductClick : (String) -> Unit,
    viewModel      : CartViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    CartContent(
        innerPadding   = innerPadding,
        isLoading      = uiState.isLoading,
        items          = uiState.items,
        totalAmount    = uiState.totalAmount,
        onIncrement    = viewModel::increment,
        onDecrement    = viewModel::decrement,
        onRemove       = viewModel::remove,
        onCheckout     = onCheckout,
        onProductClick = onProductClick,
    )
}

// ── Content ──────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartContent(
    innerPadding   : PaddingValues,
    isLoading      : Boolean,
    items          : List<CartItem>,
    totalAmount    : Double,
    onIncrement    : (CartItem) -> Unit,
    onDecrement    : (CartItem) -> Unit,
    onRemove       : (String)   -> Unit,
    onCheckout     : () -> Unit,
    onProductClick : (String)   -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding),
    ) {
        TopAppBar(
            title = { Text("Shopping Bag", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
        )

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            items.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your bag is empty", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> {
                LazyColumn(
                    modifier           = Modifier.weight(1f),
                    contentPadding     = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(items, key = { it.productId }) { item ->
                        CartItemRow(
                            item        = item,
                            onIncrement = { onIncrement(item) },
                            onDecrement = { onDecrement(item) },
                            onRemove    = { onRemove(item.productId) },
                            onClick     = { onProductClick(item.productId) },
                        )
                    }
                }

                // ── Summary ───────────────────────────────
                Surface(
                    modifier  = Modifier.fillMaxWidth(),
                    color     = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 2.dp,
                ) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹${"%.0f".format(totalAmount)}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Shipping", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Calculated at checkout", style = MaterialTheme.typography.bodyMedium)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", style = MaterialTheme.typography.titleLarge)
                            Text("₹${"%.0f".format(totalAmount)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick  = onCheckout,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape    = MaterialTheme.shapes.medium,
                            colors   = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        ) {
                            Text("Checkout", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}

// ── Cart Item Row ─────────────────────────────────────────────
@Composable
fun CartItemRow(
    item        : CartItem,
    onIncrement : () -> Unit,
    onDecrement : () -> Unit,
    onRemove    : () -> Unit,
    onClick     : () -> Unit,
) {
    Row(
        modifier             = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment    = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model              = item.imageUrl,
            contentDescription = item.name,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier
                .size(88.dp)
                .clip(MaterialTheme.shapes.medium),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(item.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Outlined.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                }
            }
            Text("₹${"%.0f".format(item.price)}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            // Quantity stepper
            Row(
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier             = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainerLow, shape = CircleShape)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            ) {
                IconButton(onClick = onDecrement, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Outlined.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                }
                Text("${item.quantity}", style = MaterialTheme.typography.labelLarge)
                IconButton(onClick = onIncrement, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Outlined.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
private fun CartContentPreview() {
    AetheraTheme {
        CartContent(
            innerPadding   = PaddingValues(),
            isLoading      = false,
            items          = listOf(
                CartItem("1", "Lumina Trainer", "", 185.0, 1),
                CartItem("2", "Onyx Chrono",    "", 240.0, 2),
            ),
            totalAmount    = 665.0,
            onIncrement    = {},
            onDecrement    = {},
            onRemove       = {},
            onCheckout     = {},
            onProductClick = {},
        )
    }
}
