package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * <b>Class representing a station</b>
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 *
 */

public final class Station {
    private final int id;
    private final String name;

    /**
     * <b>Constructor of a station</b>
     *
     * @param id id of the station
     * @param name name of the station
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;
    }

    /**
     * <b>Getter of station's id</b>
     * @return station's id
     */
    public int id() {
        return id;
    }

    /**
     * <b>Getter of station's name</b>
     * @return station's name
     */
    public String name() {
        return name;
    }

    /**
     * Redefinition of the ToString method for a Station
     *
     * @return the name of the station
     */
    @Override
    public String toString() {
        return name;
    }

}
