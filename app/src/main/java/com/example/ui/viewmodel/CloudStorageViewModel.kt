package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.AuthUserState
import com.example.auth.FirebaseAuthManager
import com.example.data.local.AppDatabase
import com.example.data.local.CloudAccountEntity
import com.example.data.local.UnifiedFileEntity
import com.example.data.local.UploadRecordEntity
import com.example.data.model.CloudProvider
import com.example.data.model.FileCategory
import com.example.data.model.SmartRoutingDecision
import com.example.data.model.StorageQuotaSummary
import com.example.data.repository.CloudStorageRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
  NEWEST("Newest"),
  OLDEST("Oldest"),
  LARGEST("Largest"),
  SMALLEST("Smallest"),
  NAME_AZ("Name (A-Z)")
}

enum class RoutingMode {
  SMART_AUTO,
  MANUAL
}

data class UploadSimulationState(
  val isUploading: Boolean = false,
  val progress: Float = 0f,
  val currentSpeedMb: Float = 0f,
  val uploadedBytes: Long = 0L,
  val totalBytes: Long = 0L,
  val completedFile: UnifiedFileEntity? = null,
  val error: String? = null
)

data class AuthActionState(
  val isLoading: Boolean = false,
  val error: String? = null,
  val success: String? = null
)

class CloudStorageViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: CloudStorageRepository
  private val authManager = FirebaseAuthManager.getInstance(application)

  val authUserState: StateFlow<AuthUserState> = authManager.userState

  private val _authActionState = MutableStateFlow(AuthActionState())
  val authActionState: StateFlow<AuthActionState> = _authActionState.asStateFlow()

  init {
    val db = AppDatabase.getDatabase(application)
    repository = CloudStorageRepository(db.cloudDao())
    viewModelScope.launch {
      repository.ensureInitialData()
    }
  }

  val accounts: StateFlow<List<CloudAccountEntity>> = repository.allAccounts
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val allFiles: StateFlow<List<UnifiedFileEntity>> = repository.allFiles
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val recentUploads: StateFlow<List<UploadRecordEntity>> = repository.recentUploads
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Filter and Search States
  private val _searchQuery = MutableStateFlow("")
  val searchQuery = _searchQuery.asStateFlow()

  private val _selectedProviderFilter = MutableStateFlow<String?>(null) // null = ALL
  val selectedProviderFilter = _selectedProviderFilter.asStateFlow()

  private val _selectedCategory = MutableStateFlow(FileCategory.ALL)
  val selectedCategory = _selectedCategory.asStateFlow()

  private val _sortOption = MutableStateFlow(SortOption.NEWEST)
  val sortOption = _sortOption.asStateFlow()

  // Storage summary
  val storageSummary: StateFlow<StorageQuotaSummary> = combine(
    accounts,
    allFiles
  ) { accList, fileList ->
    repository.calculateSummary(accList, fileList)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = StorageQuotaSummary(0, 0, 0f, 0, 0, 0, 0, 0, 0, 0)
  )

  // Filtered files Flow
  val filteredFiles: StateFlow<List<UnifiedFileEntity>> = combine(
    allFiles,
    searchQuery,
    selectedProviderFilter,
    selectedCategory,
    sortOption
  ) { files, query, provider, category, sort ->
    files.filter { file ->
      val matchesQuery = query.isBlank() || file.name.contains(query, ignoreCase = true) ||
        file.remotePath.contains(query, ignoreCase = true)
      val matchesProvider = provider == null || file.providerId == provider
      val matchesCategory = category == FileCategory.ALL || file.category == category.name
      matchesQuery && matchesProvider && matchesCategory
    }.let { list ->
      when (sort) {
        SortOption.NEWEST -> list.sortedByDescending { it.uploadDateEpoch }
        SortOption.OLDEST -> list.sortedBy { it.uploadDateEpoch }
        SortOption.LARGEST -> list.sortedByDescending { it.sizeBytes }
        SortOption.SMALLEST -> list.sortedBy { it.sizeBytes }
        SortOption.NAME_AZ -> list.sortedBy { it.name.lowercase() }
      }
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Smart Upload & Staging States
  private val _stagedFileName = MutableStateFlow("Project_Roadmap_Final.pdf")
  val stagedFileName = _stagedFileName.asStateFlow()

  private val _stagedFileSizeBytes = MutableStateFlow(128L * 1024 * 1024) // 128 MB default
  val stagedFileSizeBytes = _stagedFileSizeBytes.asStateFlow()

  private val _stagedCategory = MutableStateFlow(FileCategory.DOCUMENT)
  val stagedCategory = _stagedCategory.asStateFlow()

  private val _routingMode = MutableStateFlow(RoutingMode.SMART_AUTO)
  val routingMode = _routingMode.asStateFlow()

  private val _manualProvider = MutableStateFlow(CloudProvider.GOOGLE_DRIVE)
  val manualProvider = _manualProvider.asStateFlow()

  // Dynamic routing decision
  val smartDecision: StateFlow<SmartRoutingDecision> = combine(
    stagedFileSizeBytes,
    accounts
  ) { size, accList ->
    repository.determineOptimalProvider(size, accList)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = SmartRoutingDecision(
      selectedProvider = CloudProvider.GOOGLE_DRIVE,
      reasonTitle = "Calculating...",
      reasonExplanation = "Analyzing storage metrics across connected cloud services...",
      fitsLimits = true
    )
  )

  // Upload progress simulation state
  private val _uploadState = MutableStateFlow(UploadSimulationState())
  val uploadState = _uploadState.asStateFlow()

  // Status message / toast
  private val _userMessage = MutableStateFlow<String?>(null)
  val userMessage = _userMessage.asStateFlow()

  fun clearUserMessage() {
    _userMessage.value = null
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setProviderFilter(providerId: String?) {
    _selectedProviderFilter.value = providerId
  }

  fun setCategoryFilter(category: FileCategory) {
    _selectedCategory.value = category
  }

  fun setSortOption(sort: SortOption) {
    _sortOption.value = sort
  }

  fun setStagedFileName(name: String) {
    _stagedFileName.value = name
    // Auto guess category
    _stagedCategory.value = FileCategory.fromMime("", name.substringAfterLast('.', ""))
  }

  fun setStagedFileSize(bytes: Long) {
    _stagedFileSizeBytes.value = bytes
  }

  fun setStagedCategory(category: FileCategory) {
    _stagedCategory.value = category
  }

  fun setRoutingMode(mode: RoutingMode) {
    _routingMode.value = mode
  }

  fun setManualProvider(provider: CloudProvider) {
    _manualProvider.value = provider
  }

  fun toggleStar(fileId: String) {
    viewModelScope.launch {
      repository.toggleStar(fileId)
    }
  }

  fun deleteFile(fileId: String) {
    viewModelScope.launch {
      repository.deleteFile(fileId)
      _userMessage.value = "File removed and quota freed."
    }
  }

  fun moveFile(fileId: String, currentProviderId: String, newProviderId: String) {
    viewModelScope.launch {
      repository.moveFile(fileId, currentProviderId, newProviderId)
      val newProv = CloudProvider.fromId(newProviderId).displayName
      _userMessage.value = "File successfully migrated to $newProv!"
    }
  }

  fun toggleAccountConnection(providerId: String, currentConnected: Boolean) {
    viewModelScope.launch {
      repository.toggleAccountConnection(providerId, !currentConnected)
      val prov = CloudProvider.fromId(providerId).displayName
      val status = if (!currentConnected) "connected" else "disconnected"
      _userMessage.value = "$prov $status."
    }
  }

  fun syncAccountQuota(providerId: String) {
    viewModelScope.launch {
      val prov = CloudProvider.fromId(providerId).displayName
      _userMessage.value = "Syncing live quota for $prov..."
      delay(600)
      _userMessage.value = "$prov quotas updated."
    }
  }

  fun startUploadSimulation() {
    val targetProvider = if (_routingMode.value == RoutingMode.SMART_AUTO) {
      smartDecision.value.selectedProvider
    } else {
      _manualProvider.value
    }

    val decision = smartDecision.value
    val reason = if (_routingMode.value == RoutingMode.SMART_AUTO) {
      "${decision.reasonTitle}: ${decision.reasonExplanation}"
    } else {
      "User manually selected ${targetProvider.displayName}"
    }

    val fileName = _stagedFileName.value.ifBlank { "Cloud_Upload_${System.currentTimeMillis()}.dat" }
    val fileSize = _stagedFileSizeBytes.value
    val category = _stagedCategory.value

    viewModelScope.launch {
      _uploadState.value = UploadSimulationState(
        isUploading = true,
        progress = 0.05f,
        currentSpeedMb = 24.5f,
        uploadedBytes = (fileSize * 0.05f).toLong(),
        totalBytes = fileSize
      )

      // Simulate chunked upload progress steps
      for (step in 1..10) {
        delay(180)
        val p = step / 10f
        val speed = 22f + (step % 4) * 3.8f
        _uploadState.value = _uploadState.value.copy(
          progress = p,
          currentSpeedMb = speed,
          uploadedBytes = (fileSize * p).toLong()
        )
      }

      val mime = when (category) {
        FileCategory.DOCUMENT -> "application/pdf"
        FileCategory.IMAGE -> "image/png"
        FileCategory.VIDEO -> "video/mp4"
        FileCategory.AUDIO -> "audio/mp3"
        FileCategory.ARCHIVE -> "application/zip"
        FileCategory.CODE -> "text/plain"
        FileCategory.ALL -> "application/octet-stream"
      }

      val newFile = repository.executeUpload(
        fileName = fileName,
        fileSizeBytes = fileSize,
        mimeType = mime,
        category = category.name,
        provider = targetProvider,
        routingReason = reason
      )

      _uploadState.value = UploadSimulationState(
        isUploading = false,
        progress = 1.0f,
        completedFile = newFile
      )
      _userMessage.value = "Uploaded to ${targetProvider.displayName} successfully!"
    }
  }

  fun resetUploadState() {
    _uploadState.value = UploadSimulationState()
  }

  // Firebase Authentication & User Management
  fun signInWithEmail(email: String, pass: String) {
    if (email.isBlank() || pass.isBlank()) {
      _authActionState.value = AuthActionState(error = "Please enter both email and password")
      return
    }
    _authActionState.value = AuthActionState(isLoading = true)
    authManager.signInWithEmail(email, pass) { result ->
      result.fold(
        onSuccess = { user ->
          _authActionState.value = AuthActionState(success = "Welcome back, ${user.displayName}!")
          _userMessage.value = "Signed in as ${user.email}"
        },
        onFailure = { ex ->
          _authActionState.value = AuthActionState(error = ex.localizedMessage ?: "Authentication failed")
        }
      )
    }
  }

  fun signUpWithEmail(email: String, pass: String, displayName: String) {
    if (email.isBlank() || pass.isBlank()) {
      _authActionState.value = AuthActionState(error = "Please enter an email and password")
      return
    }
    if (pass.length < 6) {
      _authActionState.value = AuthActionState(error = "Password must be at least 6 characters")
      return
    }
    _authActionState.value = AuthActionState(isLoading = true)
    authManager.signUpWithEmail(email, pass, displayName) { result ->
      result.fold(
        onSuccess = { user ->
          _authActionState.value = AuthActionState(success = "Account created for ${user.displayName}!")
          _userMessage.value = "Account created & secure session established"
        },
        onFailure = { ex ->
          _authActionState.value = AuthActionState(error = ex.localizedMessage ?: "Registration failed")
        }
      )
    }
  }

  fun signInAnonymously() {
    _authActionState.value = AuthActionState(isLoading = true)
    authManager.signInAnonymously { result ->
      result.fold(
        onSuccess = { user ->
          _authActionState.value = AuthActionState(success = "Signed in as ${user.displayName}")
          _userMessage.value = "Guest session initialized"
        },
        onFailure = { ex ->
          _authActionState.value = AuthActionState(error = ex.localizedMessage ?: "Guest sign-in failed")
        }
      )
    }
  }

  fun updateDisplayName(newName: String) {
    if (newName.isBlank()) return
    _authActionState.value = AuthActionState(isLoading = true)
    authManager.updateDisplayName(newName) { result ->
      result.fold(
        onSuccess = {
          _authActionState.value = AuthActionState(success = "Profile name updated to $newName")
          _userMessage.value = "Profile updated successfully"
        },
        onFailure = { ex ->
          _authActionState.value = AuthActionState(error = ex.localizedMessage ?: "Update failed")
        }
      )
    }
  }

  fun sendPasswordReset(email: String) {
    if (email.isBlank()) {
      _authActionState.value = AuthActionState(error = "Please provide your email address")
      return
    }
    _authActionState.value = AuthActionState(isLoading = true)
    authManager.sendPasswordReset(email) { result ->
      result.fold(
        onSuccess = {
          _authActionState.value = AuthActionState(success = "Password reset instructions sent to $email")
        },
        onFailure = { ex ->
          _authActionState.value = AuthActionState(error = ex.localizedMessage ?: "Reset request failed")
        }
      )
    }
  }

  fun signOut() {
    authManager.signOut()
    _authActionState.value = AuthActionState(success = "Signed out securely")
    _userMessage.value = "Logged out from Firebase Auth"
  }

  fun clearAuthActionState() {
    _authActionState.value = AuthActionState()
  }
}
