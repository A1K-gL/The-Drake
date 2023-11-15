package thedrake;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameState {
    private final Board board;
    private final PlayingSide sideOnTurn;
    private final Army blueArmy;
    private final Army orangeArmy;
    private final GameResult result;

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy) {
        this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
    }

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy,
            PlayingSide sideOnTurn,
            GameResult result) {
        this.board = board;
        this.sideOnTurn = sideOnTurn;
        this.blueArmy = blueArmy;
        this.orangeArmy = orangeArmy;
        this.result = result;
    }

    public Board board() {
        return board;
    }

    public PlayingSide sideOnTurn() {
        return sideOnTurn;
    }

    public GameResult result() {
        return result;
    }

    public Army army(PlayingSide side) {
        if (side == PlayingSide.BLUE) {
            return blueArmy;
        }

        return orangeArmy;
    }

    public Army armyOnTurn() {
        return army(sideOnTurn);
    }

    public Army armyNotOnTurn() {
        if (sideOnTurn == PlayingSide.BLUE)
            return orangeArmy;

        return blueArmy;
    }

    public Tile tileAt(TilePos pos) {
        if(blueArmy.boardTroops().at(pos) != null || orangeArmy.boardTroops().at(pos) != null)
            return null;
        return board.at(pos);
    }

    private boolean canStepFrom(TilePos origin) {
        if(result != GameResult.IN_PLAY){
            return false;
        }
        if(origin == TilePos.OFF_BOARD)
            return false;
        switch (sideOnTurn) {
            case BLUE -> {
                if(!blueArmy.boardTroops().isPlacingGuards() && blueArmy.boardTroops().isLeaderPlaced()){
                    return blueArmy.boardTroops().at(origin) != null;
                }
            }
            case ORANGE -> {
                if(orangeArmy.boardTroops().isLeaderPlaced() && !orangeArmy.boardTroops().isPlacingGuards()){
                    return orangeArmy.boardTroops().at(origin) != null;
                }
            }
        }
        return false;
    }

    private boolean canStepTo(TilePos target) {
        if(result != GameResult.IN_PLAY){
            return false;
        }
        if(target == TilePos.OFF_BOARD)
            return false;
        if(blueArmy.boardTroops().troopPositions().contains(target) || orangeArmy.boardTroops().troopPositions().contains(target))
            return false;
        return board.at(target).canStepOn();
    }

    private boolean canCaptureOn(TilePos target) {
        if(result != GameResult.IN_PLAY){
            return false;
        }
        switch (sideOnTurn){
            case BLUE -> {
                if(orangeArmy.boardTroops().troopPositions().contains(target))
                    return true;
            }
            case ORANGE -> {
                if(blueArmy.boardTroops().troopPositions().contains(target))
                    return true;
            }
        }
        return false;
    }

    public boolean canStep(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canStepTo(target);
    }

    public boolean canCapture(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canCaptureOn(target);
    }

    public boolean canPlaceFromStack(TilePos target) {
        List<BoardPos> tmp = new ArrayList<>();
        tmp.addAll(blueArmy.boardTroops().troopPositions());
        tmp.addAll(orangeArmy.boardTroops().troopPositions());
        if(result != GameResult.IN_PLAY){
            return false;
        }
        if(target == TilePos.OFF_BOARD)
            return false;
        switch (sideOnTurn){
            case BLUE -> {
                if(blueArmy.stack().isEmpty())
                    return false;
                if(!blueArmy.boardTroops().isLeaderPlaced()){
                    if(target.j() > 0)
                        return false;
                }
                else if(blueArmy.boardTroops().isPlacingGuards()){
                    if(!blueArmy.boardTroops().leaderPosition().isNextTo(target) || tmp.contains(target))
                        return false;
                }else{
                    for (BoardPos i: blueArmy.boardTroops().troopPositions()) {
                        if(i.isNextTo(target))
                            return board.at(target).canStepOn() && !tmp.contains(target);
                    }
                    return false;
                }
            }
            case ORANGE -> {
                if(orangeArmy.stack().isEmpty())
                    return false;
                if(!orangeArmy.boardTroops().isLeaderPlaced()){
                    if(target.j() + 1 != board.dimension() || (blueArmy.boardTroops().leaderPosition().i() == target.i()))
                        return false;
                }
                else if(orangeArmy.boardTroops().isPlacingGuards()){
                    if(!orangeArmy.boardTroops().leaderPosition().isNextTo(target) || tmp.contains(target))
                        return false;
                }else{
                    for (BoardPos i: orangeArmy.boardTroops().troopPositions()) {
                        if(i.isNextTo(target))
                            return board.at(target).canStepOn() && !tmp.contains(target);
                    }
                    return false;
                }

            }
        }
        return true;
    }

    public GameState stepOnly(BoardPos origin, BoardPos target) {
        if (canStep(origin, target))
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);

        throw new IllegalArgumentException();
    }

    public GameState stepAndCapture(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopStep(origin, target).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState captureOnly(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopFlip(origin).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState placeFromStack(BoardPos target) {
        if (canPlaceFromStack(target)) {
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().placeFromStack(target),
                    GameResult.IN_PLAY);
        }
        throw new IllegalArgumentException();
    }

    public GameState resign() {
        return createNewGameState(
                armyNotOnTurn(),
                armyOnTurn(),
                GameResult.VICTORY);
    }

    public GameState draw() {
        return createNewGameState(
                armyOnTurn(),
                armyNotOnTurn(),
                GameResult.DRAW);
    }

    private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
        if (armyOnTurn.side() == PlayingSide.BLUE) {
            return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
        }

        return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result);
    }
}
