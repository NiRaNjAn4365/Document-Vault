package com.example.documentvault

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.documentvault.screens.AuthScreen
import com.example.documentvault.screens.HomeScreen
import com.example.documentvault.screens.ImageDetailScreen
import com.example.documentvault.screens.PhotoView
import com.example.documentvault.screens.SplashScreen

@Composable
fun Navigation(controller: NavHostController) {
    NavHost(navController = controller, startDestination = Screens.SplashScreen.route) {
        composable(route= Screens.SplashScreen.route) {
            SplashScreen(controller)
        }
        composable(route= Screens.AuthScreen.route){
            AuthScreen(navController = controller)
        }
        composable(Screens.HomeScreen.route) {
            HomeScreen(navController = controller)
        }
        composable("photo_screen/{folderId}") {
            backStackEntry->
            val id=backStackEntry.arguments?.getString("folderId")
            val numid=Integer.parseInt(id)
            PhotoView(numid,controller)
        }
        composable( "image_details_screen/{imageUri}") {
            backStackEntry->
            val uriString = backStackEntry.arguments?.getString("imageUri")
            ImageDetailScreen(uriString,controller)
        }


    }
}