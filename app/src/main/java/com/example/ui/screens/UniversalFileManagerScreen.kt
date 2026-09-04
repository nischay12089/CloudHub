package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
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
import com.example.data.local.UnifiedFileEntity
import com.example.data.model.CloudProvider
import com.example.data.model.FileCategory
import com.example.data.repository.CloudStorageRepository
import com.example.ui.components.FileItemCard
import com.example.ui.viewmodel.SortOption
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UniversalFileManagerScreen(
  files: List<UnifiedFileEntity>,
  searchQuery: String,
  selectedProvider: String?,
  selectedCategory: FileCategory,
  sortOption: SortOption,
  onSearchChange: (String) -> Unit,
  onProviderSelect: (String?) -> Unit,
  onCategorySelect: (FileCategory) -> Unit,
  onSortSelect: (SortOption) -> Unit,
  onToggleStar: (String) -> Unit,
  onDeleteFile: (String) -> Unit,
  onMoveFile: (String, String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedFileForDetails by remember { mutableStateOf<UnifiedFileEntity?>(null) }
  var selectedFileForMove by remember { mutableStateOf<UnifiedFileEntity?>(null) }
  var showSortMenu by remember { mutableStateOf(false) }

  val totalBytes = remember(files) {
    files.sumOf { it.sizeBytes }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    // Top Title & Sort Action
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "ALL STORAGE ASSETS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            fontSize = 10.sp
          ),
          color = Color(0xFF0061A4)
        )
        Text(
          text = "Universal Files",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
          ),
          color = Color(0xFF1A1C1E)
        )
        Text(
          text = "${files.size} files • ${CloudStorageRepository.formatBytes(totalBytes)} combined",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF44474E)
        )
      }

      Box {
        OutlinedButton(
          onClick = { showSortMenu = true },
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1E2E9)),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF1A1C1E)
          ),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.Sort, contentDescription = "Sort", modifier = Modifier.size(16.dp), tint = Color(0xFF0061A4))
          Spacer(modifier = Modifier.width(6.dp))
          Text(sortOption.label, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
        }

        DropdownMenu(
          expanded = showSortMenu,
          onDismissRequest = { showSortMenu = false }
        ) {
          SortOption.values().forEach { option ->
            DropdownMenuItem(
              text = { Text(option.label) },
              onClick = {
                onSortSelect(option)
                showSortMenu = false
              }
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onSearchChange,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("file_search_input"),
      placeholder = { Text("Search by name, path, extension...", color = Color(0xFF44474E).copy(alpha = 0.7f)) },
      leadingIcon = {
        Icon(
          Icons.Default.Search,
          contentDescription = "Search",
          tint = Color(0xFF0061A4)
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onSearchChange("") }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF44474E))
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF0061A4),
        unfocusedBorderColor = Color(0xFFE1E2E9),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedTextColor = Color(0xFF1A1C1E),
        unfocusedTextColor = Color(0xFF1A1C1E)
      )
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Provider Filter Horizontal Row
    val providerScrollState = rememberScrollState()
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(providerScrollState),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FilterChip(
        selected = selectedProvider == null,
        onClick = { onProviderSelect(null) },
        label = { Text("All Clouds") },
        shape = RoundedCornerShape(32.dp),
        border = FilterChipDefaults.filterChipBorder(
          enabled = true,
          selected = selectedProvider == null,
          borderColor = Color(0xFFE1E2E9),
          selectedBorderColor = Color(0xFF0061A4)
        ),
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = Color(0xFFD1E4FF),
          selectedLabelColor = Color(0xFF0061A4),
          containerColor = Color.White,
          labelColor = Color(0xFF44474E)
        )
      )

      CloudProvider.values().forEach { prov ->
        val isSelected = selectedProvider == prov.id
        FilterChip(
          selected = isSelected,
          onClick = { onProviderSelect(if (isSelected) null else prov.id) },
          label = { Text(prov.displayName) },
          leadingIcon = {
            Icon(
              imageVector = when (prov) {
                CloudProvider.GOOGLE_DRIVE -> Icons.Default.Cloud
                CloudProvider.DROPBOX -> Icons.Default.Folder
                CloudProvider.TELEGRAM -> Icons.Default.Send
              },
              contentDescription = null,
              tint = if (isSelected) Color.White else prov.brandColor,
              modifier = Modifier.size(14.dp)
            )
          },
          shape = RoundedCornerShape(32.dp),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = Color(0xFFE1E2E9),
            selectedBorderColor = prov.brandColor
          ),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = prov.brandColor,
            selectedLabelColor = Color.White,
            containerColor = Color.White,
            labelColor = Color(0xFF44474E)
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Category Filter Horizontal Row
    val categoryScrollState = rememberScrollState()
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(categoryScrollState),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      FileCategory.values().forEach { cat ->
        val isCatSelected = selectedCategory == cat
        FilterChip(
          selected = isCatSelected,
          onClick = { onCategorySelect(cat) },
          label = { Text(cat.label) },
          shape = RoundedCornerShape(32.dp),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isCatSelected,
            borderColor = Color(0xFFE1E2E9),
            selectedBorderColor = Color(0xFF0061A4)
          ),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFD1E4FF),
            selectedLabelColor = Color(0xFF0061A4),
            containerColor = Color.White,
            labelColor = Color(0xFF44474E)
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // File List
    if (files.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
          )
          Text(
            text = "No files match your filters",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "Try clearing search keywords or switching provider filters.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .testTag("file_list_view"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
      ) {
        items(files, key = { it.id }) { file ->
          FileItemCard(
            file = file,
            onFileClick = { selectedFileForDetails = file },
            onToggleStar = { onToggleStar(file.id) },
            onMoveClick = { selectedFileForMove = file },
            onDeleteClick = { onDeleteFile(file.id) }
          )
        }
      }
    }
  }

  // File Details Dialog
  selectedFileForDetails?.let { file ->
    val provider = CloudProvider.fromId(file.providerId)
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(file.uploadDateEpoch))

    AlertDialog(
      onDismissRequest = { selectedFileForDetails = null },
      title = {
        Text(
          text = "File Metadata",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          DetailRow("Filename", file.name)
          DetailRow("Provider", provider.displayName)
          DetailRow("Remote Path", file.remotePath)
          DetailRow("File Size", "${CloudStorageRepository.formatBytes(file.sizeBytes)} (${file.sizeBytes} bytes)")
          DetailRow("MIME Type", file.mimeType)
          DetailRow("Category", file.category)
          DetailRow("Uploaded", dateStr)
        }
      },
      confirmButton = {
        Button(
          onClick = { selectedFileForDetails = null },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Close")
        }
      }
    )
  }

  // Migrate / Move Dialog
  selectedFileForMove?.let { file ->
    var targetProvider by remember {
      mutableStateOf(
        CloudProvider.values().firstOrNull { it.id != file.providerId } ?: CloudProvider.GOOGLE_DRIVE
      )
    }

    AlertDialog(
      onDismissRequest = { selectedFileForMove = null },
      title = {
        Text(
          text = "Migrate Cloud Storage",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "Transfer \"${file.name}\" to another connected drive. Quota will be freed on ${CloudProvider.fromId(file.providerId).displayName}.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Text(
            text = "Select Destination:",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
          )

          CloudProvider.values().filter { it.id != file.providerId }.forEach { prov ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (targetProvider == prov) prov.brandColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = targetProvider == prov,
                onClick = { targetProvider = prov }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = prov.displayName,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = prov.brandColor
                )
                Text(
                  text = if (prov == CloudProvider.TELEGRAM) "Saved Messages Channel" else "Direct transfer",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onMoveFile(file.id, file.providerId, targetProvider.id)
            selectedFileForMove = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = targetProvider.brandColor),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Migrate Now")
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedFileForMove = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun DetailRow(label: String, value: String) {
  Column {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.primary
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
