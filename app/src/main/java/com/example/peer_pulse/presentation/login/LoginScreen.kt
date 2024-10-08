package com.example.peer_pulse.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.peer_pulse.R
import com.example.peer_pulse.data.login.SignInState
import com.example.peer_pulse.presentation.AuthViewModel
import com.example.peer_pulse.presentation.signup.AuthTopBar
import com.example.peer_pulse.utilities.ResponseState
import com.example.peer_pulse.utilities.Screens
import com.example.peer_pulse.utilities.ToastMessage
import com.example.peer_pulse.utilities.rememberImeState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun LoginScreen(state: SignInState,
          onSignInClick: () -> Unit,
          navController: NavController, authViewModel: AuthViewModel
) {
   val auth = Firebase.auth
    val context= LocalContext.current
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var valid by remember {
        mutableStateOf<Boolean?>(null)
    }
    var validpassword by remember {
        mutableStateOf<Boolean?>(null)
    }

    Scaffold(
        topBar = {
            AuthTopBar(
                title = "Log In",
                onBackClick = {
                    navController.navigateUp()
                },
                backClick = true
            )
        }
    ) {
        val imeState = rememberImeState()
        val scrollState = rememberScrollState()
        LaunchedEffect(key1 = imeState.value) {
            if(imeState.value){
                scrollState.scrollTo(scrollState.maxValue)
            }
        }
        Column(
            modifier = Modifier.padding(it)
                .padding(4.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(7.5f)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Welcome back to PeerPulse",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(4.dp)
                )
                Spacer(modifier = Modifier.height(30.dp))
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { changedEmail ->
                        email.value = changedEmail
                    },
                    label = {
                        Text(
                            text = "Email"
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Enter your email"
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                /*Text(
                    text = if (valid == true) "The email is valid!!" else if (valid == false) "Please enter a valid college email address" else "",
                    color = if (valid == true) Color.Blue else Color.Red,
                    modifier = Modifier.padding(4.dp)
                )*/
                //Spacer(modifier = Modifier.height(5.dp))
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { changedPassword ->
                        password.value = changedPassword
                    },
                    label = {
                        Text(
                            text = "password"
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Enter your password"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.height(25.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "or", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(25.dp))
                Button(
                    onClick = { onSignInClick() },
                    colors = ButtonDefaults.buttonColors(Color(0xFF121317)),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 0.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(25.dp))

                ) {

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google_image),
                            contentDescription = "Google Logo",
                            modifier = Modifier
                                .padding(start = 0.dp)
                                .size(25.dp)
                        )
                        Spacer(modifier = Modifier.width(60.dp))
                        Text(
                            text = "Continue with Google",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column(
                    modifier = Modifier

                        .weight(3.5f),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Button(
                        onClick = {
                            authViewModel.login(email.value, password.value)
                            authViewModel.college = authViewModel.whichCollege(email.value)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        Text(
                            text = "Log In",
                            fontWeight = FontWeight.Bold
                        )
                        when (val response = authViewModel.login.value) {
                            is ResponseState.Error ->
                            {
                                ToastMessage(message =response.message)
                            }
                            ResponseState.Loading -> {
                                CircularProgressIndicator()
                            }
                            is ResponseState.Success ->{
                                if(response.data == true)
                                {
                                    navController.navigate(Screens.MainGraph.route){
                                        popUpTo(Screens.AuthGraph.route){
                                            inclusive = true
                                        }
                                    }
                                }
                                else if(response.data == false)
                                {
                                    ToastMessage(message = "Login failed, please try again.")
                                }
                                else
                                {

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}