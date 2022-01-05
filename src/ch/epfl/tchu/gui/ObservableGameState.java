package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * Class that represents the observable state of a game of tCHu
 *
 * @author Albert Troussard (330361)
 * @author Menelik Nouvellon (328132)
 */
public class ObservableGameState {
    private final PlayerId owner;
    private PublicGameState publicGameState;
    private PlayerState playerState;


    //PublicGameState
    private final IntegerProperty gameTicketsPercentage, cardStateDeckPercentage;
    private final List<ObjectProperty<Card>> cardStateFUC;

    private final ObjectProperty<PlayerId> gameCurrentPlayerId, gameLastPlayer;
    private final Map<Route, ObjectProperty<PlayerId>> routes;


    //Public PlayerStates
    private final Map<PlayerId, IntegerProperty> playerCarCount, playerPoints, playerTicketCount, playerCardCount;

    //Private PlayerState
    private final ObservableList<Ticket> playerTickets;
    private final Map<Card, IntegerProperty> playerCardsCount;
    private final Map<Route, BooleanProperty> claimableRoute;

    /**
     * Constructor of ObservableGameState
     *
     * @param owner the identity of the player to whom it corresponds
     */
    public ObservableGameState(PlayerId owner) {
        this.owner = owner;
        this.gameTicketsPercentage = new SimpleIntegerProperty();
        this.cardStateDeckPercentage = new SimpleIntegerProperty();
        this.cardStateFUC = createListProperties();
        this.gameCurrentPlayerId = new SimpleObjectProperty();
        this.gameLastPlayer = new SimpleObjectProperty();
        this.routes = createMapPropertiesRoutes();
        this.playerCarCount = createMapPropertiesInteger();
        this.playerPoints = createMapPropertiesInteger();
        this.playerTicketCount = createMapPropertiesInteger();
        this.playerCardCount = createMapPropertiesInteger();
        this.playerTickets = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.playerCardsCount = createMapPropertiesCard();
        this.claimableRoute = createMapPropertiesClaimableRoutes();
    }


    /**
     * Method that update the state it contains
     *
     * @param publicGameState the public part of the game
     * @param playerState     the complete state of the player to which it corresponds
     */
    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        this.publicGameState = publicGameState;
        this.playerState = playerState;


        // ----------- PublicGameState ----------- //

        //Tickets percentage
        int i = (100 * publicGameState.ticketsCount() / ChMap.tickets().size());
        gameTicketsPercentage.setValue(i);

        //Card deck percentage
        i = 100 * publicGameState.cardState().deckSize() / Constants.TOTAL_CARDS_COUNT;
        cardStateDeckPercentage.setValue(i);

