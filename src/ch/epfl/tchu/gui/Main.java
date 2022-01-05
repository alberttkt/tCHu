package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.DecksViewCreator;
import ch.epfl.tchu.net.RemotePlayerClient;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.sound.sampled.Clip;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 * @author Albert Troussard (330361)
 * @author Menelik Nouvellon (328132)
 */
public class Main extends Application {
    static Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("TchuTchu");
        Clip cffAudio = DecksViewCreator.createAudio("resources/suisse_romande.wav");


        GridPane gridPane = new GridPane();
        // ---- Joueur 1 / Joueur 2 apparence ----
        Text joueur1 = new Text();
        Text joueur2 = new Text();
        TextField name1input = new TextField();
        TextField name2input = new TextField();
        name1input.setMaxWidth(150);
        name2input.setMaxWidth(150);

        joueur1.setStyle("-fx-fill: lightblue;");
        name1input.setStyle("-fx-background-color: lightblue;-fx-text-fill: white;");
        joueur2.setStyle("-fx-fill: pink;");
        name2input.setStyle("-fx-background-color: pink;-fx-text-fill: white;");

        joueur1.setText("Joueur 1");
        joueur2.setText("Joueur 2");
        //  -------- // ------- // ------- //

        // ---- IpAdress / Port appearence ----
        Text ipAdressText = new Text();
        Text portText = new Text();
        TextField ipAdressInput = new TextField();
        TextField portInput = new TextField();

        Font font = Font.loadFont("file:resources/windows_command_prompt.ttf", 15);
        ipAdressText.setFont(font);
        portText.setFont(font);
        ipAdressText.setStyle("-fx-fill: white;");
        portText.setStyle("-fx-fill: white;");
        VBox ipAdress = new VBox();
        VBox port = new VBox();

        VBox joueur1Input = new VBox();
        VBox joueur2Input = new VBox();
        joueur1Input.getChildren().addAll(joueur1, name1input);
        joueur2Input.getChildren().addAll(joueur2, name2input);

        HBox clientInput =new HBox();
        clientInput.getChildren().addAll(joueur1Input,joueur2Input);
        //  -------- // ------- // ------- //

        //OwnIpAdress
        String ownIpAdress = ip();
        Text textBeforeOwnIpAdressText = new Text("Mon Adresse Ip : ");
        Text ownIpAdressText = new Text(ownIpAdress);
        ownIpAdressText.setStyle("-fx-fill: white; -fx-font-size: 0.8em;");
        ownIpAdressText.setTranslateX(220);
        ownIpAdressText.setTranslateY(-22);
        textBeforeOwnIpAdressText.setStyle("-fx-fill: white; -fx-font-size: 0.8em;");
        textBeforeOwnIpAdressText.setTranslateX(120);


        //Buttons
        ToggleButton server = new ToggleButton();
        server.setText("Serveur");
        server.setFont(StringsFr.font(15, "Light"));
        server.getStyleClass().add("server");

        ToggleButton client = new ToggleButton();
        client.setText("Client");
        client.setFont(StringsFr.font(15, "Light"));
        client.getStyleClass().add("client");

        Button jouer = new Button();
        jouer.getStyleClass().add("play");
        jouer.setText("Jouer !");
        jouer.setTranslateY(10);


        Rectangle copy = new Rectangle();
        Text t = new Text("Copy");
        t.setTranslateY(10);
        t.setTranslateX(4);
        t.setFont(Font.loadFont("file:resources/cmmi10.ttf", 10));
        copy.setFill(Color.WHITE);
        copy.setHeight(15f);
        copy.setWidth(30f);
        Group copyButton = new Group(copy, t);
        copyButton.setTranslateX(265);
        copyButton.setTranslateY(-70);

        VBox serverBox = new VBox();
        serverBox.getChildren().addAll(server, joueur1Input, joueur2Input);
        server.setTranslateX(40);

        ipAdressInput.setMaxWidth(155);
        portInput.setMaxWidth(155);
        ipAdressText.setText("C:\\Users\\Tchu> Adresse Ip :");
        portText.setText("C:\\Users\\Tchu> Port : ");
        ipAdressInput.setStyle("-fx-background-color: black;-fx-text-fill: white;");
        portInput.setStyle("-fx-background-color: black;-fx-text-fill: white;");
        ipAdressInput.setFont(font);
        portInput.setFont(font);


        ipAdress.getChildren().addAll(ipAdressText,ipAdressInput);
        port.getChildren().addAll(portText, portInput);


