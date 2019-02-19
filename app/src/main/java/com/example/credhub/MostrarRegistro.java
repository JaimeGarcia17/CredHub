package com.example.credhub;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.credhub.Model.Credenciales;

/**
 * Created by Jaime García on 02,febrero,2019
 */

public class MostrarRegistro extends AppCompatActivity {

    TextView id, username, textPassword;
    Button password, home, export, update, delete;
    DatabaseHelper databaseHelper = new DatabaseHelper(this, "credencialesDB", null, 1);

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_registro);

        id = (TextView) findViewById(R.id.muestraIdentificador);
        username = (TextView) findViewById(R.id.muestraUsername);
        textPassword = (TextView) findViewById(R.id.inputPassword);

        password = (Button) findViewById(R.id.muestraPassword);
        home = (Button) findViewById(R.id.goToHome);
        delete = (Button) findViewById(R.id.eliminarRegistro);
        update = (Button) findViewById(R.id.actualizarRegistro);
        export = (Button) findViewById(R.id.exportarRegistro);

        Bundle bundle = getIntent().getExtras();
        Credenciales miCredencial = null;

        if (bundle == null) {
            startActivity(new Intent(MostrarRegistro.this, ListadoDeCredenciales.class));
        }

        miCredencial = (Credenciales) bundle.getSerializable("credencial");
        id.setText(miCredencial.getId());
        username.setText(miCredencial.getUsername());
        textPassword.setText(miCredencial.getPassword());

        final Credenciales finalMiCredencial = miCredencial;
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Toast.makeText(getApplicationContext(), finalMiCredencial.getPassword(), Toast.LENGTH_SHORT).show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                borrarRegistro(finalMiCredencial);
                startActivity(new Intent(MostrarRegistro.this, ListadoDeCredenciales.class));
                finish();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent intent = new Intent(MostrarRegistro.this, ListadoDeCredenciales.class);
                startActivity(intent);
                finish();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                actualizarRegistro();
                startActivity(new Intent(MostrarRegistro.this, ListadoDeCredenciales.class));
            }
        });

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if(compruebaConexion()){
                    exportarRegistro(finalMiCredencial.getId(), finalMiCredencial.getUsername(), finalMiCredencial.getPassword());
                }

            }
        });


    }

    private void actualizarRegistro() {

        try {

            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Constantes.COLUMN_USERNAME, username.getText().toString());
            contentValues.put(Constantes.COLUMN_PASSWORD, textPassword.getText().toString());

            sqLiteDatabase.update(Constantes.TABLE_NAME, contentValues, Constantes.COLUMN_ID + " = ?", new String[]{id.getText().toString()});
            Log.i(Constantes.LOG_DB, "UPDATE REGISTER WITH ID = " + id.getText().toString());

            Toast.makeText(this, "Se ha actualizado correctamente!", Toast.LENGTH_SHORT).show();
            sqLiteDatabase.close();

        } catch (SQLiteException exception) {
            exception.printStackTrace();
            Log.e(Constantes.LOG_DB_ERROR, "UPDATE REGISTER WITH ID = " + id.getText().toString());
        }
    }

    private void borrarRegistro( Credenciales miCredencial ) {
        try {

            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            sqLiteDatabase.delete(Constantes.TABLE_NAME, Constantes.COLUMN_ID + " = ?", new String[]{miCredencial.getId()});
            Log.i(Constantes.LOG_DB, "DELETE REGISTER WITH ID = " + miCredencial.getId());
            sqLiteDatabase.close();

        } catch (SQLiteException exception) {
            exception.printStackTrace();
            Log.e(Constantes.LOG_DB_ERROR, "DELETE REGISTER WITH ID = " + miCredencial.getId());
        }
    }

    private void exportarRegistro( String id, String username, String password ) {

        final String args[] = {"http"};
        final String idFinal = id;
        final String usernameFinal = username;
        final String passwordFinal = password;


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                EndPoint endPoint = new EndPoint();
                endPoint.establecerConexion(args);
                endPoint.exportarRegistro(idFinal, usernameFinal, passwordFinal);
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

        Toast.makeText(this, "Se ha exportado correctamente!", Toast.LENGTH_SHORT).show();
    }

    private boolean compruebaConexion(){

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
