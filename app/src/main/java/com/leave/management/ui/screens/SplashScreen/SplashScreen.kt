package com.leave.management.ui.screens.SplashScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.leave.management.navigation.ROUTE_LOGIN
import kotlinx.coroutines.delay
import com.leave.management.R
@Composable
fun SplashScreen(navController: NavHostController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.LightGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {



        Image(painter = painterResource(id = R.drawable.leaveicon4),
            contentDescription = "",
            modifier = Modifier.size(250.dp))
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "Leave Application",
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(30.dp))
        //LinearProgressIndicator
        LinearProgressIndicator(
            modifier = Modifier
                .padding(20.dp)
                .height(10.dp)
                .width(200.dp)
                .clip(RoundedCornerShape(15.dp)), // Adjust the corner radius as needed
            color = Color.Cyan
        )

    }

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(ROUTE_LOGIN)
        {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}