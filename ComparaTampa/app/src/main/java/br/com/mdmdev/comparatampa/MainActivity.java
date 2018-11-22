package br.com.mdmdev.comparatampa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class MainActivity extends AppCompatActivity {

    ImageView imageViewFoto;
    TextView txtResultado;
    Bitmap image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},0);
        }

        txtResultado = (TextView) findViewById(R.id.txtResultado);
        imageViewFoto = (ImageView) findViewById(R.id.imgFoto);
        findViewById(R.id.btnFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tirarFoto();
            }
        });


        findViewById(R.id.btnProcess2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTextFromImage();
            }
        });

    }


    // tirar foto
    public void tirarFoto() {
        Intent intentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentFoto, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            this.image = (Bitmap) extras.get("data");
            imageViewFoto.setImageBitmap(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void getTextFromImage() {
        // instacia o ocr do google
        TextRecognizer ocr = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!ocr.isOperational()) {
            Toast.makeText(getApplicationContext(), "O OCR não funcionou!", Toast.LENGTH_LONG).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(this.image).build();

            SparseArray<TextBlock> itens = ocr.detect(frame);

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < itens.size(); i++) {
                TextBlock myItem = itens.valueAt(i);
                sb.append(myItem.getValue());
                sb.append("\n");
            }

            this.txtResultado.setText(sb.toString());
        }
    }

}
