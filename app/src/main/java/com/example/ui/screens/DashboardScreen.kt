package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CloudAccountEntity
import com.example.data.local.UploadRecordEntity
import com.example.data.model.CloudProvider
import com.example.data.model.StorageQuotaSummary
import com.example.data.repository.CloudStorageRepository
import com.example.ui.components.StorageDonutChart
import com.example.ui.theme.DropboxColor
import com.example.ui.theme.GoogleDriveColor
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TelegramColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
  summary: StorageQuotaSummary,
  accounts: List<CloudAccountEntity>,
  recentUploads: List<UploadRecordEntity>,
  authUserState: com.example.auth.AuthUserState = com.example.auth.AuthUserState(),
  onNavigateToUpload: () -> Unit,
  onNavigateToFiles: () -> Unit,
  onNavigateToAccounts: () -> Unit,
  onSyncAccount: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF7F9FC))
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top App Bar Header - Professional Polish
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(Color.White)
          .border(BorderStroke(1.dp, Color(0xFFE1E2E9)), CircleShape)
          .clickable { onNavigateToAccounts() },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Menu,
          contentDescription = "Menu",
          tint = Color(0xFF44474E),
          modifier = Modifier.size(20.dp)
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "CLOUDSTACK",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.5.sp,
            fontSize = 10.sp
          ),
          color = Color(0xFF0061A4)
        )
        Text(
          text = "Unified Storage",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
          color = Color(0xFF1A1C1E)
        )
      }

      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(if (authUserState.isLoggedIn) Color(0xFF0061A4) else Color(0xFFD1E4FF))
          .border(BorderStroke(2.dp, Color.White), CircleShape)
          .clickable { onNavigateToAccounts() },
        contentAlignment = Alignment.Center
      ) {
        if (authUserState.isLoggedIn) {
          val initial = (authUserState.displayName.firstOrNull() ?: authUserState.email.firstOrNull() ?: 'U').uppercaseChar()
          Text(
            text = initial.toString(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        } else {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "User Profile",
            tint = Color(0xFF0061A4),
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // Hero Storage Capacity Card - Professional Polish
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("storage_summary_card"),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, Color(0xFFE1E2E9)),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      shape = RoundedCornerShape(30.dp)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column {
            Text(
              text = "TOTAL CAPACITY",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                fontSize = 10.sp
              ),
              color = Color(0xFF44474E)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = CloudStorageRepository.formatBytes(summary.totalUsedBytes),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1A1C1E)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "of ${CloudStorageRepository.formatBytes(summary.totalAllocatedBytes)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF44474E)
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(32.dp))
              .background(Color(0xFFD1E4FF))
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = "${summary.usedPercentage.toInt()}% Used",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF0061A4)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Donut Chart & Legend
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          StorageDonutChart(summary = summary)

          Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Google Drive Legend
            LegendItem(
              name = "Drive",
              bytes = summary.googleDriveUsedBytes,
              color = GoogleDriveColor
            )
            // Dropbox Legend
            LegendItem(
              name = "Dropbox",
              bytes = summary.dropboxUsedBytes,
              color = DropboxColor
            )
            // Telegram Legend
            LegendItem(
              name = "Telegram",
              bytes = summary.telegramTotalUploadedBytes,
              color = TelegramColor,
              subNote = "Unlimited"
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Progress Bar
        LinearProgressIndicator(
          progress = { (summary.usedPercentage / 100f).coerceIn(0f, 1f) },
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp)),
          color = Color(0xFF0061A4),
          trackColor = Color(0xFFE1E2E9)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Linear Breakdown Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, Color(0xFFF0F2F5)), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(GoogleDriveColor, CircleShape))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Drive ${CloudStorageRepository.formatBytes(summary.googleDriveUsedBytes)}",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF44474E)
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(DropboxColor, CircleShape))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Dropbox ${CloudStorageRepository.formatBytes(summary.dropboxUsedBytes)}",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF44474E)
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(TelegramColor, CircleShape))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Telegram",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF44474E)
            )
          }
        }
      }
    }

    // Smart Routing Health Callout
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("smart_routing_insight_card"),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, Color(0xFFE1E2E9)),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      shape = RoundedCornerShape(20.dp)
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.Top
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFD1E4FF)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "Smart Routing",
            tint = Color(0xFF0061A4),
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Smart Routing Active",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF1A1C1E)
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Files over 500 MB automatically divert to Telegram Cloud. Google Drive has 3.58 GB free and Dropbox has 550 MB remaining.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF44474E),
            lineHeight = 18.sp
          )
        }
      }
    }

    // Quick Actions Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = onNavigateToUpload,
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("dashboard_smart_upload_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF0061A4)
        ),
        shape = RoundedCornerShape(16.dp)
      ) {
        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Smart Upload", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
      }

      OutlinedButton(
        onClick = onNavigateToFiles,
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("dashboard_view_files_button"),
        border = BorderStroke(1.dp, Color(0xFFE1E2E9)),
        colors = ButtonDefaults.outlinedButtonColors(
          containerColor = Color.White,
          contentColor = Color(0xFF1A1C1E)
        ),
        shape = RoundedCornerShape(16.dp)
      ) {
        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF44474E))
        Spacer(modifier = Modifier.width(6.dp))
        Text("View Files", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
      }
    }

    // Connected Providers Quota Cards Section
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Connected Providers",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color(0xFF1A1C1E)
      )
      Text(
        text = "${accounts.count { it.isConnected }} Active",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = Color(0xFF0061A4)
      )
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      accounts.forEach { account ->
        ProviderDetailCard(
          account = account,
          onSyncClick = { onSyncAccount(account.providerId) }
        )
      }
    }

    // Recent Smart Upload Activity
    if (recentUploads.isNotEmpty()) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Recent Upload Decisions",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color(0xFF1A1C1E)
      )

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        recentUploads.take(4).forEach { record ->
          UploadRecordTile(record = record)
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun LegendItem(
  name: String,
  bytes: Long,
  color: Color,
  subNote: String? = null
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(10.dp)
        .background(color, CircleShape)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Column {
      Text(
        text = name,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
        color = Color(0xFF1A1C1E)
      )
      Text(
        text = subNote ?: CloudStorageRepository.formatBytes(bytes),
        style = MaterialTheme.typography.labelSmall,
        color = Color(0xFF44474E)
      )
    }
  }
}

