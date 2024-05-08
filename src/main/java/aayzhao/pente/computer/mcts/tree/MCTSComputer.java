package aayzhao.pente.computer.mcts.tree;

import aayzhao.pente.computer.Move;
import aayzhao.pente.computer.MoveImpl;
import aayzhao.pente.computer.PenteComputer;
import aayzhao.pente.computer.RandomizedMoveSet;
import aayzhao.pente.computer.mcts.RandBlockGame;
import aayzhao.pente.computer.mcts.RandomGame;
import aayzhao.pente.game.model.Board;
import aayzhao.pente.game.model.Model;
import aayzhao.pente.game.model.ModelImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class MCTSComputer implements PenteComputer {
    protected static Random random = new Random();
    private static boolean DEBUG = true;
    public static final double C_CONST = Math.sqrt(2);
    MCTSNode root;
    private int size;
    private boolean firstMove;
    static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    static final int TARGET_THREADS = Math.max(1, Math.min(8, NUM_THREADS - 1));
    static final ExecutorService executorService = Executors.newFixedThreadPool(TARGET_THREADS);
    public MCTSComputer(int size) {
        if (DEBUG) System.out.println(NUM_THREADS + " threads available");
        if (DEBUG) System.out.println(TARGET_THREADS + " threads acquired");
        this.root = null;
        this.size = size;
        this.firstMove = true;
    }
    @Override
    public double advantage(int halfPly, Board board, int whiteCaptures, int blackCaptures) {
        return 0;
    }

    @Override
    public Move bestMove(int halfPly, Board board, int whiteCaptures, int blackCaptures, Move oppMove) throws InterruptedException {
        if (root != null) {
            root = root.children.get(oppMove); // move to correct node for opponent move
        }
        if (root == null) {
            // generate root node
            root = MCTSNode.createNode(board, whiteCaptures, blackCaptures, halfPly);
            System.out.println(root);
        }

        root.parent = null; // remove parent node, as that is not under consideration anymore

        // begin searching for at least 5 seconds:
        long startTime = System.nanoTime();
        long searchTime = 5000000000L;
        while (System.nanoTime() - startTime < searchTime) {
            MCTSNode leaf = traverse(root);

            leaf.rollout();
        }
//        for (int i = 0; i < 10; i++) {
//            System.out.println("Traversal " + (i + 1) + ":");
//            MCTSNode leaf = traverse(root);
//            System.out.println("Testing " + leaf.move);
//            leaf.rollout();
//        }

        if (DEBUG) System.out.println(root.proportion + " is the old proportion");
        root = bestNode(root); // move root to the chosen node

        if (root.parent != null && root.parent.parent != null) root.parent.parent = null;
        return root.move;
    }

    @Override
    public Move bestMove(int halfPly, Move prevMove) throws InterruptedException {
        return null;
    }

    private MCTSNode bestUCT(MCTSNode node) {
        // if (node.move == null) return MCTSNode.createNode(node, node.possibleChildren.getRandom());
        double curUCT = 0.0;
        MCTSNode curNode = null;
        if (node.isLeaf()) {
            curUCT = UCT(node);
            curNode = node;
        }

        for (Move move : node.children.keySet()) {
            if (curNode == null) {
                curNode = node.children.get(move);
                curUCT = UCT(curNode);
            } else {
                MCTSNode target = node.children.get(move);
                double targetUCT = UCT(target);
                if (Double.compare(curUCT, targetUCT) < 0) {
                    curNode = target;
                    curUCT = targetUCT;
                }
            }
        }

        if (node == curNode) {
            return MCTSNode.createNode(node, node.possibleChildren.getRandom());
        }

        return curNode;
    }

    private MCTSNode bestNode(MCTSNode node) {
        MCTSNode next = null;
        if (DEBUG) System.out.println(root.model.getHalfPly() + " is the half ply");
        for (Move key : node.children.keySet()) {
            if (next == null) next = node.children.get(key);
            else {
                MCTSNode target = node.children.get(key);
                if (DEBUG) System.out.println(target.proportion + "\t\t\t" + target.move);
                if (target.proportion.denominator > next.proportion.denominator) next = target;
                else if (target.proportion.denominator == next.proportion.denominator) {
                    if (target.proportion.numerator > next.proportion.numerator) next = target;
                }
            }
        }

        if (DEBUG) System.out.printf("%.2f\t\t\t%s\n",
                ((double)(next.proportion.numerator)) / ((double) next.proportion.denominator),
                next.move);

        return next;
    }

    private double UCT(MCTSNode node) {
        // wi / ni + C_CONST sqrt(ln(Ni) / ni)
        double wi = node.proportion.numerator;
        double ni = node.proportion.denominator;
        double Ni = node.parent != null ? node.parent.proportion.denominator : 0;
        return (wi / ni) + C_CONST * (Math.sqrt(Math.log(Ni) / ni));
    }

    private MCTSNode traverse(MCTSNode node) {
        // System.out.println(!node.children.isEmpty() || node.move == null || node.possibleChildren.getIndex() == 0);
        while (!node.children.isEmpty() || node.move == null || node.possibleChildren.getIndex() == 0) node = bestUCT(node);
        return node;
    }

    @Override
    public String toString() {
        return "MCTSv2.0";
    }


    public void shutdown() {
        executorService.shutdown();
    }
}

