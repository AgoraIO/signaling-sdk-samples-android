package io.agora.signaling_reference_app

import android.os.Bundle
import io.agora.authentication_manager.AuthenticationManager

class AuthenticationActivity : BasicImplementationActivity() {
    private lateinit var authenticationManager: AuthenticationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable the user to login with a custom uid
        editUid.isEnabled = true
    }

    override fun initializeSignalingManager() {
        // Instantiate an object of the AuthenticationManager class, which extends SignalingManager
        authenticationManager = AuthenticationManager(this)
        signalingManager = authenticationManager

        // Set up a listener for updating the UI
        signalingManager.setListener(signalingManagerListener)
    }

    override fun login() {
        // Read the uid from the UI
        val uid = editUid.text.toString().toInt()
        // Login using the provided uid
        authenticationManager.loginWithToken(uid)
    }
}