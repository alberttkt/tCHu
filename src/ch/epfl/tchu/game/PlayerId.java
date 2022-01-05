package ch.epfl.tchu.game;

import java.util.List;

/**
 * <b>Enumeration of player Id's</b>
 * <li><i>PLAYER_1</i>
 * <li><i>PLAYER_2</i>
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 *
 */
public enum PlayerId {
    PLAYER_1, PLAYER_2;
    /**
     * List of all playerId's
     */
    public static final List<PlayerId> ALL = List.of(PlayerId.values());

    /**
     * Number of playerId
     */
    public static final int COUNT = ALL.size();

    /**
     *
     * @return the id of the other player
     */
    public PlayerId next() {
        if (this.ordinal()!=COUNT-1){
            return ALL.get(ordinal()+1);
        }
        return ALL.get(0);
    }
}
