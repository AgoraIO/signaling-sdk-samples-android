package io.agora.signaling_reference_app

//import io.agora.agora_manager.AgoraManager
//import io.agora.agora_manager.AgoraManager.AgoraManagerListener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

open class BasicImplementationActivity : AppCompatActivity() {
    //protected lateinit var agoraManager: AgoraManager
    protected lateinit var btnJoinLeave: Button
    protected lateinit var mainFrame: FrameLayout
    protected lateinit var containerLayout: LinearLayout
    protected lateinit var radioGroup: RadioGroup
    protected lateinit var videoFrameMap: MutableMap<Int, FrameLayout?>

    // The overridable UI layout for this activity
    protected open val layoutResourceId: Int
        get() = R.layout.activity_basic_implementation // Default layout resource ID for base activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)

        // Set up access to the UI elements
        btnJoinLeave = findViewById(R.id.btnJoinLeave) // The join/leave button
        mainFrame = findViewById(R.id.main_video_container) // The main video frame
        containerLayout = findViewById(R.id.containerLayout) // The multi-video container layout

        // Create an instance of the AgoraManager class
        initializeAgoraManager()
    }

    protected open fun initializeAgoraManager() {
      //  agoraManager = AgoraManager(this)

        // Set up a listener for updating the UI
      //  agoraManager.setListener(agoraManagerListener)
    }

    protected open fun join() {
        // Join a channel
//        agoraManager.joinChannel()
    }


    protected open fun leave() {
        // Leave the channel
  //      agoraManager.leaveChannel()
        // Update the UI
        btnJoinLeave.text = getString(R.string.join)
        if (radioGroup.visibility != View.GONE) radioGroup.visibility = View.VISIBLE

        // Clear the video containers
        containerLayout.removeAllViews()
        mainFrame.removeAllViews()
        videoFrameMap.clear()
    }

    fun joinLeave(view: View) {
        // Join/Leave button clicked
    /*    if (!agoraManager.isJoined) {
            join()
        } else {
            leave()
        }

     */
    }

    protected fun showMessage(message: String?) {
        runOnUiThread { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        /* if (agoraManager.isJoined) {
            leave()
        }

         */
        onBackPressedDispatcher.onBackPressed()
    }

}