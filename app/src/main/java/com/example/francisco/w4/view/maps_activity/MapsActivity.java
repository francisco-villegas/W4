package com.example.francisco.w4.view.maps_activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.francisco.w4.R;
import com.example.francisco.w4.Util.CONSTANTS;
import com.example.francisco.w4.Util.PicassoMarker;
import com.example.francisco.w4.model.Places;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends AppCompatActivity  {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
    private static final String TAG = "MainActivity";
    private static final String RADIUS = "2000";
    private SupportMapFragment mapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    GoogleMap map;

    @BindView(R.id.etLocation)
    AutoCompleteTextView etLocation;

    @BindView(R.id.rvPlaces)
    RecyclerView rvPlaces;

    @BindView(R.id.cvPlace)
    CardView cvPlace;

    LatLng currentLatLng;
    List<Address> addressList;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.ItemAnimator itemAnimator;

    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ButterKnife.bind(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        etLocation.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
        layoutManager = new LinearLayoutManager(getApplicationContext());
        itemAnimator = new DefaultItemAnimator();
        rvPlaces.setLayoutManager(layoutManager);
        rvPlaces.setItemAnimator(itemAnimator);
        checkPermissions();

        ChangeLocationButton();
    }

    private void ChangeLocationButton(){
        View myLocationButton = mapFragment.getView().findViewById(0x2);

        if (myLocationButton != null && myLocationButton.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            // location button is inside of RelativeLayout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myLocationButton.getLayoutParams();

            // Align it to - parent BOTTOM|LEFT
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);

            // Update margins, set to 10dp
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics());
            params.setMargins(margin, margin, margin, margin);

            myLocationButton.setLayoutParams(params);
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                }
                return;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.refresh:
                try{map.clear();}catch(Exception ex){}
                getLocation();
                Toast.makeText(this, "Map refreshed.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnRestaurant:
                map.clear();
                getGeocodeAddress("restaurant");
                Toast.makeText(this, "Restaurants close to your location.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnBank:
                map.clear();
                getGeocodeAddress("bank");
                Toast.makeText(this, "Banks close to your location.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnATM:
                map.clear();
                getGeocodeAddress("atm");
                Toast.makeText(this, "ATMs close to your location", Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnBar:
                map.clear();
                getGeocodeAddress("bar");
                Toast.makeText(this, "Bars close to your location", Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnPark:
                map.clear();
                getGeocodeAddress("park");
                Toast.makeText(this, "Parks close to your location", Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnStore:
                map.clear();
                getGeocodeAddress("store");
                Toast.makeText(this, "Stores close to your location", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.btnHospital:
                map.clear();
                getGeocodeAddress("hospital");
                Toast.makeText(this, "Hospitals close to your location", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        currentLocation = location;
                        Log.d(TAG, "onSuccess: " + location.getLatitude() + "," + location.getLongitude());
                        if (mapFragment != null) {
                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap map) {
                                    currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                    loadMap(map, currentLatLng, "You are here", "");
                                }
                            });
                        } else {
                            Toast.makeText(MapsActivity.this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    public void getGeocodeAddress(String nearByPlace) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl url = new HttpUrl.Builder()
                .scheme(CONSTANTS.BASE_SCHEME)
                .host(CONSTANTS.BASE_URL)
                .addPathSegments(CONSTANTS.PATH_PLACES)
                .addQueryParameter("location", currentLocation.getLatitude() + "," + currentLocation.getLongitude())
                .addQueryParameter("radius", RADIUS)
                .addQueryParameter("type", nearByPlace)
                .addQueryParameter("type", "true")
                .addQueryParameter("key", CONSTANTS.KEY)
                .build();
        Log.d(TAG, "getGeocodeAddress: " + url.toString());
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                final Places addressResponse = gson.fromJson(response.body().string(), Places.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PlaceAdapter placeAdapter = new PlaceAdapter(addressResponse.getResults());
                        cvPlace.setVisibility(View.VISIBLE);
                        rvPlaces.setAdapter(placeAdapter);
                        placeAdapter.notifyDataSetChanged();

                        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(placeAdapter);
                        mItemTouchHelper = new ItemTouchHelper(callback);
                        mItemTouchHelper.attachToRecyclerView(rvPlaces);

                        for (int i = 0; i < addressResponse.getResults().size(); i++) {
                            getLocationInMap(addressResponse.getResults().get(i).getVicinity(), addressResponse.getResults().get(i).getName(), false, addressResponse.getResults().get(i).getIcon());
                        }
                    }
                });
            }
        });
    }

    protected void loadMap(GoogleMap googleMap, LatLng currentLatLng, String yourLocation, String icon) {
        map = googleMap;
        if (map != null) {
            try {
                map = googleMap;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLatLng).zoom(14).tilt(0).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                PicassoMarker marker = new PicassoMarker(map.addMarker(new MarkerOptions().position(currentLatLng).title(yourLocation)));
                if(!icon.equals(""))
                    Picasso.with(this).load(icon).into(marker);
                map.setTrafficEnabled(true);
                map.setMyLocationEnabled(true);
            } catch (Exception e) {
                e.getMessage();
                Log.d(TAG, "loadMap: " + e.getMessage());
            }
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }
    private void getLocationInMap(String location, String name, boolean isSearch, String icon) {
        if (!location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 5);
                if (addressList != null) {
                    for (int i = 0; i < addressList.size(); i++) {
                        if (isSearch) {
                            currentLocation.setLatitude(addressList.get(i).getLatitude());
                            currentLocation.setLongitude(addressList.get(i).getLongitude());
                        }
                        currentLatLng = new LatLng(addressList.get(i).getLatitude(), addressList.get(i).getLongitude());
                        loadMap(map, currentLatLng, name, icon);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @OnClick({R.id.btnSearch, R.id.btnDelete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSearch:
                map.clear();
                cvPlace.setVisibility(View.GONE);
                getLocationInMap(etLocation.getText().toString(), etLocation.getText().toString(), true, "");
                break;
            case R.id.btnDelete:
                etLocation.setText("");
                break;
        }
    }

}
