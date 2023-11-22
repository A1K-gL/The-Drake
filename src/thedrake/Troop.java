package thedrake;

import java.io.PrintWriter;
import java.util.List;

public class Troop implements JSONSerializable {
    private final String name;
    private final Offset2D avers_pivot;
    private final Offset2D revers_pivot;
    private final List<TroopAction> aversAction;
    private final List<TroopAction> reversAction;
    public Troop(String name, Offset2D aversPivot, Offset2D reversPivot, List<TroopAction> aversAction, List<TroopAction> reversAction){
        this.name = name;
        this.avers_pivot = aversPivot;
        this.revers_pivot = reversPivot;
        this.aversAction = aversAction;
        this.reversAction = reversAction;
    }
    public Troop(String name, Offset2D pivot, List<TroopAction> aversAction, List<TroopAction> reversAction){
        this.name = name;
        this.avers_pivot = pivot;
        this.revers_pivot = pivot;
        this.aversAction = aversAction;
        this.reversAction = reversAction;
    }
    public Troop(String name, List<TroopAction> aversAction, List<TroopAction> reversAction){
        this.name = name;
        this.avers_pivot = new Offset2D(1,1);
        this.revers_pivot = new Offset2D(1,1);
        this.aversAction = aversAction;
        this.reversAction = reversAction;

    }
    public String name(){
        return name;
    }
    public Offset2D pivot(TroopFace face){
        switch (face){
            case AVERS -> {
                return avers_pivot;
            }
            case REVERS -> {
                return revers_pivot;
            }
        }
        return null;
    }
    public List<TroopAction> actions(TroopFace face){
        if(face == TroopFace.AVERS)
            return aversAction;
        else
            return reversAction;
    }
    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("\"%s\"", this.name);
    }
}
