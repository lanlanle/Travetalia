package hu.ait.android.travetalia;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Layer;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hu.ait.android.travetalia.data.Country;
import hu.ait.android.travetalia.data.CreatePostActivity;
import hu.ait.android.travetalia.data.Post;


public class ScratchmapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String KEY_POST = "KEY_POST";
    public static final String KEY_LAT = "KEY_LAT";
    public static final String KEY_LON = "KEY_LON";
    public static final String KEY_COUNTRY = "KEY_COUNTRY";
    public static final String KEY_ZINDEX = "KEY_ZINDEX";
    private GoogleMap mMap;
    private long zInd = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratchmap);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(ScratchmapActivity.this);

        initPosts();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Budapest and move the camera
        LatLng Budapest = new LatLng(40.416775,-3.70379);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Budapest, 3F));


        // get geocode
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                try {
                    Geocoder gc = new Geocoder(ScratchmapActivity.this, Locale.getDefault());
                    List<Address> addrs = null;
                    addrs = gc.getFromLocation(latLng.latitude, latLng.longitude, 2);
                    String country =addrs.get(0).getCountryName();
                    zInd++;


                    Intent intentCreate  = new Intent(ScratchmapActivity.this,
                            CreatePostActivity.class);
                    intentCreate.putExtra(KEY_LAT, latLng.latitude);
                    intentCreate.putExtra(KEY_LON, latLng.longitude);
                    intentCreate.putExtra(KEY_COUNTRY,country);
                    intentCreate.putExtra(KEY_ZINDEX,zInd);
                    startActivityForResult(intentCreate, 1001);

                }catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Post newPost = (Post) data.getSerializableExtra(KEY_POST);
            mMap.addMarker(new MarkerOptions().position(newPost.getLatLong()).title(
                    newPost.getLocation()).zIndex(newPost.getzIndex()));
            scratchTheMap(newPost.getCountry());
        }
    }

    public void scratchTheMap(String countryName){
        if(countryName.equals("France"))addGeoJsonLayer(Color.BLUE,R.raw.france_geojson);
        else if (countryName.equals("Spain")) addGeoJsonLayer(Color.YELLOW,R.raw.es_geojson);
        else if (countryName.equals("Italy")) addGeoJsonLayer(Color.MAGENTA,R.raw.italy_geojson);
        else if (countryName.equals("Hungary")) addGeoJsonLayer(Color.GREEN,R.raw.hungary_geojson);

    }

    public void addGeoJsonLayer(int color, int resource){

        try {
            GeoJsonLayer layer = new GeoJsonLayer(mMap, resource, getApplicationContext());

            GeoJsonPolygonStyle style = layer.getDefaultPolygonStyle();
            style.setFillColor(color);
            style.setStrokeColor(color);
            style.setStrokeWidth(1F);
            style.setZIndex((float) 1);

            layer.addLayerToMap();

        } catch (IOException ex) {
            Log.e("IOException", ex.getLocalizedMessage());
        } catch (JSONException ex) {
            Log.e("JSONException", ex.getLocalizedMessage());
        }

    }

    private void initPosts() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null){
                    getMarkersData((Map<String, Object>) dataSnapshot.getValue());
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getMarkersData(Map<String,Object> posts) {
        //get all posts
        for (Map.Entry<String, Object> entry : posts.entrySet()) {

            Map singlePost = (Map) entry.getValue();

            Double markerLat= (Double) singlePost.get("lat");
            Double markerLng =(Double) singlePost.get("lng");
            String markerLocation = (String) singlePost.get("location");
            Long markerZIndex = (Long) singlePost.get("zIndex");

            mMap.addMarker(new MarkerOptions().position(new LatLng(markerLat,markerLng)).title(
                    markerLocation).zIndex(markerZIndex));

            String markerCountry = (String) singlePost.get("country");
            scratchTheMap(markerCountry);


        }

    }



}
