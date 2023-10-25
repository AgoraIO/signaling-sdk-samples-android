package io.agora.storage_manager

import android.content.Context
import io.agora.authentication_manager.AuthenticationManager
import io.agora.rtm.*

open class StorageManager(context: Context?) : AuthenticationManager(context!!) {
    val subscribedIds = HashSet<Int>()

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
                val items = data?.metadataItems
                var summary = "User metadata\n"
                if (items != null) {
                    for (item in items) {
                        summary += "${item.key}: ${item.value}\n"
                    }
                }
                notify(summary)
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                notify("Failed to get user metadata: $errorInfo")
            }
        })
    }

    fun setChannelMetadata(key: String, value: String, revision: Long, lockName: String) {
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

    fun getChannelMetadata() {
        signalingEngine?.storage?.getChannelMetadata(channelName, channelType, object: ResultCallback<Metadata?> {
            override fun onSuccess(data: Metadata?) {
                notify("Get channel metadata success")
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

    fun subscribeUserMetadata(uid: Int) {
        if (subscribedIds.contains(uid)) {
            return
        }
        signalingEngine?.storage?.subscribeUserMetadata(
            uid.toString(),
            object : ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    notify("Subscribe user metadata success")
                    subscribedIds.add(uid)
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    notify(errorInfo.toString())
                }
            })
    }

    fun setLock (channelName: String, lockName: String, ttl: Long) {
        signalingEngine?.lock?.setLock(channelName, channelType, lockName, ttl, object: ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                notify("Lock set successfully")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                notify(errorInfo.toString())
            }
        })
    }

    fun acquireLock(channelName: String, lockName: String, retry: Boolean) {
        signalingEngine?.lock?.acquireLock(channelName, channelType, lockName, retry, object: ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                notify("Lock acquired successfully")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                notify(errorInfo.toString())
            }
        })
    }

    fun releaseLock(channelName: String, lockName: String, retry: Boolean) {
        signalingEngine?.lock?.releaseLock(channelName, channelType, lockName, object: ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                notify("Lock released successfully")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                notify(errorInfo.toString())
            }
        })
    }

    fun removeLock(channelName: String, lockName: String) {
        signalingEngine?.lock?.releaseLock(channelName, channelType, lockName, object: ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                notify("Lock released successfully")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                notify(errorInfo.toString())
            }
        })
    }

    fun getLock(channelName: String, lockName: String) {
        signalingEngine?.lock?.releaseLock(channelName, channelType, lockName, object: ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                notify("Lock released successfully")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                notify(errorInfo.toString())
            }
        })
    }
}