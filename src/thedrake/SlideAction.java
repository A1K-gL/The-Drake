package thedrake;

import java.util.ArrayList;
import java.util.List;

public class SlideAction extends TroopAction{
    public SlideAction(Offset2D offset){ super(offset);}
    public SlideAction(int OffsetX, int OffsetY) { super(OffsetX, OffsetY); }

    @Override
    public List<Move> movesFrom(BoardPos origin, PlayingSide side, GameState state) {
        List<Move> result = new ArrayList<>();
        for(int i = 1; i > 0; i++) {
            Offset2D tmp_offset = new Offset2D(offset().x * i, offset().y * i);
            TilePos target = origin.stepByPlayingSide(tmp_offset, side);
            if (state.canStep(origin, target)) {
                result.add(new StepOnly(origin, (BoardPos) target));
            } else if (state.canCapture(origin, target)) {
                result.add(new StepAndCapture(origin, (BoardPos) target));
                break;
            }else
                break;
        }
        return result;
    }
}
