package com.example.credhub;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Jaime García on 02,febrero,2019
 */

public class AnadirRegistro extends AppCompatActivity {

    EditText id, username, password;
    Button saveRegister, randomPassword;
    DatabaseHelper databaseHelper = new DatabaseHelper(this, "credencialesDB", null, 1);

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_registro);

        id = (EditText) findViewById(R.id.inputIdentificador);
        username = (EditText) findViewById(R.id.inputUsername);
        password = (EditText) findViewById(R.id.inputPassword);
        saveRegister = (Button) findViewById(R.id.botonAnadirRegistro);
        randomPassword = (Button) findViewById(R.id.generaPassword);

        SharedPreferences sharedPreferences = getSharedPreferences(Constantes.PREFERENCES,MODE_PRIVATE);
        String usernameString = sharedPreferences.getString("username","No username defined");
        String passwordString = sharedPreferences.getString("password","No password defineds");

        if(usernameString!=null && passwordString!=null){
            username.setText(usernameString);
            password.setText(passwordString);
        }

        saveRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {

                if (checkInputs() == Constantes.OK && !existeResultado(id.getText().toString())) {
                    saveRegister();
                    startActivity(new Intent(AnadirRegistro.this, ListadoDeCredenciales.class));
                    finish();
                }
            }
        });

        randomPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                password.setText(generateRandomPassword());
            }
        });

    }

    private void saveRegister() {

        try {


            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Constantes.COLUMN_ID, id.getText().toString());
            contentValues.put(Constantes.COLUMN_USERNAME, username.getText().toString());
            contentValues.put(Constantes.COLUMN_PASSWORD, password.getText().toString());

            Long fila = sqLiteDatabase.insert(Constantes.TABLE_NAME, Constantes.COLUMN_ID, contentValues);

            Log.i(Constantes.LOG_DB, "ROW[" + fila + "] - [" + id.getText().toString() + " - " + username.getText().toString()
                    + " - " + password.getText().toString() + "]");

            if (fila == Constantes.EMPTY) {
                Toast.makeText(this, "Error al almacenar en la BD!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Se ha almacenado correctamente!", Toast.LENGTH_SHORT).show();
            sqLiteDatabase.close();

            SharedPreferences.Editor editor = getSharedPreferences(Constantes.PREFERENCES,MODE_PRIVATE).edit();
            editor.putString("username",username.getText().toString());
            editor.putString("password",password.getText().toString());
            editor.apply();


        } catch (SQLiteException exception) {
            exception.printStackTrace();
            Log.e(Constantes.LOG_DB_ERROR, "[" + id.getText().toString() + " - " + username.getText().toString()
                    + " - " + password.getText().toString() + "]");
        }
    }

    private String generateRandomPassword() {

        String randomPassword;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            for (int i = 0; i < Constantes.LENGTH; i++) {
                stringBuilder.append((char) ThreadLocalRandom.current().nextInt(Constantes.MIN, Constantes.MAX));
            }
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
            Log.e(Constantes.LOG_PASSWORD_ERROR, "Error al generar");
        }

        randomPassword = stringBuilder.toString();
        Log.i(Constantes.LOG_PASSWORD, "Valor: " + randomPassword);
        return randomPassword;

    }

    private int checkInputs() {

        String user = username.getText().toString();
        String pass = password.getText().toString();
        String description = id.getText().toString();

        try {

            if (user.matches("") || pass.matches("") || description.matches("")) {
                Toast.makeText(getApplicationContext(), "Rellene todos los campos!", Toast.LENGTH_SHORT).show();
                return Constantes.EMPTY;
            }
            return Constantes.OK;

        } catch (PatternSyntaxException exception) {
            exception.printStackTrace();
            return Constantes.EMPTY;
        }


    }

    private boolean existeResultado( String identificador ) {

        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT id FROM " + Constantes.TABLE_NAME + " WHERE id = '" + identificador + "'", null);

        if (cursor.getCount() == 1) {
            Toast.makeText(this,"Ya existe ese id!",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


}
