package com.example.aethera.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aethera.presentation.home.ProductCard
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    innerPadding   : PaddingValues,
    onProductClick : (String) -> Unit,
    viewModel      : SearchViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(innerPadding) ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query            = uiState.query,
                    onQueryChange    = viewModel::onQueryChange,
                    onSearch         = {},
                    expanded         = false,
                    onExpandedChange = {},
                    placeholder      = { Text("Search products…") },
                    leadingIcon      = { Icon(Icons.Outlined.Search, null) },
                    trailingIcon     = {
                        if (uiState.query.isNotEmpty()) {
                            IconButton(onClick = viewModel::clearQuery) { Icon(Icons.Outlined.Clear, null) }
                        }
                    },
                )
            },
            expanded         = false,
            onExpandedChange = {},
            modifier         = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        ) {}

        when {
            uiState.query.isBlank() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Search the collection", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            uiState.results.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No results for \"${uiState.query}\"", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> LazyVerticalGrid(
                columns               = GridCells.Fixed(2),
                contentPadding        = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement   = Arrangement.spacedBy(16.dp),
            ) {
                items(uiState.results, key = { it.id }) { product ->
                    ProductCard(product = product, onClick = { onProductClick(product.id) })
                }
            }
        }
    }
}
