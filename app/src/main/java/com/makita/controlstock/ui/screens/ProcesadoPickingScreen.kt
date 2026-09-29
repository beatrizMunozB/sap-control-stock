package com.makita.controlstock.ui.screens


import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.os.CancellationSignal
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import com.makita.controlstock.data.network.SolicitudDetalleItem
import androidx.compose.runtime.mutableStateOf
import com.makita.controlstock.data.network.RetrofitClient.apiService

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.makita.controlstock.data.network.IniciarSolicitudLecturaRequest
import com.makita.controlstock.session.Sesion.usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.makita.controlstock.data.network.PickingDetalleResponse
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.RectangleShape
import com.makita.controlstock.data.network.EnviarDetalleASAPRequest
import com.makita.controlstock.data.network.IniciarPickingLecturaRequest
import com.makita.controlstock.data.network.PickingCapturaResponse
import com.makita.controlstock.data.network.PickingDetalleUbicacionResponse
import com.makita.controlstock.session.Sesion
import kotlinx.coroutines.launch
import android.print.PrintManager

import android.os.ParcelFileDescriptor
import android.print.PageRange

import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import java.io.FileOutputStream
import java.net.URL
import java.net.HttpURLConnection


import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File



@Composable
fun ProcesadoPickingScreen(
    idCabecera: Int,
    absEntry: Int,
    navController: NavHostController
) {

    var capturas by remember {
        mutableStateOf<List<PickingCapturaResponse>>(emptyList())
    }

    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var mensajeError2 by remember { mutableStateOf("") }

    var showDialog7 by remember { mutableStateOf(false) }

    var showDialog8 by remember { mutableStateOf(false) }
    var mensajeError8 by remember { mutableStateOf("") }


    LaunchedEffect(idCabecera) {

        try {

            val resultado =
                apiService.obtenerPickingProcesado(idCabecera)

            if (resultado.success) {
                capturas = resultado.data
            }

        } catch (e: Exception) {

            Log.e(
                "*MAKITA*PICKING*",
                "ERROR OBTENIENDO CAPTURAS DEL PICKING",
                e
            )
        }
    }


    val puedeEnviarASAP =
        capturas.isNotEmpty() &&
                capturas.all {
                    it.cantidad > 0
                }


    val procesarEnvioASAP: (Boolean) -> Unit = { envioParcialAutorizado ->

        CoroutineScope(
            Dispatchers.Main
        ).launch {

            try {

                Log.d(
                    "*MAKITA**",
                    "VERIFICANDO PICKING COMPLETO: idCabecera=$idCabecera"
                )


                val estadoPicking =
                    apiService.verificarPickingCompleto(
                        idCabecera
                    )


                Log.d(
                    "*MAKITA*SAP*",
                    "ESTADO PICKING XXX= $estadoPicking"
                )


                if (!estadoPicking.success) {

                    Toast.makeText(
                        context,
                        estadoPicking.message
                            ?: "Error verificando el Picking",
                        Toast.LENGTH_LONG
                    ).show()

                    return@launch
                }


                // =====================================================
                // 2. SI NO ESTÁ COMPLETO → SOLICITAR AUTORIZACIÓN
                // =====================================================

                if (
                    !estadoPicking.pickingCompleto &&
                    !envioParcialAutorizado
                ) {

                    Log.d(
                        "*MAKITA*SAP*",
                        "PICKING NO COMPLETO → SOLICITANDO AUTORIZACIÓN PARA ENVÍO PARCIAL"
                    )


                    mensajeError2 =
                        "EL PICKING NO ESTÁ COMPLETO\n" +
                                "Completados (Item): " +
                                "${estadoPicking.detallesCompletados} " +
                                " Item de " +
                                "${estadoPicking.totalDetalles}" +
                                "\n\n¿Desea enviarlo de forma parcial?"


                    showDialog7 = true


                    return@launch
                }


                // =====================================================
                // 3. ENVIAR DETALLE A SAP
                // =====================================================

                Log.d(
                    "*MAKITA*SAP*",
                    "ENVIANDO DETALLE A SAP: " +
                            "idCabecera=$idCabecera, " +
                            "absEntry=$absEntry, " +
                            "parcial=$envioParcialAutorizado"
                )


                val request =
                    EnviarDetalleASAPRequest(
                        idCabecera = idCabecera,
                        absEntry = absEntry,
                        enviarParcial = envioParcialAutorizado

                    )

                val respuesta =
                    apiService.enviarDetalleASAP(
                        request
                    )


                Log.d(
                    "*MAKITA*SAP*",
                    "RESPUESTA ENVIAR DETALLE = $respuesta"
                )


                if (!respuesta.success) {

                    Toast.makeText(
                        context,
                        respuesta.message
                            ?: "Error enviando detalle a SAP",
                        Toast.LENGTH_LONG
                    ).show()

                    return@launch
                }


                Log.d(
                    "*MAKITA*SAP*",
                    "DETALLES ENVIADOS A SAP = ${respuesta.detallesEnviados}"
                )


                Toast.makeText(
                    context,
                    respuesta.message
                        ?: "Capturas enviadas a SAP",
                    Toast.LENGTH_LONG
                ).show()


                // =====================================================
                // 4. DETALLE ENVIADO → CERRAR PICKING
                // =====================================================

                Log.d(
                    "*MAKITA*SAP*",
                    "DETALLE ENVIADO CORRECTAMENTE → " +
                            "CERRANDO PICKING $idCabecera"
                )


                val respuestaCierre =
                    apiService.cerrarPicking(
                        idCabecera
                    )


                Log.d(
                    "*MAKITA*SAP*",
                    "RESPUESTA CIERRE = $respuestaCierre"
                )


                if (respuestaCierre.success) {

                    Log.d(
                        "*MAKITA*IMPRESION*",
                        "PICKING CERRADO CORRECTAMENTE → GENERANDO IMPRESIÓN"
                    )


                    showDialog8 = true


                    mensajeError8 =
                        "PICKING PROCESADO DE FORMA CORRECTA EN SAP"


                    imprimirPicking(
                        context = context,
                        idDocumento =
                            respuestaCierre.idDocumento!!
                    )


                } else {

                    showDialog8 = true

                    mensajeError8 =
                        "ERROR ${respuestaCierre.message}"
                }


            } catch (e: Exception) {

                Log.e(
                    "*MAKITA*SAP*",
                    "ERROR ENVIANDO DETALLE",
                    e
                )


                showDialog8 = true

                mensajeError8 =
                    e.message
                        ?: "Error enviando detalle"
            }
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // =================================================
            // ENCABEZADO
            // =================================================

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
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                            fontSize = 20.sp
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )


                    // =========================================
                    // PICKING / EMPRESA
                    // =========================================

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "N° PICKING",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )


                            Text(
                                text = absEntry.toString(),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }


                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {

                            Text(
                                text = "Bodega",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )


                            Text(
                                text = capturas.firstOrNull()?.whsCode ?: "",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )


                    // =========================================
                    // DETALLE ARTICULO
                    // =========================================

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF4FC3F7))
                            .padding(vertical = 8.dp)
                    ) {

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "DETALLE ARTICULOS",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            /*
                            Text(
                                text = "Item: ${itemCode.trim()}",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            */
                        }
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =================================================
            // LISTA DE CAPTURAS
            // =================================================

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = capturas
                ) { captura ->


                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),

                            verticalAlignment = Alignment.Top
                        ) {


                            // =========================================
                            // COLUMNA 1
                            // =========================================

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xFFFFF9C4))
                                    .padding(12.dp)
                            ) {

                                Text(
                                    text = "Item: ${captura.ItemCode ?: ""}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )


                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )


                                Text(
                                    text = "Serie: ${captura.numeroSerie ?: "-"}",
                                    fontSize = 14.sp
                                )


                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )


                                Text(
                                    text = "Cantidad: ${captura.cantidad}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )


                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )


                                Text(
                                    text = "Ubicacion: ${captura.barCode ?: "-"}",
                                    fontSize = 14.sp
                                )


                                Text(
                                    text = "",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }


                            // =========================================
                            // COLUMNA 2
                            // =========================================

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xFFFFF9C4))
                                    .padding(12.dp),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "${captura.ItemName?.take(25) ?: ""}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )


                                Spacer(
                                    modifier = Modifier.height(20.dp)
                                )


                                IconButton(
                                    onClick = {

                                        CoroutineScope(
                                            Dispatchers.Main
                                        ).launch {

                                            try {

                                                val respuesta =
                                                    apiService.eliminarPickingCaptura(
                                                        captura.id
                                                    )


                                                if (respuesta.success) {

                                                    capturas =
                                                        capturas.filter {
                                                            it.id != captura.id
                                                        }


                                                    mensajeError2 =
                                                        respuesta.message

                                                    showDialog = true

                                                } else {

                                                    mensajeError2 =
                                                        respuesta.message

                                                    showDialog = true
                                                }

                                            } catch (e: Exception) {

                                                mensajeError2 =
                                                    "Error eliminando captura"

                                                showDialog = true
                                            }
                                        }
                                    }
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription =
                                            "Eliminar captura",
                                        tint = Color.Red,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }


                                Text(
                                    text = "ELIMINAR",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // =================================================
            // BOTÓN ENVIAR A SAP
            // =================================================

            Button(
                onClick = {
                    procesarEnvioASAP(false)
                },


                enabled = puedeEnviarASAP,


                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 8.dp)
                    .height(55.dp),


                shape = RectangleShape,


                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFBDBDBD),
                    disabledContentColor = Color(0xFF757575)
                )

            ) {

                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )


                Spacer(
                    modifier = Modifier.width(8.dp)
                )


                Text(
                    text = "ENVIAR A SAP",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }


            // =================================================
            // DIÁLOGO ELIMINAR
            // =================================================

            if (showDialog) {

                mostrarDialogo4(
                    titulo = "Aviso!",
                    mensaje = mensajeError2,
                    onDismiss = {
                        showDialog = false
                    }
                )
            }


            // =================================================
            // DIÁLOGO PICKING NO COMPLETO
            // =================================================

            if (showDialog7) {

                mostrarDialogo7(
                    titulo = "Alerta!",
                    mensaje = mensajeError2,

                    onDismiss = {
                        showDialog7 = false
                    },

                    onEnviarParcial = {
                        showDialog7 = false
                        procesarEnvioASAP(true)
                    }
                )
            }


            // =================================================
            // DIÁLOGO RESPUESTA SAP
            // =================================================

            if (showDialog8) {

                mostrarDialogo8(
                    titulo = "Alerta!",
                    mensaje = mensajeError8,
                    onDismiss = {
                        showDialog8 = false
                    }
                )
            }
        }
    }
}

