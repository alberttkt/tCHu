package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Class representing card's in common between players
 * extends publicCardState
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public final class CardState extends PublicCardState {
    //Attributes
    private final Deck<Card> deck;
    private final SortedBag<Card> discardDeck;

    //Private Constructor

    /**
     * Private constructor of a CardState
     *
     * @param faceUpCards List of Cards visible for the different players
     * @param deck        The deck of cards,not visible from players
     * @param discardDeck SortedBag of discarded cards
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discardDeck) {
        super(faceUpCards, deck.size(), discardDeck.size());
        this.deck = deck;
        this.discardDeck = discardDeck;
    }

    //public and static construction method

    /**
     * Public and static construction method
     *
     * @param deck A deck of cards
     * @return a state in which the 5 cards placed face up are the first 5 cards of the given pile,
     * the draw pile consists of the remaining cards of the pile, and the discard pile is empty.
     * @throws IllegalArgumentException if size of deck is inferior to 5 (the number of faceUpCards)
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);
        SortedBag<Card> fUCards = deck.topCards(Constants.FACE_UP_CARDS_COUNT);
        return new CardState(fUCards.toList(), deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT), SortedBag.of());
    }

    /**
     * @param slot The slot from which you want to change the card
     * @return replace one of the face-up-cards at index slot by deck's top card
     * @throws IllegalArgumentException if the deck is empty
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(!isDeckEmpty());
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

        List<Card> faceUpCards = new ArrayList<>(faceUpCards());
        faceUpCards.set(slot, deck.topCard());
        return new CardState(faceUpCards, deck.withoutTopCard(), discardDeck);
    }

    /**
     * @return the top deck card
     * @throws IllegalArgumentException if the deck is empty
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());
        return deck.topCard();
    }

    /**
     * @return The deck without the top card
     * @throws IllegalArgumentException if deck is empty
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());
        return new CardState(faceUpCards(), deck.withoutTopCard(), discardDeck);
    }

    /**
     * @param rng random
     * @return a new deck from the old discard deck shuffled with the random rng
     * @throws IllegalArgumentException if deck is not empty
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(isDeckEmpty());
        return new CardState(faceUpCards(), Deck.of(discardDeck, rng), SortedBag.of());
    }

    /**
     * @param additionalDiscards Cards you want to add to the discard
     * @return a set of cards identical to the receiver (this), but with the given cards added to the discard pile.
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        SortedBag<Card> realAdd;
        if(additionalDiscards.contains(Card.PLANE)){
            int count=additionalDiscards.countOf(Card.PLANE);
            realAdd = additionalDiscards.difference(SortedBag.of(count,Card.PLANE));
        }else{
            realAdd=additionalDiscards;
        }
        return new CardState(faceUpCards(), deck, discardDeck.union(realAdd));

    }
}
