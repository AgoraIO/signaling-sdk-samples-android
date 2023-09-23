package io.agora.signaling_manager

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.SurfaceView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.rtm.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

open class SignalingManager(context: Context) {
    // The reference to the Android activity you use for video calling
    private val activity: Activity
    protected val mContext: Context

    protected var signalingEngine: RtmClient? = null // The RTCEngine instance
    protected var mListener: SignalingManagerListener? = null // The event handler for Signaling events
    protected var config: JSONObject? // Configuration parameters from the config.json file
    protected val appId: String // Your App ID from Agora console
    var channelName: String // The name of the channel to join
    var localUid: Int // UID of the local user
    var remoteUids = HashSet<Int>() // An object to store uids of remote users
    var isJoined = false // Status of the video call
        private set
    
    init {
        config = readConfig(context)
        appId = config!!.optString("appId")
        channelName = config!!.optString("channelName")
        localUid = config!!.optInt("uid")
        mContext = context
        activity = mContext as Activity
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(activity, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID)
        }
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

    private val eventListener: RtmEventListener = object : RtmEventListener {
        override fun onMessageEvent(event: MessageEvent) {
            // Your Message Event handler
        }

        override fun onPresenceEvent(event: PresenceEvent) {
            // Your Presence Event handler
        }

        override fun onTopicEvent(event: TopicEvent) {
            // Your Topic Event handler
        }

        override fun onLockEvent(event: LockEvent) {
            // Your Lock Event handler
        }

        override fun onStorageEvent(event: StorageEvent) {
            // Your Storage Event handler
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

    protected open fun setupSignalingEngine(): Boolean {
        try {
            val rtmConfig = RtmConfig.Builder(appId, localUid.toString())
                .presenceTimeout(300)
                .useStringUserId(false)
                .eventListener(eventListener)
                .build()

            signalingEngine = RtmClient.create(rtmConfig)

            try {

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            sendMessage(e.toString())
            return false
        }
        return true
    }

    fun login(): Int {
        if (signalingEngine ==  null ) {
            setupSignalingEngine()
        }
        // Use channelName and token from the config file
        val token = config!!.optString("token")

        signalingEngine?.login(token, object : ResultCallback<Void?> {
            override fun onFailure(errorInfo: ErrorInfo?) {
                sendMessage("login failed:\n"+ errorInfo.toString())// Handle failure
            }

            override fun onSuccess(responseInfo: Void?) {
                isJoined = true
                sendMessage("login success")
            }
        })
        return 0
    }

    fun joinChannel(channelName: String, token: String?): Int {
        // Ensure that necessary Android permissions have been granted
        if (!checkSelfPermission()) {
            sendMessage("Permissions were not granted")
            return -1
        }

        return 0
    }

    fun logout() {
        if (!isJoined) {
            sendMessage("Join a channel first")
        } else {
            // To leave a channel, call the `leaveChannel` method
            signalingEngine?.logout(object: ResultCallback<Void?> {
                override fun onFailure(errorInfo: ErrorInfo?) {
                    sendMessage("logout failed:\n"+ errorInfo.toString())
                }

                override fun onSuccess(responseInfo: Void?) {
                    isJoined = false
                    sendMessage("logout success")
                }
            })

            // Destroy the engine instance
            destroySignalingEngine()
        }
    }

    protected fun destroySignalingEngine() {
        // Release the RtcEngine instance to free up resources
        //RtcEngine.destroy()
       // signalingEngine = null
    }

    private fun checkSelfPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            mContext,
            REQUESTED_PERMISSIONS[0]
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    mContext,
                    REQUESTED_PERMISSIONS[1]
                ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        protected const val PERMISSION_REQ_ID = 22
        protected val REQUESTED_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
    }

    interface SignalingManagerListener {
        fun onMessageReceived(message: String?)
        fun onSignalingEvent(eventType: String, eventArgs: Map<String, Any>)
    }

    protected fun sendMessage(message: String?) {
        mListener!!.onMessageReceived(message)
    }

}