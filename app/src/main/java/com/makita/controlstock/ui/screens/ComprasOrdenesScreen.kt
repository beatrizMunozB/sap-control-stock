package com.makita.controlstock.ui.screens
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import com.makita.controlstock.data.network.OrdenCompraResponse
import com.makita.controlstock.data.network.RetrofitClient.apiService
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch




@Composable
fun ComprasOrdenesScreen(
    navController: NavHostController
) {

    var numeroOC            by remember { mutableStateOf("") }
    var socioNegocio        by remember { mutableStateOf("") }
    var ordenes             by remember { mutableStateOf<List<OrdenCompraResponse>>(emptyList()) }
    var mensajeError        by remember { mutableStateOf("") }
    var showDialog          by remember { mutableStateOf(false) }
    var mensajeError2       by remember { mutableStateOf("") }
    var showDialogCantidad  by remember { mutableStateOf(false) }
    val socioNegocioFocusRequester = remember { FocusRequester() }

    fun validarNumeroOC() {

        if (numeroOC.trim().isEmpty()) {
            return
        }

        CoroutineScope(Dispatchers.Main).launch {

            try {

                val respuesta = apiService.validarNumeroOC(
                    numeroOC.trim()
                )

                if (respuesta.success && !respuesta.data.isNullOrEmpty())
                {
                    val orden = respuesta.data.first()
                    Log.d(
                        "*MAKITA*",
                        "OC encontrada: ${orden.DocNum}"
                    )
                }
                else
                {
                    mensajeError2 =
                        respuesta.message ?: "Orden de Compra no encontrada"
                    showDialogCantidad = true
                }

            } catch (e: Exception) {

                Log.e("*MAKITA*", "Error validando Orden de Compra", e)
                mensajeError2 = "Error al validar la Orden de Compra"
                showDialogCantidad = true
            }
        }
    }

    fun validarSocioNegocio() {

        if (socioNegocio.trim().isEmpty()) {
            return
        }

        CoroutineScope(Dispatchers.Main).launch {

            try {

                val respuesta = apiService.validarSocioNegocio(
                    socioNegocio.trim()
                )

                if (respuesta.success && !respuesta.data.isNullOrEmpty()) {

                    val socio = respuesta.data.first()

                    Log.d(
                        "*MAKITA*",
                        "Encontrado: ${socio.CardCode} - ${socio.CardName}"
                    )

                    socioNegocio = socio.CardCode ?: ""

                } else {

                    mensajeError2 =
                        respuesta.message ?: "Socio de Negocio no encontrado"

                    showDialogCantidad = true
                }

            } catch (e: Exception) {

                Log.e(
                    "*MAKITA*",
                    "Error validando Socio de Negocio",
                    e
                )

                socioNegocio = ""

                mensajeError2 =
                    "Socio de Negocio no encontrado.\n\n" +
                            "Verifique el código ingresado e intente nuevamente."

                showDialogCantidad = true

            }
        }
    }

    suspend fun buscarOrdenesCompra() {

        try {

            Log.d(
                "*MAKITA*",
                "Buscando OC - numeroOC=$numeroOC | socioNegocio=$socioNegocio"
            )

            val resultado = apiService.obtenerOrdenesCompra(
                numeroOC = numeroOC.trim(),
                socioNegocio = socioNegocio.trim()
            )

            if (resultado.success) {

                ordenes = resultado.data ?: emptyList()

                Log.d(
                    "*MAKITA*",
                    "Órdenes encontradas: ${ordenes.size}"
                )

                if (ordenes.isEmpty()) {

                    mensajeError = "No se encontraron órdenes de compra"
                    showDialog = true
                }

            } else {

                ordenes = emptyList()

                mensajeError =
                    "No fue posible obtener las órdenes de compra"

                showDialog = true
            }

        } catch (e: Exception) {

            Log.e(
                "*MAKITA*",
                "Error obteniendo órdenes de compra",
                e
            )

            mensajeError =
                "Error al obtener las órdenes de compra: ${e.message}"

            showDialog = true
        }
    }

    /*
     * CARGA INICIAL
     */
    LaunchedEffect(Unit) {
        buscarOrdenesCompra()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF2F2F2)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            /*
             * ENCABEZADO
             */
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
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Ordenes de Compra",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                OutlinedTextField(
                    value = numeroOC,
                    onValueChange = {
                        numeroOC = it
                    },
                    label = {
                        Text("Numero Orden de Compra")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            validarNumeroOC()
                        }
                    )
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                socioNegocio = "PE3003-1"

                OutlinedTextField(
                    value = socioNegocio,
                    onValueChange = {
                        socioNegocio = it.uppercase()
                    },
                    label = {
                        Text("Socio de Negocio")
                    },
                    singleLine = true,
                    modifier =Modifier
                        .fillMaxWidth()
                        .focusRequester(socioNegocioFocusRequester),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            validarSocioNegocio()
                        }
                    )
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            buscarOrdenesCompra()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text("BUSCAR")
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                /*
                 * LISTA DE ORDENES
                 */
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {

                    items(ordenes) { item ->

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 4.dp,
                                    vertical = 4.dp
                                )
                                .clickable {

                                    navController.navigate(
                                        "detalleOrdenCompra/${item.OC_DocEntry}/${item.OC_DocNum}"
                                    )
                                },

                            elevation = CardDefaults.elevatedCardElevation(
                                defaultElevation = 5.dp
                            ),

                            colors = CardDefaults.elevatedCardColors(
                                containerColor = Color.White
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = "Doc. Nro: ${item.OC_DocNum}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Text(
                                        text = item.OC_Fecha?.take(10) ?: "",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )

                                Text(
                                    text = item.Proveedor ?: "",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = item.NombreProveedor ?: "",
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * DIALOGO
     */
    if (showDialog) {

        mostrarDialogo20(
            titulo = "Aviso!",
            mensaje = mensajeError,
            onDismiss = {
                showDialog = false
            }
        )
    }

    if (showDialogCantidad) {

        mostrarDialogo20(
            titulo = "Aviso!",
            mensaje = mensajeError2,
            onDismiss = {
                showDialogCantidad = false

                socioNegocioFocusRequester.requestFocus()
            }
        )
    }
}

@Composable
fun mostrarDialogo20(
    titulo: String,
    mensaje: String,
    onDismiss: () -> Unit
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



