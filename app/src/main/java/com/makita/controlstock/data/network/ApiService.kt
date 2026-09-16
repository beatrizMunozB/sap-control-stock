package com.makita.controlstock.data.network

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import okhttp3.ResponseBody



data class ItemMovimiento(
    val fila: Int,
    val item: String,
    val cantidad: String,
    val serieDesde: String = "",
    val serieHasta: String = "",
    val ean: String = ""
)


data class CerrarPickingResponse(
    val success: Boolean,
    val message: String,
    val idCabecera: Int? = null,
    val absEntrySAP: Int? = null,
    val idDocumento: Int? = null,
    val estado: String? = null
)


data class UsuarioResponse(
    val Empresa: String,
    val Usuario: String,
    val Capturador: String,
    val Periodo: String,
    val Mes: String,
    val Fecha: String,
    val TipoProducto: String
)


data class LoginRequest(
    val usuario: String,
    val password: String
)

data class LoginResponse(
    val ok: Boolean,
    val mensaje: String? = null,
    val usuario: String? = null
)

data class LoginSAPRequest(
    val UserName: String,
    val Password: String,
    val CompanyDB: String
)

data class LoginSAPResponse(
    val ok: Boolean,
    val message: String
)

data class BodegaOrigenResponse(
    val WhsCode: String,
    val WhsName: String,
    val LocationCode: Int,
    val LocationName: String,
    val IsDefault: Int
)


data class ItemResponse(
    var ubicacion: String,
    var descripcion: String,
    var item: String,
    var tipoItem: String
)



data class ItemNombreResponse(
    val ubicacion: String,
    val descripcion: String,
    val item: String,
    val tipoItem: Int,
    val nombreItem: String
)

data class ObtenerNombreItemResponse(
    val success: Boolean,
    val data: List<ItemNombreResponse>,
    val message: String
)

data class CantidadResponse(
    val cantidad: Double
)


data class SolicitudTransferenciaRequest(
    val itemCode: String,
    val fromWarehouse: String,
    val toWarehouse: String,
    val quantity: Double,
    val binOrigen: Int,
    val binDestino: Int,
    val docDate: String,
    val usuario: String
)





data class UbicacionResponse(

    @SerializedName("Ubicacion")
    var ubicacion: String,

    @SerializedName("Item")
    var item: String,

    @SerializedName("Descripcion")
    var descripcion: String,

    @SerializedName("PrimeraUbicacion")
    var primeraUbicacion: String,

    @SerializedName("UbicacionStandar")
    var ubicacionStandar: String,

    @SerializedName("Bodega")
    var bodega: String,

    @SerializedName("Cantidad")
    var cantidad: String,

    @SerializedName("Estado")
    var estado: String
)


data class ObtenerStockUbicacionResponse(
    val status: Int,
    val data: List<UbicacionResponse>
)


data class PendientesResponse(

    val status: Int,
    val data: List<TransferenciaPendienteResponse> = emptyList(),
    val error: String? = null
)

data class ProcesadasResponse(
    val status: Int,
    val data: List<TransferenciaResponse> = emptyList(),
    val error: String? = null
)



data class TransferenciaResponse(
    val DocEntry: Int,
    val DocNum: Int,
    val DocDate: String,
    val Usuario: String,

    val LineNum: Int,
    val ItemCode: String,
    val ItemDescription: String,
    val Quantity: Double,
    val WarehouseCode: String,

    val FirstBin: Int? = null,

    val BinAbsEntryDestino: Int? = null,
    val BinCodeDestino: String? = null,

    val BinAbsEntryOrigen: Int? = null,
    val BinCodeOrigen: String? = null
)


data class ProcesadasSalidaResponse(
    val status: Int,
    val data: List<SalidaMercanciaResponse> = emptyList(),
    val error: String? = null
)


