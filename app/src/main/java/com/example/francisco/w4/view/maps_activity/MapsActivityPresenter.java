package com.example.francisco.w4.view.maps_activity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by FRANCISCO on 24/08/2017.
 */

public class MapsActivityPresenter implements MapsActivityContract.Presenter {
    MapsActivityContract.View view;
    Context context;
    private static final String TAG = "MainActivityPresenter";

    @Override
    public void attachView(MapsActivityContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void AddresToCoordinates(String s) {
        try {
            Geocoder gc = new Geocoder(context, Locale.getDefault());
            if (gc.isPresent()) {
                try {
                    view.sendLocation(gc.getFromLocationName(s, 1));

                } catch (IOException el) {
                    el.printStackTrace();
                    Toast.makeText(context, "Invalid Address", Toast.LENGTH_LONG).show();
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            view.showError("No Internet/location, please turn on and open the app again");
        }
    }

    @Override
    public void modifyLocation(GoogleMap mMap, Address address) {
        LatLng currentLatLng = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLatLng).title(address.getAddressLine(0)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));

    }
}
