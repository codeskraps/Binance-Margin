package com.codeskraps.core.domain.pnl

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar


class PnLTaskReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        //Log.i(TAG, "onReceive: ")
        context?.let {
            WorkManager.getInstance(it).run {

                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .build()

                enqueue(
                    OneTimeWorkRequestBuilder<PnLWorker>()
                        .setConstraints(constraints)
                        .build()
                )
            }
        }
    }

    companion object {
        //private val TAG = PnLTaskReceiver::class.java.simpleName

        fun setPnLAlarm(context: Context) {
            (context.getSystemService(Application.ALARM_SERVICE) as AlarmManager).run {
                val calendar: Calendar = Calendar.getInstance().apply {
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    add(Calendar.HOUR_OF_DAY, 1)
                }


                /*val testCalendar: Calendar = Calendar.getInstance().apply {
                    add(Calendar.MINUTE, 1)
                }*/

                //Log.i(TAG, "setPnLAlarm: ${testCalendar.timeInMillis} ${calendar.timeInMillis}")

                set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent(context, PnLTaskReceiver::class.java),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
        }
    }
}