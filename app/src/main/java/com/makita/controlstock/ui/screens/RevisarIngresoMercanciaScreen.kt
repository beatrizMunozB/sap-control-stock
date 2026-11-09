package com.makita.controlstock.ui.screens
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.RectangleShape
import com.makita.controlstock.data.network.IngresoCapturadoResponse
import com.makita.controlstock.data.network.RetrofitClient.apiService
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch



@Composable
fun RevisarIngresoMercanciaScreen(
    navController: NavHostController,
    docEntrySAP: Int,
    docNum: Int
) {
    var capturas by remember {
        mutableStateOf<List<IngresoCapturadoResponse>>(emptyList())
    }

    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }

    var capturaAEliminar by remember {
        mutableStateOf<IngresoCapturadoResponse?>(null)
    }

    var eliminandoCaptura by remember { mutableStateOf(false) }
    var mensajeDialogo by remember { mutableStateOf("") }
    var recargaCapturas by remember { mutableStateOf(0) }

    // ESTADOS PARA EL ENVÍO A SAP
    var enviandoASAP by remember { mutableStateOf(false) }
    var mostrarDialogoEnvioSAP by remember { mutableStateOf(false) }
    var envioSAPExitoso by remember { mutableStateOf(false) }
    var mensajeEnvioSAP by remember { mutableStateOf("") }
    var numeroEntradaSAP by remember { mutableStateOf<Int?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // =====================================================
    // 1. CONSULTAR CAPTURAS DE LA FACTURA
    // =====================================================
    LaunchedEffect(docEntrySAP, recargaCapturas) {
        cargando = true
        mensajeError = ""
        capturas = emptyList()

        try {
            Log.d(
                "*MAKITA*INGRESO*",
                "CONSULTANDO CAPTURAS | DocEntrySAP=$docEntrySAP"
            )

            val respuesta = withContext(Dispatchers.IO) {
                apiService.obtenerIngresosCapturados(docEntrySAP)
            }

            if (respuesta.success) {
                capturas = respuesta.data

                Log.d(
                    "*MAKITA*INGRESO*",
                    "CAPTURAS ENCONTRADAS: ${capturas.size}"
                )
            } else {
                mensajeError = respuesta.message
                    ?: "No fue posible consultar las capturas."
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                "*MAKITA*INGRESO*",
                "ERROR CONSULTANDO CAPTURAS",
                e
            )

            mensajeError = "Error al consultar los ingresos capturados."
        } finally {
            cargando = false
        }
    }

    // =====================================================
    // 2. ENVIAR INGRESO A SAP
    // =====================================================
    fun enviarIngresoASAP() {
        if (
            enviandoASAP ||
            cargando ||
            capturas.isEmpty() ||
            capturas.any { it.Procesado }
        ) {
            return
        }

        enviandoASAP = true
        mostrarDialogoEnvioSAP = false
        envioSAPExitoso = false
        mensajeEnvioSAP = ""
        numeroEntradaSAP = null

        coroutineScope.launch {
            try {
                Log.d(
                    "*MAKITA*INGRESO*",
                    "ENVIANDO INGRESO A SAP | DocEntrySAP=$docEntrySAP"
                )

                val respuesta = withContext(Dispatchers.IO) {
                    apiService.enviarIngresoMercanciaASAP(docEntrySAP)
                }

                if (respuesta.success) {
                    envioSAPExitoso = true
                    numeroEntradaSAP = respuesta.data?.docNumSAP

                    mensajeEnvioSAP = if (numeroEntradaSAP != null) {
                        "La entrada de mercancías se creó correctamente en SAP."
                    } else {
                        respuesta.message
                            ?: "El backend confirmó el envío a SAP."
                    }

                    Log.d(
                        "*MAKITA*INGRESO*",
                        "INGRESO ENVIADO A SAP | " +
                                "DocEntry reserva=$docEntrySAP | " +
                                "DocNum entrada=${numeroEntradaSAP ?: "No informado"}"
                    )

                    // Recargar las capturas. El backend debe haberlas marcado
                    // como procesadas después de crear correctamente el documento.
                    recargaCapturas++

                } else {
                    envioSAPExitoso = false
                    mensajeEnvioSAP = respuesta.message
                        ?: "No fue posible enviar el ingreso a SAP."
                }

                mostrarDialogoEnvioSAP = true

            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e

            } catch (e: retrofit2.HttpException) {
                val cuerpoError = try {
                    e.response()?.errorBody()?.string()
                } catch (_: Exception) {
                    null
                }

                val mensajeBackend = try {
                    org.json.JSONObject(cuerpoError ?: "")
                        .optString("message")
                        .takeIf { it.isNotBlank() && it != "null" }
                } catch (_: Exception) {
                    null
                }

                envioSAPExitoso = false
                mensajeEnvioSAP = mensajeBackend
                    ?: "Error HTTP ${e.code()} al enviar la entrada a SAP."

                Log.e(
                    "*MAKITA*INGRESO*",
                    "ERROR HTTP ENVIANDO INGRESO A SAP",
                    e
                )

                mostrarDialogoEnvioSAP = true

            } catch (e: Exception) {
                envioSAPExitoso = false
                mensajeEnvioSAP =
                    "No fue posible confirmar el envío a SAP. " +
                            "Verifica el estado del documento antes de volver a intentarlo."

                Log.e(
                    "*MAKITA*INGRESO*",
                    "ERROR ENVIANDO INGRESO A SAP",
                    e
                )

                mostrarDialogoEnvioSAP = true

            } finally {
                enviandoASAP = false
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF2F2F2),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),

        // BOTONES FIJOS INFERIORES
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
                        .padding(
                            horizontal = 8.dp,
                            vertical = 10.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // VOLVER
                    Button(
                        onClick = {
                            navController.popBackStack()
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
                            text = "VOLVER",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    // ENVIAR A SAP
                    Button(
                        onClick = {
                            enviarIngresoASAP()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RectangleShape,
                        enabled = !cargando &&
                                !enviandoASAP &&
                                capturas.isNotEmpty() &&
                                capturas.none { it.Procesado },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4FC3F7),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFBDBDBD),
                            disabledContentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (enviandoASAP) {
                                "ENVIANDO..."
                            } else {
                                "ENVIAR A SAP"
                            },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            softWrap = false
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
            // ENCABEZADO A TODO EL ANCHO
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
                        .padding(10.dp),
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
                            text = "REVISAR ITEMS CAPTURADOS",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Documento N°: $docNum",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // CONTENIDO PRINCIPAL
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 10.dp
                    )
            ) {
                Text(
                    text = "CAPTURAS REGISTRADAS: ${capturas.size}",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (cargando) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Consultando ingresos capturados...",
                            fontSize = 15.sp,
                            color = Color.DarkGray
                        )
                    }

                } else if (mensajeError.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = mensajeError,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF4FC3F7))
                                .padding(12.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                } else if (capturas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay ingresos capturados pendientes de procesar.",
                            fontSize = 15.sp,
                            color = Color.DarkGray
                        )
                    }

                } else {
                    // LISTA COMPACTA DE CAPTURAS
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(
                            items = capturas,
                            key = { it.IdCaptura }
                        ) { captura ->

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    // CÓDIGO Y DESCRIPCIÓN
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
                                            text = "${captura.ItemCode} - " +
                                                    "${captura.ItemName ?: ""}",
                                            fontSize = 12.sp,
                                            color = Color.Red,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // BODEGA | PEDIDO/CAPTURADO | ELIMINAR
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
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Text(
                                                text = captura.WhsCode
                                                    ?: captura.Bodega
                                                    ?: "",
                                                color = Color(0xFF1565C0),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        // PEDIDO / CAPTURADO
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
                                                        horizontal = 4.dp,
                                                        vertical = 5.dp
                                                    ),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "PEDIDO / CAPTURADO",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )

                                                Text(
                                                    text = "${captura.CantidadFactura.toInt()} / " +
                                                            "${captura.CantidadCapturada.toInt()}",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 17.sp,
                                                    maxLines = 1
                                                )
                                            }
                                        }

                                        // ELIMINAR CAPTURA
                                        IconButton(
                                            onClick = {
                                                capturaAEliminar = captura
                                                mensajeDialogo = ""
                                            },
                                            enabled = !eliminandoCaptura &&
                                                    !enviandoASAP &&
                                                    !captura.Procesado,
                                            modifier = Modifier.size(42.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar captura",
                                                tint = Color.Red
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Esta captura: ${captura.Cantidad}",
                                            modifier = Modifier.weight(1f),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = "Usuario: ${captura.Usuario}",
                                            fontSize = 11.sp,
                                            color = Color.DarkGray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    if (!captura.BarCode.isNullOrBlank()) {
                                        Text(
                                            text = "Ubicación: ${captura.BarCode}",
                                            fontSize = 11.sp
                                        )
                                    }

                                    if (!captura.NumeroSerie.isNullOrBlank()) {
                                        Text(
                                            text = "Serie: ${captura.NumeroSerie}",
                                            fontSize = 11.sp
                                        )
                                    }

                                    Text(
                                        text = "Fecha: ${
                                            captura.FechaCaptura
                                                .replace("T", " ")
                                                .take(19)
                                        }",
                                        fontSize = 10.sp,
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

    // =========================================================
    // 3. CONFIRMAR Y ELIMINAR CAPTURA
    // =========================================================
    if (capturaAEliminar != null) {
        val captura = capturaAEliminar!!

        AlertDialog(
            onDismissRequest = {
                if (!eliminandoCaptura) {
                    capturaAEliminar = null
                    mensajeDialogo = ""
                }
            },
            title = {
                Text(
                    text = "Eliminar captura",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "¿Deseas eliminar esta captura?"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${captura.ItemCode} - " +
                                "${captura.ItemName ?: ""}",
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Cantidad a devolver: ${captura.Cantidad}"
                    )

                    if (mensajeDialogo.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = mensajeDialogo,
                            color = Color.Red
                        )
                    }

                    if (eliminandoCaptura) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Eliminando captura..."
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !eliminandoCaptura,
                    onClick = {
                        if (eliminandoCaptura) {
                            return@TextButton
                        }

                        eliminandoCaptura = true
                        mensajeDialogo = ""

                        coroutineScope.launch {
                            try {
                                Log.d(
                                    "*MAKITA*INGRESO*",
                                    "ELIMINANDO CAPTURA | " +
                                            "IdCaptura=${captura.IdCaptura}"
                                )

                                val respuesta = withContext(Dispatchers.IO) {
                                    apiService.eliminarCapturaIngresoMercancia(
                                        captura.IdCaptura
                                    )
                                }

                                if (respuesta.success) {
                                    Log.d(
                                        "*MAKITA*INGRESO*",
                                        "CAPTURA ELIMINADA | " +
                                                "IdCaptura=${captura.IdCaptura} | " +
                                                "CantidadEliminada=" +
                                                "${respuesta.data?.cantidadEliminada}"
                                    )

                                    capturaAEliminar = null
                                    mensajeDialogo = ""

                                    // Volver a consultar las capturas.
                                    recargaCapturas++
                                } else {
                                    mensajeDialogo = respuesta.message
                                        ?: "No fue posible eliminar la captura."
                                }

                            } catch (e: kotlinx.coroutines.CancellationException) {
                                throw e

                            } catch (e: Exception) {
                                Log.e(
                                    "*MAKITA*INGRESO*",
                                    "ERROR ELIMINANDO CAPTURA " +
                                            captura.IdCaptura,
                                    e
                                )

                                mensajeDialogo =
                                    "No fue posible eliminar la captura. " +
                                            "Verifica la conexión y vuelve a intentarlo."

                            } finally {
                                eliminandoCaptura = false
                            }
                        }
                    }
                ) {
                    Text(
                        text = if (eliminandoCaptura) {
                            "ELIMINANDO..."
                        } else {
                            "ELIMINAR"
                        },
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !eliminandoCaptura,
                    onClick = {
                        capturaAEliminar = null
                        mensajeDialogo = ""
                    }
                ) {
                    Text("CANCELAR")
                }
            }
        )
    }

    // =========================================================
    // 4. RESULTADO DEL ENVÍO A SAP
    // =========================================================
    if (mostrarDialogoEnvioSAP) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoEnvioSAP = false
            },
            title = {
                Text(
                    text = if (envioSAPExitoso) {
                        "Entrada creada en SAP"
                    } else {
                        "Error al enviar a SAP"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(text = mensajeEnvioSAP)

                    if (envioSAPExitoso && numeroEntradaSAP != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Número de entrada SAP: $numeroEntradaSAP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF00695C)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEnvioSAP = false
                    }
                ) {
                    Text("ACEPTAR")
                }
            }
        )
    }
}
