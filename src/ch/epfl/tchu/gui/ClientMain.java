package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * Class that contains the main program of the tCHu client
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public class ClientMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Method who is responsible for starting the client
     *
     * @param primaryStage not used
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        //see if there is run arguments
        List<String> parametres = getParameters().getRaw();
        String hostName = parametres.size() >= 1 ? parametres.get(0) : "localhost";
        int port = parametres.size() >= 2 ? Integer.parseInt(parametres.get(1)) : 5108;

        RemotePlayerClient playerClient =
                new RemotePlayerClient(new GraphicalPlayerAdapter(),
                        hostName,
                        port);

        new Thread(playerClient::run).start();

    }
}
