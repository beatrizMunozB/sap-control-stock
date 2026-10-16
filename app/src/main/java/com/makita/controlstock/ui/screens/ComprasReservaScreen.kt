package com.makita.controlstock.ui.screens

import android.icu.text.NumberFormat
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import com.makita.controlstock.data.network.FacturaReservaResponse
import com.makita.controlstock.data.network.RetrofitClient.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Locale

@Composable
fun ComprasReservaScreen(
    navController: NavHostController
) {

    var numeroFactura by remember { mutableStateOf("") }
    var socioNegocio by remember { mutableStateOf("") }
    var numAtCard by remember { mutableStateOf("") }
    var facturas by remember { mutableStateOf<List< FacturaReservaResponse>>(emptyList()) }
    var mensajeError by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }
    val socioNegocioFocusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

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

                    mensajeError =
                        respuesta.message ?: "Socio de Negocio no encontrado"

                    showDialog = true
                }

            } catch (e: Exception) {

                Log.e(
                    "*MAKITA*",
                    "Error validando Socio de Negocio",
                    e
                )

                socioNegocio = ""

                mensajeError =
                    "Socio de Negocio no encontrado.\n\n" +
                            "Verifique el código ingresado e intente nuevamente."

                showDialog= true

            }
        }
    }
    fun validarNumeroFacturaReserva() {

        if (numeroFactura.trim().isEmpty()) {
            return
        }

        CoroutineScope(Dispatchers.Main).launch {

            try {

                val respuesta = apiService.obtenerFacturasReserva(
                    numeroFactura = numeroFactura.trim(),
                    socioNegocio = "",
                    numAtCard = ""
                )

                if (respuesta.success && !respuesta.data.isNullOrEmpty()) {

                    val factura = respuesta.data.first()

                    Log.d(
                        "*MAKITA*",
                        "Factura de reserva encontrada: ${factura.DocNum}"
                    )

                } else {

                    mensajeError = "Factura de reserva no encontrada"
                    showDialog = true
                }

            } catch (e: Exception) {

                Log.e(
                    "*MAKITA*",
                    "Error validando factura de reserva",
                    e
                )

                mensajeError = "Error al validar la factura de reserva"
                showDialog = true
            }
        }
    }
    suspend fun buscarFacturasReserva() {

        try {

            cargando = true

            Log.d(
                "*MAKITA*",
                "Buscando facturas de reserva: " +
                        "numeroFactura=$numeroFactura | " +
                        "socioNegocio=$socioNegocio | " +
                        "numAtCard=$numAtCard"
            )

            val resultado = apiService.obtenerFacturasReserva(
                numeroFactura = numeroFactura.trim(),
                socioNegocio = socioNegocio.trim(),
                numAtCard = numAtCard.trim()
            )

            if (resultado.success) {

                facturas = resultado.data ?: emptyList()

                Log.d(
                    "*MAKITA*",
                    "Facturas de reserva encontradas: ${facturas.size}"
                )

                if (facturas.isEmpty()) {
                    mensajeError = "No se encontraron facturas de reserva"
                    showDialog = true
                }

            } else {

                facturas = emptyList()
                mensajeError = "No fue posible obtener las facturas de reserva"
                showDialog = true
            }

        } catch (e: Exception) {

            Log.e(
                "*MAKITA*",
                "Error obteniendo facturas de reserva",
                e
            )

            facturas = emptyList()
            mensajeError = "Error al obtener las facturas de reserva: ${e.message}"
            showDialog = true

        } finally {
            cargando = false
        }
    }

    LaunchedEffect(Unit) {
        buscarFacturasReserva()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF2F2F2)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
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
                        text = "Facturas Reserva",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {




                OutlinedTextField(
                    value = numeroFactura,
                    onValueChange = {
                        numeroFactura = it
                    },
                    label = {
                        Text("Numero Documento SAP")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            scope.launch {
                                validarNumeroFacturaReserva()
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = numAtCard,
                    onValueChange = {
                        numAtCard = it
                    },
                    label = {
                        Text("Referencia Socio Negocio")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // BOTON BUSCAR

                Button(
                    onClick = {
                        scope.launch {
                            buscarFacturasReserva()
                        }
                    },
                    enabled = !cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = if (cargando) "BUSCANDO..." else "BUSCAR"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // LISTA DE FACTURAS DE RESERVA

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {

                    items(facturas) { item ->

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 4.dp,
                                    vertical = 4.dp
                                )
                                .clickable {
                                    navController.navigate(
                                        "detalleFacturaReserva/${item.DocEntry}/${item.DocNum}"
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
                                        text = "Doc. Nro: ${item.DocNum}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Text(
                                        text = item.DocDate?.take(10) ?: "",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Referencia: ${item.NumAtCard ?: ""}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "${item.CardCode ?: ""} - ${item.CardName ?: ""}",
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.JrnlMemo ?: "",
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Total: ${
                                        NumberFormat.getNumberInstance(Locale("es", "CL"))
                                            .apply {
                                                minimumFractionDigits = 0
                                                maximumFractionDigits = 0
                                            }
                                            .format(item.DocTotal?.toDoubleOrNull() ?: 0.0)
                                    }",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
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