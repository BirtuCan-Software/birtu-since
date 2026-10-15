package com.birtucansoftware.birtusince.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.birtucansoftware.birtusince.data.ItemEntity
import com.birtucansoftware.birtusince.domain.TimeEngine
import com.birtucansoftware.birtusince.ui.components.BirtuInfoIconButton
import com.birtucansoftware.birtusince.ui.components.BirtuSwitch
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.lock.BiometricAuthManager
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun ItemDetailInfoSection(
    item: ItemEntity,
    decryptedTitle: String,
    decryptedNotes: String,
    onEditTargetClick: () -> Unit,
    onUpdateItem: (ItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = LocalBirtuColors.current

    val hasCategory = item.category.isNotBlank()
    val hasIcon = item.categoryIcon.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp)
    ) {
        // Row 1: Target Notification
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clickable { onEditTargetClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Target notification", style = BirtuTypography.body.copy(color = colors.text))
                    Spacer(modifier = Modifier.width(4.dp))
                    BirtuInfoIconButton(
                        title = "Target Notification",
                        explanation = "Receive local device alerts when your elapsed time reaches or passes your specified target duration."
                    )
                }
                val remindText = if (item.targetIntervalMillis != null) {
                    "Remind after ${TimeEngine.formatCompact(item.targetIntervalMillis)}"
                } else "Off"
                Text(text = remindText, style = BirtuTypography.caption.copy(color = colors.text2))
            }

            BirtuSwitch(
                checked = item.targetIntervalMillis != null,
                onCheckedChange = { on ->
                    if (on) {
                        val defaultTarget = 14 * 86_400_000L
                        onUpdateItem(item.copy(targetIntervalMillis = defaultTarget))
                    } else {
                        onUpdateItem(item.copy(targetIntervalMillis = null))
                    }
                }
            )
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.outline))

        // Row 2: Group (if only icon is picked, show it; if name too, show them together; if neither, show None)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Group", style = BirtuTypography.body.copy(color = colors.text))

            if (hasCategory || hasIcon) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (hasIcon) {
                        PhosphorIcon(
                            icon = PhosphorIcons.fromName(item.categoryIcon),
                            contentDescription = item.category.ifBlank { item.categoryIcon },
                            tint = colors.primaryStrong,
                            size = 16.dp
                        )
                    }
                    if (hasCategory && hasIcon) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    if (hasCategory) {
                        Text(
                            text = item.category,
                            style = BirtuTypography.body.copy(color = colors.text2)
                        )
                    }
                }
            } else {
                Text(
                    text = "None",
                    style = BirtuTypography.body.copy(color = colors.text2)
                )
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.outline))

        // Row 3: Note
        if (decryptedNotes.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp)
            ) {
                Text(text = "Note", style = BirtuTypography.body.copy(color = colors.text))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = decryptedNotes,
                    style = BirtuTypography.body.copy(color = colors.text2, fontSize = 14.sp)
                )
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.outline))
        }

        // Row 4: Lock it switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Lock it", style = BirtuTypography.body.copy(color = colors.text))
                Spacer(modifier = Modifier.width(4.dp))
                BirtuInfoIconButton(
                    title = "Lock it",
                    explanation = "Locking a record encrypts its title and notes on device using hardware Keystore encryption. Sensitive details are hidden on screen until unlocked with fingerprint, face, or PIN."
                )
            }
            BirtuSwitch(
                checked = item.isPrivate,
                onCheckedChange = { checked ->
                    val activity = context as? FragmentActivity
                    if (activity != null) {
                        BiometricAuthManager.authenticate(
                            activity = activity,
                            title = if (checked) "Lock Item" else "Unlock Item",
                            subtitle = if (checked) "Authenticate to lock this record" else "Authenticate to unlock this record",
                            onSuccess = {
                                val targetTitle = if (item.title == "Locked Item") decryptedTitle else item.title
                                val targetNotes = if (item.notes.isBlank()) decryptedNotes else item.notes
                                onUpdateItem(item.copy(isPrivate = checked, title = targetTitle, notes = targetNotes))
                            },
                            onError = {}
                        )
                    } else {
                        val targetTitle = if (item.title == "Locked Item") decryptedTitle else item.title
                        val targetNotes = if (item.notes.isBlank()) decryptedNotes else item.notes
                        onUpdateItem(item.copy(isPrivate = checked, title = targetTitle, notes = targetNotes))
                    }
                }
            )
        }
    }
}
