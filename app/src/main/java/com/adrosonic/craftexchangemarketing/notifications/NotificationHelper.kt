package com.adrosonic.craftexchangemarketing.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.landing.ArtisanLandingActivity
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.landing.BuyerLandingActivity
import com.adrosonic.craftexchangemarketing.ui.modules.role.RoleSelectActivity
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

class NotificationHelper private constructor(context: Context) {

    private val mcontext: Context = context
    private lateinit var resultIntent : Intent
    fun displayNotification(title: String, body: String) {
        val builder = NotificationCompat.Builder(mcontext)
                .setSmallIcon(R.mipmap.ic_logo_main_round)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
        resultIntent= if (Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false)) {
            var profile = Prefs.getString(ConstantsDirectory.PROFILE,null)
            if(profile.equals(ConstantsDirectory.ARTISAN)) Intent(mcontext, ArtisanLandingActivity::class.java)
            else Intent(mcontext, BuyerLandingActivity::class.java)
        }
        else {
            Intent(mcontext, RoleSelectActivity::class.java)
        }
        val pendingIntent = PendingIntent.getActivity(mcontext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        val notificationManager = mcontext.getSystemService(Context.NOTIFICATION_SERVICE)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(ConstantsDirectory.CHANNEL_ID, ConstantsDirectory.CHANNEL_NAME, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            (notificationManager as? NotificationManager)?.createNotificationChannel(notificationChannel)
//            (notificationManager as? NotificationManager)?.cancelAll()
        }
        (notificationManager as? NotificationManager)?.notify(1, builder.build())
    }

    companion object {
        private var notificationHelperInstance: NotificationHelper? = null

        @Synchronized
        fun getInstance(context: Context): NotificationHelper? {
            if (notificationHelperInstance == null) {
                notificationHelperInstance = NotificationHelper(context)
            }
            return notificationHelperInstance
        }
    }
}