package com.olgun.unutma

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

/** One-off notification for a newly received reminder; no ongoing status notification. */
object IncomingNotice {
 fun show(c:Context,r:Reminder) {
  try {
   if(Build.VERSION.SDK_INT>=33 && c.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)return
   val manager=c.getSystemService(NotificationManager::class.java)
   val channel="time_ikiz_received"
   if(Build.VERSION.SDK_INT>=26)manager.createNotificationChannel(NotificationChannel(channel,"Gelen hatırlatmalar",NotificationManager.IMPORTANCE_DEFAULT))
   val open=PendingIntent.getActivity(c,r.id,Intent(c,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
   val notice=android.app.Notification.Builder(c,channel)
    .setSmallIcon(android.R.drawable.ic_dialog_info)
    .setContentTitle("Time-ikiz: Yeni hatırlatma")
    .setContentText(r.text.take(120))
    .setStyle(android.app.Notification.BigTextStyle().bigText(r.text.take(500)))
    .setContentIntent(open).setAutoCancel(true).build()
   manager.notify(r.id,notice)
  }catch(e:Exception){android.util.Log.w("TimeIkizNotice","Could not show incoming reminder notification",e)}
 }
}
