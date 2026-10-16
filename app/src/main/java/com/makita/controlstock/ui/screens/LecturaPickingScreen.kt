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
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type


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
    CantidadLiberada: Int,
    pickEntry: Int,
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
    var showDialogItem        by remember { mutableStateOf(false) }
    var showDialogCantidad    by remember { mutableStateOf(false) }
    var volverADetallePicking by remember { mutableStateOf(false) }
    var showDialogSerie        by remember { mutableStateOf(false) }
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

    var volverAItem by remember {
        mutableStateOf(false)
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

                                        val esEntradaManual =
                                            serieDesdeTexto == "000000000" &&
                                                    serieHastaTexto == "000000000"

                                        val cantidadIngresada =
                                            CantidadEscaneada.toIntOrNull() ?: 0

                                        if (serieDesdeTexto.isBlank() || serieHastaTexto.isBlank()) {

                                            mensajeError2 = "Rango de series inválido"
                                            showDialogCantidad = true

                                            return@launch
                                        }

                                        val serieDesde =
                                            serieDesdeTexto.toLongOrNull()

                                        val serieHasta =
                                            serieHastaTexto.toLongOrNull()

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
                                        // =================================================

                                        val cantidadEtiqueta =
                                            (serieHasta - serieDesde) + 1L


                                        // =================================================
                                        // VALIDAR CANTIDAD DIGITADA CONTRA LA ETIQUETA
                                        // =================================================

                                        if (
                                            !esEntradaManual &&
                                            cantidadIngresada > cantidadEtiqueta
                                        ) {

                                            mensajeError2 =
                                                "La cantidad ingresada ($cantidadIngresada) " +
                                                        "supera la cantidad indicada por la etiqueta ($cantidadEtiqueta)."

                                            showDialogCantidad = true

                                            CantidadEscaneada = ""
                                            cantidad = ""
                                            delay(100)
                                            cantidadFocusRequester.requestFocus()

                                            return@launch
                                        }

                                        // =================================================
                                        // LA CANTIDAD REAL SIEMPRE VIENE DE LA ETIQUETA
                                        // =================================================

                                        // La cantidad viene de la etiqueta.
                                        CantidadEscaneada =
                                            cantidadEtiqueta.toString()

                                        cantidad =
                                            cantidadEtiqueta.toString()

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
                                                binAbs,
                                                pickEntry
                                            )

                                        val detalle =
                                            datosPicking.data.firstOrNull {
                                                it.IdDetalle == idDetalle
                                            }

                                        if (detalle == null) {

                                            mensajeError2 =
                                                "No se encontró el detalle del picking."

                                            showDialogCantidad = true

                                            return@launch
                                        }

                                        // =================================================
                                        // CANTIDAD LIBERADA / CAPTURADA
                                        // =================================================

                                        val cantidadLiberada =
                                            detalle.CantidadLiberada ?: 0

                                        val cantidadCapturada =
                                            detalle.CantidadCapturada ?: 0

                                        Log.d(
                                            "*MAKITA*PICKING*",
                                            "HERRAMIENTA | " +
                                                    "CantidadCapturada=$cantidadCapturada"
                                        )

                                        val cantidadPendiente =
                                            cantidadLiberada - cantidadCapturada

                                        // =================================================
                                        // VALIDAR INGRESO MANUAL
                                        // =================================================

                                        if (
                                            esEntradaManual &&
                                            cantidadIngresada > cantidadPendiente
                                        ) {

                                            mensajeError2 =
                                                "La cantidad ingresada ($cantidadIngresada) supera la cantidad pendiente ($cantidadPendiente)."

                                            showDialogCantidad = true

                                            CantidadEscaneada = ""
                                            cantidad = ""

                                            delay(100)

                                            cantidadFocusRequester.requestFocus()

                                            return@launch
                                        }

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
                                                "El item seleccionado ya cumplio con su cantidad solicitada."

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
                                        // =================================================

                                        if (cantidadEtiqueta > cantidadPendiente) {

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
                                                        "ETIQUETA INCORRECTA, INGRESE ETIQUETA INDIVIDUAL, supera las unidades solicitadas."

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
                                        // INGRESO MANUAL
                                        // =================================================

                                        if (esEntradaManual) {

                                            val request =
                                                RegistrarPickingCapturaRequest(
                                                    idDetalle = idDetalle,
                                                    numeroSerie = "",
                                                    cantidad = cantidadIngresada,
                                                    whsCode = whsCode,
                                                    binAbs = binAbs,
                                                    barCode = codigoEsperado.uppercase(),
                                                    usuario = Sesion.usuario
                                                )

                                            val respuesta =
                                                apiService.registrarPickingCaptura(request)

                                            Log.d(
                                                "*MAKITA*",
                                                "MANUAL | " +
                                                        "success=${respuesta.success}, " +
                                                        "detalleCompleto=${respuesta.detalleCompleto}, " +
                                                        "pickingCompleto=${respuesta.pickingCompleto}, " +
                                                        "siguienteDetalle=${respuesta.siguienteDetalle}"
                                            )

                                            // =================================================
                                            // ERROR AL REGISTRAR
                                            // =================================================

                                            if (!respuesta.success) {

                                                mensajeError2 =
                                                    respuesta.message

                                                showDialogCantidad = true

                                                CantidadEscaneada = ""
                                                cantidad = ""

                                                delay(100)

                                                cantidadFocusRequester.requestFocus()

                                                return@launch
                                            }

                                            // =================================================
                                            // SI TERMINÓ EL DETALLE
                                            // =================================================

                                            if (respuesta.detalleCompleto) {

                                                // =================================================
                                                // TERMINÓ TODO EL PICKING
                                                // =================================================

                                                if (respuesta.pickingCompleto) {

                                                    CantidadEscaneada = ""

                                                    mensajeError2 = "¡PICKING TERMINADO!"
                                                    volverADetallePicking = true
                                                    showDialogCantidad = true

                                                    return@launch
                                                }

                                                // =================================================
                                                // BUSCAR SIGUIENTE DETALLE
                                                // =================================================

                                                val siguiente =
                                                    respuesta.siguienteDetalle

                                                if (siguiente != null) {

                                                    Log.d(
                                                        "*MAKITA*",
                                                        "MANUAL -> SIGUIENTE UBICACION | " +
                                                                "Id=${siguiente.Id} | " +
                                                                "Item=${siguiente.ItemCode} | " +
                                                                "Bin=${siguiente.BinCode} | " +
                                                                "BinAbs=${siguiente.BinAbs}" +
                                                                "PickEntry=${siguiente.PickEntry} | "

                                                    )

                                                    CantidadEscaneada = ""

                                                    mensajeError2 =
                                                        "Ubicación completada.\n\n" +
                                                                "Pasando a la siguiente ubicación..."

                                                    showDialogCantidad = true

                                                    delay(1500)

                                                    showDialogCantidad = false

                                                    Log.d(
                                                        "*MAKITA*",
                                                        "MANUAL -> NAVEGANDO A BinAbs=${siguiente.BinAbs} PickEntry=${siguiente.PickEntry}"
                                                    )

                                                    navController.navigate(
                                                        "detalleUbicacionPicking/" +
                                                                "$absEntry/" +
                                                                "${siguiente.BinAbs}/" +
                                                                "${siguiente.PickEntry}"
                                                    ) {
                                                        popUpTo(
                                                            "detalleUbicacionPicking/$absEntry/$binAbs/$pickEntry"
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

                                            // =================================================
                                            // MANUAL REGISTRADO PERO DETALLE NO COMPLETO
                                            // =================================================

                                            CantidadEscaneada = ""
                                            cantidad = ""

                                            return@launch
                                        }

                                        // =================================================
                                        // CONSERVAR CEROS A LA IZQUIERDA
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

                                                    mensajeError2 =
                                                        "¡PICKING TERMINADO!"

                                                    showDialogCantidad = true

                                                    return@launch

                                                } else {

                                                    val siguiente =
                                                        respuesta.siguienteDetalle

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
                                                                    "${siguiente.BinAbs}/" +
                                                                    "$pickEntry"
                                                        ) {
                                                            popUpTo(
                                                                "detalleUbicacionPicking/$absEntry/$binAbs/$pickEntry"
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
                                                "Caja MASTER registrada de forma correcta.\n\n" +
                                                        "Series registradas: $cantidadEtiqueta"
                                            }


                                        volverAItem = true
                                        showDialogCantidad = true


                                        text = ""
                                        extractedText = ""
                                        extractedText2 = ""
                                        extractedText3 = ""
                                        extractedText4 = ""
                                        textFieldValue2 = ""
                                        cantidad = ""

                                       // delay(100)
                                       // itemFocusRequester.requestFocus()
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

                                            mensajeError2 =
                                                respuesta.message

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

                                                if (respuesta.pickingCompleto)
                                                {
                                                    mensajeError2 = "¡PICKING TERMINADO!"
                                                    showDialogCantidad = true

                                                }
                                                else
                                                {

                                                    val siguiente =
                                                        respuesta.siguienteDetalle

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
                                                                    "${siguiente.BinAbs}/" +
                                                                    "$pickEntry"
                                                        ) {
                                                            popUpTo(
                                                                "detalleUbicacionPicking/$absEntry/$binAbs/$pickEntry"
                                                            ) {
                                                                inclusive = true
                                                            }
                                                        }

                                                    }
                                                    else
                                                    {

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
                                fontSize = 13.sp
                            )

                            Text(
                                text = "${itemCode.trim()} Recoger: ${CantidadLiberada} ",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 19.sp
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
                    /*
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
                    */

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.LightGray)
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    )
                            ) {
                                Text(
                                    text = "Bodega",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.DarkGray
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 14.dp
                                    )
                            ) {
                                Text(
                                    text = whsCode.uppercase().trim(),
                                    style = estiloTextoRojo,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }



                    // UBICACION
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            // ENCABEZADO
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.LightGray)
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    )
                            ) {
                                Text(
                                    text = "Ubicacion",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.DarkGray
                                )
                            }

                            // CAMPO
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 4.dp
                                    )
                            ) {

                                BasicTextField(
                                    value = ubicacionEscaneada,

                                    onValueChange = { nuevoValor ->

                                        ubicacionEscaneada = nuevoValor.uppercase().trim()

                                        Log.d(
                                            "*MAKITA*INGRESA*",
                                            "UBICACION ESPERADA = $ubicacionEscaneada vs $codigoEsperado"
                                        )

                                        // ============================================================
                                        // NO PUEDE SUPERAR 12 CARACTERES
                                        // ============================================================

                                        if (ubicacionEscaneada.length > 12) {

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
                                                "La ubicacion no puede exceder los 12 caracteres"

                                            ubicacionEscaneada = ""
                                            extractedText = ""
                                            cantidad = ""

                                            showDialog = true

                                            return@BasicTextField
                                        }

                                        // ============================================================
                                        // VALIDACION AUTOMATICA DEL ESCANER
                                        // SOLO SE EJECUTA CUANDO:
                                        // - TIENE AL MENOS 5 CARACTERES
                                        // - LLEGO AL LARGO DE LA UBICACION ESPERADA
                                        // ============================================================

                                        if (
                                            ubicacionEscaneada.length >= 5 &&
                                            ubicacionEscaneada.length ==
                                            codigoEsperado.trim().length
                                        ) {

                                            keyboardController?.hide()

                                            // ========================================================
                                            // UBICACION CORRECTA
                                            // ========================================================

                                            if (
                                                ubicacionEscaneada.uppercase() ==
                                                codigoEsperado.uppercase().trim()
                                            ) {

                                                itemFocusRequester.requestFocus()

                                            } else {

                                                // ====================================================
                                                // TONO UBICACION INCORRECTA
                                                // ====================================================

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

                                    singleLine = true,

                                    textStyle = estiloTextoRojo,

                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done
                                    ),

                                    // ================================================================
                                    // ENTER / DONE
                                    // ================================================================

                                    keyboardActions = KeyboardActions(
                                        onDone = {

                                            val ubicacion = ubicacionEscaneada.trim()

                                            Log.d(
                                                "*MAKITA*INGRESA*",
                                                "ENTER UBICACION = [$ubicacion] LARGO=${ubicacion.length}"
                                            )

                                            // ========================================================
                                            // VACÍA
                                            // ========================================================

                                            if (ubicacion.isBlank()) {

                                                return@KeyboardActions
                                            }

                                            // ========================================================
                                            // MENOS DE 5 CARACTERES
                                            // ========================================================

                                            if (ubicacion.length < 5) {

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
                                                    "La ubicación debe tener al menos 5 caracteres"

                                                ubicacionEscaneada = ""
                                                extractedText = ""
                                                cantidad = ""

                                                showDialog = true

                                                return@KeyboardActions
                                            }

                                            // ========================================================
                                            // MÁS DE 12 CARACTERES
                                            // ========================================================

                                            if (ubicacion.length > 12) {

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
                                                    "La ubicacion no puede exceder los 12 caracteres"

                                                ubicacionEscaneada = ""
                                                extractedText = ""
                                                cantidad = ""

                                                showDialog = true

                                                return@KeyboardActions
                                            }

                                            // ========================================================
                                            // VALIDAR UBICACION DIGITADA
                                            // ========================================================

                                            keyboardController?.hide()

                                            if (
                                                ubicacion.uppercase() ==
                                                codigoEsperado.uppercase().trim()
                                            ) {

                                                // ====================================================
                                                // CORRECTA
                                                // ====================================================

                                                itemFocusRequester.requestFocus()

                                            } else {

                                                // ====================================================
                                                // INCORRECTA
                                                // ====================================================

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
                                    ),

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .focusRequester(ubicacionEscaneadaRequester)
                                        .onFocusChanged { focusState ->

                                            if (focusState.isFocused) {
                                                keyboardController?.hide()
                                            }
                                        },

                                    decorationBox = { innerTextField ->

                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(
                                                    horizontal = 12.dp,
                                                    vertical = 4.dp
                                                ),
                                            contentAlignment = Alignment.CenterStart
                                        ) {

                                            if (ubicacionEscaneada.isEmpty()) {
                                                Text(
                                                    text = "Ubicación",
                                                    color = Color.Gray,
                                                    fontSize = 14.sp
                                                )
                                            }

                                            innerTextField()
                                        }
                                    }
                                )
                            }
                        }
                    }


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

                                extractedText =
                                    newText.substring(0, 17).trim()

                                Log.d(
                                    "*MAKITA*",
                                    "LARGO DE extractedText MLS100: $extractedText"
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


                                extractedText = newText.substring(0, 20).trim()

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

                                gTipoItem = apiService.consultarTipoItem(extractedText.trim()).trim()

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
                                extractedText2 = "000000000"     //Serie Desde
                                extractedText3 = "000000000"    // Serie Hasta
                                extractedText4 = "0000000000000" // EAN



                                if (itemManual.isBlank()) {
                                    return@launch
                                }

                                // ============================================
                                // VALIDAR ITEM CONTRA EL PICKING
                                // ============================================

                                if (itemManual != itemCode.trim().uppercase()) {

                                    Log.d(
                                        "MAKITA_PICKING",
                                        "ITEM INCORRECTO MANUAL = $itemManual Largo ${itemManual.length}"
                                    )

                                    try {

                                        val toneGenerator = ToneGenerator(
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
                                            "MAKITA",
                                            "Error reproduciendo sonido: ${e.message}"
                                        )
                                    }

                                    mensajeError2 =
                                        "Item incorrecto XX ${itemManual}. Escanee el item indicado en el picking ${itemCode.trim()}"

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

                                extractedText = itemManual

                                Log.d(
                                    "MAKITA_PICKING",
                                    "ITEM CORRECTO MANUAL = $extractedText"
                                )

                                // ============================================
                                // VALIDAR TIPO DE ITEM
                                // ============================================

                                try {

                                    gTipoItem = apiService
                                        .consultarTipoItem(
                                            extractedText.trim()
                                        )
                                        .trim()

                                    Log.d(
                                        "MAKITA",
                                        "TIPO ITEM: $gTipoItem"
                                    )

                                    // ============================================
                                    // OBTENER INFORMACIÓN DEL ITEM
                                    // ============================================

                                    val stock = apiService.obtenerNombreItem(
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



                                    // ============================================
                                    // PASAR A CANTIDAD
                                    // ============================================

                                    keyboardController?.hide()

                                    delay(100)

                                    cantidadFocusRequester.requestFocus()

                                } catch (e: Exception) {

                                    Log.e(
                                        "MAKITA_PICKING",
                                        "ERROR VALIDANDO ITEM MANUAL",
                                        e
                                    )

                                    mensajeError2 =
                                        "No fue posible validar el item. Verifique la conexión e intente nuevamente."

                                    showDialogItem = true

                                    text = ""
                                    extractedText = ""
                                    extractedText2 = ""
                                    extractedText3 = ""
                                    extractedText4 = ""
                                    textFieldValue2 = ""
                                    cantidad = ""
                                    response = emptyList()
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
                    value = extractedText.uppercase().trim(),
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Item")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = estiloTextoRojo,
                    singleLine = true
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )
/*
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
                */

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.LightGray)
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                )
                        ) {
                            Text(
                                text = "Descripcion",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                        }


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 10.dp
                                )
                        ) {
                            Text(
                                text = textFieldValue2.uppercase().trim(),
                                style = estiloTextoRojo,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }




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

/*
                if (showDialogCantidad) {
                    mostrarDialogo6(
                        titulo = "Aviso!",
                        mensaje = mensajeError2,
                        onDismiss = {
                            showDialogCantidad = false
                            cantidadFocusRequester.requestFocus()
                        }
                    )
                }*/


                if (showDialogCantidad) {
                    mostrarDialogo6(
                        titulo = "Aviso!",
                        mensaje = mensajeError2,
                        onDismiss = {
                            showDialogCantidad = false

                            if (volverADetallePicking) {

                                volverADetallePicking = false

                                navController.popBackStack()

                            } else if (volverAItem) {

                                volverAItem = false

                                coroutineScope.launch {
                                    delay(100)
                                    itemFocusRequester.requestFocus()
                                }

                            } else {

                                cantidadFocusRequester.requestFocus()
                            }
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