package com.example.estudiantes.demoweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ExecutorService queue = Executors.newSingleThreadExecutor();

    private final static String KEY = "6498e268f2de120d0cd71288c41cbcc6";
    private final static String DOMAIN = "https://api.openweathermap.org/data/2.5/weather";

    private final static String FORMAT = "https://api.openweathermap.org/data/2.5/weather?q=Cali,co&appid=6498e268f2de120d0cd71288c41cbcc6";

    private EditText editTextSearch;
    private Button buttonSearch;
    private TextView textViewCurrent;
    private TextView textViewMin;
    private TextView textViewMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.btnSearch);
        textViewCurrent = findViewById(R.id.txvCurrent);
        textViewMin = findViewById(R.id.txvMin);
        textViewMax = findViewById(R.id.txvMax);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editTextSearch.getText().toString();
                search(query);
            }
        });
    }

    public void search(String query){

        final String queryTmp = query;

        Runnable thread = new Runnable() {
            @Override
            public void run() {

                String strUrl = DOMAIN + "?q=" + queryTmp + "&appid=" + KEY;
                URL url = null;
                CAFData remoteData = null;

                try {
                    url = new URL(strUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                if (url != null){
                    remoteData = CAFData.dataWithContentsOfURL(url);
                    Log.d("DemoWeather", remoteData.toText());
                }

            }
        };


        queue.execute(thread);


    }


}
