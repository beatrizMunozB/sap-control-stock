package com.makita.controlstock.ui.screens

import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import android.util.Log
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
import com.makita.controlstock.data.network.SolicitudCabeceraResponse
import com.makita.controlstock.mostrarDialogo
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope


@Composable
fun TrasladosSolicitudScreen(usuario: String ,  navController: NavController) {


    var response by rememberSaveable {
        mutableStateOf<List<SolicitudCabeceraResponse>>(emptyList())
    }

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

    var solicitudes by remember {
        mutableStateOf<List<SolicitudCabeceraResponse>>(emptyList())
    }
    var numeroSolicitud by remember { mutableStateOf("") }
    var lastNumeroSolicitud by remember { mutableStateOf("") }
    var NumeroActual by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()



    fun buscarSolicitud() {

        Log.d("*MAKITA*", "buscarSolicitud")

        CoroutineScope(Dispatchers.IO).launch {

            try {


                val resultado = apiService.obtenerTrasladoSolicitud(usuario,"BOD01")

                withContext(Dispatchers.Main) {

                    solicitudes = resultado.data

                    if (solicitudes.isEmpty())
                    {
                        mensajeDialogo         = "No existen Solicitudes de Traslados"
                        mostrarDialogoSinDatos = true
                        mostrarListaCapturas   = false
                    }
                    else
                    {
                        mostrarListaCapturas = true
                    }

                    //mostrarListaCapturas = transferencias.isNotEmpty()
                }

            } catch (e: Exception) {

                Log.d("*MAKITA*", e.message.toString())
                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        context,
                        "Error al consultar Solicitudes de Traslados",
                        Toast.LENGTH_LONG
                    ).show()

                    mostrarListaCapturas = false

                }

            }
        }
    }


    fun validarSolicitud(NumeroIngresado: String) {

        val NumeroLimpio = NumeroIngresado.trim()

        if (NumeroLimpio.isEmpty()) {
            errorState = "Ingrese Numero de Solicitud Valido"
            return
        }

        errorState = ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiResponse = apiService.validarSolicitud(NumeroLimpio)
                
                withContext(Dispatchers.Main) {

                    if (apiResponse.data.isNullOrEmpty()) {
                        errorState = "No se encontraron datos para el item proporcionado"
                        numeroSolicitud = ""
                        itemFocusRequester.requestFocus()
                        return@withContext
                    }

                    val tieneValoresNulos = apiResponse.data.any { it.DocEntry == null }

                    /*
                    Log.d("*MAKITA*", "ANTES validarItem")
                    Log.d("*MAKITA*", "ubicacionValidada=$ubicacionValidada")
                    Log.d("*MAKITA*", "response=${response.size}")
                    */

                    if (tieneValoresNulos) {
                        Log.d("*MAKITA*", " ingresand modo $tieneValoresNulos ")
                        errorState = "No se encontraron datos"
                        numeroSolicitud = ""
                        mostrarDialogoProductoNoExiste = true

                        itemFocusRequester.requestFocus()
                    } else {
                        Log.d("*MAKITA*", " ingresand no tiene $tieneValoresNulos ")

                        errorState = ""
                        response = apiResponse.data
                        // textFieldValue2 = apiResponse.first().descripcion
                        /*
                        Log.d("*MAKITA*", "DESPUES validarItem")
                        Log.d("*MAKITA*", "ubicacionValidada=$ubicacionValidada")
                        Log.d("*MAKITA*", "response=${response.size}")
                        */

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


    LaunchedEffect(Unit) {
        buscarSolicitud()
    }

    Surface(modifier = Modifier.fillMaxSize()) {

        val solicitudesFiltradas = solicitudes.filter {

            numeroSolicitud.isBlank() ||
                    it.DocNum.toString().contains(numeroSolicitud)

        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(16.dp)
        ) {

            Text(
                text = "TRASLADO CON SOLICITUD",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = numeroSolicitud,
                onValueChange = { newValue ->

                    val cleanedText = newValue
                        .replace("\n", "")
                        .replace("\r", "")
                        .replace("\t", "")
                        .trim()

                    numeroSolicitud =
                        if (cleanedText.length > 20)
                            cleanedText.substring(0, 20)
                        else
                            cleanedText

                    if (numeroSolicitud.length >= 1 &&
                        numeroSolicitud != lastNumeroSolicitud) {

                        lastNumeroSolicitud = numeroSolicitud

                        coroutineScope.launch {

                            delay(120)

                            if (numeroSolicitud == lastNumeroSolicitud) {

                                Log.d(
                                    "*MAKITA*",
                                    "VALIDANDO SOLICITUD -> $numeroSolicitud"
                                )

                                validarSolicitud(numeroSolicitud)
                            }
                        }
                    }
                },

                label = { Text("N° Solicitud") },

                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.isFocused) {
                            keyboardController?.hide()
                        }
                    },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),

                keyboardActions = KeyboardActions(
                    onDone = {
                        validarSolicitud(numeroSolicitud)
                    }
                ),

                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Buscar",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                validarSolicitud(numeroSolicitud)
                            }
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                items(solicitudesFiltradas) { solicitud ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                navController.navigate(
                                    "detalleSolicitud/" +
                                            "${solicitud.DocEntry}/" +
                                            "${solicitud.DocNum}/" +
                                            "${solicitud.CardCode}/" +
                                            "${Uri.encode(solicitud.CardName ?: "")}/" +
                                            "${solicitud.FromWarehouse}/" +
                                            "${solicitud.ToWarehouse}"
                                )
                            },
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {

                            Text(
                                text = "Solicitud N° ${solicitud.DocNum}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            val fechaFormateada = try {
                                LocalDate.parse(solicitud.DocDate.take(10))
                                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                            } catch (e: Exception) {
                                solicitud.DocDate
                            }

                            Text(
                                text = "Fecha: $fechaFormateada"
                            )

                            Text(
                                text = "Cliente: ${solicitud.CardName ?: ""}"
                            )

                            Text(
                                text = "Bodega Origen: ${solicitud.FromWarehouse ?: ""}"
                            )

                            solicitud.ToWarehouse?.let {
                                Text(
                                    text = "Bodega Destino: $it"
                                )
                            }

                        }
                    }
                }
            }
        }
    }







}