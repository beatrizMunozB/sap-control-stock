package com.makita.controlstock.ui.screens
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.util.Log
import androidx.compose.foundation.background
import com.makita.controlstock.data.network.ComprasRecepcionDetalle
import com.makita.controlstock.data.network.RetrofitClient.apiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.navigationBarsPadding
import com.makita.controlstock.data.network.IniciarPosicionamientoRecepcionRequest
import kotlinx.coroutines.launch


@Composable
fun ComprasRecepcionDetalleScreen(
    navController: NavHostController,
    absEntry: Int,
    docNum: Int
) {

    var detalles by remember { mutableStateOf<List<ComprasRecepcionDetalle>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var idCabecera by remember { mutableStateOf<Int?>(null) }
    var iniciandoLectura by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()



    fun iniciarLectura() {
        Log.d("*MAKITA*", "1. Entró a iniciarLectura()")

        scope.launch {
            try {
                Log.d("*MAKITA*", "2. Iniciando proceso")
                Log.d("*MAKITA*", "AbsEntry: $absEntry")
                Log.d("*MAKITA*", "DocNum: $docNum")

                iniciandoLectura = true
                mensajeError = ""

                Log.d("*MAKITA*", "3. Ejecutando llamada API")

                val respuesta = withContext(Dispatchers.IO) {
                    apiService.iniciarPosicionamientoRecepcion(
                        IniciarPosicionamientoRecepcionRequest(
                            absEntry = absEntry,
                            docNum = docNum,
                            usuario = null
                        )
                    )
                }

                Log.d("*MAKITA*", "4. Respuesta API recibida")
                Log.d("*MAKITA*", "Success: ${respuesta.success}")
                Log.d("*MAKITA*", "Message: ${respuesta.message}")
                Log.d("*MAKITA*", "Data: ${respuesta.data}")

                if (respuesta.success && respuesta.data != null) {

                    idCabecera = respuesta.data.idCabecera

                    Log.d("*MAKITA*", "5. Navegando con idCabecera: $idCabecera")

                    navController.navigate(
                        "lecturaPosicionamientoRecepcion/${respuesta.data.idCabecera}"
                    )

                    Log.d("*MAKITA*", "6. Navegación ejecutada")

                } else {
                    mensajeError = respuesta.message
                    Log.e("*MAKITA*", "La API respondió sin éxito: ${respuesta.message}")
                }

            } catch (e: Exception) {
                mensajeError = "Error al iniciar la lectura: ${e.message}"
                Log.e("*MAKITA*", "ERROR al iniciar lectura", e)

            } finally {
                iniciandoLectura = false
                Log.d("*MAKITA*", "7. Finalizó iniciarLectura()")
            }
        }
    }


    LaunchedEffect(absEntry) {

        cargando = true
        mensajeError = ""

        try {

            val resultado = withContext(Dispatchers.IO) {
                apiService.obtenerComprasRecepcionDetalle(
                    absEntry = absEntry
                )
            }

            if (resultado.success && !resultado.data.isNullOrEmpty()) {

                detalles = resultado.data

                Log.d(
                    "*MAKITA*",
                    "Detalle recepción $absEntry: ${detalles.size} artículos"
                )

            } else {

                detalles = emptyList()
                mensajeError = "No existen artículos pendientes para esta recepción."
                showDialog = true
            }

        } catch (e: Exception) {

            Log.e(
                "*MAKITA*",
                "Error consultando detalle de recepción $absEntry",
                e
            )

            mensajeError = "Error al consultar el detalle de recepción: ${e.message}"
            showDialog = true

        } finally {
            cargando = false
        }
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
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "POSICIONAMIENTO RECEPCION",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                // DATOS DE RECEPCIÓN

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
                            text = "Nro Documento: $docNum",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )


                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Items pendientes: ${detalles.size}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00695C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "DETALLE DE ITEMS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (cargando) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {

                        items(detalles) { item ->

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                elevation = CardDefaults.elevatedCardElevation(
                                    defaultElevation = 4.dp
                                ),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = Color.White
                                ),
                                shape = RectangleShape
                            ) {
                                Column {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFE0E0E0))
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.ItemCode,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = item.Dscription ?: "",
                                            fontSize = 13.sp,
                                            color = Color.Black,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }


                                    Column(
                                        modifier = Modifier.padding(14.dp)
                                    ) {
                                        Text(
                                            text = "Bodega: ${item.FromWhsCod}",
                                            fontSize = 13.sp
                                        )

                                        Text(
                                            text = "Ubicacion propuesta: ${item.BinCode ?: "SIN UBICACION"}",
                                            fontSize = 13.sp
                                        )

                                        Text(
                                            text = "Cantidad pendiente: ${item.OpenQty.toInt()}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00695C)
                                        )

                                        Text(
                                            text = "Unidad: ${item.UMedida ?: "-"}",
                                            fontSize = 13.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(bottom = 12.dp, top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { iniciarLectura() },
                            enabled = !iniciandoLectura,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4FC3F7),
                                contentColor = Color.Black
                            ),
                            shape = RectangleShape
                        ) {
                            if (iniciandoLectura) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.Black
                                )
                            } else {
                                Text(
                                    text = "LECTURA ITEM",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                // Navegar a confirmar ubicaciones
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4FC3F7),
                                contentColor = Color.Black
                            ),
                            shape = RectangleShape
                        ) {
                            Text(
                                text = "CONFIRMAR UBICACIONES",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
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