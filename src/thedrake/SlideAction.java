package thedrake;

import java.util.ArrayList;
import java.util.List;

public class SlideAction extends TroopAction{
    public SlideAction(Offset2D offset){ super(offset);}
    public SlideAction(int OffsetX, int OffsetY) { super(OffsetX, OffsetY); }

    @Override
    public List<Move> movesFrom(BoardPos origin, PlayingSide side, GameState state) {
        List<Move> result = new ArrayList<>();
        TilePos target = origin.stepByPlayingSide(offset(), side);



        return result;
    }
}
