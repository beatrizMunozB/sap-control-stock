package com.makita.controlstock.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.makita.controlstock.data.network.OrdenVentaPickingResponse
import com.makita.controlstock.data.network.PickingOrdenResponse
import com.makita.controlstock.data.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ConsultaPedidoClientePickingScreen(
    navController: NavController
) {
    var numeroPicking by remember { mutableStateOf("") }

    var pickings by remember {
        mutableStateOf<List<OrdenVentaPickingResponse>>(emptyList())
    }

    var cargando by remember {
        mutableStateOf(false)
    }

    var mensajeError by remember {
        mutableStateOf("")
    }

    val apiService = RetrofitClient.apiService

    val keyboardController =
        LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // =========================================
        // TITULO
        // =========================================

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }

            Text(
                text = "Consultar Orden de Venta por Picking",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // =========================================
        // NUMERO PICKING
        // =========================================

        OutlinedTextField(
            value = numeroPicking,

            onValueChange = { nuevoValor ->

                numeroPicking =
                    nuevoValor.filter {
                        it.isDigit()
                    }
            },

            modifier = Modifier.fillMaxWidth(),

            label = {
                Text("Número de Picking")
            },

            singleLine = true,

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // =========================================
        // BOTON CONSULTAR
        // =========================================

        Button(
            onClick = {

                keyboardController?.hide()

                if (numeroPicking.isBlank()) {

                    mensajeError =
                        "Ingrese el número de Picking."

                    return@Button
                }

                CoroutineScope(Dispatchers.IO).launch {

                    withContext(Dispatchers.Main) {

                        cargando = true
                        mensajeError = ""
                        pickings = emptyList()
                    }

                    try {

                        val respuesta =
                            apiService.obtenerOrdenPorPicking(
                                numeroPicking.toInt()
                            )

                        withContext(Dispatchers.Main) {

                            if (respuesta.success) {

                                pickings = respuesta.data

                                if (respuesta.data.isEmpty()) {

                                    mensajeError =
                                        "No se encontró información para el Picking."
                                }

                            } else {

                                mensajeError =
                                    respuesta.message
                            }

                            cargando = false
                        }

                    } catch (e: Exception) {

                        withContext(Dispatchers.Main) {

                            mensajeError =
                                "Error al consultar el Picking: ${e.message}"

                            cargando = false
                        }
                    }
                }
            },

            modifier = Modifier.fillMaxWidth(),

            enabled = !cargando,

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4FC3F7)
            )
        ) {

            Text(
                text = if (cargando) {
                    "CONSULTANDO..."
                } else {
                    "CONSULTAR"
                }
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // =========================================
        // MENSAJE ERROR
        // =========================================

        if (mensajeError.isNotBlank()) {

            Text(
                text = mensajeError,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        // =========================================
        // RESULTADOS
        // =========================================

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            items(
                items = pickings
            ) { picking ->

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        // =========================================
                        // PICKING
                        // =========================================

                        Text(
                            text =
                                "Picking: ${picking.pickingAbsEntry}",

                            style =
                                MaterialTheme.typography.titleMedium,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        // =========================================
                        // ORDEN DE VENTA
                        // =========================================

                        Text(
                            text =
                                "Orden de Venta: ${picking.numeroOrdenVenta}",

                            fontSize = 15.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        // =========================================
                        // CLIENTE
                        // =========================================

                        Text(
                            text =
                                "Cliente: ${picking.cardCode ?: "-"} - ${picking.cardName ?: "-"}",

                            fontSize = 14.sp
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        // =========================================
                        // USUARIO
                        // =========================================

                        Text(
                            text =
                                "Usuario: ${
                                    picking.usuarioAsignado
                                        ?: "Sin asignar"
                                }",

                            fontSize = 14.sp
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        // =========================================
                        // ITEMS
                        // =========================================

                        Text(
                            text =
                                "Ítems: ${picking.cantidadItems}",

                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}