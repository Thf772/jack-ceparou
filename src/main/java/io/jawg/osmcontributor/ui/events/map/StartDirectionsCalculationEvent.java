package io.jawg.osmcontributor.ui.events.map;

import java.util.Collection;
import java.util.Vector;

import io.jawg.osmcontributor.model.entities.Poi;

/**
 * Created by Nathan Shiraini on 03/05/2017.
 */

public class StartDirectionsCalculationEvent {
    private Vector<Poi> points;
    public StartDirectionsCalculationEvent() {
        this.points = null;
    }
    public StartDirectionsCalculationEvent(Collection<Poi> points) {
        this.points = new Vector<Poi>(points);
    }
    public Vector<Poi> getPoints() {
        return this.points;
    }
}
