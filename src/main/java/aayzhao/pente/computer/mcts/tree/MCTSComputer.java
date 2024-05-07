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
    private boolean firstMove = true;

    public MCTSComputer(int size) {
        this.size = size;
    }

    @Override
    public double advantage(int halfPly, Board board, int whiteCaptures, int blackCaptures) {
        return 0;
    }

    @Override
    public Move bestMove(int halfPly, Board board, int whiteCaptures, int blackCaptures, Move oppMove) throws InterruptedException {
        return bestMove(oppMove);
    }

    @Override
    public Move bestMove(Move prevMove) throws InterruptedException {
        if (firstMove) {
            firstMove = false;
            // generate first layer of nodes
            if (prevMove == null) {
                root = MCTSNode.createNewWhiteCPU(size);
            } else {
                root = MCTSNode.createNewBlackCPU(size, prevMove);
            }
            generateNodes(root);

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

            MCTSNode next = null;
            System.out.println(root.model.getHalfPly() + " is the half ply");
            for (Move key : root.children.keySet()) {
                if (next == null) next = root.children.get(key);
                else {
                    MCTSNode target = root.children.get(key);
                    System.out.println(target.rootProp + "\t\t" + target.move);
                    if (root.model.getHalfPly() % 2 == 1 && target.rootProp.compareTo(next.rootProp) > 0) next = target;
                    else if (target.rootProp.compareTo(next.rootProp) < 0) next = target;
                }
            }
            root = next;
            System.out.println(next.rootProp + "\t\t" + next.move);
            return next.move;
        }
        else return null;
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
                1
        );
        game.run();
        this.rootProp.denominator++;
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

