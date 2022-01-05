package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Class that represents a remote player client
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public class RemotePlayerClient {
    private final Player player;
    private final String host;
    private final int port;

    /**
     * <b>Constructor of a RemotePlayerClient</b>
     *
     * @param player Player who will use the client
     * @param host   Id Address of the server
     * @param port   Common port between the player and the server to connect them
     */
    public RemotePlayerClient(Player player, String host, int port) {
        this.player = player;
        this.host = host;
        this.port = port;
    }

    /**
     * <b>Method allowing the client to communicate and interact with the Server according to the Game's progress</b>
     */
    public void run() {
        //trying to connect to the server at corresponding port
        try (Socket s = new Socket(host, port);
             BufferedReader r =
                     new BufferedReader(
                             new InputStreamReader(s.getInputStream(),
                                     StandardCharsets.US_ASCII));
             BufferedWriter w =
                     new BufferedWriter(
                             new OutputStreamWriter(s.getOutputStream(),
                                     StandardCharsets.US_ASCII))) {
            String message;
            while (((message = r.readLine()) != null)) {
                //split given message to get each argument separately
                String[] splitedMessage = message.split(Pattern.quote(" "), -1);
                MessageId type = MessageId.valueOf(splitedMessage[0]);

                //reacts differently according to the sent MessageId
                switch (type) {
                    case INIT_PLAYERS:
                        //playerId
                        PlayerId playerId = Serdes.PLAYER_ID_SERDE.deserialize(splitedMessage[1]);

                        //Map<PlayerId, String> playerNames
                        List<String> players = Serdes.LIST_STRING_SERDE.deserialize(splitedMessage[2]);
                        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, players.get(0), PlayerId.PLAYER_2, players.get(1));

                        player.initPlayers(playerId, playerNames);
                        break;

                    case RECEIVE_INFO:
                        //receiving the message that the player needs to get updated of the progress of the game
                        String info = Serdes.STRING_SERDE.deserialize(splitedMessage[1]);
                        player.receiveInfo(info);
                        break;

                    case UPDATE_STATE:
                        //receiving the new state to get updated to
                        PublicGameState newState = Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(splitedMessage[1]);
                        PlayerState ownState = Serdes.PLAYER_STATE_SERDE.deserialize(splitedMessage[2]);
                        player.updateState(newState, ownState);
                        break;

                    case SET_INITIAL_TICKETS:
                        //receiving proposed tickets
                        SortedBag<Ticket> tickets = Serdes.BAG_TICKET_SERDE.deserialize(splitedMessage[1]);

                        player.setInitialTicketChoice(tickets);
                        break;

                    case CHOOSE_INITIAL_TICKETS:
                        SortedBag<Ticket> t = player.chooseInitialTickets();

                        //sending the tickets the player has chosen
                        w.write(Serdes.BAG_TICKET_SERDE.serialize(t));
                        w.write('\n');
                        w.flush();
                        break;

                    case NEXT_TURN:
                        Player.TurnKind turnKind = player.nextTurn();

                        //sending what the player wants to plays
                        w.write(Serdes.TURN_KIND_SERDE.serialize(turnKind));
                        w.write('\n');
                        w.flush();
                        break;

                    case CHOOSE_TICKETS:
                        //receiving the options of tickets
                        SortedBag<Ticket> options = Serdes.BAG_TICKET_SERDE.deserialize(splitedMessage[1]);
                        SortedBag<Ticket> chosenTickets = player.chooseTickets(options);

                        //sending the tickets the player has chosen
                        w.write(Serdes.BAG_TICKET_SERDE.serialize(chosenTickets));
                        w.write('\n');
                        w.flush();
                        break;

                    case DRAW_SLOT:
                        int slot = player.drawSlot();

                        //sending the integer that indicates the slot of the face up cards the player has chosen (-1 if deck is chosen)
                        w.write(Serdes.INTEGER_SERDE.serialize(slot));
                        w.write('\n');
                        w.flush();
                        break;
                    case ROUTE:
                        Route rt = player.claimedRoute();

                        //sending the route the player has chosen
                        w.write(Serdes.ROUTE_SERDE.serialize(rt));
                        w.write('\n');
                        w.flush();
                        break;

                    case CARDS:
                        SortedBag<Card> cards = player.initialClaimCards();

                        //sending the cards the player has chosen
                        w.write(Serdes.BAG_CARD_SERDE.serialize(cards));
                        w.write('\n');
                        w.flush();
                        break;

                    case CHOOSE_ADDITIONAL_CARDS:
                        //receiving card options
                        List<SortedBag<Card>> options1 = Serdes.LIST_BAG_CARD_SERDE.deserialize(splitedMessage[1]);
                        SortedBag<Card> chosenCards = player.chooseAdditionalCards(options1);

                        //sending the cards the player has chosen
                        w.write(Serdes.BAG_CARD_SERDE.serialize(chosenCards));
                        w.write('\n');
                        w.flush();
                        break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
