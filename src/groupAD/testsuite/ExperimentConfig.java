package groupAD.testsuite;

import core.Game;
import players.Player;
import utils.Types;

import java.util.ArrayList;
import java.util.List;

public class ExperimentConfig {
    private final String title;
    private final Types.GAME_MODE gameMode;
    private final int visionRange;
    private Player playerUnderTest;
    private List<Player> controlPlayers;
    private long gameSeed;

    public ExperimentConfig(
            String title,
            Types.GAME_MODE gameMode,
            int visionRange) {
        this.title = title;
        this.gameMode = gameMode;
        this.visionRange = visionRange;
    }

    public String getTitle() {
        return  this.title;
    }

    public ExperimentConfig setPlayerUnderTest(Player player) {
        this.playerUnderTest = player;
        return this;
    }

    public ExperimentConfig setControlPlayers(List<Player> players) {
        this.controlPlayers = players;
        return this;
    }

    public ExperimentConfig setGameSeed(long gameSeed) {
        this.gameSeed = gameSeed;
        return this;
    }

    public void run(long[] seeds) {
        Game game = buildGame();
        ArrayList<Player> players = buildPlayers();
        game.setPlayers(players);
        List<RunResult> results = Run.runGames(game, seeds, 5, false);
        RunResult testPlayerResult = results.stream()
                .filter(result -> result.getPlayerId() == playerUnderTest.getPlayerID())
                .findFirst()
                .get();
        // Print results
        String resultString = testPlayerResult.asString();
        System.out.println(resultString);
    }

    private Game buildGame() {
        Types.DEFAULT_VISION_RANGE = visionRange;
        return new Game(gameSeed, Types.BOARD_SIZE, gameMode, title);
    }

    private ArrayList<Player> buildPlayers() {
        ArrayList<Player> players = new ArrayList<>(controlPlayers);
        players.add(playerUnderTest);

        // Make sure we have exactly NUM_PLAYERS players
        assert players.size() == Types.NUM_PLAYERS : "There should be " + Types.NUM_PLAYERS +
                " added to the game, but there are " + players.size();
        return players;
    }
}