data class SalidaMercanciaResponse(
    val DocEntry: Int,
    val DocNum: Int,
    val DocDate: String,
    val Usuario: String,

    val LineNum: Int,
    val ItemCode: String,
    val ItemDescription: String,
    val Quantity: Double,
    val WarehouseCode: String,

    val FirstBin: Int? = null,

    val BinAbsEntryOrigen: Int? = null,
    val BinCodeOrigen: String? = null
)



data class ProcesadasEntradaResponse(
    val status: Int,
    val data: List<EntradaMercanciaResponse> = emptyList(),
    val error: String? = null
)

data class EntradaMercanciaResponse(
    val DocEntry: Int,
    val DocNum: Int,
    val DocDate: String,
    val Usuario: String,
    val LineNum: Int,
    val ItemCode: String,
    val ItemDescription: String,
    val Quantity: Double,
    val WarehouseCode: String,
    val FirstBin: Int? = null,
    val BinAbsEntryOrigen: Int? = null,
    val BinCodeOrigen: String? = null
)


data class TransferenciaPendienteResponse(
    val Code: String,
    val U_Folio: String,
    val U_Usuario: String,
    val U_DocDate: String,
    val U_DocType: String,
    val U_ItemCode: String,
    val U_Descripcion: String,
    val U_Cantidad: Double,
    val U_UbiOrigen: String,
    val U_UbiDestino: String,
    val U_BodOrigen: String,
    val U_BodDestino: String,
    val U_Estado: String
)


data class TransferRequest(
    val itemCode: String,
    val fromWarehouse: String,
    val toWarehouse: String,
    val quantity: Double,
    val binOrigen: Int,
    val binDestino: Int,
    val docDate: String,
    val usuario: String
)



data class ListaTrasladoSolicitudResponse(
    val status: Int,
    val data: List<SolicitudCabeceraResponse> = emptyList(),
    val error: String? = null
)

data class SolicitudCabeceraResponse(
    val DocEntry: Int,
    val DocNum: Int,
    val DocDate: String,
    val CardCode: String?,
    val CardName: String?,
    val FromWarehouse: String?,
    val ToWarehouse: String?
)




data class ListaPickingSolicitudResponse(
    val status: Int,
    val data: List<PickingCabeceraResponse> = emptyList(),
    val error: String? = null
)

data class PickingCabeceraResponse(
    val AbsEntry : Int,
    val ObjType : String?,
    val ObjTypeName : String?,
    val PickDate: String,
    val U_PGX_LIGO_USUARIO : String?,
    val Remarks : String?,
    val Status: String?,
    val StatusName: String?
)



data class ListaPickingDetalleResponse(
    val status: Int,
    val data: List<PickingDetalleResponse> = emptyList(),
    val error: String? = null
)

data class PickingDetalleResponse(
val AbsEntry : Int,
val PickEntry : String?,
val ItemCode : String?,
val CantidadLiberada : String,
val CantidadPickeada : String?,
val CantidadCapturada : Double?,
val CantidadPickeadaSAP : Double?,
val WhsCode : String?,
val BinAbs : String?,
val BinCode : String?,
val CantidadBin :   Double?,
val CantidadPickeadaBin :  Double?,
val OrderEntry : String?,
val OrderLine : String?,
val BaseObject : String?
)



data class ListaPickingDetalleUbicacionResponse(
    val status: Int,
    val data: List<PickingDetalleUbicacionResponse> = emptyList(),
    val error: String? = null
)

data class PickingDetalleUbicacionResponse(
    val IdDetalle: Int?,
    val AbsEntry : Int,
    val PickEntry : String?,
    val ItemCode : String?,
    val ItemName : String?,
    val ItmsGrpCod : String?,
    val ItmsGrpNam : String?,
    val WhsCode : String?,
    val CantidadLiberada : String,
    val CantidadPickeada : String?,
    val BinAbs : String?,
    val BinCode : String?,
    val CantidadBin :   Double?,
    val CantidadPickeadaBin :  Double?,
    val OrderEntry : String?,
    val OrderLine : String?,
    val BaseObject : String?,
    val CantidadCapturada: Double?
)

