package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Class that represents a remote player proxy
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public class RemotePlayerProxy implements Player {
    private Socket socket;

    /**
     * <b>Constructor of the RemotePlayerProxy</b>
     *
     * @param socket Socket used to communicate through the network with the client by exchanging text messages
     */
    public RemotePlayerProxy(Socket socket) {
        this.socket = socket;
    }

    /**
     * Method that serializes each argument of the initPlayers method and sends the message to the client
     *
     * @param ownId       player's identity
     * @param playerNames Map that maps a player to this name
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String aux1 = Serdes.PLAYER_ID_SERDE.serialize(ownId);
        String aux2 = Serdes.LIST_STRING_SERDE.serialize(List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2)));

        String s = String.join(" ", List.of(aux1, aux2));
        sendMessage(MessageId.INIT_PLAYERS, s);
    }

    /**
     * Method that serializes each argument of the receiveInfo method and sends the message to the client
     *
     * @param info the string that contains the information (from class Info)
     */
    @Override
    public void receiveInfo(String info) {
        sendMessage(MessageId.RECEIVE_INFO, Serdes.STRING_SERDE.serialize(info));
    }

    /**
     * Method that serializes each argument of the updateState method and sends the message to the client
     *
     * @param newState new game state that needs to be updated to
     * @param ownState state of the concerned player
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String aux1 = Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState);
        String aux2 = Serdes.PLAYER_STATE_SERDE.serialize(ownState);
        String s = String.join(" ", List.of(aux1, aux2));
        sendMessage(MessageId.UPDATE_STATE, s);

    }

    /**
     * Method that serializes each argument of the setInitialTicketChoice method and sends the message to the client
     *
     * @param tickets that have been distributed
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        sendMessage(MessageId.SET_INITIAL_TICKETS, Serdes.BAG_TICKET_SERDE.serialize(tickets));
    }

    /**
     * Method that asks the client to choose his initial tickets and receives the chosen tickets
     *
     * @return the chosen tickets by the client
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS, null);
        return Serdes.BAG_TICKET_SERDE.deserialize(readMessage());
    }

    /**
     * Method that asks the client to choose his next action and receives the chosen one
     *
     * @return the chosen action by the client
     */
    @Override
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN, null);
        return Serdes.TURN_KIND_SERDE.deserialize(readMessage());
    }

    /**
     * Method that asks the client to choose tickets among given options and receives the chosen tickets
     *
     * @param options the ticket's option given to the player
     * @return the chosen tickets
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(MessageId.CHOOSE_TICKETS, Serdes.BAG_TICKET_SERDE.serialize(options));
        return Serdes.BAG_TICKET_SERDE.deserialize(readMessage());
    }

    /**
     * Method that asks the client to choose the slot of the card he wants to pick and receives the chosen slot as integer
     *
     * @return the chosen slot (or -1 if deck is chosen)
     */
    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT, null);
        return Serdes.INTEGER_SERDE.deserialize(readMessage());
    }

    /**
     * Method that asks the client to choose his route and receives the chosen one
     *
     * @return the chosen Route
     */
    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE, null);
        return Serdes.ROUTE_SERDE.deserialize(readMessage());
    }

    /**
     * Method that asks the client to choose cards he wants to use to take the Route and receives the chosen cards
     *
     * @return the chosen cards
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS, null);
        return Serdes.BAG_CARD_SERDE.deserialize(readMessage());
    }

    /**
     * Method that asks the client to choose cards he wants to add and receives the chosen cards
     *
     * @param options the possibilities
     * @return the chosen cards
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS, Serdes.LIST_BAG_CARD_SERDE.serialize(options));
        return Serdes.BAG_CARD_SERDE.deserialize(readMessage());
    }

    /**
     * Method that sends the message to the client
     *
     * @param id      MessageId that allows the client to know which method he has to deal with
     * @param message String to send to the client
     */
    private void sendMessage(MessageId id, String message) {
        String str;
        //if the method has no argument, it just asks for information from client
        if (message == null) {
            str = id.name();
        }
        //send the serialized arguments of the method preceded by the method called
        else {
            str = String.join(" ", List.of(id.name(), message));
        }
        try {
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
            w.write(str);
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Method that reads message sent from the client
     *
     * @return message from client
     */
    private String readMessage() {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
