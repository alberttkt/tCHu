
package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;


/**
 * <b>Class representing the station parition of a player</b>
 *
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public final class StationPartition implements StationConnectivity {
    private final int[] representative;

    /**
     * <b>Getter for the array of station and their representative</b>
     * @return the array of station representatives
     */
    private int[] representative() {
        return representative;
    }

    /**
     * Private builder of a Station partition
     *
     * @param representative array of station(the id is the index of the array) and their representative(value stored at the index)
     */

    private StationPartition(int[] representative) {
        this.representative = representative;
    }


    /**
     * Compute if the player with the StationPartition have linked both stations
     *
     * @param s1 First station
     * @param s2 Second station
     *
     * @return if both station are connected or not
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        return (s1.id() < representative.length && s2.id() < representative.length)//if the station id is in the array
                ?representative[s1.id()] == representative[s2.id()]
                :s1.id() == s2.id();//if not in the array, only way to be connected is the case in witch both stations are the same
    }

    /**
     * <b>Builder of StationPartition</b>
     */
    public static final class Builder {
        private int[] representative;

        /**
         * <b>Constructor of a StationPartitionBuilder</b>
         *
         * @param stationCount the maximal id of the stations you want to add in your representation
         *
         * @throws IllegalArgumentException if stationCount is negative
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            representative = new int[stationCount + 1];
            for (int i = 0; i <= stationCount; i++) {
                representative[i] = i;
            }

        }

        /**
         * <b>Modify the builder and connect both stations</b>
         *
         * @param s1 First station
         * @param s2 Second station
         *
         * @return the builder with a connection between both stations
         */
        public Builder connect(Station s1, Station s2) {
            //If both stations already have the same representative you don't need to link them another time
            if ((representative(s1.id()) != representative(s2.id()))) {
                //To avoid that a station pointing to a station can change its reference station and avoid loops
                if (representative[s2.id()] == s2.id())
                    representative[s2.id()] = s1.id();
                    //If both stations are already connected to an other one it choose one of the representative as the representative of both subsets
                 else if (representative[s2.id()] != s2.id() && representative[s1.id()] != s1.id())
                    representative[representative(s2.id())] = representative[representative(s1.id())];
                else if (representative[s1.id()] == s1.id())
                    representative[s1.id()] = s2.id();

            }
            return this;
        }

        /**
         * <b>Build the station partition</b>
         *
         * @return a StationPartition in a flattened version
         */
        public StationPartition build() {
            int[] finalrepresentative = new int[representative.length];
            for (int i = 0; i < representative.length; i++) {
                finalrepresentative[i] = representative(i);
            }
            return new StationPartition(finalrepresentative);
        }

        /**
         * @param id id of a station
         *
         * @return the representative of the station
         */
        private int representative(int id) {
            int v1,v2 = id;
            do {
                v1 = v2;
                v2 = representative[v1];
            }while (v1 != v2);

            return v1;
        }

    }
}
