package com.makita.controlstock.ui.screens

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

// ESTE ES EL NUEVI CON LAS UBICACIONES

@Composable
fun DetallePickingScreen(
    AbsEntry: Int,
    navController: NavHostController
) {

    var detalle by remember {
        mutableStateOf<List<PickingDetalleResponse>>(emptyList())
    }

    LaunchedEffect(AbsEntry) {

        try {

            val resultado = apiService.obtenerPickingDetalle(AbsEntry)
            detalle = resultado.data

            resultado.data?.forEach { item ->
                Log.d(
                    "*MAKITA*",
                    "Bin=${item.BinCode} | " +
                            "CantidadCapturada=${item.CantidadCapturada} | " +
                            "CantidadPickeadaSAP=${item.CantidadPickeadaSAP} | " +
                            "CantidadPickeadaBin=${item.CantidadPickeadaBin}"
                )
            }


        } catch (e: Exception) {

            Log.e("*MAKITA*", e.message ?: "")

        }

    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

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
                            text = "PICKING - UBICACIONES",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Picking N° $AbsEntry",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(detalle) { item ->

                    val BinAbs = item.BinAbs

                    val cantidadBin =
                        item.CantidadBin?.toInt() ?: 0

                    val cantidadCapturada =
                        item.CantidadCapturada?.toInt() ?: 0

                    val cantidadSAP =
                        item.CantidadPickeadaSAP?.toInt() ?: 0

                    val pendienteSAP =
                        cantidadCapturada > cantidadSAP

                    val enviadoSAP =
                        cantidadCapturada > 0 &&
                                cantidadCapturada == cantidadSAP

                    val colorTarjeta =
                        when {
                            pendienteSAP -> Color(0xFFFFE0B2)
                            enviadoSAP -> Color(0xFFC8E6C9)
                            else -> Color.White
                        }

                    val colorEstado =
                        when {
                            pendienteSAP -> Color(0xFFE65100)
                            enviadoSAP -> Color(0xFF2E7D32)
                            else -> Color.Black
                        }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 4.dp,
                                vertical = 3.dp
                            )
                            .clickable {

                                navController.navigate(
                                    "detalleUbicacionPicking/" +
                                            "${AbsEntry}/" +
                                            "${BinAbs}"

                                )
                            },
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 5.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = colorTarjeta
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = item.BinCode ?: "",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "Cant: $cantidadBin",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text(
                                    text = "Pick: $cantidadCapturada",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorEstado
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text(
                                    text = "SAP: $cantidadSAP",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            if (pendienteSAP) {

                                Text(
                                    text = "Pendiente enviar a SAP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    modifier = Modifier.padding(
                                        top = 6.dp
                                    )
                                )
                            }

                            if (enviadoSAP) {

                                Text(
                                    text = "Enviado a SAP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.padding(
                                        top = 6.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(55.dp)
            ) {

                Text(
                    text = "VOLVER",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}