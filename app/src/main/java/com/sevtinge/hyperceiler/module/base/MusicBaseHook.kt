package com.sevtinge.hyperceiler.module.base

import android.annotation.*
import android.app.*
import android.app.AndroidAppHelper.*
import android.graphics.drawable.*
import android.os.*
import androidx.core.app.*
import androidx.core.graphics.drawable.*
import cn.lyric.getter.api.*
import cn.lyric.getter.api.data.*
import cn.lyric.getter.api.listener.*
import cn.lyric.getter.api.tools.Tools.registerLyricListener
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createAfterHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import org.json.*

abstract class MusicBaseHook : BaseHook() {
    val context: Application by lazy { currentApplication() }
    // val modRes: Resources = getModuleRes(context)

    private val receiver = LyricReceiver(object : LyricListener() {
        override fun onUpdate(lyricData: LyricData) {
            try {
                this@MusicBaseHook.onUpdate(lyricData)
            } catch (e: Throwable) {
                logE(TAG, lpparam.packageName, e)
            }
        }

        override fun onStop(lyricData: LyricData) {
            try {
                this@MusicBaseHook.onStop()
            } catch (e: Throwable) {
                logE(TAG, lpparam.packageName, e)
            }
        }
    })

    init {
        loadClass("android.app.Application").methodFinder().filterByName("onCreate").first()
            .createAfterHook {
                registerLyricListener(context, API.API_VERSION, receiver)
                logE(TAG, lpparam.packageName, "registerLyricListener")
            }
    }

    abstract fun onUpdate(lyricData: LyricData)
    abstract fun onStop()

    @SuppressLint("NotificationPermission")
    fun sendNotification(text: String) {
        //  logE("sendNotification: " + context.packageName + ": " + text)
        createNotificationChannel()
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val bitmap = context.packageManager.getActivityIcon(launchIntent!!).toBitmap()
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setContentTitle(text)
        builder.setSmallIcon(IconCompat.createWithBitmap(bitmap))
        builder.setTicker(text).setPriority(NotificationCompat.PRIORITY_LOW)
        builder.setContentIntent(
            PendingIntent.getActivity(
                context, 0, launchIntent, PendingIntent.FLAG_MUTABLE
            )
        )
        val jSONObject = JSONObject()
        val jSONObject3 = JSONObject()
        val jSONObject4 = JSONObject()
        jSONObject4.put("type", 1)
        jSONObject4.put("title", text)
        jSONObject3.put("baseInfo", jSONObject4)
        jSONObject3.put("ticker", text)
        jSONObject3.put("tickerPic", "miui.focus.pic_ticker")
        jSONObject3.put("tickerPicDark", "miui.focus.pic_ticker_dark")

        jSONObject.put("param_v2", jSONObject3)
        val bundle = Bundle()
        bundle.putString("miui.focus.param", jSONObject.toString())
        val bundle3 = Bundle()
        bundle3.putParcelable(
            "miui.focus.pic_ticker", Icon.createWithBitmap(bitmap)
        )
        bundle3.putParcelable(
            "miui.focus.pic_ticker_dark", Icon.createWithBitmap(bitmap)
        )
        bundle.putBundle("miui.focus.pics", bundle3)


        builder.addExtras(bundle)
        val notification = builder.build()
        (context.getSystemService("notification") as NotificationManager).notify(
            CHANNEL_ID.hashCode(), notification
        )
    }


    private fun createNotificationChannel() {
        val notificationManager = context.getSystemService("notification") as NotificationManager
        val notificationChannel = NotificationChannel(
            CHANNEL_ID, "焦点通知歌词", NotificationManager.IMPORTANCE_DEFAULT
        )
        // 记得换下，下面出了点故障，先写死中文了
        // modRes.getString(R.string.system_ui_statusbar_music_notification)
        notificationChannel.setSound(null, null)
        notificationManager.createNotificationChannel(notificationChannel)
    }


    @SuppressLint("NotificationPermission")
    fun cancelNotification() {
        (context.getSystemService("notification") as NotificationManager).cancel(CHANNEL_ID.hashCode())
    }

    companion object {
        const val CHANNEL_ID: String = "channel_id_focusNotifLyrics"
    }
}