package io.agora.signaling_reference_app

import android.os.Bundle
import io.agora.authentication_manager.AuthenticationManager
import io.agora.rtm.StreamChannel
import io.agora.stream_channel_manager.StreamChannelManager

class StreamChannelActivity : BasicImplementationActivity() {
    private lateinit var streamChannelManager: StreamChannelManager

    override val layoutResourceId: Int
        get() = R.layout.activity_stream_channel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable the user to login with a custom uid
        editUid.isEnabled = true
    }

    override fun initializeSignalingManager() {
        // Instantiate an object of the AuthenticationManager class, which extends SignalingManager
        streamChannelManager = StreamChannelManager(this)
        signalingManager = streamChannelManager

        // Set up a listener for updating the UI
        signalingManager.setListener(signalingManagerListener)
    }

    override fun login() {
        // Read the uid from the UI
        val uid = editUid.text.toString().toInt()
        // Login using the provided uid
        streamChannelManager.loginWithToken(uid)
    }
}