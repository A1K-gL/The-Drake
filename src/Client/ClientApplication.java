package Client;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import thedrake.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClientApplication extends Application {
    private Controller controller;
    private Set<Pair<Integer,Integer>> arrowPossibleSet = null;
    private ArrayList<ImageView> board = new ArrayList<>(16);

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        arrowPossibleSet = new HashSet<>();
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("Client.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
        stage.setTitle("The Drake");
        controller = fxmlLoader.getController();
        controller.setView(this);
    }
    public void startNewGame(VBox startMenu, GridPane gameBoard, VBox bluePlayerStack, VBox orangePlayerStack, Label currentPlayer){
        startMenu.setVisible(false);
        gameBoard.setVisible(true);
        bluePlayerStack.setVisible(true);
        orangePlayerStack.setVisible(true);
        currentPlayer.setVisible(true);
        currentPlayer.setOpacity(1);
    }
    public void drawGameBoard(Board gameBoard, GameState currentState){
        if(arrowPossibleSet != null) {
            for (Pair<Integer, Integer> node : arrowPossibleSet) {
                ImageView imageView = getImageView(node.getKey(), node.getValue());
                if (imageView.getOpacity() != 1) {
                    imageView.setOpacity(1);
                } else {
                    imageView.setImage(null);
                }
            }
        }
        PositionFactory pf = new PositionFactory(4);
        arrowPossibleSet = new HashSet<>();
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                ImageView imageView = getImageView(i, j);
                imageView.setImage(null);
                if(controller.getSelectedBox() != null)
                    imageView.setOpacity(1);
                if(gameBoard.at(pf.pos(i, j)).toString().equals("mountain")){
                    imageView.setImage(new Image(this.getClass().getResource("/img/mountain.png").toString()));
                }else if(currentState.armyNotOnTurn().boardTroops().at(pf.pos(i,j)).isPresent()){
                    imageView.setImage(new Image(this.getClass().getResource(getName(currentState.armyNotOnTurn().boardTroops().at(pf.pos(i,j)).get())).toString()));
                    imageView.setOpacity(1);
                } else if(currentState.armyOnTurn().boardTroops().at(pf.pos(i,j)).isPresent()){
                    imageView.setImage(new Image(this.getClass().getResource(getName(currentState.armyOnTurn().boardTroops().at(pf.pos(i,j)).get())).toString()));
                    imageView.setOpacity(1);
                }
            }
        }
        controller.getCurrentPlayer().setText("TURN: " + currentState.armyOnTurn().side());
    }
    public void drawStacks(Army blue, Army orange){
        controller.getBluePlayerStack().getChildren().removeAll(controller.getBluePlayerStack().getChildren());
        controller.getOrangePlayerStack().getChildren().removeAll(controller.getOrangePlayerStack().getChildren());
        controller.getBluePlayerStack().getChildren().addAll(generateStack(blue));
        controller.getOrangePlayerStack().getChildren().addAll(generateStack(orange));
        if(controller.getSelectedBox() != null && !controller.getSelectedBox().getChildren().isEmpty())
            controller.getSelectedBox().getChildren().getFirst().setOpacity(0.5);
    }
    public void loadPicture(ImageView imageView, String path){
        imageView.setImage(new Image(this.getClass().getResource(path).toString()));
    }
    private List<ImageView> generateStack(Army army){
        ArrayList<ImageView> result = new ArrayList<>();
        for(Troop troop : army.stack()){
            TroopTile tile = new TroopTile(troop, army.side(), TroopFace.AVERS);
            String name = getName(tile);
            result.add(new ImageView(new Image(ClientApplication.class.getResource(name).toString())));
            ImageView currentImage = result.getLast();
            currentImage.setFitHeight(50);
            currentImage.setFitWidth(50);
            currentImage.setOpacity(1);
        }
        return result;
    }
    private String getName(TroopTile troopTile) {
        return "/img/" + troopTile.side().name().toLowerCase() + troopTile.troop().name() + troopTile.face() + ".png";
    }
    public void drawPossibleMoves(ArrayList<BoardPos> possibleSteps, int i, int j){
        if(i >= 0) {
            ImageView imageView = getImageView(i, j);
            imageView.setOpacity(0.75);
        }
        for(BoardPos pos : possibleSteps){
            ImageView imageView = getImageView(pos.i(), pos.j());
            Image currentImage = imageView.getImage();
            if(currentImage != null)
                imageView.setOpacity(0.5);
            else{
                Image image = new Image(this.getClass().getResource("/img/redArrow.png").toString());
                imageView.setImage(image);

            }
            arrowPossibleSet.add(new Pair<>(pos.i(), pos.j()));
        }
    }
    public ImageView getImageView(int row, int column){
        ObservableList<Node> listOfImages= controller.getGameBoard().getChildren();
        ImageView result = null;
        for(Node node : listOfImages){
            Integer columnIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            if (columnIndex == null)
                columnIndex = -1;
            if (rowIndex == null)
                rowIndex = -1;
            if(row == rowIndex && column == columnIndex ) {
                result = (ImageView) node;
                break;
            }
        }
        return result;
    }
    public void drawEndGame(GameState gameState){
        controller.getCurrentPlayer().setVisible(false);
        controller.getAnchorPane().setOpacity(0.75);
        controller.getEndGameVBox().setVisible(true);
        controller.getEndGameVBox().setOpacity(1);
        controller.getGameBoard().setVisible(false);
        controller.getOrangePlayerStack().setVisible(false);
        controller.getBluePlayerStack().setVisible(false);
        String end_text = "";
        if(gameState.result().equals(GameResult.VICTORY)){
            end_text = "THE " + gameState.armyNotOnTurn().side().toString() + " PLAYER WON!";
        }else{
            end_text = gameState.result().name();
        }
        controller.getEndGameLabel().setText(end_text);

    }
    public void drawStartMenu(VBox startMenu, VBox endGameVBox, AnchorPane anchorPane){
        endGameVBox.setVisible(false);
        startMenu.setVisible(true);
        anchorPane.setOpacity(1);
    }
}
