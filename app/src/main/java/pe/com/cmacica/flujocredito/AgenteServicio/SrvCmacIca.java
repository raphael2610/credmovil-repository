package pe.com.cmacica.flujocredito.AgenteServicio;

/**
 * Created by JHON on 29/06/2016.
 */
public class SrvCmacIca {

 // SERVIDOR PRUEBAS EN PRODUCCIÓN
    /*   private static final String PUERTO_HOST = ":8080";
        private static final String HOST_WEBAPI = "http://www.cmacica.com.pe"+ PUERTO_HOST + "/CrediMovil_Des/api/";
    */
 // SERVIDOR PRODUCCIÓN
//	private static final String PUERTO_HOST = ":8080";
//    private static final String HOST_WEBAPI = "http://www.cmacica.com.pe"+ PUERTO_HOST + "/ServicioCrediMovil/api/";
	//10.255.255.246

 //SERVIDOR DESARROLLO
	 private static final String PUERTO_HOST = "";
//    private static final String HOST_WEBAPI = "http://172.20.10.97"+ PUERTO_HOST + "/Servicios/CrediMovil/api/";
   private static final String HOST_WEBAPI = "http://172.20.10.97"+ PUERTO_HOST + "/optimusrest/api/";

 //SERVIDOR QA
	//private static final String PUERTO_HOST = "";
	//private static final String HOST_WEBAPI = "http://172.20.10.46"+ PUERTO_HOST + "/Servicios/ServicioCrediMovil/api/";

    /**
     * URLs del Web Service
     */

    //region Calificacion
    //public static final String GET_CALIF_SBS = HOST_WEBAPI + "Calificacion/GetCalifPorDoi?doi=";
    public static final String GET_DEUDA_IFIS = HOST_WEBAPI + "Calificacion/GetDeudaIfis?codsbs=%s&fecha=%s";
    public static final String GET_PLANPAGO_HIST= HOST_WEBAPI + "Calificacion/ListarPlanPago?cCtaCod=%s&cImei=%s";
    public static final String GET_INFOCLIENTE = HOST_WEBAPI + "Persona/SelInfoCliente?cNumDoc=%s&cTipoDoc=%s&cImei=%s";
    //endregion

    //region Simulador
    //public static final String GET_PLAN_PAGO = HOST_WEBAPI + "PlanPago/GetPlanPago?nTipoCredito=%s&MontoSol=%s&NroCuotas=%s&TipoPeriocidad=%s&TEM=%s&nMoneda=%s&nDiaFijo=%s&cPeriodoFijo=%s&nPeriodicidadEnDias=%s&dFechaDesembolso=%s&nDiasGracia=%s&bProximoMes=%s";
        public static final String POST_PLAN_PAGO = HOST_WEBAPI + "PlanPago/GetPlanPago";
    public static final String GET_ALL_AGENCIAS = HOST_WEBAPI+ "Agencia/GetAllAgencias";
    public static final String GET_ALL_TIPOCREDITO = HOST_WEBAPI + "TipoCreditos/SelTipoCreditos";
    public static final String GET_PERSONA_TIPOCREDITO = HOST_WEBAPI + "TipoCreditos/ListaTipoCreditos?nTipoPersona=%s";
    public static final String GET_FILTER_PRODUCTO = HOST_WEBAPI +"CredProductos/SelCredProductos?cAgeCod=%s&nTipoCredito=%s";
    public static final String GET_FRECUENCIA_PAGO= HOST_WEBAPI + "CredProductos/GetFrecPagoPorProducto?cCredProducto=";
    //endregion

    //region Seguridad
    public static final String GET_VALIDA_DEVICE = HOST_WEBAPI+"Auth/ValidadDevice";
    public static final String GET_VALIDA_USUARIO = HOST_WEBAPI+"Auth/LogingMovil";
    //endregion

