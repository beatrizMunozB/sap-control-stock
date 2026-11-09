package com.makita.controlstock.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.makita.controlstock.data.network.DetalleOrdenCompraResponse
import com.makita.controlstock.data.network.RetrofitClient.apiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import com.makita.controlstock.data.network.DetalleIngresoMercanciaSQLResponse
import com.makita.controlstock.data.network.ValidarArticuloIngresoMercanciaRequest
import com.makita.controlstock.data.network.ValidarBodegaIngresoMercanciaRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ButtonDefaults
import com.makita.controlstock.data.network.RegistrarCapturaIngresoMercanciaRequest
import com.makita.controlstock.session.Sesion
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.border


@Composable
fun LecturaIngresoMercanciaScreen(
    navController: NavHostController,
    idCabecera: Int,
    docEntrySAP: Int,
    docNum: Int
) {
    var bodega by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var codigoArticulo by remember { mutableStateOf("") }
    var cantidadRecibir by remember { mutableStateOf("") }
    var descripcionArticulo by remember { mutableStateOf("") }
    var itemScaneado by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    var manejaUbicacion by remember { mutableStateOf<Boolean?>(null) }
    var articuloValidado by remember { mutableStateOf(false) }
    var cargandoBodega by remember { mutableStateOf(true) }
    var validandoArticulo by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val cantidadFocusRequester = remember { FocusRequester() }
    val itemFocusRequester = remember { FocusRequester() }
    var idDetalleIngreso by remember { mutableStateOf<Int?>(null) }
    var lineaDetalleIngreso by remember { mutableStateOf<Int?>(null) }

    var cantidadFacturaArticulo by remember { mutableStateOf(0.0) }
    var cantidadCapturadaArticulo by remember { mutableStateOf(0.0) }
    var cantidadPendienteArticulo by remember { mutableStateOf(0.0) }
    var cantidadValidada by remember { mutableStateOf(false) }

    var tipoItem by remember { mutableStateOf("") }
    var registrandoCaptura by remember { mutableStateOf(false) }
    var mensajeCaptura by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    var mostrarAviso by remember { mutableStateOf(false) }
    var tituloAviso by remember { mutableStateOf("Aviso") }
    var mensajeAviso by remember { mutableStateOf("") }
    var alCerrarAviso by remember { mutableStateOf<() -> Unit>({}) }


    fun limpiarDatosArticulo(limpiarCodigo: Boolean = false) {
        if (limpiarCodigo) codigoArticulo = ""
        cantidadRecibir = ""
        descripcionArticulo = ""
        itemScaneado = ""
        articuloValidado = false
        cantidadValidada = false
        idDetalleIngreso = null
        lineaDetalleIngreso = null
        cantidadFacturaArticulo = 0.0
        cantidadCapturadaArticulo = 0.0
        cantidadPendienteArticulo = 0.0
        tipoItem = ""
        mensajeCaptura = ""
    }

    Log.d(
        "*MAKITA*",
        "INGRESA A CAPTURA: idCabecera=$idCabecera, " +
                "docEntrySAP=$docEntrySAP, docNum=$docNum"
    )

    // =========================================================
    // 1. OBTENER Y VALIDAR LA PRIMERA BODEGA
    // =========================================================
    LaunchedEffect(idCabecera) {
        cargandoBodega = true
        mensajeError = ""
        bodega = ""
        ubicacion = ""
        manejaUbicacion = null
        limpiarDatosArticulo(limpiarCodigo = true)

        try {
            val respuesta = withContext(Dispatchers.IO) {
                apiService.obtenerPrimeraBodegaIngresoMercancia(idCabecera)
            }

            if (!respuesta.success || respuesta.data == null) {
                mensajeError = respuesta.message
                    ?: "No se encontró una bodega para este ingreso."
                return@LaunchedEffect
            }

            val bodegaPropuesta = respuesta.data.whsCode.trim()

            Log.d(
                "*MAKITA*",
                "PRIMERA BODEGA: $bodegaPropuesta"
            )

            val validacionBodega = withContext(Dispatchers.IO) {
                apiService.validarBodegaIngresoMercancia(
                    ValidarBodegaIngresoMercanciaRequest(
                        idCabecera = idCabecera,
                        whsCode = bodegaPropuesta
                    )
                )
            }

            if (!validacionBodega.success || !validacionBodega.valida) {
                mensajeError = validacionBodega.message.ifBlank {
                    "La bodega no pertenece a este ingreso."
                }
                return@LaunchedEffect
            }

            val respuestaUbicacion = withContext(Dispatchers.IO) {
                apiService.validarManejaUbicacion(bodegaPropuesta)
            }

            Log.d(
                "*MAKITA*",
                "MANEJA UBICACIONES: $respuestaUbicacion"
            )

            val maneja = when (respuestaUbicacion.trim().uppercase()) {
                "SI" -> true
                "NO" -> false
                else -> null
            }

            if (maneja == null) {
                mensajeError =
                    "No fue posible determinar si la bodega maneja ubicaciones."
                return@LaunchedEffect
            }

            bodega = bodegaPropuesta
            manejaUbicacion = maneja

            Log.d(
                "*MAKITA*",
                "BODEGA VALIDADA: $bodega, " +
                        "MANEJA UBICACIONES: $maneja"
            )

        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e

        } catch (e: Exception) {
            Log.e(
                "*MAKITA*",
                "ERROR AL CARGAR LA BODEGA DEL INGRESO $idCabecera",
                e
            )

            mensajeError = "No fue posible cargar o validar la bodega."

        } finally {
            cargandoBodega = false
        }
    }

    // =========================================================
    // 2. ENFOCAR EL ESCÁNER CUANDO LA BODEGA NO USA UBICACIONES
    // =========================================================
    LaunchedEffect(cargandoBodega, manejaUbicacion, bodega) {
        if (
            !cargandoBodega &&
            manejaUbicacion == false &&
            bodega.isNotBlank()
        ) {
            delay(150)

            Log.d(
                "*MAKITA*",
                "BODEGA SIN UBICACIONES: ENFOCANDO ESCÁNER"
            )

            itemFocusRequester.requestFocus()
        }
    }

    // =========================================================
    // 3. VALIDAR AUTOMÁTICAMENTE EL ARTÍCULO ESCANEADO
    // =========================================================
    LaunchedEffect(codigoArticulo) {
        val lectura = codigoArticulo

        if (lectura.length < 20) {
            return@LaunchedEffect
        }

        if (
            cargandoBodega ||
            bodega.isBlank() ||
            manejaUbicacion == null ||
            (manejaUbicacion == true && ubicacion.isBlank())
        ) {
            return@LaunchedEffect
        }

        // Esperar a que el escáner termine de escribir.
        delay(300)

        if (codigoArticulo != lectura) {
            return@LaunchedEffect
        }

        val itemCode = lectura.substring(0, 20).trim()

        if (itemCode.isBlank()) {
            limpiarDatosArticulo(limpiarCodigo = true)
            mensajeError = "El código leído está vacío o no es válido."
            coroutineScope.launch {
                delay(150)
                itemFocusRequester.requestFocus()
            }
            return@LaunchedEffect
        }

        validandoArticulo = true
        limpiarDatosArticulo(limpiarCodigo = false)
        mensajeError = ""

        try {
            Log.d(
                "*MAKITA*",
                "VALIDANDO ARTÍCULO | Item=$itemCode | " +
                        "Bodega=$bodega | Cabecera=$idCabecera"
            )

            // 1. Validar que el artículo exista en el detalle del ingreso.
            val respuesta = withContext(Dispatchers.IO) {
                apiService.validarArticuloIngresoMercancia(
                    ValidarArticuloIngresoMercanciaRequest(
                        idCabecera = idCabecera,
                        whsCode = bodega,
                        itemCode = itemCode
                    )
                )
            }

            val detalle = respuesta.data


            Log.d(
                "*MAKITA*",
                "RESPUESTA VALIDAR ARTÍCULO | " +
                        "success=${respuesta.success} | " +
                        "valida=${respuesta.valida} | " +
                        "message=${respuesta.message} | " +
                        "detalle=$detalle"
            )

            if (!respuesta.success || !respuesta.valida || detalle == null) {

                keyboardController?.hide()
                mensajeError = ""
                mensajeError = respuesta.message.ifBlank {
                    "El item  no pertenece a esta factura de reserva o bodega."
                }

                tituloAviso = "Aviso"
                mensajeAviso = mensajeError

                alCerrarAviso = {
                    coroutineScope.launch {
                        delay(150)
                        itemFocusRequester.requestFocus()
                        delay(100)
                        keyboardController?.hide()
                    }
                }

                mostrarAviso = true



                Log.d(
                    "*MAKITA*",
                    "ITEM RECHAZADO | Item=$itemCode | Mensaje=$mensajeError"
                )

                limpiarDatosArticulo(limpiarCodigo = true)

                return@LaunchedEffect
            }

            val tipoConsultado = withContext(Dispatchers.IO) {
                apiService.consultarTipoItem(detalle.itemCode)
            }.trim().uppercase()

            if (tipoConsultado.isBlank()) {
                limpiarDatosArticulo(limpiarCodigo = true)
                mensajeError = "No se pudo determinar el tipo del item."
                coroutineScope.launch {
                    delay(150)
                    itemFocusRequester.requestFocus()
                }
                return@LaunchedEffect
            }

            // 3. Guardar una sola vez todos los datos del detalle validado.
            idDetalleIngreso = detalle.id
            lineaDetalleIngreso = detalle.lineNum
            itemScaneado = detalle.itemCode
            descripcionArticulo = detalle.itemName ?: ""
            tipoItem = tipoConsultado

            cantidadFacturaArticulo = detalle.cantidadFactura
            cantidadCapturadaArticulo = detalle.cantidadCapturada
            cantidadPendienteArticulo = detalle.cantidadPendiente

            // Marcar como válido únicamente después de completar ambas validaciones.
            articuloValidado = true
            cantidadValidada = false
            cantidadRecibir = ""

            Log.d(
                "*MAKITA*",
                "ARTÍCULO VALIDADO | " +
                        "IdDetalle=$idDetalleIngreso | " +
                        "Línea=$lineaDetalleIngreso | " +
                        "Item=$itemScaneado | " +
                        "Descripción=$descripcionArticulo | " +
                        "Bodega=$bodega | " +
                        "Tipo=$tipoItem | " +
                        "CantidadFactura=$cantidadFacturaArticulo | " +
                        "CantidadCapturada=$cantidadCapturadaArticulo | " +
                        "CantidadPendiente=$cantidadPendienteArticulo"
            )

            keyboardController?.hide()
            delay(100)
            cantidadFocusRequester.requestFocus()

        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e

        } catch (e: Exception) {
            Log.e(
                "*MAKITA*",
                "ERROR VALIDANDO ITEM: $itemCode",
                e
            )

            limpiarDatosArticulo(limpiarCodigo = true)
            mensajeError = "No fue posible validar el item."
            coroutineScope.launch {
                delay(150)
                itemFocusRequester.requestFocus()
            }

        } finally {
            validandoArticulo = false
        }
    }

    fun validarCantidadRecepcion(): Boolean {

        Log.d(
            "*MAKITA*",
            "VALIDANDO CANTIDAD | " +
                    "Item=$itemScaneado | " +
                    "IdDetalle=$idDetalleIngreso | " +
                    "Factura=$cantidadFacturaArticulo | " +
                    "Capturada=$cantidadCapturadaArticulo | " +
                    "Pendiente=$cantidadPendienteArticulo | " +
                    "Ingresada=$cantidadRecibir"
        )

        if (!articuloValidado || idDetalleIngreso == null) {
            mensajeError = "Primero debes validar el artículo."
            cantidadValidada = false
            return false
        }

        val cantidadIngresada = cantidadRecibir.toIntOrNull()

        if (cantidadIngresada == null || cantidadIngresada <= 0) {
            mensajeError = "Ingresa una cantidad mayor que cero."
            cantidadValidada = false
            return false
        }

        if (cantidadPendienteArticulo <= 0.0) {

            val pendienteActual = cantidadPendienteArticulo

            // SONIDO DE ALERTA: EL ARTÍCULO YA ESTÁ COMPLETO
            val toneGenerator = ToneGenerator(
                AudioManager.STREAM_ALARM,
                100
            )

            toneGenerator.startTone(
                ToneGenerator.TONE_CDMA_ABBR_ALERT,
                350
            )

            val handler = Handler(Looper.getMainLooper())

            handler.postDelayed({
                toneGenerator.startTone(
                    ToneGenerator.TONE_CDMA_ABBR_ALERT,
                    350
                )
            }, 400)

            handler.postDelayed({
                toneGenerator.release()
            }, 850)

            mensajeError =
                "El item ya completó la cantidad solicitada. " +
                        "Pendiente actual: ${pendienteActual.toInt()}."

            cantidadValidada = false

            Log.d(
                "*MAKITA*",
                "ARTÍCULO COMPLETADO | " +
                        "Item=$itemScaneado | " +
                        "IdDetalle=$idDetalleIngreso | " +
                        "Pendiente=$pendienteActual.toInt()"
            )

            // Limpiar los datos del artículo actual y el código escaneado.
            limpiarDatosArticulo(limpiarCodigo = true)

            // Ocultar el teclado y volver al escáner.
            keyboardController?.hide()

            coroutineScope.launch {
                delay(150)
                itemFocusRequester.requestFocus()
            }

            return false
        }

        if (cantidadIngresada.toDouble() > cantidadPendienteArticulo) {
            mensajeError =
                "La cantidad ingresada (${cantidadIngresada.toInt()}) supera " +
                        "la cantidad pendiente (${cantidadPendienteArticulo.toInt()})."

            cantidadValidada = false
            return false
        }

        cantidadValidada = true
        mensajeError = ""

        keyboardController?.hide()

        Log.d(
            "*MAKITA*",
            "CANTIDAD VÁLIDA | " +
                    "Item=$itemScaneado | " +
                    "Cantidad=$cantidadIngresada | " +
                    "Pendiente=$cantidadPendienteArticulo"
        )

        return true
    }

    // =========================================================
    // 4. INTERFAZ
    // =========================================================
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF2F2F2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()

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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
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

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "FACTURA DE RESERVA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Numero Documento: $docNum",
                            fontSize = 14.sp,
                            color = Color.White
                        )

                        Text(
                            text = "Ingreso: $idCabecera",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 4.dp
                ),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {



            // BODEGA PROPUESTA

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RectangleShape
                            )
                            .background(Color.White)
                    ) {
                        // FILA 1: TÍTULO GRIS
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE0E0E0))
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 8.dp
                                )
                        ) {
                            Text(
                                text = "INGRESE BODEGA",
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                fontWeight = FontWeight.SemiBold
                            )
                        }


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(Color.White)
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = bodega,
                                fontSize = 17.sp,
                                color = Color.Black
                            )
                        }
                    }


                    if (cargandoBodega) {
                Text(
                    text = "Cargando y validando bodega...",
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.DarkGray
                )
            }

            // ESTADO DE UBICACIONES
            when (manejaUbicacion) {
                true -> {
                    OutlinedTextField(
                        value = ubicacion,
                        onValueChange = {
                            ubicacion = it.trim()
                            limpiarDatosArticulo(limpiarCodigo = true)
                            mensajeError = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Ubicación")
                        },
                        singleLine = true,
                        enabled = !cargandoBodega
                    )
                }

                false -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 7.dp),
                        shape = RectangleShape,
                        color = Color(0xFF4FC3F7),
                        border = BorderStroke(
                            1.dp,
                            Color(0xFF039BE5)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFF4FC3F7),
                                shape = RectangleShape
                            ) {
                                Text(
                                    text = "BODEGA SIN UBICACIONES",
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 10.dp
                                    ),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                null -> {
                    Text(
                        text = if (cargandoBodega) {
                            "Esperando validación de la bodega..."
                        } else {
                            "No se pudo validar la bodega."
                        },
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ESCANEAR item


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray)
                            .background(Color.White)
                    ) {
                        // FILA 1: TÍTULO CON FONDO GRIS
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE0E0E0))
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 8.dp
                                )
                        ) {
                            Text(
                                text = "SCANEAR CODIGO",
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                fontWeight = FontWeight.SemiBold
                            )
                        }


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(Color.White)
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = codigoArticulo,
                                onValueChange = { newText ->
                                    codigoArticulo = newText
                                    keyboardController?.hide()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(itemFocusRequester),
                                singleLine = true,
                                enabled = !cargandoBodega &&
                                        !validandoArticulo &&
                                        bodega.isNotBlank() &&
                                        manejaUbicacion != null &&
                                        (manejaUbicacion == false || ubicacion.isNotBlank()),
                                textStyle = TextStyle(
                                    fontSize = 17.sp,
                                    color = Color.Black
                                ),
                                cursorBrush = SolidColor(Color(0xFF00695C))
                            )
                        }
                    }



                    if (validandoArticulo) {
                Text(
                    text = "Validando item...",
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.DarkGray
                )
            }

            // INFORMACIÓN DEL ARTÍCULO
            if (descripcionArticulo.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Item       : $itemScaneado",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Descripcion: $descripcionArticulo",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Cantidad factura: $cantidadFacturaArticulo",
                    fontSize = 14.sp
                )

                Text(
                    text = "Cantidad recibida: $cantidadCapturadaArticulo",
                    fontSize = 14.sp
                )

                Text(
                    text = "Pendiente por recibir: $cantidadPendienteArticulo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            //AQUI CANTIDAD
            // AQUÍ CANTIDAD

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RectangleShape
                            )
                            .background(Color.White)
                    ) {
                        // FILA 1: TÍTULO GRIS
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE0E0E0))
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 8.dp
                                )
                        ) {
                            Text(
                                text = "CANTIDAD",
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // FILA 2: INGRESO DE CANTIDAD
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(Color.White)
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = cantidadRecibir,
                                onValueChange = {
                                    cantidadRecibir = it.filter(Char::isDigit)
                                    cantidadValidada = false
                                    mensajeError = ""
                                    mensajeCaptura = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(cantidadFocusRequester),
                                singleLine = true,
                                enabled = articuloValidado,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        validarCantidadRecepcion()
                                    }
                                ),
                                textStyle = TextStyle(
                                    fontSize = 17.sp,
                                    color = Color.Black
                                ),
                                cursorBrush = SolidColor(Color(0xFF00695C))
                            )
                        }
                    }




                    if (articuloValidado) {

                //  BOTON REGISTRAR CAPTURA
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {

                            if (!validarCantidadRecepcion()) {
                                return@Button
                            }

                            if (
                                !tipoItem.trim().uppercase().contains("REPUESTOS") &&
                                !tipoItem.trim().uppercase().contains("ACCESORIOS")
                            ) {
                                mensajeError =
                                    "Este tipo de artículo requiere un flujo de registro específico: $tipoItem"
                                return@Button
                            }

                            val detalleId = idDetalleIngreso
                            val cantidad = cantidadRecibir.toIntOrNull()
                            val usuario = Sesion.usuario.trim()

                            if (detalleId == null) {
                                mensajeError = "No se encontró el detalle del ingreso."
                                return@Button
                            }

                            if (cantidad == null || cantidad <= 0) {
                                mensajeError = "Ingresa una cantidad válida."
                                return@Button
                            }

                            if (usuario.isBlank()) {
                                mensajeError = "No se encontró el usuario de la sesión."
                                return@Button
                            }

                            registrandoCaptura = true
                            mensajeError = ""
                            mensajeCaptura = ""

                            coroutineScope.launch {
                                try {
                                    val request = RegistrarCapturaIngresoMercanciaRequest(
                                        idDetalle = detalleId,
                                        numeroSerie = null,
                                        cantidad = cantidad,
                                        whsCode = bodega.trim(),
                                        barCode = if (manejaUbicacion == true) {
                                            ubicacion.trim().takeIf { it.isNotBlank() }
                                        } else {
                                            null
                                        },
                                        usuario = usuario
                                    )

                                    Log.d(
                                        "*MAKITA*",
                                        "ENVIANDO CAPTURA | " +
                                                "IdDetalle=$detalleId | " +
                                                "Item=$itemScaneado | " +
                                                "Cantidad=$cantidad | " +
                                                "Pendiente=$cantidadPendienteArticulo | " +
                                                "Bodega=$bodega | " +
                                                "BarCode=${request.barCode} | " +
                                                "Usuario=$usuario"
                                    )

                                    val respuesta = withContext(Dispatchers.IO) {
                                        apiService.registrarCapturaIngresoMercancia(request)
                                    }

                                    Log.d(
                                        "*MAKITA*",
                                        "RESPUESTA REGISTRO CAPTURA: $respuesta"
                                    )

                                    val data = respuesta.data

                                    if (respuesta.success && data != null) {

                                        // Actualizar cantidades con los valores del backend.
                                        cantidadCapturadaArticulo =
                                            data.cantidadCapturada.toDouble()

                                        cantidadPendienteArticulo =
                                            data.cantidadPendiente.toDouble()

                                        cantidadFacturaArticulo =
                                            cantidadCapturadaArticulo + cantidadPendienteArticulo

                                        mensajeCaptura = respuesta.message
                                            ?: "Captura registrada correctamente."

                                        mensajeError = ""

                                        Log.d(
                                            "*MAKITA*",
                                            "CAPTURA REGISTRADA | " +
                                                    "IdCaptura=${data.idCaptura} | " +
                                                    "Capturado=${data.cantidadCapturada} | " +
                                                    "Pendiente=${data.cantidadPendiente} | " +
                                                    "Estado=${data.estado}"
                                        )

                                        // Preparar la pantalla para otra lectura.
                                        codigoArticulo = ""
                                        itemScaneado = ""
                                        descripcionArticulo = ""
                                        cantidadRecibir = ""
                                        articuloValidado = false
                                        cantidadValidada = false

                                        idDetalleIngreso = null
                                        lineaDetalleIngreso = null
                                        tipoItem = ""

                                        delay(100)
                                        itemFocusRequester.requestFocus()

                                    } else {
                                        mensajeError = respuesta.message
                                            ?: "No fue posible registrar la captura."
                                    }

                                } catch (e: kotlinx.coroutines.CancellationException) {
                                    throw e

                                } catch (e: Exception) {
                                    Log.e(
                                        "*MAKITA*",
                                        "ERROR REGISTRANDO CAPTURA DE INGRESO",
                                        e
                                    )

                                    mensajeError =
                                        "Error al registrar la captura. Revisa Logcat y el backend."

                                } finally {
                                    registrandoCaptura = false
                                }
                            }
                        },
                        modifier = Modifier
                            .width(300.dp)
                            .height(50.dp),

                        shape = RectangleShape,

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4FC3F7),
                            contentColor = Color.White
                        ),
                        enabled = articuloValidado &&
                                cantidadRecibir.isNotBlank() &&
                                !registrandoCaptura
                    ) {
                        Text(
                            text = if (registrandoCaptura) {
                                "REGISTRANDO CAPTURA..."
                            } else {
                                "REGISTRAR CAPTURA"
                            }
                        )
                    }
                }
            } // Fin de if (articuloValidado)

            if (mensajeCaptura.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = mensajeCaptura,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Text(
                    text = "Total recibido: $cantidadCapturadaArticulo",
                    fontSize = 14.sp
                )

                Text(
                    text = "Pendiente: $cantidadPendienteArticulo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            // MENSAJE DE ERROR
            if (mensajeError.isNotBlank()) {

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF4FC3F7),
                    shape = RectangleShape
                ) {
                    Text(
                        text = mensajeError,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 10.dp
                        ),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

                } // Cierra la Column interior de la tarjeta
            } // Cierra ElevatedCard





        }
    }
}

@Composable
fun mostrarDialogo(
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

