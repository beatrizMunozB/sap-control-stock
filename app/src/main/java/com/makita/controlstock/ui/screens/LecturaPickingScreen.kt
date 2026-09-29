package com.makita.controlstock.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import com.makita.controlstock.data.network.RetrofitClient.apiService
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.makita.controlstock.data.network.IniciarPickingLecturaRequest
import com.makita.controlstock.data.network.ItemNombreResponse
import com.makita.controlstock.data.network.ItemResponse
import com.makita.controlstock.data.network.RegistrarPickingCapturaRequest
import com.makita.controlstock.session.Sesion
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.media.MediaPlayer
import android.provider.Settings
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper


@Composable
fun LecturaPickingScreen(
    idCabecera: Int,
    idDetalle: Int,
    absEntry: Int,
    binCode: String,
    binAbs: Int,
    whsCode: String,
    itemCode : String,
    barCode: String,
    navController: NavHostController
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var ubicacionEscaneada by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var bodegaOrigen by rememberSaveable { mutableStateOf("") }
    var localOrigen by rememberSaveable { mutableStateOf("") }
    var ubicacionOrigen by rememberSaveable { mutableStateOf("") }
    var itemEscaneado by remember { mutableStateOf("") }
    var CantidadEscaneada by remember { mutableStateOf("") }
    var manejaUbicacionDestino by remember { mutableStateOf(false) }
    val ubicacionEscaneadaRequester = remember { FocusRequester() }
    var mensajeError by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var binAbsOrigen by remember { mutableStateOf<Int?>(null) }
    var binCodeOrigen by remember { mutableStateOf("") }
    var toWarehouse by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var extractedText    by remember { mutableStateOf("") }
    var extractedText2   by remember { mutableStateOf("") }
    var extractedText3   by remember { mutableStateOf("") }
    var extractedText4   by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var gTipoItem        by remember { mutableStateOf("") }
    var cantidad         by remember { mutableStateOf("") }
    val extractedTextFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val cantidadFocusRequester = remember { FocusRequester() }
    val itemFocusRequester = remember { FocusRequester() }
    var codigoEsperado by remember { mutableStateOf("") }
    var mensajeError2 by remember { mutableStateOf("") }
    var textFieldValue2 by remember { mutableStateOf("") }
    var response35: String
    var response     by rememberSaveable { mutableStateOf<List<ItemNombreResponse>>(emptyList()) }
    var modoManual by remember { mutableStateOf(false) }

    val request = IniciarPickingLecturaRequest(
        usuario = Sesion.usuario
    )
    var showDialogItem     by remember { mutableStateOf(false) }
    var showDialogCantidad by remember { mutableStateOf(false) }
    var showDialogSerie    by remember { mutableStateOf(false) }
    var mostrarDialogoSiguienteUbicacion by remember {
        mutableStateOf(false)
    }
    var mensajeSiguienteUbicacion by remember {
        mutableStateOf("")
    }

    val estiloTextoRojo = TextStyle(
        fontSize = 16.sp,
        color = Color.Red,
        fontWeight = FontWeight.Bold
    )
    var lecturaItemJob by remember {
        mutableStateOf<kotlinx.coroutines.Job?>(null)
    }


    fun procesarCantidad() {

        keyboardController?.hide()

        if (CantidadEscaneada.isNotBlank()) {

            coroutineScope.launch {

                try {

                    keyboardController?.hide()

                    if (CantidadEscaneada.isNotBlank()) {

                        coroutineScope.launch {

                            try {

                                Log.d(
                                    "*MAKITA*PICKING*",
                                    "ENVIANDO CAPTURA → " +
                                            "idDetalle=$idDetalle, " +
                                            "cantidad=${CantidadEscaneada.toInt()}, " +
                                            "whsCode=$whsCode, " +
                                            "binAbs=$binAbs, " +
                                            "usuario=${Sesion.usuario}"
                                )

                                when (gTipoItem.trim().uppercase()) {

                                    // =================================================
                                    // HERRAMIENTAS
                                    // =================================================
                                    "HERRAMIENTAS" -> {

                                        val serieDesdeTexto = extractedText2.trim()
                                        val serieHastaTexto = extractedText3.trim()

                                        if (serieDesdeTexto.isBlank() || serieHastaTexto.isBlank()) {
                                            mensajeError2 = "Rango de series inválido"
                                            showDialogCantidad = true
                                            return@launch
                                        }

                                        val serieDesde = serieDesdeTexto.toLongOrNull()
                                        val serieHasta = serieHastaTexto.toLongOrNull()

                                        if (serieDesde == null || serieHasta == null) {
                                            mensajeError2 = "Rango de series inválido"
                                            showDialogCantidad = true
                                            return@launch
                                        }

                                        if (serieDesde > serieHasta) {
                                            mensajeError2 =
                                                "La serie desde ($serieDesdeTexto) no puede ser mayor que la serie hasta ($serieHastaTexto)"
                                            showDialogCantidad = true
                                            return@launch
                                        }

                                        // =================================================
                                        // CANTIDAD REAL DE LA ETIQUETA
                                        //
                                        // 000003752 -> 000003752 = 1
                                        // 000003752 -> 000003754 = 3
                                        // =================================================

                                        val cantidadEtiqueta = (serieHasta - serieDesde) + 1L

                                        // La cantidad viene de la etiqueta.
                                        CantidadEscaneada = cantidadEtiqueta.toString()
                                        cantidad = cantidadEtiqueta.toString()

                                        Log.d(
                                            "*MAKITA*PICKING*",
                                            "HERRAMIENTA | " +
                                                    "SerieDesde=$serieDesdeTexto | " +
                                                    "SerieHasta=$serieHastaTexto | " +
                                                    "CantidadEtiqueta=$cantidadEtiqueta"
                                        )

                                        // =================================================
                                        // OBTENER CANTIDAD DEL DETALLE ACTUAL
                                        // =================================================

                                        val datosPicking =
                                            apiService.obtenerUbicacionPickingDetalle(
                                                absEntry,
                                                binAbs
                                            )

                                        val detalle =
                                            datosPicking.data.firstOrNull {
                                                it.IdDetalle == idDetalle
                                            }

                                        if (detalle == null) {
                                            mensajeError2 = "No se encontró el detalle del picking."
                                            showDialogCantidad = true
                                            return@launch
                                        }

                                        val cantidadLiberada =
                                            detalle.CantidadLiberada
                                                .trim()
                                                .replace(",", ".")
                                                .toDoubleOrNull()

                                        if (cantidadLiberada == null) {
                                            mensajeError2 =
                                                "No se pudo obtener la cantidad solicitada del artículo."
                                            showDialogCantidad = true
                                            return@launch
                                        }

                                        val cantidadCapturada =
                                            detalle.CantidadCapturada ?: 0.0

                                        val cantidadPendiente =
                                            cantidadLiberada - cantidadCapturada

                                        Log.d(
                                            "*MAKITA*PICKING*",
                                            "HERRAMIENTA | " +
                                                    "CantidadLiberada=$cantidadLiberada | " +
                                                    "CantidadCapturada=$cantidadCapturada | " +
                                                    "CantidadPendiente=$cantidadPendiente | " +
                                                    "CantidadEtiqueta=$cantidadEtiqueta"
                                        )

                                        // =================================================
                                        // YA CUMPLIÓ LA CANTIDAD SOLICITADA
                                        // =================================================

                                        if (cantidadPendiente <= 0) {

                                            mensajeError2 =
                                                "El artículo identificado ya cumplió con su cantidad solicitada."

                                            showDialogCantidad = true

                                            CantidadEscaneada = ""

                                            delay(100)

                                            text = ""
                                            extractedText = ""
                                            extractedText2 = ""
                                            extractedText3 = ""
                                            extractedText4 = ""
                                            textFieldValue2 = ""
                                            cantidad = ""

                                            itemFocusRequester.requestFocus()

                                            return@launch
                                        }

                                        // =================================================
                                        // LA ETIQUETA NO PUEDE SUPERAR LO PENDIENTE
                                        //
                                        // SE VALIDA ANTES DE GUARDAR CUALQUIER SERIE.
                                        // =================================================

                                        if (cantidadEtiqueta.toDouble() > cantidadPendiente) {

                                            val pendienteTexto =
                                                if (cantidadPendiente % 1.0 == 0.0) {
                                                    cantidadPendiente.toInt().toString()
                                                } else {
                                                    cantidadPendiente.toString()
                                                }

                                            mensajeError2 =
                                                "ETIQUETA MASTER\n\n" +
                                                        "ETIQUETA DE CAJA contiene $cantidadEtiqueta unidades.\n\n" +
                                                        "UNIDADES SOLICITADAS: $pendienteTexto\n\n" +
                                                        "ETIQUETA INCORRECTA, INGRESE ETIQUETA INDIVIDUAL,  supera las unidades solicitadas."

                                            showDialogCantidad = true

                                            CantidadEscaneada = ""

                                            delay(100)

                                            text = ""
                                            extractedText = ""
                                            extractedText2 = ""
                                            extractedText3 = ""
                                            extractedText4 = ""
                                            textFieldValue2 = ""
                                            cantidad = ""

                                            itemFocusRequester.requestFocus()

                                            return@launch
                                        }

                                        // =================================================
                                        // CONSERVAR CEROS A LA IZQUIERDA
                                        //
                                        // Ejemplo:
                                        // 000003752 -> 000003754
                                        //
                                        // Guarda:
                                        // 000003752
                                        // 000003753
                                        // 000003754
                                        // =================================================

                                        val anchoSerie =
                                            maxOf(
                                                serieDesdeTexto.length,
                                                serieHastaTexto.length
                                            )

                                        // =================================================
                                        // GUARDAR CADA SERIE
                                        // =================================================

                                        for (serie in serieDesde..serieHasta) {

                                            val numeroSerie =
                                                serie.toString().padStart(
                                                    anchoSerie,
                                                    '0'
                                                )

                                            val request =
                                                RegistrarPickingCapturaRequest(
                                                    idDetalle = idDetalle,
                                                    numeroSerie = numeroSerie,
                                                    cantidad = 1,
                                                    whsCode = whsCode,
                                                    binAbs = binAbs,
                                                    barCode = codigoEsperado.uppercase(),
                                                    usuario = Sesion.usuario
                                                )

                                            Log.d(
                                                "*MAKITA*PICKING*",
                                                "REGISTRANDO HERRAMIENTA | " +
                                                        "Serie=$numeroSerie | cantidad=1"
                                            )

                                            val respuesta =
                                                apiService.registrarPickingCaptura(
                                                    request
                                                )

                                            Log.d(
                                                "*MAKITA*PICKING*",
                                                "Serie $numeroSerie → " +
                                                        "success=${respuesta.success}, " +
                                                        "message=${respuesta.message}, " +
                                                        "detalleCompleto=${respuesta.detalleCompleto}, " +
                                                        "pickingCompleto=${respuesta.pickingCompleto}"
                                            )

                                            if (!respuesta.success) {

                                                mensajeError2 =
                                                    "Error registrando la serie $numeroSerie: ${respuesta.message}"

                                                showDialogCantidad = true

                                                CantidadEscaneada = ""

                                                return@launch
                                            }

                                            // =================================================
                                            // SI TERMINÓ EL DETALLE
                                            // =================================================

                                            if (respuesta.detalleCompleto) {

                                                if (respuesta.pickingCompleto) {

                                                    CantidadEscaneada = ""

                                                    mensajeError2 = "¡PICKING TERMINADO!"

                                                    showDialogCantidad = true

                                                    return@launch

                                                } else {

                                                    /* AQUI SEGUIMOS*/

                                                    val siguiente = respuesta.siguienteDetalle

                                                    if (siguiente != null) {

                                                        CantidadEscaneada = ""

                                                        mensajeError2 =
                                                            "Ubicación completada.\n\n" +
                                                                    "Pasando a la siguiente ubicación..."

                                                        showDialogCantidad = true

                                                        delay(1500)

                                                        showDialogCantidad = false

                                                        navController.navigate(
                                                            "detalleUbicacionPicking/" +
                                                                    "$absEntry/" +
                                                                    "${siguiente.BinAbs}"
                                                        ) {
                                                            popUpTo(
                                                                "detalleUbicacionPicking/$absEntry/$binAbs"
                                                            ) {
                                                                inclusive = true
                                                            }
                                                        }

                                                        return@launch

                                                    } else {

                                                        mensajeError2 =
                                                            "No se encontró el siguiente detalle."

                                                        showDialogCantidad = true

                                                        return@launch
                                                    }
                                                }
                                            }
                                        }

                                        // =================================================
                                        // ETIQUETA REGISTRADA
                                        // =================================================

                                        Log.d(
                                            "*MAKITA*PICKING*",
                                            "TODAS LAS SERIES REGISTRADAS CORRECTAMENTE | " +
                                                    "Cantidad=$cantidadEtiqueta"
                                        )

                                        CantidadEscaneada = ""

                                        mensajeError2 =
                                            if (cantidadEtiqueta == 1L) {
                                                "Herramienta registrada correctamente.\n\n" +
                                                        "Cantidad capturada: 1"
                                            } else {
                                                "Caja MASTER registrada correctamente.\n\n" +
                                                        "Series registradas: $cantidadEtiqueta"
                                            }

                                        showDialogCantidad = true

                                        delay(100)

                                        text = ""
                                        extractedText = ""
                                        extractedText2 = ""
                                        extractedText3 = ""
                                        extractedText4 = ""
                                        textFieldValue2 = ""
                                        cantidad = ""

                                        itemFocusRequester.requestFocus()
                                    }


                                    // =================================================
                                    // ACCESORIOS / REPUESTOS
                                    // =================================================
                                    "ACCESORIOS", "REPUESTOS" -> {

                                        val request =
                                            RegistrarPickingCapturaRequest(
                                                idDetalle = idDetalle,
                                                numeroSerie = "",
                                                cantidad = CantidadEscaneada.toInt(),
                                                whsCode = whsCode,
                                                binAbs = binAbs,
                                                barCode = codigoEsperado,
                                                usuario = Sesion.usuario
                                            )

                                        val respuesta =
                                            apiService.registrarPickingCaptura(
                                                request
                                            )

                                        Log.d(
                                            "*MAKITA*PICKING*",
                                            "Respuesta captura 1 = $respuesta"
                                        )

                                        if (!respuesta.success) {

                                            mensajeError2 = respuesta.message
                                            showDialogCantidad = true
                                            CantidadEscaneada = ""
                                            return@launch

                                        } else {



                                            Log.d(
                                                "*MAKITA*PICKING*",
                                                "CAPTURA REGISTRADA | " +
                                                        "detalleCompleto=${respuesta.detalleCompleto} | " +
                                                        "pickingCompleto=${respuesta.pickingCompleto} | " +
                                                        "siguienteDetalle=${respuesta.siguienteDetalle}"
                                            )

                                            if (respuesta.detalleCompleto) {

                                                if (respuesta.pickingCompleto) {

                                                    mensajeError2 = "¡PICKING TERMINADO!"
                                                    showDialogCantidad = true

                                                } else {

                                                    val siguiente = respuesta.siguienteDetalle

                                                    if (siguiente != null) {

                                                        Log.d(
                                                            "*MAKITA*PICKING*",
                                                            "NAVEGANDO AL SIGUIENTE DETALLE | " +
                                                                    "Id=${siguiente.Id} | " +
                                                                    "Item=${siguiente.ItemCode} | " +
                                                                    "Bin=${siguiente.BinCode} | " +
                                                                    "BinAbs=${siguiente.BinAbs}"
                                                        )

                                                        navController.navigate(
                                                            "detalleUbicacionPicking/" +
                                                                    "$absEntry/" +
                                                                    "${siguiente.BinAbs}"

                                                        ) {
                                                            popUpTo("detalleUbicacionPicking/$absEntry/$binAbs") {
                                                                inclusive = true
                                                            }
                                                        }

                                                    } else {

                                                        Log.d(
                                                            "*MAKITA*PICKING*",
                                                            "NO HAY SIGUIENTE DETALLE"
                                                        )

                                                        mensajeError2 =
                                                            "No se encontró el siguiente detalle."

                                                        showDialogCantidad = true
                                                    }
                                                }

                                            } else {

                                                mensajeError2 =
                                                    "CAPTURA REGISTRADA CORRECTAMENTE"

                                                showDialogCantidad = true
                                            }
                                        }
                                    }


                                    // =================================================
                                    // TIPO NO RECONOCIDO
                                    // =================================================
                                    else -> {

                                        mensajeError2 =
                                            "Tipo de artículo no reconocido: " +
                                                    gTipoItem.trim()

                                        showDialogCantidad = true

                                        return@launch
                                    }
                                }

                            } catch (e: Exception) {

                                Log.e(
                                    "*MAKITA*PICKING*",
                                    "Error registrando captura",
                                    e
                                )

                                mensajeError2 =
                                    "Error al registrar la captura: ${e.message}"

                                showDialogCantidad = true
                            }
                        }
                    }

                } catch (e: Exception) {

                    Log.e(
                        "*MAKITA**",
                        "Error registrando captura",
                        e
                    )

                    mensajeError2 =
                        "Error al registrar la captura: ${e.message}"

                    showDialogCantidad = true
                }
            }
        }
    }



    suspend fun validarUbicacionOrigen() {

        keyboardController?.hide()

        if (!manejaUbicacionDestino) {
            ubicacionEscaneadaRequester.requestFocus()
            return
        }

        try {



            ubicacionEscaneadaRequester.requestFocus()

            val resp = apiService.validarUbicacionBodega(
                whsCode,
                binCode
            )

            if (resp == "SI") {

                val bin = apiService.obtenerBinUbicacion(
                    whsCode,
                    binCode
                )
                Log.d("*MAKITA*", "Respuesta Bin: $bin")

                if (bin.success) {
                    binAbsOrigen = bin.binAbs
                    binCodeOrigen = bin.binCode ?: ""
                    Log.d("*MAKITA*", "BIN DESTINO = $binAbs")
                    Log.d("*MAKITA*", "BIN CODE DESTINO = $binCode")
                    ubicacionEscaneadaRequester.requestFocus()
                }
                else
                {
                    mensajeError = "No fue posible obtener el Bin de la ubicación"
                    showDialog = true
                }

            } else {

                mensajeError = "XXUbicacion de Picking Uncorrecta"
                ubicacionEscaneada = ""
                showDialog = true
                ubicacionEscaneadaRequester.requestFocus()
            }

        } catch (e: Exception) {

            mensajeError = "Error validando ubicación de destino"
            showDialog = true
        }
    }


    LaunchedEffect(whsCode, binCode) {
        toWarehouse = whsCode
        text = itemEscaneado

        try {

            val resp = apiService.validarManejaUbicacion(toWarehouse)
            manejaUbicacionDestino = (resp == "SI")

            if (manejaUbicacionDestino) {

                val datosUbicacion = apiService.obtenerDatosUbicacion(
                    whsCode,
                    binCode
                )

                if (datosUbicacion.success) {

                    ubicacionEscaneada = ""
                    codigoEsperado     = datosUbicacion.barCode ?: ""
                    ubicacionEscaneadaRequester.requestFocus()

                    Log.d("*MAKITA*", "BarCode: ${datosUbicacion.barCode}")

                } else {
                    ubicacionEscaneada = ""
                    Log.e("*MAKITA*", datosUbicacion.message ?: "No fue posible obtener la ubicación")

                }

            }

        } catch (e: Exception) {

            manejaUbicacionDestino = false

            Log.e("*MAKITA*", e.message ?: "")

        }

    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
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
            )  {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
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

                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "NUMERO",
                                color = Color.White,
                                fontSize = 16.sp
                            )

                            Text(
                                text = absEntry.toString(),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {

                            Text(
                                text = "MAKITA CHILE",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Text(
                                text = "COM. LTDA.",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                        }

                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "UBICACION",
                                color = Color.White,
                                fontSize = 12.sp
                            )

                            Text(
                                text = barCode,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
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
                                text = whsCode,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                        }

                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF4FC3F7))
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "DETALLE ARTICULO",
                                color = Color.White,
                                fontSize = 14.sp
                            )

                            Text(
                                text = "${itemCode.trim()}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }

            }



            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // BODEGA
                    OutlinedTextField(
                        value = whsCode,
                        onValueChange = {},
                        label = {
                            Text("Bodega")
                        },
                        singleLine = true,

                        textStyle = estiloTextoRojo,
                        modifier = Modifier
                            .weight(1f)
                    )

                    // UBICACION
                    OutlinedTextField(
                        value = ubicacionEscaneada,
                        onValueChange = { nuevoValor ->

                            ubicacionEscaneada = nuevoValor.uppercase().trim()

                            Log.d(
                                "*MAKITA*INGRESA*",
                                "UBICACION ESPERADA = $ubicacionEscaneada vs $codigoEsperado"
                            )

                            if (ubicacionEscaneada.length > 12) {

                                mensajeError2 =
                                    "La ubicación no debe exceder los 10 caracteres"

                                ubicacionEscaneada = ""
                                extractedText = ""
                                cantidad = ""
                                showDialog = true

                                return@OutlinedTextField
                            }

                            if (ubicacionEscaneada.length == codigoEsperado.trim().length) {

                                keyboardController?.hide()

                                if (
                                    ubicacionEscaneada.uppercase() ==
                                    codigoEsperado.uppercase().trim()
                                ) {

                                    itemFocusRequester.requestFocus()

                                } else {

                                    try {

                                        val toneGenerator =
                                            ToneGenerator(
                                                AudioManager.STREAM_ALARM,
                                                100
                                            )

                                        toneGenerator.startTone(
                                            ToneGenerator.TONE_CDMA_ABBR_ALERT,
                                            350
                                        )

                                        Handler(Looper.getMainLooper()).postDelayed({

                                            toneGenerator.startTone(
                                                ToneGenerator.TONE_CDMA_ABBR_ALERT,
                                                350
                                            )

                                        }, 400)

                                        Handler(Looper.getMainLooper()).postDelayed({
                                            toneGenerator.release()
                                        }, 850)


                                    } catch (e: Exception) {
                                        Log.e(
                                            "*MAKITA*",
                                            "Error reproduciendo sonido: ${e.message}"
                                        )
                                    }



                                    mensajeError2 =
                                        "Ubicación incorrecta. Se esperaba: ${codigoEsperado.trim()}"

                                    ubicacionEscaneada = ""
                                    showDialog = true
                                }
                            }
                        },
                        label = {
                            Text(
                                "Ubicacion",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        singleLine = true,

                        textStyle = estiloTextoRojo,

                        modifier = Modifier
                            .weight(2f)
                            .focusRequester(ubicacionEscaneadaRequester)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    keyboardController?.hide()
                                }
                            }
                    )


                }



                if (showDialog) {
                    mostrarDialogo6(
                        titulo = "Error",
                        mensaje = mensajeError2,
                        onDismiss = {
                            showDialog = false
                            ubicacionEscaneadaRequester.requestFocus()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                /*
                OutlinedTextField(
                    value = itemEscaneado,
                    onValueChange = {
                        itemEscaneado = it
                    },
                    label = { Text("Escanee Codigo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                */



                OutlinedTextField(
                    value = text,

                    onValueChange = { newText ->

                        text = newText

                        if (newText.length < 20) {
                            modoManual = true
                            return@OutlinedTextField
                        }



                            coroutineScope.launch {

                            try {

                                Log.d(
                                    "*MAKITA*",
                                    "ITEM: $newText - LARGOXX: ${newText.length}"
                                )


                                if (newText.isEmpty()) {
                                    return@launch
                                }

                                Log.d(
                                    "*MAKITA*",
                                    "ITEM: $newText - LARGOXX: ${newText.length}"
                                )


                                if  (newText.length < 20) {

                                    Log.d(
                                        "*MAKITA*PICKING*",
                                        "LECTURA MENOR A 20: [$newText] - LARGO: ${newText.length}"
                                    )

                                    mensajeError2 =
                                        "Item incorrecto largo de etiqueta: ${newText.length}. Escanee la etiqueta correcta el item: ${itemCode.trim()} "

                                    text = ""

                                    extractedText = ""
                                    extractedText2 = ""
                                    extractedText3 = ""
                                    extractedText4 = ""

                                    textFieldValue2 = ""
                                    cantidad = ""
                                    CantidadEscaneada = ""
                                    response = emptyList()

                                    showDialogItem = true

                                    delay(100)

                                    itemFocusRequester.requestFocus()

                                    return@launch
                                }



                                extractedText =
                                    newText.substring(0, 20).trim()

                                Log.d(
                                    "*MAKITA*",
                                    "LARGO DE extractedText: $extractedText"
                                )

                                //ACA VALIDANDO ITEM IGUAL A LO SCANEADO

                                if (extractedText.trim() != itemCode.trim()) {

                                    mensajeError2 =
                                        "Item Distintos ${extractedText.trim()}. Escanee el item indicado en el picking ${itemCode.trim()}"

                                    text = ""
                                    extractedText = ""
                                    extractedText2 = ""
                                    extractedText3 = ""
                                    extractedText4 = ""
                                    textFieldValue2 = ""
                                    cantidad = ""
                                    CantidadEscaneada = ""
                                    response = emptyList()

                                    showDialogItem = true

                                    return@launch
                                }


                                gTipoItem =
                                    apiService.consultarTipoItem(
                                        extractedText.trim()
                                    ).trim()


                                Log.d(
                                    "*MAKITA*",
                                    "TIPO ITEM: $gTipoItem"
                                )





                                if (gTipoItem.trim().uppercase() == "HERRAMIENTAS") {

                                    // =================================================
                                    // VALIDAR LARGO MÍNIMO DE LA ETIQUETA
                                    // =================================================

                                    if (newText.length <= 30) {

                                        mensajeError2 =
                                            "Etiqueta de herramienta inválida.\n\n" +
                                                    "La etiqueta debe contener más de 30 caracteres."

                                        // LIMPIAR COMPLETAMENTE LA LECTURA
                                        text = ""
                                        extractedText = ""
                                        extractedText2 = ""
                                        extractedText3 = ""
                                        extractedText4 = ""
                                        textFieldValue2 = ""
                                        cantidad = ""
                                        CantidadEscaneada = ""
                                        response = emptyList()

                                        showDialogItem = true

                                        delay(100)

                                        itemFocusRequester.requestFocus()

                                        return@launch
                                    }


                                    if (newText.length > 20) {

                                        extractedText2 =
                                            newText.substring(
                                                20,
                                                newText.length.coerceAtMost(29)
                                            ).trim()


                                        extractedText3 =
                                            newText.substring(
                                                29,
                                                newText.length.coerceAtMost(38)
                                            ).trim()


                                        extractedText4 =
                                            if (newText.length > 39) {

                                                newText.substring(
                                                    39,
                                                    newText.length.coerceAtMost(52)
                                                ).trim()

                                            } else {
                                                ""
                                            }


                                        Log.d(
                                            "*MAKITA*",
                                            "ITEM: $newText | SERIE 1: $extractedText2"
                                        )

                                        Log.d(
                                            "*MAKITA*",
                                            "ITEM: $newText | SERIE 2: $extractedText3"
                                        )

                                        Log.d(
                                            "*MAKITA*",
                                            "ITEM: $newText | EAN: $extractedText4"
                                        )


                                        try {

                                            val numeroDesde =
                                                extractedText2.trim().toLong()


                                            val numeroHasta =
                                                extractedText3.trim().toLong()


                                            val cantidadEtiqueta =
                                                (numeroHasta - numeroDesde) + 1L


                                            if (cantidadEtiqueta > 0) {

                                                cantidad =
                                                    cantidadEtiqueta.toString()

                                                CantidadEscaneada =
                                                    cantidadEtiqueta.toString()


                                                Log.d(
                                                    "*MAKITA*PICKING*",
                                                    "HERRAMIENTA | " +
                                                            "SERIE DESDE=$extractedText2 | " +
                                                            "SERIE HASTA=$extractedText3 | " +
                                                            "CANTIDAD ESCANEADA=$CantidadEscaneada"
                                                )

                                            } else {

                                                cantidad = ""
                                                CantidadEscaneada = ""
                                            }

                                        } catch (e: Exception) {

                                            cantidad = ""
                                            CantidadEscaneada = ""
                                        }


                                        if (newText.length >= 50) {

                                            keyboardController?.hide()


                                            response35 =
                                                apiService.validarTipoItem(
                                                    extractedText.trim(),
                                                    gTipoItem
                                                )


                                            if (response35 == "NO") {

                                                mensajeError =
                                                    "Item: ${extractedText.trim()} NO EXISTE O NO CORRESPONDE A $gTipoItem"

                                                showDialog = true

                                                text = ""
                                                extractedText = ""
                                                extractedText2 = ""
                                                extractedText3 = ""
                                                extractedText4 = ""
                                                textFieldValue2 = ""

                                                cantidad = ""
                                                CantidadEscaneada = ""
                                                response = emptyList()

                                                delay(100)

                                                itemFocusRequester.requestFocus()

                                                return@launch
                                            }


                                            Log.d(
                                                "*MAKITA*",
                                                "ITEM: $newText | LARGO: ${newText.length}"
                                            )


                                            textFieldValue2 = ""


                                            val apiResponse =
                                                apiService.obtenerNombreItem(
                                                    extractedText.trim()
                                                )


                                            // =================================================
                                            // 9. RESPUESTA VACÍA
                                            // =================================================

                                            if (
                                                !apiResponse.success ||
                                                apiResponse.data.isEmpty()
                                            ) {

                                                mensajeError =
                                                    "No se encontraron datos para el item ${extractedText.trim()}"

                                                showDialog = true

                                                text = ""
                                                extractedText = ""
                                                extractedText2 = ""
                                                extractedText3 = ""
                                                extractedText4 = ""
                                                textFieldValue2 = ""
                                                response = emptyList()
                                                cantidad = ""
                                                CantidadEscaneada = ""

                                                delay(100)

                                                itemFocusRequester.requestFocus()

                                                return@launch
                                            }


                                            val tieneValoresNulos =
                                                apiResponse.data.any {
                                                    it.item.isBlank()
                                                }


                                            if (tieneValoresNulos) {

                                                mensajeError =
                                                    "El item ${extractedText.trim()} no tiene información válida"

                                                showDialog = true

                                                text = ""
                                                extractedText = ""
                                                extractedText2 = ""
                                                extractedText3 = ""
                                                extractedText4 = ""
                                                textFieldValue2 = ""
                                                response = emptyList()
                                                cantidad = ""
                                                CantidadEscaneada = ""

                                                delay(100)

                                                itemFocusRequester.requestFocus()

                                                return@launch
                                            }


                                            response =
                                                apiResponse.data


                                            if (response.isNotEmpty()) {

                                                textFieldValue2 =
                                                    response.first().descripcion.trim()
                                            }


                                            keyboardController?.hide()

                                            delay(100)

                                            cantidadFocusRequester.requestFocus()
                                        }
                                    }


                                } else {

                                    // =====================================================
                                    // ACCESORIOS / REPUESTOS / OTROS
                                    // =====================================================
                                    // ESTE BLOQUE SE MANTIENE IGUAL
                                    // =====================================================

                                    if (newText.length >= 52) {

                                        extractedText =
                                            newText.substring(0, 20).trim()

                                        extractedText2 =
                                            newText.substring(
                                                20,
                                                (20 + 18).coerceAtMost(newText.length)
                                            ).trim()
                                    }


                                    response35 =
                                        apiService.validarTipoItem(
                                            extractedText.trim(),
                                            gTipoItem
                                        )


                                    response35 = "SI"


                                    if (response35 == "NO") {

                                        mensajeError =
                                            "Item: ${extractedText.trim()} NO EXISTE O NO CORRESPONDE A $gTipoItem"

                                        showDialog = true

                                        text = ""
                                        //   ubicacion = ""
                                        extractedText = ""
                                        extractedText2 = ""
                                        extractedText3 = ""
                                        extractedText4 = ""
                                        textFieldValue2 = ""

                                        cantidad = ""
                                        response = emptyList()

                                        delay(100)

                                        itemFocusRequester.requestFocus()

                                        return@launch
                                    }


                                    textFieldValue2 = ""


                                    val apiResponse =
                                        apiService.obtenerNombreItem(
                                            extractedText.trim()
                                        )


                                    if (
                                        !apiResponse.success ||
                                        apiResponse.data.isEmpty()
                                    ) {

                                        mensajeError =
                                            "No se encontraron datos para el item ${extractedText.trim()}"

                                        showDialog = true

                                        text = ""
                                        extractedText = ""
                                        extractedText2 = ""
                                        extractedText3 = ""
                                        extractedText4 = ""
                                        textFieldValue2 = ""
                                        // secondTextFieldValue = ""
                                        response = emptyList()
                                        cantidad = ""

                                        delay(100)

                                        itemFocusRequester.requestFocus()

                                        return@launch
                                    }


                                    val tieneValoresNulos =
                                        apiResponse.data.any {
                                            it.item == null
                                        }


                                    if (tieneValoresNulos) {

                                        mensajeError =
                                            "El item ${extractedText.trim()} no tiene información válida"

                                        showDialog = true

                                        text = ""
                                        extractedText = ""
                                        extractedText2 = ""
                                        extractedText3 = ""
                                        extractedText4 = ""
                                        textFieldValue2 = ""
                                        //  secondTextFieldValue = ""
                                        response = emptyList()
                                        cantidad = ""

                                        delay(100)

                                        itemFocusRequester.requestFocus()

                                        return@launch
                                    }


                                    response =
                                        apiResponse.data


                                    if (response.isNotEmpty()) {

                                        textFieldValue2 =
                                            response.first().descripcion.trim()
                                    }


                                    keyboardController?.hide()

                                    delay(100)

                                    cantidadFocusRequester.requestFocus()
                                }


                            } catch (e: kotlinx.coroutines.CancellationException) {

                                throw e

                            } catch (e: Exception) {

                                Log.e(
                                    "*MAKITA*",
                                    "ERROR PROCESANDO ETIQUETA: ${e.message}",
                                    e
                                )

                                withContext(Dispatchers.Main) {

                                    mensajeError =
                                        "Error al obtener los datos, revise WiFi: ${e.message}"

                                    showDialog = true

                                    text = ""
                                    extractedText = ""
                                    extractedText2 = ""
                                    extractedText3 = ""
                                    extractedText4 = ""
                                    textFieldValue2 = ""

                                    cantidad = ""
                                    CantidadEscaneada = ""
                                    response = emptyList()

                                    delay(100)

                                    itemFocusRequester.requestFocus()
                                }
                            }
                        }
                    },

                    label = {
                        Text("Item")
                    },

                    placeholder = {
                        Text("Escanee etiqueta correcta")
                    },

                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),

                    keyboardActions = KeyboardActions(
                        onDone = {

                            coroutineScope.launch {

                                val itemManual = text.trim().uppercase()

                                if (itemManual.isBlank()) {
                                    return@launch
                                }

                                // ============================================
                                // VALIDAR ITEM CONTRA EL PICKING
                                // ============================================

                                if (itemManual.uppercase() != itemCode.trim().uppercase()) {

                                    Log.d(
                                        "*MAKITA*PICKING*",
                                        "ITEM CORRECTO MANUAL = $extractedText  Larrgho ${itemManual.length}"
                                    )

                                    mensajeError2 =
                                        "Item incorrecto XX ${itemManual.uppercase()}. Escanee el item indicado en el picking ${itemCode.trim()}"

                                    text = ""
                                    extractedText = ""
                                    extractedText2 = ""
                                    extractedText3 = ""
                                    extractedText4 = ""
                                    textFieldValue2 = ""
                                    cantidad = ""
                                    response = emptyList()

                                    showDialogItem = true

                                    return@launch
                                }

                                // ============================================
                                // ITEM CORRECTO
                                // ============================================

                                extractedText = itemManual.uppercase()

                                Log.d(
                                    "*MAKITA*PICKING*",
                                    "ITEM CORRECTO MANUAL = $extractedText"
                                )

                                try {

                                    val stock =
                                        apiService.obtenerNombreItem(
                                            extractedText.trim()
                                        )



                                    if (stock.data.isEmpty()) {

                                        mensajeError2 =
                                            "No se encontraron datos para el item ${extractedText.trim()}"

                                        showDialogItem = true

                                        text = ""
                                        extractedText = ""
                                        extractedText2 = ""
                                        extractedText3 = ""
                                        extractedText4 = ""
                                        textFieldValue2 = ""
                                        cantidad = ""
                                        response = emptyList()

                                        return@launch
                                    }

                                    response = stock.data

                                    textFieldValue2 =
                                        stock.data.first().descripcion.trim()

                                    Log.d(
                                        "*MAKITA*PICKING*",
                                        "DESCRIPCIÓN = $textFieldValue2"
                                    )

                                    // ============================================
                                    // PASAR A CANTIDAD
                                    // ============================================

                                    keyboardController?.hide()

                                    delay(100)

                                    cantidadFocusRequester.requestFocus()

                                } catch (e: Exception) {

                                    Log.e(
                                        "*MAKITA*PICKING*",
                                        "ERROR ITEM MANUAL",
                                        e
                                    )

                                    mensajeError2 =
                                        "Error al consultar el item: ${e.message}"

                                    showDialogItem = true
                                }
                            }
                        }
                    ),

                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(itemFocusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                keyboardController?.hide()
                            }
                        },

                    maxLines = 2,
                    singleLine = false,

                    leadingIcon = {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Modificando"
                        )
                    },

                    trailingIcon = {
                        if (text.isNotEmpty()) {

                            IconButton(
                                onClick = {
                                    procesarCantidad()
                                }
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Limpiar texto"
                                )
                            }
                        }
                    }
                )

                if (showDialogItem) {
                    mostrarDialogo6(
                        titulo = "Alerta!",
                        mensaje = mensajeError2,
                        onDismiss = {
                            showDialogItem = false
                            itemFocusRequester.requestFocus()
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = textFieldValue2.uppercase().trim(),
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Descripcion")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = estiloTextoRojo,
                    singleLine = true
                )



                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                OutlinedTextField(
                    value = CantidadEscaneada,
                    onValueChange = { newValue ->

                        if (
                            newValue.all { it.isDigit() } &&
                            newValue.length <= 10
                        ) {
                            CantidadEscaneada = newValue
                        }
                    },
                    label = {
                        Text("Cantidad")
                    },

                    //ACA
                    placeholder = {
                        Text(
                            text = "Cantidad",
                            style = estiloTextoRojo
                        )
                    },

                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { procesarCantidad() }
                    ),

                    modifier = Modifier
                        .width(180.dp)
                        .height(70.dp)
                        .focusRequester(cantidadFocusRequester),

                    textStyle = estiloTextoRojo,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                keyboardController?.hide()
                                procesarCantidad()

                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Ingresar cantidad"
                            )
                        }
                    },


                    isError = CantidadEscaneada.length > 10
                )

                if (CantidadEscaneada.length > 10) {

                    Text(
                        text = "La cantidad no debe exceder los 10 caracteres",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(
                            start = 16.dp,
                            top = 4.dp
                        )
                    )
                }


                if (showDialogCantidad) {
                    mostrarDialogo6(
                        titulo = "Aviso!",
                        mensaje = mensajeError2,
                        onDismiss = {
                            showDialogCantidad = false
                            cantidadFocusRequester.requestFocus()
                        }
                    )
                }



                if (showDialogSerie) {
                    mostrarDialogo6(
                        titulo = "Aviso!",
                        mensaje = mensajeError2,
                        onDismiss = {
                            showDialogCantidad = false
                            cantidadFocusRequester.requestFocus()
                        }
                    )
                }





            }

        }

    }



}
@Composable
fun mostrarDialogo6(
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