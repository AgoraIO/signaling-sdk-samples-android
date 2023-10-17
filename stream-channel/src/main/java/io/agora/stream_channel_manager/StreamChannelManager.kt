package io.agora.stream_channel_manager


import android.content.Context
import io.agora.authentication_manager.AuthenticationManager
import io.agora.rtm.ErrorInfo
import io.agora.rtm.JoinChannelOptions
import io.agora.rtm.ResultCallback

open class StreamChannelManager(context: Context?) : AuthenticationManager(
    context!!
) {
    fun joinStreamChannel(channelName: String): Int {
        val token = ""
        val streamChannel = signalingEngine?.createStreamChannel(channelName)
        streamChannel?.join(
            JoinChannelOptions(token, true, true, true),
            object : ResultCallback<Void?> {
                override fun onFailure(errorInfo: ErrorInfo?) {
                    notify("Join stream channel failed:\n" + errorInfo.toString())
                }

                override fun onSuccess(responseInfo: Void?) {
                    //isSubscribed = true
                    mListener?.onSubscribeUnsubscribe(isSubscribed)
                    notify("Joined channel: $channelName")
                }
            })
        return 0
    }
    
}