package io.jawg.osmcontributor.ui.events.map;

import com.mapbox.services.directions.v5.models.DirectionsRoute;

/**
 * Created by Nathan Shiraini on 03/05/2017.
 */

public class DoneCalculatingDirectionsEvent {
    private boolean failure;
    private DirectionsRoute route;
    public DoneCalculatingDirectionsEvent(DirectionsRoute route) {
        if (route == null) {
            this.route = null;
            this.failure = true;
        } else {
            this.route = route;
            this.failure = false;
        }
    }

    public DirectionsRoute getRoute() {
        return this.route;
    }

    public boolean hasFailed() {
        return this.failure;
    }
}
