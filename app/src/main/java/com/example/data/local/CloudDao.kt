package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CloudDao {

  // Accounts
  @Query("SELECT * FROM cloud_accounts ORDER BY providerId ASC")
  fun getAllAccounts(): Flow<List<CloudAccountEntity>>

  @Query("SELECT * FROM cloud_accounts WHERE providerId = :providerId LIMIT 1")
  suspend fun getAccountById(providerId: String): CloudAccountEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAccount(account: CloudAccountEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAccounts(accounts: List<CloudAccountEntity>)

  @Update
  suspend fun updateAccount(account: CloudAccountEntity)

  @Query("UPDATE cloud_accounts SET isConnected = :connected WHERE providerId = :providerId")
  suspend fun updateConnectionStatus(providerId: String, connected: Boolean)

  @Query("UPDATE cloud_accounts SET usedBytes = :usedBytes, lastSyncEpoch = :syncEpoch WHERE providerId = :providerId")
  suspend fun updateAccountUsage(providerId: String, usedBytes: Long, syncEpoch: Long)

  // Files
  @Query("SELECT * FROM unified_files ORDER BY uploadDateEpoch DESC")
  fun getAllFiles(): Flow<List<UnifiedFileEntity>>

  @Query("SELECT * FROM unified_files WHERE providerId = :providerId ORDER BY uploadDateEpoch DESC")
  fun getFilesByProvider(providerId: String): Flow<List<UnifiedFileEntity>>

  @Query("SELECT * FROM unified_files WHERE category = :category ORDER BY uploadDateEpoch DESC")
  fun getFilesByCategory(category: String): Flow<List<UnifiedFileEntity>>

  @Query("SELECT * FROM unified_files WHERE id = :id LIMIT 1")
  suspend fun getFileById(id: String): UnifiedFileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFile(file: UnifiedFileEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFiles(files: List<UnifiedFileEntity>)

  @Query("DELETE FROM unified_files WHERE id = :id")
  suspend fun deleteFileById(id: String)

  @Query("UPDATE unified_files SET isStarred = NOT isStarred WHERE id = :id")
  suspend fun toggleStarFile(id: String)

  @Query("UPDATE unified_files SET providerId = :newProviderId, remotePath = :newRemotePath WHERE id = :id")
  suspend fun moveFileProvider(id: String, newProviderId: String, newRemotePath: String)

  // Upload logs
  @Query("SELECT * FROM upload_records ORDER BY timestampEpoch DESC LIMIT 20")
  fun getRecentUploads(): Flow<List<UploadRecordEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUploadRecord(record: UploadRecordEntity)
}
