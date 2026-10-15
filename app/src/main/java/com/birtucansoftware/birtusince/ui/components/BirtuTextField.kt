package com.birtucansoftware.birtusince.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun BirtuTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    modifier: Modifier = Modifier,
    maxCharacters: Int = 120,
    singleLine: Boolean = true,
    minHeight: Dp = 56.dp,
    imeAction: ImeAction = ImeAction.Done,
    onDone: () -> Unit = {}
) {
    val colors = LocalBirtuColors.current
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(16.dp)

    val borderColor = if (isFocused) colors.primary else colors.outline
    val borderWidth = if (isFocused) 2.dp else 1.dp

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(minHeight)
                .clip(shape)
                .background(colors.surfaceRaised)
                .border(borderWidth, borderColor, shape)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (isFocused || value.isNotEmpty()) {
                    Text(
                        text = label,
                        style = BirtuTypography.caption.copy(
                            color = if (isFocused) colors.primaryStrong else colors.text2,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = if (isFocused) placeholder else label,
                            style = BirtuTypography.body.copy(
                                color = colors.text2,
                                fontSize = 15.sp
                            )
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = {
                            if (it.length <= maxCharacters) {
                                onValueChange(it)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isFocused = it.isFocused },
                        textStyle = BirtuTypography.body.copy(
                            color = colors.text,
                            fontSize = 15.sp
                        ),
                        singleLine = singleLine,
                        cursorBrush = SolidColor(colors.primary),
                        keyboardOptions = KeyboardOptions(imeAction = imeAction),
                        keyboardActions = KeyboardActions(onDone = { onDone() })
                    )
                }
            }
        }

        // Show character count only from 100 on (e.g. "112 / 120")
        AnimatedVisibility(
            visible = value.length >= 100,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${value.length} / $maxCharacters",
                    style = BirtuTypography.caption.copy(
                        color = colors.text2,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
