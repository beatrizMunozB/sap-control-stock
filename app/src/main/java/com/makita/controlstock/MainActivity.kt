package com.makita.controlstock

import android.app.Activity
import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.makita.controlstock.ui.theme.ControlStockTheme
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import com.makita.controlstock.data.network.RetrofitClient.apiService
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavType
import androidx.navigation.navArgument

import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.Alignment

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import com.makita.controlstock.model.OpcionDashboard
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.ui.draw.shadow
import com.makita.controlstock.data.network.ItemMovimiento
import com.makita.controlstock.data.network.ItemResponse
import com.makita.controlstock.data.network.LoginSAPRequest
import com.makita.controlstock.data.network.TransferRequest
import com.makita.controlstock.ui.screens.ComprasOrdenesScreen
import com.makita.controlstock.ui.screens.ComprasRecepcionScreen
import com.makita.controlstock.ui.screens.ComprasReservaScreen
import com.makita.controlstock.ui.screens.ConsultasScreen
import com.makita.controlstock.ui.screens.ComprasScreen
import com.makita.controlstock.ui.screens.PickingScreen
import com.makita.controlstock.ui.screens.TrasladosScreen
import com.makita.controlstock.ui.screens.ConsultaStockScreen
import com.makita.controlstock.ui.screens.ConsultaStockItemScreen
import com.makita.controlstock.ui.screens.ConsultaStockBodegaScreen
import com.makita.controlstock.ui.screens.PickingRecolectaScreen
import com.makita.controlstock.ui.screens.TrasladosEntradaScreen
import com.makita.controlstock.ui.screens.TrasladosSalidaScreen
import com.makita.controlstock.ui.screens.TrasladosSolicitudScreen
import com.makita.controlstock.ui.screens.DetalleSolicitudScreen
import androidx.compose.ui.draw.blur
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import com.makita.controlstock.data.model.CapturaPendiente
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.style.TextOverflow
import android.widget.Toast
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import com.google.gson.Gson
import com.makita.controlstock.data.network.SolicitudTransferenciaRequest
import com.makita.controlstock.data.network.TransferenciaPendienteResponse
import com.makita.controlstock.data.network.TransferenciaResponse
import com.makita.controlstock.ui.screens.LecturaSolicitudScreen
import com.makita.controlstock.session.Sesion
import com.makita.controlstock.ui.screens.DetallePickingScreen
import com.makita.controlstock.ui.screens.DetalleUbicacionPickingScreen
import com.makita.controlstock.ui.screens.LecturaPickingScreen
import com.makita.controlstock.ui.screens.PickingSolicitudScreen
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.icons.filled.Print
import com.makita.controlstock.ui.screens.ConsultaPedidoClientePickingScreen
import com.makita.controlstock.ui.screens.ConsultaPedidoClienteScreen
import com.makita.controlstock.ui.screens.ProcesadoPickingScreen
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign


val fechaHoy = LocalDate.now()
    .format(DateTimeFormatter.ISO_DATE)


