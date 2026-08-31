package com.makita.controlstock.ui.screens
import android.util.Log
import androidx.compose.material3.Text

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.makita.controlstock.ui.theme.ControlStockTheme
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import com.makita.controlstock.data.network.RetrofitClient.apiService
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavType
import androidx.navigation.navArgument

import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.Alignment

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import com.makita.controlstock.model.OpcionDashboard
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.ui.draw.shadow
import com.makita.controlstock.data.network.ItemMovimiento
import com.makita.controlstock.data.network.ItemResponse
import com.makita.controlstock.data.network.LoginSAPRequest
import com.makita.controlstock.ui.screens.ComprasOrdenesScreen
import com.makita.controlstock.ui.screens.ComprasRecepcionScreen
import com.makita.controlstock.ui.screens.ComprasReservaScreen
import com.makita.controlstock.ui.screens.ConsultasScreen
import com.makita.controlstock.ui.screens.ComprasScreen
import com.makita.controlstock.ui.screens.PickingScreen
import com.makita.controlstock.ui.screens.TrasladosScreen
import com.makita.controlstock.ui.screens.ConsultaStockScreen
import com.makita.controlstock.ui.screens.ConsultaStockItemScreen
import com.makita.controlstock.ui.screens.ConsultaStockBodegaScreen
import com.makita.controlstock.ui.screens.PickingRecolectaScreen
import com.makita.controlstock.ui.screens.TrasladosEntradaScreen
import com.makita.controlstock.ui.screens.TrasladosSalidaScreen
import com.makita.controlstock.ui.screens.TrasladosSolicitudScreen
import androidx.compose.ui.draw.blur
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import com.makita.controlstock.data.model.CapturaPendiente
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.style.TextOverflow
import android.widget.Toast
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import com.google.gson.Gson
import com.makita.controlstock.data.network.SalidaMercanciaRequest
import com.makita.controlstock.data.network.SalidaMercanciaResponse
import com.makita.controlstock.data.network.TransferenciaResponse
import com.makita.controlstock.mostrarDialogo
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

val fechaHoy = LocalDate.now()
    .format(DateTimeFormatter.ISO_DATE)


