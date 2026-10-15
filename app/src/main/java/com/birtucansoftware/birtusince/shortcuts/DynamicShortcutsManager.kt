package com.birtucansoftware.birtusince.shortcuts

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import com.birtucansoftware.birtusince.MainActivity
import com.birtucansoftware.birtusince.R
import com.birtucansoftware.birtusince.data.ItemEntity

object DynamicShortcutsManager {

    fun updateShortcuts(context: Context, topItems: List<ItemEntity>) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return

        val shortcutManager = context.getSystemService(ShortcutManager::class.java) ?: return

        val shortcuts = topItems.take(3).map { item ->
            val intent = Intent(context, MainActivity::class.java).apply {
                action = "com.birtucansoftware.birtusince.ACTION_OPEN_ITEM"
                putExtra("shortcut_item_id", item.id)
                putExtra("shortcut_start_over", true)
            }

            ShortcutInfo.Builder(context, "shortcut_item_${item.id}")
                .setShortLabel("Start over: ${item.title}")
                .setLongLabel("Start over: ${item.title}")
                .setIcon(Icon.createWithResource(context, R.drawable.ic_widget_restart))
                .setIntent(intent)
                .build()
        }

        try {
            shortcutManager.dynamicShortcuts = shortcuts
        } catch (e: Exception) {
            // Ignore if restricted
        }
    }
}