    //region Fuentes de ingreso
    public static final String URL_SYNC_BATCH_Fte_Igr = HOST_WEBAPI + "FuenteIngreso/";
    public static final String URL_SYNC_BATCH_FTEIGR_REQUEST = HOST_WEBAPI+"FuenteIngreso/ListFteIgrClixAna?cPersCodAna=%s&cImei=%s&Fecha=%s";
    public static final String URL_SYNC_BATCH_FTEIGR_RESPONSE = HOST_WEBAPI+"FuenteIngreso/SyncFteIgrMovil";
    //endregion

    //region Fuentes de Persona
    public static final String URL_SYNC_BATCHPersona = HOST_WEBAPI +"Persona/PersonaSyncBatch?filtro=%s&cImei=%s";
    //endregion

    //region CONSTANTES--------------------------------------------------------------------------------------------
    public static final String URL_SYNC_BATCHConstante = HOST_WEBAPI +"persona/ObtenerConstante";
    //endregion-----------------------------------------------------------------------------------------------------

    //region PERSONA------------------------------------------------------------------------------------------------
    public static final String GET_OBTENERPERSONA= HOST_WEBAPI+"Persona/PersonaReniec?cNumDoc=%s";
    public static final String GET_OCUPACION=HOST_WEBAPI+"Persona/SelOcupacion";
    public static final String POST_PERSONA= HOST_WEBAPI+"Persona/GuardarPersonaReniec";
    public static final String GET_UBIGEO= HOST_WEBAPI+"Persona/ListarUbigeo?cCodUbigeo=%s&pTipoFiltro=%s";
    //endRegion------------------------------------------------------------------------------------------------------
    //region SOLICITUD----------------------------------------------------------------------------------------------

    public static final String GET_PROCESO=HOST_WEBAPI+"Solicitud/ListaProcesosCreditos";
    public static final String GET_CAMPAÑAS=HOST_WEBAPI+"Solicitud/GetCampanasPorAge?CodAgencia=%s";
    public static final String GET_PROYECTOS=HOST_WEBAPI+"Solicitud/ListarProyectoAsocYGrupoOrg";
    public static final String GET_AGENCIASBN_AGE=HOST_WEBAPI+"Solicitud/ListaAgenciaBnPorAge?CodAgencia=%s";
    public static final String GET_AGROPECUARIOS=HOST_WEBAPI+"Solicitud/ListarActividadesAgropecuariasesPorProducto?CodProducto=%s";
    public static final String GET_DESTINOS=HOST_WEBAPI+"Solicitud/ListaCreddestinos?nTipoCredito=%s&CodProducto=%s";
    public static final String GET_PROYECTOS_INMOBILIARIOS=HOST_WEBAPI+"Solicitud/ListarProyectosInmobiliarios?cCodAgencia=%s&cCodProducto=%s";
    public static final String GET_INTS_CONVENIO=HOST_WEBAPI+"Solicitud/ListaIntsConvenio?nPerstipo=%s&nTipoSector=%s";
    public static final String GET_VERIF_EVA_MEN=HOST_WEBAPI+"Solicitud/ReclasificarEval?pnTipoCred=%s&pCodigoPersona=%s&nTipoPersona=%s&nMonto=%s&pbLineaCredito=%s&bFlag=%s";
    public static final String GET_DATO_CLIENTE_SOL=HOST_WEBAPI+"Solicitud/SelDatoClienteSolCred?cDoiCliente=%s&cUserOpe=%s&cAgeOpe=%s&cImei=%s";
    public static final String GET_SEL_CONDICION_SOL=HOST_WEBAPI+"Solicitud/SelCondicionSolicitud?cCodCliente=%s";
    public static final String POST_INGRESO_VENTAS=HOST_WEBAPI+"Solicitud/InsEndeuPersonaSol";
    public static final String POST_REGISTRO_SOLICITUD=HOST_WEBAPI+"Solicitud/EjecutarSolicitudCreditoMovil";
    public static final String POST_MOTOR_EVA= HOST_WEBAPI+"Solicitud/ListaReglasValidacion";
    public static final String GET_CREDPRODUCTOS= HOST_WEBAPI+"Solicitud/SelCredProductos?cAgeCod=%s&nTipoCredito=%s";
    public static final String GET_ESTADOS_SOLICITUD= HOST_WEBAPI+"Solicitud/ListarEstadosSolicitud";

