package com.example.boyi.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    TextView resultTextView;

    public void findWeather(View view){
        Log.i("cityName", cityName.getText().toString());
        //once input is finished, close the key window
        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {
            //encode cityname into url, such as san fransicro to san20%fransisco. cause this should not be space in url
            String encodeCityName = URLEncoder.encode(cityName.getText().toString(),"UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodeCityName +"&appid=bda4320affb9fd6c224ab0ac226a9ad3");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText)findViewById(R.id.cityName);
        resultTextView = (TextView)findViewById(R.id.resultTextView);
    }
    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                //Toast can popup a small dialogue
                Toast.makeText(getApplicationContext(), "Could not find your city", Toast.LENGTH_LONG);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                String mes = "";
                JSONObject jObject = new JSONObject(result);
                String weatherInfo = jObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if(main != "" && description != ""){
                        //\r\n means new line
                        mes += main + ": " + description + "\r\n";
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Could not find your city", Toast.LENGTH_LONG);
                    }

                }

                if (mes != ""){
                    resultTextView .setText(mes);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Could not find your city", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }


}
