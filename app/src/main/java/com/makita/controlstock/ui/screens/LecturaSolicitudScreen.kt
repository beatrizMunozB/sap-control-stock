package com.makita.controlstock.ui.screens
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import com.makita.controlstock.data.network.ItemResponse
import com.makita.controlstock.data.network.RetrofitClient
import com.makita.controlstock.data.network.RetrofitClient.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.makita.controlstock.data.network.CrearTransferenciaSolicitudRequest
import com.makita.controlstock.data.network.RegistrarCapturaSolicitudRequest
import com.makita.controlstock.data.network.RegistrarTrasladoSolicitudRequest
import com.makita.controlstock.data.network.SolicitudLecturaCabeceraResponse
import com.makita.controlstock.data.network.SolicitudLecturaDetalleResponse
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import com.makita.controlstock.session.Sesion
import kotlinx.coroutines.withContext
import java.io.File
import com.makita.controlstock.session.Sesion.usuario


@Composable
fun LecturaSolicitudScreen(
    idCabecera: Int,
    navController: NavHostController
) {


    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val apiService = RetrofitClient.apiService
    var ubicacionOrigen  by remember { mutableStateOf("") }
    var ubicacionDestino by remember { mutableStateOf("") }
    var responseStock    by rememberSaveable { mutableStateOf<List<ItemResponse>>(emptyList()) }
    var response         by rememberSaveable { mutableStateOf<List<ItemResponse>>(emptyList()) }
    var cantidad         by remember { mutableStateOf("") }

    var extractedText    by remember { mutableStateOf("") }
    var extractedText2   by remember { mutableStateOf("") }
    var extractedText3   by remember { mutableStateOf("") }
    var extractedText4   by remember { mutableStateOf("") }
    var gTipoItem        by remember { mutableStateOf("") }
    var textFieldValue2  by remember { mutableStateOf("") }
    var codigoLectura    by remember { mutableStateOf("") }
    val cantidadFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    var errorState by rememberSaveable { mutableStateOf<String?>(null) }
    var response35: String
    var mensajeError by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var secondTextFieldValue by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var mostrarDialogoWifiError by remember { mutableStateOf(false) }
    //var text by remember { mutableStateOf("UC031GZ             000123456000123456Y0088381616126") }
    var text by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    var docNum by remember { mutableStateOf(0) }
    var fromWarehouse by remember { mutableStateOf("") }
    var toWarehouse by remember { mutableStateOf("") }
    var cabecera by remember {
        mutableStateOf<SolicitudLecturaCabeceraResponse?>(null)
    }
    var detalle by remember {
        mutableStateOf<List<SolicitudLecturaDetalleResponse>>(emptyList())
    }
    val coroutineScope = rememberCoroutineScope()
    var manejaUbicacionDestino by remember { mutableStateOf(false) }

    var binAbsOrigen by remember { mutableStateOf<Int?>(null) }
    var binCodeOrigen by remember { mutableStateOf("") }

    var binAbsDestino by remember { mutableStateOf<Int?>(null) }
    var binCodeDestino by remember { mutableStateOf("") }


    val focusRequesterOrigen = remember { FocusRequester() }
    val focusRequesterDestino = remember { FocusRequester() }
    val itemFocusRequester = remember { FocusRequester() }
    val extractedTextFocusRequester = remember { FocusRequester() }


    LaunchedEffect(idCabecera) {

        try {

            val respuesta = apiService.obtenerSolicitudLectura(idCabecera)

            Log.d("*MAKITA*", "success = ${respuesta.success}")
            Log.d("*MAKITA*", "cabecera = ${respuesta.cabecera}")
            Log.d("*MAKITA*", "detalle = ${respuesta.detalle}")

            if (respuesta.success) {

                cabecera = respuesta.cabecera
                detalle = respuesta.detalle

                docNum = respuesta.cabecera.docNumOWTQ
                fromWarehouse = respuesta.cabecera.fromWhsCode
                toWarehouse = respuesta.cabecera.toWhsCode


            }

        } catch (e: Exception) {

            Log.e("*MAKITA*", "Error obteniendo solicitud", e)

        }


        try {

            val resp = apiService.validarManejaUbicacion(toWarehouse)

            manejaUbicacionDestino = (resp == "SI")

        } catch (e: Exception) {

            manejaUbicacionDestino = false

        }

    }

    LaunchedEffect(Unit) {
        text = "UC031GZ             000123456000123456Y0088381616126"
        focusRequester.requestFocus()
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {




        suspend fun validarUbicacionOrigen() {

            keyboardController?.hide()

            try {

                val resp = apiService.validarUbicacionBodega(
                    fromWarehouse,
                    ubicacionOrigen
                )

                if (resp == "SI") {

                    val bin = apiService.obtenerBinUbicacion(
                        fromWarehouse,
                        ubicacionOrigen
                    )

                    if (bin.success) {

                        binAbsOrigen = bin.binAbs
                        binCodeOrigen = bin.binCode ?: ""

                        Log.d("*MAKITA*", "BIN ORIGEN = $binAbsOrigen")
                        Log.d("*MAKITA*", "BIN CODE   = $binCodeOrigen")

                        focusRequesterDestino.requestFocus()

                    } else {

                        mensajeError = "No fue posible obtener el Bin de la ubicación"
                        showDialog = true
                    }

                } else {

                    mensajeError = "Ubicación de origen incorrecta"
                    ubicacionOrigen = ""
                    showDialog = true
                    focusRequesterOrigen.requestFocus()
                }

            } catch (e: Exception) {

                mensajeError = "Error validando ubicación de origen"
                showDialog = true
            }
        }


        suspend fun validarUbicacionDestino() {

            keyboardController?.hide()

            if (!manejaUbicacionDestino) {
                itemFocusRequester.requestFocus()
                return
            }

            try {

                val resp = apiService.validarUbicacionBodega(
                    toWarehouse,
                    ubicacionDestino
                )

                if (resp == "SI") {

                    val bin = apiService.obtenerBinUbicacion(
                        toWarehouse,
                        ubicacionDestino
                    )

                    if (bin.success) {

                        binAbsDestino = bin.binAbs
                        binCodeDestino = bin.binCode ?: ""

                        Log.d("*MAKITA*", "BIN DESTINO = $binAbsDestino")
                        Log.d("*MAKITA*", "BIN CODE DESTINO = $binCodeDestino")

                        itemFocusRequester.requestFocus()

                    } else {

                        mensajeError = "No fue posible obtener el Bin de la ubicación"
                        showDialog = true
                    }

                } else {

                    mensajeError = "Ubicación de Destino incorrecta"
                    ubicacionDestino = ""
                    showDialog = true
                    focusRequesterDestino.requestFocus()
                }

            } catch (e: Exception) {

                mensajeError = "Error validando ubicación de destino"
                showDialog = true
            }
        }


        suspend fun buscarStockManual(textoManual : String){
            var extractedText = textoManual
            try {
                //val stock = apiService.consultarStock(textoManual)
                val stock = apiService.obtenerUbicacionItem(textoManual.trim())
                Log.d("*MAKITA*", "RespuestaManualXX :  : $stock")
                if (stock.isEmpty())
                {
                    // Si la respuesta está vacía, asignamos un mensaje de error
                    Log.d("*MAKITA*", "Respuesta :  : $stock")
                    responseStock = emptyList() // Aseguramos que la respuesta esté vacía
                }
                else
                {
                    responseStock = stock
                    extractedText2 = stock[0].descripcion.trim()

                }
            } catch (e: Exception) {

                e.printStackTrace()
            }
        }


        Column(
            modifier =   Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "LECTURA TRASLADO CON SOLICITUD",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00897B)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Nro de Orden: $docNum",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00897B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bodega y Ubicación Origen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = fromWarehouse,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = TextStyle(
                        color = Color.Red
                    ),
                    label = { Text("Bod Origen") },
                    modifier = Modifier.weight(0.6f)
                )

                OutlinedTextField(
                    value = ubicacionOrigen,
                    onValueChange = {
                        ubicacionOrigen = it.uppercase().trim()
                    },
                    label = {
                        Text(
                            "Ubicación Origen",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            coroutineScope.launch {
                                validarUbicacionOrigen()
                            }
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequesterOrigen)
                        .onFocusChanged {
                            if (it.isFocused) {
                                keyboardController?.hide()
                            }
                        }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = toWarehouse,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = TextStyle(
                        color = Color.Red
                    ),
                    label = { Text("Bod Destino") },
                    modifier = Modifier.weight(0.6f)
                )
/*ACA*/
                OutlinedTextField(
                    value = if (manejaUbicacionDestino)
                        ubicacionDestino
                    else
                        "BODEGA SIN UBICACIONES",

                    onValueChange = {
                        ubicacionDestino = it.uppercase().trim()
                    },

                    readOnly = !manejaUbicacionDestino,
                    enabled = manejaUbicacionDestino,

                    label = {
                        Text(
                            "Ubicación Destino",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },

                    placeholder = {
                        if (!manejaUbicacionDestino) {
                            Text("BODEGA SIN UBICACIONES")
                        }
                    },

                    singleLine = true,

                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),

                    keyboardActions = KeyboardActions(
                        onDone = {
                            coroutineScope.launch {
                                validarUbicacionDestino()
                            }
                        }
                    ),

                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequesterDestino)
                        .onFocusChanged {
                            if (it.isFocused) {
                                keyboardController?.hide()
                            }
                        }
                )

            }

            Spacer(modifier = Modifier.height(16.dp))

           // text = "UC031GZ             000123456000123456Y0088381616126\r\\n"


            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText

                        extractedText = newText.substring(0, 20).trim()

                        coroutineScope.launch {

                            /// va a buscar el tipo de item

                            gTipoItem = apiService.consultarTipoItem(extractedText).trim()

                            Log.d("*MAKITA*", "TIPO ITEM: $gTipoItem")

                            if (gTipoItem == "HERRAMIENTAS")
                            {

                                try {

                                    Log.d("*MAKITA**", "LARGO ENTRA ITEM: ${newText.length}")
                                    Log.d("*MAKITA**", "LARGO ENTRA : ${newText}")


                                    if (newText.length > 20) {

                                        extractedText  = newText.substring(0, 20) // Primeros 20 caracteres (item)
                                        extractedText2 = newText.substring(20, newText.length.coerceAtMost(29)) // Serie desde
                                        extractedText3 = newText.substring(29, newText.length.coerceAtMost(38)) // Serie hasta
                                        extractedText4 = newText.substring(39, newText.length.coerceAtMost(52)) // EAN


                                        try {
                                            val numeroDesde = extractedText2.trim().toInt()
                                            val numeroHasta = extractedText3.trim().toInt()

                                            cantidad = ((numeroHasta - numeroDesde) + 1).toString()
                                        } catch (e: Exception) {
                                            cantidad = ""
                                        }

                                        if (newText.length >= 52) {

                                            keyboardController?.hide()

                                            text = ""

                                            extractedTextFocusRequester.requestFocus()
                                        }


                                    }
                                    else
                                    {


                                        extractedText = newText.substring(0, newText.length.coerceAtMost(19))
                                        extractedText2 = ""
                                        extractedText3 = ""
                                        extractedText4 = ""


                                    }
                                } catch (e: Exception)
                                {
                                    Log.e("*MAKITA*111*", "Error al extraer texto: ${e.message}")

                                    extractedText = ""
                                    extractedText2 = ""
                                    extractedText3 = ""
                                    extractedText4 = ""
                                    Toast.makeText(context, "Largo de etiqueta incorrecta (${newText.length})", Toast.LENGTH_SHORT).show()



                                }


                            }
                            else
                            {

                                ///para ACC Y REP
                                if (newText.length == 51) {
                                    extractedText = ""
                                    extractedText = newText.substring(0, 20).trim() // Primeros 20 caracteres (item)
                                    extractedText2 =
                                        newText.substring(20, (20 + 18).coerceAtMost(newText.length)).trim()
                                    Log.d("*MAKITA*", "INGRESA A LARGO 51: $extractedText")
                                }

                                if (newText.length == 41 || newText.length == 50 || newText.length == 51 || newText.length == 52 || newText.length == 53 || newText.length == 54 || newText.length == 55 || newText.length == 56) {
                                    extractedText  = newText.substring(0, 20).trim() // Primeros 20 caracteres (item)
                                    extractedText2 = newText.substring(20, (20 + 18).coerceAtMost(newText.length)).trim()
                                }

                                if (newText.length == 37) {
                                    extractedText = newText.substring(0, 20).trim() // Primeros 20 caracteres (item)
                                    extractedText2 =
                                        newText.substring(20, (20 + 5).coerceAtMost(newText.length)).trim()
                                }


                                if (newText.length == 41) {
                                    extractedText = newText.substring(0, 20).trim()  // Primeros 20 caracteres (item)
                                    extractedText2 =
                                        newText.substring(20, (20 + 8).coerceAtMost(newText.length)).trim()

                                }


                                if (newText.length == 38) {
                                    extractedText = newText.substring(0, 20).trim() // Primeros 20 caracteres (item)
                                    extractedText2 =
                                        newText.substring(20, (20 + 6).coerceAtMost(newText.length)).trim()

                                }

                                if (newText.length == 20) {
                                    extractedText = newText.substring(0, 20).trim() // Primeros 20 caracteres (item)
                                    extractedText2 = ""
                                    extractedText3 = ""
                                    extractedText4 = ""
                                    keyboardController?.hide() // Ocultar teclado
                                    cantidadFocusRequester.requestFocus() // Pasar el foco al siguiente campo

                                }

                                if (newText.length >= 20) {
                                    keyboardController?.hide() // Ocultar teclado
                                    cantidadFocusRequester.requestFocus() // Pasar el foco al siguiente campo
                                }


                            }




                            /*    }
                            catch (e: Exception) {
                                Log.e("*MAKITA*", "Error consultando tipo item: ${e.message}")
                                Toast.makeText(
                                    context,
                                    "No fue posible obtener el tipo el TIPO ITEM",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            */

                        }

                },
                label = { Text("Item") },
                placeholder = { Text("Escanear Etiqueta de Caja") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(itemFocusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            keyboardController?.hide()
                        }
                    },
                maxLines = 2,
                singleLine = false,
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Icono de edición") },
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = { text = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar texto")
                        }
                    }
                }
            )


            LaunchedEffect(extractedText) {

                Log.d("*MAKITA*", "LaunchedEffect -> extractedText = '$extractedText'")

                if (extractedText.isBlank()) {
                    return@LaunchedEffect
                }

                if (!isNetworkAvailable(context)) {
                    Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
                    return@LaunchedEffect
                }


                CoroutineScope(Dispatchers.IO).launch {
                    var continuar = true
                    try {

                        response35 =  apiService.validarExisteItem(extractedText.trim())

                        Log.d("*MAKITA**", "VALIDA EXISTE ITEM SI/NO 1: $response35")

                        withContext(Dispatchers.Main) {
                            if (response35 == "NO") {

                                textFieldValue2 = ""
                                mensajeError =
                                    "Item: ${extractedText.trim()} NO EXISTE O NO CORRESPONDE A $gTipoItem"

                                showDialog = true

                                // Limpiar valores
                                // AQUI PRUEBA
                                // text = ""

                                extractedText = ""
                                extractedText2 = ""
                                extractedText3 = ""
                                extractedText4 = ""
                                textFieldValue2 = ""
                                secondTextFieldValue = ""
                                cantidad = ""
                                response = emptyList()

                                // Enfocar el campo nuevamente
                                delay(100)

                                //itemFocusRequester.requestFocus()
                                //return@withContext  // pero solo por bloque... se cambia

                                continuar = false
                                // return@launch
                            }
                        }

                       // if (!continuar) return@launch
                       // textFieldValue2 = ""

                        if (!continuar) return@launch

                        val apiResponse = apiService.obtenerUbicacionItem(extractedText.trim())
    
                        withContext(Dispatchers.Main) {

                            if (apiResponse.isNullOrEmpty()) {
                                errorState = "No se encontraron datos para el item proporcionado"
                                return@withContext
                            }

                            errorState = null
                            val tieneValoresNulos = apiResponse.any { it.item == null }

                            if (tieneValoresNulos) {
                                Log.d("*MAKITA*", "La respuesta contiene valores nulos")
                                showErrorDialog = true

                                // Limpiar valores
                                text = ""
                                extractedText2 = ""
                                extractedText3 = ""
                                extractedText4 = ""
                                textFieldValue2 = ""
                                secondTextFieldValue = ""
                                response = emptyList()

                                itemFocusRequester.requestFocus()
                            } else {

                                response = apiResponse
                                if (response.isNotEmpty()) {
                                    textFieldValue2 = response.first().descripcion.trim()
                                }

                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            mostrarDialogoWifiError = true
//                               Log.e("*MAKITA*", "Error obteniendo datos 22222: ${e.message}")
                            Toast.makeText(
                                context,
                                "Error al obtener los datos, revise WiFi: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            showErrorDialog = true

                            text = ""
                            extractedText2 = ""
                            extractedText3 = ""
                            extractedText4 = ""
                            textFieldValue2 = ""
                            response = emptyList()


                            delay(1000)
                            itemFocusRequester.requestFocus()
                        }

                    }

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // llena descripcion

            OutlinedTextField(
                value = textFieldValue2.uppercase().trim(),
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Descripcion")
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Red,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    OutlinedTextField(
                        value = extractedText2.uppercase().trim(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Serie Desde") },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Red,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    OutlinedTextField(
                        value = extractedText3.uppercase().trim(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Serie Hasta") },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Red,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = cantidad,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth()
                              .focusRequester(cantidadFocusRequester)
                    ,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Red,
                        unfocusedTextColor = Color.Red
                    ),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                )

                if (cantidad.length > 10) {
                    Text(
                        text = "La cantidad no debe exceder los 10 caracteres",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))


                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    //horizontalArrangement = Arrangement.SpaceEvenly // Espaciado uniforme entre boton
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {


                    val buttonColors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00909E),
                        contentColor = Color.White
                    )


                    Button(
                        onClick = {
                            text = ""
                            // mientras poara pruebas
                            text = "UC031GZ             000123456000123456Y0088381616126"
                            extractedText = ""
                            extractedText2 = ""
                            extractedText3 = ""
                            extractedText4 = ""
                            textFieldValue2 = ""
                            ubicacionOrigen = ""
                            ubicacionDestino = ""

                            cantidad = ""
                            response = emptyList()
                            // ubicacionFocusRequester.requestFocus()
                            // itemFocusRequester.requestFocus()
                        },
                        colors = buttonColors,
                        modifier = Modifier
                            .weight(1f)
                            .height(43.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)

                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Borrar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "BORRAR", fontSize = 14.sp)
                    }

                    Button(
                        onClick = {
                            if (extractedText.isNotEmpty()) {

                                Log.d("*MAKITA*", "ENTRO AL IF")

                                CoroutineScope(Dispatchers.Main).launch {

                                    try {

                                        if (binAbsOrigen == null) {
                                            validarUbicacionOrigen()
                                        }

                                        if (binAbsDestino == null) {
                                            validarUbicacionDestino()
                                        }

                                        Log.d("*BIN*", "binAbsOrigen=$binAbsOrigen")
                                        Log.d("*BIN*", "binCodeOrigen=$binCodeOrigen")

                                        Log.d("*BIN*", "binAbsDestino=$binAbsDestino")
                                        Log.d("*BIN*", "binCodeDestino=$binCodeDestino")

                                        val request = RegistrarCapturaSolicitudRequest(
                                            idCabecera   = idCabecera,
                                            itemCode     = extractedText.trim(),
                                            fromWhsCode  = fromWarehouse,
                                            toWhsCode    = toWarehouse,
                                            binAbsOrigen  = binAbsOrigen,
                                            binCodeOrigen = ubicacionOrigen,

                                            binAbsDestino = binAbsDestino,
                                            binCodeDestino = ubicacionDestino,
                                            serieDesde = extractedText2.trim(),
                                            serieHasta = extractedText3.trim(),
                                            codigoEAN = extractedText4.trim(),
                                            usuario = usuario
                                        )



                                        val respuesta = apiService.registrarCapturaSolicitud(request)

                                        withContext(Dispatchers.Main) {

                                            if (respuesta.isSuccessful) {

                                                mensajeError = respuesta.body()?.message ?: "SERIE REGISTRADA"
                                                showDialog = true

                                                // Limpiar datos para la siguiente lectura
                                                ubicacionOrigen = ""
                                                ubicacionDestino = ""
                                                textFieldValue2 =  ""
                                                extractedText = ""
                                                extractedText2 = ""
                                                extractedText3 = ""
                                                extractedText4 = ""
                                                cantidad = ""

                                            } else {

                                                mensajeError = "La serie ya fue capturada ${extractedText2.trim()}"
                                                showDialog = true
                                            }
                                        }

                                    } catch (e: Exception) {

                                        withContext(Dispatchers.Main) {

                                            mensajeError = "Error: ${e.message}"
                                            showDialog = true
                                        }
                                    }
                                }
                            }
                        },
                        colors = buttonColors,
                        modifier = Modifier
                            .weight(1f)
                            .height(43.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)

                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(text = "GRABAR", fontSize = 14.sp)
                    }


                    Button(
                        onClick = {

                            CoroutineScope(Dispatchers.Main).launch {

                                try {

                                    val request = CrearTransferenciaSolicitudRequest(
                                        idCabecera = idCabecera
                                    )

                                    val respuesta = apiService.crearTransferenciaSolicitud(request)

                                    withContext(Dispatchers.Main) {

                                        if (respuesta.success) {

                                            mensajeError = "TRANSFERENCIA PROCESADA. N° SAP: ${respuesta.docNum}"
                                            showDialog = true

                                        }
                                        else {

                                            mensajeError = respuesta.message ?: "Error al crear la transferencia."
                                            showDialog = true

                                        }
                                    }

                                } catch (e: Exception) {

                                    withContext(Dispatchers.Main) {

                                        mensajeError = "Error: ${e.message}"
                                        showDialog = true

                                    }

                                }

                            }

                        },
                        colors = buttonColors,
                        modifier = Modifier
                            .weight(1.3f)      // un poco más ancho
                            .height(43.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)

                    ) {

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "ENVIAR A SAP",
                            fontSize = 14.sp
                        )
                    }


                }


            if (showDialog) {
                mostrarDialogo5(
                    titulo = "Listo!",
                    mensaje = mensajeError,
                    onDismiss = { showDialog = false }
                )
            }




        }
    }
}

