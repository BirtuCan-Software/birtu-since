package com.birtucansoftware.birtusince.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import com.birtucansoftware.birtusince.MainActivity
import com.birtucansoftware.birtusince.R
import com.birtucansoftware.birtusince.data.BirtuDatabase
import com.birtucansoftware.birtusince.data.BirtuRepository
import com.birtucansoftware.birtusince.data.PreferencesManager
import com.birtucansoftware.birtusince.domain.ItemState
import com.birtucansoftware.birtusince.domain.ItemStateResolver
import com.birtucansoftware.birtusince.domain.TimeEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BirtuAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAllWidgets(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        fun updateAllWidgets(context: Context, appWidgetManager: AppWidgetManager? = null, appWidgetIds: IntArray? = null) {
            val manager = appWidgetManager ?: AppWidgetManager.getInstance(context)
            val ids = appWidgetIds ?: manager.getAppWidgetIds(ComponentName(context, BirtuAppWidgetProvider::class.java))

            CoroutineScope(Dispatchers.IO).launch {
                val db = BirtuDatabase.getInstance(context)
                val prefs = PreferencesManager(context)
                val repo = BirtuRepository(db.itemDao(), prefs)
                val activeItems = repo.getAllActiveItems()
                val primaryItem = activeItems.firstOrNull()

                for (appWidgetId in ids) {
                    val views = RemoteViews(context.packageName, R.layout.widget_birtu_layout)

                    if (primaryItem != null) {
                        val derived = ItemStateResolver.resolve(
                            startEpochMillis = primaryItem.startEpochMillis,
                            isPaused = primaryItem.isPaused,
                            pauseEpochMillis = primaryItem.pauseTimestamp,
                            isArchived = primaryItem.isArchived,
                            isPrivate = primaryItem.isPrivate,
                            isUnlockedThisSession = false,
                            targetIntervalMillis = primaryItem.targetIntervalMillis,
                            unit = primaryItem.preferredUnit
                        )

                        if (primaryItem.isPrivate) {
                            views.setTextViewText(R.id.widget_title, "Locked item")
                            views.setTextViewText(R.id.widget_timer_text, "--:--")
                            views.setViewVisibility(R.id.widget_timer_text, View.VISIBLE)
                            views.setViewVisibility(R.id.widget_chronometer, View.GONE)
                            views.setTextViewText(R.id.widget_since_text, "")
                            views.setViewVisibility(R.id.widget_status_chip, View.GONE)
                        } else {
                            views.setTextViewText(R.id.widget_title, primaryItem.title)
                            views.setTextViewText(R.id.widget_since_text, "Since ${TimeEngine.formatSinceDate(primaryItem.startEpochMillis)}")

                            if (derived.state is ItemState.Late) {
                                views.setViewVisibility(R.id.widget_status_chip, View.VISIBLE)
                                val lateBy = (derived.state as ItemState.Late).lateByMillis
                                views.setTextViewText(R.id.widget_status_chip, "Late ${TimeEngine.formatCompact(lateBy)}")
                            } else {
                                views.setViewVisibility(R.id.widget_status_chip, View.GONE)
                            }

                            // If under 1 hour: live Chronometer; otherwise formatted duration TextView
                            if (!primaryItem.isPaused && derived.elapsedMillis < 3_600_000L) {
                                views.setViewVisibility(R.id.widget_timer_text, View.GONE)
                                views.setViewVisibility(R.id.widget_chronometer, View.VISIBLE)
                                val base = SystemClock.elapsedRealtime() - derived.elapsedMillis
                                views.setChronometer(R.id.widget_chronometer, base, "%s", true)
                            } else {
                                views.setViewVisibility(R.id.widget_chronometer, View.GONE)
                                views.setViewVisibility(R.id.widget_timer_text, View.VISIBLE)
                                views.setTextViewText(R.id.widget_timer_text, derived.formattedDuration.primaryText)
                            }
                        }

                        // Tap body opens app
                        val openIntent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            putExtra("open_item_id", primaryItem.id)
                        }
                        val openPendingIntent = PendingIntent.getActivity(
                            context,
                            appWidgetId,
                            openIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_root, openPendingIntent)

                        // Tap Start over button executes without opening app (FR-INT-002, AC-005)
                        val startOverIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                            action = WidgetActionReceiver.ACTION_WIDGET_START_OVER
                            putExtra(WidgetActionReceiver.EXTRA_ITEM_ID, primaryItem.id)
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        }
                        val startOverPendingIntent = PendingIntent.getBroadcast(
                            context,
                            appWidgetId + 1000,
                            startOverIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_btn_start_over, startOverPendingIntent)
                    } else {
                        views.setTextViewText(R.id.widget_title, context.getString(R.string.app_name))
                        views.setTextViewText(R.id.widget_timer_text, "00:00:00")
                        views.setViewVisibility(R.id.widget_chronometer, View.GONE)
                        views.setViewVisibility(R.id.widget_timer_text, View.VISIBLE)
                        views.setTextViewText(R.id.widget_since_text, "Tap to start tracking")
                        views.setViewVisibility(R.id.widget_status_chip, View.GONE)

                        val openIntent = Intent(context, MainActivity::class.java)
                        val openPendingIntent = PendingIntent.getActivity(
                            context,
                            appWidgetId,
                            openIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_root, openPendingIntent)
                    }

                    manager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }
}
