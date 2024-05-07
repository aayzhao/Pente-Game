package aayzhao.pente.computer.mcts.tree;

import aayzhao.pente.computer.Move;
import aayzhao.pente.computer.MoveImpl;
import aayzhao.pente.computer.PenteComputer;
import aayzhao.pente.computer.mcts.RandomGame;
import aayzhao.pente.game.model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MCTSComputer implements PenteComputer {
    private MCTSNode root;
    private int size;
    private boolean firstMove;

    public MCTSComputer(int size) {
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
            root = MCTSNode.createNewNode(halfPly, board, whiteCaptures, blackCaptures, oppMove);
            // generate first layer of nodes
            generateNodes(root);

            // generate second layer of nodes
            Thread[] threads = new Thread[root.children.size()];
            int i = 0;
            for (MCTSNode child : root.children.values()) {
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

        root = bestNode(root, true); // make the move
        root.parent = null; // remove tree from parent
        return this.root.move;
    }

    @Override
    public Move bestMove(int halfPly, Move prevMove) throws InterruptedException {
        MCTSNode prev = root;
        if (root != null) root = root.children.get(prevMove); // move to correct node for opponent move
        if (root == null) {
            // generate first layer of nodes
            if (prevMove == null) {
                root = MCTSNode.createNewWhiteCPU(size);
            } else {
                root = MCTSNode.createNewBlackCPU(size, prevMove);
            }
            generateNodes(root);

            // generate second layer of nodes
            Thread[] threads = new Thread[root.children.size()];
            int i = 0;
            for (MCTSNode child : root.children.values()) {
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
        // selection and queue up the upper 40% of nodes
        Comparator<MCTSNode> comp;
        if (this.root.model.getHalfPly() % 2 == 1) comp = (a, b) -> b.rootProp.compareTo(a.rootProp);
        else comp = Comparator.comparing((a) -> a.rootProp);

        // repeat while processing time allows
        long startTime = System.nanoTime();
        long timeForExpansion = 2500000000L;
        // int j = 0;
        while (System.nanoTime() - startTime < timeForExpansion) {
            PriorityQueue<MCTSNode> queue = new PriorityQueue<>(82, comp);
            queue.addAll(root.children.values());

            int searchThreshold = (int) Math.min(Math.ceil(((double) root.children.size()) * 0.75), 1);
            Thread[] threads = new Thread[searchThreshold];
            for (int i = 0; i < searchThreshold; i++) {
                final MCTSNode[] node = {queue.poll()};
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
            PriorityQueue<MCTSNode> queue = new PriorityQueue<>(82, comp);
            queue.addAll(root.children.values());

            int searchThreshold = (int) Math.min(Math.ceil(((double) root.children.size()) * 0.75), 1);
            Thread[] threads = new Thread[searchThreshold];
            for (int i = 0; i < searchThreshold; i++) {
                final MCTSNode[] node = {queue.poll()};
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
            PriorityQueue<MCTSNode> queue = new PriorityQueue<>(82, comp);
            queue.addAll(root.children.values());

            int searchThreshold = (int) Math.min(Math.ceil(((double) root.children.size()) * 0.375), 1);
            Thread[] threads = new Thread[searchThreshold];
            for (int i = 0; i < searchThreshold; i++) {
                final MCTSNode[] node = {queue.poll()};
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
            PriorityQueue<MCTSNode> queue = new PriorityQueue<>(82, comp);
            queue.addAll(root.children.values());

            int searchThreshold = (int) Math.min(Math.ceil(((double) root.children.size()) * 0.375), 1);
            Thread[] threads = new Thread[searchThreshold];
            for (int i = 0; i < searchThreshold; i++) {
                final MCTSNode[] node = {queue.poll()};
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

    private MCTSNode traverse(MCTSNode node) {
        while (!node.children.isEmpty()) {
            node = bestNode(node);
            // System.out.println("Node " + node.move);
        }

        return node;
    }

    private MCTSNode bestNode(MCTSNode node) {
        MCTSNode next = null;
        // System.out.println(root.model.getHalfPly() + " is the half ply");
        for (Move key : node.children.keySet()) {
            if (next == null) next = node.children.get(key);
            else {
                MCTSNode target = node.children.get(key);
                // System.out.println(target.rootProp + "\t\t" + target.move);
                if (node.model.getHalfPly() % 2 == 1 && target.rootProp.compareTo(next.rootProp) > 0) next = target;
                else if (node.model.getHalfPly() % 2 == 0 && target.rootProp.compareTo(next.rootProp) < 0) next = target;
            }
        }
        // System.out.println(next.rootProp + "\t\t" + next.move);
        return next;
    }

    private MCTSNode bestNode(MCTSNode node, boolean DEBUG) {
        MCTSNode next = null;
        if (DEBUG) System.out.println(root.model.getHalfPly() + " is the half ply");
        for (Move key : node.children.keySet()) {
            if (next == null) next = node.children.get(key);
            else {
                MCTSNode target = node.children.get(key);
                if (DEBUG) System.out.println(target.rootProp + "\t\t" + target.move);
                if (node.model.getHalfPly() % 2 == 1 && target.rootProp.compareTo(next.rootProp) > 0) next = target;
                else if (node.model.getHalfPly() % 2 == 0 && target.rootProp.compareTo(next.rootProp) < 0) next = target;
            }
        }
        if (DEBUG) System.out.printf("%.2f\t\t%s\n",((double)(next.rootProp.numerator + next.rootProp.denominator)) / (2.0 * (double) next.rootProp.denominator), next.move);
        return next;
    }

    private void generateNodes(MCTSNode node) throws InterruptedException {
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
        for (MCTSNode child : node.children.values()) {
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

class MCTSNode {
    public MCTSNode parent;
    public Map<Move, MCTSNode> children;
    public Proportion rootProp;
    public Move move;
    public Model model;

    public static MCTSNode createNewWhiteCPU(int size) {
        return new MCTSNode(size);
    }

    public static MCTSNode createNewBlackCPU(int size, Move whiteMove) {
        return new MCTSNode(size, whiteMove);
    }
    public static MCTSNode createNewNode(int halfPly, Board board, int whiteCaptures, int blackCaptures, Move oppMove) {
        MCTSNode res = new MCTSNode();
        res.rootProp = new Proportion();
        res.move = oppMove;
        res.model = new ModelImpl(board.copy(), whiteCaptures, blackCaptures, halfPly);
        res.parent = null;
        return res;
    }
    private MCTSNode() {
        children = new ConcurrentHashMap<>();
    }
    private MCTSNode(int size) { // CPU game as white
        parent = null;
        children = new ConcurrentHashMap<>();
        rootProp = new Proportion();
        move = null;
        model = new ModelImpl(size);
    }

    private MCTSNode(int size, Move oppMove) { // CPU game as black
        parent = null;
        children = new ConcurrentHashMap<>();
        rootProp = new Proportion();
        move = oppMove;
        model = new ModelImpl(size);
        model.move(oppMove.getRowCoord(), oppMove.getColumnCoord());
    }

    public void addNode(Move move) {
        MCTSNode child = new MCTSNode();
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
                10
        );
        game.run();
        this.rootProp.denominator += 10;
        this.rootProp.numerator += game.score;
        backpropagate();
    }

    /**
     * thread safe update during backpropagation
     * @param delta
     */
    public synchronized void updateProp(Proportion delta) {
        this.rootProp.numerator += delta.numerator;
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