        // Logo, background image and arrangement
        ImageView logo = new ImageView();
        logo.getStyleClass().add("logoMenu");
        logo.setFitHeight(250);
        logo.setFitWidth(250);
        logo.setTranslateY(50);
        VBox clientBox = new VBox();
        VBox rectangleAutourIpAdressPort = new VBox();
        rectangleAutourIpAdressPort.getChildren().addAll(ipAdress,port);
        rectangleAutourIpAdressPort.setStyle("-fx-background-color: black; -fx-padding: 5;");
        rectangleAutourIpAdressPort.setTranslateY(5);
        clientBox.getChildren().addAll(client, rectangleAutourIpAdressPort);
        client.setTranslateX(40);

        gridPane.addRow(0, serverBox, clientBox);
        gridPane.setTranslateY(-20);
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.setHgap(50);
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(-70, 20, 20, 20));
        layout.getChildren().addAll(logo, gridPane, jouer, textBeforeOwnIpAdressText,ownIpAdressText,copyButton);

        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 600, 400);
        scene.getStylesheets().addAll("backGround.css", "togglebuttonStyle.css", "logo.css");

        //Invisible in the beginning
        joueur1Input.setVisible(false);
        joueur2Input.setVisible(false);
        ipAdress.setVisible(false);
        port.setVisible(false);
        rectangleAutourIpAdressPort.setVisible(false);
        copyButton.setVisible(false);

        //if we click on server's button
        server.setOnAction(s -> {
            joueur1Input.setVisible(server.isSelected());
            joueur2Input.setVisible(server.isSelected());
            client.setSelected(false);
            ipAdress.setVisible(false);
            port.setVisible(false);
            rectangleAutourIpAdressPort.setVisible(false);
        });

        //if we click on client's button
        client.setOnAction(s -> {
            ipAdress.setVisible(client.isSelected());
            port.setVisible(client.isSelected());
            server.setSelected(false);
            joueur1Input.setVisible(false);
            joueur2Input.setVisible(false);
            rectangleAutourIpAdressPort.setVisible(client.isSelected());
        });

        //if we click on Play's button
        jouer.setOnAction(s -> {
            if (server.isSelected()) {
                Platform.setImplicitExit(false);
                Platform.runLater(() -> System.out.println("Inside Platform.runLater()"));
                window.close();
                String j1 = name1input.getText();
                String j2 = name2input.getText();

                String player1name = j1.equals("") ? "Ada" : j1;
                String player2name = j2.equals("") ? "Charles" : j2;

                SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
                Random rng = new Random();
                ServerSocket serverSocket;
                try {
                    serverSocket = new ServerSocket(5108);
                    Socket socket = serverSocket.accept();
                    Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, player1name, PlayerId.PLAYER_2, player2name);

                    cffAudio.setMicrosecondPosition(0);
                    cffAudio.start();
                    Map<PlayerId, Player> players =
                            Map.of(PLAYER_1, new GraphicalPlayerAdapter(),
                                    PLAYER_2, new RemotePlayerProxy(socket));
                    new Thread(() -> Game.play(players, playerNames, tickets, rng))
                            .start();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (client.isSelected()) {
                Platform.setImplicitExit(false);
                Platform.runLater(() -> {}/*System.out.println("Inside Platform.runLater()")*/);
                window.close();
                String ipString = ipAdressInput.getText();
                String portString = portInput.getText();


                String hostName = ipString.equals("") ? "localhost" : ipString;
                int port1 = portString.equals("") ? 5108 : Integer.parseInt(portString);


                RemotePlayerClient playerClient =
                        new RemotePlayerClient(new GraphicalPlayerAdapter(),
                                hostName,
                                port1);

                cffAudio.setMicrosecondPosition(0);
                cffAudio.start();
                new Thread(playerClient::run).start();

            }
        });

        Clipboard cb = Clipboard.getSystemClipboard();
        ClipboardContent cbc = new ClipboardContent();
        ownIpAdressText.setOnMouseMoved(event -> {
            copyButton.setTranslateX(event.getX() + 156);
            copyButton.setTranslateY(event.getY() -60);
            copyButton.setVisible(true);
        });
        ownIpAdressText.setOnMouseExited(event -> {
            copyButton.setVisible(false);
        });
        ownIpAdressText.setOnMouseClicked(event -> {
            cbc.putString(ownIpAdressText.getText());
            cb.setContent(cbc);
            t.setText("Copied");
            copy.setWidth(40f);
        });

        window.setScene(scene);
        Image image = new Image("file:resources/icone.png");
        window.getIcons().add(image);
        window.show();
    }

    public static String ip() throws SocketException {
        return NetworkInterface.networkInterfaces()
                .filter(i -> {
                    try {
                        return i.isUp() && !i.isLoopback();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .flatMap(NetworkInterface::inetAddresses)
                .filter(a -> a instanceof Inet4Address)
                .map(InetAddress::getCanonicalHostName).collect(Collectors.joining());
    }
}
