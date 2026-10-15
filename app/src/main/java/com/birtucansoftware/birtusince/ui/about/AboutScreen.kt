package com.birtucansoftware.birtusince.ui.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import com.birtucansoftware.birtusince.BuildConfig
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.birtucansoftware.birtusince.R
import com.birtucansoftware.birtusince.ui.components.BirtuIconTile
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }

    val colors = LocalBirtuColors.current
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(1.dp, colors.outline, CircleShape)
                    .clickable { onBackClick() }
                    .testTag("about_back_button"),
                contentAlignment = Alignment.Center
            ) {
                PhosphorIcon(
                    icon = PhosphorIcons.CaretLeft,
                    contentDescription = "Back",
                    tint = colors.text,
                    size = 20.dp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = "About",
                style = BirtuTypography.titleL.copy(color = colors.text, fontWeight = FontWeight.Bold)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // App Identity Hero Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.outline, RoundedCornerShape(22.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors.primarySoft)
                        .border(1.5.dp, colors.primary, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "BirtuSince Logo",
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "BirtuSince",
                    style = BirtuTypography.titleL.copy(color = colors.text, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "v${BuildConfig.VERSION_NAME}",
                    style = BirtuTypography.body.copy(color = colors.text2)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tactile, private-first event & habit interval tracker.",
                    style = BirtuTypography.body.copy(color = colors.text2, fontSize = 12.sp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dedicated Privacy & Zero Internet Promise Section
            Text(
                text = "Privacy Guarantee",
                style = BirtuTypography.label.copy(color = colors.text2, fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BirtuIconTile(
                        icon = PhosphorIcons.WifiSlash,
                        size = 46.dp,
                        iconSize = 24.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "No internet. Ever.",
                            style = BirtuTypography.titleM.copy(color = colors.text, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "100% on-device data sovereignty",
                            style = BirtuTypography.caption.copy(color = colors.primary, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "BirtuSince does not require or request any internet access permission in its manifest. Your habits, milestone records, and personal intervals never leave your device. There are zero cloud databases, zero trackers, and zero telemetry pingbacks. Everything you enter belongs exclusively to you.",
                    style = BirtuTypography.body.copy(color = colors.text2, lineHeight = 20.sp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceRaised)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PhosphorIcon(
                        icon = PhosphorIcons.ShieldCheck,
                        contentDescription = null,
                        tint = colors.primary,
                        size = 18.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Encrypted local SQLite & biometric protection",
                        style = BirtuTypography.caption.copy(color = colors.text, fontWeight = FontWeight.Medium)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Powered By Section
            Text(
                text = "Publisher & Developer",
                style = BirtuTypography.label.copy(color = colors.text2, fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "Powered by",
                    style = BirtuTypography.caption.copy(color = colors.text2)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Interactive link to birtucansoftware.com
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surfaceRaised)
                        .border(1.dp, colors.outline, RoundedCornerShape(14.dp))
                        .clickable {
                            openUrlInBrowser(context, "https://birtucansoftware.com")
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .testTag("birtucan_link_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        BirtuIconTile(
                            icon = PhosphorIcons.Sparkle,
                            size = 38.dp,
                            iconSize = 18.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "BirtuCan Technologies PLC",
                                style = BirtuTypography.body.copy(color = colors.text, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "birtucansoftware.com",
                                style = BirtuTypography.caption.copy(color = colors.primary, fontWeight = FontWeight.Medium)
                            )
                        }
                    }

                    PhosphorIcon(
                        icon = PhosphorIcons.ArrowSquareOut,
                        contentDescription = "Open Website",
                        tint = colors.primary,
                        size = 20.dp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "BirtuCan Technologies PLC creates privacy-respecting, high-performance software experiences engineered with cyber-tactile aesthetics and reliable local architectures.",
                    style = BirtuTypography.body.copy(color = colors.text2, lineHeight = 19.sp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

/**
 * Safely opens a website URL in the user's default browser with graceful fallbacks
 */
fun openUrlInBrowser(context: Context, urlString: String) {
    val uri = Uri.parse(urlString)
    val browserIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    try {
        // Attempt to launch via default intent
        context.startActivity(browserIntent)
    } catch (_: Exception) {
        // Fallback: try chooser or un-categorized VIEW
        try {
            val fallbackIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val chooser = Intent.createChooser(fallbackIntent, "Open with").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(chooser)
        } catch (_: Exception) {
            Toast.makeText(
                context,
                "No browser found. Visit: $urlString",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
