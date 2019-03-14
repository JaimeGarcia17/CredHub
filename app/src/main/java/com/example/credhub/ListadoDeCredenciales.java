package com.example.credhub;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.credhub.Model.Credenciales;

import java.util.ArrayList;

/**
 * Created by Jaime García on 02,febrero,2019
 */

public class ListadoDeCredenciales extends AppCompatActivity {

    Button anadirRegistro, importarRegistro, eliminarRegistro;
    ListView listaCredenciales;
    ArrayList<String> arrayListDescripciones;
    ArrayList<Credenciales> arrayListCredenciales;
    DatabaseHelper databaseHelper = new DatabaseHelper(this, "credencialesDB", null, 1);


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_de_credenciales);

        anadirRegistro = (Button) findViewById(R.id.anadirRegistro);
        importarRegistro = (Button) findViewById(R.id.importarRegistro);
        eliminarRegistro = (Button) findViewById(R.id.eliminarRegistro);
        listaCredenciales = (ListView) findViewById(R.id.listaCredenciales);

        consultaCredenciales();

        if (arrayListCredenciales != null) {
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListDescripciones);
            listaCredenciales.setAdapter(adapter);

            listaCredenciales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                    Credenciales miCredencial = arrayListCredenciales.get(position);

                    Intent intent = new Intent(ListadoDeCredenciales.this, MostrarRegistro.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("credencial", miCredencial);
                    intent.putExtras(bundle);

                    startActivity(intent);
                    finish();
                }
            });

        }

        anadirRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                startActivity(new Intent(ListadoDeCredenciales.this, AnadirRegistro.class));
                finish();
            }
        });

        importarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if (compruebaConexion()) {
                    startActivity(new Intent(ListadoDeCredenciales.this, ImportarRegistro.class));
                    finish();
                } else {
                    Toast.makeText(ListadoDeCredenciales.this, "El repositorio está fuera de servicio!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        eliminarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                eliminarRegistros();
                startActivity(new Intent(ListadoDeCredenciales.this, ListadoDeCredenciales.class));
                finish();
            }
        });

    }


    private void consultaCredenciales() {

        try {

            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(Constantes.FIND_ALL, null);

            Credenciales miCredencial;
            arrayListCredenciales = new ArrayList<Credenciales>();

            while (cursor.moveToNext()) {
                miCredencial = new Credenciales();
                miCredencial.setId(cursor.getString(0));
                miCredencial.setUsername(cursor.getString(1));
                miCredencial.setPassword(cursor.getString(2));

                arrayListCredenciales.add(miCredencial);
            }

            actualizarListaDescripciones();

            cursor.close();
            db.close();

        } catch (SQLException exception) {
            exception.printStackTrace();
            Log.e(Constantes.LOG_DB_ERROR, "SELECT * FROM " + Constantes.TABLE_NAME);
        }

    }


    private void actualizarListaDescripciones() {

        arrayListDescripciones = new ArrayList<String>();

        for (int i = 0; i < arrayListCredenciales.size(); i++) {
            arrayListDescripciones.add(arrayListCredenciales.get(i).getId());
        }
    }

    private void eliminarRegistros() {

        try {

            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            sqLiteDatabase.delete(Constantes.TABLE_NAME, null, null);
            Log.i(Constantes.LOG_DB, "DELETE FROM " + Constantes.TABLE_NAME);
            sqLiteDatabase.close();

        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e(Constantes.LOG_DB_ERROR, "DELETE FROM " + Constantes.TABLE_NAME);
        }

    }

    private boolean compruebaConexion() {

        final boolean[] value = new boolean[1];
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                EndPoint endPoint = new EndPoint();
                value[0] = endPoint.enLinea();
            }
        });

        try {
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return value[0];
    }

}
