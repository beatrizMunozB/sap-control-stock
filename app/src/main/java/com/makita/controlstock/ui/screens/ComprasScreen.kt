package com.makita.controlstock.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.makita.controlstock.CardOpcion
import com.makita.controlstock.model.OpcionDashboard
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton


@Composable
fun ComprasScreen(navController: NavController) {

    var descripcionSeleccionada by remember {
        mutableStateOf<String?>(null)
    }

    val opciones = listOf(
        OpcionDashboard(
            "Ordenes de Compra",
            "Recepcion de mercancia con Ordenes de Compra",
            Icons.Default.Description,
            "compras_ordenes"
        ),
        OpcionDashboard(
            "Facturas de Reserva",
            "Factura de Reserva",
            Icons.Default.Assignment,
            "compras_reserva"
        ),
        OpcionDashboard(
            "Posicionamiento Recepcion",
            "Posicionamiento Recepcion",
            Icons.Default.MoveToInbox,
            "compras_recepcion"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

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
                text = "Compras",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(opciones) { opcion ->

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        CardOpcion(opcion, navController)
                    }

                    IconButton(
                        onClick = {
                            descripcionSeleccionada = when (opcion.ruta) {

                                "compras_ordenes" ->
                                    "Permite realizar la recepción física de mercadería asociada a órdenes de compra. Podrás consultar productos pendientes, escanear artículos y registrar cantidades recibidas."

                                "compras_reserva" ->
                                    "Permite consultar las facturas de reserva de proveedores y revisar los artículos asociados. También consultar el stock disponible (BOD01) en la bodega de principal, la captura y el envio a SAP registrado la entrada de Mercancia."

                                "compras_recepcion" ->
                                    "Permite realizar el posicionamiento de mercadería  desde la entrada de mercancia PENDIENTE, asignando los itens a sus respectivas ubicaciones dentro de la bodega."

                                else -> null
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Ver descripcion",
                            tint = Color(0xFF00695C)
                        )
                    }
                }
            }
        }
    }

    // Ventana emergente de descripción

    descripcionSeleccionada?.let { descripcion ->

        AlertDialog(
            onDismissRequest = {
                descripcionSeleccionada = null
            },
            title = {
                Text(
                    text = "Descripcion de la opcion",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = descripcion,
                    fontSize = 15.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        descripcionSeleccionada = null
                    }
                ) {
                    Text("CERRAR")
                }
            }
        )
    }
}
