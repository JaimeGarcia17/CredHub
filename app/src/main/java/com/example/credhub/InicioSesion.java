package com.example.credhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;

/**
 * Created by Jaime García on 28,febrero,2019
 */

public class InicioSesion extends AppCompatActivity {

    Button login;
    EditText username, password;
    KeyStore keyStore;
    Spinner spinner;

    public static String usernameLogin = "";
    public static String passwordLogin = "";
    public static String modoComunicacion = "";

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        login = (Button) findViewById(R.id.login);
        username = (EditText) findViewById(R.id.inputUsername);
        password = (EditText) findViewById(R.id.inputPassword);

        spinner  = (Spinner) findViewById(R.id.spinner);
        String[] comunicacion = {"http","http+auth","https+auth"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, comunicacion));


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected( AdapterView<?> parent, View view, int position, long id ) {
                modoComunicacion = (String) spinner.getAdapter().getItem(position);
            }

            @Override
            public void onNothingSelected( AdapterView<?> parent ) {
                modoComunicacion = "http";
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                final String userString = username.getText().toString();
                final String passwordString = password.getText().toString();

                if (existeUsuario(userString, passwordString)) {
                    Intent intent = new Intent(InicioSesion.this, ListadoDeCredenciales.class);
                    usernameLogin = userString;
                    passwordLogin = passwordString;
                    GestionClaves gestionClaves = new GestionClaves();
                    gestionClaves.loadKeyStore();
                    try {
                        if(gestionClaves.loadPrivateKey(GestionClaves.KEY_ALIAS) == null){
                            gestionClaves.generateNewKeyPair(GestionClaves.KEY_ALIAS,InicioSesion.this);
                        }else{
                            gestionClaves.loadKeyStore();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String clave = gestionClaves.encryptString(passwordLogin);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean existeUsuario( String user, String pass ) {

        String passH = "";
        try {
            passH = hashPassword(pass);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (user.equals(getString(R.string.username)) && passH.equals(getString(R.string.passwordHash))) {
            Toast.makeText(this, "Inicio de sesión correcto!", Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(this, "Inicio de sesión incorrecto!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private String hashPassword( String pass ) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA256");
        md.reset();
        byte[] buffer = pass.getBytes("UTF-8");
        md.update(buffer);
        byte[] digest = md.digest();

        String hexStr = "";
        for (int i = 0; i < digest.length; i++) {
            hexStr += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
        }
        return hexStr;
    }
}
