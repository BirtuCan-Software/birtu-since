package com.birtucansoftware.birtusince.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.data.LapEntity
import com.birtucansoftware.birtusince.domain.TimeEngine
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun ItemDetailPastRunsSection(
    laps: List<LapEntity>,
    onLapClick: (LapEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Past runs",
                style = BirtuTypography.titleM.copy(color = colors.text, fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "${laps.size} total",
                style = BirtuTypography.caption.copy(color = colors.text2)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (laps.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                laps.forEach { lap ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
                            .clickable { onLapClick(lap) }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "#${lap.runNumber}",
                            style = BirtuTypography.label.copy(color = colors.text2)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text = TimeEngine.formatCompact(lap.durationMillis),
                                style = BirtuTypography.body.copy(
                                    color = colors.text,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = TimeEngine.formatDateRange(lap.startEpochMillis, lap.endEpochMillis),
                                style = BirtuTypography.caption.copy(color = colors.text2)
                            )
                        }

                        PhosphorIcon(
                            icon = PhosphorIcons.CaretRight,
                            contentDescription = "Edit run",
                            tint = colors.text2,
                            size = 18.dp
                        )
                    }
                }
            }
        } else {
            Text(
                text = "No past runs yet.",
                style = BirtuTypography.body.copy(color = colors.text2)
            )
        }
    }
}
