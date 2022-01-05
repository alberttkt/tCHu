package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * <b>Class representing the information of a player knowned by the other player</b>
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 *
 */
public class PublicPlayerState {
    private final int ticketCount,
            cardCount,
            carCount,
            claimPoints;
    private final List<Route> routes;

    /**
     * Constructor of a PublicPlayerState
     *
     * @param ticketCount number of Tickets of the player
     * @param cardCount number of Cards of the player
     * @param routes list of Routes claimed by the player
     * @throws IllegalArgumentException if the number of tickets or the number of cards is strictly negative
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument((ticketCount >= 0) && (cardCount >= 0));
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
       carCount = Constants.INITIAL_CAR_COUNT - routes.stream()
                                                .mapToInt(Route::length).sum();

       claimPoints = routes.stream()
                     .mapToInt(Route::claimPoints)
                     .sum();
    }

    /**
     * <b>Getter of the Ticket's count</b>
     *
     * @return player's number of ticket
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * <b>Getter of the player's cards count</b>
     *
     * @return player's number of cards
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * <b>Getter of the player's car count</b>
     *
     * @return player's number of car
     */
    public int carCount() {
        return carCount;
    }

    /**
     * <b>Getter of the player's claimed points</b>
     *
     * @return the number of construction points obtained by the player when he claimed routes
     */
    public int claimPoints() {
        return claimPoints;
    }

    /**
     * <b>Getter of the Routes claimed by the player</b>
     *
     * @return the roads that the player has seized
     */
    public List<Route> routes() {
        return routes;
    }
}
