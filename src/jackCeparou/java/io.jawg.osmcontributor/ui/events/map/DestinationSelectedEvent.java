package io.jawg.osmcontributor.ui.events.map;

import com.mapbox.services.commons.models.Position;

/**
 * Created by Nathan Shiraini on 03/05/2017.
 */

public class DestinationSelectedEvent {
    private Position pos;
    public DestinationSelectedEvent() {
        this.pos = null;
    }
    public DestinationSelectedEvent(Position pos) {
        this.pos = pos;
    }
    public Position getPosition() {
        return pos;
    }
    public void setPosition(Position pos) {
        this.pos = pos;
    }
}