data class RegistrarPickingCapturaRequest(
    val idDetalle: Int,
    val numeroSerie: String,
    val cantidad: Int,
    val whsCode: String,
    val binAbs: Int,
    val barCode: String?,
    val usuario: String
)









/*
data class SolicitudDetalleResponse(
    val LineNum: Int,
    val ItemCode: String,
    val ItemDescription: String,
    val Quantity: Double
)
*/


data class SalidaMercanciaRequest(
    val itemCode: String,
    val fromWarehouse: String,
    val quantity: Double,
    val binOrigen: Int,
    val docDate: String,
    val usuario: String
)

data class EntradaMercanciaRequest(
    val itemCode: String,
    val fromWarehouse: String,
    val quantity: Double,
    val binOrigen: Int,
    val docDate: String,
    val usuario: String
)




data class TransferLine(

    val ItemCode: String,
    val ItemDescription: String?,
    val Quantity: Double,
    val FromWarehouseCode: String?,
    val WarehouseCode: String?

)


data class GenericResponse(

    val status: Int,
    val message: String? = null,
    val error: String? = null
)

data class SolicitudDetalleResponse(
    val status: Int,
    val data: List<SolicitudDetalleItem> = emptyList(),
    val error: String? = null
)

data class SolicitudDetalleItem(
    val LineNum: Int,
    val ItemCode: String,
    val Dscription: String,
    val Quantity: Double
)


data class RegistrarTrasladoSolicitudRequest(
    val DocNumOWTQ: Int,
    val FechaProceso: String,
    val Usuario: String,
    val FromWhsCode: String,
    val ToWhsCode: String,
    val UbicacionOrigen: String,
    val UbicacionDestino: String,
    val ItemCode: String,
    val SerieDesde: String,
    val SerieHasta: String,
    val CodigoEAN: String
)


data class RegistrarTrasladoSolicitudDetalleRequest(
    val IdCabecera: Int,
    val LineNum: Int,
    val ItemCode: String,
    val Quantity: Int,
    val CantidadCapturada: Int,
    val BinAbsOrigen: Int?,
    val BinCodeOrigen: String?,
    val BinAbsDestino: Int?,
    val BinCodeDestino: String?,
    val Estado: String
)


data class RegistrarCapturaRequest(
    val IdDetalle: Int,
    val NumeroSerie: String,
    val Cantidad: Int,
    val Usuario: String,
    val FechaCaptura: String
)

data class RespuestaGeneral(
    val status: Int,
    val message: String,
    val error: String? = null
)

data class IniciarSolicitudLecturaRequest(
    val usuario: String
)


data class RespuestaGenerica(
    val success: Boolean,
    val message: String,
    val idCabecera: Int? = null
)



data class SolicitudLecturaCabeceraResponse(
    val id: Int,
    val docEntryOWTQ: Int,
    val docNumOWTQ: Int,
    val cardCode: String,
    val cardName: String,
    val fromWhsCode: String,
    val toWhsCode: String,
    val docDate: String?,
    val docDueDate: String?,
    val comments: String?,
    val usuario: String,
    val estado: String
)


data class SolicitudLecturaDetalleResponse(
    val id: Int,
    val idCabecera: Int,
    val lineNum: Int,
    val itemCode: String,
    val dscription: String,
    val quantity: Int,
    val cantidadCapturada: Int,
    val openQty: Int,
    val fromWhsCode: String,
    val toWhsCode: String,
    val binAbsOrigen: Int?,
    val binCodeOrigen: String?,
    val binAbsDestino: Int?,
    val binCodeDestino: String?,
    val lineStatus: String?,
    val tipoItem: String?,
    val estado: String
)

data class SolicitudLecturaResponse(
    val success: Boolean,
    val cabecera: SolicitudLecturaCabeceraResponse,
    val detalle: List<SolicitudLecturaDetalleResponse>
)


