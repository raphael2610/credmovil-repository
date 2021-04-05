package pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pe.com.cmacica.flujocredito.Model.AgendaComercial.Agencia;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteReferido;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteVisita;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.DivisionTerriorial;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.Oferta;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.OfertaCliente;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.Producto;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.Resultado;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.ResultadoVisita;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.TipoContacto;

public class AgendaComercialDbHelper extends SQLiteOpenHelper {


    private static final String TAG = "AgendaComercialDbHelper";

    public static final int FLAG_NUEVO = 1;
    public static final int FLAG_ACTUALIZADO = 2;

    public static final int TYPE_RESULTADO = 1;
    public static final int TYPE_PROGRAMAR = 2;

    public static final int CODIGO_VENTA_CERRADA = 1;
    public static final int CODIGO_INTERESADO = 4;

    private static final String DATABASE_NAME = "AgendaComercial.db";
    private static final int DATABASE_VERSION = 1;

    public static final int FLAG_SYNCRONIZED = 1;
    public static final int FLAG_NOT_SYNCRONIZED = 2;

    public static final int FLAG_SIN_RESULTADO = 1;
    public static final int FLAG_CON_RESULTADO = 2;

    public static final int FLAG_INSERTAR_REFERIDO_EXITOSO = 1;
    public static final int FLAG_INSERTAR_REFERIDO_ERROR = 2;
    public static final int FLAG_INSERTAR_REFERIDO_ERROR_EXISTENTE = 3;


    private static final String SQL_CREATE_VISITS_ENTRY =
            "CREATE TABLE " + AgendaComercialContract.VisitsEntry.TABLE_NAME + " ( " +
                    AgendaComercialContract.VisitsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AgendaComercialContract.VisitsEntry.COLUMN_ID_AGENCY + " INTEGER," +
                    AgendaComercialContract.VisitsEntry.COLUMN_DESC_AGENCY + " TEXT," +
                    AgendaComercialContract.VisitsEntry.COLUMN_ID_CUSTOMER + " INTEGER," +
                    AgendaComercialContract.VisitsEntry.COLUMN_NAME + " TEXT," +
                    AgendaComercialContract.VisitsEntry.COLUMN_AGE + " INTEGER," +
                    AgendaComercialContract.VisitsEntry.COLUMN_DNI + " TEXT," +
                    AgendaComercialContract.VisitsEntry.COLUMN_PHONE + " TEXT," +
                    AgendaComercialContract.VisitsEntry.COLUMN_ADDRESS + " TEXT," +
                    AgendaComercialContract.VisitsEntry.COLUMN_DATE_VISIT + " TEXT," +
                    AgendaComercialContract.VisitsEntry.COLUMN_ID_USER + " INTEGER, " +
                    AgendaComercialContract.VisitsEntry.COLUMN_FLAG + " NUMERIC, " +
                    AgendaComercialContract.VisitsEntry.COLUMN_RESULT + " INTEGER, " +
                    AgendaComercialContract.VisitsEntry.COLUMN_SYNCHRONIZATION + " INTEGER " + ")";

    private static final String SQL_DELETE_VISITS_ENTRY =
            "DROP TABLE IF EXISTS " + AgendaComercialContract.VisitsEntry.TABLE_NAME;



    private static final String SQL_CREATE_PRODUCT_ENTRY =
            "CREATE TABLE " + AgendaComercialContract.ProductEntry.TABLE_NAME + " ( " +
                    AgendaComercialContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AgendaComercialContract.ProductEntry.COLUMN_CODE + " INTEGER," +
                    AgendaComercialContract.ProductEntry.COLUMN_VALUE + " INTEGER," +
                    AgendaComercialContract.ProductEntry.COLUMN_DESCRIPTION + " TEXT )";
    private static final String SQL_DELETE_PRODUCT_ENTRY =
            "DROP TABLE IF EXISTS " + AgendaComercialContract.ProductEntry.TABLE_NAME;



    private static final String SQL_CREATE_CONTACT_TYPE_ENTRY =
            "CREATE TABLE " + AgendaComercialContract.ContactTypeEntry.TABLE_NAME + " ( " +
                    AgendaComercialContract.ContactTypeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AgendaComercialContract.ContactTypeEntry.COLUMN_CODE + " INTEGER," +
                    AgendaComercialContract.ContactTypeEntry.COLUMN_VALUE + " INTEGER," +
                    AgendaComercialContract.ContactTypeEntry.COLUMN_DESCRIPTION + " TEXT )";
    private static final String SQL_DELETE_CONTACT_TYPE_ENTRY =
            "DROP TABLE IF EXISTS " + AgendaComercialContract.ContactTypeEntry.TABLE_NAME;



    private static final String SQL_CREATE_VISIT_RESULTS_ENTRY =
            "CREATE TABLE " + AgendaComercialContract.VisitResultsEntry.TABLE_NAME + " ( " +
                    AgendaComercialContract.VisitResultsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AgendaComercialContract.VisitResultsEntry.COLUMN_CODE + " INTEGER," +
                    AgendaComercialContract.VisitResultsEntry.COLUMN_VALUE + " INTEGER," +
                    AgendaComercialContract.VisitResultsEntry.COLUMN_DESCRIPTION + " TEXT, " +
                    AgendaComercialContract.VisitResultsEntry.COLUMN_TYPE_CONTACT + " INTEGER )";
    private static final String SQL_DELETE_VISIT_RESULTS_ENTRY =
            "DROP TABLE IF EXISTS " + AgendaComercialContract.VisitResultsEntry.TABLE_NAME;



    private static final String SQL_CREATE_OFFERS_CLIENT_ENTRY =
            "CREATE TABLE " + AgendaComercialContract.OfferClientEntry.TABLE_NAME + " ( " +
                    AgendaComercialContract.OfferClientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AgendaComercialContract.OfferClientEntry.COLUMN_ID_USER + " INTEGER," +
                    AgendaComercialContract.OfferClientEntry.COLUMN_ID_CLIENT + " INTEGER," +
                    AgendaComercialContract.OfferClientEntry.COLUMN_DOC + " TEXT," +
                    AgendaComercialContract.OfferClientEntry.COLUMN_ID_OFFER + " INTEGER," +
                    AgendaComercialContract.OfferClientEntry.COLUMN_OFFER + " TEXT," +
                    AgendaComercialContract.OfferClientEntry.COLUMN_AMOUNT_OFFER_CC + " NUMERIC," +
                    AgendaComercialContract.OfferClientEntry.COLUMN_AMOUNT_OFFER_SC+ " NUMERIC )";
    private static final String SQL_DELETE_OFFERS_CLIENT_ENTRY =
            "DROP TABLE IF EXISTS " + AgendaComercialContract.OfferClientEntry.TABLE_NAME;


    private static final String SQL_CREATE_OFFERS_ENTRY =
            "CREATE TABLE " + AgendaComercialContract.OffersEntry.TABLE_NAME + " ( " +
                    AgendaComercialContract.OffersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AgendaComercialContract.OffersEntry.COLUMN_ID_CLIENT + " INTEGER," +
                    AgendaComercialContract.OffersEntry.COLUMN_DNI + " TEXT," +
                    AgendaComercialContract.OffersEntry.COLUMN_NAME + " TEXT," +
                    AgendaComercialContract.OffersEntry.COLUMN_DESC_OFFER + " TEXT," +
                    AgendaComercialContract.OffersEntry.COLUMN_OFFER_AMOUNT_CC + " NUMERIC," +
                    AgendaComercialContract.OffersEntry.COLUMN_OFFER_AMOUNT_SC+ " NUMERIC )";
    private static final String SQL_DELETE_OFFERS_ENTRY =
            "DROP TABLE IF EXISTS " + AgendaComercialContract.OffersEntry.TABLE_NAME;


