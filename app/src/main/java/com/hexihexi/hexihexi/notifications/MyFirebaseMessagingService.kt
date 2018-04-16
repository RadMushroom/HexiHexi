package com.hexihexi.hexihexi.notifications


import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hexihexi.hexihexi.R
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val bitmap:Bitmap by lazy { BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round) }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random().nextInt(60000)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, "HEXIHELPER_CHANNEL")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmap)
                .setContentTitle(remoteMessage.notification.title)
                .setContentText(remoteMessage.notification.body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
