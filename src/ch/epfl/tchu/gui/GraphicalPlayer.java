package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.*;

import static javafx.application.Platform.isFxApplicationThread;

/**
 * Class putting all the interfaces together and creates the complete interface
 *
 * @author Albert Troussard (330361)
 * @author Menelik Nouvellon (328132)
 */
public class GraphicalPlayer {

    private final Stage stage;
    private final Slider slide;

    private Map<Station, BooleanProperty> stationInticket = ticketMapBuilder();

    private Map<Station, BooleanProperty> ticketMapBuilder() {
        Map<Station, BooleanProperty> map = new HashMap<>();
        ChMap.stations().forEach(e -> map.put(e, new SimpleBooleanProperty(false)));

        return map;
    }

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final StackPane pane;
    private final ColorPicker cp;
    private final Label label;
    private final Button reset;
    private final ToggleButton draw;
    private final GridPane grid;
    private final ToggleButton eraser;
    //private final VBox drawBox;


    public final static int MAX_MESSAGE_NUMBER = 5;

    private final ObservableGameState observableGameState;
    private final ObservableList<Text> strings;
    private final ObjectProperty<ActionHandlers.DrawCardHandler> drawCardHandlerObjectProperty;
    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHandlerObjectProperty;
    private final ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandlerObjectProperty;
    private final Stage mainStage;

    /**
     * Constructor of a graphical player
     *
     * @param identity    owner of the interface
     * @param playerNames map of the players and their name
     */
    public GraphicalPlayer(PlayerId identity, Map<PlayerId, String> playerNames) {
        assert isFxApplicationThread();
        observableGameState = new ObservableGameState(identity);
        this.strings = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.drawCardHandlerObjectProperty = new SimpleObjectProperty<>();
        this.drawTicketsHandlerObjectProperty = new SimpleObjectProperty<>();
        this.claimRouteHandlerObjectProperty = new SimpleObjectProperty<>();
        this.mainStage = new Stage();

        pane = new StackPane();
        stage = new Stage();
        slide = new Slider();
        canvas = new Canvas(1110, 735);
        gc = canvas.getGraphicsContext2D();
        cp = new ColorPicker();
        label = new Label("5");
        grid = new GridPane();
        reset = new Button("Reset");
        draw = new ToggleButton();
        eraser = new ToggleButton();


        stage.setTitle("tCHu \u2014" + playerNames.get(identity));

        MapViewCreator.CardChooser cardChooser = this::chooseClaimCards;

        Pane mapView = MapViewCreator
                .createMapView(observableGameState, claimRouteHandlerObjectProperty, cardChooser);

        Node cardsView = DecksViewCreator.
                createCardsView(observableGameState, drawTicketsHandlerObjectProperty, drawCardHandlerObjectProperty);
        HBox handView = DecksViewCreator
                .createHandView(observableGameState);

        VBox infoView = InfoViewCreator.createInfoView(identity, playerNames, observableGameState, strings);

        pane.getStylesheets().add("graphicalPlayer.css");
        ObservableList<Station> stations = new SimpleListProperty<>(FXCollections.observableArrayList());

        selectorTicketCreator(mapView, stations);


        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, new Pane(infoView, grid));

        pane.getChildren().addAll(mainPane, canvas);


        Scene scene1 = new Scene(pane);


        DrawCreator();

