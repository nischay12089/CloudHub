package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AuthUserState
import com.example.ui.viewmodel.AuthActionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val FirebaseAmber = Color(0xFFFF9100)
private val PrimaryNavy = Color(0xFF0061A4)
private val BorderNeutral = Color(0xFFE1E2E9)
private val SurfaceBackground = Color(0xFFF7F9FC)
private val TextPrimary = Color(0xFF1A1C1E)
private val TextSecondary = Color(0xFF44474E)
private val SuccessGreen = Color(0xFF1B6D24)

@Composable
fun FirebaseAuthCard(
  authUserState: AuthUserState,
  authActionState: AuthActionState,
  onSignInWithEmail: (String, String) -> Unit,
  onSignUpWithEmail: (String, String, String) -> Unit,
  onSignInAnonymously: () -> Unit,
  onUpdateDisplayName: (String) -> Unit,
  onSendPasswordReset: (String) -> Unit,
  onSignOut: () -> Unit,
  onClearAuthActionState: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showResetPasswordDialog by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("firebase_auth_card"),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, BorderNeutral),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    shape = RoundedCornerShape(24.dp)
  ) {
    Column(
      modifier = Modifier.padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header with Firebase branding
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(FirebaseAmber.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Whatshot,
              contentDescription = "Firebase Authentication",
              tint = FirebaseAmber,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = "FIREBASE IDENTITY",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                fontSize = 10.sp
              ),
              color = PrimaryNavy
            )
            Text(
              text = if (authUserState.isLoggedIn) "User Account" else "Sign In & Security",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimary
            )
          }
        }

        // Active session badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (authUserState.isLoggedIn) SuccessGreen.copy(alpha = 0.12f) else PrimaryNavy.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (authUserState.isLoggedIn) SuccessGreen else PrimaryNavy)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = if (authUserState.isLoggedIn) "Authenticated" else "Guest Mode",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
              ),
              color = if (authUserState.isLoggedIn) SuccessGreen else PrimaryNavy
            )
          }
        }
      }

      // Status messages
      AnimatedVisibility(visible = authActionState.error != null) {
        authActionState.error?.let { err ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFFFEBEE))
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFBA1A1A), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(err, style = MaterialTheme.typography.bodySmall, color = Color(0xFFBA1A1A), modifier = Modifier.weight(1f))
            IconButton(onClick = onClearAuthActionState, modifier = Modifier.size(20.dp)) {
              Text("✕", fontSize = 12.sp, color = Color(0xFFBA1A1A))
            }
          }
        }
      }

      AnimatedVisibility(visible = authActionState.success != null) {
        authActionState.success?.let { msg ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SuccessGreen.copy(alpha = 0.12f))
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(msg, style = MaterialTheme.typography.bodySmall, color = SuccessGreen, modifier = Modifier.weight(1f))
            IconButton(onClick = onClearAuthActionState, modifier = Modifier.size(20.dp)) {
              Text("✕", fontSize = 12.sp, color = SuccessGreen)
            }
          }
        }
      }

      if (authUserState.isLoggedIn) {
        // Authenticated State View
        AuthenticatedUserProfileView(
          user = authUserState,
          isLoading = authActionState.isLoading,
          onEditProfile = { showEditProfileDialog = true },
          onResetPassword = { showResetPasswordDialog = true },
          onSignOut = onSignOut
        )
      } else {
        // Unauthenticated Sign In / Sign Up Form
        AuthFormView(
          isLoading = authActionState.isLoading,
          onSignInWithEmail = onSignInWithEmail,
          onSignUpWithEmail = onSignUpWithEmail,
          onSignInAnonymously = onSignInAnonymously,
          onForgotPassword = { showResetPasswordDialog = true }
        )
      }
    }
  }

  // Edit Profile Name Dialog
  if (showEditProfileDialog) {
    var newName by remember { mutableStateOf(authUserState.displayName) }
    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = { Text("Update Display Name", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Update the user identity attached to your Firebase session.", style = MaterialTheme.typography.bodyMedium)
          OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Display Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("edit_display_name_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newName.isNotBlank()) {
              onUpdateDisplayName(newName)
              showEditProfileDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
          modifier = Modifier.testTag("save_display_name_button")
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditProfileDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Reset Password Dialog
  if (showResetPasswordDialog) {
    var resetEmail by remember { mutableStateOf(authUserState.email.takeIf { it.contains("@") && !authUserState.isAnonymous } ?: "") }
    AlertDialog(
      onDismissRequest = { showResetPasswordDialog = false },
      title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            "Enter your registered email address. Firebase Auth will transmit a secure recovery link.",
            style = MaterialTheme.typography.bodyMedium
          )
          OutlinedTextField(
            value = resetEmail,
            onValueChange = { resetEmail = it },
            label = { Text("Account Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("reset_password_email_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (resetEmail.isNotBlank()) {
              onSendPasswordReset(resetEmail)
              showResetPasswordDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
          modifier = Modifier.testTag("send_reset_password_button")
        ) {
          Text("Send Recovery Link")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetPasswordDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun AuthenticatedUserProfileView(
  user: AuthUserState,
  isLoading: Boolean,
  onEditProfile: () -> Unit,
  onResetPassword: () -> Unit,
  onSignOut: () -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    // Identity row
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(50.dp)
          .clip(CircleShape)
          .background(PrimaryNavy),
        contentAlignment = Alignment.Center
      ) {
        val initial = (user.displayName.firstOrNull() ?: user.email.firstOrNull() ?: 'U').uppercaseChar()
        Text(
          text = initial.toString(),
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = user.displayName,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
          )
          if (user.isAnonymous) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE8EEF5))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("Guest", style = MaterialTheme.typography.labelSmall, color = PrimaryNavy)
            }
          }
        }
        Text(
          text = user.email,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
        Text(
          text = "UID: ${user.uid.take(16)}...",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = TextSecondary.copy(alpha = 0.7f)
        )
      }

      IconButton(
        onClick = onEditProfile,
        modifier = Modifier.testTag("edit_profile_icon_button")
      ) {
        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = PrimaryNavy, modifier = Modifier.size(20.dp))
      }
    }

    // Security & Session Details Box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(SurfaceBackground)
        .border(BorderStroke(1.dp, BorderNeutral), RoundedCornerShape(14.dp))
        .padding(12.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Security Provider", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
          Text("Firebase Auth (${user.providerId})", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Account Verification", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
          Text(
            if (user.isEmailVerified) "Verified ✓" else "Active Session",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (user.isEmailVerified) SuccessGreen else PrimaryNavy
          )
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Session Token", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
          Text("GCP KMS Encrypted (JWT)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryNavy)
        }
      }
    }

    // Account Actions
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedButton(
        onClick = onResetPassword,
        modifier = Modifier.weight(1f).testTag("auth_reset_password_btn"),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderNeutral),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
      ) {
        Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryNavy)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Password", style = MaterialTheme.typography.labelMedium)
      }

      Button(
        onClick = onSignOut,
        modifier = Modifier.weight(1f).testTag("auth_sign_out_btn"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A)),
        enabled = !isLoading
      ) {
        Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Sign Out", style = MaterialTheme.typography.labelMedium, color = Color.White)
      }
    }
  }
}