class MCTSNode {
    /**
     * default game size for nodes in this tree
     */
    public static int DEFAULT_GAME_SIZE = 9;
    /**
     * Parent MCTS Node
     */
    public MCTSNode parent;
    /**
     * Hashmap of possible moves in the position, and the corresponding child nodes.
     */
    public ConcurrentHashMap<Move, MCTSNode> children;
    /**
     * the proportion of wins for the player making the move this node is associated with vs. rollouts done
     */
    public Proportion proportion;
    /**
     * The move the player is making
     */
    public Move move;
    /**
     * The game state BEFORE the move stored by this node has been made
     * A halfPly % 2 == 1 for the model indicates that WHITE is making the move this node is
     * associated with, and that proportion is the proportion of WHITE wins.
     */
    public Model model;
    /**
     * A randomized set that guarantees O(1) insert, delete, and get random.
     */
    public RandomizedMoveSet possibleChildren;

    /**
     * Factory method for creating new MCTSNodes. If not null, this method
     * will automatically create a new node with the parent's move made. Otherwise,
     * a node representing a new, default game will be created.
     * @param parent    Can be null. Previous node
     * @param move      Move to be associated with this node
     * @return          Pointer to the newly created node
     */
    public static MCTSNode createNode(MCTSNode parent, Move move) {
        Model model;
        if (parent != null) {
            MCTSNode newNode = new MCTSNode(
                    move,
                    parent.model.getBoard(),
                    parent.model.getWhitePlayerCaptures(),
                    parent.model.getBlackPlayerCaptures(),
                    parent.model.getHalfPly()
            );

            if (parent.move != null) {
                if (!newNode.model.isValidMove(parent.move.getRowCoord(), parent.move.getColumnCoord()))
                    throw new IllegalArgumentException("Parent move does not exist for board");
                newNode.model.move( // make the move of the parent
                        parent.move.getRowCoord(),
                        parent.move.getColumnCoord()
                );
            }
            parent.addNode(newNode);
            newNode.addLeafsToSet();
            return newNode;
        } else {
            model = new ModelImpl(DEFAULT_GAME_SIZE);
            MCTSNode newNode = new MCTSNode();
            newNode.model = model;
            newNode.move = move;
            newNode.addLeafsToSet();
            return newNode;
        }
    }

    /**
     * Creates a new node, and assumes that the board is the state AFTER the opponent's previous move.
     * The board passed in must be a copy of the actual board, as the node assumes that it will
     * be the only process to modify this board after it is passed in.
     * @param board         Current board state
     * @param whiteCaptures Current white captures
     * @param blackCaptures Current black captures
     * @param halfPly       Halfply for the game
     * @return              A new node
     */
    public static MCTSNode createNode(Board board, int whiteCaptures, int blackCaptures, int halfPly) {
        MCTSNode newNode = new MCTSNode();
        newNode.model = new ModelImpl(board, whiteCaptures, blackCaptures, halfPly);
        newNode.addLeafsToSet();
        return newNode;
    }

    /**
     * Default constructor and creates a node with most fields null
     */
    private MCTSNode() {
        this.move = null;
        this.model = null;
        this.proportion = new Proportion();
        this.children = new ConcurrentHashMap<>();
        this.parent = null;
        this.possibleChildren = new RandomizedMoveSet(DEFAULT_GAME_SIZE, MCTSComputer.random);
    }

