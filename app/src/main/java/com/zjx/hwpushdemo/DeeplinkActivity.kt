package com.zjx.hwpushdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * 打开应用程序的指定页面，并在定制的Activity类中接收数据。
 */
class DeeplinkActivity : AppCompatActivity() {
    private val TAG = "PushDemoLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deeplink)
        getIntentData(intent)
    }

    private fun getIntentData(intent: Intent?) {
        if (intent != null) {
            var age: Int? = null
            var name: String? = null
            try {
                val uri = intent.data
                if (uri == null) {
                    Log.e(TAG, "getData null")
                    return
                }
//                // 方式1 ：使用＆符号分隔键值对。

                uri.getQueryParameter("age")?.let {
                    age = it.toInt()
                }
                name = uri.getQueryParameter("name")
//                //方式2：直接向Intent添加参数。
//                age = intent.getIntExtra("age", 0)
//                name = intent.getStringExtra("name")
            } catch (e: NullPointerException) {
                Log.e(TAG, "NullPointer,$e")
            } catch (e: NumberFormatException) {
                Log.e(TAG, "NumberFormatException,$e")
            } catch (e: UnsupportedOperationException) {
                Log.e(TAG, "UnsupportedOperationException,$e")
            } finally {
                Log.i(TAG, "name $name,age $age")
                Toast.makeText(this, "name $name,age $age", Toast.LENGTH_SHORT).show()
            }

            // Obtain data set in way 2.
            // String name2 = intent.getStringExtra("name");
            // int age2 = intent.getIntExtra("age", -1);
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        getIntentData(intent)
    }
}