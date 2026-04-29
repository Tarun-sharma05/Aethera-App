package com.example.aethera.presentation.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aethera.domain.models.Order
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    innerPadding : PaddingValues,
    onOrderClick : (String) -> Unit,
    onBack       : () -> Unit,
    viewModel    : OrderViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Back") } },
            )
        },
        modifier = Modifier.padding(innerPadding),
    ) { pad ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            uiState.orders.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No orders yet", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> LazyColumn(modifier = Modifier.padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.orders, key = { it.orderId }) { order ->
                    OrderCard(order = order, onClick = { onOrderClick(order.orderId) })
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = MaterialTheme.shapes.large) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Order #${order.orderId.takeLast(8)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text("${order.items.size} item(s) · ₹${"%.0f".format(order.totalAmount)}", style = MaterialTheme.typography.bodyMedium)
            Text(order.status, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    innerPadding : PaddingValues,
    orderId      : String,
    onBack       : () -> Unit,
    viewModel    : OrderDetailViewModel = koinViewModel(parameters = { org.koin.core.parameter.parametersOf(orderId) }),
) {
    val uiState by viewModel.uiState.collectAsState()
    val order = uiState.order

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Detail", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Back") } },
            )
        },
        modifier = Modifier.padding(innerPadding),
    ) { pad ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            order == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Order not found") }
            else -> Column(modifier = Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Order #${order.orderId.takeLast(8)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
                Text("Status: ${order.status}", style = MaterialTheme.typography.bodyLarge)
                Text("Total: ₹${"%.0f".format(order.totalAmount)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                order.items.forEach { item ->
                    Text("${item.name} × ${item.quantity} — ₹${"%.0f".format(item.price * item.quantity)}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
