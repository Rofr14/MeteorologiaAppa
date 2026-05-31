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

    var cidadeInput by remember { mutableStateOf("") }

    var cidadeEncontrada by remember { mutableStateOf("--") }
    var temperatura by remember { mutableStateOf("--") }
    var humidade by remember { mutableStateOf("--") }
    var vento by remember { mutableStateOf("--") }

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
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text("App de Meteorologia", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = cidadeInput,
            onValueChange = { cidadeInput = it },
            label = { Text("Escreve a cidade...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (cidadeInput.isEmpty()) {
                    Toast.makeText(context, "Por favor, escreve uma cidade!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                coroutineScope.launch {
                    try {
                        val resposta = api.getExchangeWeather(cidadeInput, API_KEY)
                        if (resposta.isSuccessful && resposta.body() != null) {
                            val dados = resposta.body()!!
                            cidadeEncontrada = "${dados.name}, ${dados.sys.country}"
                            temperatura = "${dados.main.temp} ºC"
                            humidade = "${dados.main.humidity} %"
                            vento = "${dados.wind.speed} m/s"
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

        if (cidadeEncontrada != "--") {
            Text("Tempo em $cidadeEncontrada", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(24.dp))

            CartaoMeteorologia(titulo = "Temperatura", valor = temperatura)
            CartaoMeteorologia(titulo = "Humidade", valor = humidade)
            CartaoMeteorologia(titulo = "Vento", valor = vento)
        }
    }
}

@Composable
fun CartaoMeteorologia(titulo: String, valor: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = valor, fontSize = 18.sp)
        }
    }
}