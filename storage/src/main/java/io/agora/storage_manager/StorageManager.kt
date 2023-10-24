package io.agora.storage_manager

import android.content.Context
import io.agora.authentication_manager.AuthenticationManager
import io.agora.rtm.*
import io.agora.rtm.RtmConstants.RtmChannelType

open class StorageManager(context: Context?) : AuthenticationManager(context!!) {

    fun setUserMetadata(uid: Int, key: String, value: String) {

    }

    fun updateUserMetadata(uid: Int, key: String, value: String) {

    }

    fun getUserMetadata(uid: Int) {

    }

    fun setChannelMetadata(channelName: String, key: String, value: String, revision: Int, lockName: String) {

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