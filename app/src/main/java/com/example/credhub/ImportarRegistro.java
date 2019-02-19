package com.example.credhub;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.IpSecManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.credhub.Model.Credenciales;

import org.ksoap2.serialization.SoapPrimitive;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.locks.Lock;

/**
 * Created by Jaime García on 02,febrero,2019
 */


public class ImportarRegistro extends AppCompatActivity {

    DatabaseHelper databaseHelper = new DatabaseHelper(this, "credencialesDB", null, 1);

    Vector<SoapPrimitive> vectorListaCredenciales;
    Vector<SoapPrimitive> vectorImportarRegistro;

    final ArrayList<String> arrayListDescripciones = new ArrayList<String>();
    ArrayList<String> arrayListResultado = new ArrayList<String>();
    String[] resultado = new String[3];
    EndPoint endPoint = new EndPoint();
    String[] args = {"http"};

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importar_registro);

        ListView listaCredenciales = (ListView) findViewById(R.id.listaCredencialesRepositorio);

        endPoint.establecerConexion(args);
        listaCredenciales();


        if (arrayListDescripciones != null) {
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListDescripciones);
            listaCredenciales.setAdapter(adapter);

            listaCredenciales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                    String identificador = arrayListDescripciones.get(position);
                    importaRegistro(identificador);
                    saveRegister(resultado[0], resultado[1], resultado[2]);
                    startActivity(new Intent(ImportarRegistro.this, ListadoDeCredenciales.class));
                }
            });
        }
    }


    private void listaCredenciales() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                endPoint.establecerConexion(args);
                vectorListaCredenciales = endPoint.listarRegistros();

                for (int i = 0; i < vectorListaCredenciales.size(); i++) {
                    arrayListDescripciones.add(vectorListaCredenciales.get(i).toString());
                }
            }
        });

        try {
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalThreadStateException i) {
            i.printStackTrace();
        }
    }


    private void importaRegistro( final String ident ) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                vectorImportarRegistro = endPoint.importarRegistro(ident);

                resultado[0] = (String) vectorImportarRegistro.get(0).getValue();
                resultado[1] = (String) vectorImportarRegistro.get(1).getValue();
                resultado[2] = (String) vectorImportarRegistro.get(2).getValue();
            }
        });

        try {
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalThreadStateException i) {
            i.printStackTrace();
        }
    }

    private void saveRegister( String id, String username, String password ) {

        if (!existeResultado(id) && !esNulo(id, username, password)) {
            insertaRegistro(id, username, password);
        } else if (existeResultado(id) && !esNulo(id, username, password)) {
            actualizarRegistro(id, username, password);
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean existeResultado( String identificador ) {

        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT id FROM " + Constantes.TABLE_NAME + " WHERE id = '" + identificador + "'", null);

        if (cursor.getCount() == 1) {
            return true;
        }
        return false;
    }

    private void insertaRegistro( String id, String username, String password ) {

        try {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Constantes.COLUMN_ID, id);
            contentValues.put(Constantes.COLUMN_USERNAME, username);
            contentValues.put(Constantes.COLUMN_PASSWORD, password);

            Long fila = sqLiteDatabase.insert(Constantes.TABLE_NAME, Constantes.COLUMN_ID, contentValues);

            Log.i(Constantes.LOG_DB, "ROW[" + fila + "] - [" + id + " - " + username
                    + " - " + password + "]");

            if (fila == Constantes.EMPTY) {
                Toast.makeText(this, "Error al almacenar en la BD!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Se ha almacenado correctamente!", Toast.LENGTH_SHORT).show();
            sqLiteDatabase.close();

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

    }


    private void actualizarRegistro( String id, String username, String password ) {

        try {

            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Constantes.COLUMN_USERNAME, username);
            contentValues.put(Constantes.COLUMN_PASSWORD, password);

            sqLiteDatabase.update(Constantes.TABLE_NAME, contentValues, Constantes.COLUMN_ID + " = ?", new String[]{id});
            Log.i(Constantes.LOG_DB, "UPDATE REGISTER WITH ID = " + id);

            Toast.makeText(this, "Se ha actualizado correctamente!", Toast.LENGTH_SHORT).show();
            sqLiteDatabase.close();

        } catch (SQLiteException exception) {
            exception.printStackTrace();
            Log.e(Constantes.LOG_DB_ERROR, "UPDATE REGISTER WITH ID = " + id);
        }
    }

    private boolean esNulo( String id, String username, String password ) {

        if ((id == null)) {
            return true;
        } else if (username == null) {
            return true;
        } else if (password == null) {
            return true;
        }
        return false;
    }


}
