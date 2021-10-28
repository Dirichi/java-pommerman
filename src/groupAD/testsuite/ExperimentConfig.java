package groupAD.testsuite;

import core.Game;
import groupAD.players.PlayerConfig;
import players.Player;
import utils.Types;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        return this;
    }

    public ExperimentConfig setControlPlayerConfigs(List<PlayerConfig> players) {
        this.controlPlayerConfigs = players;
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

    public String run(long[] seeds, int repetitions) {
        RunResult result = null;
        for (int i = 0; i < repetitions; i++) {
            RunResult currentResult = runOneRep(seeds, i);
            result = currentResult.combine(result);
        }
        // Print results
        String resultString = result.asString();
        String experimentSummary = String.format(
                "Experiment: (%s), Player: (%s), results: %s",
                title,
                playerConfig.getTitle(),
                resultString);
        System.out.println(experimentSummary);
        return experimentSummary;
    }

    private RunResult runOneRep(long[] seeds, int repetitionIndex) {
        Game game = buildGame();
        int testPlayerIndex = repetitionIndex % Types.NUM_PLAYERS;
        ArrayList<Player> players = buildPlayers(testPlayerIndex);
        game.setPlayers(players);
        List<RunResult> results = Run.runGames(game, seeds, 1, false, false);
        return results
                .stream()
                .filter(result -> result.getPlayerId() == playerConfig.getPlayerId())
                .findFirst()
                .get();
    }

    private Game buildGame() {
        Types.DEFAULT_VISION_RANGE = visionRange;
        return new Game(gameSeed, Types.BOARD_SIZE, gameMode, title);
    }

    private ArrayList<Player> buildPlayers(int testPlayerIndex) {
        int startId = Types.TILETYPE.AGENT0.getKey();
        int endId = Types.TILETYPE.AGENT0.getKey() + Types.NUM_PLAYERS;
        int testPlayerId = startId + testPlayerIndex;
        List<Integer> controlPlayerIds = IntStream
                .range(startId, endId)
                .filter(id -> id != testPlayerId)
                .boxed()
                .collect(Collectors.toList());
        playerConfig.reset().setSeed(gameSeed).setPlayerId(testPlayerId);

        for (PlayerConfig config: controlPlayerConfigs) {
            int playerId = controlPlayerIds.remove(0);
            config.reset().setSeed(gameSeed).setPlayerId(playerId);
        }

        ArrayList<PlayerConfig> playerConfigs = new ArrayList<>(controlPlayerConfigs);
        playerConfigs.add(playerConfig);
        // Sort players by id because external code assumes that
        // the Players are sorted by id
        playerConfigs.sort(Comparator.comparing(PlayerConfig::getPlayerId));
        List<Player> players = playerConfigs
                .stream()
                .map(PlayerConfig::buildPlayer)
                .collect(Collectors.toList());

        // Make sure we have exactly NUM_PLAYERS players
        assert players.size() == Types.NUM_PLAYERS : "There should be " + Types.NUM_PLAYERS +
                " added to the game, but there are " + players.size();
        return new ArrayList(players);
    }
}
