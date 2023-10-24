package io.agora.signaling_reference_app

import android.os.Bundle
import io.agora.storage_manager.StorageManager

class StorageActivity : AuthenticationActivity() {
    private lateinit var storageManager: StorageManager

    override val layoutResourceId: Int
        get() = R.layout.activity_storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

}