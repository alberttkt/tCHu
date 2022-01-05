package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.TreeSet;

/**
 * <b>Class representing a Ticket</b>I
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 *
 */
public final class Ticket implements Comparable<Ticket> {
    List<Trip> trips;
    private final String text;

    /**
     * <b>Constructor of a Ticket</b>
     * @param trips list of Trip that a player can do to complete the ticket
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(trips.size() > 0);
        Station from = trips.get(0).from();
        for (Trip tp : trips) {
            Preconditions.checkArgument(tp.from().name().equals(from.name()));
        }
        this.trips = List.copyOf(trips);
        text = computeText(trips);
    }

    /**
     * <b>Constructor of a Ticket</b>
     *
     * @param from Begin Station of the Ticket
     * @param to End Station of the ticket
     * @param points number of point the ticket can bring
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }



    /**
     * <b>Compute the points that the ticket bring to the player </b>
     * @param connectivity an instance of a class implementing StationConnectivity and who represent the link between stations given the route of a player
     * @return the number of points of a Ticket
     */
    public int points(StationConnectivity connectivity) {
        int points = 0;
        int min = trips.get(0).points();
        for (Trip trip : trips) {
            if(trip.points()<min) min= trip.points();
            points = Math.max(points, trip.points(connectivity));
        }
        return points == 0 ? -min : points ;
    }

    /**
     *<b>Call a private method to compute The textual representation of a Ticket</b>
     * <p>Different representation if it's:
     * <li>a city-city ticket(eg <i>Lausanne-Berne(4)</i>)
     * <li>a city-country(eg <i>Berne - {Allemagne (6), Autriche (11), France (5), Italie (8)}</i>)
     * <li>a country-country(eg <i>France - {Allemagne (5), Autriche (14), Italie (11)}</i>)</p>
     * @return textual representation of a Ticket
     */
    public String text() {
        return text;
    }



    /**
     * @param trp list of Trips of the ticket
     * @return textual representation of a Ticket
     */
    private static String computeText(List<Trip> trp) {
        TreeSet<String> trips = new TreeSet<>();
        trp.forEach(trip -> trips.add(trip.to() + " (" + trip.points() + ")"));
        StringBuilder s=new StringBuilder();

        s.append((trips.size()==1)
                ? (String.format("%s - %s (%s)", trp.get(0).from(), trp.get(0).to(), trp.get(0).points()))
                :(trp.get(0).from() + " - {" + String.join(", ", trips) + "}"));

        return s.toString();
    }


    public List<Trip> getTrips(){
        return trips;
    }





    /**
     *<b>Redefinition of the method compareTo of Object</b>
     *
     * @param that Ticket you want to compare with this Ticket
     *
     * @return result of comparison ( negative if this < that, positive if this > that and 0 is this == that )
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text.compareTo(that.text());
    }

    @Override
    public String toString() {
        return text();
    }
}