@Composable
fun mostrarDialogo5(
    titulo: String,
    mensaje: String,
    onDismiss: () -> Unit
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





fun guardarRespaldo(
    context: Context,
    registro: RegistrarTrasladoSolicitudRequest,
    fechaSolicitud: String,
) {
    // Ruta del archivo

    val archivo = File(context.filesDir, "trasladoconsolictud${fechaSolicitud}.csv")

    Log.d(
        "*MAKITA*",
        "directorio $archivo"
    )

    Log.d(
        "*MAKITA*",
        "directorio de datos $context.filesDir"
    )

    // Verificar si el archivo existe, si no, escribir el encabezado
    if (!archivo.exists()) {
        val encabezado =
            "DocNumOWTQ;FechaProceso;Usuario;FromWhsCode;ToWhsCode;UbicacionOrigen;UbicacionDestino;ItemCode;SerieDesde;SerieHasta;CodigoEAN\n"
        archivo.writeText(encabezado) // Escribir encabezado en el archivo
    }

    // Construir contenido del archivo
    val contenido = """
        ${registro.DocNumOWTQ};
        ${registro.FechaProceso};
        ${registro.Usuario};
        ${registro.FromWhsCode};
        ${registro.ToWhsCode};
        ${registro.UbicacionOrigen};
        ${registro.UbicacionDestino};
        ${registro.ItemCode};
        ${registro.SerieDesde};
        ${registro.SerieHasta};
        ${registro.CodigoEAN}
    """.trimIndent()

    try {
        // Escribir los datos en el archivo, añadiendo una nueva línea
        archivo.appendText(contenido + "\n")
        Toast.makeText(context, "Datos guardados exitosamente en formato CSV", Toast.LENGTH_SHORT)
            .show()
    } catch (e: Exception) {
        // Manejar errores
        Toast.makeText(context, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT)
            .show()
        e.printStackTrace()
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}