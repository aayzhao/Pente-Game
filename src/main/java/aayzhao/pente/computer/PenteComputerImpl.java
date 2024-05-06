package aayzhao.pente.computer;

import aayzhao.pente.game.model.Board;
import aayzhao.pente.game.model.ModelImpl;

public class PenteComputerImpl extends ModelImpl implements PenteComputer  {
    @Override
    public int advantage(int halfPly, Board board) {
        return 0;
    }

    @Override
    public Move bestMove(int halfPly, Board board) {
        return null;
    }
}
