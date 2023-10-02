# Secure authentication with tokens

Authentication is the act of validating the identity of each user before they access a system. Agora uses digital tokens to authenticate users and their privileges before they access Signaling. Each token is valid for a limited period and works only for a specific user. 

This example shows you how to retrieve a token from an authentication server, and use it to connect securely to Signaling. 

### Deploy a token server

To quickly deploy a token server, use one of the [one-click deployment](https://github.com/AgoraIO-Community/agora-token-service#one-click-deployments) methods or implement your own server using the code [here](https://github.com/AgoraIO-Community/agora-token-service). You use your token server URL for user authentication in this example.

## Understand the code

For context on this implementation, and a full explanation of the essential code snippets used in this example, read the **Secure authentication with tokens** document. 

For the UI implementation of this example, refer to [`AuthenticationActivity.kt`](../android-reference-app/app/src/main/java/io/agora/android_reference_app/AuthenticationActivity.kt).

## How to run this example

To see how to run this example, refer to the [README](../README.md) in the root folder or one of the complete product guides.