@Composable
fun ProviderDetailCard(
  account: CloudAccountEntity,
  onSyncClick: () -> Unit
) {
  val provider = CloudProvider.fromId(account.providerId)
  val isUnlimited = account.totalBytes < 0
  val percentage = if (!isUnlimited && account.totalBytes > 0) {
    ((account.usedBytes.toFloat() / account.totalBytes.toFloat()) * 100f).coerceIn(0f, 100f)
  } else 0f

  val freeBytes = if (!isUnlimited) {
    (account.totalBytes - account.usedBytes).coerceAtLeast(0L)
  } else 0L

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("provider_card_${account.providerId}"),
    colors = CardDefaults.cardColors(
      containerColor = Color.White
    ),
    border = BorderStroke(1.dp, Color(0xFFE1E2E9)),
    shape = RoundedCornerShape(20.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
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
              .background(provider.brandColor.copy(alpha = 0.12f)),
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
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = account.displayName,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF1A1C1E)
            )
            Text(
              text = account.accountEmail,
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF44474E)
            )
          }
        }

        IconButton(
          onClick = onSyncClick,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Sync,
            contentDescription = "Sync Quota",
            tint = Color(0xFF0061A4),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (isUnlimited) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Storage Pool: Virtual Unlimited",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = provider.brandColor
          )
          Text(
            text = "Consumed: ${CloudStorageRepository.formatBytes(account.usedBytes)}",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF44474E)
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Max 2 GB per file via MTProto TDLib (4 GB on Telegram Premium)",
          style = MaterialTheme.typography.labelSmall,
          color = Color(0xFF44474E).copy(alpha = 0.8f)
        )
      } else {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "${CloudStorageRepository.formatBytes(account.usedBytes)} of ${CloudStorageRepository.formatBytes(account.totalBytes)} used",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = Color(0xFF1A1C1E)
          )
          Text(
            text = "${CloudStorageRepository.formatBytes(freeBytes)} free",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (percentage > 85f) MaterialTheme.colorScheme.error else Color(0xFF0061A4)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
          progress = { (percentage / 100f).coerceIn(0f, 1f) },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = provider.brandColor,
          trackColor = Color(0xFFE1E2E9)
        )
      }
    }
  }
}

@Composable
fun UploadRecordTile(record: UploadRecordEntity) {
  val provider = CloudProvider.fromId(record.providerId)
  val dateStr = remember(record.timestampEpoch) {
    val fmt = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
    fmt.format(Date(record.timestampEpoch))
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = Color.White
    ),
    border = BorderStroke(1.dp, Color(0xFFE1E2E9)),
    shape = RoundedCornerShape(16.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(provider.brandColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = provider.brandColor,
          modifier = Modifier.size(16.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = record.fileName,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            color = Color(0xFF1A1C1E),
            modifier = Modifier.weight(1f)
          )
          Text(
            text = dateStr,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF44474E)
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = record.routingReason,
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF44474E),
          maxLines = 2
        )
      }
    }
  }
}

