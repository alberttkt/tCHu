package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

/**
 * <b>Class representing a Game of tCHu</b>
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 * <p>
 */
public final class Game {


    private Game() {
    }

    /**
     * The methode which constitutes the course of a game
     *
     * @param players     Map with the players linked to their Id's
     * @param playerNames Map with the player's Id linked to their name
     * @param tickets     a SortedBag of all the tickets used in the game
     * @param rng         random
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);

        //                              **********EARLY-GAME**********

        //initiate players by giving their PlayerId and the name of the players
        players.forEach((id, Player) -> Player.initPlayers(id, playerNames));

        //initiating a map that maps the PlayerIds to a Info class with their name
        Map<PlayerId, Info> mapInfo = Map.of(PlayerId.PLAYER_1, new Info(playerNames.get(PlayerId.PLAYER_1)), PlayerId.PLAYER_2, new Info(playerNames.get(PlayerId.PLAYER_2)));

        //initiating the game
        GameState gameState = GameState.initial(tickets, rng);
        updateState(players, gameState);
        info(gameState.currentPlayerId() == PlayerId.PLAYER_1 ? mapInfo.get(PlayerId.PLAYER_1).willPlayFirst() : mapInfo.get(PlayerId.PLAYER_2).willPlayFirst(), players);


        //giving initial tickets
        for (Map.Entry<PlayerId, Player> m : players.entrySet()) {
            m.getValue().setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
            updateState(players, gameState);
        }

        for (Map.Entry<PlayerId, Player> m : players.entrySet()) {
            gameState = gameState.withInitiallyChosenTickets(m.getKey(), m.getValue().chooseInitialTickets());
        }



        for (Map.Entry<PlayerId, Player> m : players.entrySet()) {
            PlayerState ps = gameState.playerState(m.getKey());
            int size = ps.tickets().size();
            String s = mapInfo.get(m.getKey()).keptTickets(size);
            info(s, players);
        }

        updateState(players, gameState);

        //                              **********MID-GAME**********

        boolean end = false;

        while (!end) {
            PlayerId currentPlayerId = gameState.currentPlayerId();
            Player currentPlayer = players.get(currentPlayerId);
            Info currentPlayerInfo = mapInfo.get(currentPlayerId);
            info(currentPlayerInfo.canPlay(), players);
            updateState(players, gameState);

            switch (currentPlayer.nextTurn()) {

                case DRAW_TICKETS:

                    //calling the method that ask the player to choose his tickets
                    SortedBag<Ticket> chosen_tickets = currentPlayer.
                            chooseTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));

                    // Player has drawn given number of tickets
                    info(currentPlayerInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT), players);

                    //Player has kept given number of tickets
                    info(currentPlayerInfo.keptTickets(chosen_tickets.size()), players);
                    gameState = gameState.withChosenAdditionalTickets(
                            gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT), chosen_tickets);

                    break;

                case DRAW_CARDS:

                    for (int j = 0; j < 2; j++) {
                        //Checking if we need to transform the discard deck to the deck
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

                        if (j == 1) updateState(players, gameState);

                        //asking if the player wants to draw from the deck or the face up cards
                        int i = currentPlayer.drawSlot();
                        if (i == Constants.DECK_SLOT) {
                            gameState = gameState.withBlindlyDrawnCard();
                            info(currentPlayerInfo.drewBlindCard(), players);
                        } else {
                            Card c = gameState.cardState().faceUpCard(i);
                            info(currentPlayerInfo.drewVisibleCard(c), players);
                            gameState = gameState.withDrawnFaceUpCard(i);
                        }
                    }

                    break;

                case CLAIM_ROUTE:

                    Route route = currentPlayer.claimedRoute(); //Route he wants
                    SortedBag<Card> initialCards = currentPlayer.initialClaimCards(); //Cards he wants to use to get the route

                    //if the route is a tunnel
                    if (route.level() == Route.Level.UNDERGROUND ) {

                            info(currentPlayerInfo.attemptsTunnelClaim(route, initialCards), players);

                        // draw cards one at a time, checking that the deck is not empty
                        SortedBag.Builder builder = new SortedBag.Builder();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            if (gameState.cardState().isDeckEmpty())
                                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

                            builder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        SortedBag<Card> drawnCards = builder.build();
                        gameState = gameState.withMoreDiscardedCards(drawnCards); //adding drawn cards to discard


                        int numberOfAdditionalCards = route.additionalClaimCardsCount(initialCards, drawnCards);
                        info(currentPlayerInfo.drewAdditionalCards(drawnCards, numberOfAdditionalCards), players);

                        //if no additional cards are needed
                        if (numberOfAdditionalCards == 0) {
                            gameState = gameState.withClaimedRoute(route, SortedBag.of(initialCards));
                            info(currentPlayerInfo.claimedRoute(route, initialCards), players);
                        }
                        //if additional cards are needed
                        else if (numberOfAdditionalCards > 0) {
                            List<SortedBag<Card>> possibleCards = gameState.currentPlayerState().
                                    possibleAdditionalCards(numberOfAdditionalCards, initialCards, drawnCards);

                            //if the player has at least one possible way to put cards
                            if (!possibleCards.isEmpty()) {
                                SortedBag<Card> additional = currentPlayer.
                                        chooseAdditionalCards(possibleCards);


                                //if the player has decided to use one possible set of cards
                                if (!additional.isEmpty()) {
                                    gameState = gameState.withClaimedRoute(route, initialCards.union(additional));
                                    info(currentPlayerInfo.claimedRoute(route, initialCards.union(additional)), players);
                                } else {
                                    info(currentPlayerInfo.didNotClaimRoute(route), players);
                                }
                            }
                            //if the player doesn't have the right cards
                            else {
                                info(currentPlayerInfo.didNotClaimRoute(route), players);
                            }
                        }
                    }





                        else if(route.level()== Route.Level.SKY) {
                        info(currentPlayerInfo.attemptsSkyRouteClaim(route, initialCards), players);
                        int additionalCardsCount = route.additionalClaimCardsCount(initialCards, SortedBag.of());
                        if (additionalCardsCount == 0) {
                            info(currentPlayerInfo.noadditionalSkyRoute(route), players);
                            gameState = gameState.withClaimedRoute(route, initialCards);
                        } else {
                                info(currentPlayerInfo.additionalSkyRouteCount(route, additionalCardsCount), players);
                            List<SortedBag<Card>> possibleCards = gameState.currentPlayerState().
                                    possibleAdditionalCards(additionalCardsCount, initialCards, SortedBag.of());

                            if (!possibleCards.isEmpty()) {

                                SortedBag<Card> additional = currentPlayer.
                                         chooseAdditionalCards(possibleCards);
                                if (!additional.isEmpty()){

                                    gameState = gameState.withClaimedRoute(route, initialCards.union(additional));
                                    info(currentPlayerInfo.claimedRoute(route, initialCards.union(additional)), players);

                                }else{

                                    info(currentPlayerInfo.didNotClaimRoute(route), players);
                                }


                            }else{
                                info(currentPlayerInfo.didNotClaimRoute(route), players);
                            }


                        }
                    }//If the route is an overground route
                    else {
                        gameState = gameState.withClaimedRoute(route, SortedBag.of(initialCards));
                        info(currentPlayerInfo.claimedRoute(route, initialCards), players);
                    }

                    break;
            }

            //notify when the last turn begins
            if (gameState.lastTurnBegins()) {
                info(currentPlayerInfo.
                        lastTurnBegins(gameState.playerState(currentPlayerId).carCount()), players);
            }

            // ending the game
            if (gameState.lastPlayer() != null) {
                if (gameState.lastPlayer().equals(currentPlayerId)) end = true;
            }

            gameState = gameState.forNextTurn();
        }

        //                              **********END-GAME**********

        PlayerState playerState1 = gameState.playerState(PlayerId.PLAYER_1);
        PlayerState playerState2 = gameState.playerState(PlayerId.PLAYER_2);

        //Compute the longest trail of each player and compare them
        Trail p1longest = Trail.longest(playerState1.routes());
        Trail p2longest = Trail.longest(playerState2.routes());

        ArrayList<Integer> points = new ArrayList<>();
        points.add(playerState1.finalPoints());
        points.add(playerState2.finalPoints());

        if (p1longest.length() == p2longest.length()) {
            mapInfo.forEach((id, info) -> info(info.getsLongestTrailBonus(p1longest), players));
            points.forEach(point -> point += Constants.LONGEST_TRAIL_BONUS_POINTS);
        } else {
            PlayerId playerWithLongestRoad = (p1longest.length() > p2longest.length()) ? PlayerId.PLAYER_1 : PlayerId.PLAYER_2;
            Trail longest = (p1longest.length() > p2longest.length()) ? p1longest : p2longest;
            info(mapInfo.get(playerWithLongestRoad).getsLongestTrailBonus(longest), players);

            int index = p1longest.length() > p2longest.length() ? 0 : 1;
            points.set(index, points.get(index) + Constants.LONGEST_TRAIL_BONUS_POINTS);
        }

        updateState(players, gameState);

        //announce the winner(s)
        int point0 = points.get(0);
        int point1 = points.get(1);
        if (point0 == point1) {
            info(Info.draw(List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2)), point0), players);
        } else if (point0 > point1) {
            info(mapInfo.get(PlayerId.PLAYER_1).won(point0, point1), players);
        } else {
            info(mapInfo.get(PlayerId.PLAYER_2).won(point1, point0), players);
        }
    }

    // method that sends information to both players
    private static void info(String s, Map<PlayerId, Player> players) {
        players.forEach((k, v) -> v.receiveInfo(s));
    }

    //method for informing all players of a change of state
    private static void updateState(Map<PlayerId, Player> players, GameState gameState) {
        for (Map.Entry<PlayerId, Player> m : players.entrySet()) {
            m.getValue().updateState(gameState, gameState.playerState(m.getKey()));
        }
    }
}