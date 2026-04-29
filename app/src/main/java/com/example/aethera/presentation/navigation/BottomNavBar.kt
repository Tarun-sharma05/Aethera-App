package com.example.aethera.presentation.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.aethera.ui.theme.AmberGold

private data class NavItem(
    val route        : Route,
    val label        : String,
    val selectedIcon : ImageVector,
    val unselected   : ImageVector,
)

private val navItems = listOf(
    NavItem(Route.Home,    "Home",    Icons.Filled.Home,        Icons.Outlined.Home),
    NavItem(Route.Search,  "Search",  Icons.Filled.Search,      Icons.Outlined.Search),
    NavItem(Route.Cart,    "Cart",    Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
    NavItem(Route.Profile, "Profile", Icons.Filled.Person,      Icons.Outlined.Person),
)

@Composable
fun BottomNavBar(
    currentRoute: Route,
    onNavigate  : (Route) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        navItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick  = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselected,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp),
                    )
                },
                label = {
                    Text(
                        text  = item.label,
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.onSurface,
                    selectedTextColor   = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor      = AmberGold.copy(alpha = 0.15f),
                ),
            )
        }
    }
}
