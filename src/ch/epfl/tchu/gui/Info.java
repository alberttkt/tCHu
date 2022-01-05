package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;

/**
 * Class representing the way to print different messages
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public final class Info {
    private final String playerName;

    /**
     * Constructor
     *
     * @param playerName String
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * @param route Route
     * @return the name of a route with good format(i.e Lausanne - Neuchâtel)
     */
    private static String routeName(Route route) {
        return route.station1().toString() + StringsFr.EN_DASH_SEPARATOR + route.station2().toString();
    }


    /**
     * @param trail Trail
     * @return name of trail with good format (i.e. Lausanne - Lucerne)
     */
    private static String trailName(Trail trail) {
        assert trail.station1() != null;
        assert trail.station2() != null;
        return trail.station1() + StringsFr.EN_DASH_SEPARATOR + trail.station2();
    }


    /**
     * @param card  Card
     * @param count int
     * @return name of card with 's' if there are multiple same cards
     */
    public static String cardName(Card card, int count) {
        String s = "";
        switch (card) {
            case BLACK:
                s = StringsFr.BLACK_CARD;
                break;
            case VIOLET:
                s = StringsFr.VIOLET_CARD;
                break;
            case BLUE:
                s = StringsFr.BLUE_CARD;
                break;
            case GREEN:
                s = StringsFr.GREEN_CARD;
                break;
            case YELLOW:
                s = StringsFr.YELLOW_CARD;
                break;
            case ORANGE:
                s = StringsFr.ORANGE_CARD;
                break;
            case RED:
                s = StringsFr.RED_CARD;
                break;
            case WHITE:
                s = StringsFr.WHITE_CARD;
                break;
            case LOCOMOTIVE:
                s = StringsFr.LOCOMOTIVE_CARD;
                break;
            case PLANE:
                s = StringsFr.PLANE_CARD;
                break;
        }
        return s + StringsFr.plural(count);
    }

    /**
     * @param playerNames List<String>
     * @param points      int
     * @return syntax if there is a draw
     */
    public static String draw(List<String> playerNames, int points) {
        return String.format(StringsFr.DRAW, String.join(StringsFr.AND_SEPARATOR, playerNames), points);
    }

    /**
     * @return the message stating that the player will play first
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * @param count int
     * @return the message declaring that the player has kept the given number of tickets
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * @return the message declaring that the player can play
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * @param count int
     * @return a message stating that the player has drawn the given number of tickets
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * @return a message stating that the player has drawn a "blind" card , i.e., from the top of the deck
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * @param card Card
     * @return the message declaring that the player has drawn the given face-up card
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * @param route Route
     * @param cards SortedBag<Card>
     * @return a message stating that the player has taken the given route using the given cards
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, Info.routeName(route), StringsFr.sortedBagName(cards));
    }

    /**
     * @param route        Route
     * @param initialCards SortedBag<Card>
     * @return the message stating that the player wishes to take the given tunnel road using the given cards initially
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, Info.routeName(route), StringsFr.sortedBagName(initialCards));
    }


    /**
     * @param route        Route
     * @param initialCards SortedBag<Card>
     * @return the message stating that
     */
    public String attemptsSkyRouteClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_SKYROUTE_CLAIM, playerName, Info.routeName(route), StringsFr.sortedBagName(initialCards));
    }


    /**
     * @param route        Route
     * @param count        count
     * @return the message stating that
     */
    public String additionalSkyRouteCount(Route route, int count) {
        return String.format(StringsFr.ADDITIONNAL_SKYROUTE_COUNT, playerName, Info.routeName(route), count,cardName(Card.LOCOMOTIVE,count));
    }

    /**
     * @param route Route
     * @return the message stating that
     */
    public String noadditionalSkyRoute(Route route) {
        return String.format(StringsFr.NO_ADDITIONNAL_SKYROUTE, playerName, Info.routeName(route));
    }


    /**
     * @param drawnCard      SortedBag<Card>
     * @param additionalCost int
     * @return a message stating that the player has drawn the three additional cards given,
     * and that they involve an additional cost for the number of cards given
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCard, int additionalCost) {
        return String.format(StringsFr.ADDITIONAL_CARDS_ARE, StringsFr.sortedBagName(drawnCard)) + (additionalCost == 0 ? StringsFr.NO_ADDITIONAL_COST : String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost)));
    }

    /**
     * @param route Route
     * @return a message stating that the player was unable (or unwilling) to seize the given tunnel
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, Info.routeName(route));
    }

    /**
     * @param carCount int
     * @return the message declaring that the player has only the given number (and less than or equal to 2) of
     * wagons left, and that the last round therefore begins
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     * @param longestTrail Trail
     * @return a message stating that the player gets the end-game bonus for the given path,
     * which is the longest, or one of the longest paths
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, playerName, Info.trailName(longestTrail));
    }

    /**
     * @param points      int
     * @param loserPoints int
     * @return the message declaring that the player wins the game with the given number of points,
     * his opponent having obtained only loserPoints.
     */
    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, playerName, points, StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }

}
