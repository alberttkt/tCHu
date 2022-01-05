package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public interface ActionHandlers {
    /**
     * handler used when the player wants to draw tickets
     */
    public interface DrawTicketsHandler{
        void onDrawnTickets();

    }

    /**
     * handler used when the player wants to draw cards
     */
    public interface DrawCardHandler{
        void onDrawCard(int slot);
    }

    /**
     * handler used when the player wants to claim a Route
     */
    public interface ClaimRouteHandler{
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    /**
     * handler used when the player wants to choose tickets
     */
    public interface ChooseTicketsHandler{
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    /**
     * handler used when the player wants to choose the cards used to claim a route
     */
    public interface ChooseCardsHandler{
        void onChooseCards(SortedBag<Card> cards);
    }

}
