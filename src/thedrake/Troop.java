package thedrake;

public class Troop {
    private final String name;
    private final TroopFace aversFace = TroopFace.AVERS;
    private final TroopFace reversFace = TroopFace.REVERS;

    public Troop(String name, Offset2D aversPivot, Offset2D reversPivot){
        this.name = name;
        aversFace.setPivot(aversPivot);
        reversFace.setPivot(reversPivot);
    }
    public Troop(String name, Offset2D pivot){
        this.name = name;
        aversFace.setPivot(pivot);
        reversFace.setPivot(pivot);
    }
    public Troop(String name){
        this.name = name;
        aversFace.setPivot(new Offset2D(1,1));
        reversFace.setPivot(new Offset2D(1,1));
    }
    public String name(){
        return name;
    }
    public Offset2D pivot(TroopFace face){
        return face.get_pivot();
    }
}
