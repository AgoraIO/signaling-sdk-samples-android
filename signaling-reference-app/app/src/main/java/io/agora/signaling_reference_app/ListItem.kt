package io.agora.signaling_reference_app

data class ListItem(val title: String, val id: ExampleId) {
    enum class ExampleId {
        HEADER,
        SDK_QUICKSTART,
        AUTHENTICATION_WORKFLOW,
        STREAM_CHANNEL,
        STORAGE,
        CLOUD_PROXY,
        DATA_ENCRYPTION,
        GEOFENCING
    }
}

