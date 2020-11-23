package com.adrosonic.craftexchangemarketing.services.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.ui.modules.admin.landing.adminLandingIntent
//import com.adrosonic.craftexchangemarketing.ui.modules.artisan.landing.artisanLandingIntent
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.landing.buyerLandingIntent
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pixplicity.easyprefs.library.Prefs

class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        // when App is in foreground, notification message:
        Log.e(TAG, "onMessageReceived: " + p0.notification?.title)
        val intent = adminLandingIntent(true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId =ConstantsDirectory.CHANNEL_ID
        var builder= NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_logo_main_foreground)
            .setContentTitle(p0.getNotification()?.getTitle())
            .setContentText(p0.getNotification()?.getBody())
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
        val manager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel( channelId,  ConstantsDirectory.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }
        manager.cancelAll()
        manager.notify(1, builder.build())
    }

    companion object {
        const val TAG = "MessagingService"
    }
}



