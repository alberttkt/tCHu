package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enumeration of Card's type:
 * <i><li>BLACK <li>VIOLET <li>BLUE <li>GREEN <li>YELLOW <li>ORANGE <li>RED <li>WHITE<li>LOCOMOTIVE</i>
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public enum Card {
    /**
     * All possible cards
     */
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null),
    PLANE(null);

    /**
     * List of all colored cards
     */
    public final static List<Card> CARS = List.of(Card.values()).subList(0, Color.COUNT);

    /**
     * List of all cards
     */
    public static final List<Card> ALL = List.of(Card.values());

    /**
     * Number of card's type
     */
    public static final int COUNT = ALL.size();
    public final Color color;

    /**
     * Constructor of a Card
     *
     * @param color
     */
    Card(Color color) {
        this.color = color;
    }

    /**
     * Compute the Card of given color
     *
     * @param color color you the card of
     * @return Card of Color <i>color</i>
     */
    public static Card of(Color color) {

        for (Card card : Card.values()) {
            if (card.color == color) return card;
        }
        return null;
    }

    /**
     * Getter of the Card's color
     *
     * @return Color of card
     */
    public Color color() {
        return color;
    }

}
