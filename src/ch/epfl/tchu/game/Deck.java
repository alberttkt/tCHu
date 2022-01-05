package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class representing a shuffled deck of card
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public final class Deck<C extends Comparable<C>> {
    //Attributes
    private final List<C> cards;

    //Private constructor

    /**
     * Private constructor of a Deck
     *
     * @param cards
     */
    private Deck(List<C> cards) {
        this.cards = cards;
    }

    //public and static construction method

    /**
     * @param cards The cards you want to shuffle
     * @param rng   A random to shuffle
     * @param <C>   Type of object you want to make a deck of
     * @return a deck with a sorted bag of cards and a random
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> cardList = cards.toList();
        Collections.shuffle(cardList, rng);
        return new Deck(cardList);
    }

    /**
     * Compute if the deck is empty
     *
     * @return if the deck is empty or not
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Getter for the Deck's size
     *
     * @return The size of deck
     */
    public int size() {
        return cards.size();
    }


    /**
     * Getter for the Deck's top Card
     *
     * @return the top card of the deck
     * @throws IllegalArgumentException if deck is empty
     */
    public C topCard() {
        Preconditions.checkArgument(!cards.isEmpty());
        return cards.get(0);
    }

    /**
     * Remove the top card of the Deck
     *
     * @return The deck without the top card
     * @throws IllegalArgumentException if the deck is empty
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!cards.isEmpty());
        return withoutTopCards(1);
    }

    /**
     * Getter for the Deck's top Cards
     *
     * @param count The number of top cards you want to take
     * @return the first <i>count</i> cards
     * @throws IllegalArgumentException if count is not between 0 (included) and the heap size (included)
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());
        return SortedBag.of(cards.subList(0, count));
    }

    /**
     * Remove the top cards of the Deck
     *
     * @param count The number of top cards you want to remove
     * @return the deck without the <i>count</i> top cards
     * @throws IllegalArgumentException if count is not between 0 (included) and the heap size (included)
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());
        return new Deck(cards.subList(count, cards.size()));

    }
}
