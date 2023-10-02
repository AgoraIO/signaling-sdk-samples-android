package io.agora.signaling_reference_app

import io.agora.signaling_manager.SignalingManager

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import io.agora.rtm.MessageEvent
import io.agora.rtm.PresenceEvent
import io.agora.rtm.RtmConstants
import org.json.JSONObject

open class BasicImplementationActivity : AppCompatActivity() {
    protected lateinit var signalingManager: SignalingManager
    private lateinit var btnSubscribe: Button
    private lateinit var btnLogin: Button
    private lateinit var editChannelName: EditText
    protected lateinit var editUid: EditText
    private lateinit var editMessage: EditText
    private lateinit var userListLayout: LinearLayout
    private val userIconsMap = mutableMapOf<String, View>()
    var channelName = ""
    
    // The overridable UI layout for this activity
    protected open val layoutResourceId: Int
        get() = R.layout.base_layout // Default layout resource ID for base activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)

        // Create an instance of the SignalingManager class
        initializeSignalingManager()

        btnLogin = findViewById(R.id.btnLogin)
        btnSubscribe = findViewById(R.id.btnSubscribe)
        btnSubscribe.isEnabled = false

        editChannelName = findViewById(R.id.editChannelName)
        editChannelName.setText(signalingManager.channelName)

        editUid = findViewById(R.id.editUid)
        editUid.setText(signalingManager.localUid.toString())

        editMessage = findViewById(R.id.editMessage)
        userListLayout = findViewById<LinearLayout>(R.id.userList)
    }

    protected open fun initializeSignalingManager() {
        signalingManager = SignalingManager(this)

        // Set up a listener for updating the UI
        signalingManager.setListener(signalingManagerListener)
    }

    fun publishMessage(view: View) {
        val message = editMessage.text.toString()
        val result = signalingManager.publishChannelMessage(message)
        if (result == 0) {
            displaySentMessage(message)
            editMessage.setText("")
        }
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

    open fun displaySentMessage(messageText: String) {
        // Create a new TextView
        val messageTextView = TextView(this)
        messageTextView.text = messageText
        messageTextView.setPadding(10, 10, 10, 10)
        val messageList = findViewById<LinearLayout>(R.id.messageList)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT
        )

        params.gravity = Gravity.END
        messageTextView.setBackgroundColor(Color.parseColor("#DCF8C6"))
        params.setMargins(100, 25, 15, 5)

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
        if (signalingManager.isLoggedIn) {
            signalingManager.logout()
        }
        onBackPressedDispatcher.onBackPressed()
    }

    fun displayReceivedMessage(publisher: String, message: String) {
        // Create a SpannableStringBuilder for the header
        val header = "from: $publisher"
        val headerSpannable = SpannableStringBuilder(header)
        headerSpannable.setSpan(RelativeSizeSpan(0.8f), 0, header.length, 0)
        headerSpannable.setSpan(ForegroundColorSpan(getColor(R.color.agora_blue)), 0, header.length, 0)

        // Create a SpannableString for the body
        val bodySpannable = SpannableString(message)

        // Set the text in the TextView
        val formattedText = SpannableStringBuilder().append(headerSpannable).append("\n").append(bodySpannable)

        // Create a new TextView
        val messageTextView = TextView(this)
        messageTextView.text = formattedText
        messageTextView.setPadding(10, 10, 10, 10)
        val messageList = findViewById<LinearLayout>(R.id.messageList)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT
        )

        messageTextView.setBackgroundColor(Color.parseColor("white"))
        params.setMargins(15, 25, 100, 5)

        // Add the textview to the linearlayout
        runOnUiThread {
            messageList.addView(messageTextView, params)
        }
    }

    fun updateUserList(userList: List<String>?) {
        val iconsToRemove = mutableListOf<String>()

        // Iterate over the existing user icons in the map
        for ((userId, userIconView) in userIconsMap) {
            // Check if the user is not in the updated user list
            if (userList == null || userId !in userList) {
                // Mark this user icon for removal
                iconsToRemove.add(userId)
                runOnUiThread {
                    // Remove the user icon from the UI
                    userListLayout.removeView(userIconView)
                }
            }
        }

        // Remove the user icons that need to be removed
        for (userIdToRemove in iconsToRemove) {
            userIconsMap.remove(userIdToRemove)
        }

        // Iterate over the updated user list and add new user icons
        userList?.forEach { item ->
            if (!userIconsMap.containsKey(item)) {
                val userIconView = createUserIcon(item)
                userIconsMap[item] = userIconView
                runOnUiThread {
                    userListLayout.addView(userIconView)
                }
            }
        }
    }

    private fun createUserIcon(userId: String): View {
        val userIconView = LayoutInflater.from(this).inflate(
            R.layout.user_icon_layout, userListLayout, false)

        val userIdTextView = userIconView.findViewById<TextView>(R.id.userIcon)
        userIdTextView.text = userId // Set the user ID

        // Set the layout parameters
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 20, 0)  // Adjust the right margin to add spacing
        userIdTextView.layoutParams = params

        // Add view to the map
        userIconsMap[userId] = userIconView
        return userIconView
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
                        val jsonObject = JSONObject(messageEventArgs.message.data.toString())
                        val message: String = jsonObject.optString("message", "")
                        displayReceivedMessage(eventArgs.publisherId, message)
                        //displayMessage(message,false)
                    }
                    "Presence" -> {
                        val presenceEventArgs = eventArgs as PresenceEvent
                        when (presenceEventArgs.eventType) {
                            RtmConstants.RtmPresenceEventType.REMOTE_JOIN,
                            RtmConstants.RtmPresenceEventType.REMOTE_LEAVE,
                            RtmConstants.RtmPresenceEventType.SNAPSHOT ->{
                                signalingManager.getOnlineUsers()
                            }
                        else ->{

                            }
                        }

                    }
                    "Topic" -> {

                    }
                    "Lock" -> {

                    }
                    "Storage" -> {

                    }

                }
            }

            override fun onSubscribeUnsubscribe(subscribed: Boolean) {
                runOnUiThread {
                    if (subscribed) {
                        btnSubscribe.setText(R.string.unsubscribe)
                    } else {
                        btnSubscribe.setText(R.string.subscribe)
                        userListLayout.removeAllViews()
                        userIconsMap.clear()
                    }
                }
            }

            override fun onLoginLogout(loggedIn: Boolean) {
                runOnUiThread {
                    btnSubscribe.isEnabled = loggedIn

                    if (loggedIn) {
                        btnLogin.setText(R.string.logout)
                    } else {
                        btnLogin.setText(R.string.login)
                    }
                }
            }

            override fun onUserListUpdated(userList: List<String>) {
                updateUserList(userList)
            }
        }
}