        //Face Up Cards
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = publicGameState.cardState().faceUpCard(slot);
            cardStateFUC.get(slot).set(newCard);
        }


        //current playerId
        PlayerId newCurrent = publicGameState.currentPlayerId();
        gameCurrentPlayerId.setValue(newCurrent);

        //last playerId
        PlayerId newLast = publicGameState.currentPlayerId();
        gameLastPlayer.setValue(newLast);


        //Routes associated to PlayerId
        for (Route rt : ChMap.routes()) {
            if (publicGameState.claimedRoutes().contains(rt)) {
                PlayerId id = playerState.routes().contains(rt) ? owner : owner.next();
                routes.get(rt).setValue(id);
            }
        }


        //----------- Public PlayerStates -----------//

        //Players Attributes
        for (PlayerId playerId : PlayerId.values()) {
            playerCarCount.get(playerId).setValue(publicGameState.playerState(playerId).carCount());
            playerPoints.get(playerId).setValue(publicGameState.playerState(playerId).claimPoints());
            playerCardCount.get(playerId).setValue(publicGameState.playerState(playerId).cardCount());
            playerTicketCount.get(playerId).setValue(publicGameState.playerState(playerId).ticketCount());
        }


        //----------- Private PlayerState -----------//

        //number of private tickets
        playerTickets.setAll(playerState.tickets().toList());

        // Number of each card
        for (Card c : Card.ALL) {
            i = playerState.cards().countOf(c);
            playerCardsCount.get(c).setValue(i);
        }

        //If the player could or not claim each Route
        for (Route r : ChMap.routes()) {
            claimableRoute.get(r).setValue(canClaimRoute(r));
        }
    }

    private Map<PlayerId, IntegerProperty> createMapPropertiesInteger() {
        Map<PlayerId, IntegerProperty> map = new HashMap<>();
        for (PlayerId playerId : PlayerId.values()) {
            map.put(playerId, new SimpleIntegerProperty());
        }
        return map;
    }

    private Map<Route, ObjectProperty<PlayerId>> createMapPropertiesRoutes() {
        Map<Route, ObjectProperty<PlayerId>> map = new HashMap<>();
        for (Route rt : ChMap.routes()) {
            map.put(rt, new SimpleObjectProperty<>());
        }
        return map;
    }

    private Map<Card, IntegerProperty> createMapPropertiesCard() {
        Map<Card, IntegerProperty> map = new HashMap<>();
        for (Card cd : Card.ALL) {
            map.put(cd, new SimpleIntegerProperty());
        }
        return map;
    }

    private <T> List<ObjectProperty<T>> createListProperties() {
        return List.of(new SimpleObjectProperty<>(), new SimpleObjectProperty<>(), new SimpleObjectProperty<>(),
                new SimpleObjectProperty<>(), new SimpleObjectProperty<>());
    }

    private Map<Route, BooleanProperty> createMapPropertiesClaimableRoutes() {
        Map<Route, BooleanProperty> map = new HashMap<>();
        for (Route rt : ChMap.routes()) {
            map.put(rt, new SimpleBooleanProperty());
        }
        return map;
    }

    private boolean canClaimRoute(Route rt) {
        return gameCurrentPlayerId.getValue().equals(owner) &&
                routes.get(rt).getValue() == null &&
                neighborFree(rt) &&
                playerState.canClaimRoute(rt);
    }

    private boolean neighborFree(Route rt) {
        for (Route route : ChMap.routes()) {
            if ((route.station1() == rt.station1()) &&
                    (route.station2() == rt.station2()) &&
                    (!(routes.get(route).get() == null))) {
                return false;
            }
        }
        return true;
    }


    /**
     * @return owner of the class
     */
    public PlayerId getOwner() {
        return owner;
    }

    /**
     * @return the percentage of tickets left as Property of Integer
     */
    public ReadOnlyIntegerProperty gameTicketsPercentageProperty() {
        return gameTicketsPercentage;
    }

    /**
     * @return the percentage of cards left in the deck as Property of Integer
     */
    public ReadOnlyIntegerProperty cardStateDeckPercentageProperty() {
        return cardStateDeckPercentage;
    }

    /**
     * @param slot index
     * @return the card at <i>slot</i> index in the faceUpCard as a ReadOnlyObjectProperty of Card
     */
    public ReadOnlyObjectProperty<Card> cardStateFUC(int slot) {
        return cardStateFUC.get(slot);
    }

    /**
     * @return the current player's PlayerId as a ReadOnlyObjectProperty of PlayerId
     */
    public ReadOnlyObjectProperty<PlayerId> gameCurrentPlayerIdProperty() {
        return gameCurrentPlayerId;
    }

    /**
     * @return the last player's PlayerId as a ReadOnlyObjectProperty of PlayerId
     */
    public ReadOnlyObjectProperty<PlayerId> gameLastPlayerProperty() {
        return gameLastPlayer;
    }

    /**
     * @param rt Route we want information on
     * @return the player's PlayerId that owns the route or null if none of the players owns the route
     */
    public ReadOnlyObjectProperty<PlayerId> routesProperty(Route rt) {
        return routes.get(rt);
    }

    /**
     * @return the number of cars owned by the player associated to given <i>playerId</i> as a ReadOnlyIntegerProperty
     */
    public ReadOnlyIntegerProperty playerCarCountProperty(PlayerId playerId) {
        return playerCarCount.get(playerId);
    }

    /**
     * @return the number of points that the player associated to given <i>playerId</i> has as a ReadOnlyIntegerProperty
     */
    public ReadOnlyIntegerProperty playerPointsProperty(PlayerId playerId) {
        return playerPoints.get(playerId);
    }

    /**
     * @return the number of Ticket owned by the player associated to given <i>playerId</i> as a ReadOnlyIntegerProperty
     */
    public ReadOnlyIntegerProperty playerTicketCountProperty(PlayerId playerId) {
        return playerTicketCount.get(playerId);
    }

    /**
     * @return the number of Cards owned by the player associated to given <i>playerId</i> as a ReadOnlyIntegerProperty
     */
    public ReadOnlyIntegerProperty playerCardCountProperty(PlayerId playerId) {
        return playerCardCount.get(playerId);
    }

    /**
     * @return the current player's tickets as an ObservableList of Ticket
     */
    public ObservableList<Ticket> getPlayerTickets() {
        return FXCollections
                .unmodifiableObservableList(playerTickets);
    }


    /**
     * @param cd Card
     * @return the number of <i>cd</i> cards that the current player has as a ReadOnlyIntegerProperty
     */
    public ReadOnlyIntegerProperty playerCardsCountProperty(Card cd) {
        return playerCardsCount.get(cd);
    }

    /**
     * @param rt Route
     * @return if whether or not <i>rt</i> is claimable as a ReadOnlyBooleanProperty
     */
    public ReadOnlyBooleanProperty getClaimableRoute(Route rt) {
        return claimableRoute.get(rt);
    }

    /**
     * @return false if the number of ticket is 0 and true if it's higher than 0
     */
    public boolean canDrawTickets() {
        return publicGameState.canDrawTickets();
    }

    /**
     * @return true if players can drawn a card ie if the sum of deck size and discard
     * size is bigger than the number of face up cards
     */
    public boolean canDrawCards() {
        return publicGameState.canDrawCards();
    }

    /**
     * @param rt the given route
     * @return all possible set of cards that the player can use to take on the route
     * @throws IllegalArgumentException if the player does not have enough cars to take the road
     */
    public List<SortedBag<Card>> possibleClaimCards(Route rt) {
        return playerState.possibleClaimCards(rt);
    }

    /**
     * @return playerState
     */
    public PlayerState getPlayerState() {
        return playerState;
    }
}
