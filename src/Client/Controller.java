package Client;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import thedrake.PlayingSide;

import java.net.URL;
import java.util.ResourceBundle;

import static thedrake.PlayingSide.BLUE;
import static thedrake.PlayingSide.ORANGE;

public class Controller implements Initializable {
    private Model model;
    private ClientApplication view;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ImageView startImage;
    @FXML
    private VBox startMenu;
    @FXML
    private GridPane gameBoard;
    @FXML
    private VBox bluePlayerStack;
    @FXML
    private VBox orangePlayerStack;
    @FXML
    private VBox endGameVBox;
    @FXML
    private Label endGameLabel;
    @FXML
    private Button startMenuBtn;
    @FXML
    private Label currentPlayer;
    private VBox selectedBox;
    @FXML
    public void exitEvent() {
        Platform.exit();
    }
    @FXML
    public void game() {
        view.startNewGame(startMenu, gameBoard, bluePlayerStack, orangePlayerStack, currentPlayer);
        model.startNewGame();
    }
    @FXML
    public void returnToMainMenu(){
        view.drawStartMenu(startMenu, endGameVBox, anchorPane);
    }
    public VBox getBluePlayerStack() {
        return bluePlayerStack;
    }
    public VBox getOrangePlayerStack() {
        return orangePlayerStack;
    }
    public GridPane getGameBoard() {
        return gameBoard;
    }
    public VBox getSelectedBox() {
        return selectedBox;
    }
    public AnchorPane getAnchorPane(){
        return anchorPane;
    }
    public Label getEndGameLabel(){
        return endGameLabel;
    }
    public Label getCurrentPlayer(){
        return currentPlayer;
    }
    public VBox getEndGameVBox(){
        return endGameVBox;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        model = new Model();
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                ImageView imageView = new ImageView();
                imageView.setFitHeight(75);
                imageView.setFitWidth(75);
                imageView.setCache(false);
                gameBoard.add(imageView, i, j);
                int finalJ = j;
                int finalI = i;
                imageView.setOnMouseClicked(event -> {

                        imageView.setOpacity(0.5);
                        model.update(finalJ, finalI, selectedBox != null);
                        selectedBox = null;
                        view.drawStacks(model.getGameState().army(PlayingSide.BLUE), model.getGameState().army(PlayingSide.ORANGE));
                });
            }

        }
    }
    public void setView(ClientApplication view) {
        view.loadPicture(startImage, "/img/shieldIcon.png");
        this.view = view;
        model.setView(view);
        bluePlayerStack.setOnMouseClicked(event -> {
                selectedBox = bluePlayerStack;
                model.update(model.getGameState().armyOnTurn() == model.getGameState().army(BLUE));
        });
        orangePlayerStack.setOnMouseClicked(event -> {
                selectedBox = orangePlayerStack;
                model.update(model.getGameState().armyOnTurn() == model.getGameState().army(ORANGE));
            });
        }

    }
