package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 * Class that contains the main program of the tCHu server
 *
 * @author Albert Troussard (330361)
 * @author Menelik Nouvellon (328132)
 */
public class ServerMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> parameters = getParameters().getRaw();

        String player1name = parameters.size() >= 1 ? parameters.get(0) : "Ada";
        String player2name = parameters.size() >= 2 ? parameters.get(1) : "Charles";


        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        Random rng = new Random();
        ServerSocket serverSocket = new ServerSocket(5108);
        Socket socket = serverSocket.accept();
        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, player1name, PlayerId.PLAYER_2, player2name);

        Map<PlayerId, Player> players =
                Map.of(PLAYER_1, new GraphicalPlayerAdapter(),
                        PLAYER_2, new RemotePlayerProxy(socket));

        new Thread(() -> Game.play(players, playerNames, tickets, rng))
                .start();

    }
}
