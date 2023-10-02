package io.agora.authentication_manager

import okhttp3.*
import okhttp3.Request.*
import org.json.JSONObject
import org.json.JSONException
import android.content.Context
import io.agora.rtm.RtmEventListener
import io.agora.signaling_manager.SignalingManager
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

open class AuthenticationManager(context: Context?) : SignalingManager(
    context!!
) {
    var serverUrl : String // The base URL to your token server
    private val tokenExpiryTime : Int // Time in seconds after which the token will expire.
    private val baseEventHandler: RtmEventListener? // To extend the event handler from the base class

    // Callback interface to receive the http response from an async token request
    interface TokenCallback {
        fun onTokenReceived(token: String?)
        fun onError(errorMessage: String)
    }

    init {
        // Read the server url and expiry time from the config file
        serverUrl = config!!.optString("serverUrl")
        tokenExpiryTime = config!!.optInt("tokenExpiryTime", 600)
        baseEventHandler = super.eventListener
    }

    // Listen for the event that a token is about to expire
    override val eventListener: RtmEventListener
        get() = object : RtmEventListener {
            // Listen for the event that the token is about to expire
            override fun onTokenPrivilegeWillExpire(token: String) {
                notify("Token is about to expire")
                // Get a new token
                fetchToken(object : TokenCallback {
                    override fun onTokenReceived(token: String?) {
                        // Use the token to renew
                        signalingEngine!!.renewToken(token)
                        notify("Token renewed")
                    }

                    override fun onError(errorMessage: String) {
                        // Handle the error
                        notify("Error: $errorMessage")
                    }
                })
                super.onTokenPrivilegeWillExpire(token)
            }

            // Reuse events handlers from the base class
            /* override fun onUserJoined(uid: Int, elapsed: Int) {
                baseEventHandler!!.onUserJoined(uid, elapsed)
            }
             */

        }

    fun fetchToken(callback: TokenCallback) {
        // Use the uid from the config file if not specified
        fetchToken(config!!.optInt("uid"), callback)
    }

    fun fetchToken(uid: Int, callback: TokenCallback) {
        // Prepare the Url
        val urlLString = "$serverUrl/rtm/$uid/?expiry=$tokenExpiryTime"

        val client = OkHttpClient()

        // Create a request
        val request: Request = Builder()
            .url(urlLString)
            .header("Content-Type", "application/json; charset=UTF-8")
            .get()
            .build()

        // Send the async http request
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            // Receive the response in a callback
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        // Extract token from the response
                        val responseBody = response.body!!.string()
                        val jsonObject = JSONObject(responseBody)
                        val token = jsonObject.getString("token")
                        // Return the token
                        callback.onTokenReceived(token)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        callback.onError("Invalid token response")
                    }
                } else {
                    callback.onError("Token request failed")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError("IOException: $e")
            }
        })
    }

    fun loginWithToken(uid: Int): Int {
        if (signalingEngine == null) setupSignalingEngine()
        return if (isValidURL(serverUrl)) { // A valid server url is available
            // Fetch a token from the server for uid
            fetchToken(uid, object : TokenCallback {
                override fun onTokenReceived(token: String?) {
                    // Handle the received token
                    login(uid, token)
                }

                override fun onError(errorMessage: String) {
                    // Handle the error
                    notify("Error: $errorMessage")
                }
            })
            0
        } else { // use the token from the config.json file
            val token = config!!.optString("token")
            login(uid, token)
        }
    }

    companion object {
        // A helper function to check that the URL is in the correct form
        fun isValidURL(urlString: String?): Boolean {
            return try {
                // Attempt to create a URL object from the given string
                val url = URL(urlString)
                // Check if the protocol and host in the URL are not empty
                url.protocol != null && url.host != null
            } catch (e: MalformedURLException) {
                // The given string is not a valid URL
                false
            }
        }
    }
}