    public static final String SQL_CREATE_RESULT_ENTRY =
            "CREATE TABLE " + AgendaComercialContract.ResultEntry.TABLE_NAME + " ( " +
                    AgendaComercialContract.ResultEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AgendaComercialContract.ResultEntry.COLUMN_ID_CUSTOMER + " INTEGER, " +
                    AgendaComercialContract.ResultEntry.COLUMN_ID_OFFER + " INTEGER, " +
                    AgendaComercialContract.ResultEntry.COLUMN_ID_USER + " INTEGER, " +
                    AgendaComercialContract.ResultEntry.COLUMN_AMOUNT + "  NUMERIC, " +
                    AgendaComercialContract.ResultEntry.COLUMN_ID_RESULT + " INTEGER, " +
                    AgendaComercialContract.ResultEntry.COLUMN_ID_PRODUCT + " INTEGER, " +
                    AgendaComercialContract.ResultEntry.COLUMN_COMMENTARY + " TEXT, " +
                    AgendaComercialContract.ResultEntry.COLUMN_TYPE_CREDIT + " INTEGER, " +
                    AgendaComercialContract.ResultEntry.COLUMN_CREDIT_DESTINATION + " INTEGER, " +
                    AgendaComercialContract.ResultEntry.COLUMN_TYPE_CONTACT + " INTEGER, " +
                    AgendaComercialContract.ResultEntry.COLUMN_LATITUD + " NUMERIC, " +
                    AgendaComercialContract.ResultEntry.COLUMN_LONGITUD + " NUMERIC, " +
                    AgendaComercialContract.ResultEntry.COLUMN_VISIT + " INTEGER, " +
                    AgendaComercialContract.ResultEntry.COLUMN_TYPE + " INTEGER, " +
                    AgendaComercialContract.ResultEntry.COLUM_SYNCHRONIZATION + " INTEGER )";
    public static final String SQL_DELETE_RESULT_ENTRY =
            "DROP TABLE IF EXISTS " + AgendaComercialContract.ResultEntry.TABLE_NAME;


    public static final String SQL_CREATE_REFERREDS_ENTRY =
            "CREATE TABLE " + AgendaComercialContract.ReferredsEntry.TABLE_NAME + " ( " +
                    AgendaComercialContract.ReferredsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AgendaComercialContract.ReferredsEntry.COLUMN_DNI + " TEXT, " +
                    AgendaComercialContract.ReferredsEntry.COLUMN_NAME + " TEXT, " +
                    AgendaComercialContract.ReferredsEntry.COLUMN_ADDRESS + " TEXT, " +
                    AgendaComercialContract.ReferredsEntry.COLUMN_PHONE + " TEXT, " +
                    AgendaComercialContract.ReferredsEntry.COLUMN_DEPARTMENT + " TEXT, " +
                    AgendaComercialContract.ReferredsEntry.COLUMN_PROVINCE + " TEXT, " +
                    AgendaComercialContract.ReferredsEntry.COLUMN_DISTRICT + " TEXT, " +
                    AgendaComercialContract.ReferredsEntry.COLUMN_ID_AGENCY + "  INTEGER, " +
                    AgendaComercialContract.ReferredsEntry.COLUMN_ID_USER + " INTEGER, " +
                    AgendaComercialContract.ReferredsEntry.COLUMN_ID_PRODUCT + " INTEGER, " +
                    AgendaComercialContract.ReferredsEntry.COLUMN_RESULT_STATE + " INTEGER, " +
                    AgendaComercialContract.ReferredsEntry.COLUM_SYNCHRONIZATION + " INTEGER )";
    public static final String SQL_DELETE_REFERREDS_ENTRY =
            "DROP TABLE IF EXISTS " + AgendaComercialContract.ReferredsEntry.TABLE_NAME;


    public static final String SQL_CREATE_DEPARTAMENT_ENTRY =
            "CREATE TABLE " + AgendaComercialContract.DepartmentEntry.TABLE_NAME + " ( " +
                    AgendaComercialContract.DepartmentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_COD + " TEXT, " +
                    AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_DESC + " TEXT, " +
                    AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_CIUDAD + " TEXT, " +
                    AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_COD_RENIEC + " TEXT )";
    public static final String SQL_DELETE_DEPARTAMENT_ENTRY =
            "DROP TABLE IF EXISTS " + AgendaComercialContract.DepartmentEntry.TABLE_NAME;


    public static final String SQL_CREATE_PROVINCE_ENTRY =
            "CREATE TABLE " + AgendaComercialContract.ProvinceEntry.TABLE_NAME + " ( " +
                    AgendaComercialContract.ProvinceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_COD + " TEXT, " +
                    AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_DESC + " TEXT, " +
                    AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_CIUDAD + " TEXT, " +
                    AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_COD_RENIEC + " TEXT )";
    public static final String SQL_DELETE_PROVINCE_ENTRY =
            "DROP TABLE IF EXISTS " + AgendaComercialContract.DistrictEntry.TABLE_NAME;


    public static final String SQL_CREATE_DISTRICT_ENTRY =
            "CREATE TABLE " + AgendaComercialContract.DistrictEntry.TABLE_NAME + " ( " +
                    AgendaComercialContract.DistrictEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_COD + " TEXT, " +
                    AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_DESC + " TEXT, " +
                    AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_CIUDAD + " TEXT, " +
                    AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_COD_RENIEC + " TEXT )";
    public static final String SQL_DELETE_DISTRICT_ENTRY =
            "DROP TABLE IF EXISTS " + AgendaComercialContract.DistrictEntry.TABLE_NAME;



    public static final String SQL_CREATE_AGENCY_ENTRY =
            "CREATE TABLE " + AgendaComercialContract.AgencyEntry.TABLE_NAME + " ( " +
                    AgendaComercialContract.AgencyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AgendaComercialContract.AgencyEntry.COLUMN_AGENCIA_ID + " INTEGER, " +
                    AgendaComercialContract.AgencyEntry.COLUMN_AGENCIA_COD + " TEXT, " +
                    AgendaComercialContract.AgencyEntry.COLUMN_AGENCIA_DESCRIPTION + " TEXT)";
    public static final String SQL_DELETE_AGENCY_ENTRY =
            "DROP TABLE IF EXISTS " + AgendaComercialContract.AgencyEntry.TABLE_NAME;


    public static final String SQL_DELETE_REFERIDOS =
            "DROP TABLE IF EXISTS " + AgendaComercialContract.ReferredsEntry.TABLE_NAME;

