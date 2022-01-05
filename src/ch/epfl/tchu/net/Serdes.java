package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Class Serdes containing all Serde used in the project
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public class Serdes {
    /**
     * Serde used for integer
     */
    public final static Serde<Integer> INTEGER_SERDE = Serde.of(String::valueOf, Integer::parseInt);

    /**
     * Serde used for String
     */
    public final static Serde<String> STRING_SERDE = Serde.of(
            s -> Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)),
            s -> new String(Base64.getDecoder().decode(s)));

    /**
     * Serde used for playerId
     */
    public final static Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    /**
     * Serde used for turnKind enumeration
     */
    public final static Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * Serde used for Card
     */
    public final static Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    /**
     * Serde used for Route
     */
    public final static Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    /**
     * Serde used for Ticket
     */
    public final static Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    /**
     * Serde used for a list of Strings
     */
    public final static Serde<List<String>> LIST_STRING_SERDE = Serde.listOf(STRING_SERDE, ',');

    /**
     * Serde used for a list of Cards
     */
    public final static Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE, ',');

    /**
     * Serde used for a list of Routes
     */
    public final static Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, ',');

    /**
     * Serde used for a SortedBag of Cards
     */
    public final static Serde<SortedBag<Card>> BAG_CARD_SERDE = Serde.bagOf(CARD_SERDE, ',');

    /**
     * Serde used for a SortedBag of Tickets
     */
    public final static Serde<SortedBag<Ticket>> BAG_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, ',');

    /**
     * Serde used for a list of SortedBags of Cards
     */
    public final static Serde<List<SortedBag<Card>>> LIST_BAG_CARD_SERDE = Serde.listOf(BAG_CARD_SERDE, ';');

    /**
     * Serde used for PublicCardState
     */
    public final static Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(
            //the function to serialize a PublicCardState
            (state) -> String.join(";", List.of(
                    LIST_CARD_SERDE.serialize(state.faceUpCards()),
                    INTEGER_SERDE.serialize(state.deckSize()),
                    INTEGER_SERDE.serialize(state.discardsSize()))),
            //the function to deserialize a PublicCardState
            (string) -> {
                String[] list = string.split(Pattern.quote(";"), -1);
                return new PublicCardState(
                        LIST_CARD_SERDE.deserialize(list[0]),
                        INTEGER_SERDE.deserialize(list[1]),
                        INTEGER_SERDE.deserialize(list[2]));
            }


    );

    /**
     * Serde used for PublicPlayerState
     */
    public final static Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = Serde.of(
            //the function to serialize a PublicPlayerState
            (state) -> String.join(";", List.of(
                    INTEGER_SERDE.serialize(state.ticketCount()),
                    INTEGER_SERDE.serialize(state.cardCount()),
                    LIST_ROUTE_SERDE.serialize(state.routes()))),
            //the function to deserialize a PublicPlayerState
            (string) -> {
                String[] list = string.split(Pattern.quote(";"), -1);
                return new PublicPlayerState(INTEGER_SERDE.deserialize(list[0]),
                        INTEGER_SERDE.deserialize(list[1]),
                        LIST_ROUTE_SERDE.deserialize(list[2]));
            }
    );

    /**
     * Serde used for PlayerState
     */
    public final static Serde<PlayerState> PLAYER_STATE_SERDE = Serde.of(
            //the function to serialize a PlayerState
            (state) -> String.join(";", List.of(
                    BAG_TICKET_SERDE.serialize(state.tickets()),
                    BAG_CARD_SERDE.serialize(state.cards()),
                    LIST_ROUTE_SERDE.serialize(state.routes()))),
            //the function to deserialize a PlayerState
            (string) -> {
                String[] list = string.split(Pattern.quote(";"), -1);
                return new PlayerState(BAG_TICKET_SERDE.deserialize(list[0]),
                        BAG_CARD_SERDE.deserialize(list[1]),
                        LIST_ROUTE_SERDE.deserialize(list[2]));
            }
    );

    /**
     * Serde used for PublicGameState
     */
    public final static Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = Serde.of(
            //the function to serialize a PublicGameState
            (state) -> String.join(":", List.of(
                    INTEGER_SERDE.serialize(state.ticketsCount()),
                    PUBLIC_CARD_STATE_SERDE.serialize(state.cardState()),
                    PLAYER_ID_SERDE.serialize(state.currentPlayerId()),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(state.playerState(PlayerId.PLAYER_1)),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(state.playerState(PlayerId.PLAYER_2)),
                    PLAYER_ID_SERDE.serialize(state.lastPlayer()))),
            //the function to deserialize a PublicGameState
            (string) -> {
                String[] list = string.split(Pattern.quote(":"), -1);
                int ticketCount = INTEGER_SERDE.deserialize(list[0]);
                PublicCardState cardState = PUBLIC_CARD_STATE_SERDE.deserialize(list[1]);
                PlayerId currentId = PLAYER_ID_SERDE.deserialize(list[2]);
                PublicPlayerState player1State = PUBLIC_PLAYER_STATE_SERDE.deserialize(list[3]);
                PublicPlayerState player2State = PUBLIC_PLAYER_STATE_SERDE.deserialize(list[4]);
                Map<PlayerId, PublicPlayerState> map = Map.of(PlayerId.PLAYER_1, player1State, PlayerId.PLAYER_2, player2State);
                PlayerId lastId = PLAYER_ID_SERDE.deserialize(list[5]);
                return new PublicGameState(ticketCount, cardState, currentId, map, lastId);
            }

    );
}
