package com.example.aethera.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aethera.BuildConfig
import org.koin.compose.viewmodel.koinViewModel

// ── Design tokens ────────────────────────────────────────────────────────────
private val BgTop      = Color(0xFFF8F8FF)
private val BgBottom   = Color(0xFFEEEEF8)
private val Accent     = Color(0xFF5C6BC0)
private val DangerRed  = Color(0xFFE53935)
private val SectionLabelColor = Color(0xFF7986CB)

// ── Constants ─────────────────────────────────────────────────────────────────
private const val PRIVACY_URL = "https://example.com/privacy"  // TODO: replace with real URL
private const val TERMS_URL   = "https://example.com/terms"    // TODO: replace with real URL

// ─────────────────────────────────────────────────────────────────────────────
// Stateful entry point
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Stateful entry point for the Settings screen.
 *
 * Collects [SettingsUiState] from [SettingsViewModel] and drives two side effects:
 * - [SettingsUiState.isSaved]     → shows "Saved!" Snackbar once then resets
 * - [SettingsUiState.isLoggedOut] → calls [onLogout] to pop the back stack to Login
 */

@Composable
fun SettingsScreen(
    onBack    : () -> Unit,
    onLogout  : () -> Unit = {},
    viewModel : SettingsViewModel = koinViewModel(),
) {
    val uiState           by viewModel.uiState.collectAsState()
    val snackbarHostState  = remember { SnackbarHostState() }

    // One-shot: Snackbar after a successful save
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            snackbarHostState.showSnackbar("Changes saved ✓")
            viewModel.clearSavedFlag()
        }
    }

    // One-shot: navigate to Login after logout
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) onLogout()
    }

    SettingsContent(
        state             = uiState,
        snackbarHostState = snackbarHostState,
        onBack            = onBack,
        onSaveName        = viewModel::updateName,
        onSavePhone       = viewModel::updatePhone,
        onLogout          = viewModel::logout,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Stateless content
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Stateless composable that renders the full Settings UI.
 *
 * Layout:
 * ```
 * Scaffold
 *   TopAppBar
 *   SnackbarHost
 *   LazyColumn
 *     ├── Avatar + email hero
 *     ├── SectionHeader("ACCOUNT")
 *     ├── EditableProfileRow(name)
 *     ├── ReadOnlyRow(email)
 *     ├── EditableProfileRow(phone)
 *     ├── SectionHeader("ABOUT")
 *     ├── InfoRow(version)
 *     ├── LinkRow(Privacy Policy)
 *     ├── LinkRow(Terms of Service)
 *     ├── SectionHeader("ACCOUNT ACTIONS")
 *     └── DangerRow(Sign Out)
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state             : SettingsUiState,
    snackbarHostState : SnackbarHostState,
    onBack            : () -> Unit,
    onSaveName        : (String) -> Unit,
    onSavePhone       : (String) -> Unit,
    onLogout          : () -> Unit,
) {
    val context = LocalContext.current

    // Confirmation dialog state for Sign Out
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        text       = "Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        containerColor = Color.Transparent,
    ) { scaffoldPadding ->

        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgTop, BgBottom)))
                .padding(scaffoldPadding),
        ) {

            // Global loading overlay
            AnimatedVisibility(
                visible  = state.isLoading,
                enter    = fadeIn(),
                exit     = fadeOut(),
                modifier = Modifier.align(Alignment.Center),
            ) {
                CircularProgressIndicator(color = Accent)
            }

            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {

                // ── Avatar hero ──────────────────────────────────────────────
                item {
                    AvatarHero(
                        name  = state.name,
                        email = state.email,
                    )
                    Spacer(Modifier.height(20.dp))
                }

                // ── Account section ──────────────────────────────────────────
                item { SectionHeader("ACCOUNT") }

                item {
                    SettingsCard {
                        EditableProfileRow(
                            label         = "Display Name",
                            value         = state.name,
                            icon          = Icons.Outlined.Person,
                            keyboardType  = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words,
                            onSave        = onSaveName,
                        )
                        SettingsDivider()
                        ReadOnlyRow(
                            label = "Email",
                            value = state.email,
                            icon  = Icons.Outlined.Email,
                        )
                        SettingsDivider()
                        EditableProfileRow(
                            label        = "Phone",
                            value        = state.phone,
                            icon         = Icons.Outlined.Phone,
                            keyboardType = KeyboardType.Phone,
                            onSave       = onSavePhone,
                        )
                    }
                }

                // Error card (shown inline if Firestore fails)
                if (state.error != null) {
                    item {
                        Text(
                            text     = state.error,
                            color    = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }

                // ── About section ────────────────────────────────────────────
                item { SectionHeader("ABOUT") }

                item {
                    SettingsCard {
                        InfoRow(
                            icon  = Icons.Outlined.Info,
                            label = "App Version",
                            value = BuildConfig.VERSION_NAME,
                        )
                        SettingsDivider()
                        LinkRow(
                            icon  = Icons.Outlined.Lock,
                            label = "Privacy Policy",
                        ) {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL))
                            )
                        }
                        SettingsDivider()
                        LinkRow(
                            icon  = Icons.Outlined.Description,
                            label = "Terms of Service",
                        ) {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_URL))
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }

                // ── Danger zone ──────────────────────────────────────────────
                item { SectionHeader("ACCOUNT ACTIONS") }

                item {
                    SettingsCard {
                        DangerRow(
                            icon  = Icons.Outlined.Logout,
                            label = "Sign Out",
                            onClick = { showLogoutDialog = true },
                        )
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }

    // ── Sign Out confirmation dialog ─────────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon             = {
                Icon(
                    Icons.Outlined.Logout,
                    contentDescription = null,
                    tint               = DangerRed,
                )
            },
            title            = { Text("Sign Out?", fontWeight = FontWeight.Bold) },
            text             = { Text("You'll need to log in again to access your account.") },
            confirmButton    = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = DangerRed),
                ) { Text("Sign Out", fontWeight = FontWeight.SemiBold) }
            },
            dismissButton    = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-components
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Avatar + name + email hero block shown at the top of the screen.
 * Shows the user's initials as a coloured circle if no photo is available.
 */
@Composable
private fun AvatarHero(name: String, email: String) {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // Initials avatar
        val initials = name
            .split(" ")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
            .ifEmpty { "?" }

        Box(
            modifier         = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Accent, Color(0xFF7986CB)))),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = initials,
                color      = Color.White,
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        if (name.isNotEmpty()) {
            Text(
                text       = name,
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                color      = MaterialTheme.colorScheme.onSurface,
            )
        }
        if (email.isNotEmpty()) {
            Text(
                text     = email,
                fontSize = 13.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Styled section label (e.g. "ACCOUNT", "ABOUT"). */
@Composable
private fun SectionHeader(title: String) {
    Text(
        text       = title,
        color      = SectionLabelColor,
        fontSize   = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        modifier   = Modifier.padding(start = 4.dp, bottom = 4.dp),
    )
}

/** White card wrapper used around each settings section. */
@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 4.dp)) {
            content()
        }
    }
}

/** Thin horizontal divider between rows inside a card. */
@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier  = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color     = Color(0xFFE0E0E0),
    )
}

/**
 * A row that shows an editable field.
 * - Displays a label + current value in "view" mode.
 * - Tapping the pencil icon switches to inline edit mode with a TextField.
 * - Save / Cancel buttons confirm or discard changes.
 *
 * @param label          Row label (e.g. "Display Name").
 * @param value          Current persisted value (pre-fills the field).
 * @param icon           Leading icon for the row.
 * @param keyboardType   Keyboard type for the TextField.
 * @param capitalization Word/sentence capitalization hint.
 * @param onSave         Called with the trimmed new value when the user confirms.
 */
@Composable
private fun EditableProfileRow(
    label          : String,
    value          : String,
    icon           : ImageVector,
    keyboardType   : KeyboardType   = KeyboardType.Text,
    capitalization : KeyboardCapitalization = KeyboardCapitalization.None,
    onSave         : (String) -> Unit,
) {
    var isEditing by remember { mutableStateOf(false) }
    var draft     by remember(value) { mutableStateOf(value) }
    val keyboard  = LocalSoftwareKeyboardController.current

    AnimatedContent(targetState = isEditing, label = "edit_$label") { editing ->
        if (editing) {
            // ── Edit mode ──────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                OutlinedTextField(
                    value         = draft,
                    onValueChange = { draft = it },
                    label         = { Text(label) },
                    leadingIcon   = { Icon(icon, contentDescription = null, tint = Accent) },
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType   = keyboardType,
                        capitalization = capitalization,
                        imeAction      = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboard?.hide()
                        onSave(draft)
                        isEditing = false
                    }),
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        focusedLabelColor  = Accent,
                    ),
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick  = { isEditing = false; draft = value },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(10.dp),
                    ) { Text("Cancel") }
                    Button(
                        onClick  = {
                            keyboard?.hide()
                            onSave(draft)
                            isEditing = false
                        },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Accent),
                    ) { Text("Save") }
                }
            }
        } else {
            // ── View mode ──────────────────────────────────────────────────
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = Accent,
                    modifier           = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text       = value.ifEmpty { "Tap to set" },
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color      = if (value.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant
                                     else MaterialTheme.colorScheme.onSurface,
                    )
                }
                IconButton(onClick = { isEditing = true }) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Edit $label",
                        tint               = Accent,
                        modifier           = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

/**
 * A non-editable display row.
 * Used for fields that can't be changed in-app (e.g. email requires Firebase re-auth).
 */
@Composable
private fun ReadOnlyRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = Accent,
            modifier           = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text       = value.ifEmpty { "—" },
                fontSize   = 15.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onSurface,
            )
        }
        Icon(
            Icons.Outlined.Lock,
            contentDescription = "Read-only",
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier           = Modifier.size(16.dp),
        )
    }
}

/**
 * A static informational row (label + value, no interaction).
 * Used for App Version.
 */
@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Accent, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(
            text     = label,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f),
            color    = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text     = value,
            fontSize = 14.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * A tappable row that opens an external link.
 * Shows a trailing arrow icon to signal navigation outside the app.
 */
@Composable
private fun LinkRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    TextButton(
        onClick          = onClick,
        modifier         = Modifier.fillMaxWidth(),
        contentPadding   = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        colors           = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
    ) {
        Icon(icon, contentDescription = null, tint = Accent, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(
            text     = label,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f),
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * A tappable row styled in red for destructive actions (Sign Out, Delete Account).
 */
@Composable
private fun DangerRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    TextButton(
        onClick        = onClick,
        modifier       = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        colors         = ButtonDefaults.textButtonColors(contentColor = DangerRed),
    ) {
        Icon(icon, contentDescription = null, tint = DangerRed, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(
            text       = label,
            fontSize   = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color      = DangerRed,
            modifier   = Modifier.weight(1f),
        )
    }
}