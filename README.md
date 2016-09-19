# OraChat
Android implementation of code challenge from: http://challenge.orainteractive.com

##Installation
Download OraChat.apk and place it on your Android device and install it manually. [Note: You might need to enable install apps from unknown sources in your device's settings.]

##How to build
This is a gradle based project that was built using [Android Studio](http://developer.android.com/sdk/installing/studio.html) and works best with it.

To build the app:


1. Install the following software:
       - Android SDK:
         http://developer.android.com/sdk/index.html
       - Gradle:
         http://www.gradle.org/downloads
       - Android Studio:
         http://developer.android.com/sdk/installing/studio.html

1. Run the Android SDK Manager by pressing the SDK Manager toolbar button
   in Android Studio or by running the 'android' command in a terminal
   window.

1. In the Android SDK Manager, ensure that the following are installed,
   and are updated to the latest available version:
       - Tools > Android SDK Platform-tools
       - Tools > Android SDK Tools
       - Tools > Android SDK Build-tools
       - Android 7.0 > SDK Platform (API 24)
       - Extras > Android Support Repository
       - Extras > Android Support Library

1. Create a file in your working directory called local.properties,
   containing the path to your Android SDK.

1. Import the project in Android Studio:

    1. Press File > Import Project
    1. Navigate to and choose the settings.gradle file in this project
    1. Press OK

1. Choose Build > Make Project in Android Studio or run the following
    command in the project root directory:
   ```
    ./gradlew clean assembleDebug
   ```
1. To install on your test device:

   ```
    ./gradlew installDebug
   ```
