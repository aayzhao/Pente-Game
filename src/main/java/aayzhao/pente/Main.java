package aayzhao.pente;

import aayzhao.pente.computer.FullRolloutMCTSComputer;
import aayzhao.pente.computer.Move;
import aayzhao.pente.computer.PenteComputer;
import aayzhao.pente.game.model.Model;
import aayzhao.pente.game.model.ModelImpl;
import aayzhao.pente.game.model.PieceType;
import aayzhao.pente.game.view.CLIView;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    final static Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
    public static void main(String[] args) throws InterruptedException {
        Model model = new ModelImpl(9);
        CLIView view = new CLIView(model);
        PenteComputer cpu = new FullRolloutMCTSComputer();
        System.out.println("Board:");
        model.update();

        Scanner scan = new Scanner(System.in);
        Pattern pattern = Pattern.compile("^[a-z|Ae4-Z][0-9]+$");
        String input = "";
        System.out.println("Would you like to play white (w) or black (b)? ");
        input = scan.next("[a-z]*");

        if (input.charAt(0) == 'b') {
            Move firstCPUMove = cpu.bestMove(1, model.getBoard(), 0, 0);
            model.move(firstCPUMove.getRowCoord(), firstCPUMove.getColumnCoord());
        }

        while (!input.equals("quit") && !model.isWon()) {
            input = scan.next();
            if (!pattern.matcher(input).find()) {
                continue;
            } else {
                int r = input.charAt(0) - 'a';
                int c = -1;
                Matcher matcher = lastIntPattern.matcher(input);
                if (matcher.find()) {
                    String someNumberStr = matcher.group(1);
                    int lastNumberInt = Integer.parseInt(someNumberStr);
                    c = lastNumberInt - 1;
                }
                model.move(r, c);

                Move cpuMove = cpu.bestMove(
                        model.getHalfPly(),
                        model.getBoard(),
                        model.getWhitePlayerCaptures(),
                        model.getBlackPlayerCaptures());
                model.move(cpuMove.getRowCoord(), cpuMove.getColumnCoord());
            }
        }
        String player = model.getWinner() == PieceType.WHITE ? "White" : "Black";
        System.out.println(player + " wins");

//        MCTSComputer.RandomGame randomGame = new MCTSComputer.RandomGame(0, new BoardImpl(9), 0, 0, new MoveImpl(4, 4));
//        Thread thread = new Thread(randomGame);
//        thread.start();
//        thread.join();
//        System.out.println(randomGame.score);
    }
}