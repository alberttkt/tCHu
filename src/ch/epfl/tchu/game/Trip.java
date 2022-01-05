package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * <b>Class representing a Trip</b>
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public final class Trip {
    private final Station from, to;
    private final int points;

    /**
     * <b>Constructor of a trip</b>
     *
     * @param from first station of a trip
     * @param to second station of a trip
     * @param points points of a trip
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }


    /**
     * <b>Getter for the first station of a trip</b>
     * @return station 1 (from where the trip starts)
     */
    public Station from() {
        return from;
    }

    /**
     * <b>Getter for the second station of a trip</b>
     * @return station 2 (to where the trip goes)
     */
    public Station to() {
        return to;
    }

    /**
     * <b>Getter for the points of a trip</b>
     * @return points of trip
     */
    public int points() {
        return points;
    }

    /**
     * <b>Compute the points that the trail bring to the player with StationConnectivity <i>connectivity</i></b>
     * <li>Add the points if stations trail is connected
     * <li>subtract if not
     * @param connectivity an instance of a class implementing StationConnectivity and who represent the link between stations given the route of a player
     * @return number of points of the trip for given <i>connectivity</i>
     */
    public int points(StationConnectivity connectivity) {
        if (connectivity.connected(from, to)) return points;
        else return -points;
    }

    /**
     * @param from list of begin stations
     * @param to list of end stations
     * @param points points of a trip
     * @return List of all possible trips from stations <i>from</i> to stations <i>to</i> with attributed points
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument((!from.isEmpty() && !to.isEmpty() && (points > 0)));
        List<Trip> trips = new ArrayList<Trip>();
        for (Station s1 : from) {
            for (Station s2 : to) {
                trips.add(new Trip(s1, s2, points));
            }
        }
        return trips;
    }
}
