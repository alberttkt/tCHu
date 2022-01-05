package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * <b>Interface of a player</b>
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public interface Player {
    /**
     * <b>enumeration of the different kinds of turn:</b>
     * <li><i>DRAW_TICKETS</i>
     * <li><i>DRAW_CARDS</i>
     * <li><i>CLAIM_ROUTE</i>
     */
    public enum TurnKind {
        DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE;

        public static final List<TurnKind> ALL = List.of(TurnKind.values());
    }

    /**
     * communicate to the player his own ownId identity, as well as the names of the different players,
     * including his own
     *
     * @param ownId       player's identity
     * @param playerNames Map that maps a player to this name
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * this method is called when information must be communicated to the player during the game
     *
     * @param info the string that contains the information (from class Info)
     */
     void receiveInfo(String info);

    /**
     * this method is called when the gameState has changed to inform players of the new gameState
     *
     * @param newState new game state that needs to be updated to
     * @param ownState state of the concerned player
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * communicate to the player the five tickets that have been distributed
     *
     * @param tickets that have been distributed
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * ask the player which of the tickets he was initially given out he's keeping
     *
     * @return the tickets he has chosen
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * know what type of action the player wishes to perform during this turn
     *
     * @return the type of action he wants
     */
    TurnKind nextTurn();

    /**
     * called when the player has decided to draw additional tickets during the game,
     * in order to communicate the tickets drawn and to know which ones he is keeping
     *
     * @param options the ticket's option given to the player
     * @return the tickets he has chosen
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * ask if the player wants to draw a card in the faceUpCards or in the deck
     *
     * @return -1 if deck was chosen and slot_number ∈ [0,4] the number of the faceUpCard he wants to chose
     */
    int drawSlot();

    /**
     * called when the player has decided to (attempt to) seize a road, in order to know which road it is
     *
     * @return the road he wants to seize
     */
    Route claimedRoute();

    /**
     * called when the player has decided to (attempt to) seize a route,
     * in order to know which card (s) he initially wishes to use for this
     *
     * @return the cards he wants to use to seize the road
     */
    SortedBag<Card> initialClaimCards();

    /**
     * called when the player has decided to try to seize a tunnel and additional cards are needed,
     * in order to know which card (s) he wishes to use for this
     *
     * @param options the possibilities
     * @return the additional cards he wishes to use
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);
}
