<div align="center">
  <h1>Mobile Development Documentation</h1>
</div>

Attirelly is a mobile application that uses machine learning to classify clothing items and provide outfit recommendations. The app allows users to add images of their clothes, which are then analyzed and categorized. Users can also receive outfit suggestions based on their closet or manually search for outfit ideas.

<div align="center">
  <h2>Features</h2>
</div>

- One tap sign-in with Google and sign-out.
- Receive recommendations as the default home screen.
- Newest outfit recommendations.
- Save and remove recommendations from favorites.
- Utilize machine learning to classify various clothing items.
- Upload and analyze multiple images of clothes.
- View classified results and add clothes to your virtual closet.
- Get outfit recommendations based on your closet.
- Swipe left to delete a clothing item from your closet.
- Swipe right on a clothing card to update the item name.
- Responsive design with Jetpack Compose.

<div align="center">
  <h2>Architecture</h2>
</div>

![App Architecture](https://drive.google.com/uc?export=view&id=1OjsP_uOq6cOyDaJv8P-z_9qsHU8qibjO)

<div align="center">
  <h2>Preview</h2>
</div> 

![App Preview](https://drive.google.com/uc?export=view&id=14iXKnYyBmSecUW2eFwM_vqo49ftxDOxf)

<div align="center">
  <h2>Getting Started</h2>
</div> 

### Prerequisites

- Android Studio
- Android SDK
- Latest version of Google Play Services
- An active Firebase account

1. **Clone the Repository:**
    ```bash
    git clone https://github.com/Attirely/Mobile-Development.git
    cd Mobile-Development
    ```

2. **Open in Android Studio:**
    Open the project in Android Studio.

3. **Sync Project:**
    - Make sure you have the latest version of Google Play Services installed on your development machine and the target device.
    - Allow Android Studio to sync the project and download all necessary dependencies.

4. **Firebase Setup:**
    - Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/).
    - Add your Android app to the Firebase project.
    - Download the `google-services.json` file and place it in the `app` directory.

5. **Run the App:**
    Connect your Android device or use an emulator, then run the app from Android Studio.

<div align="center">
  <h2>Dependencies</h2>
</div>  

The Attirelly app uses the following dependencies:

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Navigation Component](https://developer.android.com/guide/navigation/navigation-getting-started)
- [Lifecycle Components](https://developer.android.com/jetpack/androidx/releases/lifecycle)
- [Firebase](https://firebase.google.com/docs)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt for Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Coil for Image Loading](https://coil-kt.github.io/coil/)
- [Retrofit](https://square.github.io/retrofit/)

<div align="center">
  <h2>Acknowledgments</h2>
</div>  

- Thanks to the [Jetpack Compose](https://developer.android.com/jetpack/compose) team for providing a modern UI toolkit.
- Special thanks to the [Firebase](https://firebase.google.com/docs) team for their powerful ML Kit.
- [Clean Architecture Guide](https://developer.android.com/guide/architecture)
- [Android Application Fundamentals](https://developer.android.com/guide/components/fundamentals)
