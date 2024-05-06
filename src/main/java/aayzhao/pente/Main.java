package aayzhao.pente;

import aayzhao.pente.game.model.Model;
import aayzhao.pente.game.model.ModelImpl;
import aayzhao.pente.game.model.PieceType;
import aayzhao.pente.game.view.CLIView;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    final static Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
    public static void main(String[] args) {
        Model model = new ModelImpl(9);
        CLIView view = new CLIView(model);
        System.out.println("Board:");
        model.update();

        Scanner scan = new Scanner(System.in);
        Pattern pattern = Pattern.compile("[a-z|A-Z][0-9]+");

        String input = "";
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
            }
        }
        String player = model.getWinner() == PieceType.WHITE ? "White" : "Black";
        System.out.println(player + " wins");
    }
}