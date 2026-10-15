package com.birtucansoftware.birtusince.ui.lock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.ui.components.BirtuPrimaryButton
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun AppLockScreen(
    onUnlockClick: () -> Unit,
    hasFailedAttempt: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(hasFailedAttempt) {
        if (hasFailedAttempt) {
            // Shake 6 dp sideways 3 times in 240 ms
            repeat(3) {
                shakeOffset.animateTo(6f, tween(40))
                shakeOffset.animateTo(-6f, tween(40))
            }
            shakeOffset.animateTo(0f, tween(40))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(x = shakeOffset.value.dp)
        ) {
            // 128 dp circle filled primary-soft, Lock icon 64 dp
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(colors.primarySoft),
                contentAlignment = Alignment.Center
            ) {
                PhosphorIcon(
                    icon = PhosphorIcons.Lock,
                    contentDescription = null,
                    tint = colors.primaryStrong,
                    size = 64.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Locked",
                style = BirtuTypography.titleL.copy(
                    color = colors.text,
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (hasFailedAttempt) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Try again",
                    style = BirtuTypography.body.copy(color = colors.late)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            BirtuPrimaryButton(
                text = "Unlock",
                icon = PhosphorIcons.Fingerprint,
                onClick = onUnlockClick,
                height = 56.dp
            )
        }
    }
}
