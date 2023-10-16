package thedrake;

public enum TroopFace{
    AVERS,
    REVERS;
    private Offset2D pivot;
    public void setPivot(Offset2D pivot){
        this.pivot = pivot;
    }
    Offset2D get_pivot(){
        return pivot;
    }
}
