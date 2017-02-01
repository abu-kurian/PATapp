package com.kanishth.pat;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPoint;
import com.google.maps.android.geojson.GeoJsonPointStyle;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HeatMapsActivity extends BaseActivity {

    /**
     * Alternative radius for convolution
     */
    private static final int ALT_HEATMAP_RADIUS = 20;

    /**
     * Alternative opacity of heatmap overlay
     */
    private static final double ALT_HEATMAP_OPACITY = 0.4;

    /**
     * Alternative heatmap gradient (blue -> red)
     * Copied from Javascript version
     */
    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 255),// transparent
            Color.argb(255 / 3 * 2, 0, 255, 255),
            Color.rgb(0, 191, 255),
            Color.rgb(0, 0, 127),
            Color.rgb(255, 0, 0)
    };

    private static boolean set_pointers;
    private static boolean set_heatmaps;

    public static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {
            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
    };

    public static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
            ALT_HEATMAP_GRADIENT_START_POINTS);

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private boolean mDefaultGradient = true;
    private boolean mDefaultRadius = true;
    private boolean mDefaultOpacity = true;

    GeoJsonLayer layer;
    GeoJsonFeature pointFeature;
    private ArrayList<LatLng> dataset,dataset2,dataset3;
    private ArrayList<Double> mag;

    private LatLng area;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_heat_maps;
    }

    @Override
    protected void startDemo() {
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20, 77.6), 5));

        Intent intent = this.getIntent();
        dataset = intent.getParcelableArrayListExtra("dataset");
        ArrayList<String> name = intent.getStringArrayListExtra("dataset2");
        ArrayList<String> mg = intent.getStringArrayListExtra("mag");

        dataset2 = new ArrayList<LatLng>();
        mag = new ArrayList<>();
        for (int i=0;i<name.size();i++)
        {
            LatLng ll=Downloader.getLatLong(name.get(i));
            if (ll != null) {
                dataset2.add(ll);
                mag.add(Double.parseDouble(mg.get(i)));
            }
            else
                Log.d("*****Not added",name.get(i));
        }

        Log.d("***",dataset2.size()+" "+mag.size());

        magToPoints();




        Button button = (Button) findViewById(R.id.button);
        turn_on(2);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                turn_off(1);
                EditText editText = (EditText) findViewById(R.id.editText);
                String areaname=editText.getText().toString();

                area = Downloader.getLatLong(areaname);

                getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(area.latitude, area.longitude), 8));


                turn_on(1);
            }
        });
    }

    /**
     * Assigns a color based on the given magnitude
     */
    private static float intensityToColor(double probability) {
        if (probability < 1.0) {
            return BitmapDescriptorFactory.HUE_CYAN;
        } else if (probability < 2.5) {
            return BitmapDescriptorFactory.HUE_GREEN;
        } else if (probability < 4.5) {
            return BitmapDescriptorFactory.HUE_YELLOW;
        } else {
            return BitmapDescriptorFactory.HUE_RED;
        }
    }

    /**
     * Adds a point style to all features to change the color of the marker based on its probability
     * property
     */
    private void addColorsToMarkers(GeoJsonLayer layer) {
        // Iterate over all the features stored in the layer
        for (GeoJsonFeature feature : layer.getFeatures()) {
            // Check if the magnitude property exists
            if (feature.getProperty("mag") != null )//&& feature.hasProperty("place"))
            {
                double magnitude = Double.parseDouble(feature.getProperty("mag"));

                // Get the icon for the feature
                BitmapDescriptor pointIcon = BitmapDescriptorFactory
                        .defaultMarker(intensityToColor(magnitude));

                // Create a new point style
                GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();

                // Set options for the point style
                pointStyle.setIcon(pointIcon);
                //pointStyle.setTitle("Area name?");
                //pointStyle.setSnippet("Additional info?");

                // Assign the point style to the feature
                feature.setPointStyle(pointStyle);
            }
        }
    }

    private void addGeoJsonLayerToMap(GeoJsonLayer layer) {

        addColorsToMarkers(layer);
        layer.addLayerToMap();

        // Demonstrate receiving features via GeoJsonLayer clicks.
        layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
            @Override
            public void onFeatureClick(GeoJsonFeature feature) {
                Toast.makeText(HeatMapsActivity.this,
                        "Feature clicked: " + feature.getProperty("title"),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void changeRadius(View view) {
        if (mDefaultRadius) {
            mProvider.setRadius(ALT_HEATMAP_RADIUS);
        } else {
            //mProvider.setRadius(HeatmapTileProvider.DEFAULT_RADIUS);
            mProvider.setRadius(35);
        }
        mOverlay.clearTileCache();
        mDefaultRadius = !mDefaultRadius;
    }

    public void changeGradient(View view) {
        if (mDefaultGradient) {
            mProvider.setGradient(ALT_HEATMAP_GRADIENT);
        } else {
            mProvider.setGradient(HeatmapTileProvider.DEFAULT_GRADIENT);
        }
        mOverlay.clearTileCache();
        mDefaultGradient = !mDefaultGradient;
    }

    public void changeOpacity(View view) {
        if (mDefaultOpacity) {
            mProvider.setOpacity(ALT_HEATMAP_OPACITY);
        } else {
            mProvider.setOpacity(HeatmapTileProvider.DEFAULT_OPACITY);
        }
        mOverlay.clearTileCache();
        mDefaultOpacity = !mDefaultOpacity;
    }

    public void turn_off(int choice)
    {
        if(choice==1)
        {
            if(layer!=null) {
                layer.removeLayerFromMap();}
            set_pointers=false;
        }
        else
        {
            if(mOverlay!=null) {mOverlay.remove(); mProvider=null; }
            set_heatmaps=false;
        }
    }

    public void turn_on(int choice)
    {
        if(choice==1)
        {
            ArrayList<LatLng> dsh = new ArrayList<>();
            for (LatLng ll: dataset){
                if( ll.latitude-area.latitude<1 && ll.longitude-area.longitude<1)
                    dsh.add(ll);
            }

            Log.d("****",dsh.size()+"");

            JSONObject geoJsonData = new JSONObject();
            layer = new GeoJsonLayer(getMap(), geoJsonData);

            HashMap<String, String> properties = new HashMap<String, String>();
            properties.put("", "");
            for(int i=0; i<dsh.size();i++)
            {
                GeoJsonPoint point = new GeoJsonPoint(dsh.get(i));
                pointFeature = new GeoJsonFeature(point, "", properties, null);
                layer.addFeature(pointFeature);
            }
            if (layer != null) {
                addGeoJsonLayerToMap(layer);
            }
            set_pointers=true;
        }
        else
        {
            // Check if need to instantiate (avoid setData etc twice)

            if (mProvider == null)
            {
                mProvider = new HeatmapTileProvider.Builder().data(dataset3).build();
                mOverlay = getMap().addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                mProvider.setRadius(35);
                mProvider.setOpacity(ALT_HEATMAP_OPACITY);


                // Render links
            } else {
                mProvider.setData(dataset);
                mOverlay.clearTileCache();
            }
            set_heatmaps=true;
        }
    }

    private void magToPoints()
    {
        dataset3= new ArrayList<>();
        for(int i=0; i<dataset2.size();i++)
        {
            LatLng current = dataset2.get(i);
            double pc = (mag.get(i)/6)+1;

            for(int j=0; j<pc ; j++)
                dataset3.add(new LatLng(current.latitude+Math.random()%0.5,current.longitude+Math.random()%0.5));

        }
    }
}