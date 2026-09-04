package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.DropboxColor
import com.example.ui.theme.GoogleDriveColor
import com.example.ui.theme.TelegramColor

enum class CloudProvider(
  val id: String,
  val displayName: String,
  val defaultQuotaBytes: Long,
  val maxFileSizeBytes: Long,
  val brandColor: Color,
  val authProtocol: String,
  val defaultScopes: String
) {
  GOOGLE_DRIVE(
    id = "google_drive",
    displayName = "Google Drive",
    defaultQuotaBytes = 15L * 1024 * 1024 * 1024, // 15 GB
    maxFileSizeBytes = 5L * 1024 * 1024 * 1024 * 1024, // 5 TB
    brandColor = GoogleDriveColor,
    authProtocol = "OAuth 2.0 (Google Identity)",
    defaultScopes = "drive.file, drive.readonly"
  ),
  DROPBOX(
    id = "dropbox",
    displayName = "Dropbox",
    defaultQuotaBytes = 2L * 1024 * 1024 * 1024, // 2 GB Basic
    maxFileSizeBytes = 350L * 1024 * 1024 * 1024, // 350 GB API limit
    brandColor = DropboxColor,
    authProtocol = "OAuth 2.0 (PKCE Flow)",
    defaultScopes = "files.content.write, files.metadata.read"
  ),
  TELEGRAM(
    id = "telegram",
    displayName = "Telegram Cloud",
    defaultQuotaBytes = -1L, // Unlimited virtual storage
    maxFileSizeBytes = 2L * 1024 * 1024 * 1024, // 2 GB per file (4 GB Telegram Premium)
    brandColor = TelegramColor,
    authProtocol = "MTProto / TDLib Bridge",
    defaultScopes = "Saved Messages & Documents Stream"
  );

  companion object {
    fun fromId(id: String): CloudProvider = when (id.lowercase()) {
      "google_drive", "google", "drive" -> GOOGLE_DRIVE
      "dropbox" -> DROPBOX
      "telegram", "tg" -> TELEGRAM
      else -> GOOGLE_DRIVE
    }
  }
}

enum class FileCategory(val label: String) {
  ALL("All"),
  DOCUMENT("Docs"),
  IMAGE("Images"),
  VIDEO("Videos"),
  AUDIO("Audio"),
  ARCHIVE("Archives"),
  CODE("Code");

  companion object {
    fun fromMime(mimeType: String, extension: String = ""): FileCategory {
      val lowerMime = mimeType.lowercase()
      val lowerExt = extension.lowercase()
      return when {
        lowerMime.contains("pdf") || lowerMime.contains("text") || lowerMime.contains("word") ||
          lowerMime.contains("document") || lowerMime.contains("sheet") || lowerExt in listOf("pdf", "doc", "docx", "txt", "xlsx", "csv", "md") -> DOCUMENT
        lowerMime.startsWith("image/") || lowerExt in listOf("jpg", "jpeg", "png", "webp", "gif", "svg", "raw") -> IMAGE
        lowerMime.startsWith("video/") || lowerExt in listOf("mp4", "mkv", "mov", "avi", "webm") -> VIDEO
        lowerMime.startsWith("audio/") || lowerExt in listOf("mp3", "wav", "flac", "aac", "m4a") -> AUDIO
        lowerMime.contains("zip") || lowerMime.contains("tar") || lowerMime.contains("compressed") || lowerExt in listOf("zip", "rar", "7z", "tar", "gz") -> ARCHIVE
        lowerExt in listOf("kt", "java", "json", "dart", "py", "js", "html", "css", "ts") -> CODE
        else -> DOCUMENT
      }
    }
  }
}

data class StorageQuotaSummary(
  val totalUsedBytes: Long,
  val totalAllocatedBytes: Long,
  val usedPercentage: Float,
  val googleDriveUsedBytes: Long,
  val googleDriveTotalBytes: Long,
  val dropboxUsedBytes: Long,
  val dropboxTotalBytes: Long,
  val telegramTotalUploadedBytes: Long,
  val totalFilesCount: Int,
  val activeProvidersCount: Int
)

data class SmartRoutingDecision(
  val selectedProvider: CloudProvider,
  val reasonTitle: String,
  val reasonExplanation: String,
  val fitsLimits: Boolean,
  val warningNote: String? = null
)