@Composable
fun TrasladosSalidaScreen(usuario: String) {

        var bodegaOrigen by rememberSaveable { mutableStateOf("") }
        var localOrigen by rememberSaveable { mutableStateOf("") }
        var ubicacionOrigen by rememberSaveable { mutableStateOf("") }


        var ubicacionDestino by rememberSaveable { mutableStateOf("") }
        var mostrarCajaLectura by rememberSaveable { mutableStateOf(false) }
        var lecturaIniciada by rememberSaveable { mutableStateOf(false) }

        val coroutineScope = rememberCoroutineScope()
        var showErrorDialogUBI by remember { mutableStateOf(false) }
        var showErrorDialogUBI2 by remember { mutableStateOf(false) }
        var errorMessageUBI by remember { mutableStateOf("") }
        var errorMessageUBI2 by remember { mutableStateOf("") }
        var cantidadActual by rememberSaveable { mutableStateOf("") }
        var itemActual by rememberSaveable { mutableStateOf("") }
        var itemValido by remember { mutableStateOf<Boolean?>(null) } // null = sin validar
        var isLoading by remember { mutableStateOf(false) }
        var errorItem by remember { mutableStateOf<String?>(null) }
        var textFieldValue2 by remember { mutableStateOf("") }

        var SalidasM by remember {
        mutableStateOf<List<SalidaMercanciaResponse>>(emptyList())
         }

        var salidas by remember {
            mutableStateOf<List<SalidaMercanciaResponse>>(emptyList())
        }
        var response by rememberSaveable {
            mutableStateOf<List<ItemResponse>>(emptyList())
        }
        var response2 by rememberSaveable { mutableStateOf<List<ItemResponse>>(emptyList()) }
        var errorState by rememberSaveable { mutableStateOf<String?>(null) }
        var errorMessage by remember { mutableStateOf("") }
        val itemFocusRequester = remember { FocusRequester() }
        val cantidadFocusRequester = remember { FocusRequester() }

        val keyboardController = LocalSoftwareKeyboardController.current
        var mostrarDialogoProductoNoExiste by rememberSaveable {
            mutableStateOf(false)
        }
        var extractedText by remember { mutableStateOf("") }
        var lastInput by remember { mutableStateOf("") }

        var lastCantidadInput by remember { mutableStateOf("") }
        val coroutineScope2 = rememberCoroutineScope()

        val focusManager = LocalFocusManager.current

        var mostrarDialogoCantidad by remember { mutableStateOf(false) }
        var mostrarDialogoErrorServidor by remember { mutableStateOf(false) }

        var mostrarDialogoStock by remember { mutableStateOf(false) }
        var mensajeStock by remember { mutableStateOf("") }
        var habilitarProcesar by remember { mutableStateOf(false) }

        var mostrarDialogoTransferencia by remember { mutableStateOf(false) }
        var mensajeTransferencia by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        var capturasPendientes   by rememberSaveable { mutableStateOf<List<CapturaPendiente>>(emptyList()) }
        var mostrarListaCapturas by rememberSaveable { mutableStateOf(false) }
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        var mostrarDialogoSinDatos by remember { mutableStateOf(false) }
        var mensajeDialogo by remember { mutableStateOf("") }
        var ubicacionValidada by rememberSaveable { mutableStateOf(false) }



        fun obtenerPendientesdia() {

            Log.d("*MAKITA*", "ingresa a ver pendientes")

            CoroutineScope(Dispatchers.IO).launch {

                try {



                    val resultado = apiService.obtenerSalidaMercanciasDiarias(usuario,bodegaOrigen)


                    Log.d("*MAKITA*", resultado.toString())

                    withContext(Dispatchers.Main) {

                        salidas = resultado.data

                        if (salidas.isEmpty()) {

                            mensajeDialogo =
                                "No existen Entradas de Mercancia procesadas por usuario"

                            mostrarDialogoSinDatos = true

                            mostrarListaCapturas = false

                        } else {

                            mostrarListaCapturas = true

                        }

                        //mostrarListaCapturas = transferencias.isNotEmpty()
                    }

                } catch (e: Exception) {

                    Log.d("*MAKITA*", e.message.toString())
                    withContext(Dispatchers.Main) {

                        Toast.makeText(
                            context,
                            "Error al consultar Salida de Mercancia",
                            Toast.LENGTH_LONG
                        ).show()

                        mostrarListaCapturas = false

                    }

                }
            }
        }




        fun validarCantidad() {

            Log.d("*MAKITA*", " ingresando  a  validarCantidad ")

            if (cantidadActual.isBlank()) {
                mostrarDialogoCantidad = true
                return
            }


            val cantidad = cantidadActual.toDoubleOrNull()

            if (cantidad == null || cantidad <= 0) {
                errorState = "Cantidad inválida"
                return
            }

            Log.d("*MAKITA*", " ingresando  a  validarCantidadUbicacion ")

            CoroutineScope(Dispatchers.IO).launch {
                try {

                    // ubicacionOrigen ="I34C1"

                    Log.d("*MAKITA*",
                        "item=$itemActual | bodega=$bodegaOrigen | ubicacion=$ubicacionOrigen | cantidad=$cantidadActual"
                    )


                    val apiResponseCant = apiService.validarCantidadUbicacion(
                        itemActual.trim(),
                        bodegaOrigen.trim(),
                        ubicacionOrigen.trim()
                    )


                    withContext(Dispatchers.Main) {
                        val cantidadIngresada = cantidadActual.toDouble()
                        val cantidadRecibida = apiResponseCant

                        if (apiResponseCant == 0) {
                            mensajeStock = " No hay stock del item $itemActual en la ubicacion  $ubicacionOrigen  por la cantidad de: $cantidadRecibida, Cambie Ubicacion"
                            mostrarDialogoStock = true
                            return@withContext
                        }

                        //val cantidadRecibida  = apiResponseCant.first().cantidad



                        Log.d(
                            "*MAKITA*",
                            " cantidadRecibida=$cantidadRecibida | cantidadIngresada=$cantidadIngresada "
                        )

                        if (cantidadIngresada > cantidadRecibida) {
                            habilitarProcesar = false
                            mensajeStock = "La cantidad ingresada no puede superar el stock disponible de la ubicacion de origen = $cantidadRecibida."
                            mostrarDialogoStock = true
                            return@withContext
                        }

                        if (cantidadIngresada <= cantidadRecibida) {
                            habilitarProcesar = true
                            mensajeStock = "La cantidad ingresada correcta ($cantidadRecibida)."
                            // mostrarDialogoStock = true
                            return@withContext
                        }


                        errorState = ""
                        keyboardController?.hide()
                        cantidadFocusRequester.requestFocus()

                    }

                } catch (e: Exception) {

                    Log.e("*MAKITA*", "ERROR API", e)
                    mostrarDialogoErrorServidor = true

                    withContext(Dispatchers.Main) {
                        errorState = when (e) {
                            is java.net.UnknownHostException -> "Sin conexión a internet"
                            is java.net.SocketTimeoutException -> "Servidor no responde"
                            is retrofit2.HttpException -> "Error del servidor (${e.code()})"
                            else -> "Error inesperado"
                        }
                    }
                }
            }
        }


        fun validarItem(itemIngresado: String) {

            val itemLimpio = itemIngresado.trim()

            Log.d("*MAKITA*", "ENTRO validarItem")


            if (itemLimpio.isEmpty()) {
                errorState = "Ingrese un item"
                return
            }

            errorState = ""

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val apiResponse = apiService.obtenerUbicacionItem(itemLimpio)


                    withContext(Dispatchers.Main) {

                        if (apiResponse.isNullOrEmpty()) {
                            errorState = "No se encontraron datos para el item proporcionado"
                            itemActual = ""
                            itemFocusRequester.requestFocus()
                            return@withContext
                        }

                        val tieneValoresNulos = apiResponse.any { it.item == null }

                        Log.d("*MAKITA*", "ANTES validarItem")
                        Log.d("*MAKITA*", "ubicacionValidada=$ubicacionValidada")
                        Log.d("*MAKITA*", "response=${response.size}")

                        if (tieneValoresNulos) {
                            Log.d("*MAKITA*", " ingresand modo $tieneValoresNulos ")
                            errorState = "No se encontraron datos"
                            itemActual = ""
                            mostrarDialogoProductoNoExiste = true

                            itemFocusRequester.requestFocus()
                        } else {
                            Log.d("*MAKITA*", " ingresand no tiene $tieneValoresNulos ")

                            errorState = ""
                            response = apiResponse
                            textFieldValue2 = apiResponse.first().descripcion

                            Log.d("*MAKITA*", "DESPUES validarItem")
                            Log.d("*MAKITA*", "ubicacionValidada=$ubicacionValidada")
                            Log.d("*MAKITA*", "response=${response.size}")


                            keyboardController?.hide()
                            cantidadFocusRequester.requestFocus()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        errorState = "Error de conexión"
                    }
                }
            }



        }

        if (mostrarDialogoProductoNoExiste) {
            AlertDialog(
                onDismissRequest = {
                    mostrarDialogoProductoNoExiste = false
                },
                title = {
                    Text("Producto no existe")
                },
                text = {
                    Text("El item ingresado no existe.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarDialogoProductoNoExiste = false
                            itemFocusRequester.requestFocus()
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }


        // -------- CARGA BODEGA POR DEFECTO --------
        LaunchedEffect(Unit) {
            try {
                val response = apiService.obtenerBodegaOrigen()
                val def = response.firstOrNull { it.IsDefault == 1 }
                if (def != null) {
                    bodegaOrigen = def.WhsCode
                    localOrigen = def.LocationName

                }
            } catch (_: Exception) {}
        }


        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(16.dp)
            ) {


                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Button(
                        onClick = {
                            mostrarCajaLectura = !mostrarCajaLectura
                            lecturaIniciada = true

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800),
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Escanear",
                            modifier = Modifier.size(32.dp),
                            tint = Color.Red
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            "POSICINAMIENTO TRANSITO ",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { obtenerPendientesdia()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE65100),
                            contentColor = Color.White
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Traslado a transito",
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            "Ver Salidas de Mercancia",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Origen: ${
                                if (ubicacionOrigen.isNotBlank())
                                    "$bodegaOrigen - $ubicacionOrigen"
                                else "—"
                            }",
                            color = if (ubicacionOrigen.isNotBlank())
                                Color(0xFF2E7D32) else Color.Gray
                        )

                    }
                }




                Spacer(modifier = Modifier.height(16.dp))

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }



                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = bodegaOrigen,
                        onValueChange = {},
                        label = { Text("Bodega Origen") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = localOrigen,
                        onValueChange = {},
                        label = { Text("Local Origen") },
                        modifier = Modifier.weight(1f)
                    )
                }

                    ubicacionOrigen  = "I34C1"

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = ubicacionOrigen,
                        onValueChange = {
                            ubicacionOrigen = it.uppercase().trim()
                            ubicacionValidada = false
                        },
                        label = { Text("Ubicacion Origen") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {

                                keyboardController?.hide()

                                coroutineScope.launch {

                                    try {

                                        val resp = apiService.validarUbicacionBodega(
                                            bodegaOrigen,
                                            ubicacionOrigen
                                        )

                                        if (resp == "SI") {

                                            ubicacionValidada = true

                                            itemFocusRequester.requestFocus()

                                        } else {

                                            ubicacionValidada = false
                                            errorMessageUBI = "Ubicacion de origen incorrecta"
                                            ubicacionOrigen = ""
                                            showErrorDialogUBI = true
                                        }

                                    } catch (e: Exception) {

                                        ubicacionValidada = false
                                        errorMessageUBI = "Error validando ubicación de origen"
                                        showErrorDialogUBI = true
                                    }
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                if (it.isFocused) keyboardController?.hide()
                            }
                    )




                LaunchedEffect(ubicacionValidada) {
                    if (ubicacionValidada) {
                        itemActual = ""
                        errorState = ""
                        itemFocusRequester.requestFocus()
                    }
                }

                Log.d("*MAKITA*", "ubicacionValidada=$ubicacionValidada")

                if (ubicacionValidada)
                    /* ANTES modoLectura == ModoLectura.ARTICULOS*/
                {

                    itemActual = "DLM462Z-1"

                    OutlinedTextField(
                        value = itemActual,
                        onValueChange = { newValue ->

                            val cleanedText = newValue
                                .replace("\n", "")
                                .replace("\r", "")
                                .replace("\t", "")
                                .trim()
                                .uppercase()

                            itemActual =
                                if (cleanedText.length > 20)
                                    cleanedText.substring(0, 20)
                                else
                                    cleanedText


                            if (itemActual.length >= 5 && itemActual != lastInput) {
                                lastInput = itemActual

                                coroutineScope.launch {
                                    delay(120) // clave 🔑
                                    if (itemActual == lastInput) {
                                        Log.d("*MAKITA*", "SCANNER DETECTADO → VALIDANDO")
                                        validarItem(itemActual)
                                    }
                                }
                            }
                        }
                        ,
                        label = { Text("Scanear Codigo") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(itemFocusRequester)
                            .onFocusChanged {
                                if (it.isFocused) keyboardController?.hide()
                            },

                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done   // ENTER
                        ),

                        keyboardActions = KeyboardActions(
                            onDone = {
                                validarItem(itemActual)
                            }
                        ),

                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Enviar",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        validarItem(itemActual)
                                    }
                            )
                        },

                        isError = !errorState.isNullOrEmpty()
                    )


                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = textFieldValue2.uppercase().trim(),
                        onValueChange = {},
                        readOnly = true,
                        modifier =Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            color = Color.Red,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    )


                    Spacer(modifier = Modifier.height(8.dp))


                    OutlinedTextField(
                        value = cantidadActual,
                        onValueChange = { newValue ->

                            val cleaned = newValue.trim()

                            if (cleaned.all { it.isDigit() }) {
                                cantidadActual = cleaned
                                errorState = null
                            }
                        },
                        label = { Text("Ingrese Cantidad") },

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),

                        keyboardActions = KeyboardActions(
                            onDone = {
                                validarCantidad()
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),

                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Validar Cantidad",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        validarCantidad()
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                    }
                            )
                        },

                        isError = !errorState.isNullOrEmpty(),

                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(cantidadFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        // VER LISTA
                        /*
                        Button(
                            onClick = {

                                Log.d("*MAKITA*", "CLICKingreso en capturasPendientes  -> Item: $usuario")
                                obtenerPendientesdia()

                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RectangleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE65100),
                                contentColor = Color.White
                            )
                        ) {

                            Text(
                                //"VER AQUI (${solicitudesPendientes.size})",
                                "Ver Tranferencias hasta ayer ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        */

                        // LIMPIAR
                        Button(
                            onClick = {
                                itemActual = ""
                                cantidadActual = ""
                                textFieldValue2 = ""
                                response = emptyList()
                                errorState = ""
                                habilitarProcesar = false
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RectangleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00909E),
                                contentColor = Color.White
                            )
                        ) {
                            Text("LIMPIAR", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }


                        // AGREGAR A LISTA
                        Button(
                            onClick = {

                                val cantidad = cantidadActual.toDouble()

                                val item = itemActual
                                val descripcion = textFieldValue2
                                val ubiOrigen = ubicacionOrigen

                                val bodOrigen = bodegaOrigen

/*
                                val captura = CapturaPendiente(
                                    itemCode         = item,
                                    descripcion      = descripcion,
                                    cantidad         = cantidad,
                                    ubicacionOrigen  = ubiOrigen,
                                    bodegaOrigen     = bodOrigen
                                )

                                capturasPendientes = capturasPendientes + captura

                                Log.d("*MAKITA*", "ingreso en capturasPendientes  -> Item: $captura")

                                */

                                CoroutineScope(Dispatchers.IO).launch {

                                    try {

                                        val binAbsOrigen = apiService.obtenerAbsEntryUbicacion(
                                            item.trim(),
                                            bodOrigen.trim(),
                                            ubiOrigen.trim()
                                        )

                                        if (binAbsOrigen <= 0) {

                                            withContext(Dispatchers.Main) {
                                                mensajeTransferencia =
                                                    "Ubicación origen no válida"
                                                mostrarDialogoTransferencia = true
                                            }

                                            return@launch
                                        }




                                        val request = SalidaMercanciaRequest(
                                            itemCode      = item.trim(),
                                            fromWarehouse = bodOrigen.trim(),
                                            quantity      = cantidad.toDouble(),
                                            binOrigen     = binAbsOrigen,
                                            docDate       = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                                            usuario       = usuario
                                        )

                                        Log.d(
                                            "*MAKITA*",
                                            Gson().toJson(request)
                                        )


                                        Log.d(
                                            "*MAKITA*",
                                            " ingresa a funcion SolicitudTransferenciaRequest $request "
                                        )

                                        val response = apiService.crearSalidaMercancia(request)

                                        if (response.isSuccessful) {

                                            Log.d("MAKITA", "Insertado OK")

                                        } else {

                                            Log.d("MAKITA", "Error API: ${response.code()}")
                                        }

                                    } catch (e: Exception) {

                                        Log.d(
                                            "*MAKITA*",
                                            "ERROR EN Salide de Mercancia",
                                            e
                                        )
                                    }

                                }

                                // Limpiar pantalla
                                itemActual        = ""
                                cantidadActual    = ""
                                textFieldValue2   = ""
                                habilitarProcesar = false

                                itemFocusRequester.requestFocus()

                            },
                            enabled = habilitarProcesar,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RectangleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                "PROCESAR A SAP",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                }

            }
        }

        if (showErrorDialogUBI) {
            mostrarDialogo(
                titulo = "Error",
                mensaje = errorMessageUBI,
                onDismiss = { showErrorDialogUBI = false }
            )
        }

        if (showErrorDialogUBI2) {
            mostrarDialogo(
                titulo = "Error",
                mensaje = errorMessageUBI2,
                onDismiss = { showErrorDialogUBI2 = false }
            )
        }

        if (mostrarDialogoCantidad) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoCantidad = false },
                title = {
                    Text("Atención")
                },
                text = {
                    Text("Debe ingresar una cantidad antes de continuar.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarDialogoCantidad = false
                            cantidadFocusRequester.requestFocus()
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }



        if (mostrarDialogoErrorServidor) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoErrorServidor = false },
                title = { Text("Error") },
                text = { Text("No se pudo conectar con el servidor.") },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarDialogoErrorServidor = false
                    }) {
                        Text("OK")
                    }
                }
            )
        }



        if (mostrarDialogoStock) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoStock = false },
                title = { Text("Validacion Stock") },
                text = { Text(mensajeStock) },
                confirmButton = {
                    TextButton(
                        onClick =
                            {
                                mostrarDialogoStock = false

                                coroutineScope.launch {
                                    delay(100)
                                    cantidadActual = ""
                                    cantidadFocusRequester.requestFocus()
                                }
                            }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }


        // AQUI

        if (mostrarDialogoSinDatos) {

            AlertDialog(
                onDismissRequest = {
                    mostrarDialogoSinDatos = false
                },
                title = {
                    Text("Información")
                },
                text = {
                    Text(mensajeDialogo)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            mostrarDialogoSinDatos = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )

        }

        if (mostrarListaCapturas) {

            AlertDialog(
                onDismissRequest = {
                    mostrarListaCapturas = false
                },
                title = {
                    Text(
                        "Salida Mercancia (${salidas.size})"
                    )
                },
                text = {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                    ) {

                        items(
                            salidas,
                            key = { "${it.DocEntry}-${it.LineNum}" }
                        ) { Salidas ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF5F5F5)
                                )
                            ) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        Text(
                                            "Salida de Mercancia ${Salidas.DocEntry}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )

                                        Text(
                                            Salidas.ItemCode,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )

                                        Text(
                                            Salidas.ItemDescription,
                                            fontSize = 15.sp,
                                            color = Color.Gray,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(
                                            modifier = Modifier.height(4.dp)
                                        )

                                        Text(
                                            "Cantidad: ${Salidas.Quantity.toInt()}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00909E)
                                        )

                                        Text(
                                            "${Salidas.BinCodeOrigen ?: ""} ",
                                            fontSize = 14.sp,
                                            color = Color(0xFF00909E)
                                        )

                                        Text(
                                            "Línea: ${Salidas.LineNum}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    IconButton(
                                        onClick = {

                                            CoroutineScope(Dispatchers.IO).launch {

                                                try {

                                                    val resultado =
                                                        apiService.eliminarSalidaMercancia(
                                                            Salidas.DocEntry.toString()
                                                        )

                                                    withContext(Dispatchers.Main) {

                                                        if (resultado.status == 200) {

                                                            SalidasM =
                                                                SalidasM.filter {
                                                                    it.DocEntry != Salidas.DocEntry
                                                                }

                                                            Toast.makeText(
                                                                context,
                                                                "Salida de Mercancia eliminada",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }

                                                } catch (e: Exception) {

                                                    Log.d(
                                                        "*MAKITA*",
                                                        e.message.toString()
                                                    )
                                                }
                                            }
                                        }
                                    ) {

                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = Color.Red,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {

                    TextButton(
                        onClick = {
                            mostrarListaCapturas = false

                            ubicacionOrigen = ""
                            ubicacionDestino = ""
                        }
                    ) {

                        Text("CERRAR")
                    }
                }
            )
        }


        if (mostrarDialogoTransferencia) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoTransferencia = false },
                title = { Text("Salida de Mercancia") },
                text = { Text(mensajeTransferencia) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarDialogoTransferencia = false
                            itemActual = ""
                            cantidadActual = ""
                            textFieldValue2 = ""
                            habilitarProcesar = false
                            itemFocusRequester.requestFocus()
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }




    }