data class RegistrarCapturaSolicitudRequest(
    val idCabecera: Int,
    val itemCode: String,

    val fromWhsCode: String,
    val toWhsCode: String,

    val binAbsOrigen: Int?,
    val binCodeOrigen: String,

    val binAbsDestino: Int?,
    val binCodeDestino: String,

    val serieDesde: String,
    val serieHasta: String,
    val codigoEAN: String,
    val usuario: String
)


data class BinUbicacionResponse(
    val success: Boolean,
    val binAbs: Int?,
    val binCode: String?,
    val whsCode: String?,
    val message: String?
)

data class CrearTransferenciaSolicitudRequest(
    val idCabecera: Int
)


data class CrearTransferenciaSolicitudResponse(
    val success: Boolean,
    val status: Int,
    val message: String? = null,
    val docEntry: Int? = null,
    val docNum: Int? = null,
    val series: Int? = null
)



// PARA CAPTURAR PICKING
data class IniciarPickingLecturaRequest(
    val usuario: String
)

/* Similar al de BIN pero para picking*/
data class DatosUbicacionResponse(
    val success: Boolean,
    val binAbs: Int?,
    val binCode: String?,
    val whsCode: String?,
    val barCode: String?,
    val message: String?
)

/* ANTES

data class RegistrarPickingCapturaResponse(
    val success: Boolean,
    val message: String
)

*/


data class RegistrarPickingCapturaResponse(
    val success: Boolean,
    val message: String,
    val idDetalle: Int,
    val idCabecera: Int,
    val detalleCompleto: Boolean,
    val pickingCompleto: Boolean,
    val totalDetalles: Int,
    val detallesCompletados: Int,
    val siguienteDetalle: SiguienteDetalleResponse? = null
)

data class SiguienteDetalleResponse(
    val Id: Int,
    val ItemCode: String?,
    val WhsCode: String?,
    val BinAbs: Int?,
    val BinCode: String?,
    val CantidadLiberada: Int,
    val CantidadCapturada: Int,
    val Estado: String?
)


data class ObtenerIdCabeceraPickingResponse(
    val success: Boolean,
    val message: String?,
    val idCabecera: Int?
)


data class PickingCapturaResponse(
    @SerializedName("Id")
    val id: Int,

    @SerializedName("IdDetalle")
    val idDetalle: Int,

    @SerializedName("NumeroSerie")
    val numeroSerie: String?,

    @SerializedName("Cantidad")
    val cantidad: Int,

    @SerializedName("WhsCode")
    val whsCode: String?,

    @SerializedName("BinAbs")
    val binAbs: Int?,

    @SerializedName("BarCode")
    val barCode: String?,

    @SerializedName("Usuario")
    val usuario: String?,

    @SerializedName("FechaCaptura")
    val fechaCaptura: String?,

    @SerializedName("Procesado")
    val procesado: Boolean,

    @SerializedName("ItemCode")
    val ItemCode: String?,

    @SerializedName("ItemName")
    val ItemName: String?
)

data class PickingCapturasResponse(
    val success: Boolean,
    val data: List<PickingCapturaResponse>,
    val message: String?
)


data class EliminarPickingCapturaResponse(
    val success: Boolean,
    val message: String,
    val idCaptura: Int?,
    val idDetalle: Int?
)

data class EnviarDetalleASAPRequest(
    val idCabecera: Int,
    val absEntry: Int,
    val enviarParcial: Boolean
)
/*
data class EnviarDetalleASAPResponse(
    val success: Boolean,
    val message: String?,
    val idCabecera: Int?,
    val idDetalle: Int?,
    val absEntry: Int?,
    val binAbs: Int?,
    val capturas: List<PickingCapturaResponse>?
)*/

data class EnviarDetalleASAPResponse(
    val success: Boolean,
    val message: String?,
    val idCabecera: Int?,
    val absEntry: Int?,
    val detallesEnviados: Int?,
    val sapStatus: String?
)

data class VerificarPickingCompletoResponse(
    val success: Boolean,
    val idCabecera: Int?,
    val pickingCompleto: Boolean,
    val totalDetalles: Int,
    val detallesCompletados: Int,
    val message: String? = null
)


