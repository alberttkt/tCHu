package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <b>Class representing the game state known by players</b>
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public class PublicGameState {
    private final int ticketsCount;
    private final PlayerId currentPlayerId, lastPlayer;
    private final PublicCardState cardState;
    private final Map<PlayerId, PublicPlayerState> playerState;

    /**
     * Constructor of PublicGameState
     *
     * @param ticketsCount    the number of remaining tickets in the deck
     * @param currentPlayerId the id of the actual player
     * @param lastPlayer      id of the player who will end the game
     *                        null if the last turn hasn't begin
     * @param cardState       cardState of the game
     * @param playerState     A map with player id's linked with their publicPlayerState
     * @throws IllegalArgumentException if ticketsCount is negative or the number of player is different than 2
     * @throws NullPointerException     if cardState, playerState or currentPlayerId is null
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == PlayerId.COUNT);
        if (cardState == null || playerState == null || currentPlayerId == null) throw new NullPointerException();
        this.ticketsCount = ticketsCount;
        this.currentPlayerId = currentPlayerId;
        this.lastPlayer = lastPlayer;
        this.cardState = cardState;
        this.playerState = Map.copyOf(playerState);
    }

    /**
     * <b>Getter of ticketCount</b>
     *
     * @return ticketsCount
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * @return false if ticketsCount is 0 true if different
     */
    public boolean canDrawTickets() {
        return ticketsCount > 0;
    }

    /**
     * @return the CardState
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     * @return true if players can drawn a card ie if the sum of deck size and discard size is bigger than the number of face up cards
     */
    public boolean canDrawCards() {
        return cardState.deckSize() + cardState.discardsSize() >= Constants.FACE_UP_CARDS_COUNT;
    }

    /**
     * @return the id of current player
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * @param playerId of the player
     * @return the PublicPlayerState of this player
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * @return the PublicPlayerState of the current player
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     * @return the list of routes already claimed by any player
     */
    public List<Route> claimedRoutes() {
        List<Route> routes = new ArrayList<>();
        for (PlayerId id : PlayerId.ALL) {
            routes.addAll(playerState.get(id).routes());
        }
        return routes;
    }

    /**
     * @return the id of the last player
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }

}
