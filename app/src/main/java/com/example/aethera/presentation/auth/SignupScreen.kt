package com.example.aethera.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aethera.ui.theme.AetheraTheme
import org.koin.androidx.compose.koinViewModel

// ── Parent ──────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    innerPadding      : PaddingValues,
    onSignupSuccess   : () -> Unit,
    onNavigateToLogin : () -> Unit,
    viewModel         : SignupViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onSignupSuccess()
    }

    SignupContent(
        isLoading         = uiState.isLoading,
        error             = uiState.error,
        innerPadding      = innerPadding,
        onSignup          = { name, email, pass -> viewModel.signup(name, email, pass) },
        onNavigateToLogin = onNavigateToLogin,
        onDismissError    = viewModel::clearError,
    )
}

// ── Content ──────────────────────────────────────────────────
@Composable
fun SignupContent(
    isLoading         : Boolean,
    error             : String?,
    innerPadding      : PaddingValues,
    onSignup          : (String, String, String) -> Unit,
    onNavigateToLogin : () -> Unit,
    onDismissError    : () -> Unit,
) {
    var name     by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text       = "Create Account",
                style      = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onBackground,
                textAlign  = TextAlign.Center,
            )
            Text(
                text  = "Join the Aethera community",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Outlined.Person, null) },
                singleLine = true, shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Outlined.Email, null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Outlined.Lock, null) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            )

            if (error != null) {
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick  = { onSignup(name, email, password) },
                enabled  = !isLoading && name.isNotBlank() && email.isNotBlank() && password.length >= 6,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = MaterialTheme.shapes.medium,
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text("Create Account", style = MaterialTheme.typography.labelLarge)
            }

            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Sign in", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignupPreview() {
    AetheraTheme {
        SignupContent(false, null, PaddingValues(), { _, _, _ -> }, {}, {})
    }
}
