package com.example.credhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ListadoDeCredenciales extends AppCompatActivity {

    Button anadirRegistro, importarRegistro, bt1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_de_credenciales);

        anadirRegistro = (Button)findViewById(R.id.anadirRegistro);
        importarRegistro = (Button)findViewById(R.id.importarRegistro);
        bt1 = (Button)findViewById(R.id.mostrarPrueba);

        anadirRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListadoDeCredenciales.this, AnadirRegistro.class));
            }
        });

        importarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListadoDeCredenciales.this,ImportarRegistro.class));
            }
        });

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListadoDeCredenciales.this,MostrarRegistro.class));
            }
        });

    }


}
