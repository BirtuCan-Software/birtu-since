package com.birtucansoftware.birtusince.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.birtucansoftware.birtusince.data.BirtuDatabase
import com.birtucansoftware.birtusince.data.BirtuRepository
import com.birtucansoftware.birtusince.data.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val itemId = intent.getLongExtra("item_id", -1L)
        val notificationId = intent.getIntExtra("notification_id", -1)

        if (notificationId != -1) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }

        if (itemId != -1L) {
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
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
