package com.makita.controlstock.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.makita.controlstock.data.network.RetrofitClient.apiService
import com.makita.controlstock.data.network.StockItemBodegaResponse

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material3.ElevatedCard
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.style.TextAlign
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import java.io.IOException
import com.google.gson.annotations.SerializedName
import com.makita.controlstock.data.network.BodegaLocal
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.filled.Email
import com.makita.controlstock.data.network.EnviarStockBodegaRequest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultaStockBodegaScreen(navController: NavController) {

    var bodegaScaneada by remember { mutableStateOf("") }
    var filtroItem by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val bodegaFocusRequester = remember { FocusRequester() }

    var response by remember {
        mutableStateOf<List<StockItemBodegaResponse>>(emptyList())
    }
    var buscando by remember { mutableStateOf(false) }
    var bodegaCargada by remember { mutableStateOf(false) }
    var mostrarDialogoWifiError by remember { mutableStateOf(false) }
    var textoDialogoWifiError by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }

    var listaBodegas by remember {
        mutableStateOf<List<BodegaLocal>>(emptyList())
    }
    var cargandoBodegas by remember { mutableStateOf(true) }
    var mostrarBodegas by remember { mutableStateOf(false) }
    var enviandoCorreo by remember { mutableStateOf(false) }

    fun mostrarAviso(mensaje: String) {
        mensajeError = mensaje
        showDialog = true
    }

    // CARGAR BODEGAS DEL LOCAL
    LaunchedEffect(Unit) {
        cargandoBodegas = true

        try {
            val respuesta = withContext(Dispatchers.IO) {
                apiService.obtenerBodegasLocal()
            }

            if (respuesta.success) {
                listaBodegas = respuesta.data
            } else {
                mostrarAviso("No fue posible obtener las bodegas autorizadas.")
            }
        } catch (e: Exception) {
            Log.e("*MAKITA*", "Error al obtener bodegas", e)
            mostrarAviso(
                "Error al cargar las bodegas: ${e.message ?: "Error desconocido"}"
            )
        } finally {
            cargandoBodegas = false
        }
    }

    // CONSULTAR STOCK DE LA BODEGA
    fun buscarStock() {
        val bodegaLimpia = bodegaScaneada.trim().uppercase()

        if (bodegaLimpia.isEmpty()) {
            mostrarAviso("Ingrese codigo bodega.")
            return
        }

        if (cargandoBodegas) {
            mostrarAviso("Espere mientras se cargan las bodegas.")
            return
        }

        if (listaBodegas.isEmpty()) {
            mostrarAviso("No hay bodegas disponibles para esta sucursal.")
            return
        }

        val bodegaValida = listaBodegas.any {
            it.whsCode.equals(bodegaLimpia, ignoreCase = true)
        }

        if (!bodegaValida) {
            response = emptyList()
            bodegaCargada = false
            mostrarAviso(
                "La bodega $bodegaLimpia no pertenece a las bodegas autorizadas para este local."
            )
            return
        }

        mostrarBodegas = false
        keyboardController?.hide()

        coroutineScope.launch {
            buscando = true

            try {
                Log.d("*MAKITA*", "CONSULTANDO STOCK DE BODEGA: $bodegaLimpia")

                val apiResponse = withContext(Dispatchers.IO) {
                    apiService.obtenerStockItemPorBodega(bodegaLimpia)
                }

                val datos = apiResponse.data.orEmpty()

                if (apiResponse.status != 200 || datos.isEmpty()) {
                    response = emptyList()
                    bodegaCargada = false

                    mostrarAviso(
                        apiResponse.error
                            ?: "No hay items con stock en la bodega $bodegaLimpia."
                    )
                    return@launch
                }

                response = datos
                bodegaCargada = true
                filtroItem = ""

                Log.d(
                    "*MAKITA*",
                    "Stock cargado: ${datos.size} items de $bodegaLimpia"
                )
            } catch (e: Exception) {
                Log.e("*MAKITA*", "Error al consultar stock por bodega", e)

                response = emptyList()
                bodegaCargada = false
                textoDialogoWifiError =
                    "No fue posible consultar el stock. ${e.message ?: "Error desconocido"}"
                mostrarDialogoWifiError = true
            } finally {
                buscando = false
            }
        }
    }


    val itemsFiltrados = response.filter { item ->
        val filtro = filtroItem.trim()

        filtro.isBlank() ||
                item.ItemCode.orEmpty().contains(filtro, ignoreCase = true) ||
                item.ItemName.orEmpty().contains(filtro, ignoreCase = true) ||
                item.CodigoBarra.orEmpty().contains(filtro, ignoreCase = true)
    }


    val bodegasFiltradas = listaBodegas.filter { bodega ->
        val filtro = bodegaScaneada.trim()

        filtro.isBlank() ||
                bodega.whsCode.contains(filtro, ignoreCase = true) ||
                bodega.whsName.contains(filtro, ignoreCase = true)
    }

    // EXPORTAR STOCK A CSV
    val exportarStockLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            try {
                val contenidoCSV = buildString {
                    append('\uFEFF')

                    appendLine(
                        listOf(
                            "Item",
                            "Descripcion",
                            "Bodega",
                            "Stock",
                            "CodigoGrupo",
                            "NombreGrupo",
                            "CodigoBarra"
                        ).joinToString(";")
                    )

                    response.forEach { item ->
                        val columnas = listOf(
                            item.ItemCode.orEmpty(),
                            item.ItemName.orEmpty(),
                            item.WhsCode.orEmpty(),
                            item.Stock?.toString() ?: "0",
                            item.Grupo?.toString() ?: "",
                            item.NombreGrupo.orEmpty(),
                            item.CodigoBarra.orEmpty()
                        )

                        appendLine(
                            columnas.joinToString(";") { valor ->
                                "\"" + valor.replace("\"", "\"\"") + "\""
                            }
                        )
                    }
                }

                val outputStream = context.contentResolver.openOutputStream(uri)
                    ?: throw java.io.IOException("No se pudo abrir el archivo.")

                outputStream.use { output ->
                    output.write(contenidoCSV.toByteArray(Charsets.UTF_8))
                }

                Toast.makeText(
                    context,
                    "Stock exportado correctamente.",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Log.e("*MAKITA*", "Error al exportar stock", e)

                Toast.makeText(
                    context,
                    "Error al guardar el archivo: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // ENCABEZADO
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RectangleShape,
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 8.dp
            ),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFF00695C),
                contentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }

                Text(
                    text = "Stock por Bodega",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = mostrarBodegas && bodegasFiltradas.isNotEmpty(),
                onExpandedChange = { mostrarBodegas = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = bodegaScaneada,
                    onValueChange = { texto ->
                        bodegaScaneada = texto
                            .replace("\n", "")
                            .replace("\r", "")
                            .replace("\t", "")
                            .uppercase()
                            .take(20)

                        mostrarBodegas = true
                        bodegaCargada = false
                        response = emptyList()
                    },
                    label = { Text("Bodega") },
                    placeholder = {
                        Text(
                            if (cargandoBodegas) "Cargando..."
                            else "Ingrese Bodega",
                            maxLines = 1
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Bodega"
                        )
                    },
                    trailingIcon = {
                        if (bodegaScaneada.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    bodegaScaneada = ""
                                    filtroItem = ""
                                    response = emptyList()
                                    bodegaCargada = false
                                    mostrarBodegas = false
                                    bodegaFocusRequester.requestFocus()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpiar bodega"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    enabled = !cargandoBodegas,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { buscarStock() }
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .height(60.dp)
                        .focusRequester(bodegaFocusRequester)
                )

                ExposedDropdownMenu(
                    expanded = mostrarBodegas && bodegasFiltradas.isNotEmpty(),
                    onDismissRequest = { mostrarBodegas = false }
                ) {
                    bodegasFiltradas.forEach { bodega ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = bodega.whsCode,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = bodega.whsName,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            },
                            onClick = {
                                bodegaScaneada = bodega.whsCode
                                mostrarBodegas = false
                                keyboardController?.hide()
                                bodegaFocusRequester.requestFocus()
                            }
                        )
                    }
                }
            }

            Button(
                onClick = { buscarStock() },
                enabled = !buscando && !cargandoBodegas,
                shape = RectangleShape,
                contentPadding = PaddingValues(
                    horizontal = 6.dp,
                    vertical = 0.dp
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0)
                )
            ) {
                if (buscando) {
                    Text("...", fontSize = 12.sp)
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cargar stock"
                        )
                        Text(
                            text = "Cargar",
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // FILA 2: DESCARGAR + ENVIAR CORREO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val bodega = bodegaScaneada.trim().uppercase()
                    exportarStockLauncher.launch("Stock_${bodega}.csv")
                },
                enabled = bodegaCargada && response.isNotEmpty() && !buscando,
                shape = RectangleShape,
                contentPadding = PaddingValues(horizontal = 6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Descargar stock"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Descargar",
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }


            Button(
                onClick = {
                    val bodega = bodegaScaneada.trim().uppercase()

                    if (bodega.isBlank()) {
                        mostrarAviso("Seleccione una bodega.")
                        return@Button
                    }

                    if (response.isEmpty()) {
                        mostrarAviso("Primero debe cargar el stock de la bodega.")
                        return@Button
                    }

                    coroutineScope.launch {
                        enviandoCorreo = true

                        try {
                            val resultado = withContext(Dispatchers.IO) {
                                apiService.enviarStockBodega(
                                    EnviarStockBodegaRequest(
                                        bodega = bodega,
                                        detalle = response
                                    )
                                )
                            }

                            if (resultado.status == 200) {
                                mostrarAviso(
                                    "${resultado.mensaje}\n" +
                                            "Bodega: ${resultado.bodega ?: bodega}\n" +
                                            "Items: ${resultado.totalItems ?: response.size}"
                                )
                            } else {
                                mostrarAviso(resultado.mensaje)
                            }

                        } catch (e: Exception) {
                            Log.e("*MAKITA*", "Error al enviar stock por correo", e)
                            mostrarAviso(
                                "Error al enviar el correo: " +
                                        (e.message ?: "Error de conexión")
                            )
                        } finally {
                            enviandoCorreo = false
                        }
                    }
                },
                enabled = bodegaCargada &&
                        response.isNotEmpty() &&
                        !buscando &&
                        !enviandoCorreo,
                shape = RectangleShape,
                contentPadding = PaddingValues(horizontal = 6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF455A64)
                )
            ) {
                if (enviandoCorreo) {
                    Text(
                        text = "Enviando...",
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Enviar stock por correo"
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "Enviar correo",
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }

        }

        Spacer(modifier = Modifier.height(10.dp))

        // BUSCADOR DE ITEMS
        OutlinedTextField(
            value = filtroItem,
            onValueChange = { texto ->
                filtroItem = texto
                    .replace("\n", "")
                    .replace("\r", "")
                    .replace("\t", "")
                    .uppercase()
                    .take(50)
            },
            enabled = bodegaCargada,
            label = { Text("Buscar item") },
            placeholder = { Text("Código o descripción") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar item"
                )
            },
            trailingIcon = {
                if (filtroItem.isNotEmpty()) {
                    IconButton(onClick = { filtroItem = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar búsqueda"
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .height(60.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // CONTADOR
        if (bodegaCargada) {
            Text(
                text = "Items encontrados: ${itemsFiltrados.size} de ${response.size}",
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // RESULTADOS
        LazyColumn(
            modifier = Modifier
                .weight(1f, fill = true)
                .fillMaxWidth()
        ) {
            items(itemsFiltrados) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(3.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {

                        // ENCABEZADO GRIS: ITEM + DESCRIPCIÓN
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE0E0E0))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.ItemCode ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.widthIn(
                                    min = 75.dp,
                                    max = 115.dp
                                )
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = item.ItemName ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // DATOS DEL ITEM
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Bodega",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    item.WhsCode ?: "",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Stock físico",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = item.Stock?.let {
                                        if (it % 1.0 == 0.0) {
                                            it.toInt().toString()
                                        } else {
                                            it.toString()
                                        }
                                    } ?: "0",
                                    fontSize = 12.sp,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Grupo",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    item.Grupo?.toString() ?: "0",
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Nombre grupo",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = item.NombreGrupo ?: "",
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "EAN",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = item.CodigoBarra ?: "",
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            if (bodegaCargada && itemsFiltrados.isEmpty()) {
                item {
                    Text(
                        text = "No se encontraron items que coincidan con la búsqueda.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // DIÁLOGO DE ERROR DE CONEXIÓN
        if (mostrarDialogoWifiError) {
            AlertDialog(
                onDismissRequest = {
                    mostrarDialogoWifiError = false
                    bodegaFocusRequester.requestFocus()
                },
                title = {
                    Text(
                        text = "Error de conexión",
                        color = Color.Red
                    )
                },
                text = {
                    Text(textoDialogoWifiError)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarDialogoWifiError = false
                            bodegaFocusRequester.requestFocus()
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }

        // DIÁLOGO DE AVISOS
        if (showDialog) {
            mostrarDialogo15(
                titulo = "Aviso",
                mensaje = mensajeError,
                onDismiss = {
                    showDialog = false
                    bodegaFocusRequester.requestFocus()
                }
            )
        }
    }
}







@Composable
fun mostrarDialogo33(
    titulo: String,
    mensaje: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = titulo) },
        text = { Text(text = mensaje) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}