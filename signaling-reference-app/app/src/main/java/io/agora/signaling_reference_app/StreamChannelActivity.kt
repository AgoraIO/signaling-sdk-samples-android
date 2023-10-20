package io.agora.signaling_reference_app

import android.os.Bundle
import android.view.View
import android.widget.Button
import io.agora.authentication_manager.AuthenticationManager
import io.agora.rtm.StreamChannel
import io.agora.stream_channel_manager.StreamChannelManager

class StreamChannelActivity : AuthenticationActivity() {
    private lateinit var streamChannelManager: StreamChannelManager
    private lateinit var btnTopic: Button

    override val layoutResourceId: Int
        get() = R.layout.activity_stream_channel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable the user to login with a custom uid
        editUid.isEnabled = true
        btnTopic = findViewById(R.id.btnTopic)
        btnSubscribe.setText("Join Stream channel")
    }

    override fun initializeSignalingManager() {
        // Instantiate an object of the AuthenticationManager class, which extends SignalingManager
        streamChannelManager = StreamChannelManager(this)
        signalingManager = streamChannelManager

        // Set up a listener for updating the UI
        signalingManager.setListener(signalingManagerListener)
    }

    override fun subscribe() {
        channelName = editChannelName.text.toString()
        streamChannelManager.joinStreamChannel(channelName)
    }

    override fun unsubscribe() {
        streamChannelManager.leaveStreamChannel(channelName)
    }

    override fun login() {
        // Read the uid from the UI
        val uid = editUid.text.toString().toInt()
        // Login using the provided uid
        streamChannelManager.loginWithToken(uid)
    }

    fun joinLeaveTopic(view: View) {

    }
}