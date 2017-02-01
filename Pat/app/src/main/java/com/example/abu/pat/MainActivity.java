package com.example.abu.pat;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = "PAT";

//    List<LatLng> list = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new getIngroups().execute();

    }

    private class getIngroups extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            int resCode;
            InputStream in = null;
            String myurl = "http://192.168.1.24/sendPATdemo.php";
//              String myurl = "http://192.168.1.3/mobStatus/getUserGroup.php";//Home
            HttpURLConnection urlConnection = null;


            try {
                URL url = new URL(myurl);
                Log.i("abu", "Connecting");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(10000);

                int responseCode = 0;
                //              do {
                try{
                    urlConnection.connect();
                    responseCode = urlConnection.getResponseCode();
                    Log.i(TAG, "Got Ingroups " + responseCode);
                } catch (IOException e) {
                    Log.i(TAG, "IOException");
                    Log.e(TAG, Log.getStackTraceString(e));
//                        continue;
                }
//                } while (responseCode != 200);

                in = new BufferedInputStream(urlConnection.getInputStream());
                byte[] contents = new byte[1024];
                int bytesRead = 0;
                String strFileContents = "";
                while((bytesRead = in.read(contents)) != -1) {
                    strFileContents += new String(contents, 0, bytesRead);
                }

                Log.i(TAG, "Start");
                Log.i(TAG, strFileContents);
                Log.i(TAG, "Over");

                JSONObject jObject = new JSONObject(strFileContents);
                JSONArray jArray = jObject.getJSONArray("result");
                for (int i=0; i < jArray.length(); i++)
                {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        final String oneObjectsCity= oneObject.getString("city");
                        final String oneObjectsLat= oneObject.getString("latitude");
                        final String oneObjectsLong= oneObject.getString("longitude");
                        final String oneObjectsIntensity= oneObject.getString("intensity");
//                        list.add(oneObjectsLat,oneObjectsLong);oneObjectsLong
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

//                                inGroups.add(oneObjectsItem);
//                                inGroupAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        Log.i(TAG, "JSONException");
                    }
                }
                return null;

            } catch (IOException e) {
                Log.i(TAG, "IOException");
                Log.e(TAG, Log.getStackTraceString(e));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Connection to Server Lost. Check Server Config in Settings.", Toast.LENGTH_SHORT).show();
                    }
                });
//                finish();
                return null;
            } finally {
                urlConnection.disconnect();
                return null;
            }
        }

    }

}