private fun imprimirPicking(
    context: Context,
    idDocumento: Int
) {

    val impresora =
        Sesion.impresora

    if (impresora.isNullOrBlank()) {

        Toast.makeText(
            context,
            "No hay impresora seleccionada",
            Toast.LENGTH_SHORT
        ).show()

        return
    }

    CoroutineScope(Dispatchers.IO).launch {

        try {

            val respuesta =
                apiService.obtenerPDFPicking(
                    idDocumento
                )

            val archivoPDF =
                File(
                    context.cacheDir,
                    "Picking_$idDocumento.pdf"
                )

            val input =
                respuesta.byteStream()

            val output =
                FileOutputStream(
                    archivoPDF
                )

            input.copyTo(output)

            output.close()
            input.close()

            withContext(Dispatchers.Main) {

                val printManager =
                    context.getSystemService(
                        Context.PRINT_SERVICE
                    ) as PrintManager

                val printAdapter =
                    object : PrintDocumentAdapter() {

                        override fun onLayout(
                            oldAttributes: PrintAttributes?,
                            newAttributes: PrintAttributes,
                            cancellationSignal: CancellationSignal,
                            callback: LayoutResultCallback,
                            extras: Bundle?
                        ) {

                            if (
                                cancellationSignal.isCanceled
                            ) {

                                callback.onLayoutCancelled()

                                return
                            }

                            val info =
                                PrintDocumentInfo.Builder(
                                    "Picking_$idDocumento.pdf"
                                )
                                    .setContentType(
                                        PrintDocumentInfo.CONTENT_TYPE_DOCUMENT
                                    )
                                    .setPageCount(
                                        PrintDocumentInfo.PAGE_COUNT_UNKNOWN
                                    )
                                    .build()

                            callback.onLayoutFinished(
                                info,
                                true
                            )
                        }

                        override fun onWrite(
                            pages: Array<out PageRange>,
                            destination: ParcelFileDescriptor,
                            cancellationSignal: CancellationSignal,
                            callback: WriteResultCallback
                        ) {

                            try {

                                if (
                                    cancellationSignal.isCanceled
                                ) {

                                    callback.onWriteCancelled()

                                    return
                                }

                                FileOutputStream(
                                    destination.fileDescriptor
                                ).use { output ->

                                    archivoPDF.inputStream().use { input ->

                                        input.copyTo(
                                            output
                                        )
                                    }
                                }

                                callback.onWriteFinished(
                                    arrayOf(
                                        PageRange.ALL_PAGES
                                    )
                                )

                            } catch (e: Exception) {

                                callback.onWriteFailed(
                                    e.message
                                )
                            }
                        }
                    }

                printManager.print(
                    "Picking $idDocumento",
                    printAdapter,
                    PrintAttributes.Builder()
                        .setMediaSize(
                            PrintAttributes.MediaSize.NA_LETTER
                        )
                        .setMinMargins(
                            PrintAttributes.Margins.NO_MARGINS
                        )
                        .build()
                )
            }

        } catch (e: Exception) {

            withContext(Dispatchers.Main) {

                Toast.makeText(
                    context,
                    "Error obteniendo PDF: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

@Composable
fun mostrarDialogo4(
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

@Composable
fun mostrarDialogo7(
    titulo: String,
    mensaje: String,
    onDismiss: () -> Unit,
    onEnviarParcial: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(text = titulo)
        },

        text = {
            Text(text = mensaje)
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        },

        confirmButton = {
            TextButton(
                onClick = onEnviarParcial
            ) {
                Text("Enviar parcial")
            }
        }
    )
}

@Composable
fun mostrarDialogo8(
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