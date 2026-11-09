package com.makita.controlstock.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.makita.controlstock.ModoLectura
import com.makita.controlstock.data.model.CapturaPendiente
import com.makita.controlstock.data.network.ItemResponse
import com.makita.controlstock.data.network.RetrofitClient.apiService
import com.makita.controlstock.data.network.TransferRequest
import com.makita.controlstock.data.network.TransferenciaPendienteResponse
import com.makita.controlstock.data.network.TransferenciaResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.graphics.Color
import com.makita.controlstock.session.Sesion
import com.makita.controlstock.ui.theme.MakitaCeleste
import com.makita.controlstock.ui.theme.MakitaTeal

@Composable
fun TrasladoInmediatoScreen(navController: NavHostController, usuario: String) {

    var bodegaOrigen by rememberSaveable { mutableStateOf("") }
    var localOrigen by rememberSaveable { mutableStateOf("") }
    var ubicacionOrigen by rememberSaveable { mutableStateOf("") }
    var bodegaDestino by rememberSaveable { mutableStateOf("") }
    var localDestino by rememberSaveable { mutableStateOf("") }
    var ubicacionDestino by rememberSaveable { mutableStateOf("") }
    var mostrarCajaLectura by rememberSaveable { mutableStateOf(false) }
    var lecturaIniciada by rememberSaveable { mutableStateOf(false) }
    var modoLectura by rememberSaveable { mutableStateOf<ModoLectura?>(null) }
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
    var transferencias by remember {
        mutableStateOf<List<TransferenciaResponse>>(emptyList())
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

    var solicitudesPendientes by remember {
        mutableStateOf<List<TransferenciaPendienteResponse>>(emptyList())
    }

    var mostrarDialogoSinDatos by remember { mutableStateOf(false) }
    var mensajeDialogo by remember { mutableStateOf("") }



    fun obtenerPendientesdia()
    {

        Log.d("*MAKITA*", "ingresa a ver pendientes")

        CoroutineScope(Dispatchers.IO).launch {

            try {

                Log.d("*MAKITA*", "ingresa a ver pendientes")

                val resultado = apiService.obtenerTransferenciasDiarias(usuario)


                Log.d("*MAKITA*", resultado.toString())

                withContext(Dispatchers.Main) {

                    transferencias = resultado.data

                    if (transferencias.isEmpty()) {

                        mensajeDialogo =
                            "No existen transferencias procesadas desde ayer."

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
                        "Error al consultar transferencias",
                        Toast.LENGTH_LONG
                    ).show()

                    mostrarListaCapturas = false

                }

            }
        }
    }



    fun procesarTransferencia() {

        Log.d("*MAKITA*", " ingresa a funcion procesarTransferencia $bodegaOrigen ")

        CoroutineScope(Dispatchers.IO).launch {
            try {

                // 🔹 Obtener AbsEntry origen
                val binAbsOrigen = apiService.obtenerAbsEntryUbicacion(
                    itemActual.trim(),
                    bodegaOrigen.trim(),
                    ubicacionOrigen.trim()
                )

                Log.d("*MAKITA*", " ORIGEN obtenerAbsEntryUbicacion $binAbsOrigen  $ubicacionOrigen.trim()")

                if (binAbsOrigen <= 0) {
                    withContext(Dispatchers.Main) {
                        mensajeTransferencia = "Ubicación origen no válida"
                        mostrarDialogoTransferencia = true
                    }
                    return@launch
                }

                // 🔹 Obtener AbsEntry destino
                val binAbsDestino = apiService.obtenerAbsEntryUbicacion(
                    itemActual.trim(),
                    bodegaDestino.trim(),
                    ubicacionDestino.trim()
                )


                Log.d("*MAKITA*", "DESTINO  obtenerAbsEntryUbicacion $binAbsDestino  $ubicacionDestino.trim()")


                if (binAbsDestino <= 0) {
                    withContext(Dispatchers.Main) {
                        mensajeTransferencia = "Ubicación destino no válida"
                        mostrarDialogoTransferencia = true
                    }
                    return@launch
                }

                // 🔹 Crear request

                val fechaHoy = LocalDate.now()
                    .format(DateTimeFormatter.ISO_DATE)

                val request = TransferRequest(
                    itemCode = itemActual.trim(),
                    fromWarehouse = bodegaOrigen.trim(),
                    toWarehouse = bodegaDestino.trim(),
                    quantity = cantidadActual.toDouble(),
                    binOrigen = binAbsOrigen,
                    binDestino = binAbsDestino,
                    docDate = fechaHoy,
                    usuario = usuario
                )

                Log.d("*MAKITA*", """
    itemCode: ${request.itemCode}
    fromWarehouse: ${request.fromWarehouse}
    toWarehouse: ${request.toWarehouse}
    quantity: ${request.quantity}
    binOrigen: ${request.binOrigen}
    binDestino: ${request.binDestino}
    docDate: ${request.docDate}
""".trimIndent())

                val response = apiService.crearTransferencia(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        habilitarProcesar = false
                        mensajeStock = "Transferencia creada correctamente"
                    } else {
                        mensajeStock = "Error al crear transferencia (${response.code()})"
                    }
                    mostrarDialogoTransferencia = true
                }

                //   withContext(Dispatchers.Main) {
                //       mensajeStock = "Prueba OK - Origen: $binAbsOrigen / Destino: $binAbsDestino"
                //       mostrarDialogoTransferencia = true
                //   }

            } catch (e: Exception) {

                Log.e("*MAKITA*", "ERROR EN TRANSFERENCIA", e)

                withContext(Dispatchers.Main) {
                    mensajeTransferencia = "Error: ${e.message}"
                    mostrarDialogoTransferencia = true
                }
            }
        }
    }


    fun procesarTodasLasCapturas() {
        CoroutineScope(Dispatchers.IO).launch {
            val errores = mutableListOf<String>()
            val procesadas = mutableListOf<Long>()

            capturasPendientes.forEach { captura ->
                try {
                    val binOrigen = apiService.obtenerAbsEntryUbicacion(
                        captura.itemCode,
                        captura.bodegaOrigen,
                        captura.ubicacionOrigen
                    )
                    val binDestino = apiService.obtenerAbsEntryUbicacion(
                        captura.itemCode,
                        captura.bodegaDestino,
                        captura.ubicacionDestino
                    )

                    if (binOrigen <= 0 || binDestino <= 0) {
                        errores.add("${captura.itemCode}: bin no válido")
                        return@forEach
                    }

                    val request = TransferRequest(
                        itemCode      = captura.itemCode,
                        fromWarehouse = captura.bodegaOrigen,
                        toWarehouse   = captura.bodegaDestino,
                        quantity      = captura.cantidad,
                        binOrigen     = binOrigen,
                        binDestino    = binDestino,
                        docDate       = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                        usuario       =  usuario
                    )

                    val resp = apiService.crearTransferencia(request)
                    if (resp.isSuccessful) {
                        procesadas.add(captura.id)
                    } else {
                        errores.add("${captura.itemCode}: error ${resp.code()}")
                    }

                } catch (e: Exception) {
                    errores.add("${captura.itemCode}: ${e.message}")
                }
            }

            withContext(Dispatchers.Main) {
                capturasPendientes = capturasPendientes
                    .filter { it.id !in procesadas }

                mensajeTransferencia = buildString {
                    if (procesadas.isNotEmpty())
                        appendLine("✅ ${procesadas.size} transferencia(s) creada(s).")
                    if (errores.isNotEmpty()) {
                        appendLine("⚠️ Errores:")
                        errores.forEach { appendLine("• $it") }
                    }
                }
                mostrarDialogoTransferencia = true
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
                Text("Item no existe")
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
            val response = apiService.obtenerBodegaOrigen(Sesion.bodega)
            val def = response.firstOrNull { it.IsDefault == 1 }
            if (def != null) {
                bodegaOrigen = def.WhsCode
                localOrigen = def.LocationName
                bodegaDestino = def.WhsCode
                localDestino = def.LocationName
            }
        } catch (_: Exception) {}
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        Column( modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {


                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 8.dp
                    ),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MakitaTeal
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "Traslado Inmediato",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                Button(
                    onClick = {
                        mostrarCajaLectura = true
                        lecturaIniciada = true
                        modoLectura = ModoLectura.ORIGEN
                        Log.d("*MAKITA*", "INICIAR LECTURA -> MODO ORIGEN")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .height(50.dp),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE65100),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp
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
                        "INICIAR LECTURA",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        obtenerPendientesdia()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .height(50.dp),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE65100),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Transferencias",
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        "Ver Ultimas Transferencias",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // AQUÍ CONTINÚA EL RESTO DE TU PANTALLA
            }

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(Color(0xFF00909E))
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = { modoLectura = ModoLectura.ORIGEN },
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,   // ⬜ fondo botón
                        contentColor = Color.Black     // 🖤 texto
                    )
                ) {
                    Text(
                        text = "ORIGEN",
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = { modoLectura = ModoLectura.DESTINO },
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp),
                    shape = RectangleShape,
                    contentPadding = PaddingValues(
                        horizontal = 6.dp,
                        vertical = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "DESTINO",
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = { modoLectura = ModoLectura.ARTICULOS },
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp),
                    shape = RectangleShape,
                    contentPadding = PaddingValues(
                        horizontal = 4.dp,
                        vertical = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "ITEMS",
                        fontSize = 13.sp
                    )
                }
            }


            Spacer(modifier = Modifier.height(12.dp))


            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    Text(
                        text = "Origen: ${
                            if (ubicacionOrigen.isNotBlank())
                                "$bodegaOrigen - $ubicacionOrigen"
                            else "—"
                        }",
                        color = if (ubicacionOrigen.isNotBlank())
                            Color(0xFF2E7D32) else Color.Gray,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "Destino: ${
                            if (ubicacionDestino.isNotBlank())
                                "$bodegaDestino - $ubicacionDestino"
                            else "—"
                        }",
                        color = if (ubicacionDestino.isNotBlank())
                            Color(0xFF2E7D32) else Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))


            if (modoLectura == ModoLectura.ORIGEN) {

                LaunchedEffect(modoLectura) {
                    focusRequester.requestFocus()
                }
               /*
                OutlinedTextField(
                    value = bodegaOrigen,
                    onValueChange = {},
                    label = { Text("Bodega Origen") },
                    modifier = Modifier.fillMaxWidth()
                )
                */

                // BODEGA ORIGEN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = bodegaOrigen,
                        onValueChange = {},
                        label = { Text("Bodega Origen") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFFFF9C4),
                            focusedContainerColor = Color(0xFFFFF9C4)
                        )
                    )

                    OutlinedTextField(
                        value = localOrigen,
                        onValueChange = {},
                        label = { Text("Local Origen") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFFFF9C4),
                            focusedContainerColor = Color(0xFFFFF9C4)
                        )
                    )
                }


                Spacer(modifier = Modifier.height(8.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = ubicacionOrigen,
                        onValueChange = {
                            ubicacionOrigen = it.uppercase().trim()
                        },
                        label = { Text("Ubicacion Origen") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                if (it.isFocused) keyboardController?.hide()
                            },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFFFF9C4),
                            focusedContainerColor = Color(0xFFFFF9C4)
                        )
                    )

                    // Espacio equivalente a la segunda columna
                    Spacer(modifier = Modifier.weight(1f))
                }




                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val resp = apiService.validarUbicacionBodega(
                                    bodegaOrigen,
                                    ubicacionOrigen
                                )
                                if (resp == "SI") {
                                    modoLectura = ModoLectura.DESTINO

                                } else {
                                    errorMessageUBI = "Ubicacion de origen incorrecta"
                                    ubicacionOrigen = ""
                                    showErrorDialogUBI = true
                                }
                            } catch (e: Exception) {
                                errorMessageUBI = "Error validando ubicacion de origen"
                                showErrorDialogUBI = true
                            }
                        }
                    },
                    enabled = ubicacionOrigen.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MakitaCeleste,
                        contentColor = Color.Black
                    ),
                    shape = RectangleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("SIGUIENTE → DESTINO", fontWeight = FontWeight.Bold)
                }
            }




            // =========================================================
            // Ubicacion de  DESTINO
            // =========================================================
            if (modoLectura == ModoLectura.DESTINO) {

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                Log.d("*MAKITA*", "mPASA -> ")


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedTextField(
                        value = bodegaDestino,
                        onValueChange = {},
                        label = { Text("Bodega Destino") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFFFF9C4),
                            focusedContainerColor = Color(0xFFFFF9C4)
                        )
                    )

                    OutlinedTextField(
                        value = localDestino,
                        onValueChange = {},
                        label = { Text("Local Destino") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFFFF9C4),
                            focusedContainerColor = Color(0xFFFFF9C4)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedTextField(
                        value = ubicacionDestino,
                        onValueChange = {
                            ubicacionDestino = it.uppercase().trim()
                        },
                        label = { Text("Ubicación Destino") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                if (it.isFocused) keyboardController?.hide()
                            },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFFFF9C4),
                            focusedContainerColor = Color(0xFFFFF9C4)
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val resp = apiService.validarUbicacionBodega(
                                    bodegaDestino,
                                    ubicacionDestino
                                )
                                if (resp == "SI") {
                                    modoLectura = ModoLectura.ARTICULOS
                                } else {
                                    errorMessageUBI2 = "Ubicacion de destino incorrecta"
                                    showErrorDialogUBI2 = true
                                }
                            } catch (e: Exception) {
                                errorMessageUBI2 = "Error validando ubicación de destino"
                                showErrorDialogUBI2 = true
                            }
                        }
                    },
                    enabled = ubicacionDestino.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("SIGUIENTE → ITEMS", fontWeight = FontWeight.Bold)
                }
            }

            LaunchedEffect(modoLectura) {
                if (modoLectura == ModoLectura.ARTICULOS) {
                    itemActual = ""
                    errorState = ""
                    itemFocusRequester.requestFocus()
                }
            }



            if (modoLectura == ModoLectura.ARTICULOS) {

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
                    modifier = Modifier.fillMaxWidth(),
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

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // VER LISTA


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
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
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
                            val ubiDestino = ubicacionDestino
                            val bodOrigen = bodegaOrigen
                            val bodDestino = bodegaDestino

                            val captura = CapturaPendiente(
                                itemCode         = item,
                                descripcion      = descripcion,
                                cantidad         = cantidad,
                                ubicacionOrigen  = ubiOrigen,
                                ubicacionDestino = ubiDestino,
                                bodegaOrigen     = bodOrigen,
                                bodegaDestino    = bodDestino
                            )

                            capturasPendientes = capturasPendientes + captura

                            Log.d("*MAKITA*", "ingreso en capturasPendientes  -> Item: $captura")



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
                                                "Ubicación origen no valida"
                                            mostrarDialogoTransferencia = true
                                        }

                                        return@launch
                                    }



                                    val binAbsDestino = apiService.obtenerAbsEntryUbicacion(
                                        item.trim(),
                                        bodDestino.trim(),
                                        ubiDestino.trim()
                                    )

                                    if (binAbsDestino <= 0) {

                                        withContext(Dispatchers.Main) {

                                            mensajeTransferencia = "Ubicacion destino no corresponde"

                                            mostrarDialogoTransferencia = true
                                        }

                                        return@launch
                                    }


                                    val request = TransferRequest(
                                        itemCode      = item.trim(),
                                        fromWarehouse = bodOrigen.trim(),
                                        toWarehouse   = bodDestino.trim(),
                                        quantity      = cantidad.toDouble(),
                                        binOrigen     = binAbsOrigen,
                                        binDestino    = binAbsDestino,
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

                                    val response = apiService.crearTransferencia(request)

                                    if (response.isSuccessful) {

                                        Log.d(
                                            "MAKITA",
                                            "Insertado OK"
                                        )

                                    } else {

                                        Log.d(
                                            "MAKITA",
                                            "Error API: ${response.code()}"
                                        )
                                    }

                                } catch (e: Exception) {

                                    Log.d(
                                        "*MAKITA*",
                                        "ERROR EN TRANSFERENCIA",
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
                            "ENVIAR A SAP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

            }
        }
    }

    if (showErrorDialogUBI) {
        com.makita.controlstock.mostrarDialogo(
            titulo = "Error",
            mensaje = errorMessageUBI,
            onDismiss = { showErrorDialogUBI = false }
        )
    }

    if (showErrorDialogUBI2) {
        com.makita.controlstock.mostrarDialogo(
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
                    "Transferencias del Dia (${transferencias.size})"
                )
            },
            text = {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {

                    items(
                        transferencias,
                        key = { "${it.DocEntry}-${it.LineNum}" }
                    ) { transferencia ->

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
                                        "Transferencia ${transferencia.DocEntry}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )

                                    Text(
                                        transferencia.ItemCode,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )

                                    Text(
                                        transferencia.ItemDescription,
                                        fontSize = 15.sp,
                                        color = Color.Gray,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(
                                        modifier = Modifier.height(4.dp)
                                    )

                                    Text(
                                        "Cantidad: ${transferencia.Quantity.toInt()}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00909E)
                                    )

                                    Text(
                                        "${transferencia.BinCodeOrigen ?: ""} → ${transferencia.BinCodeDestino ?: ""}",
                                        fontSize = 14.sp,
                                        color = Color(0xFF00909E)
                                    )

                                    Text(
                                        "Línea: ${transferencia.LineNum}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                IconButton(
                                    onClick = {

                                        CoroutineScope(Dispatchers.IO).launch {

                                            try {

                                                val resultado =
                                                    apiService.eliminarTransferenciasPendientes(
                                                        transferencia.DocEntry.toString()
                                                    )

                                                withContext(Dispatchers.Main) {

                                                    if (resultado.status == 200) {

                                                        transferencias =
                                                            transferencias.filter {
                                                                it.DocEntry != transferencia.DocEntry
                                                            }

                                                        Toast.makeText(
                                                            context,
                                                            "Transferencia eliminada",
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

                        modoLectura = ModoLectura.ORIGEN

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
            title = { Text("Transferencia") },
            text = { Text(mensajeTransferencia) },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoTransferencia = false

                        // Opcional: limpiar pantalla después de procesar
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