    public AgendaComercialDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_VISITS_ENTRY);
        db.execSQL(SQL_CREATE_RESULT_ENTRY);
        db.execSQL(SQL_CREATE_PRODUCT_ENTRY);
        db.execSQL(SQL_CREATE_CONTACT_TYPE_ENTRY);
        db.execSQL(SQL_CREATE_VISIT_RESULTS_ENTRY);
        db.execSQL(SQL_CREATE_OFFERS_CLIENT_ENTRY);
        db.execSQL(SQL_CREATE_OFFERS_ENTRY);

        db.execSQL(SQL_CREATE_REFERREDS_ENTRY);
        db.execSQL(SQL_CREATE_DEPARTAMENT_ENTRY);
        db.execSQL(SQL_CREATE_PROVINCE_ENTRY);
        db.execSQL(SQL_CREATE_DISTRICT_ENTRY);
        db.execSQL(SQL_CREATE_AGENCY_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_VISITS_ENTRY);
        db.execSQL(SQL_DELETE_RESULT_ENTRY);
        db.execSQL(SQL_DELETE_PRODUCT_ENTRY);
        db.execSQL(SQL_DELETE_CONTACT_TYPE_ENTRY);
        db.execSQL(SQL_DELETE_VISIT_RESULTS_ENTRY);
        db.execSQL(SQL_DELETE_OFFERS_CLIENT_ENTRY);
        db.execSQL(SQL_DELETE_OFFERS_ENTRY);

        db.execSQL(SQL_DELETE_REFERREDS_ENTRY);
        db.execSQL(SQL_DELETE_DEPARTAMENT_ENTRY);
        db.execSQL(SQL_DELETE_PROVINCE_ENTRY);
        db.execSQL(SQL_DELETE_DISTRICT_ENTRY);
        db.execSQL(SQL_DELETE_AGENCY_ENTRY);
        onCreate(db);
    }


    public boolean insertVisit(JSONArray jsonCustomers) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {

            db.beginTransaction();

            for (int i = 0; i < jsonCustomers.length(); i++) {

                JSONObject row = jsonCustomers.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_ID_AGENCY, row.getString("IdAgencia"));
                contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_DESC_AGENCY, row.getString("DescAgencia"));
                contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_ID_CUSTOMER, row.getString("IdCliente"));
                contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_NAME, row.getString("Nombres"));
                contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_AGE, row.getInt("Edad"));
                contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_DNI, row.getString("DNI"));
                contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_PHONE, row.getString("Telefono"));
                contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_ADDRESS, row.getString("Direccion"));
                contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_DATE_VISIT, row.getString("FechaVisita"));
                contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_ID_USER, row.getString("IdUsuario"));

                contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_FLAG, FLAG_NUEVO);
                contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_RESULT, FLAG_SIN_RESULTADO);
                contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_SYNCHRONIZATION, FLAG_NOT_SYNCRONIZED);

                db.insert(
                        AgendaComercialContract.VisitsEntry.TABLE_NAME,
                        null,
                        contentValues
                );
            }


            db.setTransactionSuccessful();
            result = true;

        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }

        return result;

    }
    public Cursor getAllVisits() {


        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.VisitsEntry.COLUMN_ID_AGENCY,
                AgendaComercialContract.VisitsEntry.COLUMN_DESC_AGENCY,
                AgendaComercialContract.VisitsEntry.COLUMN_ID_CUSTOMER,
                AgendaComercialContract.VisitsEntry.COLUMN_NAME,
                AgendaComercialContract.VisitsEntry.COLUMN_AGE,
                AgendaComercialContract.VisitsEntry.COLUMN_DNI,
                AgendaComercialContract.VisitsEntry.COLUMN_PHONE,
                AgendaComercialContract.VisitsEntry.COLUMN_ADDRESS,
                AgendaComercialContract.VisitsEntry.COLUMN_DATE_VISIT,
                AgendaComercialContract.VisitsEntry.COLUMN_ID_USER,

                AgendaComercialContract.VisitsEntry.COLUMN_FLAG,
                AgendaComercialContract.VisitsEntry.COLUMN_RESULT,
                AgendaComercialContract.VisitsEntry.COLUMN_SYNCHRONIZATION
        };

        String sortOrder =
                AgendaComercialContract.VisitsEntry.COLUMN_NAME + " ASC";

        return  db.query(
                true,
                AgendaComercialContract.VisitsEntry.TABLE_NAME,
                projection,
                null,
                null,
                AgendaComercialContract.VisitsEntry.COLUMN_DNI,
                null,
                sortOrder,
                null
        );

    }
    public ArrayList<ClienteVisita> listVisits(Cursor cursor) {

        ArrayList<ClienteVisita> listVisits = new ArrayList<>();

        while ( cursor.moveToNext() ) {
            ClienteVisita clienteVisita = new ClienteVisita();

            clienteVisita.setId(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry._ID)));
            clienteVisita.setIdAgencia(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry.COLUMN_ID_AGENCY)));
            clienteVisita.setDescAgencia(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry.COLUMN_DESC_AGENCY)));
            clienteVisita.setIdCliente(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry.COLUMN_ID_CUSTOMER)));
            clienteVisita.setNombres(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry.COLUMN_NAME)));
            clienteVisita.setEdad(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry.COLUMN_AGE)));
            clienteVisita.setDni(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry.COLUMN_DNI)));
            clienteVisita.setTelefono(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry.COLUMN_PHONE)));
            clienteVisita.setDireccion(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry.COLUMN_ADDRESS)));
            clienteVisita.setFechaVisita(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry.COLUMN_DATE_VISIT)));
            clienteVisita.setIdUsuario(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry.COLUMN_ID_USER)));

            clienteVisita.setFlag(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry.COLUMN_FLAG)));
            clienteVisita.setResultado(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry.COLUMN_RESULT)));
            clienteVisita.setSincronizar(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.VisitsEntry.COLUMN_SYNCHRONIZATION)));

            listVisits.add(clienteVisita);
        }

        return listVisits;

    }



    public Cursor getAllResultSynchronize() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.ResultEntry.COLUMN_ID_CUSTOMER,
                AgendaComercialContract.ResultEntry.COLUMN_ID_OFFER,
                AgendaComercialContract.ResultEntry.COLUMN_ID_USER,
                AgendaComercialContract.ResultEntry.COLUMN_AMOUNT,
                AgendaComercialContract.ResultEntry.COLUMN_ID_RESULT,
                AgendaComercialContract.ResultEntry.COLUMN_ID_PRODUCT,

                AgendaComercialContract.ResultEntry.COLUMN_COMMENTARY,
                AgendaComercialContract.ResultEntry.COLUMN_TYPE_CREDIT,
                AgendaComercialContract.ResultEntry.COLUMN_CREDIT_DESTINATION,
                AgendaComercialContract.ResultEntry.COLUMN_TYPE_CONTACT,
                AgendaComercialContract.ResultEntry.COLUMN_VISIT,

                AgendaComercialContract.ResultEntry.COLUMN_TYPE,
                AgendaComercialContract.ResultEntry.COLUM_SYNCHRONIZATION,

                AgendaComercialContract.ResultEntry.COLUMN_LATITUD,
                AgendaComercialContract.ResultEntry.COLUMN_LONGITUD

        };


        return  db.query(
                AgendaComercialContract.ResultEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

    }
    public List<ResultadoVisita> listResultVisitsSynchronize(Cursor cursor) {

        List<ResultadoVisita> resultadoVisitaList = new ArrayList<>();

        while ( cursor.moveToNext() ) {

            ResultadoVisita resultadoVisita = new ResultadoVisita();

            resultadoVisita.setId(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ResultEntry._ID)));
            resultadoVisita.setIdCliente(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_ID_CUSTOMER)));
            resultadoVisita.setIdOferta(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_ID_OFFER)));
            resultadoVisita.setIdUsuario(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_ID_USER)));
            resultadoVisita.setMonto(cursor.getDouble(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_AMOUNT)));
            resultadoVisita.setIdResultado(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_ID_RESULT)));
            resultadoVisita.setIdProducto(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_ID_PRODUCT)));
            resultadoVisita.setComentario(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_COMMENTARY)));
            resultadoVisita.setTipoCredito(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_TYPE_CREDIT)));
            resultadoVisita.setDestinoCredito(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_CREDIT_DESTINATION)));
            resultadoVisita.setTipoContacto(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_TYPE_CONTACT)));
            resultadoVisita.setVisita(new Date(cursor.getLong(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_VISIT))));
            resultadoVisita.setTipo(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_TYPE)));
            resultadoVisita.setSincronizar(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUM_SYNCHRONIZATION)));

            resultadoVisita.setLongitud(cursor.getDouble(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_LONGITUD)));
            resultadoVisita.setLatitud(cursor.getDouble(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_LATITUD)));

            resultadoVisitaList.add(resultadoVisita);
        }

        return resultadoVisitaList;


    }
    public int deleteAllResultVisitSynchronize() {

        SQLiteDatabase db = getWritableDatabase();


        return db.delete(
                AgendaComercialContract.ResultEntry.TABLE_NAME,
                "1",
                null
        );

    }
    public int deleteAllReferidosSynchronize() {

        SQLiteDatabase db = getWritableDatabase();


        return db.delete(
                AgendaComercialContract.ReferredsEntry.TABLE_NAME,
                "1",
                null
        );

    }
    public int deleteAllVisitSynchronize() {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                AgendaComercialContract.VisitsEntry.TABLE_NAME,
                "1",
                null
        );

    }

    public Cursor filterVisits(String name) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.VisitsEntry.COLUMN_ID_AGENCY,
                AgendaComercialContract.VisitsEntry.COLUMN_DESC_AGENCY,
                AgendaComercialContract.VisitsEntry.COLUMN_ID_CUSTOMER,
                AgendaComercialContract.VisitsEntry.COLUMN_NAME,
                AgendaComercialContract.VisitsEntry.COLUMN_AGE,
                AgendaComercialContract.VisitsEntry.COLUMN_DNI,
                AgendaComercialContract.VisitsEntry.COLUMN_PHONE,
                AgendaComercialContract.VisitsEntry.COLUMN_ADDRESS,
                AgendaComercialContract.VisitsEntry.COLUMN_DATE_VISIT,
                AgendaComercialContract.VisitsEntry.COLUMN_ID_USER,

                AgendaComercialContract.VisitsEntry.COLUMN_FLAG,
                AgendaComercialContract.VisitsEntry.COLUMN_RESULT,
                AgendaComercialContract.VisitsEntry.COLUMN_SYNCHRONIZATION
        };

        String selection = AgendaComercialContract.VisitsEntry.COLUMN_NAME + " like ? ";

        String[] selectionArgs = { "%"+name+"%" };

        String sortOrder =
                AgendaComercialContract.VisitsEntry.COLUMN_NAME + " ASC";

        return db.query(
                true,
                AgendaComercialContract.VisitsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                AgendaComercialContract.VisitsEntry.COLUMN_DNI,
                null,
                sortOrder,
                null);

    }



    public boolean insertResultVisit(JSONArray jsonResultVisit, int typeContact) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {

            db.beginTransaction();

            for (int i = 0; i < jsonResultVisit.length(); i++) {

                JSONObject row = jsonResultVisit.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(AgendaComercialContract.VisitResultsEntry.COLUMN_CODE, row.getString("ConsCod"));
                contentValues.put(AgendaComercialContract.VisitResultsEntry.COLUMN_VALUE, row.getString("ConsValor"));
                contentValues.put(AgendaComercialContract.VisitResultsEntry.COLUMN_DESCRIPTION, row.getString("ConsDesc"));
                contentValues.put(AgendaComercialContract.VisitResultsEntry.COLUMN_TYPE_CONTACT, typeContact);

                db.insert(
                        AgendaComercialContract.VisitResultsEntry.TABLE_NAME,
                        null,
                        contentValues
                );
            }


            db.setTransactionSuccessful();
            result = true;

        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }

        return result;

    }
    public Cursor getAllResultVisits(int typeContact) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.VisitResultsEntry.COLUMN_CODE,
                AgendaComercialContract.VisitResultsEntry.COLUMN_VALUE,
                AgendaComercialContract.VisitResultsEntry.COLUMN_DESCRIPTION,
                AgendaComercialContract.VisitResultsEntry.COLUMN_TYPE_CONTACT
        };

        String selection = AgendaComercialContract.VisitResultsEntry.COLUMN_TYPE_CONTACT + " = ?";
        String selectionArgs[] = { String.valueOf(typeContact) };

        String sortOrder =
                AgendaComercialContract.VisitResultsEntry.COLUMN_DESCRIPTION + " ASC";

        return  db.query(
                AgendaComercialContract.VisitResultsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                null
        );


    }
    public List<Resultado> listResultVisits(Cursor cursor) {
        List<Resultado> listResults = new ArrayList<>();

        while ( cursor.moveToNext() ) {

            Resultado resultado = new Resultado();
            resultado.setCodigo(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.VisitResultsEntry.COLUMN_CODE)));
            resultado.setValor(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.VisitResultsEntry.COLUMN_VALUE)));
            resultado.setDescripcion(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.VisitResultsEntry.COLUMN_DESCRIPTION)));

            listResults.add(resultado);
        }

        return listResults;
    }
    public void deleteaAllResultVisit(int typeContact) {

        SQLiteDatabase db = getWritableDatabase();

        String whereClause = AgendaComercialContract.VisitResultsEntry.COLUMN_TYPE_CONTACT + " = ?";
        String whereArgs[] = { String.valueOf(typeContact) };

        db.delete(
                AgendaComercialContract.VisitResultsEntry.TABLE_NAME,
                whereClause,
                whereArgs
        );

    }



    public boolean insertOfferClient(JSONArray jsonOffer) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {

            db.beginTransaction();

            for (int i = 0; i < jsonOffer.length(); i++) {

                JSONObject row = jsonOffer.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(AgendaComercialContract.OfferClientEntry.COLUMN_ID_USER, row.getString("IdUsuario"));
                contentValues.put(AgendaComercialContract.OfferClientEntry.COLUMN_ID_CLIENT, row.getString("IdCliente"));
                contentValues.put(AgendaComercialContract.OfferClientEntry.COLUMN_DOC, row.getString("Doc"));
                contentValues.put(AgendaComercialContract.OfferClientEntry.COLUMN_ID_OFFER, row.getString("IdOferta"));
                contentValues.put(AgendaComercialContract.OfferClientEntry.COLUMN_OFFER, row.getString("Oferta"));
                contentValues.put(AgendaComercialContract.OfferClientEntry.COLUMN_AMOUNT_OFFER_CC, row.getString("MontoOfertCc"));
                contentValues.put(AgendaComercialContract.OfferClientEntry.COLUMN_AMOUNT_OFFER_SC, row.getString("MontoOfertSc"));


                db.insert(
                        AgendaComercialContract.OfferClientEntry.TABLE_NAME,
                        null,
                        contentValues
                );
            }


            db.setTransactionSuccessful();
            result = true;

        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }

        return result;

    }
    public Cursor getAllOfferClient() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.OfferClientEntry.COLUMN_ID_USER,
                AgendaComercialContract.OfferClientEntry.COLUMN_ID_CLIENT,
                AgendaComercialContract.OfferClientEntry.COLUMN_DOC,
                AgendaComercialContract.OfferClientEntry.COLUMN_ID_OFFER,
                AgendaComercialContract.OfferClientEntry.COLUMN_OFFER,
                AgendaComercialContract.OfferClientEntry.COLUMN_AMOUNT_OFFER_CC,
                AgendaComercialContract.OfferClientEntry.COLUMN_AMOUNT_OFFER_SC,
        };



        String selection = AgendaComercialContract.OfferClientEntry.COLUMN_OFFER + " != ?";
        String selectionArgs[] = { "null" };

        return  db.query(
                AgendaComercialContract.OfferClientEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );


    }
    public List<OfertaCliente> listOfferClient(Cursor cursor) {
        List<OfertaCliente> offerList = new ArrayList<>();

        while ( cursor.moveToNext() ) {

            OfertaCliente ofertaCliente = new OfertaCliente();

            ofertaCliente.setId(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.OfferClientEntry._ID)));
            ofertaCliente.setIdUsuario(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.OfferClientEntry.COLUMN_ID_USER)));
            ofertaCliente.setDoc(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.OfferClientEntry.COLUMN_DOC)));
            ofertaCliente.setIdOferta(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.OfferClientEntry.COLUMN_ID_OFFER)));
            ofertaCliente.setOferta(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.OfferClientEntry.COLUMN_OFFER)));
            ofertaCliente.setMontoOfertaCC(cursor.getDouble(cursor.getColumnIndex(AgendaComercialContract.OfferClientEntry.COLUMN_AMOUNT_OFFER_CC)));
            ofertaCliente.setMontoOfertaSC(cursor.getDouble(cursor.getColumnIndex(AgendaComercialContract.OfferClientEntry.COLUMN_AMOUNT_OFFER_SC)));


            offerList.add(ofertaCliente);
        }

        return offerList;
    }
    public void deleteaAllOfferClient() {

        SQLiteDatabase db = getWritableDatabase();


        db.delete(
                AgendaComercialContract.OfferClientEntry.TABLE_NAME,
                null,
                null
        );

    }



    public boolean insertProduct(JSONArray jsonProducts) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {

            db.beginTransaction();

            for (int i = 0; i < jsonProducts.length(); i++) {

                JSONObject row = jsonProducts.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(AgendaComercialContract.ProductEntry.COLUMN_CODE, row.getString("ConsCod"));
                contentValues.put(AgendaComercialContract.ProductEntry.COLUMN_VALUE, row.getString("ConsValor"));
                contentValues.put(AgendaComercialContract.ProductEntry.COLUMN_DESCRIPTION, row.getString("ConsDesc"));

                db.insert(
                        AgendaComercialContract.ProductEntry.TABLE_NAME,
                        null,
                        contentValues
                );
            }


            db.setTransactionSuccessful();
            result = true;

        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }

        return result;

    }
    public Cursor getAllProduct() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.ProductEntry.COLUMN_CODE,
                AgendaComercialContract.ProductEntry.COLUMN_VALUE,
                AgendaComercialContract.ProductEntry.COLUMN_DESCRIPTION
        };

        String sortOrder =
                AgendaComercialContract.ProductEntry.COLUMN_DESCRIPTION + " ASC";

        return  db.query(
                AgendaComercialContract.ProductEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                null
        );


    }
    public List<Producto> listProduct(Cursor cursor) {

        List<Producto> productoList = new ArrayList<>();

        while ( cursor.moveToNext() ) {

            Producto producto = new Producto();
            producto.setCodigo(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ProductEntry.COLUMN_CODE)));
            producto.setValor(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ProductEntry.COLUMN_VALUE)));
            producto.setDescripcion(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ProductEntry.COLUMN_DESCRIPTION)));

            productoList.add(producto);
        }

        return productoList;

    }
    public void deleteAllProduct() {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                AgendaComercialContract.ProductEntry.TABLE_NAME,
                null,
                null
        );

    }


    public boolean insertContactType(JSONArray jsonContactType) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {

            db.beginTransaction();

            for (int i = 0; i < jsonContactType.length(); i++) {

                JSONObject row = jsonContactType.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(AgendaComercialContract.ContactTypeEntry.COLUMN_CODE, row.getString("CodigoConstante"));
                contentValues.put(AgendaComercialContract.ContactTypeEntry.COLUMN_VALUE, row.getString("CodigoValor"));
                contentValues.put(AgendaComercialContract.ContactTypeEntry.COLUMN_DESCRIPTION, row.getString("Descripcion"));

                db.insert(
                        AgendaComercialContract.ContactTypeEntry.TABLE_NAME,
                        null,
                        contentValues
                );
            }


            db.setTransactionSuccessful();
            result = true;

        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }

        return result;

    }
    public Cursor getAllContactType() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.ContactTypeEntry.COLUMN_CODE,
                AgendaComercialContract.ContactTypeEntry.COLUMN_VALUE,
                AgendaComercialContract.ContactTypeEntry.COLUMN_DESCRIPTION
        };

        String sortOrder =
                AgendaComercialContract.ContactTypeEntry.COLUMN_DESCRIPTION + " ASC";

        return  db.query(
                AgendaComercialContract.ContactTypeEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                null
        );

    }
    public List<TipoContacto> listContactType(Cursor cursor) {

        List<TipoContacto> tipoContactoList = new ArrayList<>();

        while ( cursor.moveToNext() ) {

            TipoContacto tipoContacto = new TipoContacto();
            tipoContacto.setCodigo(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ContactTypeEntry.COLUMN_CODE)));
            tipoContacto.setValor(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ContactTypeEntry.COLUMN_VALUE)));
            tipoContacto.setDescripcion(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ContactTypeEntry.COLUMN_DESCRIPTION)));

            tipoContactoList.add(tipoContacto);
        }

        return tipoContactoList;

    }
    public void deleteAllContactType() {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                AgendaComercialContract.ContactTypeEntry.TABLE_NAME,
                null,
                null
        );

    }


    public boolean insertOffers(JSONArray jsonOffers) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {

            db.beginTransaction();

            for (int i = 0; i < jsonOffers.length(); i++) {

                JSONObject row = jsonOffers.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(AgendaComercialContract.OffersEntry.COLUMN_ID_CLIENT, row.getInt("IdCliente"));
                contentValues.put(AgendaComercialContract.OffersEntry.COLUMN_DNI, row.getString("DNI"));
                contentValues.put(AgendaComercialContract.OffersEntry.COLUMN_NAME, row.getString("Nombres"));
                contentValues.put(AgendaComercialContract.OffersEntry.COLUMN_DESC_OFFER, row.getString("DescOferta"));
                contentValues.put(AgendaComercialContract.OffersEntry.COLUMN_OFFER_AMOUNT_CC, row.getDouble("MontoOfertCc"));
                contentValues.put(AgendaComercialContract.OffersEntry.COLUMN_OFFER_AMOUNT_SC, row.getDouble("MontoOfertCc"));

                db.insert(
                        AgendaComercialContract.OffersEntry.TABLE_NAME,
                        null,
                        contentValues
                );
            }


            db.setTransactionSuccessful();
            result = true;

        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }

        return result;

    }
    public Cursor getAllOffers() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,

                AgendaComercialContract.OffersEntry.COLUMN_ID_CLIENT,
                AgendaComercialContract.OffersEntry.COLUMN_DNI,
                AgendaComercialContract.OffersEntry.COLUMN_NAME,
                AgendaComercialContract.OffersEntry.COLUMN_DESC_OFFER,
                AgendaComercialContract.OffersEntry.COLUMN_OFFER_AMOUNT_CC,
                AgendaComercialContract.OffersEntry.COLUMN_OFFER_AMOUNT_SC
        };

        String sortOrder =
                AgendaComercialContract.OffersEntry.COLUMN_NAME + " ASC";

        return  db.query(
                AgendaComercialContract.OffersEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                null
        );

    }
    public List<Oferta> listOffers(Cursor cursor) {

        List<Oferta> ofertaList = new ArrayList<>();

        while ( cursor.moveToNext() ) {

            Oferta oferta = new Oferta();
            oferta.setId(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.OffersEntry._ID)));
            oferta.setIdCliente(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.OffersEntry.COLUMN_ID_CLIENT)));
            oferta.setDni(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.OffersEntry.COLUMN_DNI)));
            oferta.setNombres(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.OffersEntry.COLUMN_NAME)));
            oferta.setDescOferta(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.OffersEntry.COLUMN_DESC_OFFER)));
            oferta.setMontoOfertCc(cursor.getDouble(cursor.getColumnIndex(AgendaComercialContract.OffersEntry.COLUMN_OFFER_AMOUNT_CC)));
            oferta.setMontoOfertSc(cursor.getDouble(cursor.getColumnIndex(AgendaComercialContract.OffersEntry.COLUMN_OFFER_AMOUNT_SC)));

            ofertaList.add(oferta);
        }

        return ofertaList;

    }
    public void deleteAllOffers() {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                AgendaComercialContract.OffersEntry.TABLE_NAME,
                null,
                null
        );

    }



    public int updateDataClient(ClienteVisita clienteVisita) {

        SQLiteDatabase db = getReadableDatabase();
        int result;

        ContentValues contentValues = new ContentValues();
        contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_NAME, clienteVisita.getNombres());
        contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_PHONE, clienteVisita.getTelefono());
        contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_ADDRESS, clienteVisita.getDireccion());
        contentValues.put(AgendaComercialContract.VisitsEntry.COLUMN_FLAG, clienteVisita.getFlag());

        String whereClauses = AgendaComercialContract.VisitsEntry.COLUMN_DNI + " = ?";
        String whereArgs[] = { String.valueOf(clienteVisita.getDni()) };

        result = db.update(
                AgendaComercialContract.VisitsEntry.TABLE_NAME,
                contentValues,
                whereClauses,
                whereArgs
        );

        return result;

    }

    /*
    public List<ResultadoVisita> getAllResultsSynchronize(){



        return listResultVisitForSynchronize(null);

    }
    private List<ResultadoVisita> listResultVisitForSynchronize(Cursor cursor) {

        List<ResultadoVisita> clienteVisitaList = new ArrayList<>();

        while ( cursor.moveToNext() ) {

            ResultadoVisita resultadoVisita = new ResultadoVisita();
            resultadoVisita.setCodigoUsuario( cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_USER_CODE)) );
            resultadoVisita.setDni( cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_DNI)) );
            resultadoVisita.setCodigoOrigen( cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_ORIGIN_CODE)) );
            resultadoVisita.setResultado( cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_RESULT)) );
            resultadoVisita.setMonto( cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_AMOUNT)) );
            resultadoVisita.setProducto( cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_PRODUCT)) );
            resultadoVisita.setComentario( cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_COMMENTARY)) );
            resultadoVisita.setTipoResultado( cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ResultEntry.COLUMN_TYPE)) );

            clienteVisitaList.add(resultadoVisita);
        }

        return clienteVisitaList;

    }
    */


    public boolean insertResultVisitCustomer(ResultadoVisita resultadoVisita, int fieldResult) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {

            db.beginTransaction();

            // TODO 1 insertar resultado
            ContentValues contentValues = new ContentValues();
            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_ID_CUSTOMER, resultadoVisita.getIdCliente());
            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_ID_OFFER, resultadoVisita.getIdOferta());
            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_ID_USER, resultadoVisita.getIdUsuario());
            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_AMOUNT, resultadoVisita.getMonto());
            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_ID_RESULT, resultadoVisita.getIdResultado());
            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_ID_PRODUCT, resultadoVisita.getIdProducto());
            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_COMMENTARY, resultadoVisita.getComentario());
            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_TYPE_CREDIT, resultadoVisita.getTipoCredito());
            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_CREDIT_DESTINATION, resultadoVisita.getDestinoCredito());
            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_TYPE_CONTACT, resultadoVisita.getTipoContacto());
            // TODO revisar el datetime de visita
            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_VISIT, resultadoVisita.getVisita().getTime());

            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_TYPE, resultadoVisita.getTipo());
            contentValues.put(AgendaComercialContract.ResultEntry.COLUM_SYNCHRONIZATION, resultadoVisita.getSincronizar());

            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_LONGITUD, resultadoVisita.getLongitud());
            contentValues.put(AgendaComercialContract.ResultEntry.COLUMN_LATITUD, resultadoVisita.getLatitud());

            db.insert(
                    AgendaComercialContract.ResultEntry.TABLE_NAME,
                    null,
                    contentValues
            );


            // TODO 2 actualizacion de resultado
            ContentValues contentValuesUpdate = new ContentValues();
            contentValuesUpdate.put(AgendaComercialContract.VisitsEntry.COLUMN_RESULT, fieldResult);


            String whereClauses = AgendaComercialContract.VisitsEntry.COLUMN_ID_CUSTOMER + " = ?";
            String whereArgs[] = { String.valueOf(resultadoVisita.getIdCliente()) };

            db.update(
                    AgendaComercialContract.VisitsEntry.TABLE_NAME,
                    contentValuesUpdate,
                    whereClauses,
                    whereArgs
            );

            db.setTransactionSuccessful();

            result = true;

        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }

        return result;

    }




    public boolean insertDepartments(JSONArray jsonDepartments) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {

            db.beginTransaction();

            for (int i = 0; i < jsonDepartments.length(); i++) {

                JSONObject row = jsonDepartments.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_COD, row.getString("CodUbigeo"));
                contentValues.put(AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_DESC, row.getString("DescUbigeo"));
                contentValues.put(AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_COD_RENIEC, row.getString("CodUbigeoRENIEC"));

                db.insert(
                        AgendaComercialContract.DepartmentEntry.TABLE_NAME,
                        null,
                        contentValues
                );
            }


            db.setTransactionSuccessful();
            result = true;

        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }

        return result;

    }
    public Cursor getAllDepartments() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_COD,
                AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_DESC,
                AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_COD_RENIEC
        };

        String sortOrder =
                AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_DESC + " ASC";

        return  db.query(
                AgendaComercialContract.DepartmentEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                null
        );

    }
    public List<DivisionTerriorial> listDepartments(Cursor cursor) {

        List<DivisionTerriorial> departamentsList = new ArrayList<>();

        while ( cursor.moveToNext() ) {

            DivisionTerriorial divisionTerriorial = new DivisionTerriorial();
            divisionTerriorial.setCodigoUbigeo(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_COD)));
            divisionTerriorial.setDescripcionUbigeo(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_DESC)));
            divisionTerriorial.setCodigoUbigeoRENIEC(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.DepartmentEntry.COLUMN_UBIGEO_COD_RENIEC)));

            departamentsList.add(divisionTerriorial);
        }

        return departamentsList;

    }
    public void deleteAllDepartments() {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                AgendaComercialContract.DepartmentEntry.TABLE_NAME,
                null,
                null
        );

    }


    public boolean insertProvinces(JSONArray jsonProvinces) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {

            db.beginTransaction();

            for (int i = 0; i < jsonProvinces.length(); i++) {

                JSONObject row = jsonProvinces.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_COD, row.getString("CodUbigeo"));
                contentValues.put(AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_DESC, row.getString("DescUbigeo"));
                contentValues.put(AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_COD_RENIEC, row.getString("CodUbigeoRENIEC"));

                db.insert(
                        AgendaComercialContract.ProvinceEntry.TABLE_NAME,
                        null,
                        contentValues
                );
            }


            db.setTransactionSuccessful();
            result = true;

        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }

        return result;

    }
    public Cursor getAllProvinces() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_COD,
                AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_DESC,
                AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_COD_RENIEC
        };

        String sortOrder =
                AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_DESC + " ASC";

        return  db.query(
                AgendaComercialContract.ProvinceEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                null
        );

    }
    public List<DivisionTerriorial> listProvinces(Cursor cursor) {

        List<DivisionTerriorial> provincesList = new ArrayList<>();

        while ( cursor.moveToNext() ) {


            DivisionTerriorial divisionTerriorial = new DivisionTerriorial();
            divisionTerriorial.setCodigoUbigeo(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_COD)));
            divisionTerriorial.setDescripcionUbigeo(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_DESC)));
            divisionTerriorial.setCodigoUbigeoRENIEC(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_COD_RENIEC)));

            provincesList.add(divisionTerriorial);
        }

        return provincesList;

    }
    public void deleteAllProvinces() {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                AgendaComercialContract.ProvinceEntry.TABLE_NAME,
                null,
                null
        );

    }
    public Cursor getProvince(String ubigeoCodProvince) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_COD,
                AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_DESC,
                AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_COD_RENIEC
        };

        String selection = AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_COD_RENIEC + " like ? ";
        String selectionArgs[] = { ubigeoCodProvince+"%" };

        String sortOrder =
                AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_DESC + " ASC";

        return  db.query(
                AgendaComercialContract.ProvinceEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                null
        );

    }
    public void deleteProvince(String ubigeoCodProvince) {

        SQLiteDatabase db = getWritableDatabase();

        String whereClause = AgendaComercialContract.ProvinceEntry.COLUMN_UBIGEO_COD + " like ?";
        String whereArgs[] = { "2"+ubigeoCodProvince+"%" };

        db.delete(
                AgendaComercialContract.ProvinceEntry.TABLE_NAME,
                whereClause,
                whereArgs
        );

    }

    public boolean insertDistricts(JSONArray jsonDistricts) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {

            db.beginTransaction();

            for (int i = 0; i < jsonDistricts.length(); i++) {

                JSONObject row = jsonDistricts.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_COD, row.getString("CodUbigeo"));
                contentValues.put(AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_DESC, row.getString("DescUbigeo"));
                contentValues.put(AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_COD_RENIEC, row.getString("CodUbigeoRENIEC"));

                db.insert(
                        AgendaComercialContract.DistrictEntry.TABLE_NAME,
                        null,
                        contentValues
                );
            }


            db.setTransactionSuccessful();
            result = true;

        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }

        return result;

    }
    public Cursor getAllDistricts() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_COD,
                AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_DESC,
                AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_COD_RENIEC
        };

        String sortOrder =
                AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_DESC + " ASC";

        return  db.query(
                AgendaComercialContract.DistrictEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                null
        );

    }
    public List<DivisionTerriorial> listDistricts(Cursor cursor) {

        List<DivisionTerriorial> districtsList = new ArrayList<>();

        while ( cursor.moveToNext() ) {

            DivisionTerriorial divisionTerriorial = new DivisionTerriorial();
            divisionTerriorial.setCodigoUbigeo(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_COD)));
            divisionTerriorial.setDescripcionUbigeo(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_DESC)));
            divisionTerriorial.setCodigoUbigeoRENIEC(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_COD_RENIEC)));

            districtsList.add(divisionTerriorial);
        }

        return districtsList;

    }
    public void deleteAllDistricts() {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                AgendaComercialContract.DistrictEntry.TABLE_NAME,
                null,
                null
        );

    }
    public Cursor getDistrict(String ubigeoCodRENIECDistrict) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_COD,
                AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_DESC,
                AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_COD_RENIEC
        };

        String selection = AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_COD_RENIEC + " like ? ";
        String selectionArgs[] = { ubigeoCodRENIECDistrict+"%" };

        String sortOrder =
                AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_DESC + " ASC";

        return  db.query(
                AgendaComercialContract.DistrictEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                null
        );

    }
    public void deleteDistrict(String ubigeoCodRENIECDistrict) {

        SQLiteDatabase db = getWritableDatabase();

        String whereClause = AgendaComercialContract.DistrictEntry.COLUMN_UBIGEO_COD_RENIEC + " like ?";
        String whereArgs[] = { ubigeoCodRENIECDistrict+"%" };

        db.delete(
                AgendaComercialContract.DistrictEntry.TABLE_NAME,
                whereClause,
                whereArgs
        );

    }



    public boolean insertAgencies(JSONArray jsonAgencies) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {

            db.beginTransaction();

            for (int i = 0; i < jsonAgencies.length(); i++) {

                JSONObject row = jsonAgencies.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(AgendaComercialContract.AgencyEntry.COLUMN_AGENCIA_ID, row.getInt("IdAgencia"));
                contentValues.put(AgendaComercialContract.AgencyEntry.COLUMN_AGENCIA_COD, row.getString("CodAgencia"));
                contentValues.put(AgendaComercialContract.AgencyEntry.COLUMN_AGENCIA_DESCRIPTION, row.getString("DescAgencia"));

                db.insert(
                        AgendaComercialContract.AgencyEntry.TABLE_NAME,
                        null,
                        contentValues
                );
            }


            db.setTransactionSuccessful();
            result = true;

        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }

        return result;

    }
    public Cursor getAllAgencies() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.AgencyEntry.COLUMN_AGENCIA_ID,
                AgendaComercialContract.AgencyEntry.COLUMN_AGENCIA_COD,
                AgendaComercialContract.AgencyEntry.COLUMN_AGENCIA_DESCRIPTION
        };

        String sortOrder =
                AgendaComercialContract.AgencyEntry.COLUMN_AGENCIA_DESCRIPTION + " ASC";

        return  db.query(
                AgendaComercialContract.AgencyEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                null
        );

    }
    public List<Agencia> listAgencies(Cursor cursor) {

        List<Agencia> agenciesList = new ArrayList<>();

        while ( cursor.moveToNext() ) {

            Agencia agencia = new Agencia();
            agencia.setIdAgencia(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.AgencyEntry.COLUMN_AGENCIA_ID)));
            agencia.setCodigo(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.AgencyEntry.COLUMN_AGENCIA_COD)));
            agencia.setDescripcion(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.AgencyEntry.COLUMN_AGENCIA_DESCRIPTION)));

            agenciesList.add(agencia);

        }

        return agenciesList;

    }
    public void deleteAllAgencies() {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                AgendaComercialContract.AgencyEntry.TABLE_NAME,
                null,
                null
        );

    }



    public int insertReferred(ClienteReferido clienteReferido) {


        SQLiteDatabase db = getWritableDatabase();
        int result = FLAG_INSERTAR_REFERIDO_ERROR;

        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_DNI, clienteReferido.getDocumento());
            contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_NAME, clienteReferido.getNombres());
            contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_ADDRESS, clienteReferido.getDireccion());
            contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_PHONE, clienteReferido.getTelefono());
            contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_DEPARTMENT, clienteReferido.getDepartamento());
            contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_PROVINCE, clienteReferido.getProvincia());
            contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_DISTRICT, clienteReferido.getDistrito());
            contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_ID_AGENCY, clienteReferido.getIdAgencia());
            contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_ID_USER, clienteReferido.getIdUsuario());
            contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_ID_PRODUCT, clienteReferido.getIdProducto());
            contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_RESULT_STATE, clienteReferido.getEstadoResultado());

            // TODO flag locales
            contentValues.put(AgendaComercialContract.ReferredsEntry.COLUM_SYNCHRONIZATION, FLAG_NOT_SYNCRONIZED);


            int validationIfExist = getReferred(clienteReferido.getDocumento()).getCount();

            if (validationIfExist == 0) {

                long resultInsert = db.insert(
                        AgendaComercialContract.ReferredsEntry.TABLE_NAME,
                        null,
                        contentValues
                );

                if (resultInsert!=-1) { result = FLAG_INSERTAR_REFERIDO_EXITOSO; }

            } else {
                result = FLAG_INSERTAR_REFERIDO_ERROR_EXISTENTE;
            }


        } catch (Exception e) {
            result = FLAG_INSERTAR_REFERIDO_ERROR;
        }

        return result;

    }

    public Cursor getReferred(String doi) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.ReferredsEntry.COLUMN_DNI,
                AgendaComercialContract.ReferredsEntry.COLUMN_NAME,
                AgendaComercialContract.ReferredsEntry.COLUMN_ADDRESS,
                AgendaComercialContract.ReferredsEntry.COLUMN_PHONE,
                AgendaComercialContract.ReferredsEntry.COLUMN_DEPARTMENT,
                AgendaComercialContract.ReferredsEntry.COLUMN_PROVINCE,
                AgendaComercialContract.ReferredsEntry.COLUMN_DISTRICT,
                AgendaComercialContract.ReferredsEntry.COLUMN_ID_AGENCY,
                AgendaComercialContract.ReferredsEntry.COLUMN_ID_USER,
                AgendaComercialContract.ReferredsEntry.COLUMN_ID_PRODUCT,
                AgendaComercialContract.ReferredsEntry.COLUMN_RESULT_STATE,

                AgendaComercialContract.ReferredsEntry.COLUM_SYNCHRONIZATION
        };

        String selection = AgendaComercialContract.ReferredsEntry.COLUMN_DNI + " = ? ";
        String selectionArgs[] = { String.valueOf(doi) };

        return  db.query(
                AgendaComercialContract.ReferredsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );
    }


    public Cursor getAllReferred(int flagSynchronized) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.ReferredsEntry.COLUMN_DNI,
                AgendaComercialContract.ReferredsEntry.COLUMN_NAME,
                AgendaComercialContract.ReferredsEntry.COLUMN_ADDRESS,
                AgendaComercialContract.ReferredsEntry.COLUMN_PHONE,
                AgendaComercialContract.ReferredsEntry.COLUMN_DEPARTMENT,
                AgendaComercialContract.ReferredsEntry.COLUMN_PROVINCE,
                AgendaComercialContract.ReferredsEntry.COLUMN_DISTRICT,
                AgendaComercialContract.ReferredsEntry.COLUMN_ID_AGENCY,
                AgendaComercialContract.ReferredsEntry.COLUMN_ID_USER,
                AgendaComercialContract.ReferredsEntry.COLUMN_ID_PRODUCT,
                AgendaComercialContract.ReferredsEntry.COLUMN_RESULT_STATE,

                AgendaComercialContract.ReferredsEntry.COLUM_SYNCHRONIZATION
        };

        String selection = AgendaComercialContract.ReferredsEntry.COLUM_SYNCHRONIZATION + " = ? ";
        String selectionArgs[] = { String.valueOf(flagSynchronized) };

        String sortOrder =
                AgendaComercialContract.ReferredsEntry.COLUMN_NAME + " ASC";

        return  db.query(
                AgendaComercialContract.ReferredsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                null
        );

    }
    public Cursor getAllReferred() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AgendaComercialContract.ReferredsEntry.COLUMN_DNI,
                AgendaComercialContract.ReferredsEntry.COLUMN_NAME,
                AgendaComercialContract.ReferredsEntry.COLUMN_ADDRESS,
                AgendaComercialContract.ReferredsEntry.COLUMN_PHONE,
                AgendaComercialContract.ReferredsEntry.COLUMN_DEPARTMENT,
                AgendaComercialContract.ReferredsEntry.COLUMN_PROVINCE,
                AgendaComercialContract.ReferredsEntry.COLUMN_DISTRICT,
                AgendaComercialContract.ReferredsEntry.COLUMN_ID_AGENCY,
                AgendaComercialContract.ReferredsEntry.COLUMN_ID_USER,
                AgendaComercialContract.ReferredsEntry.COLUMN_ID_PRODUCT,
                AgendaComercialContract.ReferredsEntry.COLUMN_RESULT_STATE,

                AgendaComercialContract.ReferredsEntry.COLUM_SYNCHRONIZATION
        };


        return  db.query(
                AgendaComercialContract.ReferredsEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null,
                null
        );

    }
    public List<ClienteReferido> listReferred(Cursor cursor) {

        List<ClienteReferido> clienteReferidoList = new ArrayList<>();

        try {

            while ( cursor.moveToNext() ) {

                ClienteReferido clienteReferido = new ClienteReferido();

                clienteReferido.setId(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ReferredsEntry._ID)));
                clienteReferido.setDocumento(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ReferredsEntry.COLUMN_DNI)));
                clienteReferido.setNombres(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ReferredsEntry.COLUMN_NAME)));
                clienteReferido.setDireccion(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ReferredsEntry.COLUMN_ADDRESS)));
                clienteReferido.setTelefono(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ReferredsEntry.COLUMN_PHONE)));
                clienteReferido.setDepartamento(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ReferredsEntry.COLUMN_DEPARTMENT)));
                clienteReferido.setProvincia(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ReferredsEntry.COLUMN_PROVINCE)));
                clienteReferido.setDistrito(cursor.getString(cursor.getColumnIndex(AgendaComercialContract.ReferredsEntry.COLUMN_DISTRICT)));
                clienteReferido.setIdAgencia(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ReferredsEntry.COLUMN_ID_AGENCY)));
                clienteReferido.setIdUsuario(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ReferredsEntry.COLUMN_ID_USER)));
                clienteReferido.setIdProducto(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ReferredsEntry.COLUMN_ID_PRODUCT)));
                clienteReferido.setEstadoResultado(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ReferredsEntry.COLUMN_RESULT_STATE)));

                clienteReferido.setSincronizar(cursor.getInt(cursor.getColumnIndex(AgendaComercialContract.ReferredsEntry.COLUM_SYNCHRONIZATION)));

                clienteReferidoList.add(clienteReferido);

            }

        } catch (Exception e) {
            Log.d(TAG, "listReferred: " + e.getMessage());
        }


        return clienteReferidoList;

    }
    public boolean updateReferredSynchronizationStatus() {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        // TODO 1 obtener los clientes sincronizados
        Cursor cursor = getAllReferred(FLAG_NOT_SYNCRONIZED);
        List<ClienteReferido> clienteReferidoList = listReferred(cursor);


        // TODO 2 actualizar su estado (sincronizacion) a FLAG_SYNCRONIZED localmente
        try {


            db.beginTransaction();


            for (ClienteReferido itemCliente: clienteReferidoList) {

                ContentValues contentValues = new ContentValues();

                contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_DNI, itemCliente.getDocumento());
                contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_NAME, itemCliente.getNombres());
                contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_ADDRESS, itemCliente.getDireccion());
                contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_PHONE, itemCliente.getTelefono());
                contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_DEPARTMENT, itemCliente.getDepartamento());
                contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_PROVINCE, itemCliente.getProvincia());
                contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_DISTRICT, itemCliente.getDistrito());
                contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_ID_AGENCY, itemCliente.getIdAgencia());
                contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_ID_USER, itemCliente.getIdUsuario());
                contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_ID_PRODUCT, itemCliente.getIdProducto());
                contentValues.put(AgendaComercialContract.ReferredsEntry.COLUMN_RESULT_STATE, itemCliente.getEstadoResultado());

                contentValues.put(AgendaComercialContract.ReferredsEntry.COLUM_SYNCHRONIZATION, FLAG_SYNCRONIZED);

                String selection = AgendaComercialContract.ReferredsEntry._ID + " = ? ";
                String selectionArgs[] = { String.valueOf(itemCliente.getId()) };

                db.update(
                        AgendaComercialContract.ReferredsEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
            }

            db.setTransactionSuccessful();

            result = true;

        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }

        // TODO 3 arrojar un resultado
        return result;

    }



}
