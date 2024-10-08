package io.agora.storage_manager

import android.content.Context
import io.agora.authentication_manager.AuthenticationManager
import io.agora.rtm.*


open class StorageManager(context: Context?) : AuthenticationManager(context!!) {
    val subscribedIds = HashSet<Int>()

    fun setUserMetadata(uid: Int, key: String, value: String) {
        // Create a metadata instance
        val metadata: Metadata = Metadata()

        // Add a metadata item
        val items = ArrayList<MetadataItem>()
        items.add(MetadataItem(key, value, -1))
        metadata.items = items

        // Record who and when set the metadata
        val options = MetadataOptions()
        options.recordTs = true
        options.recordUserId = true

        metadata.majorRevision = -1

        signalingEngine?.storage?.setUserMetadata(uid.toString(), metadata,
            options, object: ResultCallback<Void?> {
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
        val metadata: Metadata = Metadata()

        // Add a metadata item
        val items = ArrayList<MetadataItem>()
        items.add(MetadataItem(key, value, -1))
        metadata.items = items

        // Record who and when set the metadata
        val options = MetadataOptions()
        options.recordTs = true
        options.recordUserId = true

        metadata.majorRevision = -1

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
                val items = data?.items
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
        val metadata: Metadata = Metadata()

        // Add a metadata item
        val items = ArrayList<MetadataItem>()
        items.add(MetadataItem(key, value, revision))
        metadata.items = items
        metadata.majorRevision = -1

        // Record who and when set the metadata
        val options = MetadataOptions()
        options.recordTs = true
        options.recordUserId = true

        signalingEngine?.storage?.setChannelMetadata(channelName, channelType, metadata,
            options, lockName, object: ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    notify("Channel metadata set successfully")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    notify("Failed to set channel metadata: $errorInfo")
                }
            })
    }

    fun getChannelMetadata() {
        signalingEngine?.storage?.getChannelMetadata(channelName, channelType, object: ResultCallback<Metadata?> {
            override fun onSuccess(data: Metadata?) {
                var summary = "Channel metadata\n"
                val items = data?.items
                if (items != null) {
                    for (item in items) {
                        summary += "${item.key}: ${item.value}\n"
                    }
                }
                notify(summary)
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

    fun setLock (lockName: String, ttl: Long) {
        // ttl is the lock expiration time in case the user goes offline
        signalingEngine?.lock?.setLock(channelName, channelType, lockName, ttl, object: ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                notify("Lock set successfully")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                notify(errorInfo.toString())
            }
        })
    }

    fun acquireLock(lockName: String, retry: Boolean) {
        signalingEngine?.lock?.acquireLock(channelName, channelType, lockName, retry, object: ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                notify("Lock acquired successfully")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                notify(errorInfo.toString())
            }
        })
    }

    fun releaseLock(lockName: String) {
        signalingEngine?.lock?.releaseLock(channelName, channelType, lockName, object: ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                notify("Lock released successfully")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                notify(errorInfo.toString())
            }
        })
    }

    fun removeLock(lockName: String) {
        signalingEngine?.lock?.releaseLock(channelName, channelType, lockName, object: ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                notify("Lock released successfully")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                notify(errorInfo.toString())
            }
        })
    }

    fun getLocks() {
        signalingEngine?.lock?.getLocks(channelName, channelType, object: ResultCallback<ArrayList<LockDetail?>> {
            override fun onSuccess(lockDetail: ArrayList<LockDetail?>) {
                var summary = "Lock details:\n"
                for (lock in lockDetail) {
                    if (lock != null) {
                        summary += "Lock: ${lock.lockName}, Owner:${lock.lockOwner}"
                    }
                }
                notify(summary)
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                notify(errorInfo.toString())
            }
        })
    }
}