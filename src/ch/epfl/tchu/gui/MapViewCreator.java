package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Station;
import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.sound.sampled.Clip;
import java.util.ArrayList;
import java.util.List;

/**
 * Class creating the map view for the player's interface
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
class MapViewCreator {
    private static final int RADIUS = 3;
    private static final int CENTER_X = 12;
    private static final int CENTER_Y = 6;
    private static final int RECTANGLE_HEIGHT = 12;
    private static final int RECTANGLE_WIDTH = 36;

    /**
     * Create the map view of a player
     *
     * @param observableGameState       the ObservableGameState of a player
     * @param claimRouteHandlerProperty handler used when the player wants to claim a Route
     * @param cardChooser               way used to choose the cards to claim a route
     * @return The MapView (central part of the interface) that updates depending on <i>observableGameState</i>
     */
    public static Pane createMapView(ObservableGameState observableGameState, ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandlerProperty, CardChooser cardChooser) {

        BooleanProperty oneFlyRoadOwned = new SimpleBooleanProperty(false);

        ImageView view = new ImageView();
        view.getStyleClass().add("map");
        ImageView planeBrusio = new ImageView();
        planeBrusio.getStyleClass().addAll("plane", "Brusio");
        ImageView planeGeneve = new ImageView();
        planeGeneve.getStyleClass().addAll("plane", "Geneve");
        ImageView planeDelemont = new ImageView();
        planeDelemont.getStyleClass().addAll("plane", "Delemont");
        ImageView planeStGall = new ImageView();
        planeStGall.getStyleClass().addAll("plane", "StGall");


        Pane gamePane = new Pane();
        gamePane.getChildren().addAll(view, planeBrusio, planeGeneve, planeDelemont, planeStGall);
        gamePane.getStylesheets().addAll("map.css", "colors.css");

        Clip audioChoosingAirRoute = DecksViewCreator.createAudio("resources/airplane.wav");
        Clip cardChoosingSound = DecksViewCreator.createAudio("resources/tchutchu.wav");
        //routes
        for (Route route : ChMap.routes().subList(0, ChMap.TRAIN_ROUTE_LAST_INDEX)) {


            List<Node> list = new ArrayList<>();

            for (int i = 1; i <= route.length(); i++) {
                Rectangle r2 = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                r2.getStyleClass().add("filled");
                Circle c1 = new Circle(CENTER_X, CENTER_Y, RADIUS);
                Circle c2 = new Circle(CENTER_X * 2, CENTER_Y, RADIUS);

                Node wagonGroup = new Group(r2, c1, c2);
                wagonGroup.getStyleClass().add("car");

                Rectangle voie = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                voie.getStyleClass().addAll("track", "filled");

                Node caseNode = new Group(voie, wagonGroup);


                caseNode.setId(new StringBuilder().append(route.id()).append("_").append(i).toString());
                list.add(caseNode);

                observableGameState.routesProperty(route).addListener(e -> {

                    double nodeRotateInitial = caseNode.getRotate();
                    Timeline flash = new Timeline(
                            new KeyFrame(Duration.seconds(0.2), new KeyValue(caseNode.scaleXProperty(), 2, Interpolator.EASE_IN)),
                            new KeyFrame(Duration.seconds(0.2), new KeyValue(caseNode.scaleYProperty(), 2, Interpolator.EASE_IN)),
                            new KeyFrame(Duration.seconds(0.3), new KeyValue(caseNode.rotateProperty(), nodeRotateInitial + 180, Interpolator.EASE_IN)),
                            new KeyFrame(Duration.seconds(0.5), new KeyValue(caseNode.scaleYProperty(), 1, Interpolator.EASE_IN)),
                            new KeyFrame(Duration.seconds(0.5), new KeyValue(caseNode.scaleXProperty(), 1, Interpolator.EASE_IN)),
                            new KeyFrame(Duration.seconds(0.5), new KeyValue(caseNode.rotateProperty(), nodeRotateInitial + 360, Interpolator.EASE_IN))
                    );
                    flash.play();
                });


            }
            Node routeNode = new Group(list);
            routeNode.setId(route.id());

            observableGameState.routesProperty(route).addListener((owner, old, newValue) -> {
                String p = newValue.name();
                routeNode.getStyleClass().add(p);
                cardChoosingSound.setMicrosecondPosition(0);
                cardChoosingSound.start();
            });

            routeNode.getStyleClass().addAll("route", route.level().name(), route.color() == null ? "NEUTRAL" : route.color().name());
            routeNode.disableProperty().bind(claimRouteHandlerProperty.isNull().or(observableGameState.getClaimableRoute(route).not()));

            routeNode.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                    List<SortedBag<Card>> possibleClaimCards = observableGameState.getPlayerState().possibleClaimCards(route);
                    ActionHandlers.ClaimRouteHandler claimRouteH = claimRouteHandlerProperty.get();

                    if (possibleClaimCards.size() == 1) {
                        claimRouteH.onClaimRoute(route, possibleClaimCards.get(0));
                    } else {
                        ActionHandlers.ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                        cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                    }
                }
            });

            gamePane.getChildren().add(routeNode);
        }


        for (Route route : ChMap.routes().subList(ChMap.TRAIN_ROUTE_LAST_INDEX, ChMap.routes().size())) {


            for (Station station : route.stations()) {

                StackPane stackpaneClaimed = new StackPane();

                Rectangle r2 = new Rectangle(RECTANGLE_WIDTH * 2.5, RECTANGLE_HEIGHT + 3);
                r2.getStyleClass().add("filled");

                stackpaneClaimed.getChildren().addAll(r2);

                stackpaneClaimed.getStyleClass().add("car");

                StackPane stackpane = new StackPane();

                Rectangle voie = new Rectangle(RECTANGLE_WIDTH * 2.5, RECTANGLE_HEIGHT + 3);
                voie.getStyleClass().addAll("track", "filled");

                Text text2 = new Text("↪ " + route.stationOpposite(station));
                text2.setFont(Font.font("Helvetica"));
                text2.setFill(Color.WHITE);

                stackpane.getChildren().addAll(voie, text2);

                Node routeNode = new Group(stackpaneClaimed, stackpane);

                routeNode.getStyleClass().addAll("route", "PLANE");

                routeNode.disableProperty().bind(claimRouteHandlerProperty.isNull().or(observableGameState.getClaimableRoute(route).not()).or(oneFlyRoadOwned));

                routeNode.setId(new StringBuilder().append(station.id()).append("-").append(route.stationOpposite(station).id()).toString());

                observableGameState.routesProperty(route).addListener((owner, old, newValue) -> {
                    String p = newValue.name();
                    routeNode.getStyleClass().add(p);
                    text2.setFill(Color.BLACK);
                    oneFlyRoadOwned.set(true);


                    for (Station airport : ChMap.airports()) {
                        if (route.station1().id() == airport.id() || route.station2().id() == airport.id()) {
                            switch (airport.id()) {
                                case 27:
                                    planeStGall.getStyleClass().add(p);
                                    int otherStationId = route.station1().id() == airport.id() ? route.station2().id() : route.station1().id();
                                    switch (otherStationId) {
                                        case 10:
                                            turnAnimation(180, planeStGall.getRotate(), planeStGall);
                                            planeTransition(660, -100, -230, 340, planeStGall);
                                            break;
                                        case 8:
                                            turnAnimation(210, planeStGall.getRotate(), planeStGall);
                                            planeTransition(660, -100, 40, -100, planeStGall);
                                            break;
                                        case 5:
                                            turnAnimation(120, planeStGall.getRotate(), planeStGall);
                                            planeTransition(660, -100, 800, 310, planeStGall);
                                            break;
                                    }
                                    break;
                                case 10:
                                    otherStationId = route.station1().id() == airport.id() ? route.station2().id() : route.station1().id();
                                    planeGeneve.getStyleClass().add(p);
                                    switch (otherStationId) {
                                        case 27:
                                            turnAnimation(10, planeGeneve.getRotate(), planeGeneve);
                                            planeTransition(-230, 340, 660, -100, planeGeneve);
                                            break;
                                        case 8:
                                            turnAnimation(-10, planeGeneve.getRotate(), planeGeneve);
                                            planeTransition(-230, 340, 40, -100, planeGeneve);
                                            break;
                                        case 5:
                                            turnAnimation(30, planeGeneve.getRotate(), planeGeneve);
                                            planeTransition(-230, 340, 800, 310, planeGeneve);
                                    }
                                    break;
                                case 8:
                                    planeDelemont.getStyleClass().add(p);
                                    otherStationId = route.station1().id() == airport.id() ? route.station2().id() : route.station1().id();
                                    switch (otherStationId) {
                                        case 27:
                                            turnAnimation(30, planeDelemont.getRotate(), planeDelemont);
                                            planeTransition(40, -100, 660, -100, planeDelemont);
                                            break;
                                        case 10:
                                            turnAnimation(160, planeDelemont.getRotate(), planeDelemont);
                                            planeTransition(40, -100, -230, 340, planeDelemont);
                                            break;
                                        case 5:
                                            turnAnimation(90, planeDelemont.getRotate(), planeDelemont);
                                            planeTransition(40, -100, 800, 310, planeDelemont);
                                    }
                                    break;
                                case 5:
                                    planeBrusio.getStyleClass().add(p);
                                    otherStationId = route.station1().id() == airport.id() ? route.station2().id() : route.station1().id();
                                    switch (otherStationId) {
                                        case 27:
                                            turnAnimation(-30, planeBrusio.getRotate(), planeBrusio);
                                            planeTransition(800, 310, 660, -100, planeBrusio);
                                            break;
                                        case 10:
                                            turnAnimation(200, planeBrusio.getRotate(), planeBrusio);
                                            planeTransition(800, 310, -230, 340, planeBrusio);
                                            break;
                                        case 8:
                                            turnAnimation(270, planeBrusio.getRotate(), planeBrusio);
                                            planeTransition(800, 310, 40, -100, planeBrusio);
                                    }
                                    break;
                            }
                        }
                    }
                });
                oneFlyRoadOwned.addListener((owner, old, newValue) -> {
                    if (observableGameState.routesProperty(route).getValue() == null) {
                        audioChoosingAirRoute.start();
                        // audioChoosingAirRoute.setMicrosecondPosition(0);
                        text2.setText("VOL ANNULÉ");
                        text2.setFill(Color.DARKRED);
                    }
                });

                routeNode.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                        List<SortedBag<Card>> possibleClaimCards = observableGameState.getPlayerState().possibleClaimCards(route);
                        ActionHandlers.ClaimRouteHandler claimRouteH = claimRouteHandlerProperty.get();

                        if (possibleClaimCards.size() == 1) {
                            claimRouteH.onClaimRoute(route, possibleClaimCards.get(0));
                        } else {
                            ActionHandlers.ChooseCardsHandler chooseCardsH =
                                    chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                            cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                        }
                    }
                });

                gamePane.getChildren().add(routeNode);
            }
        }


        return gamePane;
    }

    private static void planeTransition(double xStart, double yStart, double xEnd, double yEnd, ImageView plane) {
        TranslateTransition transition = new TranslateTransition(new Duration(2500), plane);
        transition.setFromX(xStart);
        transition.setFromY(yStart);
        transition.setToX(xEnd);
        transition.setToY(yEnd);
        transition.play();
    }

    private static void turnAnimation(double angle, double initalAngle, ImageView plane) {
        Timeline flash = new Timeline(
                new KeyFrame(Duration.seconds(0.2), new KeyValue(plane.rotateProperty(), initalAngle + angle, Interpolator.LINEAR))
        );
        flash.play();
    }

    /**
     * Represent the way to choose the cards to claim a route
     */
    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandlers.ChooseCardsHandler handler);
    }
}
