package com.birtucansoftware.birtusince.ui.detail

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.birtucansoftware.birtusince.domain.ItemDerivedStatus
import com.birtucansoftware.birtusince.domain.ItemState
import com.birtucansoftware.birtusince.domain.TimeEngine
import com.birtucansoftware.birtusince.ui.components.BirtuSegmentedControl
import com.birtucansoftware.birtusince.ui.components.HeroProgressRing
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun ItemDetailHeroPanel(
    derived: ItemDerivedStatus,
    targetIntervalMillis: Long?,
    startEpochMillis: Long,
    preferredUnit: String,
    currentUnitIndex: Int,
    unitList: List<String>,
    isArchived: Boolean,
    isPaused: Boolean,
    isLate: Boolean,
    onUnitChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Hero Timer Panel (radius 28, surface-raised, 240 dp ring)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(colors.surfaceRaised)
                .border(1.dp, colors.outline, RoundedCornerShape(28.dp))
                .padding(vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HeroProgressRing(
                    progressFraction = derived.progressFraction,
                    isLate = isLate,
                    hasReminder = targetIntervalMillis != null,
                    size = 240.dp,
                    strokeWidth = 12.dp
                ) {
                    val primaryText = derived.formattedDuration.primaryText
                    val dynamicFontSize = when {
                        primaryText.length <= 7 -> 32.sp
                        primaryText.length <= 11 -> 24.sp
                        primaryText.length <= 15 -> 18.sp
                        primaryText.length <= 19 -> 15.sp
                        else -> 12.sp
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .clickable {
                                // Tapping big timer cycles through units
                                val nextIdx = (currentUnitIndex + 1) % unitList.size
                                val nextUnit = when (nextIdx) {
                                    1 -> TimeEngine.UNIT_DAYS
                                    2 -> TimeEngine.UNIT_HOURS
                                    3 -> TimeEngine.UNIT_MIN
                                    4 -> TimeEngine.UNIT_SEC
                                    else -> TimeEngine.UNIT_AUTO
                                }
                                onUnitChanged(nextUnit)
                            }
                    ) {
                        Text(
                            text = primaryText,
                            style = BirtuTypography.timerXL.copy(
                                color = if (isPaused) colors.text.copy(alpha = 0.55f) else colors.text,
                                fontSize = dynamicFontSize
                            ),
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Status line with 6 dp status dot
                        val (statusText, statusColor) = when {
                            isArchived -> "Put away" to colors.text2
                            isPaused -> "Paused" to colors.text2
                            isLate -> {
                                val lateBy = (derived.state as ItemState.Late).lateByMillis
                                "Late by ${TimeEngine.formatCompact(lateBy)}" to colors.late
                            }
                            else -> "Running" to colors.primaryStrong
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = statusText,
                                style = BirtuTypography.label.copy(
                                    color = statusColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Since ${TimeEngine.formatSinceDate(startEpochMillis)}",
                    style = BirtuTypography.caption.copy(color = colors.text2)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Unit toggle segmented control: Auto, Days, Hours, Min, Sec
        BirtuSegmentedControl(
            items = unitList,
            selectedIndex = currentUnitIndex,
            onItemSelected = { idx ->
                val chosen = when (idx) {
                    1 -> TimeEngine.UNIT_DAYS
                    2 -> TimeEngine.UNIT_HOURS
                    3 -> TimeEngine.UNIT_MIN
                    4 -> TimeEngine.UNIT_SEC
                    else -> TimeEngine.UNIT_AUTO
                }
                onUnitChanged(chosen)
            }
        )
    }
}
