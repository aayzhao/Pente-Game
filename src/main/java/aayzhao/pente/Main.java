package aayzhao.pente;

import aayzhao.pente.computer.Move;
import aayzhao.pente.computer.MoveImpl;
import aayzhao.pente.computer.PenteComputer;
import aayzhao.pente.computer.mcts.naive.FastMCTSComputer;
import aayzhao.pente.computer.mcts.naive.FullRolloutMCTSComputer;
import aayzhao.pente.computer.mcts.naive.StagedMCTSComputer;
import aayzhao.pente.computer.mcts.tree.MCTSComputer;
import aayzhao.pente.computer.mcts.tree.MCTSComputerDeprecated;
import aayzhao.pente.game.model.Model;
import aayzhao.pente.game.model.ModelImpl;
import aayzhao.pente.game.model.PieceType;
import aayzhao.pente.game.view.CLIView;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Naive implementation of MTCS that does successively more rollouts for the better moves
 */
public class Main {
    public static boolean player1CPU = true;
    final static Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
    public static void main(String[] args) throws InterruptedException {
        Model model = new ModelImpl(9);
        CLIView view = new CLIView(model);
        PenteComputer cpu = new MCTSComputer(model.getBoardSize());
        PenteComputer cpu19 = new MCTSComputerDeprecated(model.getBoardSize());
        PenteComputer cpu14 = new StagedMCTSComputer();
        PenteComputer cpu11 = new FullRolloutMCTSComputer();
        PenteComputer cpu05 = new FastMCTSComputer();
        System.out.println("Board:");
        model.update();

        Scanner scan = new Scanner(System.in);
        Pattern pattern = Pattern.compile("^[a-z|Ae4-Z][0-9]+$");
        String input = "";
        if (!player1CPU) {
            System.out.println("Would you like to play white (w) or black (b)? ");
            input = scan.next("[a-z]*");


            if (input.charAt(0) == 'b') {
                System.out.println("Playing as Black");
                Move firstCPUMove = cpu.bestMove(1, model.getBoard(), 0, 0, null);
                model.move(firstCPUMove.getRowCoord(), firstCPUMove.getColumnCoord());
            }
        }
        while (!input.equals("quit") && !model.isWon()) {
            if (!player1CPU) {
                if (input.charAt(0) == 'w') System.out.println("Playing as White");
                input = scan.next();
            }
            if (!pattern.matcher(input).find() && !player1CPU) {
                continue;
            } else {
                Move cpu1Move;
                if (!player1CPU) {
                    int r = input.charAt(0) - 'a';
                    int c = -1;
                    Matcher matcher = lastIntPattern.matcher(input);
                    if (matcher.find()) {
                        String someNumberStr = matcher.group(1);
                        int lastNumberInt = Integer.parseInt(someNumberStr);
                        c = lastNumberInt - 1;
                    }
                    cpu1Move = new MoveImpl(r, c);
                    model.move(r, c);
                } else {
                    cpu1Move = cpu11.bestMove(
                            model.getHalfPly(),
                            model.getBoard(),
                            model.getWhitePlayerCaptures(),
                            model.getBlackPlayerCaptures(),
                            null
                    );
                    System.out.println(cpu11);
                    model.move(cpu1Move.getRowCoord(), cpu1Move.getColumnCoord());
                }
                if (model.isWon()) break;
                Move cpu2Move = cpu.bestMove(
                        model.getHalfPly(),
                        model.getBoard(),
                        model.getWhitePlayerCaptures(),
                        model.getBlackPlayerCaptures(),
                        cpu1Move
                );
                System.out.println(cpu);
                model.move(cpu2Move.getRowCoord(), cpu2Move.getColumnCoord());
            }
        }
        String player = model.getWinner() == PieceType.WHITE ? "White" : "Black";
        System.out.println(player + " wins");
//        if (cpu instanceof MCTSComputer) {
//            ((MCTSComputer) cpu).shutdown();
//        }
    }
}