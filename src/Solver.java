import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

public class Solver {
    private MinPQ<SearchNode> minPQ;
    private Stack<SearchNode> inserted;
    private SearchNode answer;
    private boolean isSolvable;

    private class SearchNode implements Comparable<SearchNode> {
        Board board;
        int moves;
        SearchNode prev;

        public SearchNode(Board board, int moves, SearchNode prev) {
            this.board = board;
            this.moves = moves;
            this.prev = prev;
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

        minPQ = new MinPQ<>();
        inserted = new Stack<>();

        SearchNode initialSearchNode = new SearchNode(initial, 0, null);

        minPQ.insert(initialSearchNode);
        inserted.push(initialSearchNode);

        SearchNode current = minPQ.delMin();

        while(!current.board.isGoal()) {
            for (Board nextBoard : current.board.neighbors()) {
//                boolean isInserted = false;
//
//                for (SearchNode prevNode : inserted) {
//                    if (nextBoard.equals(prevNode.board)) {
//                        isInserted = true;
//                        break;
//                    }
//                }
//
//                if (!isInserted) {
//                    StdOut.println(nextBoard);
//                    minPQ.insert(new SearchNode(nextBoard, current.moves + 1, current));
//                    inserted.push(new SearchNode(nextBoard, current.moves + 1, current));
//                }

                if (current.prev == null || !nextBoard.equals(current.prev.board)) {
                    minPQ.insert(new SearchNode(nextBoard, current.moves + 1, current));
                }
            }

            if (minPQ.isEmpty()) {
                isSolvable = false;
                return;
            }
            else {
                current = minPQ.delMin();
                int test = 0;
            }
        }

        isSolvable = true;
        answer = current;
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

        return answer.moves;
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
                        return current.prev != null;
                    }

                    @Override
                    public Board next() {
                        Board board = current.board;
                        current = current.prev;

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
