package io.jawg.osmcontributor.api;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

/**
 * This class provides the basic services to compute paths and routes
 */
public class RouteAPI {

    private static final String API_KEY = "";

    /**
     * This method returns a Polyline corresponding to the recommended path from startPoint to
     * endPoint by foot.
     * @param startPoint GPS coordinates of the start point
     * @param endPoint GPS coordinates of the end point
     * @return a polyline to be drawn on a map ? what do you think ?
     */
    public static Polyline getRoute(GeoPoint startPoint, GeoPoint endPoint) {
        MapQuestRoadManager roadManager = new MapQuestRoadManager(API_KEY);
        roadManager.addRequestOption("routeType=pedestrian");


        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        waypoints.add(endPoint);

        Road road = roadManager.getRoad(waypoints);

        //TODO Perform accessibility checks


        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);


        return roadOverlay;
    }
}
