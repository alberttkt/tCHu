package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class representing the state of a player
 * extends PublicPlayerState
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public final class PlayerState extends PublicPlayerState {
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;


    /**
     * Constructor of a PlayerState
     *
     * @param tickets tickets owned by the player
     * @param cards   cards owned by the player
     * @param routes  routes claimed by the player
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;

    }

    /**
     * <b>Initialise the PlayerState at the begin of the game</b>
     *
     * @param initialCards card initially on player's hand
     * @return initial state of a player to whom the given initial cards have been dealt
     * @throws IllegalArgumentException if the number of card is not Constants.INITIAL_CARDS_COUNT
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * <b>Getter for player's tickets</b>
     *
     * @return player's ticket
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * <b>Getter for player's cards</b>
     *
     * @return player's cards
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * <b>Add the given tickets in player's hand</b>
     *
     * @param newTickets a SortedBag of all the new tickets he has
     * @return PlayerState with additional tickets
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes());
    }


    /**
     * <b>Add the given card in player's hand</b>
     *
     * @param card the new card he has
     * @return PlayerState with the additional card
     */
    public PlayerState withAddedCard(Card card) {
        return withAddedCards(SortedBag.of(card));
    }

    /**
     * <b>Add the given cards in player's hand</b>
     *
     * @param additionalCards a SortedBag of all the new cards he has
     * @return PlayerState with additional cards
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, cards.union(additionalCards), routes());
    }

    /**
     * <b>Compute if the player can claim the given Route</b>
     *
     * @param route Route the player wants to claim
     * @return whether the player can take the route
     */
    public boolean canClaimRoute(Route route) {

        for (SortedBag<Card> possibleClaimCards : route.possibleClaimCards()) {
            if (cards.contains(possibleClaimCards) && (carCount() >= route.length())) return true;
        }
        return false;
    }

    /**
     * @param route the given route
     * @return all possible set of cards that the player can use to take on the route
     * @throws IllegalArgumentException if the player does not have enough cars to take the road
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(carCount() >= route.length());


        if (route.level().equals(Route.Level.SKY)) {
            return route.possibleClaimCards();
        }


        Color colorRoute = route.color();
        List<SortedBag<Card>> result = new ArrayList<>();
        if (cards.isEmpty()) return List.of();


        Set<SortedBag<Card>> allPossibilities = cards.subsetsOfSize(route.length());

        if (colorRoute != null) {
            for (SortedBag<Card> sd : allPossibilities) {
                int aux = 0;
                for (Card cd : sd) {
                    if (route.level() == Route.Level.UNDERGROUND)
                        if ((cd.color() == route.color()) || (cd.color() == null)) aux += 1;
                    if (route.level() == Route.Level.OVERGROUND) if (cd.color() == route.color()) aux += 1;
                }
                if (aux == sd.size()&&!(sd.contains(Card.PLANE))) result.add(sd);
            }
        } else {
            for (Color color : Color.ALL) {
                for (SortedBag<Card> sd : allPossibilities) {
                    int aux = 0;
                    for (Card cd : sd) {
                        if (route.level() == Route.Level.UNDERGROUND)
                            if ((cd.color() == color) || (cd.color() == null)) aux += 1;
                        if (route.level() == Route.Level.OVERGROUND) if (cd.color() == color) aux += 1;
                    }
                    if (aux == sd.size()&&!(sd.contains(Card.PLANE))) result.add(sd);
                }
            }
        }
        List<SortedBag<Card>> list2= result.stream().distinct().collect(Collectors.toList());
        list2.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return list2;
    }

    /**
     * <b>Compute the cards that the player can discard to claim a tunnel</b>
     *
     * @param additionalCardsCount number of card that need to be discarded
     * @param initialCards         Cards used initially by the player to try to claim the Route
     * @param drawnCards           Cards drawn by the player
     * @return all possible set of cards that the player can put after putting <i>initialCards</i>
     * to get a tunnel and picking up <i>drawncards</i>.
     * @throws IllegalArgumentException if the number of additional cards is not between 1 and 3 (inclusive),
     *                                  if the set of initial cards is empty or contains more than 2 different types of cards, or
     *                                  if the set of cards drawn does not exactly contain 3 cards
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS && initialCards.toSet().size() <= 2 && initialCards.size() != 0);


        if (initialCards.contains(Card.PLANE)) {
            if (cards.contains(SortedBag.of(additionalCardsCount, Card.LOCOMOTIVE)))
                return List.of(SortedBag.of(additionalCardsCount, Card.LOCOMOTIVE));
            return List.of();
        }


        SortedBag<Card> currentCards = cards.difference(initialCards);

        //Find the color of the tunnel
        Color color = null;
        for (Card card : initialCards.toSet()) {
            if (card.color() != null) {
                color = card.color();
                break;
            }
        }
        List<SortedBag<Card>> allPossibilities = new ArrayList<>();
        if (color != null) {

            for (int i = 0; i <= additionalCardsCount; ++i) {
                int numberOfColor = additionalCardsCount - i,
                        numberOfLocomotive = i;
                SortedBag.Builder<Card> r = new SortedBag.Builder<>();
                r.add(numberOfColor, Card.of(color));
                if (color != null) r.add(numberOfLocomotive, Card.LOCOMOTIVE);
                allPossibilities.add(r.build());
            }
        } else {
            allPossibilities.add(SortedBag.of(additionalCardsCount, Card.LOCOMOTIVE));
        }

        List<SortedBag<Card>> result = new ArrayList<>();
        for (SortedBag<Card> stc : allPossibilities) {
            if (currentCards.contains(stc)) result.add(stc);
        }
        return result;
    }

    /**
     * <b>Add the Route to the player's State</b>
     *
     * @param route      Routed claimed by the player
     * @param claimCards Cards used to claim the route
     * @return PlayerState with <i>route</i> and without <i>claimCards</i> that he used to take the route
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> copy = new ArrayList<>(routes());
        copy.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), copy);
    }

    /**
     * <b>Compute the points earned by the player with tickets</b>
     *
     * @return the number of points obtained by the player thanks to his tickets
     */
    public int ticketPoints() {
        int maxId = routes().stream()
                .mapToInt(r -> Math.max(r.station1().id(), r.station2().id()))
                .max().orElse(0);
        maxId++;
        StationPartition.Builder builder = new StationPartition.Builder(maxId);
        routes().forEach(route -> builder.connect(route.station1(), route.station2()));
        StationPartition partition = builder.build();
        int ticketPoint = tickets.stream()
                .mapToInt(t -> t.points(partition))
                .sum();
        return ticketPoint;
    }

    /**
     * Compute the final points of the player
     *
     * @return the total number of points obtained by the player at the end of the game
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
