package com.makita.controlstock.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.makita.controlstock.data.network.DetalleOrdenCompraResponse
import com.makita.controlstock.data.network.RetrofitClient.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.lazy.items


@Composable
fun DetalleOrdenCompraScreen(
    navController: NavHostController,
    docEntry: Int
) {

    var detalles by remember {
        mutableStateOf<List<DetalleOrdenCompraResponse>>(emptyList())
    }

    var mensajeError by remember {
        mutableStateOf("")
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    /*
     * CARGAR DETALLE
     */
    suspend fun cargarDetalleOrdenCompra() {

        try {

            Log.d(
                "*MAKITA*",
                "Cargando detalle OC - docEntry=$docEntry"
            )

            val resultado =
                apiService.obtenerDetalleOrdenCompra(
                    docEntry = docEntry
                )

            if (resultado.success) {

                detalles = resultado.data ?: emptyList()

                Log.d(
                    "*MAKITA*",
                    "Líneas encontradas: ${detalles.size}"
                )

                if (detalles.isEmpty()) {

                    mensajeError =
                        "No se encontraron líneas para la orden de compra"

                    showDialog = true
                }

            } else {

                detalles = emptyList()

                mensajeError =
                    "No fue posible obtener el detalle de la orden de compra"

                showDialog = true
            }

        } catch (e: Exception) {

            Log.e(
                "*MAKITA*",
                "Error obteniendo detalle de OC",
                e
            )

            mensajeError =
                "Error al obtener el detalle: ${e.message}"

            showDialog = true
        }
    }

    /*
     * CARGA INICIAL
     */
    LaunchedEffect(docEntry) {

        cargarDetalleOrdenCompra()
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
                        .padding(16.dp),
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
                        text = "DETALLE ORDEN DE COMPRA",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            /*
             * CONTENIDO
             */
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {


                Text(
                    text = "Orden de Compra: $docEntry",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                /*
                 * LISTA DE DETALLE
                 */
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {

                    items(detalles) { item ->

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 4.dp,
                                    vertical = 5.dp
                                ),

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

                                /*
                                 * ITEM
                                 */
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = item.ItemCode ?: "",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Text(
                                        text = "Línea ${item.OC_Linea}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(5.dp)
                                )

                                /*
                                 * DESCRIPCIÓN
                                 */
                                Text(
                                    text = item.Dscription ?: "",
                                    fontSize = 14.sp
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                /*
                                 * CANTIDADES
                                 */
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        Text(
                                            text = "Cantidad OC",
                                            fontSize = 12.sp
                                        )

                                        Text(
                                            text = item.CantidadOC.toString(),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        Text(
                                            text = "Pendiente",
                                            fontSize = 12.sp
                                        )

                                        Text(
                                            text = item.PendienteOC.toString(),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        Text(
                                            text = "Bodega",
                                            fontSize = 12.sp
                                        )

                                        Text(
                                            text = item.Bodega ?: "",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
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
}