data class EstadoPickingResponse(
    val success: Boolean,
    val idCabecera: Int? = null,
    val estado: String? = null,
    val pickingCompleto: Boolean = false,
    val message: String? = null
)


data class ObtenerPickingProcesadoResponse(
    val success: Boolean,
    val data: List<PickingCapturaResponse>,
    val message: String
)


data class PickingOrdenResponse(
    val numeroOrdenVenta: Int,
    val cardCode: String?,
    val cardName: String?,
    val pickingAbsEntry: Int,
    val usuarioAsignado: String?,
    val cantidadItems: Int
)

data class PickingOrdenApiResponse(
    val success: Boolean,
    val data: List<PickingOrdenResponse>,
    val message: String
)


data class OrdenVentaPickingResponse(
    val numeroOrdenVenta: Int,
    val cardCode: String?,
    val cardName: String?,
    val pickingAbsEntry: Int,
    val usuarioAsignado: String?,
    val cantidadItems: Int
)

data class OrdenVentaPickingResponseApiResponse(
    val success: Boolean,
    val data: List<OrdenVentaPickingResponse>,
    val message: String
)


interface ApiService
{


    @POST("api/login-sap")
    suspend fun loginSAP(
        @Body request: LoginSAPRequest
    ): LoginSAPResponse


    @GET("api/validar-usuario/{usuario}")
    suspend fun validarUsuario(
        @Path("usuario") Usuario: String
    ):  String


    @GET("api/obtener-bodega-origen")
    suspend fun obtenerBodegaOrigen(): List<BodegaOrigenResponse>

    @GET("api/validar-ubicacion/{bodega}/{ubicacion}")
    suspend fun validarUbicacionBodega(
        @Path("bodega") bodega: String,
        @Path("ubicacion") ubicacion: String
    ): String


    @GET("api/obtener-bin-ubicacion/{bodega}/{ubicacion}")
    suspend fun obtenerBinUbicacion(
        @Path("bodega") bodega: String,
        @Path("ubicacion") ubicacion: String
    ): BinUbicacionResponse

    @GET("api/validar-Item/{item}")
    suspend fun validarItem(
        @Path("Item") item: String
    ): String

    @GET("api/obtener-ubicacion/{item}")
    suspend fun obtenerUbicacionItem(@Path("item") item: String) : List<ItemResponse>


    @GET("api/obtener-nombre-item/{item}")
    suspend fun obtenerNombreItem(
        @Path("item") item: String
    ): ObtenerNombreItemResponse


    @GET("api/obtener-stock-ubicacion/{ubicacion}")
    suspend fun obtenerStockUbicacion(
        @Path("ubicacion") ubicacion: String
    ): ObtenerStockUbicacionResponse

    @GET("api/obtener-stock-item/{item}")
    suspend fun obtenerStockItem(
        @Path("item") item: String
    ): ObtenerStockUbicacionResponse


    @GET("api/validar-cantidad-ubicacion/{item}/{bodega}/{ubicacion}")
    suspend fun validarCantidadUbicacion(
        @Path("item") item: String,
        @Path("bodega") bodega: String,
        @Path("ubicacion") ubicacion: String
    ): Int


    @GET("api/obtener-absentry-ubicacion/{item}/{bodega}/{ubicacion}")
    suspend fun obtenerAbsEntryUbicacion(
        @Path("item") item: String,
        @Path("bodega") bodega: String,
        @Path("ubicacion") ubicacion: String
    ): Int


    @POST("api/transferencia-stock")
    suspend fun crearTransferencia(
        @Body request: TransferRequest
    ): Response<Unit>

    @POST("api/salida-mercancia")
    suspend fun crearSalidaMercancia(
        @Body request: SalidaMercanciaRequest
    ): Response<Unit>

    @POST("api/entrada-mercancia")
    suspend fun crearEntradaMercancia(
        @Body request: EntradaMercanciaRequest
    ): Response<Unit>




