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
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.QrCodeScanner
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
import com.makita.controlstock.data.network.IniciarPickingLecturaRequest
import com.makita.controlstock.data.network.PickingDetalleUbicacionResponse

import kotlin.math.roundToInt
import androidx.compose.ui.platform.LocalContext

// ESTE ES EL NUEVI CON LAS UBICACIONES

@Composable
fun DetalleUbicacionPickingScreen(
    AbsEntry: Int,
    BinAbs: Int,
    navController: NavHostController
) {

    val context = LocalContext.current
    var detalle by remember { mutableStateOf<List<PickingDetalleUbicacionResponse>>(emptyList()) }
    var pickingProcesado by remember { mutableStateOf(false) }
    var pickingCompleto by remember { mutableStateOf(false) }


    /*
    LaunchedEffect(AbsEntry) {

        try {

            val resultado = apiService.obtenerUbicacionPickingDetalle(
                AbsEntry,
                BinAbs
            )

            detalle = resultado.data


        } catch (e: Exception) {

            Log.e("*MAKITA*", e.message ?: "")

        }

    }
    */

    LaunchedEffect(AbsEntry) {

        try {

            val resultado =
                apiService.obtenerUbicacionPickingDetalle(
                    AbsEntry,
                    BinAbs
                )

            detalle = resultado.data

            val estadoPicking =
                apiService.obtenerEstadoPicking(AbsEntry)

            if (estadoPicking.success) {

                pickingProcesado =
                    estadoPicking.estado == "PROCESADO"

                pickingCompleto =
                    estadoPicking.pickingCompleto

                Log.d(
                    "*MAKITA*PICKING*",
                    "Estado Picking $AbsEntry = ${estadoPicking.estado} | " +
                            "pickingCompleto=$pickingCompleto"
                )
            }

        } catch (e: Exception) {

            Log.e(
                "*MAKITA*",
                e.message ?: ""
            )
        }
    }

    val item = detalle.firstOrNull()


    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    modifier =  Modifier.padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    )
                ) {

                    IconButton(
                        onClick = { navController.popBackStack() }
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "N° PICKING",
                                color = Color.White,
                                fontSize = 12.sp
                            )

                            Text(
                                text = AbsEntry.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
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
                                text = item?.WhsCode ?: "",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                        }

                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "UBICACION",
                        color = Color.White,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${item?.BinCode ?: ""} ($BinAbs)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )


                    Button(
                        onClick = {

                            CoroutineScope(Dispatchers.Main).launch {

                                try {

                                    Log.d(
                                        "*MAKITA*PICKING*",
                                        "Buscando IdCabecera para AbsEntry = $AbsEntry"
                                    )

                                    val respuesta =
                                        apiService.obtenerIdCabeceraPicking(
                                            AbsEntry
                                        )

                                    Log.d(
                                        "*MAKITA*PICKING*",
                                        "Respuesta CABECERA = $respuesta"
                                    )

                                    if (respuesta.success) {
                                        navController.navigate(
                                            "procesadoPicking/" +
                                                    "${respuesta.idCabecera}/" +
                                                    "${item?.IdDetalle}/" +
                                                    "$AbsEntry/" +
                                                    "${item?.BinCode}/" +
                                                    "$BinAbs/" +
                                                    "${item?.WhsCode}/" +
                                                    "${item?.ItemCode}"
                                        )

                                    } else {
                                        Toast.makeText(
                                            context,
                                            respuesta.message ?: "El Picking no ha sido iniciado",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                } catch (e: Exception) {

                                    Log.e(
                                        "*MAKITA*PICKING*",
                                        "ERROR OBTENIENDO ID CABECERA",
                                        e
                                    )

                                    Toast.makeText(
                                        context,
                                        e.message ?: "Error al consultar el Picking",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },

                        modifier = Modifier
                            .width(300.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 12.dp)
                            .height(50.dp),

                        shape = RectangleShape,

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4FC3F7),
                            contentColor = Color.White
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.CheckBox,
                            contentDescription = "Revisar lo capturado",
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "REVISAR ITEM PROCESADO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            item?.let {

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
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = it.ItemCode ?: "",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Text(
                                text = it.ItemName ?: "",
                                fontSize = 13.sp,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${it.ItmsGrpCod ?: ""} - ${it.ItmsGrpNam ?: ""}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )

                        }

                        //Spacer(modifier = Modifier.width(8.dp))
                        Spacer(modifier = Modifier.height(8.dp))

                        ElevatedCard(
                            shape = RectangleShape,
                            elevation = CardDefaults.elevatedCardElevation(
                                defaultElevation = 8.dp
                            ),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = Color(0xFF00695C)
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                ),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "Recoger Cantidad",
                                    color = Color.White,
                                    fontSize = 10.sp
                                )

                                Text(
                                    text = "${it.CantidadBin?.roundToInt() ?: 0}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )

                            }

                        }

                    }

                }

            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {

                    CoroutineScope(Dispatchers.Main).launch {

                        try {

                            val request = IniciarPickingLecturaRequest(
                                usuario = usuario
                            )

                            val respuesta = apiService.iniciarPickingLectura(
                                AbsEntry,
                                request
                            )

                            Log.d("*MAKITA*", "Respuesta: $respuesta")

                            if (respuesta.success) {

                                Log.d(
                                    "*MAKITA*PICKING*",
                                    "IdDetalle = ${item?.IdDetalle}"
                                )

                                // Volver a consultar porque ahora PickingDetalle
                                // ya existe en SQL Server
                                val resultadoActualizado =
                                    apiService.obtenerUbicacionPickingDetalle(
                                        AbsEntry,
                                        BinAbs
                                    )

                                detalle = resultadoActualizado.data

                                val itemActualizado =
                                    resultadoActualizado.data.firstOrNull()


                                navController.navigate(
                                    "lecturaPicking/" +
                                            "${respuesta.idCabecera}/" +
                                            "${itemActualizado?.IdDetalle}/" +
                                            "$AbsEntry/" +
                                            "${itemActualizado?.BinCode}/" +
                                            "$BinAbs/" +
                                            "${itemActualizado?.WhsCode}/" +
                                            "${itemActualizado?.ItemCode}"
                                )

                            } else {

                                Toast.makeText(
                                    context,
                                    respuesta.message,
                                    Toast.LENGTH_LONG
                                ).show()

                            }

                        } catch (e: Exception) {

                            Log.e("*MAKITA*", "ERROR", e)

                            Toast.makeText(
                                context,
                                e.message ?: "Error",
                                Toast.LENGTH_LONG
                            ).show()

                        }

                    }

                },

                modifier = Modifier
                    .width(300.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 12.dp)
                    .navigationBarsPadding()
                    .height(50.dp),
                    shape = RectangleShape,  ///probar con RoundedCornerShape(10.dp)
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4FC3F7),

                    contentColor = Color.White),
                    enabled = !pickingCompleto,
            ) {

                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "INICIAR LECTURA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

            }



        }

    }

}