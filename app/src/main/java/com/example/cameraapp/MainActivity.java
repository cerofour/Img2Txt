package com.example.cameraapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

class LectorTexto7 {
    TextRecognizer scanerImagen =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

    final int NO_ROTACION = 0;

    public LectorTexto7() {
}

    public String ImgATextoDesdeBmp(Bitmap bmp) {
        InputImage inputImg = InputImage.fromBitmap(bmp, this.NO_ROTACION);
        return this.InputImgATexto(inputImg);
    }

    public String ImgATextoDesdeURI(Uri uri, Context contexto) {
        InputImage img;
        try {
            img = InputImage.fromFilePath(contexto, uri);
            return this.InputImgATexto(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No se pudo leer la imagen";
    }

    private String InputImgATexto(InputImage img) {
        Task<Text> resultado =
                this.scanerImagen.process(img)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {
                                // lectura correcta
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // error;
                                    }
                                }
                        );
        while (!resultado.isComplete());

        return resultado.getResult().getText();
    }

}

public class MainActivity extends AppCompatActivity {

    MaterialButton btnCamara;
    Button btnImage;

    Button btnMostrarTexto;
    ImageView visor;

    LectorTexto7 lectorTexto = new LectorTexto7();

    Intent intentMostrarTexto;

    private final ActivityResultLauncher<String> pickMedia =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    //imagen seleccionada
                    visor.setImageURI(uri);
                    this.intentMostrarTexto.putExtra("textoEscaneado", lectorTexto.ImgATextoDesdeURI(uri, getApplicationContext()));
                } else {
                    //no imagen
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.btnImage = findViewById(R.id.btnImage);
        this.visor = findViewById(R.id.iv_visor);

        this.btnImage.setOnClickListener((v -> pickMedia.launch("image/*")));

        this.btnCamara = findViewById(R.id.btnCamara);
        this.btnCamara.setOnClickListener(v -> abrirCamara());
        this.btnCamara.setIconResource(R.drawable.ic_camera);

        this.btnMostrarTexto = findViewById(R.id.mostrarTexto);

        this.intentMostrarTexto = new Intent(MainActivity.this, MostrarTexto.class);

        this.btnMostrarTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para lanzar la actividad de layout2
                startActivity(intentMostrarTexto);
            }
        });
    }

    //abre la camara
    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get("data");
            this.intentMostrarTexto.putExtra("textoEscaneado", lectorTexto.ImgATextoDesdeBmp(imgBitmap));
            visor.setImageBitmap(imgBitmap);
        }
    }

}


