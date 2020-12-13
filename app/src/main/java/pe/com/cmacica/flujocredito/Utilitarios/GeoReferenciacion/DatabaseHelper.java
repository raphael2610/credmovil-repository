package pe.com.cmacica.flujocredito.Utilitarios.GeoReferenciacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;

import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.GeoRefClienteModel;
import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.TipoDireccionModel;

/**
 * by MFPE - 2019.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    class TipoDireccionEntry {
        String TableName = "TipoDireccion";
        String Id = "Id";
        String Descripcion = "Descripcion";
    }

    class ClienteEntry {
        String TableName = "Clientes";
        String IdCliente = "IdCliente";
        String Nombre = "Nombre";
        String Doi = "Doi";
        String IdTipoDireccion = "IdTipoDireccion";
        String Telefono = "Telefono";
        String Longitud = "Longitud";
        String Latitud = "Latitud";
        String Referencia = "Referencia";
    }

    private Context context;
    private TipoDireccionEntry tbTipoDir = new TipoDireccionEntry();
    private ClienteEntry tbClientes = new ClienteEntry();


    public DatabaseHelper(Context context, String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sqlCreate1 = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT NOT NULL,UNIQUE(%s));", tbTipoDir.TableName, tbTipoDir.Id, tbTipoDir.Descripcion, tbTipoDir.Id);
            String sqlCreate2 = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL,  %s INTEGER NOT NULL, %s TEXT NOT NULL, %s NUMERIC NOT NULL, %s NUMERIC NOT NULL, %s TEXT, UNIQUE(%s),FOREIGN KEY(%s) REFERENCES %s(%s));",
                    tbClientes.TableName, tbClientes.IdCliente, tbClientes.Nombre, tbClientes.Doi, tbClientes.IdTipoDireccion,
                    tbClientes.Telefono, tbClientes.Longitud, tbClientes.Latitud, tbClientes.Referencia, tbClientes.IdCliente, tbClientes.IdTipoDireccion, tbTipoDir.TableName, tbTipoDir.Id);

            db.execSQL(sqlCreate1);
            db.execSQL(sqlCreate2);

        } catch (Exception ex){
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", tbTipoDir.TableName));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", tbClientes.TableName));
        onCreate(db);
    }

    public Boolean InsertarTipoDireccion(TipoDireccionModel tipoDireccion){
        ContentValues contenido = new ContentValues();
        contenido.put(tbTipoDir.Id, tipoDireccion.getId());
        contenido.put(tbTipoDir.Descripcion, tipoDireccion.getDescripcion());
        try {
            this.getWritableDatabase().insert(tbTipoDir.TableName, null, contenido);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }

    public ArrayList<TipoDireccionModel> ListarTipoDireccion(){
        ArrayList<TipoDireccionModel> lista = new ArrayList<>();
        Cursor cursor = this.getWritableDatabase().query(tbTipoDir.TableName,null,null,null,null,null,null);
        while(cursor.moveToNext()){
            TipoDireccionModel tipoDireccion = new TipoDireccionModel(cursor.getInt(cursor.getColumnIndex(tbTipoDir.Id)),
                                                                        cursor.getString(cursor.getColumnIndex(tbTipoDir.Descripcion)));
            lista.add(tipoDireccion);
        }
        return lista;
    }

    public Boolean InsertarCliente(GeoRefClienteModel cliente){
        ContentValues contenido = new ContentValues();
        contenido.put(tbClientes.Nombre,cliente.getNombres());
        contenido.put(tbClientes.IdTipoDireccion,cliente.getIdTipoDireccion());
        contenido.put(tbClientes.Telefono,cliente.getTelefono());
        contenido.put(tbClientes.Doi,cliente.getDoi());
        contenido.put(tbClientes.Longitud,cliente.getLongitud());
        contenido.put(tbClientes.Latitud,cliente.getLatitud());
        contenido.put(tbClientes.Referencia, cliente.getReferencia());
        try {
            this.getWritableDatabase().insert(tbClientes.TableName,null,contenido);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }

    public ArrayList<GeoRefClienteModel> ListarClientes(){
        ArrayList<GeoRefClienteModel> lista = new ArrayList<>();
        String sqlQuery = String.format("SELECT DISTINCT %s, %s, %s FROM %s", tbClientes.Nombre, tbClientes.Doi, tbClientes.Telefono, tbClientes.TableName);
        Cursor cursor = this.getWritableDatabase().rawQuery(sqlQuery,null);
        while(cursor.moveToNext()){
            GeoRefClienteModel cliente = new GeoRefClienteModel();
            try {
                cliente.setNombres(cursor.getString(cursor.getColumnIndex(tbClientes.Nombre)));
                cliente.setDoi(cursor.getString(cursor.getColumnIndex(tbClientes.Doi)));
                cliente.setTelefono(cursor.getString(cursor.getColumnIndex(tbClientes.Telefono)));
                lista.add(cliente);
            }
            catch (Exception ex){

            }
        }
        return lista;
    }

    public ArrayList<String> ListarDireccionesPorDoi(String doi){
        ArrayList<String> lista = new ArrayList<>();
        String sqlQuery = String.format("SELECT %s.%s, %s.%s FROM %s JOIN %s ON %s.%s = %s.%s WHERE %s.%s = '%s'", tbClientes.TableName, tbClientes.IdCliente, tbTipoDir.TableName, tbTipoDir.Descripcion, tbClientes.TableName, tbTipoDir.TableName, tbClientes.TableName, tbClientes.IdTipoDireccion, tbTipoDir.TableName, tbTipoDir.Id, tbClientes.TableName, tbClientes.Doi, doi);
        Cursor cursor = this.getWritableDatabase().rawQuery(sqlQuery,null);
        while (cursor.moveToNext()){
            lista.add(cursor.getInt(0) + " - " + cursor.getString(1));
        }
        return lista;
    }

    public GeoRefClienteModel ObtenerClientePorId(int id, int idTipoDireccion){
        GeoRefClienteModel cliente = new GeoRefClienteModel();
        String sqlQuery = String.format("SELECT * FROM %s JOIN %s ON %s.%s = %s.%s WHERE %s = %s AND %s = %s", tbClientes.TableName, tbTipoDir.TableName, tbClientes.TableName, tbClientes.IdTipoDireccion, tbTipoDir.TableName, tbTipoDir.Id, tbClientes.IdCliente, id, tbClientes.IdTipoDireccion, idTipoDireccion);
        Cursor cursor = this.getWritableDatabase().rawQuery(sqlQuery,null);
        while (cursor.moveToNext()){
            cliente.setIdCliente(cursor.getInt(cursor.getColumnIndex(tbClientes.IdCliente)));
            cliente.setNombres(cursor.getString(cursor.getColumnIndex(tbClientes.Nombre)));
            cliente.setDoi(cursor.getString(cursor.getColumnIndex(tbClientes.Doi)));
            cliente.setTelefono(cursor.getString(cursor.getColumnIndex(tbClientes.Telefono)));
            cliente.setIdTipoDireccion(cursor.getInt(cursor.getColumnIndex(tbClientes.IdTipoDireccion)));
            cliente.setTipoDireccion(cursor.getString(cursor.getColumnIndex(tbTipoDir.Descripcion)));
            cliente.setLongitud(cursor.getDouble(cursor.getColumnIndex(tbClientes.Longitud)));
            cliente.setLatitud(cursor.getDouble(cursor.getColumnIndex(tbClientes.Latitud)));
            cliente.setReferencia(cursor.getString(cursor.getColumnIndex(tbClientes.Referencia)));
        }
        return cliente;
    }

    public GeoRefClienteModel ObtenerClientePorDoi(String doi){
        GeoRefClienteModel cliente = null;
        String sqlQuery = String.format("SELECT DISTINCT %s, %s, %s FROM %s WHERE %s = '%s'", tbClientes.Nombre, tbClientes.Doi, tbClientes.Telefono, tbClientes.TableName, tbClientes.Doi, doi);
        Cursor cursor = this.getWritableDatabase().rawQuery(sqlQuery,null);
        while (cursor.moveToNext()){
            cliente = new GeoRefClienteModel();
            cliente.setNombres(cursor.getString(cursor.getColumnIndex(tbClientes.Nombre)));
            cliente.setDoi(cursor.getString(cursor.getColumnIndex(tbClientes.Doi)));
            cliente.setTelefono(cursor.getString(cursor.getColumnIndex(tbClientes.Telefono)));
        }
        return cliente;
    }

    public Boolean EliminarCliente(GeoRefClienteModel cliente){
        String sqlQuery = String.format("DELETE FROM %s WHERE %s = '%s' AND %s = %s", tbClientes.TableName, tbClientes.Doi, cliente.getDoi(), tbClientes.IdTipoDireccion, cliente.getIdTipoDireccion());
        try {
            this.getWritableDatabase().execSQL(sqlQuery,new String[]{});
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }

    public Boolean ActualizarCliente(GeoRefClienteModel cliente){
        String sqlQuery = String.format("UPDATE %s SET %s = %s, %s = %s, %s = '%s' WHERE %s = '%s' AND %s = %s", tbClientes.TableName, tbClientes.Longitud, cliente.getLongitud(), tbClientes.Latitud, cliente.getLatitud(), tbClientes.Referencia, cliente.getReferencia(), tbClientes.Doi, cliente.getDoi(), tbClientes.IdTipoDireccion, cliente.getIdTipoDireccion());
        try {
            this.getWritableDatabase().execSQL(sqlQuery,new String[]{});
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }

    public boolean GuardarCliente(GeoRefClienteModel cliente){
        String sqlQuery = String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = %s",tbClientes.TableName, tbClientes.Doi, cliente.getDoi(), tbClientes.IdTipoDireccion, cliente.getIdTipoDireccion());
        Cursor cursor = this.getWritableDatabase().rawQuery(sqlQuery,null);
        if(cursor.getCount() > 0){
            return ActualizarCliente(cliente);
        }
        else{
            return  InsertarCliente(cliente);
        }
    }

    public boolean EliminarClientes(){
        try {
            this.getWritableDatabase().delete(tbClientes.TableName,null,null);
            this.getWritableDatabase().execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + tbClientes.TableName + "'");
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }

    public ArrayList<GeoRefClienteModel> ListarClienteAll(){
        ArrayList<GeoRefClienteModel> lista = new ArrayList<>();
        String sqlQuery = String.format("SELECT * FROM %s", tbClientes.TableName);
        Cursor cursor = this.getWritableDatabase().rawQuery(sqlQuery,null);
        while(cursor.moveToNext()){
            GeoRefClienteModel cliente = new GeoRefClienteModel();
            try {
                cliente.setIdCliente(cursor.getInt(cursor.getColumnIndex(tbClientes.IdCliente)));
                cliente.setNombres(cursor.getString(cursor.getColumnIndex(tbClientes.Nombre)));
                cliente.setDoi(cursor.getString(cursor.getColumnIndex(tbClientes.Doi)));
                cliente.setTelefono(cursor.getString(cursor.getColumnIndex(tbClientes.Telefono)));
                cliente.setIdTipoDireccion(cursor.getInt(cursor.getColumnIndex(tbClientes.IdTipoDireccion)));
                cliente.setLongitud(cursor.getDouble(cursor.getColumnIndex(tbClientes.Longitud)));
                cliente.setLatitud(cursor.getDouble(cursor.getColumnIndex(tbClientes.Latitud)));
                cliente.setReferencia(cursor.getString(cursor.getColumnIndex(tbClientes.Referencia)));
                lista.add(cliente);
            }
            catch (Exception ex){

            }
        }
        return lista;
    }

}