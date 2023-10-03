package io.agora.authentication_manager

import okhttp3.*
import okhttp3.Request.*
import org.json.JSONObject
import org.json.JSONException
import android.content.Context
import io.agora.rtm.*
import io.agora.signaling_manager.SignalingManager
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

open class AuthenticationManager(context: Context?) : SignalingManager(
    context!!
) {
    private val serverUrl: String // The base URL to your token server
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

    // Extend the eventListener from the base class
    override val eventListener: RtmEventListener
        get() = object : RtmEventListener {
            // Listen for the event that the token is about to expire
            override fun onTokenPrivilegeWillExpire(token: String) {
                notify("Token is about to expire")
                // Fetch a new token
                fetchToken(object : TokenCallback {
                    override fun onTokenReceived(token: String?) {
                        // Use the token to renew
                        signalingEngine!!.renewToken(token,object : ResultCallback<Void?> {
                            override fun onFailure(errorInfo: ErrorInfo?) {
                                notify("Failed to renew token")
                            }

                            override fun onSuccess(responseInfo: Void?) {
                                notify("Token renewed")
                            }
                        })
                    }

                    override fun onError(errorMessage: String) {
                        // Handle the error
                        notify("Error fetching token: $errorMessage")
                    }
                })
                super.onTokenPrivilegeWillExpire(token)
            }

            // Reuse events handlers from the base class
            override fun onMessageEvent(eventArgs: MessageEvent) {
                // Your Message Event handler
                baseEventHandler?.onMessageEvent(eventArgs)
            }

            override fun onPresenceEvent(eventArgs: PresenceEvent) {
                // Your Presence Event handler
                baseEventHandler?.onPresenceEvent(eventArgs)
            }

            override fun onTopicEvent(eventArgs: TopicEvent) {
                // Your Topic Event handler
                baseEventHandler?.onTopicEvent(eventArgs)
            }

            override fun onLockEvent(eventArgs: LockEvent) {
                // Your Lock Event handler
                baseEventHandler?.onLockEvent(eventArgs)
            }

            override fun onStorageEvent(eventArgs: StorageEvent) {
                // Your Storage Event handler
                baseEventHandler?.onStorageEvent(eventArgs)
            }

            override fun onConnectionStateChanged(
                channelName: String?,
                state: RtmConstants.RtmConnectionState?,
                reason: RtmConstants.RtmConnectionChangeReason?
            ) {
                baseEventHandler?.onConnectionStateChanged(channelName, state, reason)
            }
        }

    fun fetchToken(callback: TokenCallback) {
        fetchToken(localUid, callback)
    }

    private fun fetchToken(uid: Int, callback: TokenCallback) {
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
                        val token = jsonObject.getString("rtmToken")
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
        return if (isValidURL(serverUrl)) { // A valid server url is available
            // Fetch a token from the server for the specified uid
            fetchToken(uid, object : TokenCallback {
                override fun onTokenReceived(token: String?) {
                    // Use the received token to log in
                    if (token != null) login(uid, token)
                }

                override fun onError(errorMessage: String) {
                    // Handle the error
                    notify("Error fetching token: $errorMessage")
                }
            })
            0
        } else { // use the uid and token from the config.json file
            val defaultUid = config!!.optString("uid").toInt()
            val token = config!!.optString("token")
            login(defaultUid, token)
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