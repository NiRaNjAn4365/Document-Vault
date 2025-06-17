package com.example.documentvault

sealed class Screens(val route:String) {
    object SplashScreen: Screens("splash_screen")
    object AuthScreen: Screens("auth_screen")
    object HomeScreen: Screens("home_screen")
    object PhotoView: Screens("photo_screen")
    object ImageDetail: Screens("image_details_screen")
}