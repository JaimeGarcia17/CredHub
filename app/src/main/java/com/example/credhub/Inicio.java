package com.example.credhub;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static android.media.MediaPlayer.*;

/**
 * Created by Jaime García on 02,febrero,2019
 */

public class Inicio extends AppCompatActivity {

    private int time = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Inicio.this, InicioSesion.class));
                finish();
            }
        },time);
    }
}
