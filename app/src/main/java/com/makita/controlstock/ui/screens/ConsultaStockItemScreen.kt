package com.makita.controlstock.ui.screens

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.makita.controlstock.data.network.RetrofitClient.apiService
import com.makita.controlstock.data.network.UbicacionResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material3.ElevatedCard
import androidx.compose.ui.graphics.RectangleShape


@Composable

fun ConsultaStockItemScreen(navController: NavController) {

    var itemScaneado by remember { mutableStateOf("") }
    var itemScaneadoActual by rememberSaveable { mutableStateOf("") }
    var lastInput by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    val itemFocusRequester = remember {
        FocusRequester()
    }

    var response by remember {
        mutableStateOf<List<UbicacionResponse>>(emptyList())
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    var mostrarDialogoWifiError by remember {
        mutableStateOf(false)
    }

    var textoDialogoWifiError by remember {
        mutableStateOf("")
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var mensajeError by remember {
        mutableStateOf("")
    }

    var secondTextFieldValue by remember {
        mutableStateOf("")
    }

    fun limpiarCampoScanner() {
        itemScaneado = ""
        itemScaneadoActual = ""
        lastInput = ""
        keyboardController?.hide()
    }

    fun mostrarAviso(mensaje: String) {
        mensajeError = mensaje
        showDialog = true
    }

    fun validarItem(ItemIngresado: String) {

        val itemLimpio = ItemIngresado.trim()

        if (itemLimpio.isEmpty()) {
            mostrarAviso("Ingrese un item")
            return
        }

        coroutineScope.launch {

            try {

                val apiResponse = withContext(Dispatchers.IO) { apiService.obtenerStockItem(itemLimpio) }
                val datos = apiResponse.data.orEmpty()

               if (apiResponse.status != 200 || datos.isEmpty()) {

                    response = emptyList()
                    secondTextFieldValue = ""

                    val mensaje = apiResponse.error
                        ?: "El item $itemLimpio no tiene stock disponible"

                    limpiarCampoScanner()

                    mostrarAviso(mensaje)

                    return@launch
                }

                // Validar que las ubicaciones estén informadas.
                val tieneValoresNulos = datos.any {
                    it.ubicacion.isNullOrBlank()
                }

                if (tieneValoresNulos) {

                    response = emptyList()
                    secondTextFieldValue = ""

                    limpiarCampoScanner()

                    mostrarAviso(
                        "No se encontraron ubicaciones válidas para el item $itemLimpio"
                    )

                    return@launch
                }

                // Mostrar los datos recibidos.
                response = datos

                secondTextFieldValue =
                    datos.firstOrNull()?.descripcion ?: ""


                limpiarCampoScanner()



                itemFocusRequester.requestFocus()

            } catch (e: Exception) {



                limpiarCampoScanner()

                textoDialogoWifiError =
                    "No fue posible consultar el stock. " +
                            (e.message ?: "Error desconocido")

                mostrarDialogoWifiError = true
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {


        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
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
                    text = "Stock por Item",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        LaunchedEffect(Unit) {
            itemFocusRequester.requestFocus()
        }

        OutlinedTextField(
            value = itemScaneado,
            onValueChange = { texto ->
                val cleanedText = texto
                    .replace("\n", "")
                    .replace("\r", "")
                    .replace("\t", "")
                    .uppercase()
                    .take(20)

                itemScaneado = cleanedText
                itemScaneadoActual = cleanedText
            },
            label = {
                Text("Código del artículo")
            },
            placeholder = {
                Text("Digite o escanee el código")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Código del artículo"
                )
            },
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (itemScaneado.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                itemScaneado = ""
                                itemScaneadoActual = ""
                                lastInput = ""
                                itemFocusRequester.requestFocus()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar"
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            keyboardController?.hide()
                            validarItem(itemScaneado)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Buscar item"
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(60.dp)
                .focusRequester(itemFocusRequester)
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f, fill = true)
        ) {

            items(response) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Item",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.item ?: "",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Left
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Nombre",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.descripcion ?: "",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Ubicacion",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.ubicacion ?: "",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Bodega",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.bodega ?: "",
                                textAlign = TextAlign.End
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Cantidad",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.cantidad.toDouble().toInt().toString(),
                                textAlign = TextAlign.End,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Estado",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.estado ?: "",
                                textAlign = TextAlign.End
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Ubicacion estandar",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.ubicacionStandar ?: "",
                                textAlign = TextAlign.End
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Primera ubicación",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.primeraUbicacion ?: "",
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        TextField(
            value = secondTextFieldValue,
            onValueChange = {},
            readOnly = true,
            label = {
                Text("Descripción del item")
            },
            modifier = Modifier
                .width(300.dp)
                .height(60.dp)
        )

        // Diálogo de error de conexión.
        if (mostrarDialogoWifiError) {

            AlertDialog(
                onDismissRequest = {
                    mostrarDialogoWifiError = false
                    itemFocusRequester.requestFocus()
                },
                title = {
                    Text(
                        "Error de conexion",
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
                            itemFocusRequester.requestFocus()
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }

        // Diálogo para artículos sin stock u otros avisos.
        if (showDialog) {

            mostrarDialogo15(
                titulo = "Aviso",
                mensaje = mensajeError,
                onDismiss = {
                    showDialog = false
                    itemFocusRequester.requestFocus()
                }
            )
        }
    }
}


@Composable
fun mostrarDialogo15(
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

