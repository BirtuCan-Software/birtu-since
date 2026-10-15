package com.birtucansoftware.birtusince

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.birtucansoftware.birtusince.data.BirtuDatabase
import com.birtucansoftware.birtusince.data.BirtuRepository
import com.birtucansoftware.birtusince.data.PreferencesManager
import com.birtucansoftware.birtusince.notifications.ReminderReceiver

class BirtuApp : Application() {

    lateinit var database: BirtuDatabase
        private set

    lateinit var preferences: PreferencesManager
        private set

    lateinit var repository: BirtuRepository
        private set

    override fun onCreate() {
        super.onCreate()

        database = BirtuDatabase.getInstance(this)
        preferences = PreferencesManager(this)
        repository = BirtuRepository(database.itemDao(), preferences)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderReceiver.CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_channel_desc)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
