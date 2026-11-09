package com.makita.controlstock.ui.screens
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import com.makita.controlstock.data.network.RetrofitClient.apiService
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.makita.controlstock.data.network.IniciarPickingLecturaRequest
import com.makita.controlstock.data.network.ItemNombreResponse
import com.makita.controlstock.data.network.ItemResponse
import com.makita.controlstock.data.network.RegistrarPickingCapturaRequest
import com.makita.controlstock.session.Sesion
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.media.MediaPlayer
import android.provider.Settings
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.style.TextOverflow
import com.makita.controlstock.data.network.CrearIngresoMercanciaCabeceraRequest
import com.makita.controlstock.data.network.CrearIngresoMercanciaDetalleRequest
import com.makita.controlstock.data.network.FacturaReservaDetalleResponse
import androidx.compose.material3.Scaffold


@Composable
fun DetalleFacturaReservaScreen(
    navController: NavHostController,
    docEntrySAP: Int,
    docNum: Int
) {
    var detalles by remember {
        mutableStateOf<List<FacturaReservaDetalleResponse>>(emptyList())
    }

    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var iniciandoLectura by remember { mutableStateOf(false) }
    var buscarItem by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(docEntrySAP) {
        try {
            cargando = true

            val respuesta = apiService.obtenerDetalleFacturaReserva(
                docEntrySAP = docEntrySAP
            )

            if (respuesta.success) {
                detalles = respuesta.data ?: emptyList()

                if (detalles.isEmpty()) {
                    mensajeError =
                        "La factura de reserva no tiene items pendientes de recibir"
                    showDialog = true
                }
            } else {
                mensajeError = "No fue posible obtener el detalle de la factura"
                showDialog = true
            }
        } catch (e: Exception) {
            Log.e(
                "MAKITA",
                "Error obteniendo detalle de factura de reserva",
                e
            )

            mensajeError = "Error al consultar el detalle: ${e.message}"
            showDialog = true
        } finally {
            cargando = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF2F2F2),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                color = Color(0xFFF2F2F2),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            navController.navigate(
                                "revisarIngresoMercancia/$docEntrySAP/$docNum"
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00695C),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "PROCESADOS",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = {
                            if (detalles.isEmpty() || iniciandoLectura) {
                                return@Button
                            }

                            iniciandoLectura = true
                            mensajeError = ""

                            coroutineScope.launch {
                                try {
                                    val respuestaCabecera =
                                        withContext(Dispatchers.IO) {
                                            apiService.crearIngresoMercanciaCabecera(
                                                CrearIngresoMercanciaCabeceraRequest(
                                                    docEntrySAP = docEntrySAP,
                                                    docNumSAP = docNum,
                                                    cardCode = detalles.first().CardCode,
                                                    cardName = detalles.first().CardName,
                                                    usuario = Sesion.usuario
                                                )
                                            )
                                        }

                                    Log.d(
                                        "MAKITA",
                                        "RESPUESTA CUANDO CREA CABECERA $respuestaCabecera"
                                    )

                                    if (!respuestaCabecera.success) {
                                        throw Exception(
                                            respuestaCabecera.message
                                                ?: "No fue posible crear la cabecera."
                                        )
                                    }

                                    val idCabecera =
                                        respuestaCabecera.data?.idCabecera
                                            ?: throw Exception(
                                                "No se recibió el identificador de la cabecera."
                                            )

                                    val respuestaDetalle =
                                        withContext(Dispatchers.IO) {
                                            apiService.crearIngresoMercanciaDetalle(
                                                CrearIngresoMercanciaDetalleRequest(
                                                    idCabecera = idCabecera,
                                                    detalles = detalles
                                                )
                                            )
                                        }

                                    if (!respuestaDetalle.success) {
                                        throw Exception(
                                            respuestaDetalle.message
                                                ?: "No fue posible guardar el detalle."
                                        )
                                    }

                                    navController.navigate(
                                        "lecturaIngresoMercancia/$idCabecera/$docEntrySAP/$docNum"
                                    )

                                    Log.i(
                                        "MAKITA",
                                        "Ingreso creado: idCabecera=$idCabecera, " +
                                                "DocEntrySAP=$docEntrySAP, DocNumSAP=$docNum"
                                    )
                                } catch (e: Exception) {
                                    Log.e(
                                        "MAKITA",
                                        "Error iniciando ingreso de mercancía",
                                        e
                                    )

                                    mensajeError =
                                        "No fue posible iniciar el ingreso: ${e.message}"
                                    showDialog = true
                                } finally {
                                    iniciandoLectura = false
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4FC3F7),
                            contentColor = Color.White
                        ),
                        enabled = detalles.isNotEmpty() &&
                                !cargando &&
                                !iniciandoLectura
                    ) {
                        Text(
                            text = if (iniciandoLectura) {
                                "INICIANDO..."
                            } else {
                                "INICIAR LECTURA"
                            },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // ENCABEZADO
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFF00695C)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
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
                            text = "FACTURA DE RESERVA $docNum",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (cargando) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cargando...",
                        fontSize = 16.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    // INFORMACIÓN DEL PROVEEDOR
                    if (detalles.isNotEmpty()) {
                        val factura = detalles.first()

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 6.dp
                                )
                            ) {
                                Text(
                                    text = factura.CardName?.take(40) ?: "",
                                    modifier = Modifier.fillMaxWidth(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = factura.CardCode ?: "",
                                        modifier = Modifier.weight(1f),
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "Ref.: ${factura.NumAtCard ?: ""}",
                                        modifier = Modifier.weight(1f),
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = factura.DocDate?.take(10) ?: "",
                                        modifier = Modifier.weight(1f),
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = factura.JrnlMemo ?: "",
                                        modifier = Modifier.weight(1f),
                                        fontSize = 11.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = buscarItem,
                            onValueChange = { buscarItem = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp),
                            placeholder = {
                                Text(
                                    "Busqueda por item",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar item"
                                )
                            },
                            trailingIcon = {
                                if (buscarItem.isNotEmpty()) {
                                    IconButton(
                                        onClick = { buscarItem = "" }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Limpiar búsqueda"
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 11.sp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "ITEMS PENDIENTES DE INGRESO",
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // LISTA DE ARTÍCULOS
                    val detallesFiltrados = detalles.filter { item ->
                        item.ItemCode.orEmpty().contains(
                            buscarItem.trim(),
                            ignoreCase = true
                        ) || item.Dscription.orEmpty().contains(
                            buscarItem.trim(),
                            ignoreCase = true
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(
                            items = detallesFiltrados,
                            key = { it.LineNum }
                        ) { item ->

                            // STOCK DE BOD01 POR ARTÍCULO
                            var stockBod01 by remember(item.ItemCode) {
                                mutableStateOf<Double?>(null)
                            }

                            LaunchedEffect(item.ItemCode) {
                                val itemCode = item.ItemCode.orEmpty()

                                if (itemCode.isNotBlank()) {
                                    try {
                                        val apiResponse = withContext(Dispatchers.IO) {
                                            apiService.obtenerStockItemBod01(itemCode)
                                        }

                                        val datos = apiResponse.data.orEmpty()
                                        stockBod01 = datos.firstOrNull()?.Stock ?: 0.0

                                    } catch (e: Exception) {
                                        Log.e(
                                            "MAKITA",
                                            "Error consultando stock BOD01 para $itemCode",
                                            e
                                        )
                                        stockBod01 = 0.0
                                    }
                                }
                            }

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFE0E0E0))
                                            .padding(
                                                horizontal = 6.dp,
                                                vertical = 3.dp
                                            )
                                    ) {
                                        Text(
                                            text = "${item.ItemCode ?: ""} - " +
                                                    "${item.Dscription?.take(25) ?: ""} - " +
                                                    "${item.ItmsGrpNam?.take(25) ?: ""}",
                                            fontSize = 11.sp,
                                            color = Color.Red,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // BODEGA
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(start = 2.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "BODEGA",
                                                color = Color.DarkGray,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Text(
                                                text = item.WhsCode ?: "",
                                                color = Color(0xFF1565C0),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                        }

                                        val cantidadPendiente =
                                            (item.Quantity ?: 0.0) -
                                                    (item.CantidadCapturada ?: 0.0)

                                        // INGRESO
                                        ElevatedCard(
                                            modifier = Modifier.weight(2f),
                                            shape = RectangleShape,
                                            elevation = CardDefaults.elevatedCardElevation(
                                                defaultElevation = 4.dp
                                            ),
                                            colors = CardDefaults.elevatedCardColors(
                                                containerColor = Color(0xFF00695C)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        horizontal = 5.dp,
                                                        vertical = 5.dp
                                                    ),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "INGRESO",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )

                                                Text(
                                                    text = "${item.Quantity?.toInt() ?: 0}",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 14.sp,
                                                    maxLines = 1
                                                )
                                            }
                                        }

                                        // CAPTURADO
                                        ElevatedCard(
                                            modifier = Modifier.weight(2f),
                                            shape = RectangleShape,
                                            elevation = CardDefaults.elevatedCardElevation(
                                                defaultElevation = 4.dp
                                            ),
                                            colors = CardDefaults.elevatedCardColors(
                                                containerColor = Color(0xFF1565C0)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        horizontal = 5.dp,
                                                        vertical = 5.dp
                                                    ),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "CAPTURADO",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )

                                                Text(
                                                    text = "${item.CantidadCapturada?.toInt() ?: 0}",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 14.sp,
                                                    maxLines = 1
                                                )
                                            }
                                        }

                                        // PENDIENTE
                                        ElevatedCard(
                                            modifier = Modifier.weight(2f),
                                            shape = RectangleShape,
                                            elevation = CardDefaults.elevatedCardElevation(
                                                defaultElevation = 4.dp
                                            ),
                                            colors = CardDefaults.elevatedCardColors(
                                                containerColor = Color(0xFF1565C0)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        horizontal = 5.dp,
                                                        vertical = 5.dp
                                                    ),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "PENDIENTE",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )

                                                Text(
                                                    text = "${cantidadPendiente.toInt()}",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 14.sp,
                                                    maxLines = 1
                                                )
                                            }
                                        }

                                        // STOCK BOD01
                                        ElevatedCard(
                                            modifier = Modifier.weight(2f),
                                            shape = RectangleShape,
                                            elevation = CardDefaults.elevatedCardElevation(
                                                defaultElevation = 4.dp
                                            ),
                                            colors = CardDefaults.elevatedCardColors(
                                                containerColor = Color(0xFF1565C0)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        horizontal = 5.dp,
                                                        vertical = 5.dp
                                                    ),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "STOCK",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )

                                                Text(
                                                    text = stockBod01?.toInt()?.toString() ?: "...",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 14.sp,
                                                    maxLines = 1
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
        }
    }

    if (showDialog) {
        mostrarDialogo20(
            titulo = "Aviso!",
            mensaje = mensajeError,
            onDismiss = {
                showDialog = false
            }
        )
    }
}


