package AD;

import core.GameState;
import players.Player;
import utils.Types;

public class DummyPlayer extends Player {
    public DummyPlayer(int pId) {
        super(0, pId);
    }

    @Override
    public Types.ACTIONS act(GameState gs) {
        return Types.ACTIONS.ACTION_STOP;
    }

    @Override
    public int[] getMessage() {
        // default message
        return new int[Types.MESSAGE_LENGTH];
    }

    @Override
    public Player copy() {
        return new DummyPlayer(playerID);
    }
}