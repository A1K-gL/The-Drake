package thedrake;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BoardTroops {
    private final PlayingSide playingSide;
    private final Map<BoardPos, TroopTile> troopMap;
    private final TilePos leaderPosition;
    private final int guards;

    public BoardTroops(PlayingSide playingSide) {
        this.playingSide = playingSide;
        this.troopMap = Collections.emptyMap();
        this.leaderPosition = TilePos.OFF_BOARD;
        this.guards = 0;
    }

    public BoardTroops(
            PlayingSide playingSide,
            Map<BoardPos, TroopTile> troopMap,
            TilePos leaderPosition,
            int guards) {
        this.playingSide = playingSide;
        this.troopMap = troopMap;
        this.leaderPosition = leaderPosition;
        this.guards = guards;
    }

    public Optional<TroopTile> at(TilePos pos) {
        return Optional.ofNullable(troopMap.get(pos));
    }

    public PlayingSide playingSide() {
        return playingSide;
    }

    public TilePos leaderPosition() {
        return leaderPosition;
    }

    public int guards() {
        return guards;
    }

    public boolean isLeaderPlaced() {
        return leaderPosition!=TilePos.OFF_BOARD;
    }

    public boolean isPlacingGuards() {
        return isLeaderPlaced() && guards < 2;
    }

    public Set<BoardPos> troopPositions() {
        return troopMap.keySet();
    }



    public BoardTroops placeTroop(Troop troop, BoardPos target) {
        if(this.troopMap.containsKey(target))
            throw new IllegalArgumentException();
        Map<BoardPos, TroopTile> new_troopMap = new HashMap<>(troopMap);
        new_troopMap.put(target, new TroopTile(troop,playingSide,TroopFace.AVERS));
        TilePos leader = this.leaderPosition;
        int guards = this.guards;
        if(!isLeaderPlaced())
            leader = target;
        else if(isPlacingGuards())
            guards++;
        return new BoardTroops(this.playingSide, new_troopMap, leader, guards);
    }

    public BoardTroops troopStep(BoardPos origin, BoardPos target) {
        if(isPlacingGuards() || !isLeaderPlaced())
            throw new IllegalStateException();
        if(!this.troopMap.containsKey(origin) || this.troopMap.containsKey(target))
            throw new IllegalArgumentException();
        Map<BoardPos, TroopTile> new_troopMap = new HashMap<>(troopMap);
        new_troopMap.put(target, troopMap.get(origin).flipped());
        TilePos new_leader_pos = this.leaderPosition;
        if(origin.equals(new_leader_pos))
            new_leader_pos = target;
        return new BoardTroops(this.playingSide, new_troopMap, new_leader_pos, this.guards).removeTroop(origin);
    }

    public BoardTroops troopFlip(BoardPos origin) {
        if (!isLeaderPlaced()) {
            throw new IllegalStateException(
                    "Cannot move troops before the leader is placed.");
        }

        if (isPlacingGuards()) {
            throw new IllegalStateException(
                    "Cannot move troops before guards are placed.");
        }

        if (!at(origin).isPresent())
            throw new IllegalArgumentException();
        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        TroopTile tile = newTroops.remove(origin);
        newTroops.put(origin, tile.flipped());
        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    public BoardTroops removeTroop(BoardPos target) {
        if(isPlacingGuards() || !isLeaderPlaced())
            throw new IllegalStateException();
        if(!this.troopMap.containsKey(target))
            throw new IllegalArgumentException();
        Map<BoardPos, TroopTile> new_troop_map = troopMap;
        new_troop_map.remove(target);
        TilePos new_leader_pos = leaderPosition;
        if(target.equals(leaderPosition))
            new_leader_pos = TilePos.OFF_BOARD;
        return new BoardTroops(this.playingSide, new_troop_map, new_leader_pos, this.guards);
    }
}
