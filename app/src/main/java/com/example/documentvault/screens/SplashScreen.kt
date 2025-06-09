package com.example.documentvault.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.documentvault.R
import com.example.documentvault.Screens
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(controller: NavHostController) {
    LaunchedEffect(Unit) {
        delay(5000L)
        controller.navigate(Screens.AuthScreen.route){
        popUpTo(Screens.SplashScreen.route)
        {
            inclusive=true
        }
        }
    }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        
        Image(
            bitmap = ImageBitmap.imageResource(R.drawable.safe), contentDescription = "safe",
            Modifier.size(150.dp)
        )
        Spacer(Modifier.size(15.dp))
        AnimatedDotsIndicator()
    }
}
@Composable
fun AnimatedDotsIndicator(
    totalDots: Int = 3,
    dotSize: Dp = 12.dp,
    dotSpacing: Dp = 8.dp,
    activeDotScale: Float = 1.5f,
    animationDuration: Int = 300
) {
    var selectedIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500L)
            selectedIndex = (selectedIndex + 1) % totalDots
        }
    }



    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 0 until totalDots) {
            val isSelected = i == selectedIndex
            val scale by animateFloatAsState(
                targetValue = if (isSelected) activeDotScale else 1f,
                animationSpec = tween(durationMillis = animationDuration),
                label = "dotScale"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = dotSpacing / 2)
                    .size(dotSize * scale)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.Gray else Color.White)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewSplash(){
    var controller= rememberNavController()
    SplashScreen(controller)
}