package thedrake;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TroopTile implements Tile, JSONSerializable{
    private final Troop troop;
    private final PlayingSide playingSide;
    private final TroopFace troopFace;
    public TroopTile(Troop troop, PlayingSide side, TroopFace face){
        this.troop = troop;
        this.playingSide = side;
        this.troopFace = face;
    }
    public PlayingSide side(){
        return playingSide;
    }
    public TroopFace face(){
        return troopFace;
    }
    public Troop troop(){
        return troop;
    }
    @Override
    public boolean canStepOn() {
        return false;
    }

    @Override
    public boolean hasTroop() {
        return true;
    }
    public TroopTile flipped(){
        return new TroopTile(troop, playingSide, (troopFace == TroopFace.AVERS) ? TroopFace.REVERS : TroopFace.AVERS);
    }

    @Override
    public List<Move> movesFrom(BoardPos pos, GameState state){
        List<Move> result = new ArrayList<>();
        List<TroopAction> actions = state.armyOnTurn().boardTroops().at(pos).get().troop.actions(face());
        for (TroopAction iter : actions) {
            result.addAll(iter.movesFrom(pos,playingSide,state));
        }
        return result;
    }
    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{\"troop\":");
        troop.toJSON(writer);
        writer.printf(",\"side\":");
        playingSide.toJSON(writer);
        writer.printf(",\"face\":");
        troopFace.toJSON(writer);
        writer.printf("}");
    }
}
