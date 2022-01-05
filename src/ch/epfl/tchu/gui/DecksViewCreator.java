package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


/**
 * Class creating the decks view for the player's interface
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
class DecksViewCreator {
    private static final int RECTANGLE_HEIGHT = 70;
    private static final int RECTANGLE_WIDTH = 40;



    private static ObjectProperty<Ticket> selectedTicket=new SimpleObjectProperty<>();

    public static ReadOnlyObjectProperty<Ticket> selectedTicketProperty() {
        return selectedTicket;
    }

    /**
     * Create the hand view of a player
     *
     * @param observableGameState the ObservableGameState of the player
     * @return a hand view (the bottom part of the interface) that updates depending on <i>observableGameState</i>
     */
    public static HBox createHandView(ObservableGameState observableGameState) {

        ListView<Ticket> view = new ListView<>(observableGameState.getPlayerTickets());
        view.setId("tickets");


       view.addEventHandler(MouseEvent.ANY,e->{

           if(view.getSelectionModel().getSelectedItem()!=null){

        selectedTicket.setValue(view.getSelectionModel().getSelectedItem());
           }
       });



        view.getSelectionModel().getSelectedItems();

        HBox hBox = new HBox();
        hBox.getChildren().add(view);
        hBox.getStylesheets().addAll("decks.css", "colors.css");

        HBox hBox1 = new HBox();
        hBox1.setId("hand-pane");


        for (Card card : Card.ALL) {
            Text counter = new Text();
            counter.getStyleClass().add("count");
            ReadOnlyIntegerProperty count = observableGameState.playerCardsCountProperty(card);
            counter.textProperty().bind(Bindings.convert(count));
            counter.visibleProperty().bind(Bindings.greaterThan(count, 1));

            StackPane stackPane = card.equals(Card.PLANE) ? createRectangle("plane-image") : createRectangle("train-image");
            String color = card.color() == null ? "NEUTRAL" : card.color().name();
            stackPane.getStyleClass().addAll(color, "card");
            stackPane.visibleProperty().bind(Bindings.greaterThan(count, 0));

            stackPane.getChildren().add(counter);

            hBox1.getChildren().add(stackPane);
        }
        hBox.getChildren().add(hBox1);

        return hBox;
    }


    /**
     * Create the cards view of a player
     *
     * @param observableGameState the ObservableGameState of the player
     * @param ticketHandler       handler used when the player wants to draw tickets
     * @param cardHandler         handler used when the player wants to draw cards
     * @return a Cards view (right part of the interface) that updates depending on <i>observableGameState</i>
     */
    public static Node createCardsView(ObservableGameState observableGameState, ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketHandler, ObjectProperty<ActionHandlers.DrawCardHandler> cardHandler) {
        VBox vBox = new VBox();
        vBox.setId("card-pane");
        vBox.getStylesheets().addAll("decks.css", "colors.css","logo.css");

        currentPlayerCreator(vBox,observableGameState);



        //add the ticket's button
        Button buttonGraphicTicket = buttonGraphicCreatorTicket(observableGameState);
        buttonGraphicTicket.getStyleClass().addAll("gauged");
       /* ImageView logoTicket =new ImageView("file:resources/imageTicket.png");
        logoTicket.setFitWidth(50);
        logoTicket.setFitHeight(30);

        buttonGraphicTicket.setGraphic(logoTicket);*/

        buttonGraphicTicket.setText(StringsFr.TICKETS);



        buttonGraphicTicket.setFont(StringsFr.font(15,"Light"));
        buttonGraphicTicket.disableProperty().bind(ticketHandler.isNull());

        buttonGraphicTicket.setOnMouseClicked(event -> ticketHandler.get().onDrawnTickets());

        vBox.getChildren().add(buttonGraphicTicket);

        Clip cardChoosingSound = createAudio("resources/CardSound.wav");
        //add the face up cards
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            ReadOnlyObjectProperty<Card> card = observableGameState.cardStateFUC(i);

            Rectangle outside = new Rectangle(60, 90);
            outside.getStyleClass().add("outside");

            Rectangle inside = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
            inside.getStyleClass().addAll("filled", "inside");

            Rectangle trainImage = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
            trainImage.getStyleClass().add("train-image");


            StackPane stackPane =new StackPane(outside, inside, trainImage);




            card.addListener((owner, old, newValue) -> {
                if(newValue.equals(Card.PLANE)) {
                    trainImage.getStyleClass().set(0,"plane-image");
                }else{
                    trainImage.getStyleClass().set(0,"train-image");
                }

                String color = newValue.color() == null ? "NEUTRAL" : newValue.name();
                stackPane.getStyleClass().set(0, color);
            });
            stackPane.getStyleClass().addAll("null", "card","fuc");
            int j = i;

            stackPane.setOnMouseClicked(e -> {
                cardHandler.get().onDrawCard(j);
                //cardChoosingSound.start();
                //cardChoosingSound.setMicrosecondPosition(0);
            });


            vBox.getChildren().add(stackPane);
            stackPane.disableProperty().bind(cardHandler.isNull());
        }


        //add the cards deck's button
        Button buttonGraphicCard = buttonGraphicCreatorDeck(observableGameState);

        buttonGraphicCard.getStyleClass().add("gauged");
        buttonGraphicCard.setText(StringsFr.CARDS);

        buttonGraphicCard.setFont(StringsFr.font(15,"Light"));
        buttonGraphicCard.setOnMouseClicked(e -> {
            cardHandler.get().onDrawCard(Constants.DECK_SLOT);
            cardChoosingSound.setMicrosecondPosition(0);
            cardChoosingSound.start();
           // cardChoosingSound.setMicrosecondPosition(0);
        });

        buttonGraphicCard.disableProperty().bind(cardHandler.isNull());

        vBox.getChildren().add(buttonGraphicCard);



        return vBox;
    }


    private static void currentPlayerCreator(VBox vBox, ObservableGameState observableGameState) {
        Circle c= new Circle(10);
        Text t=new Text();
        t.setFont(StringsFr.font(12,"Light"));

        vBox.getChildren().addAll(c,t);

        observableGameState.gameCurrentPlayerIdProperty().addListener((e,o,n)->{
            if(n.equals(observableGameState.getOwner())){
                c.setFill(Color.GREEN);
                t.setText("C'est votre tour!");
                t.setFill(Color.GREEN);
            }else{
                c.setFill(Color.RED);
                t.setText("Patientez...");
                t.setFill(Color.RED);
            }
        });


    }



    /**
     * Methode qui créer un audioclip à partir d'un fichier audio
     * @param audioFile le nom du file
     * @return le Clip audio du file
     */
    public static Clip createAudio(String audioFile){
        File file = new File(audioFile);
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                return clip;
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static StackPane createRectangle(String image) {
        Rectangle outside = new Rectangle(60, 90);
        outside.getStyleClass().add("outside");

        Rectangle inside = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
        inside.getStyleClass().addAll("filled", "inside");

        Rectangle trainImage = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
        trainImage.getStyleClass().add(image);

        return new StackPane(outside, inside, trainImage);
    }

    private static Button buttonGraphicCreatorTicket(ObservableGameState observableGameState) {
        ReadOnlyIntegerProperty percentageProperty = observableGameState.gameTicketsPercentageProperty();
        return buttonGraphicCreatorBase(percentageProperty);
    }

    private static Button buttonGraphicCreatorDeck(ObservableGameState observableGameState) {
        ReadOnlyIntegerProperty percentageProperty = observableGameState.cardStateDeckPercentageProperty();
        return buttonGraphicCreatorBase(percentageProperty);
    }

    private static Button buttonGraphicCreatorBase(ReadOnlyIntegerProperty percentageProperty) {
        Rectangle background = new Rectangle(50, 5);
        background.getStyleClass().add("background");
        Rectangle foreground = new Rectangle(50, 5);
        foreground.getStyleClass().add("foreground");

        foreground.widthProperty().bind(percentageProperty.multiply(50).divide(100));

        Group buttonGraphic = new Group(background, foreground);
        Button b = new Button();
        b.setGraphic(buttonGraphic);
        return b;
    }

}
