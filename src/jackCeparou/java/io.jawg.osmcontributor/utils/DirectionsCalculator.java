package io.jawg.osmcontributor.utils;

import com.mapbox.services.commons.models.Position;

import org.greenrobot.eventbus.EventBus;

/**
 * @brief Calculates the directions between two POIs
 *
 * This class will call MapBox's services API in order to get a wheelchair-friendly path between two points of interest.
 */

public class DirectionsCalculator {
    private Position origin;
    private Position destination;
    private EventBus events;


    public DirectionsCalculator(EventBus events) {
        this.origin = null;
        this.destination = null;
        this.events = events;
        this.enable();
    }

    public void enable() {
        events.register(this);
    }

    public void disable() {
        events.unregister(this);
    }

    public void setBus(EventBus events) {
        this.disable();
        this.events = events;
        this.enable();
    }
}
