package com.makita.controlstock.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.makita.controlstock.data.network.RecepcionPendiente
import com.makita.controlstock.data.network.RetrofitClient.apiService
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material.icons.Icons


@Composable
fun ComprasRecepcionScreen(navController: NavHostController) {

    var numeroEntrada by remember { mutableStateOf("") }
    var recepciones by remember { mutableStateOf<List<RecepcionPendiente>>(emptyList()) }
    var mensajeError by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }


    fun buscarRecepciones(docNum: Int? = null) {

        CoroutineScope(Dispatchers.Main).launch {

            try {

                cargando = true

                val respuesta = apiService.obtenerRecepcionesPendientes(
                    docNum = docNum
                )

                if (respuesta.success) {

                    recepciones = respuesta.data ?: emptyList()

                    if (recepciones.isEmpty()) {
                        mensajeError = "No se encontraron entradas de mercancías"
                        showDialog = true
                    }

                } else {

                    mensajeError = respuesta.message ?: "Error al consultar entradas"
                    showDialog = true
                }

            } catch (e: Exception) {

                Log.e("*MAKITA*", "Error buscando recepciones", e)

                mensajeError = "Error al consultar las entradas: ${e.message}"
                showDialog = true

            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(Unit) {
        buscarRecepciones()
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
                        text = "Posicionamiento Recepcion",
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
                    value = numeroEntrada,
                    onValueChange = {
                        numeroEntrada = it.filter { char ->
                            char.isDigit()
                        }
                    },
                    label = {
                        Text("Número de Entrada de Mercancías")
                    },
                    placeholder = {
                        Text("Ingrese o escanee el número")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            buscarRecepciones()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))



                Button(
                    onClick = {

                        val numero = numeroEntrada.trim()
                        if (numero.isEmpty()) {
                            buscarRecepciones()
                        } else {
                            buscarRecepciones(numero.toIntOrNull())
                        }
                    },
                    enabled = !cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                     colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4FC3F7),
                        contentColor = Color.Black
                    ),
                    shape = RectangleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("BUSCAR")

                }

                Spacer(modifier = Modifier.height(16.dp))

                // LISTADO DE RECEPCIONES

                Text(
                    text = "Entradas encontradas: ${recepciones.size}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (cargando) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {

                        items(recepciones) { item ->

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 4.dp,
                                        vertical = 5.dp
                                    )
                                    .clickable {

                                        navController.navigate(
                                            "compras_recepcion_detalle/${item.AbsEntry}/${item.DocNum}"
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
                                    modifier = Modifier.padding(14.dp)
                                ) {

                                    // NÚMERO DE ENTRADA

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Text(
                                            text = "Entrada N° ${item.DocNum}",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Icon(
                                            imageVector = Icons.Default.ArrowForwardIos,
                                            contentDescription = "Ver detalle",
                                            tint = Color(0xFF00695C),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = item.CardName ?: "",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "RUT: ${item.LicTradNum ?: ""}",
                                        fontSize = 13.sp
                                    )

                                    Text(
                                        text = "Código proveedor: ${item.CardCode ?: ""}",
                                        fontSize = 13.sp
                                    )

                                    Text(
                                        text = "Fecha: ${item.PickDate?.take(10) ?: ""}",
                                        fontSize = 13.sp
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    HorizontalDivider()

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Identificador: ${item.AbsEntry}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )

                                    Text(
                                        text = "Estado: ${item.Name ?: "SIN ASIGNACION"}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF00695C)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO DE AVISOS

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
