package io.agora.stream_channel_manager

import android.content.Context
import io.agora.authentication_manager.AuthenticationManager
import io.agora.rtm.*

open class StreamChannelManager(context: Context?) : AuthenticationManager(context!!) {
    private lateinit var streamChannel: StreamChannel
    var isStreamChannelJoined = false
    var isTopicJoined = false
    var joinedTopicName: String = ""

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
                                isStreamChannelJoined = false
                            }

                            override fun onSuccess(responseInfo: Void?) {
                                isStreamChannelJoined = true
                                mListener?.onSubscribeUnsubscribe(isStreamChannelJoined)
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

    fun leaveStreamChannel(channelName: String): Int {
        streamChannel.leave(object : ResultCallback<Void?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                notify("Leave stream channel failed:\n" + errorInfo.toString())
            }

            override fun onSuccess(responseInfo: Void?) {
                isStreamChannelJoined = false
                mListener?.onSubscribeUnsubscribe(isStreamChannelJoined)
                notify("Left stream channel: $channelName")
            }
        })
        return 0
    }

    fun joinTopic (topicName: String) {
        streamChannel.joinTopic(topicName, JoinTopicOptions(), object : ResultCallback<Void?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                notify("Failed to join topic: $topicName")
            }

            override fun onSuccess(responseInfo: Void?) {
                notify("Successfully joined topic: $topicName")
                joinedTopicName = topicName
                isTopicJoined = true
                subscribeTopic(topicName)
            }
        })
    }

    fun subscribeTopic(topicName: String) {
        streamChannel.subscribeTopic(topicName,TopicOptions(), object : ResultCallback<SubscribeTopicResult?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                notify("Failed to subscribed topic: $topicName")
            }

            override fun onSuccess(responseInfo: SubscribeTopicResult?) {
                notify("Subscribed to topic: $topicName")
            }
        })
    }

    fun leaveTopic (topicName: String) {
        streamChannel.leaveTopic(topicName, object : ResultCallback<Void?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                notify("Failed to leave topic: $topicName")
            }

            override fun onSuccess(responseInfo: Void?) {
                notify("Successfully left topic: $topicName")
                joinedTopicName = ""
                isTopicJoined = false
            }
        })
    }

    fun publishTopicMessage(topicName: String, message: String): Int {
        streamChannel.publishTopicMessage(topicName, message, PublishOptions(), object : ResultCallback<Void?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                notify("Message send failed")
            }

            override fun onSuccess(responseInfo: Void?) {
                notify("Message sent to stream channel")
            }
        })
        return 0
    }
    
}