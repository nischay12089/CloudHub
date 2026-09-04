package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cloud_accounts")
data class CloudAccountEntity(
  @PrimaryKey val providerId: String,
  val displayName: String,
  val accountEmail: String,
  val isConnected: Boolean,
  val usedBytes: Long,
  val totalBytes: Long,
  val authProtocol: String,
  val scopes: String,
  val lastSyncEpoch: Long
)

@Entity(tableName = "unified_files")
data class UnifiedFileEntity(
  @PrimaryKey val id: String,
  val name: String,
  val sizeBytes: Long,
  val mimeType: String,
  val category: String,
  val providerId: String,
  val remotePath: String,
  val uploadDateEpoch: Long,
  val isStarred: Boolean = false
)

@Entity(tableName = "upload_records")
data class UploadRecordEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val fileName: String,
  val sizeBytes: Long,
  val providerId: String,
  val routingReason: String,
  val timestampEpoch: Long,
  val status: String = "SUCCESS"
)
