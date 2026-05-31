package com.example.meteorologiaapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


import com.example.meteorologiaapp.network.WeatherService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    EcraMeteorologia()
                }
            }
        }
    }
}

@Composable
fun EcraMeteorologia() {
    val API_KEY = "8fc86da9e92c89a83269a76b11eb9caf"
    var cidade by remember { mutableStateOf("") }
    var temperatura by remember { mutableStateOf("-- ºC") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val api = remember {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("App de Meteorologia", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = cidade,
            onValueChange = { cidade = it },
            label = { Text("Escreve a cidade...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (cidade.isEmpty()) {
                    Toast.makeText(context, "Por favor, escreve uma cidade!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                coroutineScope.launch {
                    try {
                        val resposta = api.getExchangeWeather(cidade, API_KEY)
                        if (resposta.isSuccessful && resposta.body() != null) {
                            val dados = resposta.body()!!
                            // Aqui só vamos buscar a temperatura por agora
                            temperatura = "${dados.main.temp} ºC"
                        } else {
                            Toast.makeText(context, "Cidade não encontrada!", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Erro de ligação.", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Tempo")
        }

        Spacer(modifier = Modifier.height(48.dp))


        Text("Temperatura Atual:", fontSize = 20.sp)
        Text(text = temperatura, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}