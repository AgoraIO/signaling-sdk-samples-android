package io.agora.signaling_manager

import io.agora.rtm.*

import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets

open class SignalingManager(context: Context) {
    private val mContext: Context

    protected var signalingEngine: RtmClient? = null // The RTCEngine instance
    protected var mListener: SignalingManagerListener? = null // The event handler for Signaling events
    protected var config: JSONObject? // Configuration parameters from the config.json file
    private val appId: String // Your App ID from Agora console
    var channelName: String // The name of the Signaling channel
    private var channelType = RtmConstants.RtmChannelType.MESSAGE

    var localUid: Int // UID of the local user
    var isLoggedIn = false // Login status
        private set
    var isSubscribed = false // Channel subscription status
        private set

    init {
        mContext = context
        config = readConfig(mContext)
        appId = config!!.optString("appId")
        channelName = config!!.optString("channelName")
        localUid = config!!.optInt("uid") // Default value
    }

    fun setListener(mListener: SignalingManagerListener?) {
        this.mListener = mListener
    }

    private fun readConfig(context: Context): JSONObject? {
        // Read parameters from the config.json file
        try {
            val inputStream = context.resources.openRawResource(R.raw.config)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, StandardCharsets.UTF_8)
            return JSONObject(jsonString)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    protected open val eventListener: RtmEventListener = object : RtmEventListener {
        override fun onMessageEvent(eventArgs: MessageEvent) {
            // Your Message Event handler
            mListener?.onSignalingEvent("Message", eventArgs)
        }

        override fun onPresenceEvent(eventArgs: PresenceEvent) {
            // Your Presence Event handler
            mListener?.onSignalingEvent("Presence", eventArgs)
        }

        override fun onTopicEvent(eventArgs: TopicEvent) {
            // Your Topic Event handler
            mListener?.onSignalingEvent("Topic", eventArgs)
        }

        override fun onLockEvent(eventArgs: LockEvent) {
            // Your Lock Event handler
            mListener?.onSignalingEvent("Lock", eventArgs)
        }

        override fun onStorageEvent(eventArgs: StorageEvent) {
            // Your Storage Event handler
            mListener?.onSignalingEvent("Storage", eventArgs)
        }

        override fun onConnectionStateChanged(
            channelName: String?,
            state: RtmConstants.RtmConnectionState?,
            reason: RtmConstants.RtmConnectionChangeReason?
        ) {
            super.onConnectionStateChanged(channelName, state, reason)
        }

        override fun onTokenPrivilegeWillExpire(channelName: String) {
            // Your Token Privilege Will Expire Event handler
        }
    }

    protected open fun setupSignalingEngine(uid: Int): Boolean {
        try {
            val rtmConfig = RtmConfig.Builder(appId, uid.toString())
                .presenceTimeout(config!!.optString("presenceTimeout").toInt())
                .useStringUserId(false)
                .eventListener(eventListener)
                .build()
            signalingEngine = RtmClient.create(rtmConfig)
            localUid = uid
        } catch (e: Exception) {
            notify(e.toString())
            return false
        }
        return true
    }

    fun login(uid: Int): Int {
        // Use the token from the config file
        val token = config!!.optString("token")
        return login(uid, token)
    }

    fun login(uid: Int, token: String): Int {
        if (signalingEngine ==  null ) {
            setupSignalingEngine(uid)
        }

        signalingEngine?.login(token, object : ResultCallback<Void?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                notify("Login failed:\n"+ errorInfo.toString())// Handle failure
            }

            override fun onSuccess(responseInfo: Void?) {
                localUid = uid
                isLoggedIn = true
                notify("Successfully logged in")
                mListener?.onLoginLogout(isLoggedIn)
            }
        })
        return 0
    }

    fun logout() {
        if (!isLoggedIn) {
            notify("Join a channel first")
        } else {
            // To leave a channel, call the `leaveChannel` method
            signalingEngine?.logout(object: ResultCallback<Void?> {
                override fun onFailure(errorInfo: ErrorInfo?) {
                    notify("Logout failed:\n"+ errorInfo.toString())
                }

                override fun onSuccess(responseInfo: Void?) {
                    isLoggedIn = false
                    if (isSubscribed) {
                        isSubscribed = false
                        mListener?.onSubscribeUnsubscribe(isSubscribed)
                    }
                    notify("Logged out successfully")
                    mListener?.onLoginLogout(isLoggedIn)
                    // Destroy the engine instance
                    destroySignalingEngine()
                }
            })
        }
    }

    fun subscribe(channelName: String): Int {
        // Subscribe to a channel
        val subscribeOptions = SubscribeOptions(true, true, true, true)

        signalingEngine?.subscribe(channelName, subscribeOptions, object: ResultCallback<Void?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                notify("Subscribe failed:\n"+ errorInfo.toString())
            }

            override fun onSuccess(responseInfo: Void?) {
                isSubscribed = true
                mListener?.onSubscribeUnsubscribe(isSubscribed)
                notify("Subscribed to channel: $channelName")
            }
        })
        return 0
    }

    fun unsubscribe(channelName: String): Int {
        signalingEngine?.unsubscribe(channelName, object: ResultCallback<Void?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                notify("Unsubscribe failed:\n"+ errorInfo.toString())
            }

            override fun onSuccess(responseInfo: Void?) {
                isSubscribed = false
                notify("Unsubscribed from channel: $channelName")
                mListener?.onSubscribeUnsubscribe(isSubscribed)
            }
        })
        return 0
    }

    fun publishChannelMessage (message: String): Int {
        val publishOptions = PublishOptions()
        var result = 0

        signalingEngine?.publish(channelName, message, publishOptions, object: ResultCallback<Void?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                notify("Failed to send message:\n"+ errorInfo.toString())
                result =  1
            }

            override fun onSuccess(responseInfo: Void?) {
                notify("Message sent")
            }
        })

        return result
    }

    fun getOnlineUsers () {

        val getOnlineUsersOptions = GetOnlineUsersOptions(true, true)
        signalingEngine?.presence?.getOnlineUsers(channelName, channelType, getOnlineUsersOptions,  object: ResultCallback<GetOnlineUsersResult?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                notify("Failed to obtain user list")
            }

            override fun onSuccess(getOnlineUsersResult: GetOnlineUsersResult?) {
                val list = getOnlineUsersResult?.userStateList
                val userList: List<String> = list?.map { it.userId } ?: emptyList()
                mListener?.onUserListUpdated(userList)
            }
        })
    }

    protected open fun destroySignalingEngine() {
        // Release the SignalingEngine instance to free up resources
        signalingEngine = null
        RtmClient.release()
    }

    interface SignalingManagerListener {
        fun onMessageReceived(message: String?)
        fun onSignalingEvent(eventType: String, eventArgs: Any)
        fun onSubscribeUnsubscribe(subscribed: Boolean)
        fun onLoginLogout(loggedIn: Boolean)
        fun onUserListUpdated(userList: List<String>)
    }

    protected fun notify(message: String?) {
        // Sends notification message to the Ui
        mListener?.onMessageReceived(message)
    }

}