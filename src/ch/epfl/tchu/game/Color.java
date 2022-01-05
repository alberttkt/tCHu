package ch.epfl.tchu.game;

import java.util.List;
/**
 * <b>Enumeration of colors taken by the cards:</b>
 * <i><li>BLACK <li>VIOLET <li>BLUE <li>GREEN <li>YELLOW <li>ORANGE <li>RED <li>WHITE</i>
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 *
 */
public enum Color {
    /**
     * All possible Colors
     */
    BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE;
    /**
     * List of all different colors
     */
    public static final List<Color> ALL = List.of(Color.values());
    /**
     * Number of different colors
     */
    public static final int COUNT = ALL.size();
}
