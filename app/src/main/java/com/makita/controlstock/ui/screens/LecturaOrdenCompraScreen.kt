package com.makita.controlstock.ui.screens
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Button
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.KeyboardType
import com.makita.controlstock.data.network.RegistrarCapturaOrdenCompraRequest
import kotlinx.coroutines.launch


@Composable


fun LecturaOrdenCompraScreen(
    navController: NavHostController,
    docEntry: Int,
    docNum: Int
) {
    var bodega by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var codigoArticulo by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    var detalles by remember { mutableStateOf<List<DetalleOrdenCompraResponse>>(emptyList()) }
    var cardCode by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }
    var descripcionArticulo by remember { mutableStateOf("") }
    var cantidadRecibir by remember { mutableStateOf("") }
    var detalleSeleccionado by remember { mutableStateOf<DetalleOrdenCompraResponse?>(null) }
    var cantidadValidada by remember { mutableStateOf(false) }
    var manejaUbicacion by remember { mutableStateOf<Boolean?>(null) }
    var MuestraMensaje by remember { mutableStateOf(false) }
    val bodegaFocusRequester = remember { FocusRequester() }
    val ubicacionFocusRequester = remember { FocusRequester() }
    val itemFocusRequester = remember { FocusRequester() }
    var extractedText    by remember { mutableStateOf("") }
    var extractedText2   by remember { mutableStateOf("") }
    var extractedText3   by remember { mutableStateOf("") }
    var extractedText4   by remember { mutableStateOf("") }
    var registrandoCaptura by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Consultando el detalle del pedido

    LaunchedEffect(docEntry) {
        cargando = true
        mensajeError = ""

        try {
            val resultado = withContext(Dispatchers.IO) {
                apiService.obtenerDetalleOrdenCompra(docEntry = docEntry)
            }

            if (resultado.success && !resultado.data.isNullOrEmpty()) {
                detalles = resultado.data
                cardCode = resultado.data.first().CardCode ?: ""
                cardName = resultado.data.first().CardName ?: ""

                bodega = resultado.data.firstNotNullOfOrNull {
                    it.Bodega?.takeIf(String::isNotBlank)
                } ?: ""

            } else {
                detalles = emptyList()
                mensajeError = "La orden de compra no tiene líneas disponibles."
            }

        } catch (e: Exception) {
            Log.e(
                "*MAKITA*",
                "Error consultando orden de compra $docEntry",
                e
            )
            mensajeError = "Error al consultar la orden de compra."
        } finally {
            cargando = false
        }
    }

    // CONSULTAR SI LA BODEGA MANEJA UBICACIONES
    LaunchedEffect(bodega, cargando) {
        manejaUbicacion = null
        ubicacion = ""

        if (bodega.isBlank() || cargando) {
            return@LaunchedEffect
        }

        try {

            val respuesta = withContext(Dispatchers.IO) {
                apiService.validarManejaUbicacion(bodega.trim())
            }

            manejaUbicacion = when (respuesta.trim().uppercase()) {
                "SI" -> true
                "NO" -> false
                else -> null
            }

            Log.i(
                "*MAKITA*",
                "Bodega=$bodega, manejaUbicacion=$respuesta"
            )

            if (manejaUbicacion == null) {
                mensajeError =
                    "Respuesta inválida al consultar las ubicaciones de la bodega."
            } else {
                mensajeError = ""
            }

        } catch (e: Exception) {
            manejaUbicacion = null

            Log.e(
                "*MAKITA*",
                "Error validando ubicaciones para la bodega $bodega",
                e
            )

            mensajeError =
                "No fue posible validar la bodega. Intenta nuevamente."
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF2F2F2)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
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
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 24.dp,
                            bottom = 16.dp
                        ),
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
                        text = "LECTURA ORDEN COMPRA (Pedido)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 5.dp
                    ),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Código: ${
                                if (cardCode.isNotBlank()) cardCode else "-"
                            }",
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Nro Orden $docNum",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Proveedor: ${
                                if (cardName.isNotBlank()) cardName
                                else if (cargando) "Cargando..."
                                else "-"
                            }",
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (mensajeError.isNotBlank()) {
                    Text(
                        text = mensajeError,
                        color = Color.Red,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // BODEGA SELECCIONADA
                OutlinedTextField(
                    value = bodega,
                    onValueChange = {
                        bodega = it.trim()
                        codigoArticulo = ""
                        descripcionArticulo = ""
                        detalleSeleccionado = null
                        cantidadRecibir = ""
                        cantidadValidada = false
                        ubicacion = ""
                        mensajeError = ""
                    },
                    label = {
                        Text("Bodega")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(bodegaFocusRequester),
                    singleLine = true,
                    enabled = !cargando && detalles.isNotEmpty()
                )

                if (detalles.isNotEmpty()) {
                    val bodegasDisponibles = detalles
                        .mapNotNull {
                            it.Bodega?.takeIf(String::isNotBlank)
                        }
                        .distinct()

                    Text(
                        text = "Bodegas de la orden: ${
                            bodegasDisponibles.joinToString(", ")
                        }",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // UBICACION: SOLO SI LA BODEGA LA MANEJA
                when (manejaUbicacion) {
                    true -> {
                        OutlinedTextField(
                            value = ubicacion,
                            onValueChange = { nuevoTexto ->
                                ubicacion = nuevoTexto
                                    .replace("\n", "")
                                    .replace("\r", "")
                                    .replace("\t", "")
                                    .trim()
                            },
                            label = {
                                Text("Ubicación")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(ubicacionFocusRequester),
                            singleLine = true,
                            enabled = !cargando
                        )
                    }

                    false -> {
                        Text(
                            text = "Esta bodega no maneja ubicaciones.",
                            color = Color.DarkGray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    null -> {
                        Text(
                            text = if (bodega.isBlank() || cargando) {
                                "Selecciona una bodega."
                            } else {
                                "Consultando si la bodega maneja ubicaciones..."
                            },
                            color = Color.DarkGray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ESCANEAR Y VALIDAR ARTICULO
                OutlinedTextField(
                    value = codigoArticulo,
                    onValueChange = { newText ->

                        codigoArticulo = newText
                        descripcionArticulo = ""
                        detalleSeleccionado = null
                        cantidadRecibir = ""
                        cantidadValidada = false
                        mensajeError = ""

                        if (newText.isNotBlank()) {

                            // VALIDAR LARGO MINIMO DE LA ETIQUETA
                            if (newText.length < 20) {

                                Log.d(
                                    "*MAKITA*RECEPCION*",
                                    "LECTURA MENOR A 20: [$newText] - LARGO: ${newText.length}"
                                )

                                mensajeError =
                                    "Item incorrecto. Largo de etiqueta: ${newText.length}. " +
                                            "Escanee la etiqueta correcta."

                                codigoArticulo = ""
                                MuestraMensaje = true

                                return@OutlinedTextField
                            }

                            // EXTRAER ITEMCODE DE LOS PRIMEROS 20 CARACTERES
                            val codigo = newText.substring(0, 20).trim()

                            Log.d(
                                "*MAKITA*RECEPCION*",
                                "ETIQUETA ESCANEADA=[$newText], " +
                                        "ITEMCODE EXTRAIDO=[$codigo], " +
                                        "LARGO=${newText.length}"
                            )

                            // VALIDAR ARTICULO CONTRA LA ORDEN Y LA BODEGA
                            val detalleCorrecto = detalles.firstOrNull {
                                it.ItemCode?.trim()?.equals(
                                    codigo,
                                    ignoreCase = true
                                ) == true &&
                                        it.Bodega?.trim()?.equals(
                                            bodega.trim(),
                                            ignoreCase = true
                                        ) == true &&
                                        it.PendienteOC > 0
                            }

                            if (detalleCorrecto != null) {

                                // ARTICULO VALIDO
                                detalleSeleccionado = detalleCorrecto
                                descripcionArticulo = detalleCorrecto.Dscription ?: ""
                                mensajeError = ""

                                Log.d(
                                    "*MAKITA*RECEPCION*",
                                    "ARTICULO VALIDO: ${detalleCorrecto.ItemCode}, " +
                                            "BODEGA=${detalleCorrecto.Bodega}, " +
                                            "PENDIENTE=${detalleCorrecto.PendienteOC}"
                                )

                                keyboardController?.hide()

                            } else {

                                // VERIFICAR SI EL ARTICULO PERTENECE A OTRA BODEGA
                                val articuloOtraBodega = detalles.firstOrNull {
                                    it.ItemCode?.trim().equals(
                                        codigo,
                                        ignoreCase = true
                                    ) &&
                                            it.PendienteOC > 0
                                }

                                if (articuloOtraBodega != null) {

                                    mensajeError =
                                        "El artículo corresponde a la bodega " +
                                                "${articuloOtraBodega.Bodega}, " +
                                                "no a la bodega $bodega."

                                } else {

                                    mensajeError =
                                        "El artículo $codigo no corresponde a un artículo " +
                                                "pendiente de esta orden de compra."
                                }

                                codigoArticulo = ""
                                detalleSeleccionado = null
                                descripcionArticulo = ""
                                cantidadRecibir = ""
                                cantidadValidada = false

                                MuestraMensaje = true

                                Log.d(
                                    "*MAKITA*RECEPCION*",
                                    "ARTICULO RECHAZADO: $codigo"
                                )
                            }
                        }
                    },
                    label = {
                        Text("Escanear código del item")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !cargando && detalles.isNotEmpty()
                )

                // RESULTADO DEL ARTICULO Y CANTIDAD A RECIBIR
                if (detalleSeleccionado != null) {

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Artículo correcto",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Text(
                        text = "Código: $codigoArticulo",
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Descripción: $descripcionArticulo",
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Bodega: $bodega",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Cantidad OC: ${detalleSeleccionado?.CantidadOC ?: 0}",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )

                    Text(
                        text = "Cantidad pendiente: ${
                            detalleSeleccionado?.PendienteOC ?: 0
                        }",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // CANTIDAD A RECIBIR
                    OutlinedTextField(
                        value = cantidadRecibir,
                        onValueChange = { nuevoTexto ->
                            cantidadRecibir = nuevoTexto.filter {
                                it.isDigit()
                            }
                            cantidadValidada = false
                            mensajeError = ""
                        },
                        label = {
                            Text("Cantidad a recibir")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        enabled = !cantidadValidada
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // VALIDAR CANTIDAD
                    Button(
                        onClick = {
                            val detalle = detalleSeleccionado
                            val cantidad = cantidadRecibir.toIntOrNull()

                            when {
                                detalle == null -> {
                                    mensajeError =
                                        "Primero debes escanear un artículo válido."
                                }

                                cantidad == null || cantidad <= 0 -> {
                                    mensajeError =
                                        "Ingresa una cantidad mayor que cero."
                                }

                                cantidad.toDouble() > detalle.PendienteOC.toDouble() -> {
                                    mensajeError =
                                        "La cantidad ingresada supera la cantidad pendiente."
                                }

                                else -> {
                                    cantidadValidada = true
                                    mensajeError = ""

                                    Log.i(
                                        "*MAKITA*",
                                        "Cantidad validada: " +
                                                "DocEntry=$docEntry, " +
                                                "DocNum=$docNum, " +
                                                "ItemCode=${detalle.ItemCode}, " +
                                                "Bodega=${detalle.Bodega}, " +
                                                "CantidadOC=${detalle.CantidadOC}, " +
                                                "Pendiente=${detalle.PendienteOC}, " +
                                                "CantidadRecibir=$cantidad"
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = detalleSeleccionado != null &&
                                cantidadRecibir.isNotBlank() &&
                                !cantidadValidada
                    ) {
                        Text("VALIDAR CANTIDAD")
                    }


                    if (cantidadValidada) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Cantidad validada correctamente.",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))


                    }

                }
            }
        }

        if (MuestraMensaje) {
            mostrarDialogo25(
                titulo = "Aviso!",
                mensaje = mensajeError,
                onDismiss = {
                    MuestraMensaje = false
                    bodegaFocusRequester.requestFocus()
                }
            )
        }
    }
}


@Composable
fun mostrarDialogo25(
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


