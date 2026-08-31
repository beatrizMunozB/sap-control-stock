package com.makita.controlstock.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.makita.controlstock.data.network.RetrofitClient.apiService
import com.makita.controlstock.data.network.UbicacionResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ConsultaStockItemScreen(navController: NavController) {
    var ubicacion by remember { mutableStateOf("") }
    var ubicacionActual by rememberSaveable { mutableStateOf("") }
    var lastInput by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var errorState by rememberSaveable { mutableStateOf<String?>(null) }
    val ubicacionFocusRequester = remember { FocusRequester() }
    var mostrarDialogoUbicacionNoExiste by rememberSaveable { mutableStateOf(false) }
    var response by rememberSaveable { mutableStateOf<List<UbicacionResponse>>(emptyList()) }

    val keyboardController = LocalSoftwareKeyboardController.current
    var mostrarDialogoWifiError by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var textoDialogoWifiError by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var mensajeError by remember { mutableStateOf("") }
    var secondTextFieldValue by remember { mutableStateOf("") }

    fun validarItem(UbicacionIngresado: String) {


        Log.d("*MAKITA*", "ENTER SCANNER ENTRA A validar item → VALIDANDO $UbicacionIngresado")

        val ubicacionLimpio = UbicacionIngresado.trim()

        Log.d("*MAKITA*", " ingresand modo 1 $ubicacionLimpio ")

        if (ubicacionLimpio.isEmpty()) {
            errorState = "Ingrese una ubicacion"
            return
        }

        errorState = ""

        CoroutineScope(Dispatchers.IO).launch {
            try {

                Log.d("*MAKITA*", " ingresand modo 2 $ubicacionLimpio ")

                val apiResponse = apiService.obtenerStockItem(ubicacionLimpio)
                response = apiResponse.data

                Log.d(
                    "*MAKITA*",
                    "VALIDACION: $response"
                )

                secondTextFieldValue =
                    apiResponse.data.firstOrNull()?.descripcion ?: ""

                ubicacion = ""
                ubicacionActual = ""
                lastInput = ""

                keyboardController?.hide()

                ubicacionFocusRequester.requestFocus()

                withContext(Dispatchers.Main) {

                    if (apiResponse.data.isNullOrEmpty()) {
                        errorState = "No se encontraron datos para la ubicacion scaneada"
                        ubicacionActual = ""
                        ubicacionFocusRequester.requestFocus()
                        return@withContext
                    }

                    val tieneValoresNulos = apiResponse.data.any { it.ubicacion == null }



                    if (tieneValoresNulos) {
                        Log.d("*MAKITA*", " ingresand modo $tieneValoresNulos ")
                        errorState = "No se encontraron datos"
                        ubicacionActual = ""
                        mostrarDialogoUbicacionNoExiste = true

                        ubicacionFocusRequester.requestFocus()
                    } else {
                        Log.d("*MAKITA*", " ingresand no tiene $tieneValoresNulos ")

                        errorState = ""
                        response = apiResponse.data
                        // textFieldValue2 = apiResponse.first().descripcion

                        keyboardController?.hide()
                        ubicacionFocusRequester.requestFocus()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorState = "Error de conexión"

                    mostrarDialogoWifiError = true
//                               Log.e("*MAKITA*", "Error obteniendo datos 22222: ${e.message}")
                    Toast.makeText(
                        context,
                        "Error al obtener los datos, revise WiFi: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    showErrorDialog = true

                }
            }
        }



    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }
            Text(
                text = "Consulta Stock por Item",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        LaunchedEffect(Unit) {
            ubicacionFocusRequester.requestFocus()
        }


        OutlinedTextField(
            value = ubicacion,
            onValueChange = {

                val cleanedText = it
                    .replace("\n", "")
                    .replace("\r", "")
                    .replace("\t", "")
                    .trim()
                    .uppercase()

                val valorLimitado =
                    if (cleanedText.length > 20)
                        cleanedText.substring(0, 20)
                    else
                        cleanedText

                ubicacion = valorLimitado
                ubicacionActual = valorLimitado

                if (valorLimitado != lastInput) {

                    lastInput = valorLimitado

                    if (valorLimitado.isNotEmpty()) {

                        Log.d("*MAKITA*", "VALIDANDO")
                        validarItem(valorLimitado)
                    }
                }
            },

            label = { Text("Escanear Item") },
            placeholder = { Text("Escanear Ubicacion") },

            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Escanear"
                )
            },

            trailingIcon = {
                if (ubicacion.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            ubicacion = ""
                            ubicacionActual = ""
                            lastInput = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Borrar texto"
                        )
                    }
                }
            },

            singleLine = true,

            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            ),

            modifier = Modifier
                .width(300.dp)
                .height(60.dp)
                .focusRequester(ubicacionFocusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        keyboardController?.hide()
                    }
                }
        )


        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {

            items(response) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Item: ${item.item}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))


                        Text(text = "Descripción       : ${item.descripcion}")
                        Text(text = "Ubicación         : ${item.ubicacion}")
                        Text(text = "Bodega            : ${item.bodega}")
                        Text(text = "Cantidad          : ${item.cantidad.toInt()}")
                        Text(text = "Estado            : ${item.estado}")
                        Text(text = "Ubicación estándar: ${item.ubicacionStandar}")


                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = secondTextFieldValue,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .width(300.dp)
                .height(60.dp)
        )


        if (mostrarDialogoWifiError) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoWifiError = false },
                title = { Text("Error de conexión", color = Color.Red) },
                text = { Text(textoDialogoWifiError) },
                confirmButton = {
                    TextButton(onClick = { mostrarDialogoWifiError = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }

        if (showDialog) {
            mostrarDialogo3(
                titulo = "Error",
                mensaje = mensajeError,
                onDismiss = { showDialog = false }
            )
        }

    }
}

