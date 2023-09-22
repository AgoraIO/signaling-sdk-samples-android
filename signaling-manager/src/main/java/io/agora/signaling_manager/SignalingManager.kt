package io.agora.signaling_manager

import io.agora.rtm.*

import android.Manifest
import android.app.Activity
import android.content.Context
import org.json.JSONObject
import org.json.JSONException
import android.view.SurfaceView
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.util.HashSet

open class SignalingManager(context: Context) {
    // The reference to the Android activity you use for video calling
    private val activity: Activity
    protected val mContext: Context

    protected var signalingEngine: io.agora.rtm.RtmClient? = null // The RTCEngine instance
    protected var mListener: AgoraManagerListener? = null // The event handler for AgoraEngine events
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

    fun setListener(mListener: AgoraManagerListener?) {
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

    protected open fun setupSignalingEngine(): Boolean {
        try {

        } catch (e: Exception) {
            sendMessage(e.toString())
            return false
        }
        return true
    }

    fun joinChannel(): Int {
        // Use channelName and token from the config file
        val token = config!!.optString("rtcToken")
        //return joinChannel(channelName, token)
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

    fun leaveChannel() {
        if (!isJoined) {
            sendMessage("Join a channel first")
        } else {
            // To leave a channel, call the `leaveChannel` method
            //agoraEngine!!.leaveChannel()
            sendMessage("You left the channel")

            // Set the `joined` status to false
            isJoined = false
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

    interface AgoraManagerListener {
        fun onMessageReceived(message: String?)
        fun onRemoteUserJoined(remoteUid: Int, surfaceView: SurfaceView?)
        fun onRemoteUserLeft(remoteUid: Int)
        fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int)
    }

    protected fun sendMessage(message: String?) {
        mListener!!.onMessageReceived(message)
    }

}