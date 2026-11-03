package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.GMobiledata
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDialog(
    onDismiss: () -> Unit,
    onLoginSuccess: (email: String, username: String) -> Unit,
    onContinueAsGuest: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableStateOf("signup") } // "login", "signup"

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Password strength calculation
    val passwordStrength = when {
        password.isEmpty() -> 0f
        password.length < 6 -> 0.3f
        password.length < 10 -> 0.65f
        password.any { it.isDigit() } && password.any { !it.isLetterOrDigit() } -> 1.0f
        else -> 0.85f
    }
    val strengthLabel = when {
        passwordStrength <= 0.3f -> "Weak (Min 10 characters)"
        passwordStrength <= 0.65f -> "Fair"
        passwordStrength <= 0.85f -> "Good"
        else -> "Strong"
    }
    val strengthColor = when {
        passwordStrength <= 0.3f -> Color(0xFFFF5252)
        passwordStrength <= 0.65f -> Color(0xFFFFB300)
        else -> Color(0xFF00E676)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .testTag("auth_dialog")
        ) {
            // Header with logo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Larpify",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Text(
                text = "Free music. Zero ads. Zero chill.",
                color = Color(0xFFB3B3B3),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 18.dp)
            )

            // Tabs: Log in | Sign up
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF282828))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedTab == "signup") MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { selectedTab = "signup" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign Up",
                        color = if (selectedTab == "signup") Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedTab == "login") MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { selectedTab = "login" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log In",
                        color = if (selectedTab == "login") Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (selectedTab == "signup") {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Display Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("auth_username_input")
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email address") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().testTag("auth_email_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().testTag("auth_password_input")
            )

            if (selectedTab == "signup") {
                Spacer(modifier = Modifier.height(8.dp))
                // Password strength meter
                LinearProgressIndicator(
                    progress = { passwordStrength },
                    modifier = Modifier.fillMaxWidth(),
                    color = strengthColor
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Password strength:", color = Color(0xFFB3B3B3), fontSize = 11.sp)
                    Text(strengthLabel, color = strengthColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm password") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary action button
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        onShowToast("Please enter email and password")
                        return@Button
                    }
                    if (selectedTab == "signup" && password.length < 8) {
                        onShowToast("Password must be at least 8 characters")
                        return@Button
                    }
                    val finalUser = if (username.isNotBlank()) username else email.substringBefore("@")
                    onLoginSuccess(email, finalUser)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().testTag("auth_submit_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = if (selectedTab == "signup") "Create Free Account" else "Log In",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Passkey Button
            Button(
                onClick = {
                    onLoginSuccess("passkey.user@larpify.app", "Passkey User")
                    onShowToast("Authenticated with Passkey")
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().testTag("auth_passkey_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282828)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign in with Passkey", color = Color.White, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Continue as guest
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onContinueAsGuest()
                        onDismiss()
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Continue as Guest (3 days pass)",
                    color = Color(0xFFB3B3B3),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
