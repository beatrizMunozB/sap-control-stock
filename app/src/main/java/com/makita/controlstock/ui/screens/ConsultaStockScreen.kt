package com.makita.controlstock.ui.screens

import android.text.Layout
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import com.makita.controlstock.data.network.UbicacionResponse
import com.makita.controlstock.data.network.RetrofitClient.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.platform.LocalContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultaStockScreen(navController: NavController) {
    var ubicacion by remember { mutableStateOf("") }
    var ubicacionActual by rememberSaveable { mutableStateOf("") }
    var lastInput by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var errorState by rememberSaveable { mutableStateOf<String?>(null) }
    val ubicacionFocusRequester = remember { FocusRequester() }
    var mostrarDialogoUbicacionNoExiste by rememberSaveable { mutableStateOf(false) }
    var response by remember { mutableStateOf<List<UbicacionResponse>>(emptyList()) }

    val keyboardController = LocalSoftwareKeyboardController.current
    var mostrarDialogoWifiError by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var textoDialogoWifiError by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var mensajeError by remember { mutableStateOf("") }
    var secondTextFieldValue by remember { mutableStateOf("") }

    fun validarItem(UbicacionIngresado: String) {

        Log.d(
            "*MAKITA*",
            "ENTER SCANNER ENTRA A validar item → VALIDANDO $UbicacionIngresado"
        )

        val ubicacionLimpio = UbicacionIngresado.trim()

        Log.d("*MAKITA*", "ingresand modo 1 $ubicacionLimpio")

        if (ubicacionLimpio.isEmpty()) {
            errorState = "Ingrese una ubicacion"
            return
        }

        errorState = ""

        CoroutineScope(Dispatchers.IO).launch {
            try {

                Log.d("*MAKITA*", "ingresand modo 2 $ubicacionLimpio")

                val apiResponse = apiService.obtenerStockUbicacion(ubicacionLimpio)

                val datos = apiResponse.data ?: emptyList()

                Log.d(
                    "*MAKITA*",
                    "VALIDACION: $datos"
                )

                withContext(Dispatchers.Main) {

                    if (datos.isEmpty()) {

                        response = emptyList()
                        secondTextFieldValue = ""

                        errorState =
                            "No se encontraron datos para la ubicacion scaneada"

                        ubicacion = ""
                        ubicacionActual = ""
                        lastInput = ""

                        ubicacionFocusRequester.requestFocus()

                        return@withContext
                    }

                    val tieneValoresNulos =
                        datos.any { it.ubicacion == null }

                    Log.d(
                        "*MAKITA*",
                        "ingresand modo $tieneValoresNulos"
                    )

                    if (tieneValoresNulos) {

                        response = emptyList()

                        errorState = "No se encontraron datos"
                        ubicacion = ""
                        ubicacionActual = ""
                        lastInput = ""

                        mostrarDialogoUbicacionNoExiste = true

                        ubicacionFocusRequester.requestFocus()

                    } else {

                        Log.d(
                            "*MAKITA*",
                            "ingresand no tiene $tieneValoresNulos"
                        )

                        response = datos

                        Log.d(
                            "*MAKITA*",
                            "RESPUESTA GUARDADA: $response"
                        )

                        secondTextFieldValue =
                            datos.firstOrNull()?.descripcion ?: ""

                        errorState = ""

                        ubicacion = ""
                        ubicacionActual = ""
                        lastInput = ""

                        keyboardController?.hide()
                        ubicacionFocusRequester.requestFocus()
                    }
                }

            } catch (e: Exception) {

                Log.e(
                    "*MAKITA*",
                    "Error obteniendo stock",
                    e
                )

                withContext(Dispatchers.Main) {

                    errorState = "Error de conexión"

                    mostrarDialogoWifiError = true

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

            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }

            Text(
                text = "Stock por Ubicacion",
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

                        Log.d(
                            "*MAKITA*",
                            "VALIDANDO"
                        )

                        validarItem(valorLimitado)
                    }
                }
            },

            label = {
                Text("Escanear Ubicacion")
            },

            placeholder = {
                Text("Escanear Ubicacion")
            },

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


            response.forEach { item ->

                item {

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

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = "Descripcion       : ${item.descripcion}"
                            )

                            Text(
                                text = "Ubicación         : ${item.ubicacion}"
                            )

                            Text(
                                text = "Bodega            : ${item.bodega}"
                            )

                            Text(text = "Cantidad          : ${item.cantidad.toDouble().toInt()}")

                            Text(
                                text = "Estado            : ${item.estado}"
                            )

                            Text(
                                text = "Ubicacion estandar: ${item.ubicacionStandar}"
                            )

                            Text(
                                text = "Primera ubicacion : ${item.primeraUbicacion}"
                            )

                            Text(
                                text = "BarCode           : ${item.BarCode}"
                            )
                        }
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(10.dp))


        TextField(
            value = secondTextFieldValue,
            onValueChange = {},
            readOnly = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .width(320.dp)
                .height(80.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedTextColor = Color(0xFF424242),
                unfocusedTextColor = Color(0xFF424242),
                focusedIndicatorColor = Color(0xFF9E9E9E),
                unfocusedIndicatorColor = Color(0xFFBDBDBD)
            )
        )


        if (mostrarDialogoWifiError) {

            AlertDialog(
                onDismissRequest = {
                    mostrarDialogoWifiError = false
                },

                title = {
                    Text(
                        "Error de conexión",
                        color = Color.Red
                    )
                },

                text = {
                    Text(textoDialogoWifiError)
                },

                confirmButton = {

                    TextButton(
                        onClick = {
                            mostrarDialogoWifiError = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }


        if (showDialog) {

            mostrarDialogo3(
                titulo = "Error",
                mensaje = mensajeError,
                onDismiss = {
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun mostrarDialogo3(
    titulo: String,
    mensaje: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = titulo) },
        text = { Text(text = mensaje) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}