// ---------------- MAIN ACTIVITY ----------------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        //enableEdgeToEdge() sacan
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContent {
            ControlStockTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

// ---------------- NAVIGATION ----------------
@Composable
fun LoadingIcon() {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    Icon(
        imageVector = Icons.Default.Refresh,
        contentDescription = "Cargando",
        modifier = Modifier
            .size(50.dp)
            .rotate(rotation)
    )
}




@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {


        composable("splash") {
            SplashScreen(navController)
        }

        composable("login") {
            PantallaLogin(navController)
        }

        composable(
            route = "second/{usuario}",
            arguments = listOf(
                navArgument("usuario") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""

            SecondScreen(
                navController = navController

            )
        }


        composable("compras") {
            ComprasScreen(navController)
        }

        /*
        composable("picking") {
            PickingScreen(navController)
        }
        */


        composable(
            route = "picking/{usuario}",
            arguments = listOf(
                navArgument("usuario") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""
            Log.d("*MAKITA*", "muestra valor de mostrarDialogoStock  -> Usuario: $usuario")


            PickingSolicitudScreen(
                usuario = usuario,
                navController = navController
            )
        }

        /*
        composable("picking_recolecta") {
            PickingRecolectaScreen(navController)
        }
        */


        composable(
            route = "traslados/{usuario}"
        ) { backStackEntry ->

            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""

            TrasladosScreen(navController, usuario)
        }

        composable("consultas") {
            ConsultasScreen(navController)
        }


        composable("consulta_ubicacion") {
            ConsultaStockScreen(navController)
        }

        composable("consulta_stock") {
            ConsultaStockItemScreen(navController)
        }

        composable("consulta_bodega") {
            ConsultaStockBodegaScreen(navController)
        }

        composable("consulta_pedido") {
            ConsultaPedidoClienteScreen(navController)
        }

        composable("consulta_picking") {
            ConsultaPedidoClientePickingScreen(navController)
        }


        composable("compras_ordenes") {
            ComprasOrdenesScreen(navController)
        }

        composable("compras_reserva") {
            ComprasReservaScreen(navController)
        }
        composable("compras_recepcion") {
            ComprasRecepcionScreen(navController)
        }


        composable(
            route = "traslado_salida/{usuario}",
            arguments = listOf(
                navArgument("usuario") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""
            Log.d("*MAKITA*", "muestra valor de mostrarDialogoStock  -> Usuario: $usuario")

            TrasladosSalidaScreen(
                usuario = usuario
            )
        }


        composable(
            route = "traslado_entrada/{usuario}",
            arguments = listOf(
                navArgument("usuario") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""
            Log.d("*MAKITA*", "muestra valor de mostrarDialogoStock  -> Usuario: $usuario")

            TrasladosEntradaScreen(
                usuario = usuario
            )
        }

        composable(
            route = "detalleSolicitud/{docEntry}/{DocNum}/{CardCode}/{CardName}/{fromWarehouse}/{toWarehouse}"
        ) { backStackEntry ->


            val docEntry =
                backStackEntry.arguments?.getString("docEntry")?.toIntOrNull() ?: 0

            val DocNum =
                backStackEntry.arguments?.getString("DocNum")?.toIntOrNull() ?: 0

            val cardCode =
                backStackEntry.arguments?.getString("CardCode") ?: ""

            val cardName =
                backStackEntry.arguments?.getString("CardName") ?: ""

            val fromWarehouse  =
                backStackEntry.arguments?.getString("fromWarehouse") ?: ""

            val toWarehouse =
                backStackEntry.arguments?.getString("toWarehouse") ?: ""

            DetalleSolicitudScreen(
                docEntry = docEntry,
                DocNum = DocNum,
                CardCode = cardCode,
                CardName = cardName,
                FromWarehouse = fromWarehouse,
                ToWarehouse = toWarehouse,
                navController = navController
            )
        }


        composable(
            route = "detallePicking/{AbsEntry}"
        ) { backStackEntry ->

            val AbsEntry =
                backStackEntry.arguments?.getString("AbsEntry")?.toIntOrNull() ?: 0

            DetallePickingScreen(
                AbsEntry = AbsEntry,
                navController = navController
            )
        }


        composable(
            route = "detalleUbicacionPicking/{AbsEntry}/{BinAbs}",
            arguments = listOf(
                navArgument("AbsEntry") { type = NavType.IntType },
                navArgument("BinAbs") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val absEntry = backStackEntry.arguments?.getInt("AbsEntry") ?: 0
            val binAbs = backStackEntry.arguments?.getInt("BinAbs") ?: 0


            DetalleUbicacionPickingScreen(
                AbsEntry = absEntry,
                BinAbs = binAbs,
                navController = navController
            )
        }


        composable(
            route = "lecturaPicking/{idCabecera}/{idDetalle}/{absEntry}/{binCode}/{binAbs}/{whsCode}/{ItemCode}",
            arguments = listOf(
                navArgument("idCabecera") { type = NavType.IntType },
                navArgument("idDetalle") { type = NavType.IntType },
                navArgument("absEntry") { type = NavType.IntType },
                navArgument("binCode") { type = NavType.StringType },
                navArgument("binAbs") { type = NavType.IntType },
                navArgument("whsCode") { type = NavType.StringType },
                navArgument("ItemCode") { type = NavType.StringType },
            )
        ) { backStackEntry ->

            val idCabecera = backStackEntry.arguments?.getInt("idCabecera") ?: 0
            val idDetalle = backStackEntry.arguments?.getInt("idDetalle") ?: 0
            val absEntry = backStackEntry.arguments?.getInt("absEntry") ?: 0
            val binCode = backStackEntry.arguments?.getString("binCode") ?: ""
            val binAbs = backStackEntry.arguments?.getInt("binAbs") ?: 0
            val whsCode = backStackEntry.arguments?.getString("whsCode") ?: ""
            val itemCode = backStackEntry.arguments?.getString("ItemCode") ?: ""

            LecturaPickingScreen(
                idCabecera = idCabecera,
                idDetalle = idDetalle,
                absEntry = absEntry,
                binCode = binCode,
                binAbs = binAbs,
                whsCode = whsCode,
                itemCode = itemCode,
                navController = navController
            )
        }



        composable(
            route = "procesadoPicking/{idCabecera}/{absEntry}",
            arguments = listOf(
                navArgument("idCabecera") { type = NavType.IntType },
                navArgument("absEntry") { type = NavType.IntType }

            )
        ) { backStackEntry ->

            val idCabecera = backStackEntry.arguments?.getInt("idCabecera") ?: 0

            val absEntry = backStackEntry.arguments?.getInt("absEntry") ?: 0

            ProcesadoPickingScreen(
                idCabecera = idCabecera,
                absEntry = absEntry,
                navController = navController
            )
        }




        composable(
            route = "lecturaSolicitud/{idCabecera}"
        ) { backStackEntry ->

            val idCabecera =
                backStackEntry.arguments?.getString("idCabecera")?.toIntOrNull() ?: 0

            LecturaSolicitudScreen(
                idCabecera = idCabecera,
                navController = navController
            )
        }



        composable(
            route = "consulta_bodega/{usuario}",
            arguments = listOf(
                navArgument("usuario") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""
            Log.d("*MAKITA*", "muestra valor de mostrarDialogoStock  -> Usuario: $usuario")

            TrasladosSolicitudScreen(
                usuario = usuario,
                navController = navController
            )
        }




        composable(
            route = "traslado_inmediato/{usuario}",
            arguments = listOf(
                navArgument("usuario") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""
            Log.d("*MAKITA*", "muestra valor de mostrarDialogoStock  -> Usuario: $usuario")

            TerceraScreen(
                usuario = usuario
            )
        }


    }
}


@Composable
fun SplashScreen(navController: NavController) {


    val infiniteTransition = rememberInfiniteTransition(label = "")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )


    val x = 5f
    val y = 5f
    val offset = 5f

    val brush = Brush.linearGradient(
        colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.5f), Color.Transparent),
        start = Offset(x, y), // animar x,y
        end = Offset(x + offset, y + offset)
    )


    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Halo difuso detrás del logo
        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    alpha = glowAlpha * 0.25f
                    scaleX = scale * 1.2f
                    scaleY = scale * 1.2f
                }
                .background(
                    color = Color(0xFFD32F2F),
                    shape = CircleShape
                )
                .blur(24.dp)
        )

        // Logo principal
        //Image(
        //    painter = painterResource(id = R.drawable.makitarojosmall),
        //    contentDescription = "Logo",
        //    modifier = Modifier
        //        .size(120.dp)
        //        .graphicsLayer {
        //            alpha = glowAlpha
        //            scaleX = scale
        //            scaleY = scale
        //        }
        // )

        Image(
            painter = painterResource(id = R.drawable.makitarojosmall),
            contentDescription = "Logo",
            modifier = Modifier
                .size(100.dp)
                .drawBehind {
                    drawRect(brush) // Dibuja el brillo encima
                }
        )

        // Nombre de la app abajo
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "OPERACIONES DE BODEGA",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F),
                letterSpacing = 2.sp
            )
            Text(
                text = "Cada movimiento, bajo control",
                fontSize = 15.sp,
                color = Color(0xFF9E9E9E),
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun PantallaLogin(navController: NavController) {

   // var usuario by rememberSaveable { mutableStateOf("mktcapturador20") }
   //  var password by rememberSaveable { mutableStateOf("mM77248@") }

    var usuario by rememberSaveable { mutableStateOf("mktcapturati") }
    var password by rememberSaveable { mutableStateOf("M@kita26@") }
    var error by rememberSaveable { mutableStateOf("") }
    var usuarioSAP by rememberSaveable { mutableStateOf("") }
    val passwordFocusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    var showErrorDialogUSU by remember { mutableStateOf(false) }
    var errorMessageUSU by remember { mutableStateOf("") }
    var usuarioValidado by rememberSaveable { mutableStateOf(false) }
    val usuarioFocusRequester = remember { FocusRequester() }
    var isLoading by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    val offsetX = remember { Animatable(-300f) }
    val rotation = remember { Animatable(0f) }
    val rotationX = remember { Animatable(90f) }
 //   val rotationY = remember { Animatable(90f) }
    val rotationYAnim = remember { Animatable(90f) }
    var mostrarDialogoImpresora by remember { mutableStateOf(false) }
    var impresoraSeleccionada by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current



    CambiarColorBarraEstado(Color(0xFF00909E))

    LaunchedEffect(Unit) {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
    }


    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banner_login),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                )

                Text(
                    text = "Control de Inventario",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                LaunchedEffect(Unit) {
                    rotationYAnim.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(
                            durationMillis = 1200,
                            easing = LinearOutSlowInEasing
                        )
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.makita_143x31_transparente),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(90.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Iniciar sesión",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = usuario,
                    onValueChange = {
                        usuario = it
                        error = ""
                        usuarioSAP = ""
                        usuarioValidado = false
                    },
                    label = { Text("Usuario") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(usuarioFocusRequester)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused && usuario.isNotBlank()) {
                                coroutineScope.launch {
                                    try {
                                        val respuesta = apiService.validarUsuario(usuario)

                                        if (respuesta == "SI") {
                                            usuarioSAP = usuario
                                            usuarioValidado = true
                                            Sesion.usuario = usuario
                                            passwordFocusRequester.requestFocus()
                                        } else {
                                            errorMessageUSU = "Usuario incorrecto: $usuario"
                                            showErrorDialogUSU = true
                                        }
                                    } catch (e: Exception) {
                                        errorMessageUSU = "Ingrese usuario correcto: $usuario"
                                        showErrorDialogUSU = true
                                    }
                                }
                            }
                        },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                var passwordVisible by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },

                    visualTransformation =
                        if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),

                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            }
                        ) {
                            Icon(
                                imageVector =
                                    if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },

                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    enabled = usuarioSAP.isNotEmpty() && !isLoading,
                    onClick = {
                        keyboardController?.hide()

                        if (password.isBlank()) {
                            error = "Ingrese contraseña"
                            return@Button
                        }

                        coroutineScope.launch {
                            try {
                                isLoading = true
                                delay(800)

                                val response = apiService.loginSAP(
                                    LoginSAPRequest(
                                        UserName = usuarioSAP,
                                        Password = password,
                                        CompanyDB = "CLPRDMAKITA"
                                    )
                                )

                                isLoading = false

                                /*
                                if (response.ok) {
                                    navController.navigate("second/$usuarioSAP") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    error = response.message ?: "Usuario o contraseña incorrecta"
                                }
                                */
                                if (response.ok) {
                                    mostrarDialogoImpresora = true
                                } else {
                                    error = response.message ?: "Usuario o contraseña incorrecta"
                                }

                            } catch (e: Exception) {
                                isLoading = false
                                error = "No se pudo conectar al servidor"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00909E)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ingresar", color = Color.White)
                }

                if (error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = error, color = Color.Red)
                }
            }
        }


        if (isLoading) {

            val infiniteTransition = rememberInfiniteTransition(label = "")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = ""
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Icon(
                        imageVector = Icons.Filled.Autorenew,
                        contentDescription = "Cargando",
                        tint = Color.White,
                        modifier = Modifier
                            .size(60.dp)
                            .rotate(rotation)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Validando credenciales...",
                        color = Color.White
                    )
                }
            }
        }
    }

    if (showErrorDialogUSU) {
        mostrarDialogo(
            titulo = "Error",
            mensaje = errorMessageUSU,
            onDismiss = {
                showErrorDialogUSU = false
                password = ""
                usuario = ""
                usuarioFocusRequester.requestFocus()
            }
        )
    }


    if (mostrarDialogoImpresora) {

        AlertDialog(
            onDismissRequest = {
                // No cerrar hasta seleccionar una impresora
            },

            title = {
                Text(
                    text = "Seleccionar impresora",
                    fontWeight = FontWeight.Bold
                )
            },

            text = {

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                impresoraSeleccionada = "Picking"
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = impresoraSeleccionada == "Picking",
                            onClick = {
                                impresoraSeleccionada = "Picking"
                            }
                        )

                        Text("Picking")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                impresoraSeleccionada = "Administracion"
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = impresoraSeleccionada == "Administracion",
                            onClick = {
                                impresoraSeleccionada = "Administracion"
                            }
                        )

                        Text("Administracion")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                impresoraSeleccionada = "Herramientas"
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = impresoraSeleccionada == "Herramientas",
                            onClick = {
                                impresoraSeleccionada = "Herramientas"
                            }
                        )

                        Text("Herramientas")
                    }
                }
            },

            confirmButton = {
                Button(
                    enabled = impresoraSeleccionada.isNotEmpty(),
                    onClick = {

                        Sesion.impresora = impresoraSeleccionada

                        mostrarDialogoImpresora = false

                        navController.navigate("second/$usuarioSAP") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    }
                ) {
                    Text("Continuar")
                }
            }
        )
    }



}


