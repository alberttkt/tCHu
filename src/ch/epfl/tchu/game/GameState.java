package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * <b>Class representing the game state unknown by players</b>
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public final class GameState extends PublicGameState {
    private final Deck<Ticket> tickets;
    private final Map<PlayerId, PlayerState> playerStateMap;
    private final CardState cardState;


    /**
     * private constructor of GameState
     *
     * @param tickets         the deck of tickets
     * @param currentPlayerId the id of the current player
     * @param lastPlayer      the id of the last player
     * @param playerStateMap  a map with player id's linked with their PlayerState
     * @param cardState       the cardState of the game
     */
    private GameState(Deck<Ticket> tickets, PlayerId currentPlayerId, PlayerId lastPlayer, Map<PlayerId, PlayerState> playerStateMap, CardState cardState) {
        super(tickets.size(), cardState, currentPlayerId, Map.copyOf(playerStateMap), lastPlayer);
        this.tickets = tickets;
        this.playerStateMap = Map.copyOf(playerStateMap);
        this.cardState = cardState;
    }


    /**
     * <b>static method to create a GameState</b>
     *
     * @param tickets SortedBag of all the tickets of the game
     * @param rng     random to shuffle the different decks
     * @return a GameState in witch the cards has been distributed to players and ready to begin the game
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        Deck<Card> deck = Deck.of(Constants.ALL_CARDS, rng);
        Map<PlayerId, PlayerState> map = new EnumMap<>(PlayerId.class);

        for (PlayerId id : PlayerId.ALL) {
            PlayerState state = PlayerState.initial(deck.topCards(Constants.INITIAL_CARDS_COUNT));
            deck = deck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
            map.put(id, state);
        }

        int firstPlayer = rng.nextInt(PlayerId.COUNT);

        PlayerId playerBegin =PlayerId.ALL.get(firstPlayer);

        return new GameState(Deck.of(tickets, rng), playerBegin, null, map, CardState.of(deck));
    }

    /**
     * <b>Getter for the state of a given player</b>
     *
     * @param playerId id of the player you want the state
     * @return the state of the the player with id <i>playerId</i>>
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerStateMap.get(playerId);
    }

    /**
     * <b>Getter for the state of the current player</b>
     *
     * @return the state of the current player
     */
    @Override
    public PlayerState currentPlayerState() {
        return playerStateMap.get(currentPlayerId());
    }

    /**
     * @param count number of cards you want to pick
     * @return SortedBag of the <i>count</i> top tickets
     * @throws IllegalArgumentException if count is not between 0 and the size of the ticket deck (included)
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument( 0 <= count && count <= ticketsCount());
        return tickets.topCards(count);
    }

    /**
     * @param count number of cards you want to withdraw of the ticket's deck
     * @return a GameState without the <i>count</i> top tickets in the deck
     * @throws IllegalArgumentException if count is not between 0 and the size of the ticket deck (included)
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(0 <= count && count <= ticketsCount());
        return new GameState(tickets.withoutTopCards(count), currentPlayerId(), lastPlayer(), playerStateMap, cardState);
    }

    /**
     * @return the top card of the deck
     * @throws IllegalArgumentException if the card deck is empty
     */
    public Card topCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     * @return a gameState without the top deck card
     * @throws IllegalArgumentException if the card deck is empty
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return new GameState(tickets, currentPlayerId(), lastPlayer(), playerStateMap, cardState.withoutTopDeckCard());
    }

    /**
     * @param discardedCards a SortedBag of card who has to be discarded
     * @return a GameState with these cards added to the discard
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(tickets, currentPlayerId(), lastPlayer(), playerStateMap, cardState.withMoreDiscardedCards(discardedCards));
    }

    /**
     * <b>Recreate a card deck if it is empty</b>
     *
     * @param rng Random to shuffle the deck
     * @return a GameState in witch the card deck recreated from the discard
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        if (!cardState.isDeckEmpty()) return this;
        return new GameState(tickets, currentPlayerId(), lastPlayer(), playerStateMap, cardState.withDeckRecreatedFromDiscards(rng));
    }

    /**
     * <b>add the initially chosen tickets to the state of the player</b>
     *
     * @param playerId the id of the player
     * @param chosenTickets the tickets he has chosen
     * @return a GameState in witch the player of id <i>playerId</i> has chosen the tickets of <i>chosenTickets</i> at the beginning of the game
     * @throws IllegalArgumentException if the player already have a ticket
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerStateMap.get(playerId).tickets().isEmpty());

        PlayerState ps = playerStateMap.get(playerId).withAddedTickets(chosenTickets);

        Map newMap = new EnumMap(playerStateMap);
        newMap.put(playerId,ps);
        return new GameState(tickets, currentPlayerId(), lastPlayer(), newMap, cardState);
    }


    /**
     * <b>add the chosen tickets to the state of the player</b>
     *
     * @param drawnTickets  ticket drawn from the ticket deck
     * @param chosenTickets tickets from drawnTickets chosen by the current player
     * @return a GameState in witch the current player has now the <i>chosenTickets</i>
     * @throws IllegalArgumentException if drawnTickets don't contains chosenTickets
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        PlayerState ps = currentPlayerState().withAddedTickets(chosenTickets);
        Map newMap = new EnumMap(playerStateMap);
        newMap.put(currentPlayerId(),ps);
        return new GameState(tickets.withoutTopCards(drawnTickets.size()), currentPlayerId(), lastPlayer(),newMap, cardState);
    }

    /**
     * <b>add the chosen face up card to current player's state</b>
     *
     * @param slot slot of the card drawn by the player
     * @return a GameState in witch the current player has drawn the card <i>slot</i> of face up cards
     * and this card has been replaced by the top card of the deck
     */
    public GameState withDrawnFaceUpCard(int slot) {
        PlayerState ps = currentPlayerState().withAddedCard(cardState.faceUpCard(slot));
        Map newMap = new EnumMap(playerStateMap);
        newMap.put(currentPlayerId(),ps);
        return new GameState(tickets, currentPlayerId(), lastPlayer(), newMap, cardState.withDrawnFaceUpCard(slot));
    }

    /**
     * <b>add the top card of the deck to the current player's state</b>
     *
     * @return a GameState in witch the current player has drawn the card on the top of the deck
     */
    public GameState withBlindlyDrawnCard() {
        PlayerState ps = currentPlayerState().withAddedCard(cardState.topDeckCard());
        Map newMap = new EnumMap(playerStateMap);
        newMap.put(currentPlayerId(),ps);
        return new GameState(tickets, currentPlayerId(), lastPlayer(), newMap , cardState.withoutTopDeckCard());
    }

    /**
     * <b>add the route to the current player's state</b>
     *
     * @param route route claimed by the player
     * @param card  cards used to claim the route
     * @return a GameState in witch the current player has claimed <i>route</i> with <i>card</i>
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> card) {
        PlayerState ps = currentPlayerState().withClaimedRoute(route, card);
        Map newMap = new EnumMap(playerStateMap);
        newMap.put(currentPlayerId(),ps);
        return new GameState(tickets, currentPlayerId(), lastPlayer(), newMap, cardState.withMoreDiscardedCards(card));
    }

    /**
     * <b>Compute if the last turn begins</b>
     *
     * @return if the last turn has begin
     */
    public boolean lastTurnBegins() {
        return currentPlayerState().carCount() <= 2 && lastPlayer() == null;
    }

    /**
     * @return if last turn begins: <li>a GameState in witch the last player is the actual player  and the other player become the current player</li>
     * <br>else: <li>a GameState in witch the current player has change
     */
    public GameState forNextTurn() {
        return (lastTurnBegins())
                ? new GameState(tickets, currentPlayerId().next(), currentPlayerId(), playerStateMap, cardState)
                : new GameState(tickets, currentPlayerId().next(), lastPlayer(), playerStateMap, cardState);

    }
}
