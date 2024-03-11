package Client;

import thedrake.*;

import java.util.ArrayList;
import java.util.Random;

import static thedrake.BoardTile.MOUNTAIN;


public class Model {
    private ClientApplication view;
    private GameState gameState;
    private Board gameBoard;
    private BoardPos selectedTile;
    public GameState getGameState() {
        return gameState;
    }
    public void setView(ClientApplication view) {
        this.view = view;
    }
    public void startNewGame(){
        generateNewBoard();
        gameState = new StandardDrakeSetup().startState(gameBoard);
        view.drawStacks(gameState.army(PlayingSide.BLUE), gameState.army(PlayingSide.ORANGE));
        view.drawGameBoard(gameBoard, gameState);
    }
    private void generateNewBoard(){
        gameBoard = new Board(4);
        PositionFactory pf = new PositionFactory(4);
        Random rand = new Random();
        gameBoard = gameBoard.withTiles(new Board.TileAt(pf.pos(1, rand.nextInt(1,3)), MOUNTAIN));
        gameBoard = gameBoard.withTiles(new Board.TileAt(pf.pos(2,rand.nextInt(1,3)), MOUNTAIN));
        //DRAW CASE
//        {
//            gameBoard = gameBoard.withTiles(new Board.TileAt(pf.pos(0, 2), MOUNTAIN));
//            gameBoard = gameBoard.withTiles(new Board.TileAt(pf.pos(1, 2), MOUNTAIN));
//            gameBoard = gameBoard.withTiles(new Board.TileAt(pf.pos(2, 2), MOUNTAIN));
//            gameBoard = gameBoard.withTiles(new Board.TileAt(pf.pos(3, 2), MOUNTAIN));
//            gameBoard = gameBoard.withTiles(new Board.TileAt(pf.pos(0, 1), MOUNTAIN));
//            gameBoard = gameBoard.withTiles(new Board.TileAt(pf.pos(1, 1), MOUNTAIN));
//            gameBoard = gameBoard.withTiles(new Board.TileAt(pf.pos(2, 1), MOUNTAIN));
//            gameBoard = gameBoard.withTiles(new Board.TileAt(pf.pos(3, 1), MOUNTAIN));
//        }
    }
    public void update(boolean correctPlayer){
        selectedTile = null;
        view.drawStacks(gameState.army(PlayingSide.BLUE), gameState.army(PlayingSide.ORANGE));
        view.drawGameBoard(gameBoard, gameState);
        if(correctPlayer) {
            ArrayList<BoardPos> possibleSteps = new ArrayList<>();
            PositionFactory pf = new PositionFactory(4);
            possibleSteps = generatePossibleStepList(gameState);
            view.drawPossibleMoves(possibleSteps, -1, -1);
        }
    }
    public void update(int i, int j, boolean stackSelected){
        PositionFactory pf = new PositionFactory(4);
        ArrayList<BoardPos> possibleSteps = new ArrayList<>();
        GameState oldGameState = gameState;
        if(gameState.canPlaceFromStack(pf.pos(i,j)) && stackSelected){
            if(!gameState.armyOnTurn().stack().isEmpty()) {
                gameState = gameState.placeFromStack(pf.pos(i, j));
                view.drawGameBoard(gameBoard, gameState);
                selectedTile = null;
            }
        }else if(selectedTile == null ){
            possibleSteps = generatePossibleStepList(i, j);
            view.drawGameBoard(gameBoard, gameState);
            view.drawPossibleMoves(possibleSteps, i, j);
            selectedTile = pf.pos(i,j);
        }
        else{
            possibleSteps = generatePossibleStepList(selectedTile.i(), selectedTile.j());
            for(int iI = 0; iI < possibleSteps.size(); iI++){
                if(possibleSteps.get(iI).equals(pf.pos(i,j))){
                    gameState = gameState.armyOnTurn().boardTroops().at(selectedTile).get().movesFrom(selectedTile, gameState).get(iI).execute(gameState);
                    view.drawGameBoard(gameBoard, gameState);
                    break;
                }
            }
            view.drawGameBoard(gameBoard, gameState);
            selectedTile = null;
        }
        possibleSteps = generatePossibleStepList(gameState);
        if(gameState.armyOnTurn().boardTroops().isLeaderPlaced() && possibleSteps.isEmpty() && draw()){
            gameState = oldGameState.resign();
        }else {
            if (draw() && generatePossibleStepList(oldGameState).isEmpty() && generatePossibleStepList(gameState).isEmpty()) {
                gameState = gameState.draw();
            }
        }
        if(gameState.result() != GameResult.IN_PLAY){
            view.drawEndGame(gameState);
        }
    }
    private boolean draw() {
        for (BoardPos troops : gameState.armyOnTurn().boardTroops().troopPositions()) {
            if (!generatePossibleStepList(troops.i(), troops.j()).isEmpty()) {
                return false;
            }
        }
        for (BoardPos troops : gameState.armyNotOnTurn().boardTroops().troopPositions()) {
            if (!generatePossibleStepList(troops.i(), troops.j()).isEmpty()) {
                return false;
            }
        }
        if (!gameState.armyOnTurn().boardTroops().isLeaderPlaced() || gameState.armyOnTurn().boardTroops().isPlacingGuards())
            return false;
        return true;
    }
    private ArrayList<BoardPos> generatePossibleStepList(int i, int j){
        ArrayList<BoardPos> result = new ArrayList<>();
        PositionFactory pf = new PositionFactory(4);
        if(!gameState.armyOnTurn().boardTroops().at(pf.pos(i,j)).isPresent())
            return result;
        for(Move move : gameState.armyOnTurn().boardTroops().at(pf.pos(i,j)).get().movesFrom(pf.pos(i,j),gameState)){
            result.add(move.target());
        }
        return result;
    }
    private ArrayList<BoardPos> generatePossibleStepList(GameState gameState){
        ArrayList<BoardPos> result = new ArrayList<>();
        PositionFactory pf = new PositionFactory(4);
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(gameState.canPlaceFromStack(pf.pos(i,j))){
                    result.add(pf.pos(i,j));
                }
            }
        }
        return result;
    }
}

