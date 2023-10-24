package io.agora.signaling_reference_app

import android.os.Bundle
import io.agora.geofencing_manager.GeofencingManager

class GeofencingActivity : AuthenticationActivity() {
    private lateinit var geofencingManager: GeofencingManager

    override fun initializeSignalingManager() {
        // Instantiate an object of the AuthenticationManager class, which extends SignalingManager
        geofencingManager = GeofencingManager(this)
        signalingManager = geofencingManager

        // Set up a listener for updating the UI
        signalingManager.setListener(signalingManagerListener)
    }

    override fun login() {
        // Read the uid from the UI
        val uid = editUid.text.toString().toInt()
        // Login using the provided uid
        geofencingManager.loginWithToken(uid)
    }
}