    /**
     * Creates a parentless node for the given move and game state. NOTE: move and board will
     * be used internally and the node expects that it will be the only process modifying
     * the input board after its creation.
     * @param move          The move to make
     * @param board         The current board state, before the move has been played
     * @param whiteCaptures The current white player's captures
     * @param blackCaptures The current black player's captures
     * @param halfPly       The halfply before the move is being made. Indicates white (odd) or black (even) to play
     */
    private MCTSNode(Move move, Board board, int whiteCaptures, int blackCaptures, int halfPly) {
        this.move = move;
        this.model = new ModelImpl(board, whiteCaptures, blackCaptures, halfPly);
        this.proportion = new Proportion();
        this.children = new ConcurrentHashMap<>();
        this.parent = null;
        this.possibleChildren = new RandomizedMoveSet(board.getSize(), MCTSComputer.random);
    }

    /**
     * Adds the specified node to this node's list of children. Also updates the
     * new child's parent pointer to this node
     * @param child     Node to be added
     */
    private void addNode(MCTSNode child) {
        this.children.put(child.move, child);
        this.possibleChildren.remove(child.move);
        child.parent = this;
    }

    /**
     * Adds all possible moves to the randomized move set
     */
    private void addLeafsToSet() {
        Model temp = new ModelImpl(
                this.model.getBoard(),
                this.model.getWhitePlayerCaptures(),
                this.model.getBlackPlayerCaptures(),
                this.model.getHalfPly()
        );
        if (this.move != null) temp.move(this.move.getRowCoord(), this.move.getColumnCoord());

        // note possible leafs from this node
        for (int i = 0; i < this.model.getBoardSize(); i++) {
            for (int j = 0; j < this.model.getBoardSize(); j++) {
                if (temp.isValidMove(i, j)) {
                    this.possibleChildren.insert(new MoveImpl(i, j));
                }
            }
        }
    }

    public boolean isLeaf() {
        return this.possibleChildren.getIndex() != 0;
    }

    /**
     * Performs a rollout for this node and the move this node is associated with.
     * If the rollout results in a win for white (positive score), and this node is
     * representing a position for white to play (this.model.getHalfPly() is odd), then the node's numerator
     * is incremented.
     * Vice versa for black.
     */
    public void rollout() {
        if (this.move == null) throw new IllegalStateException("Null move for + " + this);
        // if (this.proportion.denominator > 0)
        //    throw new IllegalStateException("Cannot do a rollout on already simulated node");
//        RandomGame game = new RandBlockGame(
//                this.model.getHalfPly(),
//                this.model.getBoard(),
//                this.model.getWhitePlayerCaptures(),
//                this.model.getBlackPlayerCaptures(),
//                this.move,
//                1
//        );
//        game.run();

        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < MCTSComputer.TARGET_THREADS; i++) {
            RandomGame rolloutGame = new RandBlockGame(
                    this.model.getHalfPly(),
                    this.model.getBoard(),
                    this.model.getWhitePlayerCaptures(),
                    this.model.getBlackPlayerCaptures(),
                    this.move,
                    1
            );
            futures.add(MCTSComputer.executorService.submit(new RolloutTask(rolloutGame)));
        }

//        this.backpropagate(game.score);

        // int totalScore = 0;
        for (Future<Integer> future : futures) {
            try {
                backpropagate(future.get());
            } catch (InterruptedException | ExecutionException e) {
                // Handle exceptions
            }
        }

        // this.backpropagate(totalScore);
    }

    /**
     * Takes in a game score which is either a 1 (win for white) or -1 (win for black).
     * If the node represents white to move (halfPly % 2 == 1), then its numerator is incremented
     * as well as the denominator, else only denominator is incremented.
     * @param score     Result of the rollout game.
     */
    private synchronized void backpropagate(int score) {
        // if (move == null) System.out.println("Updating parent proportions");
        // if (score != 1 && score != -1) throw new IllegalArgumentException("Invalid score for a rollout");
        this.proportion.denominator += 1;
        if (this.model.getHalfPly() % 2 == 1 && score > 0) this.proportion.numerator += 1;
        else if (this.model.getHalfPly() % 2 == 0 && score < 0) this.proportion.numerator += 1;
        if (parent != null) parent.backpropagate(score);
        // if (move == null) System.out.println(proportion);
    }

    /**
     * Rollout task that encapsulates a random game's execution.
     */
    private static class RolloutTask implements Callable<Integer> {
        private final RandomGame game;

        public RolloutTask(RandomGame game) {
            this.game = game;
        }

        @Override
        public Integer call() {
            game.run();
            return game.score;
        }
    }
}
