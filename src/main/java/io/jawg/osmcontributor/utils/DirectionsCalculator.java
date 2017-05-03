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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import io.jawg.osmcontributor.BuildConfig;
import io.jawg.osmcontributor.model.entities.Poi;
import io.jawg.osmcontributor.ui.events.map.DoneCalculatingDirectionsEvent;
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
    private Vector<Poi> points;
    private EventBus events;
    private boolean enabled;

    ////////////////////////////////////////////////////////////////////
    /////     Constructors, destructors and (un)initializators     /////
    ////////////////////////////////////////////////////////////////////

    public DirectionsCalculator(EventBus events) {
        this.points = new Vector<Poi>();
        this.events = events;
    }

    public void setBus(EventBus events) {
        this.events = events;
    }

    ////////////////////////////////////////////////////////////////////
    /////     Getters and setters                                  /////
    ////////////////////////////////////////////////////////////////////

    public Vector<Poi> getPoints() {
        return this.points;
    }

    public void setPoints(Collection<Poi> points) {
        this.points = new Vector<Poi>(points);
    }

    public boolean addPoint(Poi point) {
        return this.points.add(point);
    }

    public boolean  removePoint(Poi  point) {
        return this.points.remove(point);
    }

    public void clearPoints() {
        this.points.removeAllElements();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isReady() {
        return this.isEnabled() &&
                this.points.size() >= 2;
    }

    ////////////////////////////////////////////////////////////////////
    /////     Events                                               /////
    ////////////////////////////////////////////////////////////////////

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public boolean calculateDirections(StartDirectionsCalculationEvent ev) {
        if (!this.isReady()) return false; // TODO Error signalling

        ArrayList<Position> pos = new ArrayList<>(this.points.size());

        for (int i = 0; i < this.points.size(); i++) {
            pos.add(Position.fromCoordinates(this.points.get(i).getLongitude(), this.points.get(i).getLatitude()));
        }

        MapboxDirections.Builder builder = new MapboxDirections.Builder();
        builder.setCoordinates(pos)
                .setOverview(DirectionsCriteria.OVERVIEW_FULL)
                .setProfile(DirectionsCriteria.PROFILE_WALKING)
                .setAccessToken(BuildConfig.MAPBOX_TOKEN);

        MapboxDirections client = null;
        try {
            client = builder.build();
        } catch (ServicesException e) {
            e.printStackTrace();
            // TODO Error signalling
            return false;
        }

        if (client == null) {
            // TODO Error signalling
            return false;
        }

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    // TODO Error signalling
                    events.post(new DoneCalculatingDirectionsEvent(null));
                } else if (response.body().getRoutes().size() < 1) {
                    // TODO Error signalling
                    events.post(new DoneCalculatingDirectionsEvent(null));
                } else {
                    // TODO On success
                    events.post(new DoneCalculatingDirectionsEvent(response.body().getRoutes().get(0)));
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                // TODO Error signalling
                events.post(new DoneCalculatingDirectionsEvent(null));
            }
        });
        return true;
    }
}
