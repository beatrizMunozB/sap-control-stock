package com.makita.controlstock.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.util.Log
import androidx.compose.foundation.background
import com.makita.controlstock.data.network.ComprasRecepcionDetalle
import com.makita.controlstock.data.network.RetrofitClient.apiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.makita.controlstock.data.network.ConfirmarUbicacionPosicionamientoRecepcionRequest
import com.makita.controlstock.data.network.IniciarPosicionamientoRecepcionRequest
import com.makita.controlstock.data.network.PosicionamientoRecepcionDetalle
import com.makita.controlstock.data.network.RegistrarPosicionamientoRecepcionRequest
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect


@Composable
fun LecturaPosicionamientoRecepcionScreen(
    navController: NavController,
    idCabecera: Int
) {
    var detalles by remember {
        mutableStateOf<List<PosicionamientoRecepcionDetalle>>(emptyList())
    }

    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf("") }

    var codigoArticulo by remember { mutableStateOf("") }
    var cantidadCapturar by remember { mutableStateOf("") }
    var codigoUbicacion by remember { mutableStateOf("") }

    var articuloSeleccionado by remember {
        mutableStateOf<PosicionamientoRecepcionDetalle?>(null)
    }

    var registrandoCaptura by remember { mutableStateOf(false) }
    var esperandoUbicacion by remember { mutableStateOf(false) }
    var validandoUbicacion by remember { mutableStateOf(false) }

    val focusRequesterArticulo = remember { FocusRequester() }
    val focusRequesterCantidad = remember { FocusRequester() }
    val focusRequesterUbicacion = remember { FocusRequester() }

    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    // ------------------------------------------------
    // VALIDAR ARTICULO
    // ------------------------------------------------

    fun validarArticulo(codigo: String) {

        if (esperandoUbicacion) return

        val itemCode = codigo.trim()

        if (itemCode.isEmpty()) return

        val articulo = detalles.find {
            it.ItemCode.trim().equals(itemCode, ignoreCase = true)
        }

        if (articulo == null) {
            articuloSeleccionado = null
            mensajeError = "El artículo $itemCode no pertenece a esta recepción."
            codigoArticulo = ""
            keyboardController?.hide()
            return
        }

        val pendiente =
            articulo.CantidadPendiente - articulo.CantidadCapturada

        if (pendiente <= 0) {
            articuloSeleccionado = null
            mensajeError = "El artículo $itemCode ya no tiene cantidad pendiente."
            codigoArticulo = ""
            keyboardController?.hide()
            return
        }

        articuloSeleccionado = articulo
        mensajeError = ""
        mensajeExito = ""
        codigoArticulo = ""
        cantidadCapturar = ""

        keyboardController?.hide()
    }

    // ------------------------------------------------
    // PROCESAR LECTURA
    // ------------------------------------------------

    fun procesarLecturaArticulo(codigo: String, esEscaner: Boolean) {

        val lectura = codigo.trim()

        if (lectura.isBlank()) return

        val itemCode = if (esEscaner) {
            if (lectura.length < 20) {
                mensajeError = "La etiqueta escaneada no contiene los 20 caracteres esperados."
                codigoArticulo = ""
                return
            }

            lectura.substring(0, 20).trim()
        } else {
            lectura
        }

        Log.d(
            "POSICIONAMIENTO",
            "LECTURA: $lectura | ITEM EXTRAIDO: $itemCode"
        )

        validarArticulo(itemCode)
    }

    // ------------------------------------------------
    // CONFIRMAR CAPTURA
    // ------------------------------------------------

    fun confirmarCaptura() {

        val articulo = articuloSeleccionado ?: return

        if (registrandoCaptura || esperandoUbicacion) return

        val cantidad = cantidadCapturar
            .replace(",", ".")
            .toDoubleOrNull()

        if (cantidad == null || cantidad <= 0) {
            mensajeError = "Ingrese una cantidad válida."
            return
        }

        val pendiente =
            articulo.CantidadPendiente - articulo.CantidadCapturada

        if (cantidad > pendiente) {
            mensajeError = "La cantidad supera lo pendiente ($pendiente)."
            return
        }

        scope.launch {
            try {
                registrandoCaptura = true
                mensajeError = ""
                mensajeExito = ""

                keyboardController?.hide()

                val respuesta = withContext(Dispatchers.IO) {
                    apiService.registrarPosicionamientoRecepcion(
                        RegistrarPosicionamientoRecepcionRequest(
                            idDetalle = articulo.Id,
                            itemCode = articulo.ItemCode,
                            codigoLeido = articulo.ItemCode,
                            cantidad = cantidad,
                            usuario = null
                        )
                    )
                }

                if (respuesta.success && respuesta.data != null) {

                    val resultado = respuesta.data

                    val detalleActualizado = detalles.map { item ->
                        if (item.Id == resultado.idDetalle) {
                            item.copy(
                                CantidadCapturada = resultado.cantidadCapturada,
                                Estado = resultado.estado
                            )
                        } else {
                            item
                        }
                    }

                    detalles = detalleActualizado

                    articuloSeleccionado = detalleActualizado.find {
                        it.Id == resultado.idDetalle
                    }

                    mensajeExito = respuesta.message

                    cantidadCapturar = ""
                    codigoUbicacion = ""

                    esperandoUbicacion = true

                } else {
                    mensajeError = respuesta.message
                }

            } catch (e: Exception) {
                mensajeError = "Error al registrar la captura: ${e.message}"

            } finally {
                registrandoCaptura = false
            }
        }
    }

    // ------------------------------------------------
    // VALIDAR UBICACION
    // ------------------------------------------------

    fun validarUbicacion(ubicacion: String) {

        val articulo = articuloSeleccionado ?: return

        if (!esperandoUbicacion || validandoUbicacion) return

        val ubicacionLimpia = ubicacion.trim()

        if (ubicacionLimpia.isEmpty()) {
            mensajeError = "Debe escanear una ubicación."
            return
        }

        scope.launch {
            try {
                validandoUbicacion = true
                mensajeError = ""
                mensajeExito = ""

                keyboardController?.hide()

                val respuesta = withContext(Dispatchers.IO) {
                    apiService.confirmarUbicacionPosicionamientoRecepcion(
                        ConfirmarUbicacionPosicionamientoRecepcionRequest(
                            idDetalle = articulo.Id,
                            ubicacion = ubicacionLimpia
                        )
                    )
                }

                if (respuesta.success && respuesta.data != null) {

                    val resultado = respuesta.data

                    detalles = detalles.map { item ->
                        if (item.Id == resultado.idDetalle) {
                            item.copy(
                                BinCode = resultado.binCode,
                                BinCodeEntry = resultado.binCodeEntry
                            )
                        } else {
                            item
                        }
                    }

                    mensajeExito = respuesta.message

                    articuloSeleccionado = null
                    codigoArticulo = ""
                    codigoUbicacion = ""
                    cantidadCapturar = ""

                    esperandoUbicacion = false

                } else {
                    mensajeError = respuesta.message
                    codigoUbicacion = ""
                }

            } catch (e: Exception) {
                mensajeError = "Error al validar ubicación: ${e.message}"
                codigoUbicacion = ""

            } finally {
                validandoUbicacion = false
            }
        }
    }

    // ------------------------------------------------
    // CARGAR DETALLE
    // ------------------------------------------------

    LaunchedEffect(idCabecera) {
        try {
            cargando = true
            mensajeError = ""

            val respuesta = withContext(Dispatchers.IO) {
                apiService.obtenerPosicionamientoRecepcionDetalle(idCabecera)
            }

            if (respuesta.success) {
                detalles = respuesta.data
            } else {
                detalles = emptyList()
                mensajeError = "No se pudo obtener el detalle."
            }

        } catch (e: Exception) {
            detalles = emptyList()
            mensajeError = "Error al cargar el detalle: ${e.message}"

        } finally {
            cargando = false
        }
    }

    // ------------------------------------------------
    // CONTROLAR FOCO
    // ------------------------------------------------

    LaunchedEffect(
        cargando,
        articuloSeleccionado,
        esperandoUbicacion,
        detalles.isNotEmpty()
    ) {
        if (!cargando && detalles.isNotEmpty()) {

            when {
                esperandoUbicacion && articuloSeleccionado != null -> {
                    focusRequesterUbicacion.requestFocus()
                }

                articuloSeleccionado != null -> {
                    focusRequesterCantidad.requestFocus()
                }

                else -> {
                    focusRequesterArticulo.requestFocus()
                }
            }
        }
    }

    // ------------------------------------------------
    // DETECTAR LECTURA DEL ESCANER
    // ------------------------------------------------

    LaunchedEffect(codigoArticulo) {

        if (
            codigoArticulo.length >= 20 &&
            !esperandoUbicacion &&
            !cargando &&
            articuloSeleccionado == null
        ) {
            delay(300)

            val etiqueta = codigoArticulo

            // Verifica que el texto no haya cambiado durante la espera.
            if (
                etiqueta.length >= 20 &&
                codigoArticulo == etiqueta
            ) {
                procesarLecturaArticulo(
                    codigo = etiqueta,
                    esEscaner = true
                )
            }
        }
    }

    // ------------------------------------------------
    // INTERFAZ
    // ------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ENCABEZADO

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Lectura Posicionamiento",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Cabecera: $idCabecera",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // LECTURA ARTICULO

        OutlinedTextField(
            value = codigoArticulo,
            onValueChange = { nuevoTexto ->
                if (!esperandoUbicacion && !cargando) {
                    codigoArticulo = nuevoTexto
                    mensajeError = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequesterArticulo),
            label = {
                Text("Item")
            },
            placeholder = {
                Text("Escanee etiqueta o digite el item")
            },
            enabled = !esperandoUbicacion && !cargando,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // El ingreso manual utiliza el código completo.
                    // Las etiquetas del escáner se procesan en el LaunchedEffect.
                    if (
                        codigoArticulo.isNotBlank() &&
                        codigoArticulo.length < 20
                    ) {
                        procesarLecturaArticulo(
                            codigo = codigoArticulo,
                            esEscaner = false
                        )
                    }
                }
            )
        )

        // ARTICULO SELECCIONADO

        articuloSeleccionado?.let { item ->

            Spacer(modifier = Modifier.height(12.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp
                ),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White
                )
            ) {
                Column {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE0E0E0))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${item.ItemCode} - ${item.Dscription ?: "Sin Descripcion"}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1
                        )
                    }

                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = "Bodega origen: ${item.BodegaOrigen}",
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Bodega destino: ${item.BatToWarehouse ?: "SIN BODEGA"}",
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Cantidad pendiente: ${
                                item.CantidadPendiente - item.CantidadCapturada
                            }",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00695C)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Cantidad capturada: ${item.CantidadCapturada}",
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Ubicación: ${item.BinCode ?: "SIN UBICACIÓN"}",
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Estado: ${item.Estado}",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // CAPTURA DE CANTIDAD

            if (!esperandoUbicacion) {

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = cantidadCapturar,
                    onValueChange = { nuevoTexto ->
                        cantidadCapturar = nuevoTexto.filter {
                            it.isDigit() || it == '.' || it == ','
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterCantidad),
                    label = {
                        Text("Cantidad a posicionar")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            confirmarCaptura()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        confirmarCaptura()
                    },
                    enabled = !registrandoCaptura &&
                            cantidadCapturar.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4FC3F7),
                        contentColor = Color.Black
                    ),
                    shape = RectangleShape
                ) {
                    if (registrandoCaptura) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black
                        )
                    } else {
                        Text(
                            text = "CONFIRMAR CAPTURA",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // LECTURA UBICACION

            if (esperandoUbicacion) {

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ESCANEAR UBICACION DE DESTINO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Bodega destino: ${item.BatToWarehouse ?: ""}",
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = codigoUbicacion,
                    onValueChange = { nuevoTexto ->
                        codigoUbicacion = nuevoTexto
                        mensajeError = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterUbicacion),
                    label = {
                        Text("Ubicacion")
                    },
                    enabled = !validandoUbicacion,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            validarUbicacion(codigoUbicacion)
                        }
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        validarUbicacion(codigoUbicacion)
                    },
                    enabled = !validandoUbicacion &&
                            codigoUbicacion.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4FC3F7),
                        contentColor = Color.Black
                    ),
                    shape = RectangleShape
                ) {
                    if (validandoUbicacion) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black
                        )
                    } else {
                        Text(
                            text = "CONFIRMAR UBICACIÓN",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // MENSAJES

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (mensajeExito.isNotEmpty()) {
            Text(
                text = mensajeExito,
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // LISTADO DE DETALLES

        when {

            cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            detalles.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No existen items pendientes.")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(detalles) { item ->

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RectangleShape,
                            elevation = CardDefaults.elevatedCardElevation(
                                defaultElevation = 8.dp
                            ),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Column {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFE0E0E0))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${item.ItemCode} - ${(item.Dscription ?: "Sin Descripcion").take(25)}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        maxLines = 1
                                    )
                                }

                                Column(
                                    modifier = Modifier.padding(14.dp)
                                ) {
                                    Text(
                                        text = "Bodega origen: ${item.BodegaOrigen}",
                                        fontSize = 13.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Bodega destino: ${item.BatToWarehouse ?: "SIN BODEGA"}",
                                        fontSize = 13.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Pendiente: ${item.CantidadPendiente - item.CantidadCapturada}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00695C)
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Capturado: ${item.CantidadCapturada}",
                                        fontSize = 13.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Ubicación: ${item.BinCode ?: "SIN UBICACIÓN"}",
                                        fontSize = 13.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Estado: ${item.Estado}",
                                        fontSize = 13.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

