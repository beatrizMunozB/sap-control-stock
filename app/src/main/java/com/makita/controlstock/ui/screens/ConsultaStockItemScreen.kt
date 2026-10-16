package com.makita.controlstock.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.style.TextAlign
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

    var errorState by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val ubicacionFocusRequester = remember {
        FocusRequester()
    }

    var mostrarDialogoUbicacionNoExiste by rememberSaveable {
        mutableStateOf(false)
    }

    var response by remember {
        mutableStateOf<List<UbicacionResponse>>(emptyList())
    }

    val keyboardController =
        LocalSoftwareKeyboardController.current

    var mostrarDialogoWifiError by remember {
        mutableStateOf(false)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var textoDialogoWifiError by remember {
        mutableStateOf("")
    }

    var showErrorDialog by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    var mensajeError by remember {
        mutableStateOf("")
    }

    var secondTextFieldValue by remember {
        mutableStateOf("")
    }


    fun validarItem(UbicacionIngresado: String) {

        Log.d(
            "*MAKITA*",
            "ENTER SCANNER ENTRA A validar item → VALIDANDO $UbicacionIngresado"
        )

        val ubicacionLimpio =
            UbicacionIngresado.trim()

        Log.d(
            "*MAKITA*",
            "ingresando modo 1 $ubicacionLimpio"
        )

        if (ubicacionLimpio.isEmpty()) {

            errorState = "Ingrese Item"

            return
        }

        errorState = ""

        coroutineScope.launch {

            try {

                Log.d(
                    "*MAKITA*",
                    "ingresando modo 2 $ubicacionLimpio"
                )

                /*
                 * La llamada al backend se ejecuta
                 * solamente en Dispatchers.IO.
                 */
                val apiResponse =
                    withContext(Dispatchers.IO) {
                        apiService.obtenerStockItem(
                            ubicacionLimpio
                        )
                    }

                /*
                 * Desde aquí volvemos al Main Thread.
                 */

                Log.d(
                    "*MAKITA*",
                    "VALIDACION: ${apiResponse.data}"
                )


                response =
                    apiResponse.data

                Log.d(
                    "*MAKITA*",
                    "DESPUES DE response"
                )

                /*
                 * Obtenemos la descripción del primer registro.
                 */
                secondTextFieldValue =
                    apiResponse.data
                        .firstOrNull()
                        ?.descripcion
                        ?: ""

                Log.d(
                    "*MAKITA*",
                    "NO PASA $secondTextFieldValue"
                )

                /*
                 * Limpiamos el campo del scanner.
                 */
                ubicacion = ""
                ubicacionActual = ""
                lastInput = ""

                keyboardController?.hide()

                /*
                 * Primero verificamos si realmente
                 * no llegó ningún registro.
                 */
                if (apiResponse.data.isNullOrEmpty()) {

                    Log.d(
                        "*MAKITA*",
                        "RESPUESTA VACIA"
                    )

                    errorState =
                        "No se encontraron datos para la ubicacion scaneada"

                    ubicacionActual = ""

                    ubicacionFocusRequester.requestFocus()

                    return@launch
                }

                /*
                 * Aquí solamente comprobamos si EXISTE
                 * algún registro cuya ubicación sea null.
                 *
                 * La lista completa NO es null ni está vacía.
                 */
                val tieneValoresNulos =
                    apiResponse.data.any {
                        it.ubicacion == null
                    }

                Log.d(
                    "*MAKITA*",
                    "ingresando modo xxxx $tieneValoresNulos"
                )

                if (tieneValoresNulos) {

                    Log.d(
                        "*MAKITA*",
                        "EXISTEN REGISTROS CON UBICACION NULL"
                    )

                    errorState =
                        "No se encontraron datos"

                    ubicacionActual = ""




                } else {

                    Log.d(
                        "*MAKITA*",
                        "NO EXISTEN UBICACIONES NULL"
                    )

                    errorState = ""

                    response =
                        apiResponse.data

                    keyboardController?.hide()

                    ubicacionFocusRequester.requestFocus()
                }

            } catch (e: Exception) {

                Log.e(
                    "*MAKITA*",
                    "ERROR OBTENIENDO DATOS",
                    e
                )

                errorState =
                    "Error de conexión"

                mostrarDialogoWifiError = true

                textoDialogoWifiError =
                    e.message
                        ?: "Error desconocido"

                Toast.makeText(
                    context,
                    "Error al obtener los datos, revise WiFi: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

                mensajeError =
                    e.message
                        ?: "Error desconocido"

                showErrorDialog = true
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
                text = "Stock por Item ",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        LaunchedEffect(Unit) {

            ubicacionFocusRequester.requestFocus()
        }


        OutlinedTextField(

            value = ubicacion,

            onValueChange = {

                val cleanedText =
                    it
                        .replace("\n", "")
                        .replace("\r", "")
                        .replace("\t", "")
                        .trim()
                        .uppercase()

                val valorLimitado =
                    if (cleanedText.length > 20) {
                        cleanedText.substring(0, 20)
                    } else {
                        cleanedText
                    }

                ubicacion =
                    valorLimitado

                ubicacionActual =
                    valorLimitado

                if (valorLimitado != lastInput) {

                    lastInput =
                        valorLimitado

                    if (valorLimitado.isNotEmpty()) {

                        Log.d(
                            "*MAKITA*",
                            "VALIDANDO"
                        )

                        validarItem(
                            valorLimitado
                        )
                    }
                }
            },

            label = {
                Text(
                    "Escanear Etiqueta Item"
                )
            },

            placeholder = {
                Text(
                    "Escanear Etiqueta Item"
                )
            },

            leadingIcon = {

                Icon(
                    imageVector =
                        Icons.Default.QrCodeScanner,
                    contentDescription =
                        "Escanear"
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
                            imageVector =
                                Icons.Default.Clear,
                            contentDescription =
                                "Borrar texto"
                        )
                    }
                }
            },

            singleLine = true,

            keyboardOptions =
                KeyboardOptions(
                    imeAction =
                        ImeAction.Search,
                    keyboardType =
                        KeyboardType.Text
                ),

            modifier =
                Modifier
                    .width(300.dp)
                    .height(60.dp)
                    .focusRequester(
                        ubicacionFocusRequester
                    )
                    .onFocusChanged { focusState ->

                        if (focusState.isFocused) {

                            keyboardController?.hide()
                        }
                    }
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )

        LazyColumn {

            items(response) { item ->

                Card(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),

                    elevation =
                        CardDefaults.cardElevation(
                            4.dp
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Item",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.item ?: "",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Left
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Nombre",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.descripcion ?: "",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Ubicacion",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.ubicacion ?: "",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Bodega",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.bodega ?: "",
                                textAlign = TextAlign.End
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Cantidad",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.cantidad.toDouble().toInt().toString(),
                                textAlign = TextAlign.End,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Estado",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.estado ?: "",
                                textAlign = TextAlign.End
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Ubicacion estandar",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.ubicacionStandar ?: "",
                                textAlign = TextAlign.End
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Primera ubicación",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.primeraUbicacion ?: "",
                                textAlign = TextAlign.End
                            )
                        }


                    }
                }
            }
        }


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        TextField(

            value =
                secondTextFieldValue,

            onValueChange = {},

            readOnly = true,

            modifier =
                Modifier
                    .width(300.dp)
                    .height(60.dp)
        )


        /*
         * Diálogo de error WiFi
         */
        if (mostrarDialogoWifiError) {

            AlertDialog(

                onDismissRequest = {
                    mostrarDialogoWifiError =
                        false
                },

                title = {
                    Text(
                        "Error de conexión",
                        color = Color.Red
                    )
                },

                text = {
                    Text(
                        textoDialogoWifiError
                    )
                },

                confirmButton = {

                    TextButton(
                        onClick = {
                            mostrarDialogoWifiError =
                                false
                        }
                    ) {

                        Text("Aceptar")
                    }
                }
            )
        }


        /*
         * Diálogo genérico que ya tenías.
         */
        if (showDialog) {

            mostrarDialogo15(

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
fun mostrarDialogo15(
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

