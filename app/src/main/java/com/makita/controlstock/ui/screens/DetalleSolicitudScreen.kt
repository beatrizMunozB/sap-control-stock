package com.makita.controlstock.ui.screens

import android.util.Log
import android.widget.Toast
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


@Composable
fun DetalleSolicitudScreen(
    docEntry: Int,
    DocNum: Int,
    CardCode: String,
    CardName: String,
    FromWarehouse: String,
    ToWarehouse: String,
    navController: NavHostController
) {

    val detalle = remember {
        mutableStateOf<List<SolicitudDetalleItem>>(emptyList())
    }
    val context = LocalContext.current


    LaunchedEffect(docEntry) {

        try {

            val resultado =
                apiService.obtenerDetalleSolicitud(docEntry)

            detalle.value = resultado.data

        } catch (e: Exception) {

            Log.e("*MAKITA*", e.message ?: "")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "TRASLADO CON SOLICITUD",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF00897B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = CardName,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF3F3F3)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Código ",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = CardCode,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Nro Orden",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = DocNum.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn (
                modifier = Modifier.weight(1f)
            ) {

                items(detalle.value) { item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {

                            /*
                            Text(
                                text = "Línea ${item.LineNum}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00897B),
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))
                            */


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Text(
                                    text = item.ItemCode,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )

                                Text(
                                    text = "Cant: ${item.Quantity.toInt()}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF00897B)
                                )
                            }

                            Text(
                                text = item.Dscription,
                                fontSize = 12.sp
                            )


                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

/// aca COPIAR LA LECTURA

            Button(
                onClick = {

                    CoroutineScope(Dispatchers.Main).launch {

                        try {

                            Log.d("*MAKITA*", "Iniciando lectura DocEntry: $docEntry")

                            val request = IniciarSolicitudLecturaRequest(
                                usuario = usuario
                            )

                            val respuesta = apiService.iniciarSolicitudLectura(docEntry,request)

                            if (respuesta.success) {

                                Log.d("*MAKITA*", "IdCabecera: ${respuesta.idCabecera}")
///
                                navController.navigate(
                                    "lecturaSolicitud/${respuesta.idCabecera}"
                                )

                            } else {

                                Toast.makeText(
                                    context,
                                    respuesta.message,
                                    Toast.LENGTH_LONG
                                ).show()

                            }

                            // Después de que el backend responda,
                            // recién navegaremos.

                        } catch (e: Exception) {

                            Log.e("*MAKITA*", "ERROR", e)



                        }

                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp)
                    .height(55.dp)
            ) {
                Text(
                    text = "INICIAR LECTURA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

        }
    }
}