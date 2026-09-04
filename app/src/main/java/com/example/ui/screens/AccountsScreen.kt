package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AuthUserState
import com.example.data.local.CloudAccountEntity
import com.example.data.model.CloudProvider
import com.example.data.repository.CloudStorageRepository
import com.example.ui.components.FirebaseAuthCard
import com.example.ui.theme.DropboxColor
import com.example.ui.theme.GoogleDriveColor
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TelegramColor

@Composable
fun AccountsScreen(
  accounts: List<CloudAccountEntity>,
  authUserState: AuthUserState = AuthUserState(),
  authActionState: com.example.ui.viewmodel.AuthActionState = com.example.ui.viewmodel.AuthActionState(),
  onToggleAccount: (String, Boolean) -> Unit,
  onSyncAccount: (String) -> Unit,
  onSignInWithEmail: (String, String) -> Unit = { _, _ -> },
  onSignUpWithEmail: (String, String, String) -> Unit = { _, _, _ -> },
  onSignInAnonymously: () -> Unit = {},
  onUpdateDisplayName: (String) -> Unit = {},
  onSendPasswordReset: (String) -> Unit = {},
  onSignOut: () -> Unit = {},
  onClearAuthActionState: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  var showArchitectureDialog by remember { mutableStateOf(false) }
  var accountToEdit by remember { mutableStateOf<CloudAccountEntity?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "SECURITY & CONTROL PLANE",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            fontSize = 10.sp
          ),
          color = Color(0xFF0061A4)
        )
        Text(
          text = "Cloud Accounts & Auth",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
          ),
          color = Color(0xFF1A1C1E)
        )
        Text(
          text = "Firebase Auth Session & Cloud Storage Connectors",
          style = MaterialTheme.typography.bodyMedium,
          color = Color(0xFF44474E)
        )
      }

      IconButton(
        onClick = { showArchitectureDialog = true },
        modifier = Modifier.testTag("open_architecture_info_button")
      ) {
        Icon(
          Icons.Default.AccountTree,
          contentDescription = "Architecture Blueprint",
          tint = Color(0xFF0061A4)
        )
      }
    }

    // Primary Firebase Authentication & User Management Card
    FirebaseAuthCard(
      authUserState = authUserState,
      authActionState = authActionState,
      onSignInWithEmail = onSignInWithEmail,
      onSignUpWithEmail = onSignUpWithEmail,
      onSignInAnonymously = onSignInAnonymously,
      onUpdateDisplayName = onUpdateDisplayName,
      onSendPasswordReset = onSendPasswordReset,
      onSignOut = onSignOut,
      onClearAuthActionState = onClearAuthActionState
    )

    // Architecture Overview Banner
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("architecture_banner_card"),
      colors = CardDefaults.cardColors(
        containerColor = Color.White
      ),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1E2E9)),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      shape = RoundedCornerShape(20.dp)
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFD1E4FF)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Default.Security,
            contentDescription = null,
            tint = Color(0xFF0061A4),
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Zero-Knowledge Control Plane",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF1A1C1E)
          )
          Text(
            text = "OAuth tokens encrypted via GCP KMS. Upload payloads stream direct to provider resumable endpoints.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF44474E)
          )
        }

        TextButton(onClick = { showArchitectureDialog = true }) {
          Text("Specs", fontWeight = FontWeight.Bold, color = Color(0xFF0061A4))
        }
      }
    }

    // Provider Account Cards
    accounts.forEach { account ->
      val provider = CloudProvider.fromId(account.providerId)

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("account_manage_card_${account.providerId}"),
        colors = CardDefaults.cardColors(
          containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1E2E9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(24.dp)
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Title & Connection Toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(provider.brandColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = when (provider) {
                    CloudProvider.GOOGLE_DRIVE -> Icons.Default.Cloud
                    CloudProvider.DROPBOX -> Icons.Default.Folder
                    CloudProvider.TELEGRAM -> Icons.Default.Send
                  },
                  contentDescription = provider.displayName,
                  tint = provider.brandColor,
                  modifier = Modifier.size(22.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Text(
                  text = account.displayName,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFF1A1C1E)
                )
                Text(
                  text = account.accountEmail,
                  style = MaterialTheme.typography.bodySmall,
                  color = Color(0xFF44474E)
                )
              }
            }

            Switch(
              checked = account.isConnected,
              onCheckedChange = { onToggleAccount(account.providerId, account.isConnected) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = provider.brandColor
              )
            )
          }

          // Details Matrix
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0xFFF7F9FC))
              .border(androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1E2E9)), RoundedCornerShape(14.dp))
              .padding(14.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Auth Protocol",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF44474E)
                )
                Text(
                  text = account.authProtocol,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFF1A1C1E)
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Granted Scopes / Stream",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF44474E)
                )
                Text(
                  text = account.scopes,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = Color(0xFF0061A4)
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Quota Utilization",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF44474E)
                )
                Text(
                  text = if (account.totalBytes > 0) {
                    "${CloudStorageRepository.formatBytes(account.usedBytes)} / ${CloudStorageRepository.formatBytes(account.totalBytes)}"
                  } else {
                    "${CloudStorageRepository.formatBytes(account.usedBytes)} (Unlimited pool)"
                  },
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFF1A1C1E)
                )
              }
            }
          }

          // Actions Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = { onSyncAccount(account.providerId) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Sync Quota")
            }

            Button(
              onClick = { accountToEdit = account },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = provider.brandColor),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Credentials")
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }

  // System Architecture Dialog
  if (showArchitectureDialog) {
    AlertDialog(
      onDismissRequest = { showArchitectureDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.AccountTree, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Cloud Integration Blueprint", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }
      },
      text = {
        Column(
          modifier = Modifier.verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          ArchitecturePoint(
            title = "1. Decoupled Data Plane",
            desc = "Upload streams bypass serverless Cloud Functions to prevent RAM exhaustion and timeouts. Client requests a resumable upload URI, then streams binary chunks directly to Google Drive/Dropbox."
          )
          ArchitecturePoint(
            title = "2. Telegram MTProto Bridge",
            desc = "Because Telegram requires a persistent stateful TCP MTProto connection (TDLib), a containerized microservice runs on GCP Cloud Run to maintain the user's session and push files to Saved Messages."
          )
          ArchitecturePoint(
            title = "3. Smart Routing Matrix",
            desc = "Algorithm evaluates file size and headroom: files > 500MB route to Telegram unlimited pool (preserving expensive Drive quotas). Files > 2GB obey Telegram's protocol ceiling and route to Drive."
          )
          ArchitecturePoint(
            title = "4. KMS Token Encryption",
            desc = "OAuth2 refresh tokens and session keys are secured using Google Cloud KMS and Firestore restricted sub-collections with server-only write rules."
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showArchitectureDialog = false },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Understood")
        }
      }
    )
  }

  // Credentials / Token Config Modal
  accountToEdit?.let { account ->
    var emailInput by remember { mutableStateOf(account.accountEmail) }
    var scopesInput by remember { mutableStateOf(account.scopes) }

    AlertDialog(
      onDismissRequest = { accountToEdit = null },
      title = {
        Text(
          text = "${account.displayName} Credentials",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Configure live endpoint parameters or simulated OAuth session credentials.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Account Identifier / Phone") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = scopesInput,
            onValueChange = { scopesInput = it },
            label = { Text("Permission Scopes") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Text(
            text = "Token status: AES256_GCM_ENCRYPTED (Valid)",
            style = MaterialTheme.typography.labelSmall,
            color = SuccessGreen
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { accountToEdit = null },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Save Configuration")
        }
      },
      dismissButton = {
        TextButton(onClick = { accountToEdit = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun ArchitecturePoint(title: String, desc: String) {
  Column {
    Text(
      text = title,
      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = desc,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      lineHeight = 18.sp
    )
  }
}
