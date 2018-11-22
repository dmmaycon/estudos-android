package br.com.whbbrasil.ordemliberacao;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayAdapter<String> adapterList;
    ArrayList<String> list = new ArrayList<String>();
    ListView lista;
    Button btnLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Start dos componentes
        lista = (ListView) findViewById(R.id.ltv_lista);
        btnLoad = (Button) findViewById(R.id.btnLoad);

        // cria o adpter para as listas
        adapterList = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        adapterList.notifyDataSetChanged();
        lista.setAdapter(adapterList);


        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();

            }
        });

        // seta a opção de clicar no item para levar a uma nova active
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(getApplicationContext(), list.get(position), Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), FormActivity.class);
                i.putExtra("escolhido",  list.get(position));
                startActivity(i);
            }
        });



    }

    @Override
    protected void onPause() {
        super.onPause();
        adapterList.clear();
        adapterList.notifyDataSetChanged();
    }

    public void load() {
        MyTask mt = new MyTask();
        mt.execute("");
        this.list = mt.getList();
    }

    class MyTask extends AsyncTask<String, String, String> {


        ArrayAdapter<String> adapterList;
        ArrayList<String> list = new ArrayList<String>();



        // Método principal executa em Thread diferente e deve fazer o processamento
        @Override
        protected String doInBackground(String... params) {
            String conteudo = HttpManager.getDados("http://192.168.25.11/serverPHP/");
            Log.d("DEBUG", "valor do conteudo " + conteudo);
            return conteudo;
        }

        // Recebe o retorno do doInBackgroun
        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                Log.d("DEBUG", "valor do S " + s);
                try {
                    JSONArray js = new JSONArray(s);
                    for (int i = 0; i < js.length(); i++) {
                        JSONObject jsonLinha = js.getJSONObject(i);
                        String lv = "COD: " + jsonLinha.getString("id") + " Nome: " + jsonLinha.getString("nome");
                        Log.d("DEBUG", "valor do LV: " + lv);
                        setList(lv);
                    }

                    // cria o adpter para as listas
                    adapterList = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                    adapterList.notifyDataSetChanged();
                    lista.setAdapter(adapterList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Log.d("DEBUG", "erro ao receber o retorno do servidor" + s);
            }

        }


        public void setList(String s) {
            this.list.add(s);
        }

        public ArrayList<String> getList() {
            return  this.list;
        }



    }

}


