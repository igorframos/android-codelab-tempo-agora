package com.codelab.android.tempoagora;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class CityWeatherActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_city_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private View rootView;
        private String cityName;
        private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_city_weather, container, false);
            cityName = getCityName();
            setCityName();
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            AsyncTask<Void, Void, String> getWeatherTask = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    try {
                        return getWeatherForCity();
                    } catch (IOException e) {
                        Log.e(getClass().getName(), e.getMessage(), e);
                    }
                    Toast.makeText(getActivity(),
                            R.string.connection_failed_error_message,
                            Toast.LENGTH_LONG).show();
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    TextView resultTextView = (TextView) rootView.findViewById(R.id.resultTextView);
                    resultTextView.setText(s);
                }
            };
            getWeatherTask.execute();
        }

        private String getCityName() {
            return getActivity().getIntent().getStringExtra(TempoAgoraMainActivity.CITY_NAME);

        }

        private void setCityName() {
            TextView textView = (TextView) rootView.findViewById(R.id.cityName);
            textView.setText(cityName);
        }

        private String getWeatherForCity() throws IOException {
            URL url = new URL(BASE_URL + URLEncoder.encode(cityName, "UTF-8"));
            Log.d("URL to connect", url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = connection.getInputStream();
                return getStringFromInputStream(stream);
            } else {
                throw new IllegalStateException("Error getting data from OpenWeatherMap.");
            }
        }

        private static String getStringFromInputStream(InputStream inputStream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String response = "";
            String line;
            while ((line = reader.readLine()) != null) {
                response += line;
            }
            return response;
        }
    }
}
