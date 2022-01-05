package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Class representing a Trail</b>
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public final class Trail {
    private static Trail EMPTY_TRAIL=new Trail(0,null,null,List.of());
    private final int length;
    private final Station station1, station2;
    private final List<Route> routes;

    /**
     * Private Constructor of a Trail
     *
     * @param length   length of the trail
     * @param station1 station 1 of the Trail
     * @param station2 station 2 of the Trail
     * @param routes   list of Route that constitute the Trail
     */
    private Trail(int length, Station station1, Station station2, List<Route> routes) {
        this.length = length;
        this.station1 = station1;
        this.station2 = station2;
        this.routes = routes;
    }

    /**
     * <b>Getter for the length of a Trail</b>
     *
     * @return the length of the Trail
     */
    public int length() {
        return length;
    }

    /**
     * <b>Getter for the Station 1 of a Trail</b>
     *
     * @return station 1 of the Trail
     */
    public Station station1() {
        if (length == 0) return null;
        return station1;
    }

    /**
     * @return station 2 of the Trail
     */
    public Station station2() {
        if (length == 0) return null;
        return station2;
    }

    /**
     * <b>Compute the longest Trail made of a list of Route</b>
     *
     * @param routes a list of Route
     * @return the longest Trail made of the routes of the given list <i>routes</i>
     */
    public static Trail longest(List<Route> routes) {
        //case when the list routes is empty
        if (routes.size() == 0) return EMPTY_TRAIL;
        List<Trail> oneRoute = new ArrayList<>();
        //Variable representing the longest Trail, initialised with a Trail made of the first route of 'routes'
        Trail maxLengthTrail = new Trail(routes.get(0).length(), routes.get(0).station1(), routes.get(0).station2(), List.of(routes.get(0)));

        //Iterate on 'routes' to create a list of Trails made of each Route (in both ways)
        for (Route route : routes) {
            Trail Trail1 = new Trail(route.length(), route.station1(), route.station2(), List.of(route));
            oneRoute.add(Trail1);
            if (Trail1.length() > maxLengthTrail.length()) maxLengthTrail = Trail1;
            Trail Trail2 = new Trail(route.length(), route.station2(), route.station1(), List.of(route));
            oneRoute.add(Trail2);
        }

        //for each iteration of the while loop, the number of route per Trail increase by 1
        //it stop when it's impossible to add a new Route to a trail for the previous iteration
        while (oneRoute.size() != 0) {
            List<Trail> aux = new ArrayList<>();

            //For each trail, iterate in routes to know if you can add them at the end of the Trail
            for (Trail tr : oneRoute) {
                for (Route route : routes) {

                    if (!(tr.routes.contains(route))) {
                        List<Route> copy = new ArrayList<>(tr.routes);
                        Trail addTrail ;



                        for (Station s : route.stations()) {
                            if (tr.station2 == s) {
                                copy.add(route);
                                addTrail = new Trail(tr.length() + route.length(), tr.station1(), route.stationOpposite(s), copy);
                                aux.add(addTrail);
                                if (addTrail.length() > maxLengthTrail.length()) maxLengthTrail = addTrail;

                            }
                        }
                    }
                }
            }
            //the list with Trail of 1 more route become the new base list
            oneRoute = aux;
        }
        return maxLengthTrail;
    }


    /**
     * Redefinition of the method toString for a Trail
     *
     * @return Trail with between Stations
     */
    @Override
    public String toString() {
        if (length == 0) return "--EMPTY Trail--";
        StringBuilder s = new StringBuilder();
        Station st = station1;
        s.append(st.name());
        for (Route rt : routes) {
            s.append(" - " + rt.stationOpposite(st));
            st = rt.stationOpposite(st);
        }
        s.append(String.format(" (%s)", length));
        return s.toString();
    }
}
