package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Class representing the known informations about cards in common (in the PublicGame)
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public class PublicCardState {
    private final List<Card> faceUpCards;
    private final int deckSize, discardsSize;

    /**
     * Constructor of PublicCardState
     *
     * @param faceUpCards  the face up cards ont the game
     * @param deckSize     the size of the Deck
     * @param discardsSize the size of the discard deck
     * @throws IllegalArgumentException if faceUpCards does not contain the correct number of elements (5),
     *                                  or if the size of the draw pile or discard pile is negative
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument((faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT) && (deckSize >= 0) && (discardsSize >= 0));
        List<Card> copy = List.copyOf(faceUpCards);
        this.faceUpCards = copy;
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * @return size of all the public cards
     */
    public int totalSize() {
        return Constants.FACE_UP_CARDS_COUNT + deckSize + discardsSize;
    }

    /**
     * @return list of all faceUpCards
     */
    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     * @param slot position of the card
     * @return the card at the <i>slot</i> position in the face Up Cards
     * @throws IndexOutOfBoundsException if <i>slot</i> is not between 0 (inclusive) and 5(exclusive)
     */
    public Card faceUpCard(int slot) {
        return faceUpCards.get(Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT));
    }

    /**
     * @return size of the deck
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * @return if the deck is empty
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     * @return the size of the discard deck
     */
    public int discardsSize() {
        return discardsSize;
    }


}
