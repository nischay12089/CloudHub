package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DriveFileMove
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UnifiedFileEntity
import com.example.data.model.CloudProvider
import com.example.data.model.FileCategory
import com.example.data.repository.CloudStorageRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FileItemCard(
  file: UnifiedFileEntity,
  onFileClick: () -> Unit,
  onToggleStar: () -> Unit,
  onMoveClick: () -> Unit,
  onDeleteClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showMenu by remember { mutableStateOf(false) }
  val provider = CloudProvider.fromId(file.providerId)
  val category = try {
    FileCategory.valueOf(file.category)
  } catch (e: Exception) {
    FileCategory.DOCUMENT
  }

  val (catIcon, catBgColor, catIconColor) = when (category) {
    FileCategory.DOCUMENT -> Triple(Icons.Default.Description, Color(0xFFE0F2FE), Color(0xFF0284C7))
    FileCategory.IMAGE -> Triple(Icons.Default.Image, Color(0xFFF3E8FF), Color(0xFF9333EA))
    FileCategory.VIDEO -> Triple(Icons.Default.VideoFile, Color(0xFFFFEDD5), Color(0xFFEA580C))
    FileCategory.AUDIO -> Triple(Icons.Default.AudioFile, Color(0xFFDCFCE7), Color(0xFF16A34A))
    FileCategory.ARCHIVE -> Triple(Icons.Default.Archive, Color(0xFFFEF3C7), Color(0xFFD97706))
    FileCategory.CODE -> Triple(Icons.Default.Code, Color(0xFFF1F5F9), Color(0xFF475569))
    FileCategory.ALL -> Triple(Icons.Default.Folder, Color(0xFFF1F5F9), Color(0xFF64748B))
  }

  val dateStr = remember(file.uploadDateEpoch) {
    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    formatter.format(Date(file.uploadDateEpoch))
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onFileClick() }
      .testTag("file_card_${file.id}"),
    colors = CardDefaults.cardColors(
      containerColor = Color.White
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1E2E9)),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    shape = RoundedCornerShape(20.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Category Icon
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(catBgColor),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = catIcon,
          contentDescription = file.category,
          tint = catIconColor,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Info
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = file.name,
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          color = Color(0xFF1A1C1E)
        )

        Spacer(modifier = Modifier.height(3.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = CloudStorageRepository.formatBytes(file.sizeBytes),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF44474E)
          )

          Box(
            modifier = Modifier
              .size(3.dp)
              .background(Color(0xFFE1E2E9), CircleShape)
          )

          Text(
            text = dateStr,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF44474E)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Provider Badge
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(provider.brandColor.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Icon(
            imageVector = when (provider) {
              CloudProvider.GOOGLE_DRIVE -> Icons.Default.Cloud
              CloudProvider.DROPBOX -> Icons.Default.Folder
              CloudProvider.TELEGRAM -> Icons.Default.Send
            },
            contentDescription = provider.displayName,
            tint = provider.brandColor,
            modifier = Modifier.size(11.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = provider.displayName,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            ),
            color = provider.brandColor
          )
        }
      }

      // Star Icon Button
      IconButton(
        onClick = onToggleStar,
        modifier = Modifier.size(38.dp)
      ) {
        Icon(
          imageVector = if (file.isStarred) Icons.Filled.Star else Icons.Outlined.StarBorder,
          contentDescription = if (file.isStarred) "Starred" else "Unstarred",
          tint = if (file.isStarred) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.size(20.dp)
        )
      }

      // Overflow Menu
      Box {
        IconButton(
          onClick = { showMenu = true },
          modifier = Modifier.size(38.dp)
        ) {
          Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "File options",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
        }

        DropdownMenu(
          expanded = showMenu,
          onDismissRequest = { showMenu = false }
        ) {
          DropdownMenuItem(
            text = { Text("View Details") },
            leadingIcon = {
              Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            onClick = {
              showMenu = false
              onFileClick()
            }
          )
          DropdownMenuItem(
            text = { Text("Migrate Provider") },
            leadingIcon = {
              Icon(Icons.Outlined.DriveFileMove, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            onClick = {
              showMenu = false
              onMoveClick()
            }
          )
          DropdownMenuItem(
            text = { Text("Delete File", color = MaterialTheme.colorScheme.error) },
            leadingIcon = {
              Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            },
            onClick = {
              showMenu = false
              onDeleteClick()
            }
          )
        }
      }
    }
  }
}
