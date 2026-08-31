package com.makita.controlstock.ui.screens


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
import kotlinx.coroutines.launch




@Composable
fun ProcesadoPickingScreen(
    idCabecera: Int,
    idDetalle: Int,
    absEntry: Int,
    binCode: String,
    binAbs: Int,
    whsCode: String,
    itemCode: String,
    navController: NavHostController
) {

    var capturas by remember {
        mutableStateOf<List<PickingCapturaResponse>>(emptyList())
    }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var mensajeError2 by remember { mutableStateOf("") }
    var showDialog7 by remember { mutableStateOf(false) }



    LaunchedEffect(idDetalle, binAbs) {

        try {

            val resultado =
                apiService.obtenerPickingCapturas(
                    idDetalle,
                    binAbs
                )

            if (resultado.success) {
                capturas = resultado.data
            }

        } catch (e: Exception) {

            //  ACA MENSAJE MAS CLARO PARA EL USUARIO
            Log.e(
                "*MAKITA*PICKING*",
                "ERROR OBTENIENDO CAPTURAS",
                e
            )
        }
    }


    val puedeEnviarASAP =
        capturas.size == 1 &&
                capturas.first().cantidad > 0

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

                    // =========================================
                    // TITULO
                    // =========================================

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
                        modifier = Modifier.height(10.dp)
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
                                text = "MAKITA CHILE",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Text(
                                text = "COM. LTDA.",
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
                    // UBICACION / BODEGA
                    // =========================================

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "UBICACION",
                                color = Color.White,
                                fontSize = 12.sp
                            )

                            Text(
                                text = binCode,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {

                            Text(
                                text = "BODEGA",
                                color = Color.White,
                                fontSize = 12.sp
                            )

                            Text(
                                text = whsCode,
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
                                text = "DETALLE ARTÍCULOS",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Text(
                                text = "Item: ${itemCode.trim()}",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // =================================================
            // TITULO CAPTURAS
            // =================================================

            Spacer(
                modifier = Modifier.height(12.dp)
            )


            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),

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
                                modifier = Modifier.weight(1f)
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
                                    text = "Ubicacion: $binCode",
                                    fontSize = 14.sp
                                )

                                Text(
                                    text = "",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }



                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xFFFFF9C4))
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "${captura.ItemName ?: ""}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(20.dp)
                                )

                                IconButton(
                                    onClick = {

                                        CoroutineScope(Dispatchers.Main).launch {

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



                                                    mensajeError2 = respuesta.message
                                                    showDialog = true

                                                    /*
                                                    Toast.makeText(
                                                        context,
                                                        respuesta.message,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    */

                                                } else {


                                                    mensajeError2 = respuesta.message
                                                    showDialog = true

                                                    /*
                                                    Toast.makeText(
                                                        context,
                                                        respuesta.message,
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    */


                                                }

                                            } catch (e: Exception) {


                                                /*
                                                Toast.makeText(
                                                    context,
                                                    e.message ?: "Error eliminando captura",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                 */

                                                mensajeError2 = "Error eliminando captura"
                                                showDialog = true


                                            }
                                        }
                                    }
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar captura",
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


            if (showDialog) {
                mostrarDialogo4(
                    titulo = "Error",
                    mensaje = mensajeError2,
                    onDismiss = {
                        showDialog = false
                        //ubicacionEscaneadaRequester.requestFocus()
                    }
                )
            }

            if (showDialog7) {
                mostrarDialogo7(
                    titulo = "Alerta!",
                    mensaje = mensajeError2,
                    onDismiss = {
                        showDialog7 = false
                        //ubicacionEscaneadaRequester.requestFocus()
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = {

                    CoroutineScope(Dispatchers.Main).launch {

                        try {

                            // =====================================================
                            // 1. VERIFICAR SI TODO EL PICKING ESTÁ COMPLETO
                            // =====================================================

                            Log.d(
                                "*MAKITA*SAP*",
                                "VERIFICANDO PICKING COMPLETO: idCabecera=$idCabecera"
                            )

                            val estadoPicking =
                                apiService.verificarPickingCompleto(idCabecera)

                            Log.d(
                                "*MAKITA*SAP*",
                                "ESTADO PICKING = $estadoPicking"
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
                            // 2. SI NO ESTÁ COMPLETO → NO ENVIAR A SAP
                            // =====================================================

                            if (!estadoPicking.pickingCompleto) {

                                Log.d(
                                    "*MAKITA*SAP*",
                                    "PICKING NO COMPLETO → NO SE ENVÍA A SAP"
                                )


                                mensajeError2 = "EL PICKING NO ESTÁ COMPLETO\n" +
                                        "Completados (Item): " +
                                        "${estadoPicking.detallesCompletados} " +
                                        " Item de " +
                                        "${estadoPicking.totalDetalles}"
                                showDialog7 = true

                                /*
                                Toast.makeText(
                                    context,
                                    "EL PICKING NO ESTÁ COMPLETO\n" +
                                            "Completados: " +
                                            "${estadoPicking.detallesCompletados} " +
                                            "de " +
                                            "${estadoPicking.totalDetalles}",
                                    Toast.LENGTH_LONG
                                ).show()

                                 */

                                return@launch
                            }

                            // =====================================================
                            // 3. PICKING COMPLETO → ENVIAR DETALLE A SAP
                            // =====================================================

                            Log.d(
                                "*MAKITA*SAP*",
                                "PICKING COMPLETO → ENVIANDO DETALLE A SAP: " +
                                        "idCabecera=$idCabecera, " +
                                        "idDetalle=$idDetalle, " +
                                        "absEntry=$absEntry, " +
                                        "binAbs=$binAbs"
                            )

                            val request =
                                EnviarDetalleASAPRequest(
                                    idCabecera = idCabecera,
                                    idDetalle = idDetalle,
                                    absEntry = absEntry,
                                    binAbs = binAbs
                                )

                            val respuesta =
                                apiService.enviarDetalleASAP(request)

                            Log.d(
                                "*MAKITA*SAP*",
                                "RESPUESTA ENVIAR DETALLE = $respuesta"
                            )

                            if (!respuesta.success) {

                                Toast.makeText(
                                    context,
                                    respuesta.message ?: "Error enviando detalle a SAP",
                                    Toast.LENGTH_LONG
                                ).show()

                                return@launch
                            }

                            Log.d(
                                "*MAKITA*SAP*",
                                "CAPTURAS RECIBIDAS = ${respuesta.capturas}"
                            )

                            Toast.makeText(
                                context,
                                respuesta.message ?: "Capturas enviadas a SAP",
                                Toast.LENGTH_LONG
                            ).show()


                            // =====================================================
                            // 4. DETALLE ENVIADO → CERRAR PICKING EN SAP
                            // =====================================================

                            Log.d(
                                "*MAKITA*SAP*",
                                "DETALLE ENVIADO CORRECTAMENTE → " +
                                        "CERRANDO PICKING $idCabecera"
                            )

                            val respuestaCierre =
                                apiService.cerrarPicking(idCabecera)

                            Log.d(
                                "*MAKITA*SAP*",
                                "RESPUESTA CIERRE = $respuestaCierre"
                            )

                            if (respuestaCierre.success) {

                                Log.d(
                                    "*MAKITA*SAP*",
                                    "PICKING CERRADO CORRECTAMENTE EN SAP"
                                )

                                Toast.makeText(
                                    context,
                                    "PICKING CERRADO CORRECTAMENTE EN SAP",
                                    Toast.LENGTH_LONG
                                ).show()

                            } else {

                                Log.e(
                                    "*MAKITA*SAP*",
                                    "ERROR CERRANDO PICKING: ${respuestaCierre.message}"
                                )

                                Toast.makeText(
                                    context,
                                    respuestaCierre.message
                                        ?: "Error cerrando picking en SAP",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } catch (e: Exception) {

                            Log.e(
                                "*MAKITA*SAP*",
                                "ERROR ENVIANDO DETALLE",
                                e
                            )

                            Toast.makeText(
                                context,
                                e.message ?: "Error enviando detalle",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                enabled = puedeEnviarASAP,
                modifier = Modifier
                    .fillMaxWidth()
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