package com.example.cameraapp;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MostrarTexto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.texto_mostrado);
        Intent intent = getIntent();
        String valorRecibido = intent.getStringExtra("textoEscaneado");

        TextView textView = findViewById(R.id.textoEscaneado);
        textView.setText(valorRecibido);

        Button btnCopy = findViewById(R.id.Copiar);

        btnCopy.setOnClickListener(v -> {
            // Obtener el servicio del portapapeles
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            // Copiar el texto al portapapeles
            clipboard.setText(valorRecibido);

            Toast.makeText(MostrarTexto.this, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show();
        });

    }
}
