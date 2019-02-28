package com.example.credhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Jaime García on 28,febrero,2019
 */

public class InicioSesion extends AppCompatActivity {

    Button login;
    EditText username, password;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        login = (Button)findViewById(R.id.login);
        username = (EditText)findViewById(R.id.inputUsername);
        password = (EditText)findViewById(R.id.inputPassword);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if(existeUsuario()){
                    startActivity(new Intent(InicioSesion.this, ListadoDeCredenciales.class));
                }
            }
        });
    }

    /*TODO*/
    private boolean existeUsuario(){
        return true;
    }
}