    //endRegion-----------------------------------------------------------------------------------------------------

    //Region COBRANZAS---------------------------------------------------------------------------------------------
    public static final String GET_VALIDA_GESTOR=HOST_WEBAPI+"Cobranza/Validagestor?Usuario=%s";

    public static final String GET_TIPO_CONTACTO=HOST_WEBAPI+"Cobranza/SelTipoContatoGestor?TipoContacto=%s";

    public static final String GET_PERSONA_TELEFONO=HOST_WEBAPI+"Cobranza/ListaTelefonoPersona?Codigo=%s";

    public static final String GET_TIPO_GESTION=HOST_WEBAPI+"Cobranza/ListarTipogestion?TipoContacto=%s";

    public static final String GET_MOTIVONOPAGO=HOST_WEBAPI+"Cobranza/ListarMotivoNoPago?nIdResultado=%s";

    public static final String GET_RESULTADO=HOST_WEBAPI+"Cobranza/ListarResultados?TipoContacto=%s&TipoGestion=%s";

    public static final String GET_RESULTADOMK=HOST_WEBAPI+"Cobranza/ListarResultadoMK?TipoContacto=%s&TipoGestion=%s";

    public static final String GET_ESTGESTION=HOST_WEBAPI+"Cobranza/ListarEstgestion";

    public static final String GET_DETALLE_GESTION=HOST_WEBAPI+"Cobranza/ListarDetalleGestion?Dni=%s";

    public static final String POST_REGISTRO_GESTION=HOST_WEBAPI+"Cobranza/RegistroGestion";

    public static final String GET_CLIENTES_COBRANZA=HOST_WEBAPI+"Cobranza/ListarClientesCobranza?Codigoanalista=%s";

	public static final String GET_CLIENTES_RECUPERACIONES=HOST_WEBAPI+"Cobranza/ListarClientesRecuperaciones?Codigoanalista=%s";

		public static final String GET_OBTENER_CONSTANTE_SISTEMA = HOST_WEBAPI+"Cobranza/ObtenerConstanteSistema?nConsSisCod=%s";

		//endregion---------------------------------------------------------------------------------------------------------

		//region GeoReferenciacion
		public static final String GET_CLIENTES_GEOREF = HOST_WEBAPI+"Persona/SeleccionarGeoreferenciacion?latitud=%s&longitud=%s&radio=%s";
		public static final String POST_CLIENTES_GEOREF = HOST_WEBAPI+"Persona/InsertarGeoreferenciacion";


        public static final String GET_CLIENTE = HOST_WEBAPI + "Persona/SeleccionarGeoCliente?doi=%s&Usuario=%s";
        public static final String GET_CREDITOS_CLIENTE = HOST_WEBAPI + "Persona/SeleccionarCreditosCliente?doi=%s";
        public static final String POST_GEOCLIENTES = HOST_WEBAPI + "Persona/InsertarGeoClientes";
        public static final String GET_CLIENTES_CARTERA = HOST_WEBAPI + "Persona/SeleccionarClientesPorCartera?user=%s";
        public static final String GET_CLIENTES_CON_GEOPOSICION = HOST_WEBAPI + "Persona/SeleccionarClientesConGeoPosicion?nombres=%s&apellidoPaterno=%s&apellidoMaterno=%s&usuario=%s";
		//endregion

        //AgendaComercial
        public static final String URL_AGENDA_COMERCIAL = "https://cajaica.pe:4443/ventas/Home/Logeodevice/%s-%s";

        //region Autoservicio
        public static final String POST_RESET_PASSWORD = HOST_WEBAPI+"Auth/ResetPassword";
        public static final String POST_UNLOCK_ACCOUNT = HOST_WEBAPI+"Auth/UnlockAccount";

