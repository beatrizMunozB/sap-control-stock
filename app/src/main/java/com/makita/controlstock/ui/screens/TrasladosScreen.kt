package com.makita.controlstock.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.SwapHoriz
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

@Composable
fun TrasladosScreen(navController: NavController,usuario: String) {

    val opciones = listOf(
        OpcionDashboard("Inmediato", "Traspaso de items entre ubicaciones", Icons.Default.SwapHoriz, "traslado_inmediato/$usuario"),
        OpcionDashboard("Posicionar Transito (Salida)", "Salida de Mercaderia", Icons.Default.Outbox, "traslado_salida/$usuario"),
        OpcionDashboard("Posicionar Transito (Entrada)", "Entrada de Mercaderia", Icons.Default.MoveToInbox, "traslado_entrada/$usuario"),
        OpcionDashboard("Traslado con Solicitud", "Consulta de stock por Bodega", Icons.Default.Description, "consulta_bodega/$usuario")
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

            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }

            Text(
                text = "Traslados",
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
                CardOpcion(opcion, navController)
            }
        }
    }
}
