package pe.com.cmacica.flujocredito.ViewModel.Cobranza;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import pe.com.cmacica.flujocredito.AgenteServicio.RESTService;
import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.Cobranza.DetalleGestionModel;
import pe.com.cmacica.flujocredito.Model.Cobranza.EstrategiaGestionModel;
import pe.com.cmacica.flujocredito.Model.Cobranza.MotivoNoPagoModel;
import pe.com.cmacica.flujocredito.Model.Cobranza.RegistroGestionModel;
import pe.com.cmacica.flujocredito.Model.Cobranza.ResultadoModel;
import pe.com.cmacica.flujocredito.Model.Cobranza.TelefonoModel;
import pe.com.cmacica.flujocredito.Model.Cobranza.TipoContactoModel;
import pe.com.cmacica.flujocredito.Model.Cobranza.TipoGestionModel;
import pe.com.cmacica.flujocredito.Model.General.ConstanteSistemaModel;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.Dialogos.DateDialog;
import pe.com.cmacica.flujocredito.Utilitarios.UGeneral;
import pe.com.cmacica.flujocredito.Utilitarios.UPreferencias;



public class ActividadGestionCobranza extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = ActividadGestionCobranza.class.getSimpleName();

    //REGION PROPIEDADES
    private TipoContactoModel TipoContactoSel;
    private TipoGestionModel TipoGestionSel;
    private DetalleGestionModel DetalleGestionSel;
    private ResultadoModel ResultadoSel;
    private ResultadoModel ResultadoGestionSel;
    private MotivoNoPagoModel MotivoNoPagoSel;
    private EstrategiaGestionModel EstrategiaGestionSel;
	private ConstanteSistemaModel Cargos;

    //REGION CONTROLES
    private Spinner spn_NroCredito, spn_TipoContacto, spn_Telefono, spn_TipoGestion, spn_Resultado, spn_MtvnoPago,
            spn_ResultadoGestion, spn_EstrategiaGestion;
    private EditText txtTipoCredito, txtValorDeuda, txtDiasAtraso, txtProducto, txtMonto, txtEstado, txtObservacion, txtActividadActualEdit;
    private TextView lblFecha,txtResultaGestion, txtResultadoGestion, txtTipoGestion, txtTipoContacto, txtActividadActual, txtEstrategiaGestion;
	private ActividadGestionCobranza vm = this;
    private Gson gson = new Gson();
    private ProgressDialog progressDialog;
    private FloatingActionButton fab_guardar;
    private static String CodCliente;
    private static String Documento;

    //REGION VARIABLES
    short Parametro;

    RegistroGestionModel Reg = new RegistroGestionModel();

    public static void createInstance(Activity activity, String pCodigoCliente, String pDocumento) {
        CodCliente = pCodigoCliente;
        Documento = pDocumento;
        Intent intent = getLaunchIntent(activity);
        activity.startActivity(intent);
    }

    public static Intent getLaunchIntent(Context context) {
        Intent intent = new Intent(context, ActividadGestionCobranza.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_gestion_cobranza);
        showToolbar(getResources().getString(R.string.RegistroGestion), true);
        spn_TipoContacto = (Spinner) findViewById(R.id.spn_TipoContacto);
        spn_Telefono = (Spinner) findViewById(R.id.spn_Telefono);
        spn_TipoGestion = (Spinner) findViewById(R.id.spn_TipoGestion);
        spn_Resultado = (Spinner) findViewById(R.id.spn_Resultado);
        spn_MtvnoPago = (Spinner) findViewById(R.id.spn_MtvnoPago);
        spn_ResultadoGestion = (Spinner) findViewById(R.id.spn_ResultadoGestion);
        spn_EstrategiaGestion = (Spinner) findViewById(R.id.spn_EstrategiaGestion);
        spn_NroCredito = (Spinner) findViewById(R.id.spn_NroCredito);
        txtResultaGestion=(TextView) findViewById(R.id.txtResultaGestion);


        txtTipoCredito = (EditText) findViewById(R.id.txtTipoCredito);
        txtValorDeuda = (EditText) findViewById(R.id.txtValorDeuda);
        txtDiasAtraso = (EditText) findViewById(R.id.txtDiasAtraso);
        txtProducto = (EditText) findViewById(R.id.txtProducto);
        txtEstado = (EditText) findViewById(R.id.txtEstado);
        txtObservacion = (EditText) findViewById(R.id.txtObservacion);

		txtActividadActualEdit = (EditText) findViewById(R.id.txtActividadActualEdit);
        txtActividadActual = (TextView) findViewById(R.id.txtActividadActualCliente);
        txtResultadoGestion = (TextView) findViewById(R.id.txtResultadoGestion);
        txtTipoContacto = (TextView) findViewById(R.id.txtTipoContacto);
        txtTipoGestion = (TextView) findViewById(R.id.txtTipoGestion);
        txtEstrategiaGestion = (TextView) findViewById(R.id.txtEstrategiaGestion);

        txtMonto = (EditText) findViewById(R.id.txtMonto);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        fab_guardar=(FloatingActionButton) findViewById(R.id.fab_guardar);
        lblFecha.setText(UGeneral.obtenerTiempoCorto());

        lblFecha.setEnabled(false);
        txtMonto.setEnabled(false);
        txtTipoCredito.setFocusable(false);
        txtProducto.setFocusable(false);
        txtEstado.setFocusable(false);
        txtValorDeuda.setFocusable(false);
        txtDiasAtraso.setFocusable(false);

        txtResultaGestion.setVisibility(View.GONE);
        spn_ResultadoGestion.setVisibility(View.GONE);

		OnCargarConstanteSistema(1300);

        lblFecha.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager Frag = getSupportFragmentManager();
                        DialogFragment picker = new DateDialog();
                        picker.show(Frag, "DatePicker");
                    }
                }
        );

        OnValidaGestor(UPreferencias.ObtenerUserLogeo(this));

        spn_TipoContacto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TipoContactoSel = (TipoContactoModel) parent.getItemAtPosition(position);
                OnCargarResultado();
                OnCargarResultadoMK();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spn_TipoGestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TipoGestionSel = (TipoGestionModel) parent.getItemAtPosition(position);
                OnCargarResultado();
                OnCargarResultadoMK();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_NroCredito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DetalleGestionSel = (DetalleGestionModel) parent.getItemAtPosition(position);
                txtTipoCredito.setText(DetalleGestionSel.getTipoCredito());
                txtProducto.setText(DetalleGestionSel.getProducto());
                txtEstado.setText(DetalleGestionSel.getEstadoDetalleGestion());
                txtValorDeuda.setText(String.valueOf(DetalleGestionSel.getValorCuota()));
                txtDiasAtraso.setText(String.valueOf(DetalleGestionSel.getDiasAtraso()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_Resultado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ResultadoSel = (ResultadoModel) parent.getItemAtPosition(position);
                if (ResultadoSel.getResDescripcion().toLowerCase().contains("compromiso")) {
                    lblFecha.setEnabled(true);
                    txtMonto.setEnabled(true);
                    Reg.datoParametro = "C";

                } else {
                    lblFecha.setEnabled(false);
                    txtMonto.setEnabled(false);
                    txtMonto.setText("0.0");
                    Reg.datoParametro = "S";
                    Reg.dFecCompromiso = "";
                }
                OnCargarMotivoNoPago(ResultadoSel.getResCod());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
       /* spn_ResultadoGestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ResultadoGestionSel = (ResultadoModel) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        spn_MtvnoPago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MotivoNoPagoSel = (MotivoNoPagoModel) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_EstrategiaGestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EstrategiaGestionSel = (EstrategiaGestionModel) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fab_guardar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

						if(Cargos.getCodigoConstSisValor().contains(UPreferencias.ObtenerCargoPersona(vm))){
                            OnGuardarGestion();
                        }else {

							double Monto;
							Monto =Double.parseDouble(txtMonto.getText().toString());
							if(ResultadoSel.getResDescripcion().toLowerCase().contains("compromiso"))
							{
								if (Monto==0){

									Snackbar.make(findViewById(R.id.actividadGestionCobranza),
											"Ingrese Monto",
											Snackbar.LENGTH_LONG).show();
									return;
								}

								SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");

								Date FechaSeleccionada ;
								Date FechaActual ;
								Boolean TodoOk=false;
								try {
									FechaSeleccionada = sdf.parse(lblFecha.getText().toString());
									FechaActual=sdf.parse(UGeneral.obtenerTiempoCorto());
									if (FechaSeleccionada.before(FechaActual))
									{
										TodoOk=false;
									}
									else
									{
										if(FechaSeleccionada.after(FechaActual))
										{
											TodoOk=true;
										}
										else
										{
											//cuando es la misma fecha
											TodoOk=true;
										}

									}
								} catch (ParseException e) {
									e.printStackTrace();
								}
								if (TodoOk==false)
								{
									Snackbar.make(findViewById(R.id.actividadGestionCobranza),
											"Fecha de promesa debe ser mayor que la fecha actual",
											Snackbar.LENGTH_LONG).show();
									return;
								}
								else
								{
									OnGuardarGestion();
								}

							}
							else
							{
								OnGuardarGestion();
							}
						}
                    }
                }
        );
    }

    private void showToolbar(String tittle, boolean upButton) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }

    private void OnValidaGestor(String Usuario) {
        String url = String.format(SrvCmacIca.GET_VALIDA_GESTOR, Usuario);
        VolleySingleton.
                getInstance(this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Procesar la respuesta Json
                                        ProcesarGestor(response);
                                    }
                                },
                                new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.cancel();
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    private void ProcesarGestor(JSONObject response) {
        try {

            if (response.getBoolean("Data")) {

                Parametro = 2;
                OnCargarTipoContacto(Parametro);
                OnCargarPersonaTelefono();
                OnCargarEstGestion();
                OnCargarTipoGestion(Parametro);
                OnCargarDetalleGestion();

            } else {
                Parametro = 1;
                OnCargarTipoContacto(Parametro);
                OnCargarPersonaTelefono();
                OnCargarEstGestion();
                OnCargarTipoGestion(Parametro);
                OnCargarDetalleGestion();
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void OnCargarTipoContacto(int Parametro) {
        String url = String.format(SrvCmacIca.GET_TIPO_CONTACTO, Parametro);
        VolleySingleton.
                getInstance(this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Procesar la respuesta Json
                                        ProcesarTipoContacto(response);
                                    }
                                },
                                new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.cancel();
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    private void ProcesarTipoContacto(JSONObject response) {
        try {
            if (response.getBoolean("IsCorrect")) {

                JSONArray ListaTipoContacto = response.getJSONArray("Data");
                TipoContactoModel[] ArrayTipoContacto = gson.fromJson(ListaTipoContacto.toString(), TipoContactoModel[].class);

                ArrayAdapter<TipoContactoModel> adpSpinnerTipoContacto = new ArrayAdapter<TipoContactoModel>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Arrays.asList(ArrayTipoContacto)
                );
                adpSpinnerTipoContacto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spn_TipoContacto.setAdapter(adpSpinnerTipoContacto);
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Aviso")
                        .setMessage(response.getString("Message"))
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface arg0) {
                                //ActividadLogin.this.finish();
                            }
                        })
                        //.setNegativeButton(android.R.string.cancel, null)//sin listener
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Salir
                                //ActividadLogin.this.finish();
                                //txtPassword.setText("");
                            }
                        })
                        .show();
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }
    }

    private void OnCargarPersonaTelefono() {

        String url = String.format(SrvCmacIca.GET_PERSONA_TELEFONO, CodCliente);
        VolleySingleton.
                getInstance(this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Procesar la respuesta Json
                                        ProcesarPersonaTelefono(response);
                                    }
                                },
                                new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.cancel();
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    private void ProcesarPersonaTelefono(JSONObject response) {
        try {
            if (response.getBoolean("IsCorrect")) {

                JSONArray ListaPersonaTelefono = response.getJSONArray("Data");
                TelefonoModel[] ArrayPersonaTelefono = gson.fromJson(ListaPersonaTelefono.toString(), TelefonoModel[].class);

                ArrayAdapter<TelefonoModel> adpSpinnerPersonaTelefono = new ArrayAdapter<TelefonoModel>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Arrays.asList(ArrayPersonaTelefono)
                );

                adpSpinnerPersonaTelefono.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spn_Telefono.setAdapter(adpSpinnerPersonaTelefono);
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Aviso")
                        .setMessage(response.getString("Message"))
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface arg0) {
                                //ActividadLogin.this.finish();
                            }
                        })
                        //.setNegativeButton(android.R.string.cancel, null)//sin listener
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Salir
                                //ActividadLogin.this.finish();
                                //txtPassword.setText("");
                            }
                        })
                        .show();
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void OnCargarMotivoNoPago(int nIdResultado) {

        String url = String.format(SrvCmacIca.GET_MOTIVONOPAGO, nIdResultado);
        VolleySingleton.
                getInstance(this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Procesar la respuesta Json
                                        ProcesarMotivoNoPago(response);
                                    }
                                },
                                new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.cancel();
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    private void ProcesarMotivoNoPago(JSONObject response) {

        try {
            if (response.getBoolean("IsCorrect")) {

                JSONArray ListaMotivoNoPago = response.getJSONArray("Data");
                MotivoNoPagoModel[] ArrayPersonaTelefono = gson.fromJson(ListaMotivoNoPago.toString(), MotivoNoPagoModel[].class);

                ArrayAdapter<MotivoNoPagoModel> adpSpinnerMotivoNoPago = new ArrayAdapter<MotivoNoPagoModel>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Arrays.asList(ArrayPersonaTelefono)
                );
                adpSpinnerMotivoNoPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spn_MtvnoPago.setAdapter(adpSpinnerMotivoNoPago);
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Aviso")
                        .setMessage(response.getString("Message"))
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface arg0) {
                                //ActividadLogin.this.finish();
                            }
                        })
                        //.setNegativeButton(android.R.string.cancel, null)//sin listener
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Salir
                                //ActividadLogin.this.finish();
                                //txtPassword.setText("");
                            }
                        })
                        .show();
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void OnCargarEstGestion() {

        String url = String.format(SrvCmacIca.GET_ESTGESTION);
        VolleySingleton.
                getInstance(this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Procesar la respuesta Json
                                        ProcesarEstGestion(response);
                                    }
                                },
                                new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.cancel();
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

	private void OnCargarConstanteSistema(int nConsSisCod) {

        String url = String.format(SrvCmacIca.GET_OBTENER_CONSTANTE_SISTEMA, nConsSisCod);
        VolleySingleton.
                getInstance(this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Procesar la respuesta Json
                                        ProcesarConstanteSistema(response);
                                        OcultarVistas();
                                    }
                                },
                                new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.cancel();
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    private void ProcesarConstanteSistema(JSONObject response) {
        try {
            if (response.getBoolean("IsCorrect")) {

                JSONObject ConstSis = response.getJSONObject("Data");
                ConstanteSistemaModel ObjectConstanteSistema = gson.fromJson(ConstSis.toString(), ConstanteSistemaModel.class);

                Cargos = ObjectConstanteSistema;
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Aviso")
                        .setMessage(response.getString("Message"))
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface arg0) {
                                //ActividadLogin.this.finish();
                            }
                        })
                        //.setNegativeButton(android.R.string.cancel, null)//sin listener
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Salir
                                //ActividadLogin.this.finish();
                                //txtPassword.setText("");
                            }
                        })
                        .show();
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ProcesarEstGestion(JSONObject response) {
        try {
            if (response.getBoolean("IsCorrect")) {

                JSONArray ListaEstGestion = response.getJSONArray("Data");
                EstrategiaGestionModel[] ArrayPersonaTelefono = gson.fromJson(ListaEstGestion.toString(), EstrategiaGestionModel[].class);

                ArrayAdapter<EstrategiaGestionModel> adpSpinnerEstGestion = new ArrayAdapter<EstrategiaGestionModel>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Arrays.asList(ArrayPersonaTelefono)
                );
                adpSpinnerEstGestion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spn_EstrategiaGestion.setAdapter(adpSpinnerEstGestion);
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Aviso")
                        .setMessage(response.getString("Message"))
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface arg0) {
                                //ActividadLogin.this.finish();
                            }
                        })
                        //.setNegativeButton(android.R.string.cancel, null)//sin listener
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Salir
                                //ActividadLogin.this.finish();
                                //txtPassword.setText("");
                            }
                        })
                        .show();
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void OnCargarTipoGestion(int Parametro) {
        String url = String.format(SrvCmacIca.GET_TIPO_GESTION, Parametro);
        VolleySingleton.
                getInstance(this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Procesar la respuesta Json
                                        ProcesarTipoGestion(response);
                                    }
                                },
                                new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.cancel();
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    private void ProcesarTipoGestion(JSONObject response) {
        try {
            if (response.getBoolean("IsCorrect")) {

                JSONArray ListaTipoGestion = response.getJSONArray("Data");
                TipoGestionModel[] ArrayTipoGestion = gson.fromJson(ListaTipoGestion.toString(), TipoGestionModel[].class);

                ArrayAdapter<TipoGestionModel> adpSpinnerTipoGestion = new ArrayAdapter<TipoGestionModel>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Arrays.asList(ArrayTipoGestion)
                );
                adpSpinnerTipoGestion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spn_TipoGestion.setAdapter(adpSpinnerTipoGestion);
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Aviso")
                        .setMessage(response.getString("Message"))
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface arg0) {
                                //ActividadLogin.this.finish();
                            }
                        })
                        //.setNegativeButton(android.R.string.cancel, null)//sin listener
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Salir
                                //ActividadLogin.this.finish();
                                //txtPassword.setText("");
                            }
                        })
                        .show();
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void OnCargarResultado() {
        try {

            //progressDialog = ProgressDialog.show(getContext(),"Espere por favor","Generando calendario.");
            if (TipoContactoSel == null || TipoGestionSel == null) {
                return;
            }

            String Url = String.format(SrvCmacIca.GET_RESULTADO, TipoContactoSel.getTipoContacto(), TipoGestionSel.getCodigo());
            VolleySingleton.
                    getInstance(this).
                    addToRequestQueue(
                            new JsonObjectRequest(
                                    Request.Method.GET,
                                    Url,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            // Procesar la respuesta Json
                                            ProcesarResultado(response);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d(TAG, "Error Volley: " + error.toString());
                                            // progressDialog.cancel();
                                        }
                                    }
                            )
                    );
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            Toast.makeText(
                    this,
                    ex.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ProcesarResultado(JSONObject response) {
        try {
            if (response.getBoolean("IsCorrect")) {

                JSONArray ListaResultado = response.getJSONArray("Data");
                ResultadoModel[] ArrayResultado = gson.fromJson(ListaResultado.toString(), ResultadoModel[].class);

                ArrayAdapter<ResultadoModel> adpSpinnerResultado = new ArrayAdapter<ResultadoModel>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Arrays.asList(ArrayResultado)
                );
                adpSpinnerResultado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spn_Resultado.setAdapter(adpSpinnerResultado);
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Aviso")
                        .setMessage(response.getString("Message"))
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface arg0) {
                                //ActividadLogin.this.finish();
                            }
                        })
                        //.setNegativeButton(android.R.string.cancel, null)//sin listener
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Salir
                                //ActividadLogin.this.finish();
                                //txtPassword.setText("");
                            }
                        })
                        .show();
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void OnCargarResultadoMK() {
        try {

            //progressDialog = ProgressDialog.show(getContext(),"Espere por favor","Generando calendario.");
            if (TipoContactoSel == null || TipoGestionSel == null) {
                return;
            }
            String Url = String.format(SrvCmacIca.GET_RESULTADOMK, TipoContactoSel.getTipoContacto(), TipoGestionSel.getCodigo());
            VolleySingleton.
                    getInstance(this).
                    addToRequestQueue(
                            new JsonObjectRequest(
                                    Request.Method.GET,
                                    Url,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            // Procesar la respuesta Json
                                            ProcesarResultadoMK(response);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d(TAG, "Error Volley: " + error.toString());
                                            // progressDialog.cancel();
                                        }
                                    }
                            )
                    );
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            Toast.makeText(
                    this,
                    ex.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ProcesarResultadoMK(JSONObject response) {
        try {
            if (response.getBoolean("IsCorrect")) {

                JSONArray ListaResultado = response.getJSONArray("Data");
                ResultadoModel[] ArrayResultadoMK = gson.fromJson(ListaResultado.toString(), ResultadoModel[].class);

                ArrayAdapter<ResultadoModel> adpSpinnerResultadoMK = new ArrayAdapter<ResultadoModel>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Arrays.asList(ArrayResultadoMK)
                );
                adpSpinnerResultadoMK.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spn_ResultadoGestion.setAdapter(adpSpinnerResultadoMK);
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Aviso")
                        .setMessage(response.getString("Message"))
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface arg0) {
                                //ActividadLogin.this.finish();
                            }
                        })
                        //.setNegativeButton(android.R.string.cancel, null)//sin listener
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Salir
                                //ActividadLogin.this.finish();
                                //txtPassword.setText("");
                            }
                        })
                        .show();
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void OnCargarDetalleGestion() {

        String url = String.format(SrvCmacIca.GET_DETALLE_GESTION, Documento);
        VolleySingleton.
                getInstance(this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Procesar la respuesta Json
                                        ProcesarDetalleGestion(response);
                                    }
                                },
                                new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.cancel();
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    private void ProcesarDetalleGestion(JSONObject response) {

        try {
            if (response.getBoolean("IsCorrect")) {

                JSONArray ListaDetalleGestion = response.getJSONArray("Data");
                DetalleGestionModel[] ArrayResultado = gson.fromJson(ListaDetalleGestion.toString(), DetalleGestionModel[].class);


                if (Arrays.asList(ArrayResultado).size()==0)
                {
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Aviso")
                            .setMessage("El cliente no tiene crédito(s) con 1 a 30 días de atraso")
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface arg0) {
                                    //ActividadLogin.this.finish();
                                }
                            })
                            //.setNegativeButton(android.R.string.cancel, null)//sin listener
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    onBackPressed();
                                }
                            })
                            .show();
                }
                else
                {
                    ArrayAdapter<DetalleGestionModel> adpSpinnerDetalleGestion = new ArrayAdapter<DetalleGestionModel>(
                            this,
                            android.R.layout.simple_spinner_item,
                            Arrays.asList(ArrayResultado)
                    );
                    adpSpinnerDetalleGestion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spn_NroCredito.setAdapter(adpSpinnerDetalleGestion);
                }


            } else {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Aviso")
                        .setMessage(response.getString("Message"))
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface arg0) {
                                //ActividadLogin.this.finish();
                            }
                        })
                        //.setNegativeButton(android.R.string.cancel, null)//sin listener
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Salir
                                //ActividadLogin.this.finish();
                                //txtPassword.setText("");
                            }
                        })
                        .show();
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void OnGuardarGestion() {

        progressDialog = ProgressDialog.show(this,"Espere por favor","Guardando Datos");
        fab_guardar.setEnabled(false);
        Gson gsonpojo = new GsonBuilder().create();
        Reg.CodigoCuenta = DetalleGestionSel.getNroCredito();
        Reg.nIdBase = DetalleGestionSel.getnIdBase();
        Reg.CodigoPersona=CodCliente;
        Reg.nMontoCuota = DetalleGestionSel.getValorCuota();
        Reg.DiasAtraso = DetalleGestionSel.getDiasAtraso();
        Reg.nMontoPactado = txtMonto.getText().toString().equals("") ? 0.00 : Double.parseDouble(txtMonto.getText().toString());
        Reg.dFecCompromiso = lblFecha.getText().toString();
        Reg.Usuario = UPreferencias.ObtenerUserLogeo(this);
        Reg.CodigoAnalista = DetalleGestionSel.getAnalista();
        Reg.nIdResultado = ResultadoSel == null ? 1 : ResultadoSel.getResCod();
        Reg.Observacion = txtObservacion.getText().toString();
    /*    Reg.nIdResultadoMK = ResultadoGestionSel.getResCod();*/
        Reg.nIdMotivonoPago = MotivoNoPagoSel.getMotnpCod();
        Reg.nIdestGestion = EstrategiaGestionSel == null ? 0 : EstrategiaGestionSel.getEstGesionCod();
        Reg.nIdTipoGestion = TipoGestionSel == null ? 1 : TipoGestionSel.getCodigo();
		Reg.ActividadActualCliente = txtActividadActualEdit.getText().toString();
        Reg.datoParametro = Reg.datoParametro == null ? "C": Reg.datoParametro;

        String json = gsonpojo.toJson(Reg);
        HashMap<String, String> cabeceras = new HashMap<>();

        new RESTService(this).post(SrvCmacIca.POST_REGISTRO_GESTION, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.cancel();
                        ProcesarGuardar(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.toString());
                        progressDialog.cancel();
                        // errorservice(error);
                    }
                }
                , cabeceras);
    }
    private void ProcesarGuardar(JSONObject response){

       try {if (response.getBoolean("IsCorrect")) {

           new AlertDialog.Builder(this)
                   .setIcon(android.R.drawable.ic_dialog_alert)
                   .setTitle("Aviso")
                   .setMessage("Se Guardó Correctamente los Datos")
                   .setOnDismissListener(new DialogInterface.OnDismissListener() {
                       @Override
                       public void onDismiss(DialogInterface arg0) {
                           //ActividadLogin.this.finish();
                       }})

                   .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                       @Override
                       public void onClick(DialogInterface dialog, int which){
                           finish();
                       }
                   })
                   .show();

            }
        }
       catch (JSONException e) {
           Log.d(TAG, e.getMessage());
           Toast.makeText(
                   this,
                   e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

	private void OcultarVistas(){
        if (Cargos.getCodigoConstSisValor().contains(UPreferencias.ObtenerCargoPersona(this))){
            OnCargarMotivoNoPago(0);
            txtTipoContacto.setVisibility(View.GONE);
            spn_TipoContacto.setVisibility(View.GONE);
            txtTipoGestion.setVisibility(View.GONE);
            spn_TipoGestion.setVisibility(View.GONE);
            txtResultadoGestion.setVisibility(View.GONE);
            spn_Resultado.setVisibility(View.GONE);
            spn_ResultadoGestion.setVisibility(View.GONE);
            txtActividadActual.setVisibility(View.VISIBLE);
            txtActividadActualEdit.setVisibility(View.VISIBLE);
            txtMonto.setVisibility(View.GONE);
            txtEstrategiaGestion.setVisibility(View.GONE);
            spn_EstrategiaGestion.setVisibility(View.GONE);
            txtObservacion.setVisibility(View.GONE);
            lblFecha.setEnabled(true);
        }else{
            txtTipoContacto.setVisibility(View.VISIBLE);
            spn_TipoContacto.setVisibility(View.VISIBLE);
            txtTipoGestion.setVisibility(View.VISIBLE);
            spn_TipoGestion.setVisibility(View.VISIBLE);
            txtResultadoGestion.setVisibility(View.VISIBLE);
            spn_Resultado.setVisibility(View.VISIBLE);
            spn_ResultadoGestion.setVisibility(View.VISIBLE);
            spn_MtvnoPago.setVisibility(View.VISIBLE);
            txtActividadActual.setVisibility(View.GONE);
            txtActividadActualEdit.setVisibility(View.GONE);
            txtMonto.setVisibility(View.VISIBLE);
            txtEstrategiaGestion.setVisibility(View.VISIBLE);
            spn_EstrategiaGestion.setVisibility(View.VISIBLE);
            txtObservacion.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        actualizarFecha(year, month, dayOfMonth);
    }

    public void actualizarFecha(int ano, int mes, int dia) {
        // Setear en el textview la fecha
        mes += 1;
        String cdia, cmes;
        if (dia < 10) {
            cdia = "0" + String.valueOf(dia);
        } else {
            cdia = String.valueOf(dia);
        }
        if (mes < 10) {
            cmes = "0" + String.valueOf(mes);
        } else {
            cmes = String.valueOf(mes);
        }

        lblFecha.setText(ano + "-" + cmes + "-" + cdia);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.Guardar:


                break;
            case android.R.id.home:
                // app icon in action bar clicked; go home

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Aviso")
                        .setMessage("Desea Regresar a la Ventana Anterior?")
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface arg0) {
                                // ActividadLogin.this.finish();
                            }})
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })//sin listener
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                //Salir

                                onBackPressed();
                            }
                        })
                        .show();
                break;
            default:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

      //  getMenuInflater().inflate(R.menu.menu_guardar, menu);

        // Verificación de visibilidad acción eliminar
        // if (uriContacto != null) {

        //}

        return true;
    }

}
