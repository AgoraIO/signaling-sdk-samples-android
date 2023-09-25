package io.agora.signaling_reference_app

import io.agora.signaling_manager.SignalingManager
//import io.agora.agora_manager.AgoraManager.AgoraManagerListener

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import io.agora.rtm.MessageEvent

open class BasicImplementationActivity : AppCompatActivity() {
    protected lateinit var signalingManager: SignalingManager
    protected lateinit var btnJoinLeave: Button
    var channelName = ""
    lateinit var editChannelName: EditText
    lateinit var editUid: EditText
    lateinit var editMessage: EditText
    
    // The overridable UI layout for this activity
    protected open val layoutResourceId: Int
        get() = R.layout.base_layout // Default layout resource ID for base activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)

        // Create an instance of the SignalingManager class
        initializeSignalingManager()

        editChannelName = findViewById(R.id.editChannelName)
        editChannelName.setText(signalingManager.channelName)

        editUid = findViewById(R.id.editUid)
        editUid.setText(signalingManager.localUid.toString())

        editMessage = findViewById(R.id.editMessage)
    }

    protected open fun initializeSignalingManager() {
        signalingManager = SignalingManager(this)

        // Set up a listener for updating the UI
        signalingManager.setListener(signalingManagerListener)
    }

    fun publishMessage(view: View) {
        val message = editMessage.text.toString()
        signalingManager.publishChannelMessage(message)
        displayMessage(message, true)
    }

    protected open fun login() {
        val uid = editUid.text.toString().toInt()
        signalingManager.login(uid)
    }

    protected open fun logout() {
        signalingManager.logout()
    }

    protected open fun subscribe() {
        // Subscribe to a channel
        channelName = editChannelName.text.toString()
        signalingManager.subscribe(channelName)
    }


    protected open fun unsubscribe() {
        // Unsubscribe from the channel
        signalingManager.unsubscribe(channelName)
        signalingManager.logout()
        // Update the UI
        btnJoinLeave.text = getString(R.string.subscribe)
    }

    fun loginLogout(view: View) {
        // Subscribe/Unsubscribe button clicked
        if (!signalingManager.isLoggedIn) {
            login()
        } else {
            logout()
        }
    }

    fun subscribeUnsubscribe(view: View) {
        // Subscribe/Unsubscribe button clicked
         if (!signalingManager.isSubscribed) {
            subscribe()
        } else {
            unsubscribe()
        }
    }

    open fun displayMessage(messageText: String?, isSentMessage: Boolean) {
        // Create a new TextView
        val messageTextView = TextView(this)
        messageTextView.text = messageText
        messageTextView.setPadding(10, 10, 10, 10)
        val messageList = findViewById<LinearLayout>(R.id.messageList)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        if (isSentMessage) {
            params.gravity = Gravity.END
            messageTextView.setBackgroundColor(Color.parseColor("#DCF8C6"))
            params.setMargins(100, 25, 15, 5)
        } else {
            messageTextView.setBackgroundColor(Color.parseColor("white"))
            params.setMargins(15, 25, 100, 5)
        }
        // Add the textview to the linearlayout
        runOnUiThread {
            messageList.addView(messageTextView, params)
        }
    }

    protected fun showMessage(message: String?) {
        runOnUiThread { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() }
    }


    @Deprecated("Deprecated in Java", ReplaceWith("onBackPressedDispatcher.onBackPressed()"))
    override fun onBackPressed() {
        /* if (agoraManager.isJoined) {
            leave()
        } */
        onBackPressedDispatcher.onBackPressed()
    }

    protected val signalingManagerListener: SignalingManager.SignalingManagerListener
        get() = object : SignalingManager.SignalingManagerListener {
            override fun onMessageReceived(message: String?) {
                showMessage(message)
            }

            override fun onSignalingEvent(eventType: String, eventArgs: Any) {
                when (eventType) {
                    "Message" -> {
                        val messageEventArgs = eventArgs as MessageEvent
                        displayMessage(messageEventArgs.message.toString(),false)
                    }
                    "Presence" -> {

                    }
                    "Topic" -> {

                    }
                    "Lock" -> {

                    }
                    "Storage" -> {

                    }

                }
            }
        }
}