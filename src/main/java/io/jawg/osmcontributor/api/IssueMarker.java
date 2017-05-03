package io.jawg.osmcontributor.api;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by patrick on 03/05/17.
 */

public class IssueMarker {

    LatLng coordinates;
    String description;
    String title;
    //Image ?


    public IssueMarker(LatLng coordinates, String description, String title) {
        this.coordinates = coordinates;
        this.description = description;
        this.title = title;
    }
}
