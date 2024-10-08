package com.example.peer_pulse.presentation.splashScreens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.peer_pulse.R
import com.example.peer_pulse.presentation.AuthViewModel
import com.example.peer_pulse.utilities.Screens
import kotlinx.coroutines.delay

@Composable
fun SplashScreen1(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authValue = authViewModel.isUserAuthenticated

    val scale = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 =true) {
        scale.animateTo(
            targetValue = 0.5f,
            animationSpec = tween(
                durationMillis = 1500,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(1000)
        if (authValue) {
            navController.navigate(Screens.MainGraph.route){
                popUpTo(Screens.SplashScreen1.route){
                    inclusive = true
                }
            }
        } else {
            navController.navigate(Screens.AuthGraph.route){
                popUpTo(Screens.SplashScreen1.route){
                    inclusive = true
                }
            }
        }
    }

    Box (
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(red = 1, green = 72, blue = 171))
    ){
            Image(
                painter = painterResource(id = R.drawable.applogoblue),
                contentDescription = "Splash screen picture",
                modifier = Modifier
                    .scale(scale.value)
                    .size(400.dp)
            )
    }
}
