package io.jawg.osmcontributor.utils;

import com.mapbox.services.commons.models.Position;
import com.mapbox.services.directions.v5.DirectionsCriteria;
import com.mapbox.services.directions.v5.MapboxDirections;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.jawg.osmcontributor.BuildConfig;
import io.jawg.osmcontributor.ui.events.map.DestinationSelectedEvent;
import io.jawg.osmcontributor.ui.events.map.OriginSelectedEvent;
import io.jawg.osmcontributor.ui.events.map.StartDirectionsCalculationEvent;

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

    ////////////////////////////////////////////////////////////////////
    /////     Constructors, destructors and (un)initializators     /////
    ////////////////////////////////////////////////////////////////////

    public DirectionsCalculator(EventBus events) {
        this.origin = null;
        this.destination = null;
        this.events = events;
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

    public void setOrigin(Position pos){
        this.origin = pos;
    }

    public void setDestination(Position pos) {
        this.destination = pos;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isReady() {
        return this.isEnabled() &&
                this.origin != null &&
                this.destination != null;
    }

    ////////////////////////////////////////////////////////////////////
    /////     Events                                               /////
    ////////////////////////////////////////////////////////////////////

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOriginSelectedEvent(OriginSelectedEvent e) {
        this.setOrigin(e.getPosition());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDestinationSelectedEvent(DestinationSelectedEvent e) {
        this.setDestination(e.getPosition());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onStartDirectionsCalculationEvent(StartDirectionsCalculationEvent e) {

    }

    ////////////////////////////////////////////////////////////////////
    /////     Calculations                                         /////
    ////////////////////////////////////////////////////////////////////

    public void calculateDirections() {
        if (!this.isReady()) return; // TODO Error signalling
        MapboxDirections.Builder builder = new MapboxDirections.Builder()
                .setOrigin(this.getOrigin())
                .setDestination(this.getDestination())
                .setOverview(DirectionsCriteria.OVERVIEW_FULL)
                .setProfile(DirectionsCriteria.PROFILE_WALKING)
                .setAccessToken(BuildConfig.MAPBOX_TOKEN);

    }


}
