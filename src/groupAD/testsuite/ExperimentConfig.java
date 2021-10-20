package groupAD.testsuite;

import core.Game;
import groupAD.players.PlayerConfig;
import players.Player;
import utils.Types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExperimentConfig {
    private final String title;
    private final Types.GAME_MODE gameMode;
    private final int visionRange;
    private PlayerConfig playerConfig;
    private List<PlayerConfig> controlPlayerConfigs;
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
        return this.title;
    }

    public ExperimentConfig setPlayerConfig(PlayerConfig config) {
        this.playerConfig = config;
        playerConfig.reset();
        return this;
    }

    public ExperimentConfig setControlPlayerConfigs(List<PlayerConfig> players) {
        this.controlPlayerConfigs = players;
        for (PlayerConfig config: controlPlayerConfigs) {
            config.reset();
        }
        return this;
    }

    public ExperimentConfig setGameSeed(long gameSeed) {
        this.gameSeed = gameSeed;
        return this;
    }

    public ExperimentConfig reset() {
        this.playerConfig = null;
        this.controlPlayerConfigs = null;
        this.gameSeed = 0;
        return this;
    }

    public String run(long[] seeds) {
        Game game = buildGame();
        ArrayList<Player> players = buildPlayers();
        game.setPlayers(players);
        List<RunResult> results = Run.runGames(game, seeds, 5, false, true);
        RunResult playerResult = results.stream()
                .filter(result -> result.getPlayerId() == playerConfig.getPlayerId())
                .findFirst()
                .get();
        // Print results
        String resultString = playerResult.asString();
        String experimentSummary = String.format(
                "Experiment: (%s), Player: (%s), results: %s",
                title,
                playerConfig.getTitle(),
                resultString);
        System.out.println(experimentSummary);
        return experimentSummary;
    }

    private Game buildGame() {
        Types.DEFAULT_VISION_RANGE = visionRange;
        return new Game(gameSeed, Types.BOARD_SIZE, gameMode, title);
    }

    private ArrayList<Player> buildPlayers() {
        ArrayList<PlayerConfig> playerConfigs = new ArrayList<>(controlPlayerConfigs);
        playerConfigs.add(playerConfig);
        // Shuffle positions to make sure that the tested player
        // does not learn to play only from one position in the
        // game.
        Collections.shuffle(playerConfigs);
        for (int i = 0; i < playerConfigs.size(); i++) {
            int playerID = Types.TILETYPE.AGENT0.getKey() + i;
            playerConfigs.get(i).setSeed(gameSeed).setPlayerId(playerID);
        }
        List<Player> players = playerConfigs.stream().map(config -> config.buildPlayer()).collect(Collectors.toList());
        // Make sure we have exactly NUM_PLAYERS players
        assert players.size() == Types.NUM_PLAYERS : "There should be " + Types.NUM_PLAYERS +
                " added to the game, but there are " + players.size();
        return new ArrayList(players);
    }
}