    /* lo usare despues
    @POST("api/solicitud-transferencia")
    suspend fun crearSolicitudTransferencia(
        @Body request: SolicitudTransferenciaRequest
    ): Response<Unit>
   */


    @GET("api/transferencias-diarias/{usuario}")
    suspend fun obtenerTransferenciasDiarias(
        @Path("usuario") usuario: String
    ): ProcesadasResponse


    @POST("api/transferencias-eliminar/{docentry}")
    suspend fun eliminarTransferenciasPendientes(
        @Path("docentry") docentry: String
    ): GenericResponse

    @POST("api/salidamercancia-eliminar/{docentry}")
    suspend fun eliminarSalidaMercancia(
        @Path("docentry") docentry: String
    ): GenericResponse


    @POST("api/iniciar-solicitud-lectura/{docentry}")
    suspend fun iniciarSolicitudLectura(
        @Path("docentry") docEntry: Int,
        @Body request: IniciarSolicitudLecturaRequest
    ): RespuestaGenerica

    @GET("api/salidamercancia-diarias/{usuario}/{bodega}")
    suspend fun obtenerSalidaMercanciasDiarias(
        @Path("usuario") usuario: String,
        @Path("bodega") bodega: String
    ): ProcesadasSalidaResponse

    @GET("api/entradamercancia-diarias/{usuario}/{bodega}")
    suspend fun obtenerEntradaMercanciasDiarias(
        @Path("usuario") usuario: String,
        @Path("bodega") bodega: String
    ): ProcesadasEntradaResponse

    @POST("api/entradamercancia-eliminar/{docentry}")
    suspend fun eliminarEntradaMercancia(
        @Path("docentry") docentry: String
    ): GenericResponse

    @GET("api/traslado-solicitud/{usuario}/{bodega}")
    suspend fun obtenerTrasladoSolicitud(
        @Path("usuario") usuario: String,
        @Path("bodega") bodega: String
    ): ListaTrasladoSolicitudResponse


    @GET("api/obtener-picking-solicitud/{usuario}/{bodega}")
    suspend fun obtenerPickingSolicitud(
        @Path("usuario") usuario: String,
        @Path("bodega") bodega: String
    ): ListaPickingSolicitudResponse

    @GET("api/validar-solicitud/{docnum}")
    suspend fun validarSolicitud(
        @Path("docnum") docnum: String
    ): ListaTrasladoSolicitudResponse

    @GET("api/validar-picking/{AbsEntry}")
    suspend fun validarPicking(
        @Path("AbsEntry") AbsEntry: String
    ): ListaPickingSolicitudResponse



    @GET("api/obtener-picking-detalle/{AbsEntry}")
    suspend fun obtenerPickingDetalle(
        @Path("AbsEntry") AbsEntry: Int
    ): ListaPickingDetalleResponse


    @GET("api/obtener-ubicacion-picking/{AbsEntry}/{BinAbs}")
    suspend fun obtenerUbicacionPickingDetalle(
        @Path("AbsEntry") AbsEntry: Int,
        @Path("BinAbs") BinAbs: Int,
    ): ListaPickingDetalleUbicacionResponse



    @GET("api/traslado-solicitud-detalle/{docEntry}")
    suspend fun obtenerDetalleSolicitud(
        @Path("docEntry") docEntry: Int
    ): SolicitudDetalleResponse


    @GET("api/validar-ubicacion-item/{fechainventario}/{item}/{ubicacion}/{usuario}")
    suspend fun validarUbicacionProducto(
        @Path("fechainventario") FechaInventario: String,
        @Path("item") Item: String,
        @Path("ubicacion") Ubicacion: String,
        @Path("usuario") Usuario: String
    ):  String

    @POST("api/registrar-traslado-solicitud")
    suspend fun registrarTrasladoSolicitud(
        @Body request: RegistrarTrasladoSolicitudRequest
    ): RespuestaGeneral

    @GET("api/insertar-inventario/{item}/{tipoitem}")
    suspend fun validarTipoItem(
        @Path("item") Item: String,
        @Path("tipoitem") tipoitem: String
    ):  String


