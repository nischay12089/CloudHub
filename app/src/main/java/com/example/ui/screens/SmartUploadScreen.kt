package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.local.CloudAccountEntity
import com.example.data.model.CloudProvider
import com.example.data.model.FileCategory
import com.example.data.model.SmartRoutingDecision
import com.example.data.repository.CloudStorageRepository
import com.example.ui.theme.DropboxColor
import com.example.ui.theme.GoogleDriveColor
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TelegramColor
import com.example.ui.viewmodel.RoutingMode
import com.example.ui.viewmodel.UploadSimulationState

data class SampleFilePreset(
  val name: String,
  val sizeBytes: Long,
  val category: FileCategory
)

@Composable
fun SmartUploadScreen(
  fileName: String,
  fileSizeBytes: Long,
  category: FileCategory,
  routingMode: RoutingMode,
  manualProvider: CloudProvider,
  smartDecision: SmartRoutingDecision,
  uploadState: UploadSimulationState,
  accounts: List<CloudAccountEntity>,
  onFileNameChange: (String) -> Unit,
  onFileSizeChange: (Long) -> Unit,
  onCategoryChange: (FileCategory) -> Unit,
  onRoutingModeChange: (RoutingMode) -> Unit,
  onManualProviderChange: (CloudProvider) -> Unit,
  onStartUpload: () -> Unit,
  onResetUpload: () -> Unit,
  onNavigateToFiles: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  val presets = listOf(
    SampleFilePreset("Q4_Financial_Dataset.csv", 45L * 1024 * 1024, FileCategory.DOCUMENT),
    SampleFilePreset("HighRes_Product_Video_4K.mp4", 780L * 1024 * 1024, FileCategory.VIDEO),
    SampleFilePreset("Engineering_Architecture_Repo.zip", 1850L * 1024 * 1024, FileCategory.ARCHIVE),
    SampleFilePreset("Enterprise_Backup_Image.iso", 3600L * 1024 * 1024, FileCategory.ARCHIVE)
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    Column {
      Text(
        text = "INTELLIGENT ROUTING ENGINE",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp,
          fontSize = 10.sp
        ),
        color = Color(0xFF0061A4)
      )
      Text(
        text = "Smart Upload",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = (-0.5).sp
        ),
        color = Color(0xFF1A1C1E)
      )
      Text(
        text = "Automatic space balancing & direct resumable cloud push",
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFF44474E)
      )
    }

    // Presets Row
    Column {
      Text(
        text = "Quick File Presets:",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = Color(0xFF44474E)
      )
      Spacer(modifier = Modifier.height(4.dp))
      val presetScrollState = rememberScrollState()
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(presetScrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        presets.forEach { preset ->
          SuggestionChip(
            onClick = {
              onFileNameChange(preset.name)
              onFileSizeChange(preset.sizeBytes)
              onCategoryChange(preset.category)
            },
            shape = RoundedCornerShape(32.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1E2E9)),
            colors = androidx.compose.material3.SuggestionChipDefaults.suggestionChipColors(
              containerColor = Color.White,
              labelColor = Color(0xFF1A1C1E)
            ),
            label = {
              Text(
                text = "${preset.name.substringBefore('.')} (${CloudStorageRepository.formatBytes(preset.sizeBytes)})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
              )
            }
          )
        }
      }
    }

    // File Details Configuration Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1E2E9)),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      shape = RoundedCornerShape(24.dp)
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Text(
          text = "1. File Parameters",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = Color(0xFF0061A4)
        )

        OutlinedTextField(
          value = fileName,
          onValueChange = onFileNameChange,
          label = { Text("File Name") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("upload_file_name_input"),
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0061A4),
            unfocusedBorderColor = Color(0xFFE1E2E9),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color(0xFF1A1C1E),
            unfocusedTextColor = Color(0xFF1A1C1E)
          )
        )

        // File Size Adjuster Slider
        Column {
          val currentMb = (fileSizeBytes / (1024f * 1024f)).coerceIn(10f, 4000f)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "File Payload Size",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
              color = Color(0xFF1A1C1E)
            )
            Text(
              text = CloudStorageRepository.formatBytes(fileSizeBytes),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0061A4)
              )
            )
          }

          Slider(
            value = currentMb,
            onValueChange = { mb ->
              onFileSizeChange((mb * 1024 * 1024).toLong())
            },
            valueRange = 10f..4000f,
            steps = 39,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
              thumbColor = Color(0xFF0061A4),
              activeTrackColor = Color(0xFF0061A4),
              inactiveTrackColor = Color(0xFFE1E2E9)
            )
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("10 MB", style = MaterialTheme.typography.labelSmall, color = Color(0xFF44474E))
            Text("500 MB (Preserve Threshold)", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0061A4))
            Text("2 GB (TG Limit)", style = MaterialTheme.typography.labelSmall, color = TelegramColor)
            Text("4 GB", style = MaterialTheme.typography.labelSmall, color = Color(0xFF44474E))
          }
        }
      }
    }

    // Routing Strategy Tabs
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1E2E9)),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      shape = RoundedCornerShape(24.dp)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Text(
          text = "2. Destination Routing Mode",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = Color(0xFF0061A4)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TabRow(
          selectedTabIndex = if (routingMode == RoutingMode.SMART_AUTO) 0 else 1,
          modifier = Modifier.clip(RoundedCornerShape(14.dp)),
          containerColor = Color(0xFFE8EEF5)
        ) {
          Tab(
            selected = routingMode == RoutingMode.SMART_AUTO,
            onClick = { onRoutingModeChange(RoutingMode.SMART_AUTO) },
            selectedContentColor = Color(0xFF0061A4),
            unselectedContentColor = Color(0xFF44474E),
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Smart Auto-Route", fontWeight = FontWeight.SemiBold)
              }
            }
          )
          Tab(
            selected = routingMode == RoutingMode.MANUAL,
            onClick = { onRoutingModeChange(RoutingMode.MANUAL) },
            selectedContentColor = Color(0xFF0061A4),
            unselectedContentColor = Color(0xFF44474E),
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Manual Pick", fontWeight = FontWeight.SemiBold)
              }
            }
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Dynamic Display Based on Mode
        if (routingMode == RoutingMode.SMART_AUTO) {
          // Smart Recommendation Card
          val prov = smartDecision.selectedProvider
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("smart_decision_card"),
            colors = CardDefaults.cardColors(
              containerColor = prov.brandColor.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(14.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = when (prov) {
                      CloudProvider.GOOGLE_DRIVE -> Icons.Default.Cloud
                      CloudProvider.DROPBOX -> Icons.Default.Folder
                      CloudProvider.TELEGRAM -> Icons.Default.Send
                    },
                    contentDescription = null,
                    tint = prov.brandColor,
                    modifier = Modifier.size(24.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "Recommended Target",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                      text = prov.displayName,
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = prov.brandColor
                    )
                  }
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(prov.brandColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = "Optimal Route",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = smartDecision.reasonTitle,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = smartDecision.reasonExplanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
              )

              smartDecision.warningNote?.let { warning ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = warning,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                  )
                }
              }
            }
          }
        } else {
          // Manual Provider Selection
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CloudProvider.values().forEach { prov ->
              val account = accounts.find { it.providerId == prov.id }
              val isUnlimited = prov == CloudProvider.TELEGRAM
              val freeBytes = if (account != null && !isUnlimited) {
                (account.totalBytes - account.usedBytes).coerceAtLeast(0L)
              } else 0L
              val isSelected = manualProvider == prov
              val exceedsTg = prov == CloudProvider.TELEGRAM && fileSizeBytes > prov.maxFileSizeBytes

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) prov.brandColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                  .clickable { onManualProviderChange(prov) }
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = isSelected,
                  onClick = { onManualProviderChange(prov) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = prov.displayName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = prov.brandColor
                  )
                  Text(
                    text = if (isUnlimited) "Virtual Unlimited (2GB max/file)" else "${CloudStorageRepository.formatBytes(freeBytes)} free",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  if (exceedsTg) {
                    Text(
                      text = "Warning: File exceeds 2 GB ceiling for Telegram standard!",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.error
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // Upload Execution Card
    if (uploadState.isUploading) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("upload_progress_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(18.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Streaming Direct Chunks...",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Text(
              text = "${(uploadState.progress * 100).toInt()}%",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            )
          }

          LinearProgressIndicator(
            progress = { uploadState.progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(10.dp)
              .clip(RoundedCornerShape(5.dp)),
            color = MaterialTheme.colorScheme.primary
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "${CloudStorageRepository.formatBytes(uploadState.uploadedBytes)} / ${CloudStorageRepository.formatBytes(uploadState.totalBytes)}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "${uploadState.currentSpeedMb} MB/s",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.primary
            )
          }

          Text(
            text = "Negotiating direct resumable URL session with target cloud provider. Bypasses middleware bandwidth bottlenecks.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
          )
        }
      }
    } else if (uploadState.completedFile != null) {
      // Completed Success Card
      val completed = uploadState.completedFile
      val prov = CloudProvider.fromId(completed.providerId)

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("upload_success_card"),
        colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(18.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = SuccessGreen,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Upload Completed Successfully!",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Stored on ${prov.displayName} (${completed.remotePath})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = onNavigateToFiles,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("View in Explorer")
            }

            OutlinedButton(
              onClick = onResetUpload,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Upload Another")
            }
          }
        }
      }
    } else {
      // Start Upload Trigger Button
      Button(
        onClick = onStartUpload,
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("execute_upload_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF0061A4)
        ),
        shape = RoundedCornerShape(16.dp)
      ) {
        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        val targetName = if (routingMode == RoutingMode.SMART_AUTO) {
          smartDecision.selectedProvider.displayName
        } else {
          manualProvider.displayName
        }
        Text(
          text = "Push to $targetName",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
