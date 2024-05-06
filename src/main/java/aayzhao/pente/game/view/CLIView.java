package aayzhao.pente.game.view;

import aayzhao.pente.game.model.Board;
import aayzhao.pente.game.model.Model;
import aayzhao.pente.game.model.ModelObserver;
import aayzhao.pente.game.model.PieceType;

public class CLIView implements ModelObserver {
    public CLIView(Model model) {
        model.subscribe(this);
    }
    @Override
    public void update(Model model) {
        System.out.printf("\n____________\nPly: %.1f\n", ((double) model.getHalfPly() / 2));
        System.out.printf("White Captures: %d\tBlack Captures: %d\n", model.getWhitePlayerCaptures(), model.getBlackPlayerCaptures());
        String playerMove = model.getHalfPly() % 2 == 1 ? "White to move: " : "Black to move: ";
        System.out.println(playerMove);
        for (int i = model.getBoardSize() - 1; i >= 0; i--) {
            System.out.printf("%c ", 'a' + i);
            for (int j = 0; j < model.getBoardSize(); j++) {
                String ch = PieceType.pieceToInt(model.getIntersection(i, j)) == 0 ? ".  " : (PieceType.pieceToInt(model.getIntersection(i, j)) == 1 ? "O  " : "X  ");
                System.out.print(ch);
            }
            System.out.println();
        }
        System.out.print("  ");
        for (int i = 1; i < 10; i++) {
            System.out.printf("%d  ", i);
        }
//        for (int i = 10; i < 20; i++) {
//            System.out.printf("%d ", i);
//        }
        System.out.println("\n");
    }
}
