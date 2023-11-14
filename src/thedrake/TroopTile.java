package thedrake;

import java.util.List;

public class TroopTile implements Tile{
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
    public List<Move> movesFrom(BoardPos pos, GameState state) {
        return null;
    }
}
