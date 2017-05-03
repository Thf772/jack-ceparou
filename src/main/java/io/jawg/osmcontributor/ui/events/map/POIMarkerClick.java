package io.jawg.osmcontributor.ui.events.map;

import io.jawg.osmcontributor.model.entities.Poi;
import io.jawg.osmcontributor.ui.utils.views.map.marker.LocationMarkerView;

/**
 * Created by alexandre on 5/3/17.
 */

public class POIMarkerClick {
    LocationMarkerView<Poi> marker;

    public POIMarkerClick(LocationMarkerView<Poi> marker) {
        this.marker = marker;
    }

    public LocationMarkerView<Poi> getMarker() {
        return marker;
    }
}
