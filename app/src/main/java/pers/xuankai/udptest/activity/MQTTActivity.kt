package pers.xuankai.udptest.activity

import android.os.Bundle
import com.dylanc.longan.immerseStatusBar
import com.dylanc.longan.startActivity
import com.gitee.xuankaicat.communicate.MQTTCommunicate
import pers.xuankai.udptest.BaseActivity
import pers.xuankai.udptest.databinding.ActivityMqttactivityBinding
import java.nio.charset.Charset

class MQTTActivity : BaseActivity<ActivityMqttactivityBinding>() {
    private val communicate = MQTTCommunicate.MQTT.apply {
        address = "192.168.3.22"
        serverPort = 1883
        inCharset = Charset.forName("gb2312")
        outCharset = Charset.forName("gb2312")
        username = "siot"
        password = "siot"
        publishTopic = "DeviceTest/000000"
        responseTopic = "DeviceTest/123456"
        open()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        immerseStatusBar()

        binding.btn.setOnClickListener {
            val sendText = binding.editText.text.toString()
            if(sendText.isEmpty()) return@setOnClickListener

            communicate.send(sendText)
            binding.textView.text = "等待数据..."
            communicate.startReceive {
                binding.textView.text = it
                return@startReceive false
            }
        }

        /**
         * 以一个收到status决定亮灭的小灯为例，按下发送开启时小灯亮，按下发送关闭时小灯灭
         */

        binding.btnOpen.setOnClickListener {
            val sendText = "{cmd:\"set\", status:1}"
            communicate.send(sendText)
        }

        binding.btnClose.setOnClickListener {
            val sendText = "{cmd:\"set\", status:0}"
            communicate.send(sendText)
        }

        binding.btnUdp.setOnClickListener {
            startActivity<MainActivity>()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        communicate.stopReceive()
        communicate.close()
    }
}