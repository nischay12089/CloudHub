package com.example.data.repository

import com.example.data.local.CloudAccountEntity
import com.example.data.local.CloudDao
import com.example.data.local.UnifiedFileEntity
import com.example.data.local.UploadRecordEntity
import com.example.data.model.CloudProvider
import com.example.data.model.FileCategory
import com.example.data.model.SmartRoutingDecision
import com.example.data.model.StorageQuotaSummary
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

class CloudStorageRepository(
  private val cloudDao: CloudDao,
  private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

  val allAccounts: Flow<List<CloudAccountEntity>> = cloudDao.getAllAccounts()
  val allFiles: Flow<List<UnifiedFileEntity>> = cloudDao.getAllFiles()
  val recentUploads: Flow<List<UploadRecordEntity>> = cloudDao.getRecentUploads()

  suspend fun ensureInitialData() = withContext(ioDispatcher) {
    val existingAccounts = cloudDao.getAllAccounts().first()
    if (existingAccounts.isEmpty()) {
      val now = System.currentTimeMillis()

      // Initial Accounts
      val accounts = listOf(
        CloudAccountEntity(
          providerId = CloudProvider.GOOGLE_DRIVE.id,
          displayName = "Google Drive",
          accountEmail = "user.workspace@gmail.com",
          isConnected = true,
          usedBytes = 11_420_000_000L, // ~11.42 GB
          totalBytes = 15_000_000_000L, // 15 GB
          authProtocol = "OAuth 2.0 (Google Workspace)",
          scopes = "drive.file, drive.readonly",
          lastSyncEpoch = now - 180_000L
        ),
        CloudAccountEntity(
          providerId = CloudProvider.DROPBOX.id,
          displayName = "Dropbox Personal",
          accountEmail = "user.dropbox@company.com",
          isConnected = true,
          usedBytes = 1_450_000_000L, // ~1.45 GB
          totalBytes = 2_000_000_000L, // 2 GB
          authProtocol = "OAuth 2.0 (PKCE Token)",
          scopes = "files.content.write, files.metadata.read",
          lastSyncEpoch = now - 320_000L
        ),
        CloudAccountEntity(
          providerId = CloudProvider.TELEGRAM.id,
          displayName = "Telegram Cloud (Saved)",
          accountEmail = "+1 (555) 019-2834",
          isConnected = true,
          usedBytes = 8_750_000_000L, // ~8.75 GB consumed in messages
          totalBytes = -1L, // Unlimited
          authProtocol = "MTProto / TDLib Session",
          scopes = "Saved Messages Document Channel",
          lastSyncEpoch = now - 60_000L
        )
      )
      cloudDao.insertAccounts(accounts)

      // Initial Files
      val initialFiles = listOf(
        UnifiedFileEntity(
          id = UUID.randomUUID().toString(),
          name = "Quarterly_Financial_Review_2026.pdf",
          sizeBytes = 24_500_000L,
          mimeType = "application/pdf",
          category = FileCategory.DOCUMENT.name,
          providerId = CloudProvider.GOOGLE_DRIVE.id,
          remotePath = "/Finance/Q3/Quarterly_Financial_Review_2026.pdf",
          uploadDateEpoch = now - 3_600_000L * 2,
          isStarred = true
        ),
        UnifiedFileEntity(
          id = UUID.randomUUID().toString(),
          name = "Client_Presentation_Deck.pptx",
          sizeBytes = 84_000_000L,
          mimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation",
          category = FileCategory.DOCUMENT.name,
          providerId = CloudProvider.DROPBOX.id,
          remotePath = "/Projects/Pitch/Client_Presentation_Deck.pptx",
          uploadDateEpoch = now - 3_600_000L * 7,
          isStarred = false
        ),
        UnifiedFileEntity(
          id = UUID.randomUUID().toString(),
          name = "Product_Demo_4K_Master.mp4",
          sizeBytes = 1_250_000_000L, // 1.25 GB -> Routed to Telegram
          mimeType = "video/mp4",
          category = FileCategory.VIDEO.name,
          providerId = CloudProvider.TELEGRAM.id,
          remotePath = "Telegram Saved Messages #4812",
          uploadDateEpoch = now - 3_600_000L * 14,
          isStarred = true
        ),
        UnifiedFileEntity(
          id = UUID.randomUUID().toString(),
          name = "Raw_Branding_Assets_Archive.zip",
          sizeBytes = 620_000_000L, // 620 MB -> Routed to Telegram
          mimeType = "application/zip",
          category = FileCategory.ARCHIVE.name,
          providerId = CloudProvider.TELEGRAM.id,
          remotePath = "Telegram Saved Messages #4813",
          uploadDateEpoch = now - 3_600_000L * 22,
          isStarred = false
        ),
        UnifiedFileEntity(
          id = UUID.randomUUID().toString(),
          name = "App_Architecture_Diagram.png",
          sizeBytes = 8_400_000L,
          mimeType = "image/png",
          category = FileCategory.IMAGE.name,
          providerId = CloudProvider.GOOGLE_DRIVE.id,
          remotePath = "/Design/Specs/App_Architecture_Diagram.png",
          uploadDateEpoch = now - 3_600_000L * 30,
          isStarred = true
        ),
        UnifiedFileEntity(
          id = UUID.randomUUID().toString(),
          name = "Machine_Learning_Dataset_Cleaned.csv",
          sizeBytes = 145_000_000L,
          mimeType = "text/csv",
          category = FileCategory.DOCUMENT.name,
          providerId = CloudProvider.GOOGLE_DRIVE.id,
          remotePath = "/Data/Machine_Learning_Dataset_Cleaned.csv",
          uploadDateEpoch = now - 3_600_000L * 40,
          isStarred = false
        ),
        UnifiedFileEntity(
          id = UUID.randomUUID().toString(),
          name = "Podcast_Episode_32_HQ.flac",
          sizeBytes = 380_000_000L,
          mimeType = "audio/flac",
          category = FileCategory.AUDIO.name,
          providerId = CloudProvider.DROPBOX.id,
          remotePath = "/Media/Audio/Podcast_Episode_32_HQ.flac",
          uploadDateEpoch = now - 3_600_000L * 60,
          isStarred = false
        ),
        UnifiedFileEntity(
          id = UUID.randomUUID().toString(),
          name = "Docker_Cluster_Setup.yaml",
          sizeBytes = 42_000L,
          mimeType = "text/yaml",
          category = FileCategory.CODE.name,
          providerId = CloudProvider.GOOGLE_DRIVE.id,
          remotePath = "/DevOps/Docker_Cluster_Setup.yaml",
          uploadDateEpoch = now - 3_600_000L * 72,
          isStarred = false
        )
      )
      cloudDao.insertFiles(initialFiles)

      // Initial Upload Log
      val initialLogs = listOf(
        UploadRecordEntity(
          fileName = "Product_Demo_4K_Master.mp4",
          sizeBytes = 1_250_000_000L,
          providerId = CloudProvider.TELEGRAM.id,
          routingReason = "Large file (>500MB) auto-routed to Telegram to preserve Cloud Drive quotas",
          timestampEpoch = now - 3_600_000L * 14
        ),
        UploadRecordEntity(
          fileName = "Raw_Branding_Assets_Archive.zip",
          sizeBytes = 620_000_000L,
          providerId = CloudProvider.TELEGRAM.id,
          routingReason = "Auto-routed to Telegram unlimited storage pool",
          timestampEpoch = now - 3_600_000L * 22
        ),
        UploadRecordEntity(
          fileName = "Quarterly_Financial_Review_2026.pdf",
          sizeBytes = 24_500_000L,
          providerId = CloudProvider.GOOGLE_DRIVE.id,
          routingReason = "Standard doc routed to primary Google Drive workspace",
          timestampEpoch = now - 3_600_000L * 2
        )
      )
      initialLogs.forEach { cloudDao.insertUploadRecord(it) }
    }
  }

  fun calculateSummary(
    accounts: List<CloudAccountEntity>,
    files: List<UnifiedFileEntity>
  ): StorageQuotaSummary {
    var fixedUsed = 0L
    var fixedTotal = 0L
    var gDriveUsed = 0L
    var gDriveTotal = 0L
    var dropboxUsed = 0L
    var dropboxTotal = 0L
    var telegramUsed = 0L
    var activeCount = 0

    for (acc in accounts) {
      if (acc.isConnected) {
        activeCount++
        when (acc.providerId) {
          CloudProvider.GOOGLE_DRIVE.id -> {
            gDriveUsed = acc.usedBytes
            gDriveTotal = acc.totalBytes
            fixedUsed += acc.usedBytes
            fixedTotal += acc.totalBytes
          }
          CloudProvider.DROPBOX.id -> {
            dropboxUsed = acc.usedBytes
            dropboxTotal = acc.totalBytes
            fixedUsed += acc.usedBytes
            fixedTotal += acc.totalBytes
          }
          CloudProvider.TELEGRAM.id -> {
            telegramUsed = acc.usedBytes
          }
        }
      }
    }

    val percentage = if (fixedTotal > 0L) {
      (fixedUsed.toFloat() / fixedTotal.toFloat()) * 100f
    } else 0f

    return StorageQuotaSummary(
      totalUsedBytes = fixedUsed,
      totalAllocatedBytes = fixedTotal,
      usedPercentage = percentage,
      googleDriveUsedBytes = gDriveUsed,
      googleDriveTotalBytes = gDriveTotal,
      dropboxUsedBytes = dropboxUsed,
      dropboxTotalBytes = dropboxTotal,
      telegramTotalUploadedBytes = telegramUsed,
      totalFilesCount = files.size,
      activeProvidersCount = activeCount
    )
  }

  fun determineOptimalProvider(
    fileSizeBytes: Long,
    accounts: List<CloudAccountEntity>
  ): SmartRoutingDecision {
    val tgAccount = accounts.find { it.providerId == CloudProvider.TELEGRAM.id }
    val gDriveAccount = accounts.find { it.providerId == CloudProvider.GOOGLE_DRIVE.id }
    val dropboxAccount = accounts.find { it.providerId == CloudProvider.DROPBOX.id }

    val isTelegramConnected = tgAccount?.isConnected == true
    val isGDriveConnected = gDriveAccount?.isConnected == true
    val isDropboxConnected = dropboxAccount?.isConnected == true

    val gDriveFree = if (isGDriveConnected && gDriveAccount != null) {
      (gDriveAccount.totalBytes - gDriveAccount.usedBytes).coerceAtLeast(0L)
    } else 0L

    val dropboxFree = if (isDropboxConnected && dropboxAccount != null) {
      (dropboxAccount.totalBytes - dropboxAccount.usedBytes).coerceAtLeast(0L)
    } else 0L

    val telegramMaxSizeBytes = CloudProvider.TELEGRAM.maxFileSizeBytes // 2 GB

    // Check if file is bigger than Telegram's 2GB limit
    if (fileSizeBytes > telegramMaxSizeBytes) {
      // File cannot go to Telegram
      return when {
        isGDriveConnected && gDriveFree >= fileSizeBytes -> {
          SmartRoutingDecision(
            selectedProvider = CloudProvider.GOOGLE_DRIVE,
            reasonTitle = "Google Drive Capacity",
            reasonExplanation = "File (${formatBytes(fileSizeBytes)}) exceeds Telegram's 2GB ceiling. Routed to Google Drive which has ${formatBytes(gDriveFree)} headroom.",
            fitsLimits = true,
            warningNote = "Exceeds Telegram 2GB limit."
          )
        }
        isDropboxConnected && dropboxFree >= fileSizeBytes -> {
          SmartRoutingDecision(
            selectedProvider = CloudProvider.DROPBOX,
            reasonTitle = "Dropbox Capacity",
            reasonExplanation = "File exceeds Telegram's 2GB ceiling. Routed to Dropbox which has ${formatBytes(dropboxFree)} free space.",
            fitsLimits = true
          )
        }
        else -> {
          SmartRoutingDecision(
            selectedProvider = CloudProvider.GOOGLE_DRIVE,
            reasonTitle = "Storage Quota Exceeded",
            reasonExplanation = "File exceeds Telegram 2GB limit and no cloud drive has enough available space.",
            fitsLimits = false,
            warningNote = "Insufficient quota on connected drives."
          )
        }
      }
    }

    // Rule 2: Large file (> 500 MB) and fits in Telegram -> prioritize Telegram!
    val largeFileThreshold = 500L * 1024 * 1024 // 500 MB
    if (isTelegramConnected && fileSizeBytes >= largeFileThreshold) {
      return SmartRoutingDecision(
        selectedProvider = CloudProvider.TELEGRAM,
        reasonTitle = "Smart Quota Preservation",
        reasonExplanation = "File size (${formatBytes(fileSizeBytes)}) is over 500 MB. Routing to Telegram Cloud preserves limited quotas on Google Drive and Dropbox.",
        fitsLimits = true
      )
    }

    // Rule 3: Traditional drives based on remaining headroom
    if (gDriveFree >= fileSizeBytes || dropboxFree >= fileSizeBytes) {
      return if (gDriveFree >= dropboxFree && isGDriveConnected) {
        SmartRoutingDecision(
          selectedProvider = CloudProvider.GOOGLE_DRIVE,
          reasonTitle = "Optimal Available Headroom",
          reasonExplanation = "Google Drive has the most available capacity (${formatBytes(gDriveFree)} free vs ${formatBytes(dropboxFree)} on Dropbox).",
          fitsLimits = true
        )
      } else if (isDropboxConnected && dropboxFree >= fileSizeBytes) {
        SmartRoutingDecision(
          selectedProvider = CloudProvider.DROPBOX,
          reasonTitle = "Optimal Available Headroom",
          reasonExplanation = "Dropbox selected based on available capacity (${formatBytes(dropboxFree)} free).",
          fitsLimits = true
        )
      } else {
        SmartRoutingDecision(
          selectedProvider = CloudProvider.GOOGLE_DRIVE,
          reasonTitle = "Primary Workspace Drive",
          reasonExplanation = "Routed to Google Drive.",
          fitsLimits = true
        )
      }
    }

    // Rule 4: Fallback to Telegram if cloud drives are full but file <= 2GB
    if (isTelegramConnected) {
      return SmartRoutingDecision(
        selectedProvider = CloudProvider.TELEGRAM,
        reasonTitle = "Unlimited Fallback Pool",
        reasonExplanation = "Google Drive and Dropbox have insufficient free space. Routed to Telegram unlimited storage pool.",
        fitsLimits = true
      )
    }

    return SmartRoutingDecision(
      selectedProvider = CloudProvider.GOOGLE_DRIVE,
      reasonTitle = "Default Drive",
      reasonExplanation = "No optimal route found; selected default provider.",
      fitsLimits = false,
      warningNote = "All storage drives are near or at full capacity."
    )
  }

  suspend fun executeUpload(
    fileName: String,
    fileSizeBytes: Long,
    mimeType: String,
    category: String,
    provider: CloudProvider,
    routingReason: String
  ): UnifiedFileEntity = withContext(ioDispatcher) {
    val fileId = UUID.randomUUID().toString()
    val now = System.currentTimeMillis()

    val path = when (provider) {
      CloudProvider.GOOGLE_DRIVE -> "/Uploads/${fileName}"
      CloudProvider.DROPBOX -> "/MyFiles/${fileName}"
      CloudProvider.TELEGRAM -> "Telegram Saved Messages #${(1000..9999).random()}"
    }

    val newFile = UnifiedFileEntity(
      id = fileId,
      name = fileName,
      sizeBytes = fileSizeBytes,
      mimeType = mimeType,
      category = category,
      providerId = provider.id,
      remotePath = path,
      uploadDateEpoch = now,
      isStarred = false
    )

    cloudDao.insertFile(newFile)

    // Update provider usage
    val account = cloudDao.getAccountById(provider.id)
    if (account != null) {
      val newUsage = account.usedBytes + fileSizeBytes
      cloudDao.updateAccountUsage(provider.id, newUsage, now)
    }

    // Insert log record
    cloudDao.insertUploadRecord(
      UploadRecordEntity(
        fileName = fileName,
        sizeBytes = fileSizeBytes,
        providerId = provider.id,
        routingReason = routingReason,
        timestampEpoch = now
      )
    )

    newFile
  }

  suspend fun moveFile(
    fileId: String,
    fromProviderId: String,
    toProviderId: String
  ) = withContext(ioDispatcher) {
    val file = cloudDao.getFileById(fileId) ?: return@withContext
    val newPath = when (toProviderId) {
      CloudProvider.GOOGLE_DRIVE.id -> "/Transferred/${file.name}"
      CloudProvider.DROPBOX.id -> "/Transferred/${file.name}"
      CloudProvider.TELEGRAM.id -> "Telegram Saved Messages #${(1000..9999).random()}"
      else -> "/Transferred/${file.name}"
    }

    cloudDao.moveFileProvider(fileId, toProviderId, newPath)

    // Decrement from old provider
    val oldAcc = cloudDao.getAccountById(fromProviderId)
    if (oldAcc != null) {
      val updated = (oldAcc.usedBytes - file.sizeBytes).coerceAtLeast(0L)
      cloudDao.updateAccountUsage(fromProviderId, updated, System.currentTimeMillis())
    }

    // Increment on new provider
    val newAcc = cloudDao.getAccountById(toProviderId)
    if (newAcc != null) {
      val updated = newAcc.usedBytes + file.sizeBytes
      cloudDao.updateAccountUsage(toProviderId, updated, System.currentTimeMillis())
    }
  }

  suspend fun deleteFile(fileId: String) = withContext(ioDispatcher) {
    val file = cloudDao.getFileById(fileId) ?: return@withContext
    cloudDao.deleteFileById(fileId)

    // Decrement provider usage
    val acc = cloudDao.getAccountById(file.providerId)
    if (acc != null) {
      val updated = (acc.usedBytes - file.sizeBytes).coerceAtLeast(0L)
      cloudDao.updateAccountUsage(file.providerId, updated, System.currentTimeMillis())
    }
  }

  suspend fun toggleStar(fileId: String) = withContext(ioDispatcher) {
    cloudDao.toggleStarFile(fileId)
  }

  suspend fun toggleAccountConnection(providerId: String, connected: Boolean) = withContext(ioDispatcher) {
    cloudDao.updateConnectionStatus(providerId, connected)
  }

  suspend fun updateAccount(account: CloudAccountEntity) = withContext(ioDispatcher) {
    cloudDao.updateAccount(account)
  }

  companion object {
    fun formatBytes(bytes: Long): String {
      if (bytes < 0) return "Unlimited"
      val kb = bytes / 1024.0
      val mb = kb / 1024.0
      val gb = mb / 1024.0
      val tb = gb / 1024.0
      return when {
        tb >= 1.0 -> String.format("%.2f TB", tb)
        gb >= 1.0 -> String.format("%.2f GB", gb)
        mb >= 1.0 -> String.format("%.1f MB", mb)
        kb >= 1.0 -> String.format("%.1f KB", kb)
        else -> "$bytes B"
      }
    }
  }
}
