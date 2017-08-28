package com.example.francisco.w4.view.maps_activity;

import android.content.Context;
import android.location.Address;

import com.example.francisco.w4.BasePresenter;
import com.example.francisco.w4.BaseView;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

/**
 * Created by FRANCISCO on 24/08/2017.
 */

public interface MapsActivityContract {

    interface View extends BaseView {

        void sendLocation(List<Address> fromLocationName);
    }

    interface Presenter extends BasePresenter<View> {
        void setContext(Context context);
        void AddresToCoordinates(String s);
        void modifyLocation(GoogleMap mMap, Address address);
    }
}
