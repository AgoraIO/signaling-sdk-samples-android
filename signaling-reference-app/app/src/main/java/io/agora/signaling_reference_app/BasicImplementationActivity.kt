package io.agora.signaling_reference_app

//import io.agora.agora_manager.AgoraManager
//import io.agora.agora_manager.AgoraManager.AgoraManagerListener

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior


open class BasicImplementationActivity : AppCompatActivity() {
    //protected lateinit var agoraManager: AgoraManager
    protected lateinit var btnJoinLeave: Button
    protected  lateinit var  bottom_sheet_view: LinearLayout


    // The overridable UI layout for this activity
    protected open val layoutResourceId: Int
        get() = R.layout.activity_basic_implementation // Default layout resource ID for base activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)

        // Set up access to the UI elements
//        btnJoinLeave = findViewById(R.id.btn_join_leave) // The join/leave button
        bottom_sheet_view = findViewById(R.id.bottom_sheet_view)

        // Set a click listener on the button to show the bottom sheet
        val bottomSheetButton: Button = findViewById(R.id.bottom_sheet_button)

        // Associate the BottomSheetBehavior with the bottom sheet view
        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_view)

        bottomSheetButton.setOnClickListener {
            // Set the state of the bottom sheet (e.g., STATE_COLLAPSED, STATE_EXPANDED)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        // Create an instance of the AgoraManager class
        initializeAgoraManager()
    }


    private fun showBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_view)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
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
        messageList.addView(messageTextView, params)
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