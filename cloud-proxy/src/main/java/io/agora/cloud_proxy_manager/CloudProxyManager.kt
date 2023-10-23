package io.agora.cloud_proxy_manager

import android.content.Context
import io.agora.authentication_manager.AuthenticationManager
import io.agora.rtm.*

open class CloudProxyManager(context: Context?) : AuthenticationManager(context!!) {

    override fun setupSignalingEngine(uid: Int): Boolean {
        // Define proxy configuration
        val proxyConfig = RtmProxyConfig( RtmConstants.RtmProxyType.HTTP,
                "<your proxy server domain name or IP address>",
                8080, // Your proxy server listening port,
                "<proxy login account>", // Optional
                "<proxy login password>" // Optional
            )

        try {
            val rtmConfig = RtmConfig.Builder(appId, uid.toString())
                .presenceTimeout(config!!.optString("presenceTimeout").toInt())
                .useStringUserId(false)
                .eventListener(eventListener)
                .proxyConfig(proxyConfig) // Set proxy configuration
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