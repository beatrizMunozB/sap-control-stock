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
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import com.google.gson.Gson
import com.makita.controlstock.data.network.SalidaMercanciaRequest
import com.makita.controlstock.data.network.SalidaMercanciaResponse
import com.makita.controlstock.data.network.PickingCabeceraResponse
import com.makita.controlstock.mostrarDialogo
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import com.makita.controlstock.data.network.SolicitudCabeceraResponse
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner


@Composable
fun PickingSolicitudScreen(usuario: String, navController: NavController) {

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
    var capturasPendientes by rememberSaveable {
        mutableStateOf<List<CapturaPendiente>>(emptyList())
    }
    var mostrarListaCapturas by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mostrarDialogoSinDatos by remember { mutableStateOf(false) }
    var mensajeDialogo by remember { mutableStateOf("") }
    var ubicacionValidada by rememberSaveable { mutableStateOf(false) }

    var solicitudes by remember {
        mutableStateOf<List<PickingCabeceraResponse>>(emptyList())
    }

    var response by rememberSaveable {
        mutableStateOf<List<PickingCabeceraResponse>>(emptyList())
    }

    var numeroPicking by remember { mutableStateOf("") }
    var lastnumeroPicking by remember { mutableStateOf("") }
    var NumeroActual by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var actualizandoPicking by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()


    fun buscarPicking() {

        if (actualizandoPicking) return

        actualizandoPicking = true

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val resultado = apiService.obtenerPickingSolicitud(usuario, "BOD01")

                Log.d("*MAKITA*", "RESPUESTA PICKING = $resultado")
                Log.d("*MAKITA*", "CANTIDAD PICKING = ${resultado.data.size}")

                withContext(Dispatchers.Main) {

                    solicitudes = resultado.data

                    actualizandoPicking = false

                    if (solicitudes.isEmpty()) {

                        mensajeDialogo = "No existen Picking Liberados"
                        mostrarDialogoSinDatos = true
                        mostrarListaCapturas = false

                    } else {

                        mostrarListaCapturas = true

                        Toast.makeText(
                            context,
                            "Picking actualizados: ${solicitudes.size}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {

                Log.d("*MAKITA*", "ERROR PICKING = ${e.message}")

                withContext(Dispatchers.Main) {

                    actualizandoPicking = false

                    Toast.makeText(
                        context,
                        "Error al consultar Picking",
                        Toast.LENGTH_LONG
                    ).show()

                    mostrarListaCapturas = false
                }
            }
        }
    }


    fun validarPicking(NumeroIngresado: String) {

        val NumeroLimpio = NumeroIngresado.trim()

        if (NumeroLimpio.isEmpty()) {
            errorState = "Ingrese Numero de Picking"
            return
        }

        errorState = ""

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val apiResponse = apiService.validarPicking(NumeroLimpio)

                withContext(Dispatchers.Main) {

                    if (apiResponse.data.isNullOrEmpty()) {

                        errorState = "No se encontraron datos para el item proporcionado"
                        numeroPicking = ""
                        itemFocusRequester.requestFocus()

                        return@withContext
                    }

                    val tieneValoresNulos = apiResponse.data.any { it.AbsEntry == null }

                    if (tieneValoresNulos) {

                        Log.d("*MAKITA*", " ingresand modo $tieneValoresNulos ")

                        errorState = "No se encontraron datos"
                        numeroPicking = ""
                        mostrarDialogoProductoNoExiste = true

                        itemFocusRequester.requestFocus()

                    } else {

                        Log.d("*MAKITA*", " ingresand no tiene $tieneValoresNulos ")

                        errorState = ""
                        response = apiResponse.data

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


    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->

            if (event == Lifecycle.Event.ON_RESUME) {
                buscarPicking()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        val solicitudesFiltradas = solicitudes.filter {

            numeroPicking.isBlank() ||
                    it.AbsEntry.toString().contains(numeroPicking)
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp
                ),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFF00695C)
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "PICKING",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            buscarPicking()
                        },
                        enabled = !actualizandoPicking,
                        modifier = Modifier.size(56.dp)
                    ) {

                        if (actualizandoPicking) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )

                        } else {

                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualizar Picking",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                OutlinedTextField(
                    value = numeroPicking,

                    onValueChange = { newValue ->

                        val cleanedText = newValue
                            .replace("\n", "")
                            .replace("\r", "")
                            .replace("\t", "")
                            .trim()

                        numeroPicking =
                            if (cleanedText.length > 20)
                                cleanedText.substring(0, 20)
                            else
                                cleanedText
                    },

                    label = {
                        Text("N° Picking")
                    },

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

                            if (numeroPicking.isNotBlank()) {
                                validarPicking(numeroPicking)
                            }
                        }
                    ),

                    trailingIcon = {

                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Buscar",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    validarPicking(numeroPicking)
                                }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize()
                    ) {

                        items(solicitudesFiltradas) { solicitud ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {

                                        navController.navigate(
                                            "detallePicking/${solicitud.AbsEntry}"
                                        )
                                    }
                                    .border(
                                        width = 2.dp,
                                        color = Color(0xFF1976D2),
                                        shape = RoundedCornerShape(12.dp)
                                    ),

                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),

                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 4.dp
                                ),

                                shape = RoundedCornerShape(12.dp)
                            ) {

                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {

                                    val fechaFormateada = try {

                                        LocalDate.parse(
                                            solicitud.PickDate.take(10)
                                        ).format(
                                            DateTimeFormatter.ofPattern("dd-MM-yyyy")
                                        )

                                    } catch (e: Exception) {

                                        solicitud.PickDate
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {

                                        Text(
                                            text = "Picking N° ${solicitud.AbsEntry}",
                                            modifier = Modifier.weight(1f),
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        Text(
                                            text = fechaFormateada,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {

                                        Text(
                                            text = "Comentarios: ${solicitud.Remarks ?: ""}",
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (
                        lazyListState.isScrollInProgress &&
                        lazyListState.layoutInfo.totalItemsCount > 0
                    ) {

                        val totalItems =
                            lazyListState.layoutInfo.totalItemsCount

                        val visibleItems =
                            lazyListState.layoutInfo.visibleItemsInfo.size

                        if (totalItems > visibleItems) {

                            val firstVisibleItem =
                                lazyListState.firstVisibleItemIndex

                            val maxFirstVisibleItem =
                                (totalItems - visibleItems).coerceAtLeast(1)

                            val scrollFraction =
                                (
                                        firstVisibleItem.toFloat() /
                                                maxFirstVisibleItem.toFloat()
                                        ).coerceIn(0f, 1f)

                            BoxWithConstraints(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .fillMaxHeight()
                                    .padding(
                                        top = 4.dp,
                                        bottom = 4.dp,
                                        end = 2.dp
                                    )
                            ) {

                                val thumbHeight = 70.dp

                                val availableHeight =
                                    (maxHeight - thumbHeight)
                                        .coerceAtLeast(0.dp)

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(
                                            y = availableHeight * scrollFraction
                                        )
                                        .width(10.dp)
                                        .height(thumbHeight)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color.White)
                                        .border(
                                            width = 2.dp,
                                            color = Color.DarkGray,
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}