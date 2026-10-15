package com.birtucansoftware.birtusince.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.birtucansoftware.birtusince.data.BirtuDatabase
import com.birtucansoftware.birtusince.data.BirtuRepository
import com.birtucansoftware.birtusince.data.PreferencesManager
import com.birtucansoftware.birtusince.notifications.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_WIDGET_START_OVER = "com.birtucansoftware.birtusince.ACTION_WIDGET_START_OVER"
        const val EXTRA_ITEM_ID = "extra_item_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_WIDGET_START_OVER) {
            val itemId = intent.getLongExtra(EXTRA_ITEM_ID, -1L)
            if (itemId == -1L) return

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = BirtuDatabase.getInstance(context)
                    val prefs = PreferencesManager(context)
                    val repository = BirtuRepository(db.itemDao(), prefs)

                    repository.startOver(itemId)

                    val item = repository.getItem(itemId)
                    if (item != null) {
                        AlarmScheduler.scheduleItemReminder(context, item)
                    }

                    // Refresh widget state immediately
                    BirtuAppWidgetProvider.updateAllWidgets(context)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
