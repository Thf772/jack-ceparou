package io.jawg.osmcontributor.utils;

import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.directions.v5.DirectionsCriteria;
import com.mapbox.services.directions.v5.MapboxDirections;
import com.mapbox.services.directions.v5.models.DirectionsResponse;
import com.mapbox.services.directions.v5.models.DirectionsRoute;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.jawg.osmcontributor.BuildConfig;
import io.jawg.osmcontributor.ui.events.map.DestinationSelectedEvent;
import io.jawg.osmcontributor.ui.events.map.DirectionsCalculationFailureEvent;
import io.jawg.osmcontributor.ui.events.map.DirectionsCalculationResponseEvent;
import io.jawg.osmcontributor.ui.events.map.OriginSelectedEvent;
import io.jawg.osmcontributor.ui.events.map.StartDirectionsCalculationEvent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @brief Calculates the directions between two POIs
 *
 * This class will call MapBox's services API in order to get a wheelchair-friendly path between two points of interest.
 */

public class DirectionsCalculator {
    private Position origin;
    private Position destination;
    private EventBus events;
    private boolean enabled;

    private DirectionsRoute route;

    ////////////////////////////////////////////////////////////////////
    /////     Constructors, destructors and (un)initializators     /////
    ////////////////////////////////////////////////////////////////////

    public DirectionsCalculator(EventBus events) {
        this.origin = null;
        this.destination = null;
        this.events = events;
        this.route = null;
        this.enable();
    }

    public void enable() {
        events.register(this);
        this.enabled = true;
    }

    public void disable() {
        events.unregister(this);
        this.enabled = false;
    }

    public void setBus(EventBus events) {
        if (this.enabled) this.disable();
        this.events = events;
    }

    ////////////////////////////////////////////////////////////////////
    /////     Getters and setters                                  /////
    ////////////////////////////////////////////////////////////////////

    public Position getOrigin() {
        return this.origin;
    }

    public Position getDestination() {
        return this.destination;
    }

    public DirectionsRoute getRoute() {
        return this.route;
    }

    public void setOrigin(Position pos){
        this.origin = pos;
    }

    public void setDestination(Position pos) {
        this.destination = pos;
    }

    public void setRoute(DirectionsRoute route) {
        this.route = route;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isReady() {
        return this.isEnabled() &&
                this.origin != null &&
                this.destination != null;
    }

    public boolean isCalculated() {
        return this.route != null;
    }

    ////////////////////////////////////////////////////////////////////
    /////     Events                                               /////
    ////////////////////////////////////////////////////////////////////

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onOriginSelectedEvent(OriginSelectedEvent e) {
        this.setOrigin(e.getPosition());
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDestinationSelectedEvent(DestinationSelectedEvent e) {
        this.setDestination(e.getPosition());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onStartDirectionsCalculationEvent(StartDirectionsCalculationEvent e) {
        this.calculateDirections();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDirectionsCalculationResponseEvent(DirectionsCalculationResponseEvent e) {
        this.setRoute(e.getRoute());
        this.checkWheelchairFriendliness();
    }

    ////////////////////////////////////////////////////////////////////
    /////     Calculations                                         /////
    ////////////////////////////////////////////////////////////////////

    public void calculateDirections() {
        if (!this.isReady()) return; // TODO Error signalling
        MapboxDirections client = null;
        try {
            client = new MapboxDirections.Builder()
                    .setOrigin(this.getOrigin())
                    .setDestination(this.getDestination())
                    .setOverview(DirectionsCriteria.OVERVIEW_FULL)
                    .setProfile(DirectionsCriteria.PROFILE_WALKING)
                    .setAccessToken(BuildConfig.MAPBOX_TOKEN)
                    .build();
        } catch (ServicesException e) {
            // TODO Error signalling
            e.printStackTrace();
            return;
        }

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    // TODO Error signalling
                } else if (response.body().getRoutes().size() < 1) {
                    // TODO Error signalling
                } else {
                    // We have at least one route, so it worked!
                    events.post(new DirectionsCalculationResponseEvent(response.body().getRoutes().get(0)));
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                events.post(new DirectionsCalculationFailureEvent(call, throwable));
            }
        });
    }

    public void checkWheelchairFriendliness() {
        // TODO identify OSM routes
    }

}
