package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.auth.FirebaseAuthManager
import com.example.ui.screens.AccountsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SmartUploadScreen
import com.example.ui.screens.UniversalFileManagerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CloudStorageViewModel

enum class AppTab(
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String
) {
  DASHBOARD("Dashboard", Icons.Filled.PieChart, Icons.Outlined.PieChart, "tab_dashboard"),
  FILES("Files", Icons.Filled.Folder, Icons.Outlined.Folder, "tab_files"),
  SMART_UPLOAD("Upload", Icons.Filled.CloudUpload, Icons.Outlined.CloudUpload, "tab_upload"),
  ACCOUNTS("Accounts", Icons.Filled.ManageAccounts, Icons.Outlined.ManageAccounts, "tab_accounts")
}

class MainActivity : ComponentActivity() {

  private val viewModel: CloudStorageViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    FirebaseAuthManager.ensureFirebaseInitialized(applicationContext)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }

        // State collection from ViewModel
        val accounts by viewModel.accounts.collectAsStateWithLifecycle()
        val allFiles by viewModel.allFiles.collectAsStateWithLifecycle()
        val filteredFiles by viewModel.filteredFiles.collectAsStateWithLifecycle()
        val storageSummary by viewModel.storageSummary.collectAsStateWithLifecycle()
        val recentUploads by viewModel.recentUploads.collectAsStateWithLifecycle()

        // Firebase Auth states
        val authUserState by viewModel.authUserState.collectAsStateWithLifecycle()
        val authActionState by viewModel.authActionState.collectAsStateWithLifecycle()

        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
        val selectedProviderFilter by viewModel.selectedProviderFilter.collectAsStateWithLifecycle()
        val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
        val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()

        val stagedFileName by viewModel.stagedFileName.collectAsStateWithLifecycle()
        val stagedFileSizeBytes by viewModel.stagedFileSizeBytes.collectAsStateWithLifecycle()
        val stagedCategory by viewModel.stagedCategory.collectAsStateWithLifecycle()
        val routingMode by viewModel.routingMode.collectAsStateWithLifecycle()
        val manualProvider by viewModel.manualProvider.collectAsStateWithLifecycle()
        val smartDecision by viewModel.smartDecision.collectAsStateWithLifecycle()
        val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()

        val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

        LaunchedEffect(userMessage) {
          userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
          }
        }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          contentWindowInsets = WindowInsets.safeDrawing,
          snackbarHost = { SnackbarHost(snackbarHostState) },
          bottomBar = {
            NavigationBar(
              modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .testTag("bottom_nav_bar"),
              containerColor = androidx.compose.ui.graphics.Color.White,
              tonalElevation = 2.dp
            ) {
              AppTab.values().forEach { tab ->
                val selected = currentTab == tab
                NavigationBarItem(
                  selected = selected,
                  onClick = { currentTab = tab },
                  icon = {
                    Icon(
                      imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                      contentDescription = tab.label
                    )
                  },
                  label = {
                    Text(
                      text = tab.label,
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                      )
                    )
                  },
                  modifier = Modifier.testTag(tab.testTag),
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = androidx.compose.ui.graphics.Color(0xFF0061A4),
                    selectedTextColor = androidx.compose.ui.graphics.Color(0xFF0061A4),
                    indicatorColor = androidx.compose.ui.graphics.Color(0xFFD1E4FF),
                    unselectedIconColor = androidx.compose.ui.graphics.Color(0xFF44474E),
                    unselectedTextColor = androidx.compose.ui.graphics.Color(0xFF44474E)
                  )
                )
              }
            }
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            when (currentTab) {
              AppTab.DASHBOARD -> DashboardScreen(
                summary = storageSummary,
                accounts = accounts,
                recentUploads = recentUploads,
                authUserState = authUserState,
                onNavigateToUpload = { currentTab = AppTab.SMART_UPLOAD },
                onNavigateToFiles = { currentTab = AppTab.FILES },
                onNavigateToAccounts = { currentTab = AppTab.ACCOUNTS },
                onSyncAccount = { viewModel.syncAccountQuota(it) }
              )

              AppTab.FILES -> UniversalFileManagerScreen(
                files = filteredFiles,
                searchQuery = searchQuery,
                selectedProvider = selectedProviderFilter,
                selectedCategory = selectedCategory,
                sortOption = sortOption,
                onSearchChange = { viewModel.setSearchQuery(it) },
                onProviderSelect = { viewModel.setProviderFilter(it) },
                onCategorySelect = { viewModel.setCategoryFilter(it) },
                onSortSelect = { viewModel.setSortOption(it) },
                onToggleStar = { viewModel.toggleStar(it) },
                onDeleteFile = { viewModel.deleteFile(it) },
                onMoveFile = { fileId, curProv, newProv -> viewModel.moveFile(fileId, curProv, newProv) }
              )

              AppTab.SMART_UPLOAD -> SmartUploadScreen(
                fileName = stagedFileName,
                fileSizeBytes = stagedFileSizeBytes,
                category = stagedCategory,
                routingMode = routingMode,
                manualProvider = manualProvider,
                smartDecision = smartDecision,
                uploadState = uploadState,
                accounts = accounts,
                onFileNameChange = { viewModel.setStagedFileName(it) },
                onFileSizeChange = { viewModel.setStagedFileSize(it) },
                onCategoryChange = { viewModel.setStagedCategory(it) },
                onRoutingModeChange = { viewModel.setRoutingMode(it) },
                onManualProviderChange = { viewModel.setManualProvider(it) },
                onStartUpload = { viewModel.startUploadSimulation() },
                onResetUpload = { viewModel.resetUploadState() },
                onNavigateToFiles = { currentTab = AppTab.FILES }
              )

              AppTab.ACCOUNTS -> AccountsScreen(
                accounts = accounts,
                authUserState = authUserState,
                authActionState = authActionState,
                onToggleAccount = { id, cur -> viewModel.toggleAccountConnection(id, cur) },
                onSyncAccount = { viewModel.syncAccountQuota(it) },
                onSignInWithEmail = { email, pass -> viewModel.signInWithEmail(email, pass) },
                onSignUpWithEmail = { email, pass, name -> viewModel.signUpWithEmail(email, pass, name) },
                onSignInAnonymously = { viewModel.signInAnonymously() },
                onUpdateDisplayName = { viewModel.updateDisplayName(it) },
                onSendPasswordReset = { viewModel.sendPasswordReset(it) },
                onSignOut = { viewModel.signOut() },
                onClearAuthActionState = { viewModel.clearAuthActionState() }
              )
            }
          }
        }
      }
    }
  }
}
