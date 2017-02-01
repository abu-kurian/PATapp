package com.example.abu.patapp;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<LatLng> list = null;
    public static final String TAG = "PAT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        list = new ArrayList<>();
//        list.add(new LatLng(20, 78));
//        list.add(new LatLng(20, 79));
//        list.add(new LatLng(22, 75));
//        list.add(new LatLng(21, 76));
//        list.add(new LatLng(21, 77));

        new GetIntensityData().execute();



        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(20,78);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private class GetIntensityData extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            int resCode;
            InputStream in = null;
            String myurl = "http://192.168.1.24/PATserverCode.php";
//              String myurl = "http://192.168.1.3/mobStatus/getUserGroup.php";//Home
            HttpURLConnection urlConnection = null;


            try {
                URL url = new URL(myurl);
                Log.i("abu", "Connecting");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000000);
                urlConnection.setReadTimeout(1000);

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
                Log.e("OUTSIDE","LOOP");
                JSONObject jObject = new JSONObject(strFileContents);
                JSONArray jArray = jObject.getJSONArray("result");
                for (int i=0; i < jArray.length(); i++)
                {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        final String oneObjectsCity= oneObject.getString("c");
                        final Double latitude= Double.parseDouble(oneObject.getString("la"));
                        final Double longitude= Double.parseDouble(oneObject.getString("l"));
                        final Double intensity= Double.parseDouble(oneObject.getString("i"));

                        //final Double latitude=oneObject.getDouble("latitude");
                        //final Double longitude=oneObject.getDouble("longitude");
                        //final Double intensity=oneObject.getDouble("intensity");


                        int intensityNorm= (int) Math.ceil(intensity);
                        Log.d(TAG,"City="+oneObjectsCity+"Latitude="+Double.toString(latitude) +"Longitude="+Double.toString(longitude));
                        while(intensityNorm-- >= 0){
                            list.add(new LatLng(latitude, longitude));
                        }

//                        public void onMapReady(GoogleMap googleMap) {
//                            mMap = googleMap;
//
//                            // Add a marker in Sydney and move the camera
//                            LatLng sydney = new LatLng(-34, 151);
//                            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//                        }



//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                 LatLng sydney = new LatLng(latitude, longitude);
//                                mMap.addMarker(new MarkerOptions().position(sydney).title(oneObjectsCity));
//                            }
//                        });


                    } catch (JSONException e) {
                        Log.i(TAG, "JSONException");
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                                .data(list)
                                .build();
                        // Add a tile overlay to the map, using the heat map tile provider.
                        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

                    }
                });
                return null;

            } catch (IOException e) {
                Log.i(TAG, "IOException");
                Log.e(TAG, Log.getStackTraceString(e));
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "Connection to Server Lost. Check Server Config in Settings.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                finish();
                return null;
            } finally {
                urlConnection.disconnect();
                return null;
            }
        }

    }

}
