package com.example.estudiantes.demoweather;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ExecutorService queue = Executors.newSingleThreadExecutor();

    private final static String KEY = "6498e268f2de120d0cd71288c41cbcc6";
    private final static String DOMAIN = "https://api.openweathermap.org/data/2.5/weather";
    private final static String IMGDOMAIN = "https://openweathermap.org/img/w/";

    private final static String FORMAT = "https://api.openweathermap.org/data/2.5/weather?q=Cali,co&appid=6498e268f2de120d0cd71288c41cbcc6";

    private EditText editTextSearch;
    private Button buttonSearch;
    private TextView textViewCurrent;
    private TextView textViewMin;
    private TextView textViewMax;
    private TextView textViewWeather;
    private ImageView imgWeather;

    private Button btnGo = null;
    private double lng = 0;
    private double lat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.btnSearch);
        textViewCurrent = findViewById(R.id.txvCurrent);
        textViewMin = findViewById(R.id.txvMin);
        textViewMax = findViewById(R.id.txvMax);
        textViewWeather = findViewById(R.id.txvWeather);
        imgWeather = findViewById(R.id.imgWeather);
        btnGo = findViewById(R.id.btnGo);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editTextSearch.getText().toString();
                search(query);
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lng != 0 && lat != 0){
                    Uri uri = Uri.parse("geo:" + lat + "," + lng);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });
    }

    public void search(String query){

        final String queryTmp = query;

        Runnable thread = new Runnable() {
            @Override
            public void run() {

                String strUrl = DOMAIN + "?q=" + queryTmp + "&appid=" + KEY + "&units=metric&lang=es";
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

                    try {
                        JSONObject root = new JSONObject(remoteData.toText());
                        JSONObject coord = root.getJSONObject("coord");
                        JSONArray weather = root.getJSONArray("weather");
                        JSONObject main = root.getJSONObject("main");

                        String desc = "";
                        String icon = "";
                        Bitmap bitmap = null;

                        lat = coord.getDouble("lat");
                        lng = coord.getDouble("lon");

                        if(weather.length() > 0){
                            JSONObject aWeather = weather.getJSONObject(0);
                            desc = aWeather.getString("description");
                            icon = aWeather.getString("icon");

                            strUrl = IMGDOMAIN + icon + ".png";
                            url = new URL(strUrl);
                            remoteData = CAFData.dataWithContentsOfURL(url);
                            if (remoteData != null){
                                bitmap = remoteData.toImage();
                            }
                        }

                        final String descTemp = desc;
                        final Bitmap bitmapTemp = bitmap;
                        final float temp = (float) main.getDouble("temp");
                        final float tempMin = (float) main.getDouble("temp_min");
                        final float tempMax = (float) main.getDouble("temp_max");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textViewCurrent.setText(String.valueOf(temp));
                                textViewMin.setText(String.valueOf(tempMin));
                                textViewMax.setText(String.valueOf(tempMax));
                                textViewWeather.setText(descTemp);
                                imgWeather.setImageBitmap(bitmapTemp);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

            }
        };


        queue.execute(thread);


    }


}
