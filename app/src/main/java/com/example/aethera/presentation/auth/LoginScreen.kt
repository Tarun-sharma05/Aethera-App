package com.example.aethera.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
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
@Composable
fun LoginScreen(
    innerPadding       : PaddingValues,
    onLoginSuccess     : () -> Unit,
    onNavigateToSignup : () -> Unit,
    viewModel          : LoginViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onLoginSuccess()
    }

    LoginContent(
        isLoading          = uiState.isLoading,
        error              = uiState.error,
        innerPadding       = innerPadding,
        onLogin            = { email, pass -> viewModel.login(email, pass) },
        onNavigateToSignup = onNavigateToSignup,
        onDismissError     = viewModel::clearError,
    )
}

// ── Content (pure UI, @Preview-able) ─────────────────────────
@Composable
fun LoginContent(
    isLoading          : Boolean,
    error              : String?,
    innerPadding       : PaddingValues,
    onLogin            : (String, String) -> Unit,
    onNavigateToSignup : () -> Unit,
    onDismissError     : () -> Unit,
) {
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
                text      = "AETHERA",
                style     = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color     = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Text(
                text  = "Welcome back",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value         = email,
                onValueChange = { email = it },
                label         = { Text("Email") },
                leadingIcon   = { Icon(Icons.Outlined.Email, null) },
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape         = MaterialTheme.shapes.medium,
                modifier      = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value                  = password,
                onValueChange          = { password = it },
                label                  = { Text("Password") },
                leadingIcon            = { Icon(Icons.Outlined.Lock, null) },
                singleLine             = true,
                visualTransformation   = PasswordVisualTransformation(),
                keyboardOptions        = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape                  = MaterialTheme.shapes.medium,
                modifier               = Modifier.fillMaxWidth(),
            )

            if (error != null) {
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick  = { onLogin(email, password) },
                enabled  = !isLoading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = MaterialTheme.shapes.medium,
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text("Sign In", style = MaterialTheme.typography.labelLarge)
            }

            TextButton(onClick = onNavigateToSignup) {
                Text("Don't have an account? Sign up", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    AetheraTheme {
        LoginContent(false, null, PaddingValues(), { _, _ -> }, {}, {})
    }
}
