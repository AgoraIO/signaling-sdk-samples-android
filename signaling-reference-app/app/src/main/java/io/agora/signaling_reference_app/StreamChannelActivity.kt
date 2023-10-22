package io.agora.signaling_reference_app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import io.agora.stream_channel_manager.StreamChannelManager

class StreamChannelActivity : AuthenticationActivity() {
    private lateinit var streamChannelManager: StreamChannelManager
    private lateinit var btnTopic: Button
    private lateinit var editTopicName: EditText

    override val layoutResourceId: Int
        get() = R.layout.activity_stream_channel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable the user to login with a custom uid
        editUid.isEnabled = true
        btnTopic = findViewById(R.id.btnTopic)
        btnSubscribe.setText(R.string.join_stream_channel)
        editTopicName = findViewById(R.id.editTopicName)
    }

    override fun subscribeUnsubscribe(view: View) {
        // Subscribe/Unsubscribe button clicked
        if (!streamChannelManager.isStreamChannelJoined) {
            subscribe()
        } else {
            unsubscribe()
        }
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
        btnTopic.setText(R.string.join_topic)
    }

    override fun handleSubscribeUnsubscribe(subscribed: Boolean) {
        runOnUiThread {
            if (subscribed) {
                btnSubscribe.setText(R.string.leave_stream_channel)
                btnTopic.isEnabled = true
            } else {
                btnSubscribe.setText(R.string.join_stream_channel)
                btnTopic.isEnabled = false
                btnTopic.setText(R.string.join_topic)
                userListLayout.removeAllViews()
                userIconsMap.clear()
            }
        }
    }

    override fun login() {
        // Read the uid from the UI
        val uid = editUid.text.toString().toInt()
        // Login using the provided uid
        streamChannelManager.loginWithToken(uid)
    }

    override fun publishMessage(message: String) {
        val result = streamChannelManager.publishTopicMessage(streamChannelManager.joinedTopicName, message)
        if (result == 0) {
            displaySentMessage(message)
            editMessage.setText("")
        }
    }

    fun joinLeaveTopic(view: View) {

        if (!streamChannelManager.isTopicJoined) {
            streamChannelManager.joinTopic(editTopicName.text.toString())
            btnTopic.setText(R.string.leave_topic)
        } else {
            streamChannelManager.leaveTopic(streamChannelManager.joinedTopicName)
            btnTopic.setText(R.string.join_topic)
        }
    }
}