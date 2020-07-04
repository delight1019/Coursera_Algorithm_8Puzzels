import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

public class Solver {
    private SearchNode answer;
    private boolean isSolvable;
    private int moves;

    private class SearchNode implements Comparable<SearchNode> {
        Board board;
        int moves;
        SearchNode prev;
        SearchNode next;

        public SearchNode(Board board, int moves, SearchNode prev) {
            this.board = board;
            this.moves = moves;
            this.prev = prev;
            this.next = null;
        }


        @Override
        public int compareTo(SearchNode that) {
            if (this.board.manhattan() < that.board.manhattan()) {
                return -1;
            }
            else if (this.board.manhattan() > that.board.manhattan()) {
                return 1;
            }

            return 0;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }

        solve(initial);
    }

    private void solve(Board initial) {
        Board swappedBoard = initial.twin();

        MinPQ<SearchNode> minPQ = new MinPQ<>();
        MinPQ<SearchNode> swappedMinPQ = new MinPQ<>();
        SearchNode initialSearchNode = new SearchNode(initial, 0, null);
        SearchNode swappedInitialSearchNode = new SearchNode(swappedBoard, 0, null);
        minPQ.insert(initialSearchNode);
        swappedMinPQ.insert(swappedInitialSearchNode);
        SearchNode current = minPQ.delMin();
        SearchNode swappedCurrent = swappedMinPQ.delMin();

        while(true) {
            if (current.board.isGoal()) {
                moves = current.moves;

                while (current.prev != null) {
                    current.prev.next = current;
                    current = current.prev;
                }

                answer = current;
                isSolvable = true;
                return;
            }

            if (swappedCurrent.board.isGoal()) {
                isSolvable = false;
                return;
            }

            for (Board nextBoard : current.board.neighbors()) {
                if (current.prev == null || !nextBoard.equals(current.prev.board)) {
                    minPQ.insert(new SearchNode(nextBoard, current.moves + 1, current));
                }
            }

            for (Board nextBoard : swappedCurrent.board.neighbors()) {
                if (swappedCurrent.prev == null || !nextBoard.equals(swappedCurrent.prev.board)) {
                    swappedMinPQ.insert(new SearchNode(nextBoard, swappedCurrent.moves + 1, swappedCurrent));
                }
            }

            current = minPQ.delMin();
            swappedCurrent = swappedMinPQ.delMin();
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!isSolvable()) {
            return -1;
        }

        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) {
            return null;
        }

        return new Iterable<Board>() {
            @Override
            public Iterator<Board> iterator() {
                return new Iterator<Board>() {
                    private SearchNode current = answer;

                    @Override
                    public boolean hasNext() {
                        return current != null;
                    }

                    @Override
                    public Board next() {
                        Board board = current.board;
                        current = current.next;

                        return board;
                    }
                };
            }
        };
    }

    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
