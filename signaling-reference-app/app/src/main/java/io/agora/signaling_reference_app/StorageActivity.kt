package io.agora.signaling_reference_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import io.agora.rtm.RtmConstants
import io.agora.rtm.StorageEvent
import io.agora.storage_manager.StorageManager

class StorageActivity : AuthenticationActivity() {
    private lateinit var storageManager: StorageManager
    private lateinit var editUserMetadata: EditText
    private lateinit var editKey: EditText
    private lateinit var editValue: EditText
    private lateinit var editRevision: EditText
    private lateinit var editLock: EditText
    private lateinit var editLockName: EditText

    override val layoutResourceId: Int
        get() = R.layout.activity_storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editUserMetadata = findViewById(R.id.editUserMetadata)
        editKey = findViewById(R.id.editKey)
        editValue = findViewById(R.id.editValue)
        editRevision = findViewById(R.id.editRevision)
        editLock = findViewById(R.id.editLock)
        editLockName = findViewById(R.id.editLockName)
    }

    override fun initializeSignalingManager() {
        // Instantiate an object of the AuthenticationManager class, which extends SignalingManager
        storageManager = StorageManager(this)
        signalingManager = storageManager

        // Set up a listener for updating the UI
        signalingManager.setListener(signalingManagerListener)
    }

    override fun login() {
        // Read the uid from the UI
        val uid = editUid.text.toString().toInt()
        // Login using the provided uid
        storageManager.loginWithToken(uid)
    }

    override fun onUserIconClick(userIconView: View) {
        val userIdTextView = userIconView.findViewById<TextView>(R.id.userIcon)
        val uid = userIdTextView.text.toString().toInt()
        // Get the selected user's metadata
        storageManager.getUserMetadata(uid)
    }

    override fun createUserIcon(userId: String): View {
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

        // Subscribe to the user's metadata to receive updates
        storageManager.subscribeUserMetadata(userId.toInt())

        // Add an onclick listener
        userIconView.setOnClickListener{
            onUserIconClick(userIconView)
        }

        // Add view to the map
        userIconsMap[userId] = userIconView

        return userIconView
    }

    fun onUserMetadataUpdate(view: View) {
        val value = editUserMetadata.text.toString()
        storageManager.setUserMetadata(storageManager.localUid,"userBio", value)
        editUserMetadata.setText("")
    }

    fun onChannelMetadataUpdate(view: View) {
        val key = editKey.text.toString()
        if (key.isEmpty()) return

        val value = editValue.text.toString()

        val revision: Long = try {
            editRevision.text.toString().toLong()
        } catch (e: NumberFormatException) {
            -1L
        }

        val lock = editLock.text.toString()

        storageManager.setChannelMetadata(key, value, revision, lock)

    }

    private fun showChannelMetadata(data: io.agora.rtm.Metadata) {
        var summary = "Channel metadata\n"
        val items = data.items
        if (items != null) {
            for (item in items) {
                val row = "${item.key}: ${item.value}, Rev: ${item.revision}\n"
                Log.i("ChannelMetadata", row)
                summary += row
            }
        }
        showMessage(summary)
    }

    override fun onStorageEvent(storageEventArgs: StorageEvent) {
        when (storageEventArgs.storageType) {
            RtmConstants.RtmStorageType.CHANNEL -> {
                // channel metadata was updated
                showChannelMetadata(storageEventArgs.data)
            }
            RtmConstants.RtmStorageType.USER -> {
                // user metadata was updated
                showMessage(
                    "Metadata event " +
                            storageEventArgs.eventType +
                            ", User: " +
                            storageEventArgs.target
                )
                //showUserMetadata(eventArgs.publisher, eventArgs.data.metadata);
            }
            else -> {
                showMessage("Storage event: ${storageEventArgs.eventType}")
            }
        }
    }

    fun onSetLock(view: View) {
        val lockName = editLockName.text.toString()
        storageManager.setLock(lockName, 60)
    }

    fun onGetLock(view: View) {
        storageManager.getLocks()
    }

    fun onReleaseLock(view: View) {
        val lockName = editLockName.text.toString()
        storageManager.releaseLock(lockName)
    }

    fun onAcquireLock(view: View) {
        val lockName = editLockName.text.toString()
        storageManager.acquireLock(lockName, false)
    }

    fun onRemoveLock(view: View) {
        val lockName = editLockName.text.toString()
        storageManager.removeLock(lockName)
    }

    override fun handleSubscribeUnsubscribe(subscribed: Boolean) {
        super.handleSubscribeUnsubscribe(subscribed)
        if (subscribed) {
            storageManager.updateUserMetadata(storageManager.localUid, "userBio", "This is my default bio.")
        }
    }
}