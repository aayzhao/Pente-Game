package aayzhao.pente.computer.mcts.tree;

import aayzhao.pente.computer.Move;
import aayzhao.pente.computer.MoveImpl;
import aayzhao.pente.computer.PenteComputer;
import aayzhao.pente.computer.mcts.RandomGame;
import aayzhao.pente.game.model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Deprecated
public class MCTSComputerDeprecated implements PenteComputer {
    private static boolean DEBUG = true;
    public static final double C_CONST = Math.sqrt(2.0);
    private MCTSNodeDeprecated root;
    private int size;
    private boolean firstMove;

    public MCTSComputerDeprecated(int size) {
        this.size = size;
        firstMove = true;
    }

    @Override
    public double advantage(int halfPly, Board board, int whiteCaptures, int blackCaptures) {
        return 0;
    }

    /**
     *
     * @param halfPly           odd indicates white's turn to move, even indicates black
     * @param board             Board to analyze
     * @param whiteCaptures     times white has captured
     * @param blackCaptures     times black has captured
     * @param oppMove           the previous move played to arrive in this position
     * @return
     * @throws InterruptedException
     */
    @Override
    public Move bestMove(int halfPly, Board board, int whiteCaptures, int blackCaptures, Move oppMove) throws InterruptedException {
        if (root != null) {
            root = root.children.get(oppMove); // move to correct node for opponent move
        }
        if (root == null) {
            // generate root node
            root = MCTSNodeDeprecated.createNewNode(halfPly, board, whiteCaptures, blackCaptures, oppMove);
            // generate first layer of nodes
            generateNodes(root);

            // generate second layer of nodes
            Thread[] threads = new Thread[root.children.size()];
            int i = 0;
            for (MCTSNodeDeprecated child : root.children.values()) {
                Runnable r = () -> {
                    try {
                        generateNodes(child);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                };
                Thread t = new Thread(r);
                threads[i] = t;
                threads[i].start();
                i++;
            }
            for (Thread t : threads) {
                t.join();
            }
        }
        root.parent = null; // remove parent reference, to prevent catastrophic backpropagation
        mcts(); // expand and populate tree

        root = finalChoice(root); // make the move
        root.parent = null; // remove tree from parent
        return this.root.move;
    }

    @Override
    public Move bestMove(int halfPly, Move prevMove) throws InterruptedException {
        MCTSNodeDeprecated prev = root;
        if (root != null) root = root.children.get(prevMove); // move to correct node for opponent move
        if (root == null) {
            // generate first layer of nodes
            if (prevMove == null) {
                root = MCTSNodeDeprecated.createNewWhiteCPU(size);
            } else {
                root = MCTSNodeDeprecated.createNewBlackCPU(size, prevMove);
            }
            generateNodes(root);

            // generate second layer of nodes
            Thread[] threads = new Thread[root.children.size()];
            int i = 0;
            for (MCTSNodeDeprecated child : root.children.values()) {
                Runnable r = () -> {
                    try {
                        generateNodes(child);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                };
                Thread t = new Thread(r);
                threads[i] = t;
                threads[i].start();
                i++;
            }
            for (Thread t : threads) {
                t.join();
            }
        }

        mcts();

        root = bestNode(root);
        return this.root.move;
    }

    private void mcts() throws InterruptedException {
        long startTime = System.nanoTime();
        long computationTime = 500000000L;
        while (System.nanoTime() - startTime < computationTime) {
            MCTSNodeDeprecated leaf = traverse(root); // select leaf

            generateNodes(leaf); // expand
        }
    }

    @Deprecated
    private void mctsStaged() throws InterruptedException {
        // selection and queue up the upper 40% of nodes
        Comparator<MCTSNodeDeprecated> comp;
        if (this.root.model.getHalfPly() % 2 == 1) comp = (a, b) -> b.rootProp.compareTo(a.rootProp);
        else comp = Comparator.comparing((a) -> a.rootProp);

        // repeat while processing time allows
        long startTime = System.nanoTime();
        long timeForExpansion = 2500000000L;
        // int j = 0;
        while (System.nanoTime() - startTime < timeForExpansion) {
            PriorityQueue<MCTSNodeDeprecated> queue = new PriorityQueue<>(82, comp);
            queue.addAll(root.children.values());

            int searchThreshold = (int) Math.min(Math.ceil(((double) root.children.size()) * 0.75), 1);
            Thread[] threads = new Thread[searchThreshold];
            for (int i = 0; i < searchThreshold; i++) {
                final MCTSNodeDeprecated[] node = {queue.poll()};
                Runnable r = () -> {
                    node[0] = traverse(node[0]);
                    try {
                        generateNodes(node[0]);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                };
                threads[i] = new Thread(r);
                threads[i].start();
                // System.out.printf("Expansion %d Complete\n", j++);
            }
            for (Thread t : threads) t.join();
        }

        startTime = System.nanoTime();
        timeForExpansion = 2500000000L;
        while (System.nanoTime() - startTime < timeForExpansion) {
            PriorityQueue<MCTSNodeDeprecated> queue = new PriorityQueue<>(82, comp);
            queue.addAll(root.children.values());

            int searchThreshold = (int) Math.min(Math.ceil(((double) root.children.size()) * 0.75), 1);
            Thread[] threads = new Thread[searchThreshold];
            for (int i = 0; i < searchThreshold; i++) {
                final MCTSNodeDeprecated[] node = {queue.poll()};
                Runnable r = () -> {
                    node[0] = traverse(node[0]);
                    try {
                        generateNodes(node[0]);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                };
                threads[i] = new Thread(r);
                threads[i].start();
                // System.out.printf("Expansion %d Complete\n", j++);
            }
            for (Thread t : threads) t.join();
        }

        startTime = System.nanoTime();
        timeForExpansion = 1500000000L;
        while (System.nanoTime() - startTime < timeForExpansion) {
            PriorityQueue<MCTSNodeDeprecated> queue = new PriorityQueue<>(82, comp);
            queue.addAll(root.children.values());

            int searchThreshold = (int) Math.min(Math.ceil(((double) root.children.size()) * 0.375), 1);
            Thread[] threads = new Thread[searchThreshold];
            for (int i = 0; i < searchThreshold; i++) {
                final MCTSNodeDeprecated[] node = {queue.poll()};
                Runnable r = () -> {
                    node[0] = traverse(node[0]);
                    try {
                        generateNodes(node[0]);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                };
                threads[i] = new Thread(r);
                threads[i].start();
                // System.out.printf("Expansion %d Complete\n", j++);
            }
            for (Thread t : threads) t.join();
        }

        startTime = System.nanoTime();
        timeForExpansion = 1500000000L;
        while (System.nanoTime() - startTime < timeForExpansion) {
            PriorityQueue<MCTSNodeDeprecated> queue = new PriorityQueue<>(82, comp);
            queue.addAll(root.children.values());

            int searchThreshold = (int) Math.min(Math.ceil(((double) root.children.size()) * 0.375), 1);
            Thread[] threads = new Thread[searchThreshold];
            for (int i = 0; i < searchThreshold; i++) {
                final MCTSNodeDeprecated[] node = {queue.poll()};
                Runnable r = () -> {
                    node[0] = traverse(node[0]);
                    try {
                        generateNodes(node[0]);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                };
                threads[i] = new Thread(r);
                threads[i].start();
                // System.out.printf("Expansion %d Complete\n", j++);
            }
            for (Thread t : threads) t.join();
        }
    }

    private MCTSNodeDeprecated traverse(MCTSNodeDeprecated node) {
        while (!node.children.isEmpty()) {
            node = bestNode(node);
            // System.out.println("Node " + node.move);
        }

        return node;
    }

    private MCTSNodeDeprecated bestNode(MCTSNodeDeprecated node) {
        MCTSNodeDeprecated next = null;
        double nextUCT = 0;
        System.out.println(root.model.getHalfPly() + " is the half ply");
        for (Move key : node.children.keySet()) {
            if (next == null) {
                next = node.children.get(key);
                nextUCT = nodeUCT(next);
            }
            else {
                MCTSNodeDeprecated target = node.children.get(key);
                double targetUCT = nodeUCT(target);
                // System.out.println(target.rootProp + "\t\t" + target.move);
                if (node.model.getHalfPly() % 2 == 1 && Double.compare(nodeUCT(target), nextUCT) > 0) {
                    next = target;
                    nextUCT = targetUCT;
                }
                else if (node.model.getHalfPly() % 2 == 0 && Double.compare(nodeUCT(target), nextUCT) < 0) {
                    next = target;
                    nextUCT = targetUCT;
                }
            }
        }
        System.out.println("Best node:\t" + next.rootProp + "\t\t" + next.move);
        System.out.println("Parent info:\t" + next.parent.rootProp);
        return next;
    }

    @Deprecated
    private MCTSNodeDeprecated bestNode(MCTSNodeDeprecated node, boolean debug) {
        MCTSNodeDeprecated next = null;
        if (debug) System.out.println(root.model.getHalfPly() + " is the half ply");
        for (Move key : node.children.keySet()) {
            if (next == null) next = node.children.get(key);
            else {
                MCTSNodeDeprecated target = node.children.get(key);
                if (debug) System.out.println(target.rootProp + "\t\t" + target.move);
                if (node.model.getHalfPly() % 2 == 1 && target.rootProp.compareTo(next.rootProp) > 0) next = target;
                else if (node.model.getHalfPly() % 2 == 0 && target.rootProp.compareTo(next.rootProp) < 0) next = target;
            }
        }
        if (debug) System.out.printf("%.2f\t\t%s\n",((double)(next.rootProp.numerator + next.rootProp.denominator)) / (2.0 * (double) next.rootProp.denominator), next.move);
        return next;
    }

    private MCTSNodeDeprecated finalChoice(MCTSNodeDeprecated node) {
        MCTSNodeDeprecated next = null;
        if (DEBUG) System.out.println(root.model.getHalfPly() + " is the half ply");
        for (Move key : node.children.keySet()) {
            if (next == null) next = node.children.get(key);
            else {
                MCTSNodeDeprecated target = node.children.get(key);
                if (DEBUG) System.out.println(target.rootProp + "\t\t" + target.move);
                if (target.rootProp.denominator > next.rootProp.denominator) next = target;
                else if (target.rootProp.denominator == next.rootProp.denominator) {
                    if (node.model.getHalfPly() % 2 == 1) next = target.rootProp.numerator > next.rootProp.numerator ? target : next;
                    else next = target.rootProp.numerator < next.rootProp.numerator ? target : next;
                }
            }
        }
        if (DEBUG) System.out.printf("%.2f\t\t%s\n",
                ((double)(next.rootProp.numerator + next.rootProp.denominator)) /
                        (2.0 * (double) next.rootProp.denominator),
                next.move);
        return next;
    }

    private double nodeUCT(MCTSNodeDeprecated node) {
        // UCT = wi / ni + C_CONST sqrt(ln Ni / ni)
        double wi = node.rootProp.numerator;
        double ni = node.rootProp.denominator;
        double Ni = node.parent.rootProp.denominator;
        double s = node.rootProp.numerator < 0 ? -1.0 : 1.0;

        return (wi / ni) + s * C_CONST * Math.sqrt(Math.log(Ni) / ni);
    }

    private void generateNodes(MCTSNodeDeprecated node) throws InterruptedException {
        int size  = node.model.getBoardSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (node.model.isValidMove(i, j)) {
                    node.addNode(new MoveImpl(i, j));
                }
            }
        }
        Thread[] threads = new Thread[node.children.size()];
        int i = 0;
        for (MCTSNodeDeprecated child : node.children.values()) {
            Runnable r = child::rollout;
            threads[i] =  new Thread(r);
            threads[i].start();
            i++;
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    @Override
    public String toString() {
        return "MCTSv1.9";
    }
}
@Deprecated
class MCTSNodeDeprecated {
    public MCTSNodeDeprecated parent;
    public Map<Move, MCTSNodeDeprecated> children;
    public Proportion rootProp;
    public Move move;
    public Model model;

    public static MCTSNodeDeprecated createNewWhiteCPU(int size) {
        return new MCTSNodeDeprecated(size);
    }

    public static MCTSNodeDeprecated createNewBlackCPU(int size, Move whiteMove) {
        return new MCTSNodeDeprecated(size, whiteMove);
    }
    public static MCTSNodeDeprecated createNewNode(int halfPly, Board board, int whiteCaptures, int blackCaptures, Move oppMove) {
        MCTSNodeDeprecated res = new MCTSNodeDeprecated();
        res.rootProp = new Proportion();
        res.move = oppMove;
        res.model = new ModelImpl(board.copy(), whiteCaptures, blackCaptures, halfPly);
        res.parent = null;
        return res;
    }
    private MCTSNodeDeprecated() {
        children = new ConcurrentHashMap<>();
    }
    private MCTSNodeDeprecated(int size) { // CPU game as white
        parent = null;
        children = new ConcurrentHashMap<>();
        rootProp = new Proportion();
        move = null;
        model = new ModelImpl(size);
    }

    private MCTSNodeDeprecated(int size, Move oppMove) { // CPU game as black
        parent = null;
        children = new ConcurrentHashMap<>();
        rootProp = new Proportion();
        move = oppMove;
        model = new ModelImpl(size);
        model.move(oppMove.getRowCoord(), oppMove.getColumnCoord());
    }

    public void addNode(Move move) {
        MCTSNodeDeprecated child = new MCTSNodeDeprecated();
        child.parent = this;
        child.move = move;
        child.model = new ModelImpl(
                this.model.getBoard().copy(),
                this.model.getWhitePlayerCaptures(),
                this.model.getBlackPlayerCaptures(),
                this.model.getHalfPly()
        );
        child.model.move(move.getRowCoord(), move.getColumnCoord());
        child.rootProp = new Proportion();
        this.children.put(move, child);
    }

    public void rollout() {
        RandomGame game = new RandomGame(
                this.model.getHalfPly(),
                this.model.getBoard(),
                this.model.getWhitePlayerCaptures(),
                this.model.getBlackPlayerCaptures(),
                this.move,
                1
        );
        game.run();
        this.rootProp.denominator += 1;
        if (this.model.getHalfPly() % 2 == 0 && game.score > 0) this.rootProp.numerator = 1;
        else if (this.model.getHalfPly() % 2 == 1 && game.score < 0) this.rootProp.numerator = 1;
        backpropagate();
    }

    /**
     * thread safe update during backpropagation
     * @param delta
     */
    public synchronized void updateProp(Proportion delta) {
        // this.rootProp.numerator += delta.numerator;
        this.rootProp.denominator += delta.denominator;
        if (parent != null) parent.updateProp(delta);
    }

    /**
     * Start backpropagating the delta in values up the node tree
     */
    public void backpropagate() {
        parent.updateProp(this.rootProp);
    }

    public int getDepth() {
        if (this.children.isEmpty()) return 1;
        else return this.children.values().iterator().next().getDepth() + 1;
    }

    public Proportion getRootProp() {
        return this.rootProp;
    }

}

