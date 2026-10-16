package com.makita.controlstock.ui.screens
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.makita.controlstock.data.network.UbicacionResponse
import kotlinx.coroutines.withContext
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun StockItemOrdenCompraScreen(
    navController: NavController,
    itemCode: String
) {
    var response by remember { mutableStateOf<List<UbicacionResponse>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }

    LaunchedEffect(itemCode) {
        try {
            val resultado = withContext(Dispatchers.IO) {
                apiService.obtenerStockItem(itemCode)
            }

            response = resultado.data ?: emptyList()

            if (response.isEmpty()) {
                mensajeError = "No se encontró stock para el artículo."
            }
        } catch (e: Exception) {
            Log.e(
                "*MAKITA*",
                "Error consultando stock del artículo $itemCode",
                e
            )

            mensajeError = "Error al consultar el stock: ${e.message}"
        } finally {
            cargando = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                    contentDescription = "Volver"
                )
            }

            Text(
                text = "Stock del artículo",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFFE0E0E0)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Código del artículo",
                    fontSize = 13.sp
                )

                Text(
                    text = itemCode,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            cargando -> {
                CircularProgressIndicator()
            }

            response.isEmpty() -> {
                Text(
                    text = mensajeError,
                    color = Color.Red
                )
            }

            else -> {
                LazyColumn {
                    items(response) { item ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            elevation = CardDefaults.elevatedCardElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = item.descripcion ?: "",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )

                                Text("Ubicación: ${item.ubicacion ?: ""}")
                                Text("Bodega: ${item.bodega ?: ""}")

                                Text(
                                    text = "Cantidad: ${item.cantidad.toDouble().toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )

                                Text("Estado: ${item.estado ?: ""}")
                                Text(
                                    "Ubicación estándar: ${item.ubicacionStandar ?: ""}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

