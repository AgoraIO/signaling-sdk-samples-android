package io.agora.storage_manager

import android.content.Context
import io.agora.authentication_manager.AuthenticationManager
import io.agora.rtm.*
import io.agora.rtm.RtmConstants.RtmChannelType


open class StorageManager(context: Context?) : AuthenticationManager(context!!) {

    fun setUserMetadata(uid: Int, key: String, value: String) {
        // Create a metadata instance
        val metadata: Metadata = signalingEngine!!.storage!!.createMetadata()

        // Add a metadata item
        metadata.setMetadataItem(MetadataItem(key, value, -1))
        metadata.majorRevision = -1

        signalingEngine?.storage?.setUserMetadata(uid.toString(), metadata,
            MetadataOptions(true, true), object: ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                   notify("User metadata set successfully")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    notify("Failed to set user metadata: $errorInfo")
                }
            })
    }

    fun updateUserMetadata(uid: Int, key: String, value: String) {
        // Create a metadata instance
        val metadata: Metadata = signalingEngine!!.storage!!.createMetadata()

        // Add a metadata item
        metadata.setMetadataItem(MetadataItem(key, value))

        signalingEngine?.storage?.updateUserMetadata(uid.toString(), metadata,
            MetadataOptions(true, true), object: ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    notify("User metadata updated successfully")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    notify("Failed to update user metadata: $errorInfo")
                }
            })
    }

    fun getUserMetadata(uid: Int) {
        signalingEngine?.storage?.getUserMetadata(uid.toString(), object: ResultCallback<Metadata?> {
            override fun onSuccess(data: Metadata?) {
                notify("get user metadata success")
                val items = data?.metadataItems
                if (items != null) {
                    for (item in items) {
                        notify(item.toString())
                    }
                }
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                notify(errorInfo.toString())
            }
        })
    }

    fun setChannelMetadata(channelName: String, key: String, value: String, revision: Long, lockName: String) {
        // Create a metadata instance
        val metadata: Metadata = signalingEngine!!.storage!!.createMetadata()

        // Add a metadata item
        metadata.setMetadataItem(MetadataItem(key, value, revision))
        metadata.majorRevision = -1

        signalingEngine?.storage?.setChannelMetadata(channelName, channelType, metadata,
            MetadataOptions(true, true), lockName, object: ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    notify("User metadata set successfully")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    notify("Failed to set user metadata: $errorInfo")
                }
            })
    }

    fun getChannelMetadata(channelName: String, channelType: RtmChannelType) {

    }

    fun subscribeUserMetadata(uid: Int) {

    }

    fun setLock (channelName: String, channelType: RtmChannelType, lockName: String, ttl: String) {

    }

    fun acquireLock() {

    }

    fun releaseLock() {

    }

    fun removeLock() {

    }

    fun getLock() {

    }
}