package com.name.petmemo.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.name.petmemo.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val petName = intent.getStringExtra("PET_NAME") ?: "Ваш питомец"
        val eventTitle = intent.getStringExtra("EVENT_TITLE") ?: "Напоминание"
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)

        val notification = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_round)
            .setContentTitle("Напоминание для: $petName")
            .setContentText(eventTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }
}