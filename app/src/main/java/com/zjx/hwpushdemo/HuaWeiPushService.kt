package com.zjx.hwpushdemo

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.huawei.hms.push.SendException
import java.util.*

/**
 * 华为推送
 */
class HuaWeiPushService : HmsMessageService() {
    val TAG = "PushDemoLog"

    companion object {
        const val CODELABS_ACTION = "com.huawei.codelabpush.action"
    }

    override fun onNewToken(token: String?) {
        Log.i(
            TAG, "received refresh token:${token}"
        )
        // 将token令牌发送到您的应用服务器。
        if (!TextUtils.isEmpty(token)) {
            //此方法回调必须在10秒内完成。否则，您需要启动一个新的Job进行回调处理。
            refreshedTokenToServer(token!!)
        }
        val intent = Intent()
        intent.action = CODELABS_ACTION
        intent.putExtra("method", "onNewToken")
        intent.putExtra("msg", "onNewToken called, token: ${token}")
        sendBroadcast(intent)
    }

    /**
     * 保存token到服务器（必须在10秒内完成）
     * @param token String
     */
    private fun refreshedTokenToServer(token: String) {
        Log.i(TAG, "sending token to server. token: ${token}")
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        Log.i(TAG, "onMessageReceived is called")
        if (message != null) {
            // getNotification() 从消息中获取通知数据实例.
            Log.i(
                TAG,
                // getCollapseKey() 获取消息的分类标识符（崩溃键）。
                "getCollapseKey: ${message.collapseKey} \n" +
                        // getData() 获取消息的有效内容数据.
                        "getData: ${message.data} \n" +
                        "getFrom: ${message.from} \n" +
                        // getTo() 获取邮件的收件人.
                        "getTo: ${message.to} \n" +
                        // getMessageId() 获取消息的ID.
                        "getMessageId: ${message.messageId} \n" +
                        // getOriginalUrgency() 获得消息的原始优先级.
                        "getOriginalUrgency: ${message.originalUrgency} \n" +
                        "getUrgency: ${message.urgency} \n" +
                        // getSentTime() 获取从服务器发送消息的时间.
                        "getSendTime: ${message.sentTime} \n" +
                        // getMessageType() 获取消息的类型.
                        "getMessageType: ${message.messageType} \n" +
                        "getTtl: ${message.ttl}"
            )

            val notification = message.notification
            if (notification != null) {
                Log.i(
                    // getImageUrl() 从消息中获取图像URL
                    TAG, " getImageUrl: ${notification.imageUrl} \n" +
                            // getTitle() 获取消息标题
                            "getTitle: ${notification.title} \n" +
                            // getTitleLocalizationKey() 获取通知消息标题的键
                            "getTitleLocalizationKey: ${notification.titleLocalizationKey} \n" +
                            // getTitleLocalizationArgs() 获取消息显示标题的可变参数
                            "getTitleLocalizationArgs: ${Arrays.toString(notification.titleLocalizationArgs)} \n" +
                            // getBody() 获取消息的显示内容
                            "getBody: ${notification.body} \n" +
                            // getBodyLocalizationKey() 获取消息显示内容的关键字
                            "getBodyLocalizationKey: ${notification.bodyLocalizationKey} \n" +
                            // getBodyLocalizationArgs() 获取消息显示内容的可变参数
                            "getBodyLocalizationArgs: ${Arrays.toString(notification.bodyLocalizationArgs)} \n" +
                            // getIcon() 从消息中获取图标
                            "getIcon: ${notification.icon} \n" +
                            // getSound() 从消息中获取声音
                            "getSound: ${notification.sound} \n" +
                            // getTag() 从消息中获取标签以覆盖消息
                            "getTag: ${notification.tag} \n" +
                            // getColor() 获取消息中图标的颜色
                            "getColor: ${notification.color} \n" +
                            // getClickAction() 获取通过轻按消息触发的操作
                            "getClickAction: ${notification.clickAction} \n" +
                            // getChannelId() 获取支持消息显示的通道ID
                            "getChannelId: ${notification.channelId} \n" +
                            // getLink() 从消息中获取要访问的URL
                            "getLink: ${notification.link} \n" +
                            // getNotifyId() 获取消息的唯一ID
                            "getNotifyId: ${notification.notifyId}"
                )
            }
            val intent = Intent()
            intent.action = CODELABS_ACTION
            intent.putExtra("method", "onMessageReceived")
            intent.putExtra(
                "msg",
                "onMessageReceived called, message id: ${message.messageId} , payload data:${message.data}"
            )
            sendBroadcast(intent)
            val judgeWhetherIn10s = false
            // If the messages are not processed in 10 seconds, the app needs to use WorkManager for processing.
            if (judgeWhetherIn10s) {
                startWorkManagerJob(message)
            } else {
                // Process message within 10s
                processWithin10s(message)
            }
        } else {
            Log.i(TAG, "Received message entity is null!")
        }
    }

    private fun startWorkManagerJob(message: RemoteMessage) {
        Log.d(TAG, "Start new Job processing.")
    }

    private fun processWithin10s(message: RemoteMessage) {
        Log.d(TAG, "Processing now.")
    }

    override fun onMessageSent(msgId: String?) {
        Log.i(
            TAG, "onMessageSent called, Message id:${msgId}"
        )
        val intent = Intent()
        intent.action = CODELABS_ACTION
        intent.putExtra("method", "onMessageSent")
        intent.putExtra("msg", "onMessageSent called, Message id:${msgId}")
        sendBroadcast(intent)
    }

    override fun onSendError(msgId: String?, exception: Exception?) {
        Log.i(
            TAG,
            "onSendError called, message id: ${msgId}, ErrCode: ${(exception as SendException).errorCode} , description: ${exception.message}"
        )
        val intent = Intent()
        intent.action = CODELABS_ACTION
        intent.putExtra("method", "onSendError")
        intent.putExtra(
            "msg",
            "onSendError called, message id: ${msgId}, ErrCode: ${(exception as SendException).errorCode} , description: ${exception.message}"
        )
        sendBroadcast(intent)
    }

    override fun onTokenError(e: java.lang.Exception?) {
        Log.i(TAG, "token error : ${e.toString()}")
    }
}