package br.com.mdmdev.comparatampa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class MainActivity extends AppCompatActivity {

    ImageView imageViewFoto; // ocr
    ImageView imageViewFoto2; // datamatix
    TextView txtResultado;
    TextView txtBarcode;
    TextView txtFinal;
    Bitmap image;
    Bitmap image2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},0);
        }

        txtResultado = (TextView) findViewById(R.id.txtResultado);
        txtBarcode = (TextView) findViewById(R.id.txtBarcode);
        txtFinal = (TextView) findViewById(R.id.txtFinal);

        imageViewFoto = (ImageView) findViewById(R.id.imgFoto);
        imageViewFoto2 = (ImageView) findViewById(R.id.imgFoto2);

        // btn foto ocr

        findViewById(R.id.btnFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tirarFoto(1);
            }
        });

        // btn foto datamatrix
        findViewById(R.id.btnFoto2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tirarFoto(2);
            }
        });



        // processa os dados e compara ambos
        findViewById(R.id.btnProcess2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTextFromImage();
                getBarcode();
                compara();
            }
        });

    }


    // tirar foto
    public void tirarFoto(int cod) {
        Intent intentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentFoto, cod);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            this.image = (Bitmap) extras.get("data");
            imageViewFoto.setImageBitmap(image);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            this.image2 = (Bitmap) extras.get("data");
            imageViewFoto2.setImageBitmap(image2);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Processa o OCR da primeira imagem
     */
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

    /**
     * Processa o barcode da segunda imagem
     */
    public void getBarcode() {
        BarcodeDetector bc = new BarcodeDetector.Builder(getApplicationContext()).setBarcodeFormats(Barcode.DATA_MATRIX).build();

        Frame frame = new Frame.Builder().setBitmap(this.image2).build();

        SparseArray<Barcode> barsCode = bc.detect(frame);

        try {
            Barcode result = barsCode.valueAt(0);
            this.txtBarcode.setText(result.rawValue);
        } catch (ArrayIndexOutOfBoundsException a) {
            Toast.makeText(this, "Tire outra foto do DATAMATRIX", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Compara os dois text view e da o resultado
     */
    public void compara() {
        if (this.txtResultado.getText().toString() != "Texto OCR" && this.txtBarcode.getText().toString() != "Texto Data Matrix") {

            Log.d("DEBUG", "Valor do txtResultado" + this.txtResultado.getText().toString().replace(" ", "").trim());
            Log.d("DEBUG", "Valor do txtBarcode" + this.txtBarcode.getText().toString().replace(" ", "").trim());

            if (this.txtResultado.getText().toString().replace(" ", "").trim().equals(this.txtBarcode.getText().toString().replace(" ", "").trim())) {

                this.txtFinal.setText("TEXTO IGUAL!");
                this.txtFinal.setTextColor(Color.BLUE);

            } else {

                this.txtFinal.setText("TEXTO DIFERENTES!");
                this.txtFinal.setTextColor(Color.RED);
            }
        } else {
            Toast.makeText(this, "Processamento em andamento!", Toast.LENGTH_LONG).show();
        }

    }

}
