package com.birtucansoftware.birtusince.ui.tile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.birtucansoftware.birtusince.BirtuApp
import com.birtucansoftware.birtusince.ui.components.BirtuPrimaryButton
import com.birtucansoftware.birtusince.ui.components.BirtuTextField
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTheme
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors
import com.birtucansoftware.birtusince.widget.BirtuAppWidgetProvider
import kotlinx.coroutines.launch

class QuickCaptureActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = (application as BirtuApp).repository

        setContent {
            BirtuTheme {
                val colors = LocalBirtuColors.current
                var title by remember { mutableStateOf("") }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.scrim)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(colors.surfaceRaised)
                            .border(1.dp, colors.outline, RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "New",
                                style = BirtuTypography.titleL.copy(
                                    color = colors.text,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            BirtuTextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = "Watered the plants",
                                label = "What happened?",
                                maxCharacters = 120,
                                onDone = {
                                    if (title.isNotBlank()) {
                                        lifecycleScope.launch {
                                            repository.createItem(title = title.trim())
                                            BirtuAppWidgetProvider.updateAllWidgets(this@QuickCaptureActivity)
                                            finish()
                                        }
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            BirtuPrimaryButton(
                                text = "Start",
                                icon = PhosphorIcons.Plus,
                                enabled = title.isNotBlank(),
                                onClick = {
                                    if (title.isNotBlank()) {
                                        lifecycleScope.launch {
                                            repository.createItem(title = title.trim())
                                            BirtuAppWidgetProvider.updateAllWidgets(this@QuickCaptureActivity)
                                            finish()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
