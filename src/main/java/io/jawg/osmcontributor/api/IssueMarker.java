package io.jawg.osmcontributor.api;

import com.mapbox.mapboxsdk.geometry.LatLng;

import io.jawg.osmcontributor.utils.core.MapElement;

/**
 * Created by patrick on 03/05/17.
 */

public class IssueMarker implements MapElement {

    LatLng coordinates;
    String description;
    String title;
    Long id;
    //Image ?


    public IssueMarker(LatLng coordinates, String description, String title, Long id) {
        this.coordinates = coordinates;
        this.description = description;
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public LatLng getPosition() {
        return coordinates;
    }
}
