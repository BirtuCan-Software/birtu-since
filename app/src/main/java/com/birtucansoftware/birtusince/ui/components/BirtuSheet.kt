package com.birtucansoftware.birtusince.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun BirtuSheetContainer(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalBirtuColors.current

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.scrim)
                .clickable(onClick = onDismissRequest),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding()
                    .clickable(enabled = false) {} // Prevent dismiss click inside sheet
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(colors.surface)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Grabber 32 x 4
                    Box(
                        modifier = Modifier
                            .size(width = 32.dp, height = 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(colors.outline)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    content()
                }
            }
        }
    }
}

@Composable
fun BirtuConfirmDialog(
    visible: Boolean,
    title: String,
    body: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDanger: Boolean = true
) {
    if (!visible) return
    val colors = LocalBirtuColors.current
    val shape = RoundedCornerShape(28.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(312.dp)
                .clip(shape)
                .background(colors.surfaceRaised)
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = BirtuTypography.titleL.copy(
                        color = colors.text,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = body,
                    style = BirtuTypography.body.copy(
                        color = colors.text2,
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    BirtuTonalButton(
                        text = cancelText,
                        onClick = onDismiss,
                        height = 48.dp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    if (isDanger) {
                        BirtuDangerButton(
                            text = confirmText,
                            onClick = onConfirm,
                            height = 48.dp
                        )
                    } else {
                        BirtuPrimaryButton(
                            text = confirmText,
                            onClick = onConfirm,
                            height = 48.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestoreChoiceDialog(
    visible: Boolean,
    onReplaceAll: () -> Unit,
    onMerge: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return
    val colors = LocalBirtuColors.current
    val shape = RoundedCornerShape(28.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(320.dp)
                .clip(shape)
                .background(colors.surfaceRaised)
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Restore Records",
                    style = BirtuTypography.titleL.copy(
                        color = colors.text,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "How would you like to restore this backup file?",
                    style = BirtuTypography.body.copy(
                        color = colors.text2,
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                BirtuPrimaryButton(
                    text = "Merge with existing data",
                    onClick = onMerge,
                    modifier = Modifier.fillMaxWidth(),
                    height = 44.dp
                )

                Spacer(modifier = Modifier.height(10.dp))

                BirtuTonalButton(
                    text = "Replace all existing data",
                    onClick = onReplaceAll,
                    modifier = Modifier.fillMaxWidth(),
                    height = 44.dp
                )

                Spacer(modifier = Modifier.height(10.dp))

                BirtuTextButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun BirtuSnackbar(
    message: String,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    val shape = RoundedCornerShape(16.dp)

    // Inverted colors: text fill, bg text
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .heightIn(min = 48.dp)
            .clip(shape)
            .background(colors.text)
            .padding(start = 16.dp, end = 4.dp), // Less end padding to account for action button padding
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message,
                style = BirtuTypography.label.copy(
                    color = colors.bg,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            if (actionLabel != null) {
                Box(
                    modifier = Modifier
                        .clickable(
                            onClick = onActionClick,
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null
                        )
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = actionLabel,
                        style = BirtuTypography.label.copy(
                            color = colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
