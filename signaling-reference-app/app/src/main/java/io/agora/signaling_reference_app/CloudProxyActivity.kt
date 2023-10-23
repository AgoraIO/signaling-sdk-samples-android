package io.agora.signaling_reference_app

import android.os.Bundle
import io.agora.cloud_proxy_manager.CloudProxyManager

class CloudProxyActivity : AuthenticationActivity() {
    private lateinit var cloudProxyManager: CloudProxyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable the user to login with a custom uid
        editUid.isEnabled = true
    }

    override fun initializeSignalingManager() {
        // Instantiate an object of the AuthenticationManager class, which extends SignalingManager
        cloudProxyManager = CloudProxyManager(this)
        signalingManager = cloudProxyManager

        // Set up a listener for updating the UI
        signalingManager.setListener(signalingManagerListener)
    }

    override fun login() {
        // Read the uid from the UI
        val uid = editUid.text.toString().toInt()
        // Login using the provided uid
        cloudProxyManager.loginWithToken(uid)
    }
}