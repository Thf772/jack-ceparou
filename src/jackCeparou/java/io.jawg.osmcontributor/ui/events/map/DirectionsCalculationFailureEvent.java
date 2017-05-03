package io.jawg.osmcontributor.ui.events.map;

import com.mapbox.services.directions.v5.models.DirectionsResponse;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Nathan Shiraini on 03/05/2017.
 */

public class DirectionsCalculationFailureEvent {
    private Call<DirectionsResponse> call;
    private Throwable throwable;
    public DirectionsCalculationFailureEvent(Call<DirectionsResponse> call, Throwable throwable) {
        this.call = call;
        this.throwable = throwable;
    }
    public Call<DirectionsResponse> getCall() {
        return this.call;
    }
    public Throwable getThrowable() {
        return this.throwable;
    }
    public void setCall(Call<DirectionsResponse> call) {
        this.call = call;
    }
    public void setResponse(Throwable throwable) {
        this.throwable = throwable;
    }
}