@Composable
private fun AuthFormView(
  isLoading: Boolean,
  onSignInWithEmail: (String, String) -> Unit,
  onSignUpWithEmail: (String, String, String) -> Unit,
  onSignInAnonymously: () -> Unit,
  onForgotPassword: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Sign In, 1 = Register
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var displayName by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }

  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    // Mode Switcher Tabs
    TabRow(
      selectedTabIndex = selectedTab,
      modifier = Modifier.clip(RoundedCornerShape(12.dp)),
      containerColor = Color(0xFFE8EEF5)
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        selectedContentColor = PrimaryNavy,
        unselectedContentColor = TextSecondary,
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Sign In", fontWeight = FontWeight.SemiBold)
          }
        }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        selectedContentColor = PrimaryNavy,
        unselectedContentColor = TextSecondary,
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Create Account", fontWeight = FontWeight.SemiBold)
          }
        }
      )
    }

    if (selectedTab == 1) {
      OutlinedTextField(
        value = displayName,
        onValueChange = { displayName = it },
        label = { Text("Display Name") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryNavy) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag("auth_name_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = PrimaryNavy,
          unfocusedBorderColor = BorderNeutral,
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White
        )
      )
    }

    OutlinedTextField(
      value = email,
      onValueChange = { email = it },
      label = { Text("Email Address") },
      leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryNavy) },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryNavy,
        unfocusedBorderColor = BorderNeutral,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
      )
    )

    OutlinedTextField(
      value = password,
      onValueChange = { password = it },
      label = { Text("Password") },
      leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryNavy) },
      trailingIcon = {
        IconButton(onClick = { passwordVisible = !passwordVisible }) {
          Icon(
            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = "Toggle password visibility",
            tint = TextSecondary
          )
        }
      },
      visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryNavy,
        unfocusedBorderColor = BorderNeutral,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
      )
    )

    if (selectedTab == 0) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        TextButton(onClick = onForgotPassword) {
          Text("Forgot password?", style = MaterialTheme.typography.labelSmall, color = PrimaryNavy)
        }
      }
    }

    // Main action button
    Button(
      onClick = {
        if (selectedTab == 0) {
          onSignInWithEmail(email, password)
        } else {
          onSignUpWithEmail(email, password, displayName)
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("auth_primary_submit_btn"),
      colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
      shape = RoundedCornerShape(14.dp),
      enabled = !isLoading
    ) {
      if (isLoading) {
        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
      } else {
        Text(
          text = if (selectedTab == 0) "Sign In with Firebase" else "Create Firebase Account",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
      }
    }

    // Alternative Anonymous / Guest login
    OutlinedButton(
      onClick = onSignInAnonymously,
      modifier = Modifier
        .fillMaxWidth()
        .height(44.dp)
        .testTag("auth_guest_signin_btn"),
      shape = RoundedCornerShape(14.dp),
      border = BorderStroke(1.dp, BorderNeutral),
      colors = ButtonDefaults.outlinedButtonColors(
        containerColor = Color.White,
        contentColor = TextPrimary
      ),
      enabled = !isLoading
    ) {
      Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryNavy)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Continue as Guest (Anonymous Auth)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
    }
  }
}
