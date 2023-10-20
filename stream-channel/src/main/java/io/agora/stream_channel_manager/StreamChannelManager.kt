package io.agora.stream_channel_manager

import android.content.Context
import io.agora.authentication_manager.AuthenticationManager
import io.agora.rtm.ErrorInfo
import io.agora.rtm.JoinChannelOptions
import io.agora.rtm.ResultCallback
import io.agora.rtm.StreamChannel

open class StreamChannelManager(context: Context?) : AuthenticationManager(context!!) {
    private lateinit var streamChannel: StreamChannel

    fun leaveStreamChannel(channelName: String): Int {
        streamChannel.leave(object : ResultCallback<Void?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                notify("Leave stream channel failed:\n" + errorInfo.toString())
            }

            override fun onSuccess(responseInfo: Void?) {
                //isSubscribed = true
                mListener?.onSubscribeUnsubscribe(isSubscribed)
                notify("Left stream channel: $channelName")
            }
        })
        return 0
    }

    fun joinStreamChannel(channelName: String): Int {
        fetchRTCToken(channelName,1, object : TokenCallback {
            override fun onTokenReceived(token: String?) {
                // Use the received token to log in
                if (token != null) {
                    streamChannel = signalingEngine!!.createStreamChannel(channelName)
                    streamChannel?.join(
                        JoinChannelOptions(token, true, true, true),
                        object : ResultCallback<Void?> {
                            override fun onFailure(errorInfo: ErrorInfo?) {
                                notify("Join stream channel failed:\n" + errorInfo.toString())
                            }

                            override fun onSuccess(responseInfo: Void?) {
                                //isSubscribed = true
                                mListener?.onSubscribeUnsubscribe(isSubscribed)
                                notify("Joined stream channel: $channelName")
                            }
                        })
                }
            }

            override fun onError(errorMessage: String) {
                // Handle the error
                notify("Error fetching token: $errorMessage")
            }
        })

        return 0
    }
    
}