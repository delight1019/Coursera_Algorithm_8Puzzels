import java.util.Iterator;

public class Board {
    private int[][] tiles;
    private int size;
    private int zero;
    private int manhattan;
    private int hamming;

    private class location {
        int row;
        int col;

        public location(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.size = tiles.length;

        this.tiles = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.tiles[i][j] = tiles[i][j];


                if (tiles[i][j] == 0) {
                    zero = i * size + j;
                }

                int value = tiles[i][j];

                int row = (value-1) / size;
                int col = (value-1) % size;

                if (value != 0) {
                    this.manhattan += Math.abs(row - i) + Math.abs(col - j);
                }

                if (tiles[i][j] != 0 && tiles[i][j] != i * size + j + 1) {
                    this.hamming++;
                }
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(size);
        stringBuilder.append('\n');

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                stringBuilder.append(tiles[i][j]);
                stringBuilder.append(' ');
            }

            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }

    // board dimension n
    public int dimension() {
        return size;
    }

    // number of tiles out of place
    public int hamming() {
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        int[][] goal = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == size-1 && j == size-1) {
                    goal[i][j] = 0;
                }
                else {
                    goal[i][j] = i * size + j + 1;
                }
            }
        }

        return equals(new Board(goal));
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) {
            return true;
        }

        if (y == null) {
            return false;
        }

        if (y.getClass() != this.getClass()) {
            return false;
        }

        Board that = (Board) y;

        if (this.size != that.size) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (this.tiles[i][j] != that.tiles[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        return new Iterable<Board>() {
            @Override
            public Iterator<Board> iterator() {
                return new Iterator<Board>() {
                    private Board current = new Board(tiles);
                    private int currentNeigbor = 0;

                    @Override
                    public boolean hasNext() {
                        if (current.numOfNeighbors() == 0) {
                            return false;
                        }

                        return (currentNeigbor < current.numOfNeighbors());
                    }

                    @Override
                    public Board next() {
                        int row = zero / size;
                        int col = zero % size;

                        if (currentNeigbor == 0) {
                            currentNeigbor++;

                            if (col != 0) {
                                location src = new location(row, col);
                                location dest = new location(row, col-1);

                                return current.twin(src, dest);
                            }
                        }

                        if (currentNeigbor == 1) {
                            currentNeigbor++;

                            if (row != size-1) {
                                location src = new location(row, col);
                                location dest = new location(row+1, col);

                                return current.twin(src, dest);
                            }
                        }

                        if (currentNeigbor == 2) {
                            currentNeigbor++;

                            if (col != size-1) {
                                location src = new location(row, col);
                                location dest = new location(row, col+1);

                                return current.twin(src, dest);
                            }
                        }

                        if (currentNeigbor == 3) {
                            currentNeigbor++;

                            if (row != 0) {
                                location src = new location(row, col);
                                location dest = new location(row-1, col);

                                return current.twin(src, dest);
                            }
                        }

                        return null;
                    }
                };
            }
        };
    }

    private Board twin(location p, location q) {
        int[][] newTiles = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == p.row && j == p.col) {
                    newTiles[i][j] = tiles[q.row][q.col];
                }
                else if (i == q.row && j == q.col) {
                    newTiles[i][j] = tiles[p.row][p.col];
                }
                else {
                    newTiles[i][j] = tiles[i][j];
                }
            }
        }

        return new Board(newTiles);
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        location src = null;
        location dest = null;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (tiles[i][j] != 0) {
                    if (src == null) {
                        src = new location(i, j);
                    }
                    else if (dest == null) {
                        dest = new location(i, j);
                        return twin(src, dest);
                    }
                }
            }
        }

        return null;
    }

    private int numOfNeighbors() {
        int row = zero / size;
        int col = zero % size;

        if (row != 0) {
            return 4;
        }

        if (col != size-1) {
            return 3;
        }

        if (row != size-1) {
            return 2;
        }

        if (row != 0) {
            return 1;
        }

        return 0;
    }

    // unit testing (not graded)
    public static void main(String[] args) {

    }

}