    @GET("api/validar-existe-item/{item}")
    suspend fun validarExisteItem(
        @Path("item") Item: String
    ):  String

    @GET("api/solicitud-lectura/{idCabecera}")
    suspend fun obtenerSolicitudLectura(
        @Path("idCabecera") idCabecera: Int
    ): SolicitudLecturaResponse


    @POST("api/registrar-captura-solicitud")
    suspend fun registrarCapturaSolicitud(
        @Body request: RegistrarCapturaSolicitudRequest
    ): Response<GenericResponse>


    @GET("api/validar-maneja-ubicacion/{bodega}")
    suspend fun validarManejaUbicacion(
        @Path("bodega") bodega: String
    ): String

    @POST("api/crearTransferenciaSolicitud")
    suspend fun crearTransferenciaSolicitud(
        @Body request: CrearTransferenciaSolicitudRequest
    ): CrearTransferenciaSolicitudResponse

    @GET("api/consultar-TipoItem/{item}")
    suspend fun consultarTipoItem(
        @Path("item") Item: String
    ):  String



    @POST("api/iniciar-picking-lectura/{absEntry}")
    suspend fun iniciarPickingLectura(
        @Path("absEntry") absEntry: Int,
        @Body request: IniciarPickingLecturaRequest
    ): RespuestaGenerica

    @POST("api/obtener-cabecera-picking/{docentry}")
    suspend fun obtenerIdCabeceraPicking(
        @Path("docentry") docentry: Int
    ): ObtenerIdCabeceraPickingResponse


    @GET("api/obtener-datos-ubicacion/{bodega}/{ubicacion}")
    suspend fun obtenerDatosUbicacion(
        @Path("bodega") bodega: String,
        @Path("ubicacion") ubicacion: String
    ): DatosUbicacionResponse


    @POST("api/registrar-picking-captura")
    suspend fun registrarPickingCaptura(
        @Body request: RegistrarPickingCapturaRequest
    ): RegistrarPickingCapturaResponse

    @POST("api/cerrar-picking/{item}")
    suspend fun cerrarPicking(
        @Path("item") idCabecera: Int
    ): CerrarPickingResponse




    @GET("api/obtener-picking-capturas/{idDetalle}/{binAbs}")
    suspend fun obtenerPickingCapturas(
        @Path("idDetalle") idDetalle: Int,
        @Path("binAbs") binAbs: Int
    ): PickingCapturasResponse

    @DELETE("api/eliminar-picking-captura/{id}")
    suspend fun eliminarPickingCaptura(
        @Path("id") id: Int
    ): EliminarPickingCapturaResponse

    @POST("api/enviar-detalle-sap")
    suspend fun enviarDetalleASAP(
        @Body request: EnviarDetalleASAPRequest
    ): EnviarDetalleASAPResponse

    @GET("api/verificar-picking-completo/{idCabecera}")
    suspend fun verificarPickingCompleto(
        @Path("idCabecera") idCabecera: Int
    ): VerificarPickingCompletoResponse

    @GET("api/obtener-estado-picking/{absEntry}")
    suspend fun obtenerEstadoPicking(
        @Path("absEntry") absEntry: Int
    ): EstadoPickingResponse


    @GET("api/obtener-picking-procesado/{absEntry}")
    suspend fun obtenerPickingProcesado(
        @Path("absEntry") absEntry: Int
    ): ObtenerPickingProcesadoResponse

    @GET("api/picking-documento/{idDocumento}/pdf")
    suspend fun obtenerPDFPicking(
        @Path("idDocumento") idDocumento: Int
    ): ResponseBody

    @GET("api/picking-por-orden/{docNum}")
    suspend fun obtenerPickingsPorOrden(
        @Path("docNum") docNum: Int
    ): PickingOrdenApiResponse

    @GET("api/orden-por-picking/{absEntry}")
    suspend fun obtenerOrdenPorPicking(
        @Path("absEntry") absEntry: Int
    ): OrdenVentaPickingResponseApiResponse



    



































}