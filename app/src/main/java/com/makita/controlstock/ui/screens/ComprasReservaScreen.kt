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
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Clear
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
    var numeroBL by remember { mutableStateOf("") }

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

            }
            else
            {

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
    fun validarNumeroBL() {

        if (numeroBL.trim().isEmpty()) {
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {

                val respuesta = apiService.validarNumeroBL(
                    numeroBL = numeroBL.trim()
                )

                if (respuesta.success && !respuesta.data.isNullOrEmpty()) {

                    val factura = respuesta.data.first()

                    Log.d(
                        "MAKITA",
                        "Factura de reserva encontrada por BL: ${factura.DocNum}"
                    )

                } else {

                    mensajeError = "Factura de reserva con BL ${numeroBL.trim()} no encontrada"
                    showDialog = true
                }

            } catch (e: Exception) {

                Log.e(
                    "MAKITA",
                    "Error validando número de BL",
                    e
                )

                mensajeError = "Error al validar el número de BL"
                showDialog = true
            }
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
                        text = "FACTURAS DE RESERVA",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
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


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = numeroFactura,
                        onValueChange = {
                            numeroFactura = it
                        },
                        label = {
                            Text("DocNum SAP", fontSize = 12.sp)
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
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

                    OutlinedTextField(
                        value = socioNegocio,
                        onValueChange = {
                            socioNegocio = it.uppercase()
                        },
                        label = {
                            Text("Socio Negocio", fontSize = 12.sp)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(socioNegocioFocusRequester),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                validarSocioNegocio()
                            }
                        )
                    )
                }

                // FILA 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = numAtCard,
                        onValueChange = {
                            numAtCard = it
                        },
                        label = {
                            Text("Nro Referencia", fontSize = 12.sp)
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = numeroBL,
                        onValueChange = {
                            numeroBL = it
                        },
                        label = {
                            Text("Nro. de BL", fontSize = 12.sp)
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                scope.launch {
                                    validarNumeroBL()
                                }
                            }
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {



                    Button(
                        onClick = {
                            scope.launch {
                                buscarFacturasReserva()
                            }
                        },
                        enabled = !cargando,
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4FC3F7)
                        ),
                        shape = RectangleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = if (cargando) "BUSCANDO..." else "BUSCAR"
                        )
                    }

                    // BOTÓN LIMPIAR
                    OutlinedButton(
                        onClick = {
                            numeroFactura = ""
                            socioNegocio = ""
                            numAtCard = ""
                            numeroBL = ""

                            facturas = emptyList()
                            mensajeError = ""
                            showDialog = false
                        },
                        enabled = !cargando,
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4FC3F7)
                        ),
                        shape = RectangleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar"
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text("LIMPIAR")
                    }
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


                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFE0E0E0))
                                        .padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Text(
                                            text = "Doc Nro: ${item.DocNum}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Text(
                                            text = try {
                                                val fecha = item.DocDate?.take(10)
                                                if (!fecha.isNullOrBlank()) {
                                                    java.time.LocalDate.parse(fecha).format(
                                                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                                    )
                                                } else {
                                                    ""
                                                }
                                            } catch (e: Exception) {
                                                item.DocDate?.take(10) ?: ""
                                            },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
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
                                    text = "BL: ${item.U_BL ?: ""}",
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Contenedor: ${item.U_Contenedor ?: ""}",
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Invoice: ${item.U_InvoiceNo ?: ""}",
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Referencia: ${item.U_BL ?: ""}",
                                    fontSize = 13.sp
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