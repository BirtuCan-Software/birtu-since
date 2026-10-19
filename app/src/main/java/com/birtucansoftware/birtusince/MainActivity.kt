package com.birtucansoftware.birtusince

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.birtucansoftware.birtusince.notifications.ReminderReceiver
import com.birtucansoftware.birtusince.ui.MainViewModel
import com.birtucansoftware.birtusince.ui.about.AboutScreen
import com.birtucansoftware.birtusince.ui.components.BirtuBottomBar
import com.birtucansoftware.birtusince.ui.components.BirtuConfirmDialog
import com.birtucansoftware.birtusince.ui.components.BirtuSnackbar
import com.birtucansoftware.birtusince.ui.components.BirtuTopAppBar
import com.birtucansoftware.birtusince.ui.components.RestoreChoiceDialog
import com.birtucansoftware.birtusince.ui.components.rememberCurrentTick
import com.birtucansoftware.birtusince.ui.detail.ItemDetailScreen
import com.birtucansoftware.birtusince.ui.help.HelpTourViewModel
import com.birtucansoftware.birtusince.ui.help.InteractiveSpotlightTourOverlay
import com.birtucansoftware.birtusince.ui.help.TourHighlightTarget
import com.birtucansoftware.birtusince.ui.home.HomeScreen
import com.birtucansoftware.birtusince.ui.home.QuickAddSheet
import com.birtucansoftware.birtusince.ui.lock.AppLockScreen
import com.birtucansoftware.birtusince.ui.lock.BiometricAuthManager
import com.birtucansoftware.birtusince.ui.putaway.PutAwayScreen
import com.birtucansoftware.birtusince.ui.settings.SettingsScreen
import com.birtucansoftware.birtusince.ui.stats.StatsScreen
import com.birtucansoftware.birtusince.ui.theme.BirtuTheme
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainActivity : FragmentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private var pendingRestoreJson: String? = null
    var showRestoreChoiceDialog by mutableStateOf(false)

    private val safeRcMap = java.util.concurrent.ConcurrentHashMap<Int, Int>()

    val customActivityResultRegistry = object : androidx.activity.result.ActivityResultRegistry() {
        override fun <I, O> onLaunch(
            requestCode: Int,
            contract: androidx.activity.result.contract.ActivityResultContract<I, O>,
            input: I,
            options: androidx.core.app.ActivityOptionsCompat?
        ) {
            val safeCode = (requestCode and 0x7FFF) + 1000
            safeRcMap[safeCode] = requestCode
            val intent = contract.createIntent(this@MainActivity, input)
            try {
                androidx.core.app.ActivityCompat.startActivityForResult(
                    this@MainActivity,
                    intent,
                    safeCode,
                    options?.toBundle()
                )
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to start activity for result", e)
                Toast.makeText(this@MainActivity, "Could not launch file request: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val originalCode = safeRcMap.remove(requestCode) ?: requestCode
        if (!customActivityResultRegistry.dispatchResult(originalCode, resultCode, data)) {
            @Suppress("DEPRECATION")
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            val themePreset by viewModel.themePreset.collectAsState()
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            BirtuTheme(preset = themePreset, isDarkMode = isDarkMode) {
                MainAppContent()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onStop() {
        super.onStop()
        viewModel.lockApp()
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        val openItemId = intent.getLongExtra("open_item_id", -1L)
            .takeIf { it != -1L } ?: intent.getLongExtra("shortcut_item_id", -1L)

        if (openItemId != -1L) {
            if (intent.getBooleanExtra("shortcut_start_over", false)) {
                viewModel.startOver(openItemId)
            }
        }
    }

    @Composable
    private fun MainAppContent() {
        val customRegistryOwner = remember {
            object : androidx.activity.result.ActivityResultRegistryOwner {
                override val activityResultRegistry: androidx.activity.result.ActivityResultRegistry
                    get() = customActivityResultRegistry
            }
        }

        androidx.compose.runtime.CompositionLocalProvider(
            androidx.activity.compose.LocalActivityResultRegistryOwner provides customRegistryOwner
        ) {
            val colors = LocalBirtuColors.current
        val activeItems by this@MainActivity.viewModel.activeItems.collectAsState()
        val isLoadingActiveItems by this@MainActivity.viewModel.isLoadingActiveItems.collectAsState()
        val archivedItems by this@MainActivity.viewModel.archivedItems.collectAsState()
        val appLockEnabled by this@MainActivity.viewModel.appLockEnabled.collectAsState()
        val isAppUnlocked by this@MainActivity.viewModel.isAppUnlocked.collectAsState()
        val unlockedItemIds by this@MainActivity.viewModel.unlockedItemIds.collectAsState()
        val currentTick by rememberCurrentTick()
        val snackbarEvent by this@MainActivity.viewModel.snackbarEvent.collectAsState()
        val themePreset by this@MainActivity.viewModel.themePreset.collectAsState()
        val isDarkMode by this@MainActivity.viewModel.isDarkMode.collectAsState()
        val filterSelection by this@MainActivity.viewModel.filterSelection.collectAsState()
        val selectedFilter by this@MainActivity.viewModel.selectedFilter.collectAsState()
        val sortOption by this@MainActivity.viewModel.sortOption.collectAsState()
        val isSortAscending by this@MainActivity.viewModel.isSortAscending.collectAsState()
        val draftTitle by this@MainActivity.viewModel.draftTitle.collectAsState()

        val notifManager = remember { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
        var areNotifsEnabled by remember {
            mutableStateOf(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    notifManager.areNotificationsEnabled()
                } else true
            )
        }

        fun refreshNotificationStatus() {
            areNotifsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                notifManager.areNotificationsEnabled()
            } else true
        }

        val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
        androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
            val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                    refreshNotificationStatus()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        val saveBackupLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/json")
        ) { uri: Uri? ->
            if (uri != null) {
                lifecycleScope.launch {
                    try {
                        val json = withContext(Dispatchers.IO) {
                            this@MainActivity.viewModel.exportBackupJson()
                        }
                        val writeSuccess = com.birtucansoftware.birtusince.data.BackupFileStorageHelper.writeBackupToUri(this@MainActivity, uri, json)
                        if (writeSuccess) {
                            this@MainActivity.viewModel.showSnackbar(getString(R.string.backup_saved))
                        } else {
                            Toast.makeText(this@MainActivity, "Could not write to file", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Throwable) {
                        Log.e("MainActivity", "Error exporting backup", e)
                        Toast.makeText(this@MainActivity, "Failed to save backup: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val restoreBackupLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            if (uri != null) {
                lifecycleScope.launch {
                    try {
                        val content = com.birtucansoftware.birtusince.data.BackupFileStorageHelper.readBackupFromUri(this@MainActivity, uri)
                        if (content.isNullOrBlank()) {
                            Toast.makeText(this@MainActivity, "Selected backup file is empty", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        pendingRestoreJson = content
                        showRestoreChoiceDialog = true
                    } catch (e: Throwable) {
                        Log.e("MainActivity", "Error reading backup file", e)
                        Toast.makeText(this@MainActivity, "Failed to read backup: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val requestNotificationPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            refreshNotificationStatus()
            if (isGranted) {
                ReminderReceiver.sendTestNotification(this@MainActivity)
                this@MainActivity.viewModel.showSnackbar("Test notification sent!")
            } else {
                this@MainActivity.viewModel.showSnackbar("Notification permission denied")
            }
        }

        var currentTab by remember { mutableStateOf("home") }
        var selectedDetailItemId by remember { mutableStateOf<Long?>(null) }
        var showAboutScreen by rememberSaveable { mutableStateOf(false) }
        var showQuickAddSheet by remember { mutableStateOf(false) }
        var hasLockFailedAttempt by remember { mutableStateOf(false) }

        val helpTourViewModel: HelpTourViewModel = viewModel()
        var showHelpTour by rememberSaveable { mutableStateOf(false) }

        // Map storing bounds of highlighted UI elements in real coordinates
        val targetBoundsMap = remember { mutableStateMapOf<TourHighlightTarget, Rect>() }

        LaunchedEffect(Unit) {
            helpTourViewModel.closeTourEvent.collect {
                showHelpTour = false
            }
        }

        // BackHandler: if tour is active, back moves 1 step back or finishes
        BackHandler(enabled = showHelpTour) {
            helpTourViewModel.previousStep()
        }

        // BackHandler for sub-screens
        BackHandler(enabled = !showHelpTour && (showAboutScreen || selectedDetailItemId != null || currentTab != "home")) {
            if (showAboutScreen) {
                showAboutScreen = false
            } else if (selectedDetailItemId != null) {
                selectedDetailItemId = null
            } else {
                currentTab = "home"
            }
        }

        if (appLockEnabled && !isAppUnlocked) {
            AppLockScreen(
                onUnlockClick = {
                    BiometricAuthManager.authenticate(
                        activity = this@MainActivity,
                        title = "Unlock BirtuSince",
                        subtitle = "Authenticate to open your records",
                        onSuccess = {
                            viewModel.unlockApp()
                            hasLockFailedAttempt = false
                        },
                        onError = {
                            hasLockFailedAttempt = true
                        }
                    )
                },
                hasFailedAttempt = hasLockFailedAttempt
            )

            LaunchedEffect(Unit) {
                BiometricAuthManager.authenticate(
                    activity = this@MainActivity,
                    title = "Unlock BirtuSince",
                    subtitle = "Authenticate to open your records",
                    onSuccess = {
                        viewModel.unlockApp()
                        hasLockFailedAttempt = false
                    },
                    onError = {
                        hasLockFailedAttempt = true
                    }
                )
            }
        } else {

        Scaffold(
            topBar = {
                // Persistent App Bar with Logo + App Name on left, Theme Toggle & Help icon on right
                if (selectedDetailItemId == null && !showAboutScreen) {
                    BirtuTopAppBar(
                        onHelpClick = {
                            helpTourViewModel.resetToStart()
                            showHelpTour = true
                        },
                        logoModifier = Modifier.onGloballyPositioned { coords ->
                            targetBoundsMap[TourHighlightTarget.LOGO_APP_NAME] = coords.boundsInRoot()
                        },
                        helpButtonModifier = Modifier.onGloballyPositioned { coords ->
                            targetBoundsMap[TourHighlightTarget.HELP_BUTTON] = coords.boundsInRoot()
                        },
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        modifier = Modifier.statusBarsPadding()
                    )
                }
            },
            bottomBar = {
                if (selectedDetailItemId == null && !showAboutScreen) {
                    BirtuBottomBar(
                        currentRoute = currentTab,
                        onNavigate = { currentTab = it },
                        onAddClick = { showQuickAddSheet = true },
                        shouldNudge = activeItems.isEmpty() || currentTab == "home",
                        addButtonModifier = Modifier.onGloballyPositioned { coords ->
                            targetBoundsMap[TourHighlightTarget.ADD_BUTTON] = coords.boundsInRoot()
                        },
                        statsTabModifier = Modifier.onGloballyPositioned { coords ->
                            targetBoundsMap[TourHighlightTarget.STATS_TAB] = coords.boundsInRoot()
                        }
                    )
                }
            },
            containerColor = colors.bg
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (showAboutScreen) {
                    AboutScreen(
                        onBackClick = { showAboutScreen = false }
                    )
                } else if (selectedDetailItemId != null) {
                    val itemId = selectedDetailItemId!!
                    val item = (activeItems + archivedItems).find { it.id == itemId }

                    if (item != null) {
                        val laps by viewModel.observeLaps(itemId).collectAsState(initial = emptyList())
                        val decryptedTitle = viewModel.decryptTitle(item)
                        val decryptedNotes = viewModel.decryptNotes(item)

                        ItemDetailScreen(
                            item = item,
                            laps = laps,
                            currentTick = currentTick,
                            decryptedTitle = decryptedTitle,
                            decryptedNotes = decryptedNotes,
                            onBackClick = { selectedDetailItemId = null },
                            onStartOverNow = { viewModel.startOver(itemId) },
                            onStartOverPast = { pastEpoch -> viewModel.startOver(itemId, pastEpoch) },
                            onPauseResumeClick = {
                                if (item.isPaused) viewModel.resumeItem(itemId) else viewModel.pauseItem(itemId)
                            },
                            onPutAwayClick = {
                                viewModel.putAwayItem(itemId)
                                selectedDetailItemId = null
                            },
                            onBringBackClick = { viewModel.bringBackItem(itemId) },
                            onDeleteItemClick = {
                                viewModel.deleteItem(itemId)
                                selectedDetailItemId = null
                            },
                            onUnitChanged = { newUnit ->
                                viewModel.updateItem(item.copy(preferredUnit = newUnit))
                            },
                            onUpdateItem = { updated -> viewModel.updateItem(updated) },
                            onSaveLap = { updatedLap -> viewModel.updateLap(updatedLap) },
                            onDeleteLap = { lapId -> viewModel.deleteLap(lapId) }
                        )
                    } else {
                        selectedDetailItemId = null
                    }
                } else {
                    when (currentTab) {
                        "home" -> HomeScreen(
                            items = activeItems,
                            currentTick = currentTick,
                            unlockedItemIds = unlockedItemIds,
                            filterSelection = filterSelection,
                            onFilterSelectionChange = { selection -> viewModel.setFilterSelection(selection) },
                            selectedFilter = selectedFilter,
                            onFilterSelect = { filter -> viewModel.setSelectedFilter(filter) },
                            sortOption = sortOption,
                            isSortAscending = isSortAscending,
                            onSortOptionSelect = { option -> viewModel.setSortOption(option) },
                            onSortAscendingToggle = { ascending -> viewModel.setSortAscending(ascending) },
                            onTogglePinClick = { itemId -> viewModel.togglePinItem(itemId) },
                            onItemClick = { itemId -> selectedDetailItemId = itemId },
                            onStartOverClick = { itemId -> viewModel.startOver(itemId) },
                            onPauseResumeClick = { itemId ->
                                val item = activeItems.find { it.id == itemId }
                                if (item != null) {
                                    if (item.isPaused) viewModel.resumeItem(itemId) else viewModel.pauseItem(itemId)
                                }
                            },
                            onPutAwayClick = { itemId -> viewModel.putAwayItem(itemId) },
                            onDeleteClick = { itemId -> viewModel.deleteItem(itemId) },
                            onUnlockClick = { itemId ->
                                BiometricAuthManager.authenticate(
                                    activity = this@MainActivity,
                                    title = "Unlock Item",
                                    subtitle = "Authenticate to view this record",
                                    onSuccess = { viewModel.unlockItem(itemId) },
                                    onError = {}
                                )
                            },
                            onHelpClick = {
                                helpTourViewModel.resetToStart()
                                showHelpTour = true
                            },
                            searchBarModifier = Modifier.onGloballyPositioned { coords ->
                                targetBoundsMap[TourHighlightTarget.SEARCH_BAR] = coords.boundsInRoot()
                            },
                            isLoading = isLoadingActiveItems
                        )

                        "stats" -> StatsScreen(
                            items = (activeItems + archivedItems).filter { !it.isOneTime },
                            currentTick = currentTick
                        )

                        "put_away" -> PutAwayScreen(
                            items = archivedItems,
                            onItemClick = { itemId -> selectedDetailItemId = itemId },
                            onBringBackClick = { itemId -> viewModel.bringBackItem(itemId) },
                            onDeleteItemClick = { itemId -> viewModel.deleteItem(itemId) }
                        )

                        "settings" -> {
                            val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            val areNotifsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                notifManager.areNotificationsEnabled()
                            } else true

                            SettingsScreen(
                                currentTheme = themePreset,
                                onThemeSelect = { viewModel.setThemePreset(it) },
                                isDarkMode = isDarkMode,
                                onDarkModeToggled = { viewModel.setDarkMode(it) },
                                appLockEnabled = appLockEnabled,
                                onAppLockToggled = { enable ->
                                    if (enable) {
                                        BiometricAuthManager.authenticate(
                                            activity = this@MainActivity,
                                            title = "Enable App Lock",
                                            subtitle = "Confirm fingerprint or face to protect app",
                                            onSuccess = { viewModel.setAppLockEnabled(true) },
                                            onError = {}
                                        )
                                    } else {
                                        viewModel.setAppLockEnabled(false)
                                    }
                                },
                                onSaveCopyClick = {
                                    try {
                                        saveBackupLauncher.launch("birtusince_backup_${System.currentTimeMillis()}.json")
                                    } catch (e: Exception) {
                                        Log.e("MainActivity", "Failed to launch save backup", e)
                                        Toast.makeText(this@MainActivity, "Cannot open file saver: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                },
                                onBringBackCopyClick = {
                                    try {
                                        restoreBackupLauncher.launch(arrayOf("*/*"))
                                    } catch (e: Exception) {
                                        Log.e("MainActivity", "Failed to launch restore backup", e)
                                        Toast.makeText(this@MainActivity, "Cannot open file picker: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                },
                                 areNotificationsEnabled = areNotifsEnabled,
                                onSendTestNotificationClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                            ReminderReceiver.sendTestNotification(this@MainActivity)
                                            viewModel.showSnackbar("Test notification sent!")
                                        } else {
                                            requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    } else {
                                        ReminderReceiver.sendTestNotification(this@MainActivity)
                                        viewModel.showSnackbar("Test notification sent!")
                                    }
                                },
                                onAboutClick = { showAboutScreen = true }
                            )
                        }
                    }
                }

                // Inverted floating Snackbar
                AnimatedVisibility(
                    visible = snackbarEvent != null,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                ) {
                    snackbarEvent?.let { event ->
                        BirtuSnackbar(
                            message = event.message,
                            actionLabel = event.actionLabel,
                            onActionClick = {
                                event.onAction?.invoke()
                                viewModel.clearSnackbar()
                            }
                        )
                    }
                }
            }
        }

        // Auto-dismiss snackbar after 5 seconds
        LaunchedEffect(snackbarEvent) {
            if (snackbarEvent != null) {
                delay(5000)
                viewModel.clearSnackbar()
            }
        }

        // Quick Add Bottom Sheet
        QuickAddSheet(
            visible = showQuickAddSheet,
            initialDraft = draftTitle,
            onDismissRequest = { draft ->
                showQuickAddSheet = false
                if (draft.isNotBlank()) {
                    viewModel.setDraftTitle(draft)
                    viewModel.showSnackbar(getString(R.string.draft_kept))
                }
            },
            onStartItem = { title, startEpoch, category, icon, notes, targetInterval, isPrivate, isOneTime ->
                showQuickAddSheet = false
                viewModel.createItem(
                    title = title,
                    startEpochMillis = startEpoch,
                    category = category,
                    categoryIcon = icon,
                    notes = notes,
                    targetIntervalMillis = targetInterval,
                    isPrivate = isPrivate,
                    isOneTime = isOneTime
                )
            },
            existingGroups = activeItems.map { it.category }.filter { it.isNotBlank() }.distinct()
        )

        // Restore Backup Choice Dialog
        if (showRestoreChoiceDialog && pendingRestoreJson != null) {
            RestoreChoiceDialog(
                visible = true,
                onMerge = {
                    showRestoreChoiceDialog = false
                    val json = pendingRestoreJson ?: return@RestoreChoiceDialog
                    pendingRestoreJson = null
                    lifecycleScope.launch {
                        val ok = viewModel.restoreBackupJson(json, replaceAll = false)
                        if (ok) {
                            viewModel.showSnackbar(getString(R.string.backup_restored))
                        } else {
                            Toast.makeText(this@MainActivity, "Invalid backup file", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onReplaceAll = {
                    showRestoreChoiceDialog = false
                    val json = pendingRestoreJson ?: return@RestoreChoiceDialog
                    pendingRestoreJson = null
                    lifecycleScope.launch {
                        val ok = viewModel.restoreBackupJson(json, replaceAll = true)
                        if (ok) {
                            viewModel.showSnackbar(getString(R.string.backup_restored))
                        } else {
                            Toast.makeText(this@MainActivity, "Invalid backup file", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onDismiss = {
                    showRestoreChoiceDialog = false
                    pendingRestoreJson = null
                }
            )
        }

        // Interactive Spotlight Guided Tour Overlay
        AnimatedVisibility(
            visible = showHelpTour,
            enter = fadeIn(animationSpec = tween(220)),
            exit = fadeOut(animationSpec = tween(220))
        ) {
            val stepIndex by helpTourViewModel.currentStepIndex.collectAsState()
            val currentStep = helpTourViewModel.tourSteps[stepIndex]
            val bounds = targetBoundsMap[currentStep.target]

            InteractiveSpotlightTourOverlay(
                currentStep = currentStep,
                stepIndex = stepIndex,
                totalSteps = helpTourViewModel.tourSteps.size,
                targetBounds = bounds,
                onNextStep = { helpTourViewModel.nextStep() },
                onPreviousStep = { helpTourViewModel.previousStep() },
                onCancelTour = { helpTourViewModel.cancelTour() }
            )
        }
        }
    }
}
}
