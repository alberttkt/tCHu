package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.util.StringConverter;

/**
 * Class that defines concrete versions of StringConverter to print a SortedBag of Card
 *
 * @author Albert Troussard (330361)
 * @author Menelik Nouvellon (328132)
 */
public class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

    /**
     * @param cards Card
     * @return turns multiset of cards into string
     */
    @Override
    public String toString(SortedBag<Card> cards) {
        return StringsFr.sortedBagName(cards);
    }

    /**
     * @param string String
     * @return simply throws an exception of type UnsupportedOperationException because it is never used in this context
     */
    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
