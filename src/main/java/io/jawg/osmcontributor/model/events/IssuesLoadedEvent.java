package io.jawg.osmcontributor.model.events;

import java.util.List;

import io.jawg.osmcontributor.api.IssueMarker;

/**
 * Created by patrick on 04/05/17.
 */

public class IssuesLoadedEvent {
    List<IssueMarker> markers;

    public List<IssueMarker> getMarkers() {
        return markers;
    }

    public IssuesLoadedEvent(List<IssueMarker> markers) {

        this.markers = markers;
    }
}
