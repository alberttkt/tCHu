package ch.epfl.tchu.net;

/**
 * <b>Enumeration of all kind of message sent to the Client</b>
 * <li>INIT_PLAYERS<li>RECEIVE_INFO<li>UPDATE_STATE<li>SET_INITIAL_TICKETS
 * <li>CHOOSE_INITIAL_TICKETS<li>NEXT_TURN<li>CHOOSE_TICKETS<li>DRAW_SLOT
 * <li>ROUTE<li>CARDS<li>CHOOSE_ADDITIONAL_CARDS
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public enum MessageId {
    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS
}
