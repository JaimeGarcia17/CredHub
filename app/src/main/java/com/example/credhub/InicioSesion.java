package com.example.credhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by Jaime García on 28,febrero,2019
 */

public class InicioSesion extends AppCompatActivity {

    Button login;
    EditText username, password;

    public static String usernameLogin = "";
    public static String passwordLogin = "";

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        login = (Button) findViewById(R.id.login);
        username = (EditText) findViewById(R.id.inputUsername);
        password = (EditText) findViewById(R.id.inputPassword);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                final String userString = username.getText().toString();
                final String passwordString = password.getText().toString();

                if (existeUsuario(userString, passwordString)) {
                    Intent intent = new Intent(InicioSesion.this, ListadoDeCredenciales.class);
                    usernameLogin = userString;
                    passwordLogin = passwordString;
                    startActivity(intent);
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
