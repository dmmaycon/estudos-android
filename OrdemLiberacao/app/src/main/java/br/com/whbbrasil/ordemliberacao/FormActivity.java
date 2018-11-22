package br.com.whbbrasil.ordemliberacao;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class FormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Intent i = getIntent();
        TextView tv = (TextView) findViewById(R.id.txtEscolhido);
        tv.setText("Liberação número: " + i.getStringExtra("escolhido"));
    }
}
