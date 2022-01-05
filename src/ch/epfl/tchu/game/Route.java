package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Objects.requireNonNull;

/**
 * Class representing a route
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public final class Route {
    /**
     * <b>Enumeration of the different level:</b>
     * <li><i>OVERGROUND</i>
     * <li><i>UNDERGROUND</i>
     */
    public enum Level {OVERGROUND, UNDERGROUND,SKY}

    private final String id;
    private final Station station1, station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * <b>Constructor of a Route</b>
     *
     * @param id       id of the Route
     * @param station1 Station of departure  of the Route
     * @param station2 end Station of the Route
     * @param length   length of the Route
     * @param level    Level of the Route(<i>Overground</i> or <i>Underground</i>)
     * @param color    color of the Route
     * @throws IllegalArgumentException if <i>station1</i> = <i>station2</i> or if the <i>length</i> is not between 0 and 6.
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument((!station1.equals(station2)) && (length <= Constants.MAX_ROUTE_LENGTH) && (length >= Constants.MIN_ROUTE_LENGTH));
        if (station1 == null || station2 == null || id == null || level == null) throw new NullPointerException();
        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.length = length;
        this.level = level;
        this.color = color;
    }

    /**
     * <b>Getter of the Route's id</b>
     *
     * @return id of the route
     */
    public String id() {
        return id;
    }

    /**
     * <b>Getter of the Route's Station of departure</b>
     *
     * @return station1 of the route
     */
    public Station station1() {
        return station1;
    }

    /**
     * <b>Getter of the Route's end Station</b>
     *
     * @return station2 of the route
     */
    public Station station2() {
        return station2;
    }

    /**
     * <b>Getter of the Route's length</b>
     *
     * @return length of the route
     */
    public int length() {
        return length;
    }

    /**
     * <b>Getter of the Route's level</b>
     *
     * @return if it's a tunnel or a route
     */
    public Level level() {
        return level;
    }

    /**
     * <b>Getter of the Route's color</b>
     *
     * @return color of the deck
     */
    public Color color() {
        return color;
    }

    /**
     * <b>Getter of a list of Route's both Stations</b>
     *
     * @return the list of the connected stations
     */
    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();
        stations.add(station1);
        stations.add(station2);
        return stations;
    }

    /**
     * Compute the opposite station of a given station
     *
     * @param station the station you want the opposite of
     * @return the opposite station (station1 for station2 and vice-versa)
     * @throws IllegalArgumentException if the 2 stations are the same
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument((station.equals(station1) || station.equals(station2)));
        return (station.equals(station1)) ? station2 : station1;
    }

    /**
     * <b>Compute the ways for a player to claim the Route</b>
     *
     * @return the different ways for the player to choose his cards to claim the route
     */
    public List<SortedBag<Card>> possibleClaimCards() {

    if(level==Level.SKY) return List.of(SortedBag.of(Card.PLANE));


        List<SortedBag<Card>> result = new ArrayList<>();

        //if it's a non-coloured route
        if (this.color == null) {
            if (level.equals(Level.UNDERGROUND)) {
                for (int i = 0; i < length; ++i) {
                    int a = i;
                    Card.CARS.forEach(card -> {
                        SortedBag<Card> r = SortedBag.of(length - a, card, a, Card.LOCOMOTIVE);
                        if (!r.isEmpty()) result.add(r);
                    });
                }
                result.add(SortedBag.of(length, Card.LOCOMOTIVE));
            } else {
                Card.CARS.forEach(card -> result.add(SortedBag.of(length, card)));
            }
        }
        //if it's a coloured route
        else {
            if (level.equals(Level.UNDERGROUND)) {
                for (int i = 0; i < length + 1; ++i) {
                    SortedBag<Card> r = SortedBag.of(length - i, Card.of(this.color), i, Card.LOCOMOTIVE);
                    if (!r.isEmpty()) result.add(r);
                }
            } else {
                result.add(SortedBag.of(length, Card.of(this.color)));
            }
        }
        return result;
    }


    /**
     * <b>Compute the number of Card a player need to add to claim the Route</b>
     *
     * @param claimCards card used by the player who tried to claim the Route
     * @param drawnCards cards drawn by the player
     * @return Number of additional cards to add th claim the tunnel
     * @throws IllegalArgumentException if:<li> the Route is not a tunnel
     *                                  <li> the number of drawn cards is different of what it has to be
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        //Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);
        if(level==Level.SKY) return new Random().nextInt(4);
        int additional = 0;
        for (Card drawn : drawnCards) {
            boolean isIn = false;
            for (Card claim : claimCards) {
                if (drawn.color() == claim.color() || drawn.color() == null) {
                    isIn = true;
                    break;
                }
            }
            if (isIn) additional++;
        }
        return additional;
    }

    /**
     * Compute the points a player earn when he claims the Route
     *
     * @return the number of building points when the player takes a route
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }
}
