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
                    streamChannel.join(
                        JoinChannelOptions(token, true, true, true),
                        object : ResultCallback<Void?> {
                            override fun onFailure(errorInfo: ErrorInfo?) {
                                notify("Join stream channel failed:\n" + errorInfo.toString())
                                isStreamChannelJoined = false
                            }

                            override fun onSuccess(responseInfo: Void?) {
                                isStreamChannelJoined = true
                                mListener?.onSubscribeUnsubscribe(true)
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
                isTopicJoined =false
                joinedTopicName = ""
                mListener?.onSubscribeUnsubscribe(false)
                notify("Left stream channel: $channelName")
            }
        })
        return 0
    }

    fun joinTopic (topicName: String) {
        streamChannel.joinTopic(topicName, JoinTopicOptions(), object : ResultCallback<Void?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                notify("Failed to join topic: $topicName")
                isTopicJoined = false
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

        streamChannel.subscribeTopic(topicName,  TopicOptions(), object : ResultCallback<SubscribeTopicResult?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                notify("Failed to subscribe to topic: $topicName")
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
                notify("Message sent to stream channel: $channelName, topic: $topicName")
            }
        })
        return 0
    }

    override fun logout() {
        if (!isLoggedIn) {
            notify("You need to login first")
        } else {
            // Leave the joined topic
            if (isTopicJoined) leaveTopic(joinedTopicName)
            // Leave the channel
            if (isStreamChannelJoined) leaveStreamChannel(channelName)
            // logout
            signalingEngine?.logout(object: ResultCallback<Void?> {
                override fun onFailure(errorInfo: ErrorInfo?) {
                    notify("Logout failed:\n"+ errorInfo.toString())
                }

                override fun onSuccess(responseInfo: Void?) {
                    isLoggedIn = false
                    notify("Logged out successfully")
                    mListener?.onLoginLogout(isLoggedIn)
                    // Destroy the engine instance
                    destroySignalingEngine()
                }
            })
        }
    }

    // Extend the eventListener from the base class
    override val eventListener: RtmEventListener
        get() = object : RtmEventListener {
            // Listen for the event that the token is about to expire
            override fun onTokenPrivilegeWillExpire(token: String) {
                handleTokenExpiry()
                super.onTokenPrivilegeWillExpire(token)
            }

            // Reuse events handlers from the base class
            override fun onMessageEvent(eventArgs: MessageEvent) {
                baseEventHandler?.onMessageEvent(eventArgs)
            }

            override fun onPresenceEvent(eventArgs: PresenceEvent) {
                baseEventHandler?.onPresenceEvent(eventArgs)
            }

            override fun onTopicEvent(eventArgs: TopicEvent) {
                notify("Topic event: ${eventArgs.type}")
                baseEventHandler?.onTopicEvent(eventArgs)
            }

            override fun onLockEvent(eventArgs: LockEvent) {
                baseEventHandler?.onLockEvent(eventArgs)
            }

            override fun onStorageEvent(eventArgs: StorageEvent) {
                baseEventHandler?.onStorageEvent(eventArgs)
            }

            override fun onConnectionStateChanged(
                channelName: String?,
                state: RtmConstants.RtmConnectionState?,
                reason: RtmConstants.RtmConnectionChangeReason?
            ) {
                baseEventHandler?.onConnectionStateChanged(channelName, state, reason)
            }
        }
}