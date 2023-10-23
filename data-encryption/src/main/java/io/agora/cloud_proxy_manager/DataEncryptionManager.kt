package io.agora.cloud_proxy_manager

import android.content.Context
import io.agora.authentication_manager.AuthenticationManager
import io.agora.rtm.*
import io.agora.rtm.RtmConstants.RtmEncryptionMode
import java.util.Base64

open class DataEncryptionManager(context: Context?) : AuthenticationManager(context!!) {

    private fun toByteArray(base64String: String): ByteArray? {
        // Decode the Base64 string to a ByteArray
        return Base64.getDecoder().decode(base64String)
    }

    private fun toAscii(hexString: String): String {
        val output = StringBuilder()
        var i = 0
        while (i < hexString.length) {
            val str = hexString.substring(i, i + 2)
            val char = str.toInt(16).toChar()
            output.append(char)
            i += 2
        }
        return output.toString()
    }

    override fun setupSignalingEngine(uid: Int): Boolean {
        val encryptionMode: RtmEncryptionMode = RtmEncryptionMode.values().get(config!!.getInt("encryptionMode"))
        val encryptionKey = config!!.getString("cipherKey")
        val encryptionSalt = config!!.getString("salt")

        // Define encryption configuration
        val encryptionConfig = RtmEncryptionConfig(
            encryptionMode,
            toAscii(encryptionKey),
            toByteArray(encryptionSalt)
        )

        try {
            val rtmConfig = RtmConfig.Builder(appId, uid.toString())
                .presenceTimeout(config!!.optString("presenceTimeout").toInt())
                .useStringUserId(false)
                .eventListener(eventListener)
                .encryptionConfig(encryptionConfig) // Set encryption configuration
                .build()
            signalingEngine = RtmClient.create(rtmConfig)
            localUid = uid
        } catch (e: Exception) {
            notify(e.toString())
            return false
        }
        return true
    }

}