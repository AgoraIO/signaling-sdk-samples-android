# Android reference app for Agora Signaling SDK

This repository holds the code examples used for the [Agora Signaling SDK for Android](https://docs-beta.agora.io/en/signaling/overview/product-overview?platform=android) documentation. It is a robust and comprehensive documentation reference app for Android, designed to enhance your productivity and understanding. It's built to be flexible, easily extensible, and beginner-friendly.

Clone the repo, run, and test the samples, and use the code in your own project. Enjoy.

- [Samples](#samples)
- [Prerequisites](#prerequisites)
- [Run the app](#run-the-app)
- [Contact](#contact)

## Samples

This reference app includes several samples that illustrate the functionality and features of Agora Signaling SDK. Each sample is self-contained and the relevant code can be found in its own folder in the root directory. For more information about each sample, see:

- [SDK quickstart](signaling-manager/) - the minimum code you need to integrate low-latency, high-concurrency
  signaling features into your app using Signaling SDK.
- [Secure authentication with tokens](authentication-workflow/) - quickly set up an authentication token server, retrieve a token from the server, and use it to connect securely to Signaling as a specific user.
- [Stream channels](stream-channel/) - communicate to other users in topics.
- [Store channel and user data](storage) - easily store data for users and channels without the need to
  set up your own databases. 
- [Connect through restricted networks with Cloud Proxy](cloud-proxy/) - ensure reliable connectivity for your users when they connect from an
  environment with a restricted network.
- [Data encryption](data-encryption) - integrate built-in data encryption into your app using Signaling.
- [Geofencing](geofencing) - only connect to Signaling within the specified region.

To view the UI implementation, open the relevant Activity Class file [here](signaling-reference-app/app/src/main/java/io/agora/signaling_reference_app).


## Prerequisites

Before getting started with this reference app, ensure you have the following set up:

- Android Studio 4.1 or higher
- Android SDK API Level 24 or higher
- A mobile device that runs Android 4.1 or higher
- An Agora account and project
- A computer with Internet access. Ensure that no firewall is blocking your network communication.

## Run the app

1. **Clone the repository**

    To clone the repository to your local machine, open Terminal and navigate to the directory where you want to clone the repository. Then, use the following command:

    ```sh
    git clone https://github.com/AgoraIO/signaling-sdk-samples-android.git
    ```

1. **Open the project**

    Launch Android Studio. From the **File** menu, select **Open...** and navigate to the [signaling-reference-app](signaling-reference-app) folder. Start Gradle sync to automatically install all project dependencies.

1. **Modify the project configuration**

   The app loads connection parameters from the [`config.json`](./agora-manager/src/main/res/raw/config.json) file. Ensure that the file is populated with the required parameter values before running the application.

    - `uid`: The user ID associated with the application.
    - `appId`: (Required) The unique ID for the application obtained from [Agora Console](https://console.agora.io). 
    - `channelName`: The default name of the channel to join.
    - `token`: A token generated for `uid`. You generate a temporary token using the [Agora token builder](https://agora-token-generator-demo.vercel.app/).
    - `serverUrl`: The URL for the token generator. See [Secure authentication with tokens](https://docs-beta.agora.io/en/signaling/get-started/authentication-workflow) for information on how to set up a token server.
    - `tokenExpiryTime`: The time in seconds after which a token expires.

    If a valid `serverUrl` is provided, all samples use the token server to obtain a token except the **SDK quickstart** project that uses the `token`. If a `serverUrl` is not specified, all samples except **Secure authentication with tokens** use the `token` from `config.json`.

1. **Build and run the project**

    To build and run the project, select your connected Android device or emulator and press the **Run** button in Android Studio.

1. **Run the samples in the reference app**

    From the main app screen, choose and launch a sample.

## Contact

If you have any questions, issues, or suggestions, please file an issue in our [GitHub Issue Tracker](https://github.com/AgoraIO/video-sdk-samples-android/issues).
