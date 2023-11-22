package thedrake;

import java.io.PrintWriter;

public class Board implements JSONSerializable{
    private final int dimension;
    private final TileAt[][] tiles;
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

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{\"dimension\":%d,\"tiles\":[", dimension);
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                tiles[j][i].tile.toJSON(writer);
                if(i + 1 != dimension || j + 1 != dimension)
                    writer.printf(",");
                writer.printf("");
            }
        }
        writer.printf("]}");
    }
}

