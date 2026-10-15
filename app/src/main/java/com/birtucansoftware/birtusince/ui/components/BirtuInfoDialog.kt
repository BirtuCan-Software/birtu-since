package com.birtucansoftware.birtusince.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

/**
 * Tiny clickable info icon button that pops up an explanation dialog when tapped.
 */
@Composable
fun BirtuInfoIconButton(
    title: String,
    explanation: String,
    modifier: Modifier = Modifier,
    iconSize: Dp = 16.dp,
    tint: Color = LocalBirtuColors.current.text2
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable { showDialog = true }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        PhosphorIcon(
            icon = PhosphorIcons.Info,
            contentDescription = "Info about $title",
            tint = tint,
            size = iconSize
        )
    }

    if (showDialog) {
        BirtuExplanationDialog(
            title = title,
            explanation = explanation,
            onDismiss = { showDialog = false }
        )
    }
}

/**
 * Dialog displaying a clean, high-legibility feature explanation.
 */
@Composable
fun BirtuExplanationDialog(
    title: String,
    explanation: String,
    onDismiss: () -> Unit
) {
    val colors = LocalBirtuColors.current
    val shape = RoundedCornerShape(24.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(312.dp)
                .clip(shape)
                .background(colors.surfaceRaised)
                .border(1.dp, colors.outline, shape)
                .padding(22.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BirtuIconTile(
                        icon = PhosphorIcons.Info,
                        size = 36.dp,
                        iconSize = 18.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = BirtuTypography.titleM.copy(
                            color = colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = explanation,
                    style = BirtuTypography.body.copy(
                        color = colors.text2,
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                BirtuPrimaryButton(
                    text = "Got it",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    height = 42.dp
                )
            }
        }
    }
}
