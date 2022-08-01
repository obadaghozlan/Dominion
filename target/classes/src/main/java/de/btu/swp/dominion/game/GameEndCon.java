package de.btu.swp.dominion.game;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import de.btu.swp.dominion.gameLogic.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameEndCon extends GameCon implements Initializable {

    private PlayerService playerService = getPlayerService2();

    @FXML
    private HBox pointsHBox;

    @FXML
    private VBox firstPlace;

    @FXML
    private VBox secondPlace;

    @FXML
    private VBox thirdPlace;

    @FXML
    private VBox fourthPlace;

    @FXML
    private Label playerOne;

    @FXML
    private Label playerTwo;

    @FXML
    private Label playerThree;

    @FXML
    private Label playerFour;

    @FXML
    private Label pointsPlayerOne;

    @FXML
    private Label pointsPlayerTwo;

    @FXML
    private Label pointsPlayerThree;

    @FXML
    private Label pointsPlayerFour;

    @FXML
    private BorderPane mainBorder;

    ArrayList<String> names = new ArrayList<>(playerService.getPlayer().getPointslist().size());
    ArrayList<String> points = new ArrayList<>(playerService.getPlayer().getPointslist().size());
    TreeMap<String, Integer> mappings = new TreeMap<>(playerService.getPlayer().getPointslist());
    Timer timer = new Timer();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GuiDesigner.scaling(mainBorder);

        Map<String, Integer> map = sortByValue(mappings);
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            names.add(e.getKey().toString());
            points.add(e.getValue().toString());
            System.out.println(e.getKey() + " " + e.getValue());
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (true) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            if (names.size() == 4) {
                                playerOne.setText(names.get(3));
                                pointsPlayerOne.setText("Punkte: " + points.get(3));
                                playerTwo.setText(names.get(2));
                                pointsPlayerTwo.setText("Punkte: " + points.get(2));
                                playerThree.setText(names.get(1));
                                pointsPlayerThree.setText("Punkte: " + points.get(1));
                                playerFour.setText(names.get(0));
                                pointsPlayerFour.setText("Punkte: " + points.get(0));
                                firstPlace.setVisible(true);
                                secondPlace.setVisible(true);
                                thirdPlace.setVisible(true);
                                fourthPlace.setVisible(true);
                            }
                            if (names.size() == 3) {
                                playerOne.setText(names.get(2));
                                pointsPlayerOne.setText("Punkte: " + points.get(2));
                                playerTwo.setText(names.get(1));
                                pointsPlayerTwo.setText("Punkte: " + points.get(1));
                                playerThree.setText(names.get(0));
                                pointsPlayerThree.setText("Punkte: " + points.get(0));
                                firstPlace.setVisible(true);
                                secondPlace.setVisible(true);
                                thirdPlace.setVisible(true);
                            }
                            if (names.size() == 2) {
                                playerOne.setText(names.get(1));
                                pointsPlayerOne.setText("Punkte: " + points.get(1));
                                playerTwo.setText(names.get(0));
                                pointsPlayerTwo.setText("Punkte: " + points.get(0));
                                firstPlace.setVisible(true);
                                secondPlace.setVisible(true);
                            }

                        }
                    });
                }
            }
        }, 0, 300);

    }

    @FXML
    void gameendBtnClicked(MouseEvent event) throws IOException {
        if (metaData.getSoundOnMeta()) {
            musicBox.getBackgroundAudio().play();
        }

        if (playerService.getPlayer().getHost()) {
            playerService.getServerServices().stopServer();
        }
        try {
            timer.cancel();
            playerService.getPlayer().setEnd(false);
            Parent root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/HauptMenu.fxml"));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (window.getScene() == null) {
                Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                window.setScene(sc);
            } else {
                window.getScene().setRoot(root);
            }
        } catch (IOException e) {
            System.err.println("err GameCon 02: Fehler bei Siegerbild");
        }
        if (playerService.getPlayer().getHost()) {
            playerService.getServerServices().stopServer();
        } else {
            // leave the Server
            playerService.getClient().getClient().close();
        }
    }

    @FXML
    void exitSpielHandler(MouseEvent event) {
        if (playerService.getPlayer().getHost()) {
            playerService.getServerServices().stopServer();
        } else {
            // leave the Server
            playerService.getClient().getClient().close();
        }
        System.exit(0);
    }

    @FXML
    private void goToMain(MouseEvent event) {
        /** close if he is not connected */
        if (!playerService.getClient().getClient().isConnected()) {
            try {
                Runtime.getRuntime().exec("cmd /c mvn exec:java");
            } catch (IOException e) {
                System.out.println("stating präsentation relaunch");
            }
            try {
                Runtime.getRuntime().exec("java -jar dominion-0.0.1-shaded.jar");
            } catch (IOException e) {
                System.out.println("faild");
            }
            System.exit(0);
            /*
            if (metaData.getSoundOnMeta()) {
                musicBox.getBackgroundAudio().play();
            }
            try {
                timer.cancel();
                playerService.getPlayer().setEnd(false);
                Parent root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/HauptMenu.fxml"));
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                if (window.getScene() == null) {
                    Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                    window.setScene(sc);
                } else {
                    window.getScene().setRoot(root);
                }
            } catch (IOException e) {
                System.err.println("err GameCon 02: Fehler bei Siegerbild");
            }*/
        }
    }

    public static <String, Integer extends Comparable<? super Integer>> Map<String, Integer> sortByValue(
            Map<String, Integer> map) {
        List<Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());

        Map<String, Integer> result = new LinkedHashMap<>();
        for (Entry<String, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}