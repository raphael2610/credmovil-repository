package pe.com.cmacica.flujocredito.Utilitarios.carteraanalista;

import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.NfcAdapter;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.cert.CRLException;
import java.util.ArrayList;
import java.util.List;

import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;
import pe.com.cmacica.flujocredito.Model.carteraanalista.Credito;
import pe.com.cmacica.flujocredito.Model.carteraanalista.Direccion;
import pe.com.cmacica.flujocredito.Model.carteraanalista.TipoDireccion;
import pe.com.cmacica.flujocredito.Utilitarios.Constantes;
import pe.com.cmacica.flujocredito.Utilitarios.UPreferencias;

public class CarteraAnalistaDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "CarteraAnalistaDbHelper";

    private static final String SQL_CREATE_CLIENT_ENTRY =
            "CREATE TABLE " + CarteraAnalistaContract.ClientEntry.TABLE_NAME + " ( " +
                    CarteraAnalistaContract.ClientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CarteraAnalistaContract.ClientEntry.COLUM_PERS_COD + " TEXT," +
                    CarteraAnalistaContract.ClientEntry.COLUMN_NAME + " TEXT," +
                    CarteraAnalistaContract.ClientEntry.COLUMN_DOI + " TEXT," +
                    CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_ONE + " TEXT," +
                    CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO + " TEXT," +
                    CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS_HOME + " TEXT," +
                    CarteraAnalistaContract.ClientEntry.COLUMN_GEOPOSITION + " TEXT," +
                    CarteraAnalistaContract.ClientEntry.COLUMN_CREDITS + " TEXT," +

                    CarteraAnalistaContract.ClientEntry.COLUMN_PHONE + " TEXT," +
                    CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS + " INTEGER," +
                    CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS + " TEXT," +
                    CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE + " NUMERIC," +
                    CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE + " NUMERIC," +
                    CarteraAnalistaContract.ClientEntry.COLUMN_FLAG + " NUMERIC," +
                    CarteraAnalistaContract.ClientEntry.COLUM_SYNCHRONIZATION + " NUMERIC)";

    private static final String SQL_DELETE_CLIENT_ENTRY =
            "DROP TABLE IF EXISTS " + CarteraAnalistaContract.ClientEntry.TABLE_NAME;

    private static final String SQL_CREATE_TYPE_ADDRESS_ENTRY =
            "CREATE TABLE " + CarteraAnalistaContract.TypeAddressEntry.TABLE_NAME + " ( " +
                    CarteraAnalistaContract.TypeAddressEntry._ID + " INTEGER," +
                    CarteraAnalistaContract.TypeAddressEntry.COLUMN_NAME + " TEXT)";

    private static final String SQL_DELETE_TYPE_ADDRESS_ENTRY =
            "DROP TABLE IF EXISTS " + CarteraAnalistaContract.TypeAddressEntry.TABLE_NAME;

    private static final String SQL_CREATE_CREDIT_ENTRY =
            "CREATE TABLE " + CarteraAnalistaContract.CreditEntry.TABLE_NAME + " ( " +
                    CarteraAnalistaContract.CreditEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CarteraAnalistaContract.CreditEntry.COLUMN_NAME + " TEXT, " +
                    CarteraAnalistaContract.CreditEntry.COLUMN_CTA_COD + " TEXT, " +
                    CarteraAnalistaContract.CreditEntry.COLUMN_STATUS + " TEXT," +
                    CarteraAnalistaContract.CreditEntry.COLUMN_DOI + " TEXT," +
                    CarteraAnalistaContract.CreditEntry.COLUMN_AMOUNT + " NUMERIC)";



    private static final String SQL_DELETE_CREDIT_ENTRY =
            "DROP TABLE IF EXISTS " + CarteraAnalistaContract.CreditEntry.TABLE_NAME;


    private static final String DATABASE_NAME = "CarteraAnalista.db";
    private static final int DATABASE_VERSION = 1;

    public static final int FLAG_ACTUAL_ANALISTA = 0;
    public static final int FLAG_INSERTADO = 1;
    public static final int FLAG_ACTUALIZADO = 2;
    public static final int FLAG_OTRAS_CARTERAS_ANALISTA = 3;

    public static final int FLAG_INSERTAR_DIRECCION_EXITOSO = 1;
    public static final int FLAG_INSERTAR_DIRECCION_ERROR = 2;
    public static final int FLAG_INSERTAR_DIRECCION_ERROR_EXISTENTE = 3;

    public static final int FLAG_SYNCRONIZED = 1;
    public static final int FLAG_NOT_SYNCRONIZED = 2;



    public CarteraAnalistaDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CLIENT_ENTRY);
        db.execSQL(SQL_CREATE_TYPE_ADDRESS_ENTRY);
        db.execSQL(SQL_CREATE_CREDIT_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CLIENT_ENTRY);
        db.execSQL(SQL_DELETE_TYPE_ADDRESS_ENTRY);
        db.execSQL(SQL_DELETE_CREDIT_ENTRY);
        onCreate(db);
    }



    public Boolean insertCustomerPortafolio(Cliente cliente, int flag) {

        SQLiteDatabase db = getWritableDatabase();
        Boolean result = false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUM_PERS_COD, cliente.getPersCod());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_NAME, cliente.getNombre());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_DOI, cliente.getDoi());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_ONE, cliente.getTelefonoUno());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO, cliente.getTelefonoDos());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS_HOME, cliente.getDireccionDomicilio());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_GEOPOSITION, cliente.getGeoposicion());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_CREDITS, cliente.getCreditos());

        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE, cliente.getTelefono());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS, cliente.getIdTipoDireccion());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS, cliente.getReferencia());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE, cliente.getLongitud());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE, cliente.getLatitud());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, flag);
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUM_SYNCHRONIZATION, cliente.getSincronizar());

        try {
            long value = db.insert(
                    CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                    null,
                    contentValues
            );

            if (value != -1) { result = true; }

        } catch (Exception e) { result=false;}

        return result;
    }


    public Boolean insertCustomer(Cliente cliente, int flag) {

        SQLiteDatabase db = getWritableDatabase();
        Boolean result = false;

        ContentValues contentValues = new ContentValues();

        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_GEOPOSITION, "NO");
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_ONE, "");
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO, "");

        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_NAME, cliente.getNombre());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_DOI, cliente.getDoi());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE, cliente.getTelefono());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS, cliente.getIdTipoDireccion());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS, cliente.getReferencia());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE, cliente.getLongitud());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE, cliente.getLatitud());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, flag);
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUM_SYNCHRONIZATION, cliente.getSincronizar());

        try {
            long value = db.insert(
                    CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                    null,
                    contentValues
            );

            if (value != -1) { result = true; }

        } catch (Exception e) { result=false;}

        return result;
    }


    public ArrayList<Cliente> getAllCustomers(Context context) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,

                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO,

                CarteraAnalistaContract.ClientEntry.COLUMN_NAME,
                CarteraAnalistaContract.ClientEntry.COLUMN_DOI,
                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE,
                CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_FLAG,
                CarteraAnalistaContract.ClientEntry.COLUM_SYNCHRONIZATION

        };

        String selection = CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS + " != ? and "
                + CarteraAnalistaContract.ClientEntry.COLUM_SYNCHRONIZATION + " == ? ";

        String selectionArgs[] = { "0", String.valueOf(FLAG_NOT_SYNCRONIZED) };

        Cursor cursor =  db.query(
                CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        return listSynchronize(cursor, context);
    }

    private ArrayList<Cliente> listSynchronize(Cursor cursor, Context context) {

        ArrayList<Cliente> listCustomer = new ArrayList<>();

        String user = UPreferencias.ObtenerUserLogeo(context);

        while ( cursor.moveToNext() ) {
            Cliente cliente = new Cliente();

            cliente.setTelefonoDos(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO)));

            cliente.setId(cursor.getInt(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry._ID)));
            cliente.setNombre(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_NAME)));
            cliente.setDoi(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_DOI)));
            cliente.setTelefono(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE)));

            cliente.setIdTipoDireccion(cursor.getInt(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS)));
            cliente.setReferencia(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS)));
            cliente.setLatitud(cursor.getDouble(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE)));
            cliente.setLongitud(cursor.getDouble(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE)));
            cliente.setFlag(cursor.getInt(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG)));


            cliente.setUser(user);


            if (cliente.getTelefono()==null || cliente.getTelefono().isEmpty()) {
                cliente.setTelefono( cliente.getTelefonoDos()==null ? "" : cliente.getTelefonoDos() );
            }

            listCustomer.add(cliente);
        }

        return listCustomer;

    }


    public Cursor getAllCustomerFlagNotSyncronized() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,

                CarteraAnalistaContract.ClientEntry.COLUM_PERS_COD,
                CarteraAnalistaContract.ClientEntry.COLUMN_NAME,
                CarteraAnalistaContract.ClientEntry.COLUMN_DOI,
                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_ONE,
                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO,
                CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS_HOME,
                CarteraAnalistaContract.ClientEntry.COLUMN_GEOPOSITION,
                CarteraAnalistaContract.ClientEntry.COLUMN_CREDITS,


                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE,
                CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_FLAG,
                CarteraAnalistaContract.ClientEntry.COLUM_SYNCHRONIZATION
        };


        String selection = CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS + " != ? and "
                + CarteraAnalistaContract.ClientEntry.COLUM_SYNCHRONIZATION + " == ? ";

        String selectionArgs[] = { "0", String.valueOf(FLAG_NOT_SYNCRONIZED) };

        return  db.query(
                CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

    }


    private List<Cliente> listAllCustomerFlagNotSyncronized(Cursor cursor) {

        List<Cliente> listCustomer = new ArrayList<>();

        try {

            while ( cursor.moveToNext() ) {
                Cliente cliente = new Cliente();
                cliente.setId(cursor.getInt(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry._ID)));
                cliente.setPersCod(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUM_PERS_COD)));
                cliente.setNombre(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_NAME)));
                cliente.setDoi(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_DOI)));
                cliente.setTelefonoUno(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_ONE)));
                cliente.setTelefonoDos(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO)));
                cliente.setDireccionDomicilio(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS_HOME)));
                cliente.setGeoposicion(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_GEOPOSITION)));
                cliente.setCreditos(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_CREDITS)));

                cliente.setTelefono(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE)));
                cliente.setIdTipoDireccion(cursor.getInt(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS)));
                cliente.setReferencia(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS)));
                cliente.setLatitud(cursor.getDouble(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE)));
                cliente.setLongitud(cursor.getDouble(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE)));
                cliente.setFlag(cursor.getInt(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG)));
                cliente.setSincronizar(cursor.getInt(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUM_SYNCHRONIZATION)));

                listCustomer.add(cliente);
            }

        } catch (Exception e) {
            Log.d(TAG, "listCustomers: " + e.getMessage());
        }

        return listCustomer;

    }



    public void updateClientSynchronizationStatus(Context context) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;


        // TODO 1 obtener los clientes sincronizados
        Cursor cursor = getAllCustomerFlagNotSyncronized();
        List<Cliente> clienteList = listAllCustomerFlagNotSyncronized(cursor);

        // TODO 2 actualizar su estado (sincronizacion) a FLAG_SYNCRONIZED localmente
        try {


            db.beginTransaction();


            for (Cliente itemCliente: clienteList) {

                ContentValues contentValues = new ContentValues();
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUM_PERS_COD, itemCliente.getPersCod());
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_NAME, itemCliente.getNombre());
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_DOI, itemCliente.getDoi());
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_ONE, itemCliente.getTelefonoUno());
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO, itemCliente.getTelefonoDos());
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS_HOME, itemCliente.getDireccionDomicilio());
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_GEOPOSITION, itemCliente.getGeoposicion());
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_CREDITS, itemCliente.getCreditos());

                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE, itemCliente.getTelefono());
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS, itemCliente.getIdTipoDireccion());
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS, itemCliente.getReferencia());
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE, itemCliente.getLongitud());
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE, itemCliente.getLatitud());
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, itemCliente.getFlag());

                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUM_SYNCHRONIZATION, FLAG_SYNCRONIZED);


                String selection = CarteraAnalistaContract.ClientEntry._ID + " = ? ";
                String selectionArgs[] = { String.valueOf(itemCliente.getId()) };

                db.update(
                        CarteraAnalistaContract.ClientEntry.TABLE_NAME,
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
        //return result;
    }



    public Cursor getCustomers() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,

                CarteraAnalistaContract.ClientEntry.COLUM_PERS_COD,
                CarteraAnalistaContract.ClientEntry.COLUMN_NAME,
                CarteraAnalistaContract.ClientEntry.COLUMN_DOI,
                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_ONE,
                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO,
                CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS_HOME,
                CarteraAnalistaContract.ClientEntry.COLUMN_GEOPOSITION,
                CarteraAnalistaContract.ClientEntry.COLUMN_CREDITS,


                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE,
                CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_FLAG
        };

        String sortOrder =
                CarteraAnalistaContract.ClientEntry.COLUMN_NAME + " ASC";

        return  db.query(
                true,
                CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                projection,
                null,
                null,
                CarteraAnalistaContract.ClientEntry.COLUMN_DOI,
                null,
                sortOrder,
                null
        );

    }

    public ArrayList<Cliente> listCustomers(Cursor cursor) {

        ArrayList<Cliente> listCustomer = new ArrayList<>();

        try {

            while ( cursor.moveToNext() ) {
                Cliente cliente = new Cliente();
                cliente.setId(cursor.getInt(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry._ID)));
                cliente.setPersCod(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUM_PERS_COD)));
                cliente.setNombre(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_NAME)));
                cliente.setDoi(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_DOI)));
                cliente.setTelefonoUno(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_ONE)));
                cliente.setTelefonoDos(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO)));
                cliente.setDireccionDomicilio(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS_HOME)));
                cliente.setGeoposicion(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_GEOPOSITION)));
                cliente.setCreditos(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_CREDITS)));

                cliente.setTelefono(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE)));
                cliente.setIdTipoDireccion(cursor.getInt(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS)));
                cliente.setReferencia(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS)));
                cliente.setLatitud(cursor.getDouble(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE)));
                cliente.setLongitud(cursor.getDouble(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE)));
                cliente.setFlag(cursor.getInt(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG)));

                listCustomer.add(cliente);
            }

        } catch (Exception e) {
            Log.d(TAG, "listCustomers: " + e.getMessage());
        }

        return listCustomer;

    }

    public Cursor filterCustomers(String nameDoi) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,

                CarteraAnalistaContract.ClientEntry.COLUM_PERS_COD,
                CarteraAnalistaContract.ClientEntry.COLUMN_NAME,
                CarteraAnalistaContract.ClientEntry.COLUMN_DOI,
                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_ONE,
                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO,
                CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS_HOME,
                CarteraAnalistaContract.ClientEntry.COLUMN_GEOPOSITION,
                CarteraAnalistaContract.ClientEntry.COLUMN_CREDITS,

                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE,
                CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_FLAG
        };

        String selection = CarteraAnalistaContract.ClientEntry.COLUMN_NAME + " like ? or " +
                CarteraAnalistaContract.ClientEntry.COLUMN_DOI + " like ? ";

        String[] selectionArgs = { "%"+nameDoi+"%", "%"+nameDoi+"%" };

        String sortOrder =
                CarteraAnalistaContract.ClientEntry.COLUMN_NAME + " ASC";

        return db.query(
                true,
                CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                CarteraAnalistaContract.ClientEntry.COLUMN_DOI,
                null,
                sortOrder,
                null);

    }

    public Cursor getCustomer(String doi) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                CarteraAnalistaContract.ClientEntry.COLUMN_NAME,
                CarteraAnalistaContract.ClientEntry.COLUMN_DOI,
                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE,
                CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_FLAG
        };

        String selection = CarteraAnalistaContract.ClientEntry.COLUMN_DOI + " = ?";

        String[] selectionArgs = { doi };

        return  db.query(
                CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

    }

    public Cursor getAllAddressByCustomer(String doi) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,

                CarteraAnalistaContract.ClientEntry.COLUM_PERS_COD,
                CarteraAnalistaContract.ClientEntry.COLUMN_NAME,
                CarteraAnalistaContract.ClientEntry.COLUMN_DOI,
                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_ONE,
                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO,
                CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS_HOME,
                CarteraAnalistaContract.ClientEntry.COLUMN_GEOPOSITION,
                CarteraAnalistaContract.ClientEntry.COLUMN_CREDITS,

                CarteraAnalistaContract.ClientEntry.COLUMN_PHONE,
                CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_FLAG
        };

        String selection = CarteraAnalistaContract.ClientEntry.COLUMN_DOI + " = ? and (" +
                CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS + " = ? or " +
                CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS + " = ? )";

        String[] selectionArgs = { doi, String.valueOf(1), String.valueOf(2) };

        return  db.query(
                true,
                CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS,
                null,
                null,
                null
        );

    }

    public Cliente customer(Cursor cursor) {

        Cliente cliente = null;

        if (cursor.moveToNext()) {
            cliente = new Cliente();
            cliente.setNombre(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_NAME)));
            cliente.setDoi(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_DOI)));
            cliente.setTelefono(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE)));
        }

        return cliente;

    }

    public Cursor getAddress(int id) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE
        };

        String selection = BaseColumns._ID + " = ?";

        String[] selectionArgs = { Integer.toString(id) };

        return  db.query(
                CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

    }

    public ArrayList<Direccion> listAddress(Cursor cursor) {

        ArrayList<Direccion> listAddress = new ArrayList<>();

        while ( cursor.moveToNext() ) {

            Direccion direccion = new Direccion();
            direccion.setId( cursor.getInt(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS)) );
            direccion.setReferencia( cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS)) );
            direccion.setLatitud( cursor.getDouble(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE)) );
            direccion.setLongitud( cursor.getDouble(cursor.getColumnIndex(CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE)) );
            listAddress.add(direccion);

        }

        return listAddress;

    }

    public int insertAddress(Cliente cliente) {

        SQLiteDatabase db = getWritableDatabase();

        int result = FLAG_INSERTAR_DIRECCION_ERROR;

        ContentValues contentValues = new ContentValues();
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUM_PERS_COD, cliente.getPersCod());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_NAME, cliente.getNombre());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_DOI, cliente.getDoi());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_ONE, cliente.getTelefonoUno());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO, cliente.getTelefonoDos());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS_HOME, cliente.getDireccionDomicilio());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_GEOPOSITION, "SI");
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_CREDITS, cliente.getCreditos());

        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE, cliente.getTelefono());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS, cliente.getIdTipoDireccion());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS, cliente.getReferencia());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE, cliente.getLongitud());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE, cliente.getLatitud());

        if (cliente.getFlag() == FLAG_ACTUAL_ANALISTA || cliente.getFlag() == FLAG_ACTUALIZADO) {
            contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, FLAG_ACTUALIZADO);
        } else if (cliente.getFlag() == FLAG_INSERTADO){
            contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, FLAG_INSERTADO);
        } else {
            contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, FLAG_OTRAS_CARTERAS_ANALISTA);
        }

        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUM_SYNCHRONIZATION, FLAG_NOT_SYNCRONIZED);

        try {


            // TODO 1 obtenemos las direcciones del cliente
            Cursor cursor = getAllAddressByCustomer(cliente.getDoi());

            // TODO 2 validamos que no sea repetida
            ArrayList<Cliente> clienteArrayList = listCustomers(cursor);

            for (Cliente itemCliente : clienteArrayList) {
                if (itemCliente.getIdTipoDireccion() == cliente.getIdTipoDireccion()) {
                    result = FLAG_INSERTAR_DIRECCION_ERROR_EXISTENTE;
                    return result;
                }
            }


            // TODO 3 insertamos en la base de datos
            long value = db.insert(
                    CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                    null,
                    contentValues);

            if (value != -1) { result = FLAG_INSERTAR_DIRECCION_EXITOSO; }

        } catch (Exception e) {
            Log.d("TAG", "insertAddress: " + e.getMessage());
            result = FLAG_INSERTAR_DIRECCION_ERROR;
        }

        return result;
    }

    public int updateAddress(Cliente cliente) {

        SQLiteDatabase db = getReadableDatabase();
        int result;
        int resultUpdate = FLAG_INSERTAR_DIRECCION_ERROR;

        Log.d(TAG, "updateAddress: " + cliente.getIdTipoDireccion());

        ContentValues contentValues = new ContentValues();
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS, cliente.getIdTipoDireccion());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS, cliente.getReferencia());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE, cliente.getLatitud());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE, cliente.getLongitud());

        if (cliente.getFlag() == FLAG_ACTUAL_ANALISTA || cliente.getFlag() == FLAG_ACTUALIZADO) {
            contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, FLAG_ACTUALIZADO);
        } else if (cliente.getFlag() == FLAG_INSERTADO){
            contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, FLAG_INSERTADO);
        } else {
            contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, FLAG_OTRAS_CARTERAS_ANALISTA);
        }

        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUM_SYNCHRONIZATION, FLAG_NOT_SYNCRONIZED);

        String whereClauses = BaseColumns._ID + " = ?";
        String whereArgs[] = { String.valueOf(cliente.getId()) };

        result = db.update(
                CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                contentValues,
                whereClauses,
                whereArgs
        );

        if (result>0) {
            resultUpdate = FLAG_INSERTAR_DIRECCION_EXITOSO;
        }

        return resultUpdate;

    }

    public Boolean insertTypeAddress() {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues direccionDomicilio = new ContentValues();
        direccionDomicilio.put(BaseColumns._ID, 1);
        direccionDomicilio.put(CarteraAnalistaContract.TypeAddressEntry.COLUMN_NAME, "Dirección Domicilio");

        ContentValues direccionNegocio = new ContentValues();
        direccionNegocio.put(BaseColumns._ID, 2);
        direccionNegocio.put(CarteraAnalistaContract.TypeAddressEntry.COLUMN_NAME, "Dirección Negocio");


        db.insert(
                CarteraAnalistaContract.TypeAddressEntry.TABLE_NAME,
                null,
                direccionDomicilio
        );
        db.insert(
                CarteraAnalistaContract.TypeAddressEntry.TABLE_NAME,
                null,
                direccionNegocio
        );

        return false;
    }

    public int countListTypeAddress() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                CarteraAnalistaContract.TypeAddressEntry.COLUMN_NAME
        };

        String sortOrder =
                CarteraAnalistaContract.TypeAddressEntry.COLUMN_NAME + " ASC";

        Cursor cursor = db.query(
                CarteraAnalistaContract.TypeAddressEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        return cursor.getCount();
    }

    public Cursor getTypeAddress() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                CarteraAnalistaContract.TypeAddressEntry.COLUMN_NAME
        };

        String sortOrder =
                CarteraAnalistaContract.TypeAddressEntry.COLUMN_NAME + " ASC";


        return db.query(
                CarteraAnalistaContract.TypeAddressEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

    }

    public ArrayList<TipoDireccion> listTypeAddress(Cursor cursor) {

        ArrayList<TipoDireccion> tipoDireccions = new ArrayList<>();

        while( cursor.moveToNext() ) {
            TipoDireccion tipoDireccion = new TipoDireccion();
            tipoDireccion.setId(cursor.getInt(cursor.getColumnIndex(CarteraAnalistaContract.TypeAddressEntry._ID)));
            tipoDireccion.setName(cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.TypeAddressEntry.COLUMN_NAME)));
            tipoDireccions.add(tipoDireccion);
        }

        return tipoDireccions;
    }

    public Boolean updateCustomer(Cliente cliente) {

        SQLiteDatabase db = getWritableDatabase();

        int result;
        boolean resultUpdate = false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_NAME, cliente.getNombre());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_DOI, cliente.getDoi());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE, cliente.getTelefono());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, FLAG_ACTUALIZADO);

        if (cliente.getFlag() == FLAG_ACTUAL_ANALISTA || cliente.getFlag() == FLAG_ACTUALIZADO) {
            contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, FLAG_ACTUALIZADO);
        } else {
            contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, FLAG_INSERTADO);
        }

        String whereClauses = BaseColumns._ID + " = ?";
        String whereArgs[] = { String.valueOf(cliente.getId()) };

        result = db.update(
                CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                contentValues,
                whereClauses,
                whereArgs
        );

        if (result > 0) {
            resultUpdate = true;
        }

        return resultUpdate;

    }

    public Cursor searchCustomer(String doi) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS,
                CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE,
                CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE
        };

        String selection = CarteraAnalistaContract.ClientEntry.COLUMN_DOI + " = ?";

        String[] selectionArgs = { doi };


        return  db.query(
                CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

    }

    public boolean insertCustomer(JSONArray jsonCustomers) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {

            db.beginTransaction();

            for (int i = 0; i < jsonCustomers.length(); i++) {

                JSONObject row = jsonCustomers.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUM_PERS_COD, row.getString("PersCod"));
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_NAME, row.getString("PersNombre"));
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_DOI, row.getString("NumeroDocumento"));
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_ONE, row.getString("PersTelefono"));
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO, row.getString("PersTelefono2"));
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS_HOME, row.getString("PersDireccDomicilio"));
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_GEOPOSITION, row.getString("GeoPosicion"));
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_CREDITS, row.getString("Creditos"));

                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE, row.getString("Telefono"));
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS, row.getInt("TipoDireccion"));
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS, row.getString("Direccion"));
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE, row.getDouble("Longitud"));
                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE, row.getDouble("Latitud"));

                if (row.getString("CarteraAnalista").toUpperCase().equals("SI")) {
                    contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, FLAG_ACTUAL_ANALISTA);
                } else {
                    contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, FLAG_OTRAS_CARTERAS_ANALISTA);
                }

                contentValues.put(CarteraAnalistaContract.ClientEntry.COLUM_SYNCHRONIZATION, FLAG_SYNCRONIZED);

                db.insert(
                        CarteraAnalistaContract.ClientEntry.TABLE_NAME,
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

    public Cursor getCredit(String doi) {

        SQLiteDatabase db = getWritableDatabase();

        String[] projection = {
                BaseColumns._ID,
                CarteraAnalistaContract.CreditEntry.COLUMN_NAME,
                CarteraAnalistaContract.CreditEntry.COLUMN_CTA_COD,
                CarteraAnalistaContract.CreditEntry.COLUMN_STATUS,
                CarteraAnalistaContract.CreditEntry.COLUMN_AMOUNT
        };

        String selection = CarteraAnalistaContract.CreditEntry.COLUMN_DOI + " = ?";

        String[] selectionArgs = { doi };

        return db.query(
                CarteraAnalistaContract.CreditEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

    }

    public ArrayList<Credito> listCredit(Cursor cursor) {

        ArrayList<Credito> listCredit = new ArrayList<>();

        while ( cursor.moveToNext() ) {
            Credito credito = new Credito();

            credito.setNombre( cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.CreditEntry.COLUMN_NAME)) );
            credito.setNumero( cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.CreditEntry.COLUMN_CTA_COD)) );
            credito.setEstado( cursor.getString(cursor.getColumnIndex(CarteraAnalistaContract.CreditEntry.COLUMN_STATUS)) );
            credito.setMonto( cursor.getDouble(cursor.getColumnIndex(CarteraAnalistaContract.CreditEntry.COLUMN_AMOUNT)) );

            listCredit.add(credito);
        }

        return listCredit;
    }

    public boolean insertCredit(List<Credito> creditoList, String doi) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {
            db.beginTransaction();

            for (Credito credito : creditoList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(CarteraAnalistaContract.CreditEntry.COLUMN_NAME, credito.getNombre());
                contentValues.put(CarteraAnalistaContract.CreditEntry.COLUMN_CTA_COD, credito.getNumero());
                contentValues.put(CarteraAnalistaContract.CreditEntry.COLUMN_STATUS, credito.getEstado());
                contentValues.put(CarteraAnalistaContract.CreditEntry.COLUMN_DOI, doi);
                contentValues.put(CarteraAnalistaContract.CreditEntry.COLUMN_AMOUNT, credito.getMonto());


                db.insert(
                        CarteraAnalistaContract.CreditEntry.TABLE_NAME,
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



    public int deleteAllCustomer() {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                "1",
                null);

    }


    public boolean insertCustomerSearchName(Cliente cliente) {

        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUM_PERS_COD, cliente.getPersCod());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_NAME, cliente.getNombre());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_DOI, cliente.getDoi());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_ONE, cliente.getTelefonoUno());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE_TWO, cliente.getTelefonoDos());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS_HOME, cliente.getDireccionDomicilio());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_CREDITS, cliente.getCreditos());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_GEOPOSITION, cliente.getGeoposicion());

        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_PHONE, cliente.getTelefono());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS, cliente.getIdTipoDireccion());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_ADDRESS, cliente.getReferencia());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LONGITUDE, cliente.getLongitud());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_LATITUDE, cliente.getLatitud());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUMN_FLAG, cliente.getFlag());
        contentValues.put(CarteraAnalistaContract.ClientEntry.COLUM_SYNCHRONIZATION, FLAG_SYNCRONIZED);

        try {

            // TODO 1 obtenemos datos, si existe el cliente en la base de datos local
            Cursor cursor = getCustomer(cliente.getDoi());

            // TODO 2 validamos si existe el cliente
            if (cursor.getCount() == 0) {

                Log.d(TAG, "insertCustomerSearchName: 1");
                long value = db.insert(
                        CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                        null,
                        contentValues);

                if (value != -1) { result = true; }

            } else {

                // TODO 3 existe el cliente, no se debe de registrar
                Log.d(TAG, "insertCustomerSearchName: 2");
                result = true;
            }


        } catch (Exception e) {
            Log.d("TAG", "insertAddress: " + e.getMessage());
        }

        return result;

    }

    public void deleteCustomer(String doi) {

        SQLiteDatabase db = getWritableDatabase();

        String whereClause = CarteraAnalistaContract.ClientEntry.COLUMN_ID_TYPE_ADDRESS + " = ? and " +
                CarteraAnalistaContract.ClientEntry.COLUMN_DOI + " = ? ";

        String whereArgs[] = { String.valueOf(0), doi };

        int resultado = db.delete(
                CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                whereClause,
                whereArgs
        );

    }


    public Cursor customerInsertUpdate() {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                CarteraAnalistaContract.ClientEntry._ID
        };

        String selection = CarteraAnalistaContract.ClientEntry.COLUMN_FLAG + " = ? or " +
                CarteraAnalistaContract.ClientEntry.COLUMN_FLAG + " = ? ";

        String[] selectionArgs = { String.valueOf(FLAG_INSERTADO), String.valueOf(FLAG_ACTUALIZADO) };


        return db.query(
                CarteraAnalistaContract.ClientEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

    }



}