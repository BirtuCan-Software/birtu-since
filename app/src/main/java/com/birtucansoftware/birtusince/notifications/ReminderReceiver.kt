package com.birtucansoftware.birtusince.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.birtucansoftware.birtusince.MainActivity
import com.birtucansoftware.birtusince.R
import com.birtucansoftware.birtusince.domain.TimeEngine

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "birtusince_reminders"
        const val ACTION_START_OVER = "com.birtucansoftware.birtusince.ACTION_START_OVER"

        fun createNotificationChannel(manager: NotificationManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Local reminder notifications for tracked intervals"
                }
                manager.createNotificationChannel(channel)
            }
        }

        fun sendTestNotification(context: Context) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createNotificationChannel(notificationManager)

            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openPendingIntent = PendingIntent.getActivity(
                context,
                8888,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setColor(0xFFF28500.toInt()) // Tangelo orange theme primary
                .setContentTitle("Test Target Notification")
                .setContentText("Target notifications enabled! You will be notified when your tracked targets are reached.")
                .setAutoCancel(true)
                .setContentIntent(openPendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "Open BirtuSince", openPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            notificationManager.notify(9999, notification)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val itemId = intent.getLongExtra(AlarmScheduler.EXTRA_ITEM_ID, -1L)
        if (itemId == -1L) return

        val title = intent.getStringExtra(AlarmScheduler.EXTRA_ITEM_TITLE) ?: "Item"
        val targetMillis = intent.getLongExtra(AlarmScheduler.EXTRA_TARGET_MILLIS, 0L)
        val isPrivate = intent.getBooleanExtra(AlarmScheduler.EXTRA_IS_PRIVATE, false)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val bodyText = if (isPrivate) {
            "A locked item is ready."
        } else {
            val formattedTime = TimeEngine.formatCompact(targetMillis)
            "$title. It has been $formattedTime."
        }

        // Open action (opens app to item)
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("open_item_id", itemId)
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            (itemId * 10).toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Start over action (direct from notification)
        val startOverIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_START_OVER
            putExtra("item_id", itemId)
            putExtra("notification_id", itemId.toInt())
        }
        val startOverPendingIntent = PendingIntent.getBroadcast(
            context,
            (itemId * 10 + 1).toInt(),
            startOverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setColor(0xFFF28500.toInt()) // Tangelo orange theme primary
            .setContentTitle("Time is up")
            .setContentText(bodyText)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .addAction(R.drawable.ic_widget_restart, "Start over", startOverPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Open", openPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(itemId.toInt(), notification)
    }
}
