package io.jawg.osmcontributor.ui.events.map;

import com.mapbox.services.directions.v5.models.DirectionsResponse;
import com.mapbox.services.directions.v5.models.DirectionsRoute;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Nathan Shiraini on 03/05/2017.
 */

public class DirectionsCalculationResponseEvent {
    private DirectionsRoute route;
    public DirectionsCalculationResponseEvent(DirectionsRoute route) {
        this.route = route;
    }
    public DirectionsRoute getRoute() {
        return this.route;
    }
    public void setRoute(DirectionsRoute route) {
        this.route = route;
    }
}
