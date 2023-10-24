package io.agora.geofencing_manager

import android.content.Context
import java.util.EnumSet
import io.agora.authentication_manager.AuthenticationManager
import io.agora.rtm.*
import io.agora.rtm.RtmConstants.RtmAreaCode

open class GeofencingManager(context: Context?) : AuthenticationManager(context!!) {

    override fun setupSignalingEngine(uid: Int): Boolean {
        // Define the set of area codes
        val areaCodes = EnumSet.of(RtmAreaCode.NA, RtmAreaCode.EU)
        notify("Geofencing area codes: ${areaCodes}")

        try {
            val rtmConfig = RtmConfig.Builder(appId, uid.toString())
                .presenceTimeout(config!!.optString("presenceTimeout").toInt())
                .useStringUserId(false)
                .eventListener(eventListener)
                .areaCode(areaCodes) // Specify the area codes
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