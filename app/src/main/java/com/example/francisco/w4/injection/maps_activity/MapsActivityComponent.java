package com.example.francisco.w4.injection.maps_activity;

import com.example.francisco.w4.view.maps_activity.MapsActivity;

import dagger.Component;

/**
 * Created by FRANCISCO on 24/08/2017.
 */

@Component(modules = MapsActivityModel.class)
public interface MapsActivityComponent {

    void inject(MapsActivity mapsActivity);
}
