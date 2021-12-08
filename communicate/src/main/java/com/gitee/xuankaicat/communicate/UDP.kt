@file:Suppress("unused")

package com.gitee.xuankaicat.communicate

import android.os.Handler
import android.os.Looper
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.charset.Charset
import kotlin.concurrent.thread

class UDP : Communicate {
    private val socket = DatagramSocket()
    override var serverPort = 9000
    private var _address: InetAddress = InetAddress.getByName("192.168.200.1")
    override var address: String
        get() = _address.hostAddress!!
        set(value) {
            _address = InetAddress.getByName(value)
        }
    override var inCharset: Charset = Charsets.UTF_8
    override var outCharset: Charset = Charsets.UTF_8

    private var isReceiving = false
    private var receiveThread: Thread? = null

    override fun send(message: String) {
        val bytes = message.toByteArray(outCharset)
        val len = bytes.size
        val sendPacket = DatagramPacket(bytes, len, _address, serverPort)
        thread {
            socket.send(sendPacket)
        }
    }

    override fun startReceive(onReceive: OnReceiveFunc): Boolean {
        if(receiveThread != null) return false
        isReceiving = true
        val receive = ByteArray(100)
        val receivePacket = DatagramPacket(receive, receive.size)
        receiveThread = thread {
            while (isReceiving) {
                for (i in 0 until 100) {
                    if(receive[i].compareTo(0) == 0) break
                    receive[i] = 0
                }

                try {
                    socket.receive(receivePacket)
                    val data = String(receivePacket.data, inCharset)
                    Handler(Looper.getMainLooper()).post {
                        isReceiving = onReceive(data)
                    }
                } catch (ignore: Exception) {
                    break
                }
            }
            isReceiving = false
            receiveThread = null
        }
        return true
    }

    override fun stopReceive() {
        receiveThread?.interrupt()
    }

    override fun open(): Boolean {
        //使用直接返回true替代抛出异常可以统一应用层调用风格
        //throw Exception("UDP should not use 'open' function anyway.")
        return true
    }

    override fun close() = socket.close()
}