    //region Agenda Comercial FJCG
    public static final String GET_ID_USUARIO_ANALISTA = HOST_WEBAPI  + "Persona/SeleccionarIdUsuarioAnalistaBDVentas?codUsuario=%s";
    public static final String GET_PRODUCTOS_OFRECIDOS_ANALISTA = HOST_WEBAPI  + "Persona/ListarProductosOfrecidosAnalista?IdUsuario=%s";
    public static final String POST_RESULTADOS_VISITA = HOST_WEBAPI  + "Persona/InsertarResultadosVisita";
    public static final String GET_TIPOS_CONTACTO = HOST_WEBAPI  + "Persona/ListarTipoContactoDeVisita";
    public static final String GET_RESULTADOS_VISITA = HOST_WEBAPI +  "Persona/ListarResultadosVisita?idUsuario=%s&tipoContacto=%s";
    public static final String GET_OFERTAS_CLIENTE = HOST_WEBAPI + "Persona/SeleccionarOfertasClienteBDVentas?Tipo=%s&IdCliente=%s&TipoP=%s";
    public static final String GET_VISITAS = HOST_WEBAPI + "Persona/ListaClientesPendientesRegistrarVisita?IdUsuario=%s";
    public static final String POST_ACTUALIZAR_CLIENTE = HOST_WEBAPI + "Persona/UpdDatosClientesBDVentas";

    //region Agenda Comercial - Referidos FJCG
    public static final String GET_DEPARTAMENTOS_BDVENTAS = HOST_WEBAPI + "Persona/SeleccionarDepartamentosBDVentas";
    public static final String GET_PROVINCIAS_BDVENTAS = HOST_WEBAPI + "Persona/SeleccionarProvinciasBDVentas?codUbigeo=%s";
    public static final String GET_DISTRITOS_BDVENTAS = HOST_WEBAPI + "Persona/SeleccionarDistritosBDVentas?codUbigeo=%s";
    public static final String GET_ALL_AGENCIAS_BI = HOST_WEBAPI +  "Persona/SeleccionarAgenciasBDVentas";
    public static final String GET_BUSQUEDA_PERSONA_RENIEC = HOST_WEBAPI + "Persona/BusqPersonaReniec?cNumDoc=%s";
    public static final String GET_VALIDAR_CLIENTE_REGISTRO_RESULTADO = HOST_WEBAPI  + "Persona/ValidarClienteRegistroResultadosBDVentas?idUsuario=%s&idCliente=%s";
    public static final String POST_REFERIDOS = HOST_WEBAPI +  "Persona/InsertarReferidos";
    public static final String GET_VALIDAR_CLIENTE_REFERIDOS = HOST_WEBAPI +  "Persona/ValidarClienteReferidoBDVentas?cNumeroDocumento=%s";


      // region ExpedienteCredito
      public static final String GET_INFORMACION_CLIENTE = HOST_WEBAPI + "Digitalizacion/FiltroPersonaMovil?DOIPersona=%s";
      public static final String GET_LISTADO_CREDITOS = HOST_WEBAPI + "Digitalizacion/ObtenerExpedienteCredito?CodigoPersona=%s&cUser=%s";
      public static final String GET_LISTADO_CREDITOS_SIN_FICTICIO = HOST_WEBAPI + "Digitalizacion/ObtenerExpedienteCreditoSinFicticio?CodigoPersona=%s&cUser=%s";
      public static final String GET_LISTADO_EXPEDIENTES = HOST_WEBAPI + "Digitalizacion/ObtenerHistorialExpediente?cCtaCod=%s&nIdCar=%s&codCliente=%s";
      public static final String GET_LISTADO_TIPOS_EXPEDIENTES = HOST_WEBAPI + "Digitalizacion/ListarTipos?nIdCar=%s";
      public static final String SAVE_EXPEDIENTE = HOST_WEBAPI + "Digitalizacion/GuardarExpediente";
      public static final String UPDATE_EXPEDIENTE = HOST_WEBAPI + "Digitalizacion/UpdateExpediente";
      public static final String DELETE_EXPEDIENTE = HOST_WEBAPI + "Digitalizacion/DeleteExpediente?nIdObj=%s&Usuario=%s";
      public static final String CREATE_CREDIT_FICTIONAL = HOST_WEBAPI + "Digitalizacion/Crearcredfic?Operacion=1&cPersCod=%s";
      // endregion


}

