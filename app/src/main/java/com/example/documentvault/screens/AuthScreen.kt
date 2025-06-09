package com.example.documentvault.screens


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.documentvault.Screens

@Composable
fun AuthScreen(navController: NavController) {
    val visibleDarkBlue = Color(0x6515137E)
    var pinDigits by remember { mutableStateOf(List(4) { "" }) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(visibleDarkBlue)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Enter the Security Pin",
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.size(20.dp))

            createDots(pinDigits = pinDigits)
        }

        createDigits(pinDigits,navController) { updatedDigits ->
            pinDigits = updatedDigits
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun createDots(pinDigits: List<String>) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        pinDigits.forEach { digit ->
            OutlinedTextField(
                value = digit,
                onValueChange = {},
                modifier = Modifier
                    .size(60.dp)
                    .padding(4.dp),
                readOnly = true,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                visualTransformation = PasswordVisualTransformation()
            )
        }
    }
}

@Composable
fun createDigits(
    pinDigits: List<String>,
    navController: NavController,
    onPinChange: (List<String>) -> Unit
) {
    val context = LocalContext.current
    val numberList = (1..9).toList() + listOf(-1, 0, -2)

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(horizontal = 32.dp)
    ) {
        items(numberList) { number ->
            when (number) {
                -1 -> Button(
                    onClick = {
                        if (pinDigits.any { it.isEmpty() }) {
                            Toast.makeText(context, "Please enter all the digits", Toast.LENGTH_SHORT).show()
                        } else {
                            if (pinDigits.joinToString("") == "1234") {
                                navController.navigate(Screens.HomeScreen.route) {
                                    popUpTo(Screens.AuthScreen.route) { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Incorrect PIN. Try again.", Toast.LENGTH_SHORT).show()
                                onPinChange(List(4) { "" })
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .size(60.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = "Submit PIN")
                }

                -2 -> Button(
                    onClick = {
                        val index = pinDigits.indexOfLast { it.isNotEmpty() }
                        if (index != -1) {
                            val mutable = pinDigits.toMutableList()
                            mutable[index] = ""
                            onPinChange(mutable)
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .size(60.dp)
                ) {
                    Text("⌫")
                }

                else -> Button(
                    onClick = {
                        val index = pinDigits.indexOfFirst { it.isEmpty() }
                        if (index != -1) {
                            val mutable = pinDigits.toMutableList()
                            mutable[index] = number.toString()
                            onPinChange(mutable)
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .size(60.dp)
                ) {
                    Text(text = number.toString())

                }
            }
        }
    }
}
@Preview(showSystemUi = true)
@Composable
fun PreviewAuth(){
    //AuthScreen()
}