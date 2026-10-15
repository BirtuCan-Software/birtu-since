package com.birtucansoftware.birtusince.ui.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.birtucansoftware.birtusince.BuildConfig
import com.birtucansoftware.birtusince.R
import com.birtucansoftware.birtusince.ui.components.BirtuIconTile
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.AmberDarkColors
import com.birtucansoftware.birtusince.ui.theme.AmberLightColors
import com.birtucansoftware.birtusince.ui.theme.ArcticDarkColors
import com.birtucansoftware.birtusince.ui.theme.ArcticLightColors
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.BubblegumDarkColors
import com.birtucansoftware.birtusince.ui.theme.BubblegumLightColors
import com.birtucansoftware.birtusince.ui.theme.EmeraldDarkColors
import com.birtucansoftware.birtusince.ui.theme.EmeraldLightColors
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors
import com.birtucansoftware.birtusince.ui.theme.ObsidianDarkColors
import com.birtucansoftware.birtusince.ui.theme.ObsidianLightColors
import com.birtucansoftware.birtusince.ui.theme.TangeloDarkColors
import com.birtucansoftware.birtusince.ui.theme.TangeloLightColors
import com.birtucansoftware.birtusince.ui.theme.ThemePreset

@Composable
fun SettingsScreen(
    currentTheme: ThemePreset,
    onThemeSelect: (ThemePreset) -> Unit,
    isDarkMode: Boolean = false,
    onDarkModeToggled: ((Boolean) -> Unit)? = null,
    appLockEnabled: Boolean,
    onAppLockToggled: (Boolean) -> Unit,
    onSaveCopyClick: () -> Unit,
    onBringBackCopyClick: () -> Unit,
    areNotificationsEnabled: Boolean,
    onSendTestNotificationClick: () -> Unit = {},
    onHelpTourClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = LocalBirtuColors.current

    val notifManager = remember { context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager }
    var liveNotifsEnabled by remember(areNotificationsEnabled) { mutableStateOf(areNotificationsEnabled) }

    fun checkNotifPermission() {
        liveNotifsEnabled = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N && notifManager != null) {
            notifManager.areNotificationsEnabled()
        } else true
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkNotifPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Section 1: Look
            SettingsSectionHeader(title = "Look")
            Spacer(modifier = Modifier.height(10.dp))

            // Dark Mode Toggle Row
            if (onDarkModeToggled != null) {
                SettingsToggleRow(
                    title = "Dark mode",
                    subtitle = if (isDarkMode) "Deep contrast palette enabled" else "Crisp high-legibility light theme",
                    icon = if (isDarkMode) PhosphorIcons.Moon else PhosphorIcons.Sun,
                    checked = isDarkMode,
                    onCheckedChange = onDarkModeToggled
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "Theme Palette",
                style = BirtuTypography.titleM.copy(color = colors.text)
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Six theme tiles: Tangelo, Arctic, Obsidian, Amber, Emerald, Bubblegum
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ThemeMiniTile(
                    name = "Tangelo",
                    preset = ThemePreset.Tangelo,
                    themeColors = if (isDarkMode) TangeloDarkColors else TangeloLightColors,
                    isSelected = currentTheme == ThemePreset.Tangelo,
                    onClick = { onThemeSelect(ThemePreset.Tangelo) }
                )

                ThemeMiniTile(
                    name = "Arctic",
                    preset = ThemePreset.Arctic,
                    themeColors = if (isDarkMode) ArcticDarkColors else ArcticLightColors,
                    isSelected = currentTheme == ThemePreset.Arctic,
                    onClick = { onThemeSelect(ThemePreset.Arctic) }
                )

                ThemeMiniTile(
                    name = "Obsidian",
                    preset = ThemePreset.Obsidian,
                    themeColors = if (isDarkMode) ObsidianDarkColors else ObsidianLightColors,
                    isSelected = currentTheme == ThemePreset.Obsidian,
                    onClick = { onThemeSelect(ThemePreset.Obsidian) }
                )

                ThemeMiniTile(
                    name = "Amber",
                    preset = ThemePreset.Amber,
                    themeColors = if (isDarkMode) AmberDarkColors else AmberLightColors,
                    isSelected = currentTheme == ThemePreset.Amber,
                    onClick = { onThemeSelect(ThemePreset.Amber) }
                )

                ThemeMiniTile(
                    name = "Emerald",
                    preset = ThemePreset.Emerald,
                    themeColors = if (isDarkMode) EmeraldDarkColors else EmeraldLightColors,
                    isSelected = currentTheme == ThemePreset.Emerald,
                    onClick = { onThemeSelect(ThemePreset.Emerald) }
                )

                ThemeMiniTile(
                    name = "Bubblegum",
                    preset = ThemePreset.Bubblegum,
                    themeColors = if (isDarkMode) BubblegumDarkColors else BubblegumLightColors,
                    isSelected = currentTheme == ThemePreset.Bubblegum,
                    onClick = { onThemeSelect(ThemePreset.Bubblegum) }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section 2: Lock
            SettingsSectionHeader(
                title = "Lock",
                infoExplanation = "Enabling App Lock requires biometric authentication (fingerprint or face unlock) or device PIN every time BirtuSince is opened from the background."
            )
            Spacer(modifier = Modifier.height(10.dp))

            SettingsToggleRow(
                title = "App lock",
                subtitle = "Fingerprint or face to open.",
                icon = PhosphorIcons.Lock,
                checked = appLockEnabled,
                onCheckedChange = onAppLockToggled
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Section 3: Backup
            SettingsSectionHeader(
                title = "Backup",
                infoExplanation = "Save a copy exports all your records and history as a JSON file. Bring back a copy allows you to merge or replace your data from a backup file."
            )
            Spacer(modifier = Modifier.height(10.dp))

            SettingsGroupCard {
                // Save a copy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable { onSaveCopyClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BirtuIconTile(
                        icon = PhosphorIcons.DownloadSimple,
                        size = 40.dp,
                        iconSize = 20.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Save a copy", style = BirtuTypography.body.copy(color = colors.text))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PhosphorIcon(
                                icon = PhosphorIcons.ShieldCheck,
                                contentDescription = null,
                                tint = colors.text2,
                                size = 14.dp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Stays on your phone.",
                                style = BirtuTypography.caption.copy(color = colors.text2)
                            )
                        }
                    }
                    PhosphorIcon(
                        icon = PhosphorIcons.CaretRight,
                        contentDescription = null,
                        tint = colors.text2,
                        size = 18.dp
                    )
                }

                SettingsDivider()

                // Bring back a copy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable { onBringBackCopyClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BirtuIconTile(
                        icon = PhosphorIcons.UploadSimple,
                        size = 40.dp,
                        iconSize = 20.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Bring back a copy", style = BirtuTypography.body.copy(color = colors.text))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PhosphorIcon(
                                icon = PhosphorIcons.ShieldCheck,
                                contentDescription = null,
                                tint = colors.text2,
                                size = 14.dp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Stays on your phone.",
                                style = BirtuTypography.caption.copy(color = colors.text2)
                            )
                        }
                    }
                    PhosphorIcon(
                        icon = PhosphorIcons.CaretRight,
                        contentDescription = null,
                        tint = colors.text2,
                        size = 18.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section 4: Alerts & Reminders
            SettingsSectionHeader(
                title = "Alerts & Reminders",
                infoExplanation = "System Notifications control whether device popups are permitted. You can also send a test notification to verify alert appearance."
            )
            Spacer(modifier = Modifier.height(10.dp))

            SettingsGroupCard {
                // System Notifications row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable {
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                            context.startActivity(intent)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        BirtuIconTile(
                            icon = PhosphorIcons.Bell,
                            size = 40.dp,
                            iconSize = 20.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "System Notifications", style = BirtuTypography.body.copy(color = colors.text, fontWeight = FontWeight.Medium))
                            Text(
                                text = "Manage app alert permissions",
                                style = BirtuTypography.caption.copy(color = colors.text2)
                            )
                        }
                    }

                    Text(
                        text = if (liveNotifsEnabled) "On" else "Off",
                        style = BirtuTypography.body.copy(color = if (liveNotifsEnabled) colors.primaryStrong else colors.text2)
                    )
                }

                SettingsDivider()

                // Send Test Notification row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable { onSendTestNotificationClick() }
                        .testTag("settings_test_notification_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BirtuIconTile(
                        icon = PhosphorIcons.Bell,
                        size = 40.dp,
                        iconSize = 20.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Send test notification", style = BirtuTypography.body.copy(color = colors.text, fontWeight = FontWeight.Medium))
                        Text(
                            text = "Preview what target reminders look like",
                            style = BirtuTypography.caption.copy(color = colors.text2)
                        )
                    }
                    PhosphorIcon(
                        icon = PhosphorIcons.CaretRight,
                        contentDescription = null,
                        tint = colors.text2,
                        size = 18.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section 5: About
            SettingsSectionHeader(title = "About")
            Spacer(modifier = Modifier.height(10.dp))

            SettingsGroupCard {
                // About BirtuSince navigation row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable { onAboutClick() }
                        .testTag("settings_about_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BirtuIconTile(
                        icon = PhosphorIcons.Info,
                        size = 40.dp,
                        iconSize = 20.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "About BirtuSince", style = BirtuTypography.body.copy(color = colors.text, fontWeight = FontWeight.Medium))
                        Text(
                            text = "Privacy guarantee & publisher details",
                            style = BirtuTypography.caption.copy(color = colors.text2)
                        )
                    }
                    PhosphorIcon(
                        icon = PhosphorIcons.CaretRight,
                        contentDescription = null,
                        tint = colors.text2,
                        size = 18.dp
                    )
                }

                SettingsDivider()

                // Version row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Version", style = BirtuTypography.body.copy(color = colors.text))
                    Text(text = "v${BuildConfig.VERSION_NAME}", style = BirtuTypography.body.copy(color = colors.text2))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Centered App Logo and App Name
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.primarySoft)
                        .border(1.5.dp, colors.primary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "BirtuSince Logo",
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "BirtuSince",
                    style = BirtuTypography.titleM.copy(
                        color = colors.text,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Powered by BirtuCan Technologies PLC footer link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Powered by ",
                    style = BirtuTypography.caption.copy(color = colors.text2)
                )
                Text(
                    text = "BirtuCan Technologies PLC",
                    style = BirtuTypography.caption.copy(
                        color = colors.primary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable {
                        com.birtucansoftware.birtusince.ui.about.openUrlInBrowser(context, "https://birtucansoftware.com")
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
