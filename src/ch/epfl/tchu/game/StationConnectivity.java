package ch.epfl.tchu.game;
/**
 *<b>Interface of the connectivity between stations</b>
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 *
 */
public interface StationConnectivity {


    /**
     * Compute if the player with the StationPartition have linked both stations
     *
     * @param s1 Station 1
     * @param s2 Station 2
     * @return if both station are connected
     */
    public abstract boolean connected(Station s1, Station s2);
}
