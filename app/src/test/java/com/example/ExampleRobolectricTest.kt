package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CloudAccountEntity
import com.example.data.local.CloudDao
import com.example.data.model.CloudProvider
import com.example.data.repository.CloudStorageRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Unified Cloud", appName)
  }

  @Test
  fun `test smart routing large file diverted to telegram`() {
    val accounts = listOf(
      CloudAccountEntity(
        providerId = CloudProvider.GOOGLE_DRIVE.id,
        displayName = "Google Drive",
        accountEmail = "user@gmail.com",
        isConnected = true,
        usedBytes = 11_000_000_000L,
        totalBytes = 15_000_000_000L,
        authProtocol = "OAuth 2.0",
        scopes = "drive.file",
        lastSyncEpoch = System.currentTimeMillis()
      ),
      CloudAccountEntity(
        providerId = CloudProvider.DROPBOX.id,
        displayName = "Dropbox",
        accountEmail = "user@dropbox.com",
        isConnected = true,
        usedBytes = 1_500_000_000L,
        totalBytes = 2_000_000_000L,
        authProtocol = "OAuth 2.0",
        scopes = "files.content.write",
        lastSyncEpoch = System.currentTimeMillis()
      ),
      CloudAccountEntity(
        providerId = CloudProvider.TELEGRAM.id,
        displayName = "Telegram",
        accountEmail = "+1234567890",
        isConnected = true,
        usedBytes = 5_000_000_000L,
        totalBytes = -1L,
        authProtocol = "MTProto",
        scopes = "Saved Messages",
        lastSyncEpoch = System.currentTimeMillis()
      )
    )

    // Dummy DAO for test instance
    val repo = CloudStorageRepository(object : CloudDao {
      override fun getAllAccounts() = kotlinx.coroutines.flow.flowOf(accounts)
      override suspend fun getAccountById(providerId: String) = accounts.find { it.providerId == providerId }
      override suspend fun insertAccount(account: CloudAccountEntity) {}
      override suspend fun insertAccounts(accounts: List<CloudAccountEntity>) {}
      override suspend fun updateAccount(account: CloudAccountEntity) {}
      override suspend fun updateConnectionStatus(providerId: String, connected: Boolean) {}
      override suspend fun updateAccountUsage(providerId: String, usedBytes: Long, syncEpoch: Long) {}
      override fun getAllFiles() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.local.UnifiedFileEntity>())
      override fun getFilesByProvider(providerId: String) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.local.UnifiedFileEntity>())
      override fun getFilesByCategory(category: String) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.local.UnifiedFileEntity>())
      override suspend fun getFileById(id: String) = null
      override suspend fun insertFile(file: com.example.data.local.UnifiedFileEntity) {}
      override suspend fun insertFiles(files: List<com.example.data.local.UnifiedFileEntity>) {}
      override suspend fun deleteFileById(id: String) {}
      override suspend fun toggleStarFile(id: String) {}
      override suspend fun moveFileProvider(id: String, newProviderId: String, newRemotePath: String) {}
      override fun getRecentUploads() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.local.UploadRecordEntity>())
      override suspend fun insertUploadRecord(record: com.example.data.local.UploadRecordEntity) {}
    })

    // 750 MB file should be auto-routed to Telegram (>500MB rule)
    val decision = repo.determineOptimalProvider(750L * 1024 * 1024, accounts)
    assertEquals(CloudProvider.TELEGRAM, decision.selectedProvider)
    assertTrue(decision.fitsLimits)

    // 40 MB file should be routed to Google Drive (largest headroom)
    val smallDecision = repo.determineOptimalProvider(40L * 1024 * 1024, accounts)
    assertEquals(CloudProvider.GOOGLE_DRIVE, smallDecision.selectedProvider)
  }

  @Test
  fun `test firebase auth manager initializes and provides auth state`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val authManager = com.example.auth.FirebaseAuthManager.getInstance(context)
    val state = authManager.userState.value
    // Initial state should not be logged in or anonymous
    org.junit.Assert.assertNotNull(state)
  }
}