        stage.setScene(scene1);
        stage.getIcons().add(new Image("file:resources/imageTicket.png"));
        stage.show();


    }


    private void selectorTicketCreator(Pane mapView, ObservableList<Station> stations) {
        for (Station station : ChMap.stations()) {
            Circle c = new Circle(11);
            c.setStroke(Color.FLORALWHITE);
            c.setFill(Color.DEEPPINK);
            c.setId(Integer.toString(station.id()));

            DecksViewCreator.selectedTicketProperty().addListener((e, o, n) -> {
                Set<Station> newStations = new HashSet<>();
                for (Trip trip : n.getTrips()) {
                    newStations.addAll(List.of(trip.from(), trip.to()));
                }

                stations.setAll(newStations);
                if (stations.contains(station)) {
                    stationInticket.get(station).set(true);
                } else {
                    stationInticket.get(station).set(false);
                }

            });

            c.visibleProperty().bind(stationInticket.get(station));

            mapView.getChildren().add(c);

        }
    }


    private void DrawCreator() {

        draw.getStyleClass().add("toggle-button-Draw");
        eraser.getStyleClass().add("toggle-button-Gomme");

        canvas.disableProperty().bind(draw.selectedProperty().not().and(eraser.selectedProperty().not()));


        reset.setOnAction(e -> resetDraw());

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);


        slide.setMin(1);
        slide.setMax(20);
        slide.setValue(5);
        slide.setShowTickLabels(true);
        slide.setShowTickMarks(true);
        slide.setMaxWidth(80);
        slide.valueProperty().addListener(e -> {
            gc.setLineWidth(slide.getValue());
            label.setText(String.format("%.0f", slide.getValue()));
        });


        cp.setValue(Color.BLACK);
        cp.setMaxWidth(50);


        cp.setOnAction(e -> gc.setStroke(cp.getValue()));


        canvas.setOnMousePressed(e -> {
            gc.beginPath();
            if (eraser.isSelected()) {
                gc.clearRect(e.getX(), e.getY(), gc.getLineWidth() * 2, gc.getLineWidth() * 2);
            } else if (draw.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }
        });
        canvas.setOnMouseDragged(e -> {


            if (eraser.isSelected()) {
                gc.clearRect(e.getX(), e.getY(), gc.getLineWidth() * 2, gc.getLineWidth() * 2);
            } else if (draw.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }
        });
        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(draw, eraser);


        grid.addRow(0, cp, slide, label);
        grid.addRow(1, reset, draw, eraser);

        grid.setAlignment(Pos.TOP_CENTER);


        // pane.getChildren().add(grid);


        grid.setTranslateY(611);
        grid.setTranslateX(20);

        canvas.setTranslateY(-50);
        canvas.setTranslateX(70);
    }

    private void resetDraw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.beginPath();
        slide.setValue(5);

    }

    /**
     * Method that modify the ObservableGameState (and the interface linked with)
     *
     * @param publicGameState PublicGameState of the game
     * @param playerState     PlayerState of the owner
     */
    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        observableGameState.setState(publicGameState, playerState);
    }

    /**
     * Add the message to the interface
     *
     * @param message the message the game want to communicate with the player
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        if (strings.size() >= MAX_MESSAGE_NUMBER) {
            strings.remove(strings.get(0));
        }
        strings.add(new Text(message));
    }

    /**
     * call one handler depending on which action the player want to do and disable the others
     *
     * @param drawTicketsHandler handler used when the player wants to draw tickets
     * @param drawCardHandler    handler used when the player wants to draw cards
     * @param claimRouteHandler  handler used when the player wants to claim a route
     */
    public void startTurn(ActionHandlers.DrawTicketsHandler drawTicketsHandler, ActionHandlers.DrawCardHandler drawCardHandler, ActionHandlers.ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();

        if (observableGameState.canDrawCards()) {
            drawCardHandlerObjectProperty.set(slot -> {
                drawCardHandler.onDrawCard(slot);
                drawTicketsHandlerObjectProperty.set(null);
                claimRouteHandlerObjectProperty.set(null);
                drawCardHandlerObjectProperty.set(null);
            });
        }
        if (observableGameState.canDrawTickets()) {
            drawTicketsHandlerObjectProperty.set(() -> {
                drawTicketsHandler.onDrawnTickets();
                drawCardHandlerObjectProperty.set(null);
                claimRouteHandlerObjectProperty.set(null);
                drawTicketsHandlerObjectProperty.set(null);
            });
        }
        claimRouteHandlerObjectProperty.set((route, cards) -> {
            claimRouteHandler.onClaimRoute(route, cards);
            drawCardHandlerObjectProperty.set(null);
            drawTicketsHandlerObjectProperty.set(null);
            claimRouteHandlerObjectProperty.set(null);
        });
    }

    /**
     * Create a pop up window to allow the player to choose the tickets he wants
     *
     * @param options              the different tickets that the player can take
     * @param chooseTicketsHandler handler used when the player wants to draw tickets
     */
    public void chooseTickets(SortedBag<Ticket> options, ActionHandlers.ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        ObservableList<Ticket> tickets = new SimpleListProperty<>(FXCollections.observableArrayList());
        tickets.setAll(options.toList());
        ListView<Ticket> listView = new ListView<>(tickets);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Button butt = new Button(StringsFr.CHOOSE);
        int minimumTicketsNumber = options.size() - Constants.DISCARDABLE_TICKETS_COUNT;
        Text t = new Text(String.format(StringsFr.CHOOSE_TICKETS, options.size() - Constants.DISCARDABLE_TICKETS_COUNT, StringsFr.plural(minimumTicketsNumber)));
        TextFlow flow = new TextFlow(t);

        butt.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).lessThan(minimumTicketsNumber));


        VBox vbox = new VBox();
        vbox.getChildren().addAll(List.of(flow, listView, butt));
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add("chooser.css");
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setScene(scene);
        stage.setTitle(StringsFr.TICKETS_CHOICE);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);


        stage.setOnCloseRequest(Event::consume);


        butt.setOnAction(e -> {
            stage.hide();
            chooseTicketsHandler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));
        });
       // Image image = new Image("file:resources/imageTicket.png");
        //stage.getIcons().add(image);
        stage.show();

    }

    /**
     * Method used when the player has to choose his second card
     *
     * @param drawCardHandler handler used when the player wants to draw cards
     */
    public void drawCard(ActionHandlers.DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCardHandlerObjectProperty.set(slot -> {
            drawCardHandler.onDrawCard(slot);
            drawTicketsHandlerObjectProperty.set(null);
            claimRouteHandlerObjectProperty.set(null);
            drawCardHandlerObjectProperty.set(null);
        });
    }

    /**
     * Create a pop up window to allow the player to choose the cards he wants to use to initially try to claim the route (if there different choice)
     *
     * @param options     the different SortedBag of cards that the player use to initially try to claim the route
     * @param chooseCardH handler used when the player has to choose the cards to initially try to claim the route
     */
    public void chooseClaimCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler chooseCardH) {
        assert isFxApplicationThread();
        Text t = new Text(StringsFr.CHOOSE_CARDS);

        ObservableList<SortedBag<Card>> cards = FXCollections.observableArrayList(options);
        ListView<SortedBag<Card>> listView = new ListView<>(cards);
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));
        Button butt = new Button(StringsFr.CHOOSE);
        TextFlow flow = new TextFlow(t);


        butt.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).lessThan(1));

        VBox vbox = new VBox();
        vbox.getChildren().addAll(flow, listView, butt);
        sceneCreator(chooseCardH, listView, butt, vbox);

    }

    /**
     * Create a pop up window to allow the player to choose the additional cards he wants to use (if he don't want to use more card's he just has to select nothing)
     *
     * @param options     the different SortedBag of cards that the player can use as additional cards
     * @param chooseCardH handler used when the player has to choose the cards to initially try to claim the route
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler chooseCardH) {
        assert isFxApplicationThread();
        Text t = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);
        ObservableList<SortedBag<Card>> cards = FXCollections.observableArrayList(options);
        ListView<SortedBag<Card>> listView = new ListView<>(cards);
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));
        Button butt = new Button(StringsFr.CHOOSE);
        TextFlow flow = new TextFlow(t);


        VBox vbox = new VBox();
        vbox.getChildren().addAll(flow, listView, butt);
        sceneCreator(chooseCardH, listView, butt, vbox);

    }

    private void sceneCreator(ActionHandlers.ChooseCardsHandler chooseCardH, ListView<SortedBag<Card>> listView, Button butt, VBox vbox) {
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add("chooser.css");
        Stage stage = new Stage(StageStyle.UTILITY);


        stage.setScene(scene);
        stage.setTitle(StringsFr.CARDS_CHOICE);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);

        stage.setOnCloseRequest(Event::consume);
        butt.setOnAction(e -> {

            if (listView.getSelectionModel().getSelectedItems().isEmpty()) {
                chooseCardH.onChooseCards(SortedBag.of());
            } else {
                chooseCardH.onChooseCards(listView.getSelectionModel().getSelectedItem());
            }
            stage.hide();
        });
        stage.show();
    }

}


