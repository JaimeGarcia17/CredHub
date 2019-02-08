package com.example.credhub;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static android.media.MediaPlayer.*;

public class Inicio extends AppCompatActivity {

    private int time = 5000;
    MediaPlayer miCancion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        miCancion = MediaPlayer.create(Inicio.this, R.raw.siuuu);
        miCancion.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Inicio.this, ListadoDeCredenciales.class));
                miCancion.stop();
                finish();
            }
        },time);
    }
}
