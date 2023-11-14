package thedrake;

public class Board {
    private final int dimension;
    private TileAt[][] tiles;
    public Board(int dimension) {
        this.dimension = dimension;
        tiles = new TileAt[dimension][dimension];
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                tiles[i][j] = new TileAt(new BoardPos(dimension,i,j), BoardTile.EMPTY);
            }
        }
    }

    // Rozměr hrací desky
    public int dimension() {
        return this.dimension;
    }
    public BoardTile at(TilePos pos) {
        return tiles[pos.i()][pos.j()].tile;
    }

    // Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
    public Board withTiles(TileAt... ats) {

        Board n_Board = new Board(dimension);
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                n_Board.tiles[i][j] = this.tiles[i][j];
            }
        }
        for(int i = 0; i < ats.length; i++){
            n_Board.tiles[ats[i].pos.i()][ats[i].pos.j()] = new TileAt(ats[i].pos, ats[i].tile);
        }
        return n_Board;
    }

    public PositionFactory positionFactory() {
        return new PositionFactory(dimension);
    }

    public static class TileAt {
        public final BoardPos pos;
        public final BoardTile tile;

        public TileAt(BoardPos pos, BoardTile tile) {
            this.pos = pos;
            this.tile = tile;
        }
    }
}

