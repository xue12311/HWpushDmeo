package com.zjx.hwpushdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.push.HmsMessaging

class MainActivity : AppCompatActivity() {
    val TAG = "PushDemoLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * 在异步模式下获取AAID
     */
    fun getAAID(view: View?) {
        HmsInstanceId.getInstance(this).aaid
            .addOnSuccessListener { aaidResult ->
                //获取AAID 成功
                var aaid: String = aaidResult.id
                showLog("获取AAID成功 AAID = ${aaid}")
            }.addOnFailureListener { exception ->
                //获取AAID 失败
                showLog("获取AAID失败  错误详情: ${exception}")
            }
    }

    /**
     * 删除本地AAID及其生成时间戳
     */
    fun deleteAAID(view: View?) {
        try {
            showLog("删除AAID成功")
            HmsInstanceId.getInstance(this).deleteAAID()
        } catch (e: Exception) {
            showLog("删除AAID失败  错误详情: ${e}")
        }
    }

    /**
     * 此方法是同步方法，您不能在主线程中调用它。否则，主线程可能会被阻塞。
     * getToken（String appId，String scope），此方法用于获取访问HUAWEI Push Kit所需的令牌。
     * 如果没有本地AAID，该方法将在调用时自动生成AAID，因为华为推送服务器需要根据该AAID生成令牌。
     *
     * 接收token有两种场景：
     * EMUI10.0以上的华为设备在上述申请token步骤中HmsInstanceId.getInstance.getToken方法返回token；
     * EMUI10.0以下的设备在如下方法中调用上述申请token方法后，通过MyPushService中重载的onNewToken方法获得的token。
     */
    fun getToken(view: View?) {
        Thread {
            try {
                // 读取文件：agconnect-services.json
                var appid = AGConnectServicesConfig.fromContext(this@MainActivity)
                    .getString("client/app_id")
                showLog("获取appid成功 appid = $appid")
                var token = HmsInstanceId.getInstance(this@MainActivity).getToken(appid, "HCM")
                showLog("获取Token成功 appid = $appid , token = $token")
            } catch (e: Exception) {
                showLog("获取Token失败 错误详情：$e")
            }
        }.start()
    }

    /**
     * 此方法是同步方法。不要在主线程中调用它。否则，主线程可能会被阻塞。
     * deleteToken（String appId，String scope）此方法用于获取令牌。
     * 删除令牌后，将不会删除相应的AAID。
     */
    fun deleteToken(view: View?) {
        Thread {
            try {
                // 读取文件：agconnect-services.json
                var appid = AGConnectServicesConfig.fromContext(this@MainActivity)
                    .getString("client/app_id")
                showLog("获取appid成功 appid = $appid")
                HmsInstanceId.getInstance(this@MainActivity).deleteToken(appid, "HCM")
                showLog("删除Token成功 appid = $appid")
            } catch (e: Exception) {
                showLog("删除Token失败 错误详情：$e")
            }
        }.start()
    }

    /**
     * 添加订阅
     */
    fun addTopic(view: View?) {
        try {
            HmsMessaging.getInstance(this)
                .subscribe("zjx")
                .addOnCompleteListener { task ->
                    //订阅成功
                    if (task.isSuccessful) {
                        showLog("订阅成功")
                    } else {
                        showLog("订阅失败 ${task.exception.message}")
                    }
                }
        } catch (e: Exception) {
            showLog("订阅失败 ${e}")
        }
    }

    /**
     * 取消订阅
     */
    fun deleteTopic(view: View?) {
        try {
            HmsMessaging.getInstance(this)
                .unsubscribe("zjx")
                .addOnCompleteListener { task ->
                    //订阅成功
                    if (task.isSuccessful) {
                        showLog("取消订阅成功")
                    } else {
                        showLog("取消订阅失败 ${task.exception.message}")
                    }
                }
        } catch (e: Exception) {
            showLog("取消订阅失败 ${e}")
        }
    }

    /**
     * 开启推送
     */
    fun getTurnOnPush(view: View?) {
        HmsMessaging.getInstance(this).turnOnPush().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showLog("开启推送成功")
            } else {
                showLog("开启推送失败 ${task.exception.message}")
            }
        }
    }

    /**
     * 关闭推送
     */
    fun getTurnOffPush(view: View?) {
        HmsMessaging.getInstance(this).turnOffPush().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showLog("关闭推送成功")
            } else {
                showLog("关闭推送失败 ${task.exception.message}")
            }
        }
    }

    /**
     * 是否已经启用自动初始化
     */
    fun isAutoInitEnabled(view: View?) {
        var isEnabled = HmsMessaging.getInstance(this).isAutoInitEnabled
        showLog("isAutoInitEnabled = ${isEnabled}")
    }

    /**
     * 设置是否需要启用自动初始化
     */
    fun setAutoInitEnabled(view: View?) {
        var isEnabled = HmsMessaging.getInstance(this).isAutoInitEnabled
        setAutoInitEnabled(!isEnabled)
    }

    private fun setAutoInitEnabled(isEnabled: Boolean) {
        HmsMessaging.getInstance(this).isAutoInitEnabled = isEnabled
        showLog("setAutoInitEnabled : ${isEnabled}")
    }

    /**
     * 在打开指定界面中，如何生成Intent参数。
     */
    fun generateIntentUri(view: View?) {
        var intent = Intent(Intent.ACTION_VIEW)
        //传递参数
        // 方式1 ：使用＆符号分隔键值对。
        //intent://com.zjx.hwpushdemo/deeplink?name=zjx&age=18#Intent;scheme=pushscheme;launchFlags=0x4000000;end
        intent.data = Uri.parse("pushscheme://com.zjx.hwpushdemo/deeplink?name=zjx&age=18")
//        //方式2：直接向Intent添加参数。
//        //intent://com.zjx.hwpushdemo/deeplink?#Intent;scheme=pushscheme;launchFlags=0x4000000;i.age=18;S.name=zjx;end
//        intent.setData(Uri.parse("pushscheme://com.zjx.hwpushdemo/deeplink?"))
//        intent.putExtra("name", "zjx")
//        intent.putExtra("age", 18)
        // 以下标志是必需的。如果未添加，则可能会显示重复的消息。
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val intentUri = intent.toUri(Intent.URI_INTENT_SCHEME)
        // intentUri的值将分配给要发送的消息中的intent参数。
        showLog("intentUri : ${intentUri}")
    }

    //    ACj60F5MZ7JKTf4Bn84bJHWplmou03usqrP9Pk0l19CuM9sCMEr-RMbwk5VwPO4ZXSfZcgbfX7FGbGSMT4OQzgWCrhLlL__SxV-qFJuCk1mereJpPRJ_uVnzDrJ6bSriMg
    fun showLog(log: String) {
        Log.i(TAG, "当前线程: ${Thread.currentThread().name} , ${log}")
    }
}