enum class ModoLectura {
    ORIGEN,
    DESTINO,
    ARTICULOS
}


@Composable
fun SecondScreen(navController: NavController) {
    val usuario = Sesion.usuario
    val opciones = listOf(
        OpcionDashboard("Compras", "Recepcion y Devolucion", Icons.Default.ShoppingCart, "compras"),
        OpcionDashboard("Picking", "Picking", Icons.Default.List, "picking/$usuario"),
        OpcionDashboard("Traslados", "Ingresos y Salidas", Icons.Default.SyncAlt, "traslados/$usuario"),
        OpcionDashboard("Consultas", "Consultas de inventario", Icons.Default.Search, "consultas")
    )
    var mostrarDialogoImpresora by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Bienvenido $usuario",
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal
            )

            IconButton(
                onClick = {
                    mostrarDialogoImpresora = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = "Impresora Bluetooth",
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(opciones) { opcion ->
                CardOpcion(opcion, navController)
            }
        }
    }


    if (mostrarDialogoImpresora) {

        var nombreImpresora by remember {
            mutableStateOf("")
        }

        AlertDialog(
            onDismissRequest = {
                mostrarDialogoImpresora = false
            },

            title = {
                Text("Impresora Bluetooth")
            },

            text = {

                OutlinedTextField(
                    value = nombreImpresora,
                    onValueChange = {
                        nombreImpresora = it
                    },
                    label = {
                        Text("Ingrese nombre impresora Bluetooth")
                    },
                    singleLine = true
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        mostrarDialogoImpresora = false
                    }
                ) {
                    Text("Aceptar")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        mostrarDialogoImpresora = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }



}


@Composable
fun CardOpcion(opcion: OpcionDashboard, navController: NavController) {


    val colorIcono = when {

        // 🔴 Menú principal
        opcion.ruta == "consultas" -> Color.Red
        opcion.ruta == "compras" -> Color(0xFF388E3C)
        opcion.ruta == "picking" -> Color(0xFF1976D2)
        opcion.ruta.startsWith("traslados") -> Color(0xFFFF9800)

        // 🔵 Submenú consultas
                opcion.ruta == "consulta_stock" ||
                opcion.ruta == "consulta_ubicacion" ||
                opcion.ruta == "consulta_bodega" ||
                opcion.ruta == "consulta_pedido" ||
                opcion.ruta == "consulta_picking"
                     -> Color.Red

        // 🟢 Entrada
        opcion.ruta == "traslado_entrada" ||
                opcion.ruta == "compras_recepcion" -> Color(0xFF388E3C)

        // Salida
        opcion.ruta == "traslado_salida" -> Color(0xFF6D4C41)

        // Movimiento picking o  traslado_inmediato
        opcion.ruta == "picking" || opcion.ruta == "traslado_inmediato" -> Color(0xFF7B1FA2)

        // Documentos
        opcion.ruta == "compras_ordenes"   ||  opcion.ruta == "compras_reserva" -> Color(0xFF388E3C)

        else -> Color(0xFF3949AB)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                navController.navigate(opcion.ruta)
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🔹 Círculo con icono
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(colorIcono.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = opcion.icono,
                    contentDescription = opcion.titulo,
                    tint = colorIcono,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {

                Text(
                    text = opcion.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = opcion.subtitulo,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}




@Composable
fun TerceraScreen(usuario: String) {

    var bodegaOrigen by rememberSaveable { mutableStateOf("") }
    var localOrigen by rememberSaveable { mutableStateOf("") }
    var ubicacionOrigen by rememberSaveable { mutableStateOf("") }

    var bodegaDestino by rememberSaveable { mutableStateOf("") }
    var localDestino by rememberSaveable { mutableStateOf("") }
    var ubicacionDestino by rememberSaveable { mutableStateOf("") }
    var mostrarCajaLectura by rememberSaveable { mutableStateOf(false) }
    var lecturaIniciada by rememberSaveable { mutableStateOf(false) }
    var modoLectura by rememberSaveable { mutableStateOf<ModoLectura?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var showErrorDialogUBI by remember { mutableStateOf(false) }
    var showErrorDialogUBI2 by remember { mutableStateOf(false) }
    var errorMessageUBI by remember { mutableStateOf("") }
    var errorMessageUBI2 by remember { mutableStateOf("") }
    var cantidadActual by rememberSaveable { mutableStateOf("") }
    var itemActual by rememberSaveable { mutableStateOf("") }
    var itemValido by remember { mutableStateOf<Boolean?>(null) } // null = sin validar
    var isLoading by remember { mutableStateOf(false) }
    var errorItem by remember { mutableStateOf<String?>(null) }
    var textFieldValue2 by remember { mutableStateOf("") }

    var transferencias by remember {
        mutableStateOf<List<TransferenciaResponse>>(emptyList())
    }
    var response by rememberSaveable {
        mutableStateOf<List<ItemResponse>>(emptyList())
    }
    var response2 by rememberSaveable { mutableStateOf<List<ItemResponse>>(emptyList()) }
    var errorState by rememberSaveable { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    val itemFocusRequester = remember { FocusRequester() }
    val cantidadFocusRequester = remember { FocusRequester() }

    val keyboardController = LocalSoftwareKeyboardController.current
    var mostrarDialogoProductoNoExiste by rememberSaveable {
        mutableStateOf(false)
    }
    var extractedText by remember { mutableStateOf("") }
    var lastInput by remember { mutableStateOf("") }

    var lastCantidadInput by remember { mutableStateOf("") }
    val coroutineScope2 = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    var mostrarDialogoCantidad by remember { mutableStateOf(false) }
    var mostrarDialogoErrorServidor by remember { mutableStateOf(false) }

    var mostrarDialogoStock by remember { mutableStateOf(false) }
    var mensajeStock by remember { mutableStateOf("") }
    var habilitarProcesar by remember { mutableStateOf(false) }

    var mostrarDialogoTransferencia by remember { mutableStateOf(false) }
    var mensajeTransferencia by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var capturasPendientes   by rememberSaveable { mutableStateOf<List<CapturaPendiente>>(emptyList()) }
    var mostrarListaCapturas by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var solicitudesPendientes by remember {
        mutableStateOf<List<TransferenciaPendienteResponse>>(emptyList())
    }

    var mostrarDialogoSinDatos by remember { mutableStateOf(false) }
    var mensajeDialogo by remember { mutableStateOf("") }



    fun obtenerPendientesdia() {

        Log.d("*MAKITA*", "ingresa a ver pendientes")

        CoroutineScope(Dispatchers.IO).launch {

            try {

                Log.d("*MAKITA*", "ingresa a ver pendientes")

                val resultado = apiService.obtenerTransferenciasDiarias(usuario)


                Log.d("*MAKITA*", resultado.toString())

                withContext(Dispatchers.Main) {

                    transferencias = resultado.data

                    if (transferencias.isEmpty()) {

                        mensajeDialogo =
                            "No existen transferencias procesadas desde ayer."

                        mostrarDialogoSinDatos = true

                        mostrarListaCapturas = false

                    } else {

                        mostrarListaCapturas = true

                    }


                    //mostrarListaCapturas = transferencias.isNotEmpty()
                }

            } catch (e: Exception) {

                Log.d("*MAKITA*", e.message.toString())
                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        context,
                        "Error al consultar transferencias",
                        Toast.LENGTH_LONG
                    ).show()

                    mostrarListaCapturas = false

                }

            }
        }
    }



    fun procesarTransferencia() {

        Log.d("*MAKITA*", " ingresa a funcion procesarTransferencia $bodegaOrigen ")

        CoroutineScope(Dispatchers.IO).launch {
            try {

                // 🔹 Obtener AbsEntry origen
                val binAbsOrigen = apiService.obtenerAbsEntryUbicacion(
                    itemActual.trim(),
                    bodegaOrigen.trim(),
                    ubicacionOrigen.trim()
                )

                Log.d("*MAKITA*", " ORIGEN obtenerAbsEntryUbicacion $binAbsOrigen  $ubicacionOrigen.trim()")


                if (binAbsOrigen <= 0) {
                    withContext(Dispatchers.Main) {
                        mensajeTransferencia = "Ubicación origen no válida"
                        mostrarDialogoTransferencia = true
                    }
                    return@launch
                }

                // 🔹 Obtener AbsEntry destino
                val binAbsDestino = apiService.obtenerAbsEntryUbicacion(
                    itemActual.trim(),
                    bodegaDestino.trim(),
                    ubicacionDestino.trim()
                )


                Log.d("*MAKITA*", "DESTINO  obtenerAbsEntryUbicacion $binAbsDestino  $ubicacionDestino.trim()")


                if (binAbsDestino <= 0) {
                    withContext(Dispatchers.Main) {
                        mensajeTransferencia = "Ubicación destino no válida"
                        mostrarDialogoTransferencia = true
                    }
                    return@launch
                }

                // 🔹 Crear request

                val fechaHoy = LocalDate.now()
                    .format(DateTimeFormatter.ISO_DATE)

                val request = TransferRequest(
                    itemCode = itemActual.trim(),
                    fromWarehouse = bodegaOrigen.trim(),
                    toWarehouse = bodegaDestino.trim(),
                    quantity = cantidadActual.toDouble(),
                    binOrigen = binAbsOrigen,
                    binDestino = binAbsDestino,
                    docDate = fechaHoy,
                    usuario = usuario
                )

                Log.d("*MAKITA*", """
    itemCode: ${request.itemCode}
    fromWarehouse: ${request.fromWarehouse}
    toWarehouse: ${request.toWarehouse}
    quantity: ${request.quantity}
    binOrigen: ${request.binOrigen}
    binDestino: ${request.binDestino}
    docDate: ${request.docDate}
""".trimIndent())

                   val response = apiService.crearTransferencia(request)
                   withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        habilitarProcesar = false
                        mensajeStock = "Transferencia creada correctamente"
                    } else {
                        mensajeStock = "Error al crear transferencia (${response.code()})"
                    }
                       mostrarDialogoTransferencia = true
                   }

             //   withContext(Dispatchers.Main) {
             //       mensajeStock = "Prueba OK - Origen: $binAbsOrigen / Destino: $binAbsDestino"
             //       mostrarDialogoTransferencia = true
             //   }

            } catch (e: Exception) {

                Log.e("*MAKITA*", "ERROR EN TRANSFERENCIA", e)

                withContext(Dispatchers.Main) {
                    mensajeTransferencia = "Error: ${e.message}"
                    mostrarDialogoTransferencia = true
                }
            }
        }
    }


    fun procesarTodasLasCapturas() {
        CoroutineScope(Dispatchers.IO).launch {
            val errores = mutableListOf<String>()
            val procesadas = mutableListOf<Long>()

            capturasPendientes.forEach { captura ->
                try {
                    val binOrigen = apiService.obtenerAbsEntryUbicacion(
                        captura.itemCode,
                        captura.bodegaOrigen,
                        captura.ubicacionOrigen
                    )
                    val binDestino = apiService.obtenerAbsEntryUbicacion(
                        captura.itemCode,
                        captura.bodegaDestino,
                        captura.ubicacionDestino
                    )

                    if (binOrigen <= 0 || binDestino <= 0) {
                        errores.add("${captura.itemCode}: bin no válido")
                        return@forEach
                    }

                    val request = TransferRequest(
                        itemCode      = captura.itemCode,
                        fromWarehouse = captura.bodegaOrigen,
                        toWarehouse   = captura.bodegaDestino,
                        quantity      = captura.cantidad,
                        binOrigen     = binOrigen,
                        binDestino    = binDestino,
                        docDate       = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                        usuario       =  usuario
                    )

                    val resp = apiService.crearTransferencia(request)
                    if (resp.isSuccessful) {
                        procesadas.add(captura.id)
                    } else {
                        errores.add("${captura.itemCode}: error ${resp.code()}")
                    }

                } catch (e: Exception) {
                    errores.add("${captura.itemCode}: ${e.message}")
                }
            }

            withContext(Dispatchers.Main) {
                capturasPendientes = capturasPendientes
                    .filter { it.id !in procesadas }

                mensajeTransferencia = buildString {
                    if (procesadas.isNotEmpty())
                        appendLine("✅ ${procesadas.size} transferencia(s) creada(s).")
                    if (errores.isNotEmpty()) {
                        appendLine("⚠️ Errores:")
                        errores.forEach { appendLine("• $it") }
                    }
                }
                mostrarDialogoTransferencia = true
            }
        }
    }




    fun validarCantidad() {

        Log.d("*MAKITA*", " ingresando  a  validarCantidad ")

        if (cantidadActual.isBlank()) {
            mostrarDialogoCantidad = true
            return
        }


        val cantidad = cantidadActual.toDoubleOrNull()

        if (cantidad == null || cantidad <= 0) {
            errorState = "Cantidad inválida"
            return
        }

        Log.d("*MAKITA*", " ingresando  a  validarCantidadUbicacion ")

        CoroutineScope(Dispatchers.IO).launch {
            try {

               // ubicacionOrigen ="I34C1"

                Log.d("*MAKITA*",
                    "item=$itemActual | bodega=$bodegaOrigen | ubicacion=$ubicacionOrigen | cantidad=$cantidadActual"
                )


                val apiResponseCant = apiService.validarCantidadUbicacion(
                    itemActual.trim(),
                    bodegaOrigen.trim(),
                    ubicacionOrigen.trim()
                )


                withContext(Dispatchers.Main) {
                    val cantidadIngresada = cantidadActual.toDouble()
                    val cantidadRecibida = apiResponseCant

                    if (apiResponseCant == 0) {
                        mensajeStock = " No hay stock del item $itemActual en la ubicacion  $ubicacionOrigen  por la cantidad de: $cantidadRecibida, Cambie Ubicacion"
                        mostrarDialogoStock = true
                        return@withContext
                    }

                    //val cantidadRecibida  = apiResponseCant.first().cantidad



                    Log.d(
                        "*MAKITA*",
                        " cantidadRecibida=$cantidadRecibida | cantidadIngresada=$cantidadIngresada "
                    )

                    if (cantidadIngresada > cantidadRecibida) {
                        habilitarProcesar = false
                        mensajeStock = "La cantidad ingresada no puede superar el stock disponible de la ubicacion de origen = $cantidadRecibida."
                        mostrarDialogoStock = true
                        return@withContext
                    }

                    if (cantidadIngresada <= cantidadRecibida) {
                        habilitarProcesar = true
                        mensajeStock = "La cantidad ingresada correcta ($cantidadRecibida)."
                       // mostrarDialogoStock = true
                        return@withContext
                    }


                    errorState = ""
                    keyboardController?.hide()
                    cantidadFocusRequester.requestFocus()

                }

            } catch (e: Exception) {

                Log.e("*MAKITA*", "ERROR API", e)
                mostrarDialogoErrorServidor = true

                withContext(Dispatchers.Main) {
                    errorState = when (e) {
                        is java.net.UnknownHostException -> "Sin conexión a internet"
                        is java.net.SocketTimeoutException -> "Servidor no responde"
                        is retrofit2.HttpException -> "Error del servidor (${e.code()})"
                        else -> "Error inesperado"
                    }
                }
            }
        }
    }


    fun validarItem(itemIngresado: String) {


        val itemLimpio = itemIngresado.trim()

        if (itemLimpio.isEmpty()) {
            errorState = "Ingrese un item"
            return
        }

        errorState = ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiResponse = apiService.obtenerUbicacionItem(itemLimpio)

                withContext(Dispatchers.Main) {

                    if (apiResponse.isNullOrEmpty()) {
                        errorState = "No se encontraron datos para el item proporcionado"
                        itemActual = ""
                        itemFocusRequester.requestFocus()
                        return@withContext
                    }

                    val tieneValoresNulos = apiResponse.any { it.item == null }



                    if (tieneValoresNulos) {
                        Log.d("*MAKITA*", " ingresand modo $tieneValoresNulos ")
                        errorState = "No se encontraron datos"
                        itemActual = ""
                        mostrarDialogoProductoNoExiste = true

                        itemFocusRequester.requestFocus()
                    } else {
                        Log.d("*MAKITA*", " ingresand no tiene $tieneValoresNulos ")

                        errorState = ""
                        response = apiResponse
                        textFieldValue2 = apiResponse.first().descripcion
                        keyboardController?.hide()
                        cantidadFocusRequester.requestFocus()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorState = "Error de conexión"
                }
            }
        }



    }

    if (mostrarDialogoProductoNoExiste) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoProductoNoExiste = false
            },
            title = {
                Text("Producto no existe")
            },
            text = {
                Text("El item ingresado no existe.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoProductoNoExiste = false
                        itemFocusRequester.requestFocus()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }


    // -------- CARGA BODEGA POR DEFECTO --------
    LaunchedEffect(Unit) {
        try {
            val response = apiService.obtenerBodegaOrigen()
            val def = response.firstOrNull { it.IsDefault == 1 }
            if (def != null) {
                bodegaOrigen = def.WhsCode
                localOrigen = def.LocationName
                bodegaDestino = def.WhsCode
                localDestino = def.LocationName
            }
        } catch (_: Exception) {}
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(16.dp)
        ) {


            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Button(
                    onClick = {
                        mostrarCajaLectura = !mostrarCajaLectura
                        lecturaIniciada = true
                        modoLectura = ModoLectura.ORIGEN
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800),
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Escanear",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Red
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        "INICIAR LECTURA",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { obtenerPendientesdia()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE65100),
                        contentColor = Color.White
                    )
                ) {

                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Transferencias",
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        "VER ULTIMAS TRANSFERENCIAS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(Color(0xFF00909E))
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = { modoLectura = ModoLectura.ORIGEN },
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,   // ⬜ fondo botón
                        contentColor = Color.Black     // 🖤 texto
                    )
                ) {
                    Text(
                        text = "ORIGEN",
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = { modoLectura = ModoLectura.DESTINO },
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(
                        horizontal = 6.dp,
                        vertical = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "DESTINO",
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = { modoLectura = ModoLectura.ARTICULOS },
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(
                        horizontal = 4.dp,
                        vertical = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "ARTICULOS",
                        fontSize = 13.sp
                    )
                }
            }


            Spacer(modifier = Modifier.height(12.dp))


            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Origen: ${
                            if (ubicacionOrigen.isNotBlank())
                                "$bodegaOrigen - $ubicacionOrigen"
                            else "—"
                        }",
                        color = if (ubicacionOrigen.isNotBlank())
                            Color(0xFF2E7D32) else Color.Gray
                    )

                    Text(
                        text = "Destino: ${
                            if (ubicacionDestino.isNotBlank())
                                "$bodegaDestino - $ubicacionDestino"
                            else "—"
                        }",
                        color = if (ubicacionDestino.isNotBlank())
                            Color(0xFF2E7D32) else Color.Gray
                    )
                }
            }




            Spacer(modifier = Modifier.height(16.dp))


            if (modoLectura == ModoLectura.ORIGEN) {

                LaunchedEffect(modoLectura) {
                    focusRequester.requestFocus()
                }

                OutlinedTextField(
                    value = bodegaOrigen,
                    onValueChange = {},
                    label = { Text("Bodega Origen") },
                    modifier = Modifier.fillMaxWidth()
                )

                ubicacionOrigen  = "I34A1"
                ubicacionDestino = "I34C1"

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = localOrigen,
                    onValueChange = {},
                    label = { Text("Local Origen") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ubicacionOrigen,
                    onValueChange = {
                        ubicacionOrigen = it.uppercase().trim()
                    },
                    label = { Text("Ubicacion Origen") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) keyboardController?.hide()
                        }
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val resp = apiService.validarUbicacionBodega(
                                    bodegaOrigen,
                                    ubicacionOrigen
                                )
                                if (resp == "SI") {
                                    modoLectura = ModoLectura.DESTINO

                                } else {
                                    errorMessageUBI = "Ubicacion de origen incorrecta"
                                    ubicacionOrigen = ""
                                    showErrorDialogUBI = true
                                }
                            } catch (e: Exception) {
                                errorMessageUBI = "Error validando ubicación de origen"
                                showErrorDialogUBI = true
                            }
                        }
                    },
                    enabled = ubicacionOrigen.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("SIGUIENTE → DESTINO", fontWeight = FontWeight.Bold)
                }
            }

            // =========================================================
            // Ubicacion de  DESTINO
            // =========================================================
            if (modoLectura == ModoLectura.DESTINO) {

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                Log.d("*MAKITA*", "mPASA -> ")
                OutlinedTextField(
                    value = bodegaDestino,
                    onValueChange = {},
                    label = { Text("Bodega Destino") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFFF9C4),
                        focusedContainerColor = Color(0xFFFFF9C4))
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = localDestino,
                    onValueChange = {},
                    label = { Text("Local Destino") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFFF9C4),
                        focusedContainerColor = Color(0xFFFFF9C4))
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ubicacionDestino,
                    onValueChange = {
                        ubicacionDestino = it.uppercase().trim()
                    },
                    label = { Text("Ubicación Destino") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) keyboardController?.hide()
                        },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFFF9C4),
                        focusedContainerColor = Color(0xFFFFF9C4))

                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val resp = apiService.validarUbicacionBodega(
                                    bodegaDestino,
                                    ubicacionDestino
                                )
                                if (resp == "SI") {
                                    modoLectura = ModoLectura.ARTICULOS
                                } else {
                                    errorMessageUBI2 = "Ubicacion de destino incorrecta"
                                    showErrorDialogUBI2 = true
                                }
                            } catch (e: Exception) {
                                errorMessageUBI2 = "Error validando ubicación de destino"
                                showErrorDialogUBI2 = true
                            }
                        }
                    },
                    enabled = ubicacionDestino.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("SIGUIENTE → ARTÍCULOS", fontWeight = FontWeight.Bold)
                }
            }

            LaunchedEffect(modoLectura) {
                if (modoLectura == ModoLectura.ARTICULOS) {
                    itemActual = ""
                    errorState = ""
                    itemFocusRequester.requestFocus()
                }
            }



            if (modoLectura == ModoLectura.ARTICULOS) {

                itemActual = "DLM462Z-1"

                OutlinedTextField(
                    value = itemActual,
                    onValueChange = { newValue ->

                        val cleanedText = newValue
                            .replace("\n", "")
                            .replace("\r", "")
                            .replace("\t", "")
                            .trim()
                            .uppercase()

                        itemActual =
                            if (cleanedText.length > 20)
                                cleanedText.substring(0, 20)
                            else
                                cleanedText


                        if (itemActual.length >= 5 && itemActual != lastInput) {
                            lastInput = itemActual

                            coroutineScope.launch {
                                delay(120) // clave 🔑
                                if (itemActual == lastInput) {
                                    Log.d("*MAKITA*", "SCANNER DETECTADO → VALIDANDO")
                                    validarItem(itemActual)
                                }
                            }
                        }
                    }
                    ,
                    label = { Text("Scanear Codigo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(itemFocusRequester)
                        .onFocusChanged {
                            if (it.isFocused) keyboardController?.hide()
                        },

                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done   // ENTER
                    ),

                    keyboardActions = KeyboardActions(
                        onDone = {
                            validarItem(itemActual)
                        }
                    ),

                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Enviar",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    validarItem(itemActual)
                                }
                        )
                    },

                    isError = !errorState.isNullOrEmpty()
                )


                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = textFieldValue2.uppercase().trim(),
                    onValueChange = {},
                    readOnly = true,
                    modifier =Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = Color.Red,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                )


                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cantidadActual,
                    onValueChange = { newValue ->

                        val cleaned = newValue.trim()

                        if (cleaned.all { it.isDigit() }) {
                            cantidadActual = cleaned
                            errorState = null
                        }
                    },
                    label = { Text("Ingrese Cantidad") },

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),

                    keyboardActions = KeyboardActions(
                        onDone = {
                            validarCantidad()
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    ),

                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Validar Cantidad",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    validarCantidad()
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                        )
                    },

                    isError = !errorState.isNullOrEmpty(),

                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(cantidadFocusRequester)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // VER LISTA


                    Button(
                        onClick = {

                            Log.d("*MAKITA*", "CLICKingreso en capturasPendientes  -> Item: $usuario")
                            obtenerPendientesdia()

                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE65100),
                            contentColor = Color.White
                        )
                    ) {

                        Text(
                            //"VER AQUI (${solicitudesPendientes.size})",
                            "Ver Tranferencias hasta ayer ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }




                    // LIMPIAR
                    Button(
                        onClick = {
                            itemActual = ""
                            cantidadActual = ""
                            textFieldValue2 = ""
                            response = emptyList()
                            errorState = ""
                            habilitarProcesar = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00909E),
                            contentColor = Color.White
                        )
                    ) {
                        Text("LIMPIAR", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }


                    // AGREGAR A LISTA
                    Button(
                        onClick = {

                            val cantidad = cantidadActual.toDouble()

                            val item = itemActual
                            val descripcion = textFieldValue2
                            val ubiOrigen = ubicacionOrigen
                            val ubiDestino = ubicacionDestino
                            val bodOrigen = bodegaOrigen
                            val bodDestino = bodegaDestino

                            val captura = CapturaPendiente(
                                itemCode         = item,
                                descripcion      = descripcion,
                                cantidad         = cantidad,
                                ubicacionOrigen  = ubiOrigen,
                                ubicacionDestino = ubiDestino,
                                bodegaOrigen     = bodOrigen,
                                bodegaDestino    = bodDestino
                            )

                            capturasPendientes = capturasPendientes + captura

                            Log.d("*MAKITA*", "ingreso en capturasPendientes  -> Item: $captura")



                            CoroutineScope(Dispatchers.IO).launch {

                                try {

                                    val binAbsOrigen = apiService.obtenerAbsEntryUbicacion(
                                        item.trim(),
                                        bodOrigen.trim(),
                                        ubiOrigen.trim()
                                    )

                                    if (binAbsOrigen <= 0) {

                                        withContext(Dispatchers.Main) {
                                            mensajeTransferencia =
                                                "Ubicación origen no válida"
                                            mostrarDialogoTransferencia = true
                                        }

                                        return@launch
                                    }



                                    val binAbsDestino = apiService.obtenerAbsEntryUbicacion(
                                        item.trim(),
                                        bodDestino.trim(),
                                        ubiDestino.trim()
                                    )

                                    if (binAbsDestino <= 0) {

                                        withContext(Dispatchers.Main) {

                                            mensajeTransferencia =
                                                "Ubicación destino no válida"

                                            mostrarDialogoTransferencia = true
                                        }

                                        return@launch
                                    }


                                    val request = TransferRequest(
                                        itemCode      = item.trim(),
                                        fromWarehouse = bodOrigen.trim(),
                                        toWarehouse   = bodDestino.trim(),
                                        quantity      = cantidad.toDouble(),
                                        binOrigen     = binAbsOrigen,
                                        binDestino    = binAbsDestino,
                                        docDate       = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                                        usuario       = usuario
                                    )

                                    Log.d(
                                        "*MAKITA*",
                                        Gson().toJson(request)
                                    )


                                    Log.d(
                                        "*MAKITA*",
                                        " ingresa a funcion SolicitudTransferenciaRequest $request "
                                    )

                                    val response = apiService.crearTransferencia(request)

                                    if (response.isSuccessful) {

                                        Log.d(
                                            "MAKITA",
                                            "Insertado OK"
                                        )

                                    } else {

                                        Log.d(
                                            "MAKITA",
                                            "Error API: ${response.code()}"
                                        )
                                    }

                                } catch (e: Exception) {

                                    Log.d(
                                        "*MAKITA*",
                                        "ERROR EN TRANSFERENCIA",
                                        e
                                    )
                                }

                            }

                            // Limpiar pantalla
                            itemActual        = ""
                            cantidadActual    = ""
                            textFieldValue2   = ""
                            habilitarProcesar = false

                            itemFocusRequester.requestFocus()

                        },
                        enabled = habilitarProcesar,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "PROCESAR A SAP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

            }
        }
    }

    if (showErrorDialogUBI) {
        mostrarDialogo(
            titulo = "Error",
            mensaje = errorMessageUBI,
            onDismiss = { showErrorDialogUBI = false }
        )
    }

    if (showErrorDialogUBI2) {
        mostrarDialogo(
            titulo = "Error",
            mensaje = errorMessageUBI2,
            onDismiss = { showErrorDialogUBI2 = false }
        )
    }

    if (mostrarDialogoCantidad) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCantidad = false },
            title = {
                Text("Atención")
            },
            text = {
                Text("Debe ingresar una cantidad antes de continuar.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCantidad = false
                        cantidadFocusRequester.requestFocus()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }



    if (mostrarDialogoErrorServidor) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoErrorServidor = false },
            title = { Text("Error") },
            text = { Text("No se pudo conectar con el servidor.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoErrorServidor = false
                }) {
                    Text("OK")
                }
            }
        )
    }



    if (mostrarDialogoStock) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoStock = false },
            title = { Text("Validacion Stock") },
            text = { Text(mensajeStock) },
            confirmButton = {
                TextButton(
                    onClick =
                        {
                            mostrarDialogoStock = false

                            coroutineScope.launch {
                                delay(100)
                                cantidadActual = ""
                                cantidadFocusRequester.requestFocus()
                            }
                        }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }


    // AQUI

    if (mostrarDialogoSinDatos) {

        AlertDialog(
            onDismissRequest = {
                mostrarDialogoSinDatos = false
            },
            title = {
                Text("Información")
            },
            text = {
                Text(mensajeDialogo)
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoSinDatos = false
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )

    }

    if (mostrarListaCapturas) {

        AlertDialog(
            onDismissRequest = {
                mostrarListaCapturas = false
            },
            title = {
                Text(
                    "Transferencias del Dia (${transferencias.size})"
                )
            },
            text = {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {

                    items(
                        transferencias,
                        key = { "${it.DocEntry}-${it.LineNum}" }
                    ) { transferencia ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F5F5)
                            )
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        "Transferencia ${transferencia.DocEntry}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )

                                    Text(
                                        transferencia.ItemCode,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )

                                    Text(
                                        transferencia.ItemDescription,
                                        fontSize = 15.sp,
                                        color = Color.Gray,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(
                                        modifier = Modifier.height(4.dp)
                                    )

                                    Text(
                                        "Cantidad: ${transferencia.Quantity.toInt()}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00909E)
                                    )

                                    Text(
                                        "${transferencia.BinCodeOrigen ?: ""} → ${transferencia.BinCodeDestino ?: ""}",
                                        fontSize = 14.sp,
                                        color = Color(0xFF00909E)
                                    )

                                    Text(
                                        "Línea: ${transferencia.LineNum}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                IconButton(
                                    onClick = {

                                        CoroutineScope(Dispatchers.IO).launch {

                                            try {

                                                val resultado =
                                                    apiService.eliminarTransferenciasPendientes(
                                                        transferencia.DocEntry.toString()
                                                    )

                                                withContext(Dispatchers.Main) {

                                                    if (resultado.status == 200) {

                                                        transferencias =
                                                            transferencias.filter {
                                                                it.DocEntry != transferencia.DocEntry
                                                            }

                                                        Toast.makeText(
                                                            context,
                                                            "Transferencia eliminada",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }

                                            } catch (e: Exception) {

                                                Log.d(
                                                    "*MAKITA*",
                                                    e.message.toString()
                                                )
                                            }
                                        }
                                    }
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = Color.Red,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {

                TextButton(
                    onClick = {

                        mostrarListaCapturas = false

                        modoLectura = ModoLectura.ORIGEN

                        ubicacionOrigen = ""

                        ubicacionDestino = ""
                    }
                ) {

                    Text("CERRAR")
                }
            }
        )
    }


    if (mostrarDialogoTransferencia) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoTransferencia = false },
            title = { Text("Transferencia") },
            text = { Text(mensajeTransferencia) },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoTransferencia = false

                        // Opcional: limpiar pantalla después de procesar
                        itemActual = ""
                        cantidadActual = ""
                        textFieldValue2 = ""
                        habilitarProcesar = false
                        itemFocusRequester.requestFocus()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }




}







fun procesarScan(
    rawText: String,
    gTipoItem: String
): ItemMovimiento {

    var item = ""
    var serieDesde = ""
    var serieHasta = ""
    var ean = ""

    val text = rawText.trim().replace("\n", "").replace("\r", "")

    try {
        if (gTipoItem == "HERRAMIENTAS") {
            if (text.length > 20) {
                item        = text.substring(0, 20)
                serieDesde  = text.substring(20, text.length.coerceAtMost(29))
                serieHasta  = text.substring(29, text.length.coerceAtMost(38))
                ean         = text.substring(39, text.length.coerceAtMost(52))
            } else {
                item = text.substring(0, text.length.coerceAtMost(19))
            }
        } else {
            if (text.length >= 20) {
                item = text.substring(0, 20).trim()
                serieDesde = text.substring(20, text.length.coerceAtMost(38)).trim()
            }
        }
    } catch (e: Exception) {
        Log.e("*MAKITA*", "Error procesando etiqueta: ${e.message}")
    }

    return ItemMovimiento(
        fila = 0, // se mantiene el original luego
        item = item,
        cantidad = "",
        serieDesde = serieDesde,
        serieHasta = serieHasta,
        ean = ean
    )
}



@Composable
fun CambiarColorBarraEstado(color: Color, darkIcons: Boolean = true) {
    val view = LocalView.current

    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = android.graphics.Color.parseColor("#00909E")
        WindowCompat.getInsetsController(window, view)
            .isAppearanceLightStatusBars = darkIcons
    }
}

@Composable
fun mostrarDialogo(
    titulo: String,
    mensaje: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = titulo) },
        text = { Text(text = mensaje, fontSize = 16.sp) },

        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}



fun login(
    usuario: String,
    password: String,
    onResult: (Boolean, String?) -> Unit
) {
    val client = OkHttpClient()

    val json = """
        {
          "usuario": "$usuario",
          "password": "$password"
        }
    """.trimIndent()

    val body = json.toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url("http://172.16.128.237:2001/api/login")
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            onResult(false, "Error de conexión")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                onResult(true, null)
            } else {
                onResult(false, "Usuario o contraseña incorrectos")
            }
        }
    })
}
