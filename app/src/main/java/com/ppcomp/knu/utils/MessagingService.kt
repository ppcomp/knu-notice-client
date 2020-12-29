package com.ppcomp.knu.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Subscription
import com.ppcomp.knu.`object`.noticeData.Alarm
import com.ppcomp.knu.activity.MainActivity
import java.time.LocalDate
import java.util.ArrayList

/**
 * 푸시 알림 클래스
 * @author 정준
 */
class MessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseService"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)

        if(remoteMessage.notification != null) {
            Log.d(TAG, "Notification Message Title: ${remoteMessage.notification?.title}")
            Log.d(TAG, "Notification Message Body: ${remoteMessage.notification?.body}")
            sendNotification(remoteMessage)
        } else if(remoteMessage.data != null){ // 데이터 활용, SharedPreferences 저장
            val preference = PreferenceHelper.getInstance(this)
            Log.d(TAG, "Data: ${remoteMessage.data}")
            val subscriptionCodes = remoteMessage.data["sub_codes"] // ex. "cse+main"
//            val keywords = remoteMessage.data["keys"]               // ex. "장학+등록"

            val splitSubsCode = subscriptionCodes?.split("-")

            val getAlarm = PreferenceHelper.get("alarm", "").toString()
            var listType: TypeToken<ArrayList<Alarm>> = object : TypeToken<ArrayList<Alarm>>() {}
            val makeGson = GsonBuilder().create()
            var alarmList = ArrayList<Alarm>()

            if(getAlarm!="") {
                alarmList = makeGson.fromJson(getAlarm, listType.type)
            }

            if (splitSubsCode != null) {
                for (i in splitSubsCode) {
                    Log.d("알람",i)
                    alarmList.add(Alarm(i, LocalDate.now().toString()))
                }
            }
            var toJson = makeGson.toJson(alarmList, listType.type)
            PreferenceHelper.put("alarm", toJson)
        }
    }
    override fun onNewToken(token: String) {
        Log.d(TAG, "new Token: $token")
    }

    private fun sendNotification(message: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("Notification", message.notification?.body)    // 메시지 값 전달
        }

        val CHANNEL_ID = "KeywordNotification"
        val CHANNEL_NAME = "키워드 알림채널"
        val description = "해당하는 키워드의 공지사항이 새로 올라오면 알려줍니다."
        val importance = NotificationManager.IMPORTANCE_HIGH

        var notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { //안드로이드 오레오부터 적용되는 코드
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            channel.description = description
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.setShowBadge(false)
            notificationManager.createNotificationChannel(channel)
        }

        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT) //intent를 특정 시점에 실행시킬 때 사용
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var notificationBuilder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(0, notificationBuilder.build())
    }
}