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
        // Instantiate an object of the AuthenticationManager class, which is an extension of the AgoraManager
        authenticationManager = AuthenticationManager(this)
        signalingManager = authenticationManager

        // Set up a listener for updating the UI
        signalingManager.setListener(signalingManagerListener)
    }

    override fun login() {
        val uid = editUid.text.toString().toInt()
        authenticationManager.loginWithToken(uid)
    }
}