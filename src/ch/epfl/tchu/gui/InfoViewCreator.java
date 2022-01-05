package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class creating the info view for the player's interface
 *
 * @author Albert Troussard (330361)
 * @author Menelik Nouvellon (328132)
 */
class InfoViewCreator {
    /**
     * Create the InfoView of a player
     *
     * @param owner               id of the interface's owner
     * @param playerNames         map of the differents id and the name linked with
     * @param observableGameState the ObservableGameState of the owner
     * @param gameInfo            ObservableList of the
     * @return The InfoView (left part of the interface) that updates depending on <i>observableGameState</i>
     */
    public static VBox createInfoView(PlayerId owner, Map<PlayerId, String> playerNames, ObservableGameState observableGameState, ObservableList<Text> gameInfo) {
        TextFlow textFlow = new TextFlow();
        textFlow.setId("game-info");
        VBox playerStats = new VBox();
        playerStats.setId("player-stats");
        List<PlayerId> ids = new ArrayList<>();
        ids.add(owner);
        for (int i = 0; i < PlayerId.COUNT; i++) {
            PlayerId id = PlayerId.ALL.get(i);
            if (!id.equals(owner)) ids.add(id);
        }


        for (PlayerId playerId : ids) {
            TextFlow stat = new TextFlow();
            stat.getStyleClass().add(playerId.name());


            Circle circle = new Circle(5);
            circle.getStyleClass().add("filled");
            ReadOnlyIntegerProperty ticketCount = observableGameState.playerTicketCountProperty(playerId);
            ReadOnlyIntegerProperty cardCount = observableGameState.playerCardCountProperty(playerId);
            ReadOnlyIntegerProperty carCount = observableGameState.playerCarCountProperty(playerId);
            ReadOnlyIntegerProperty points = observableGameState.playerPointsProperty(playerId);
            Text text = new Text();
            text.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(playerId), ticketCount, cardCount, carCount, points));
            text.setFont(StringsFr.font(15, "Bold"));
            stat.getChildren().addAll(circle, text);
            playerStats.getChildren().add(stat);
        }

        Bindings.bindContent(textFlow.getChildren(), gameInfo);


        Separator separator = new Separator(Orientation.HORIZONTAL);

        VBox vBox = new VBox();
        vBox.getStylesheets().addAll("info.css", "colors.css");
        vBox.getChildren().addAll(playerStats, separator, textFlow);

        return vBox;

    }
}
