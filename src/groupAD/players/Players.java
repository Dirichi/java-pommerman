package groupAD.players;

import players.Player;
import players.SimpleEvoAgent;
import players.mcts.MCTSParams;
import players.mcts.MCTSPlayer;
import players.rhea.RHEAPlayer;
import players.rhea.utils.Constants;
import players.rhea.utils.RHEAParams;
import utils.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Players {
    private final long seed;

    public Players(long seed) {
        this.seed = seed;
    }

    private static final PlayerConfig DEFAULT_MCTS = new PlayerConfig(
            "Default MCTS",
            MCTSPlayer.class,
            new MCTSParams());
    private static final PlayerConfig DEFAULT_RHEA = new PlayerConfig(
            "Default RHEA",
            RHEAPlayer.class,
            new RHEAParams());
    private static final PlayerConfig MCTS_SMALL_K = new PlayerConfig(
            "MCTS (K = sqrt(2) / 2)",
            MCTSPlayer.class,
            new MCTSParams(),
            Map.of("K", Math.sqrt(2) / 2));
    private static final PlayerConfig MCTS_SMALLER_K = new PlayerConfig(
            "MCTS (K = sqrt(2) / 4)",
            MCTSPlayer.class,
            new MCTSParams(),
            Map.of("K", Math.sqrt(2) / 4));
    private static final PlayerConfig MCTS_BIG_K = new PlayerConfig(
            "MCTS (K = sqrt(2) * 2)",
            MCTSPlayer.class,
            new MCTSParams(),
            Map.of("K", Math.sqrt(2) * 2));
    private static final PlayerConfig MCTS_BIGGER_K = new PlayerConfig(
            "MCTS (K = sqrt(2) * 4)",
            MCTSPlayer.class,
            new MCTSParams(),
            Map.of("K", Math.sqrt(2) * 4));

    /** Mapping of player_config_id to Player Config.
     * Used in determining what player configs to run through the test suite. */
    private static final Map<String, PlayerConfig> PLAYER_CONFIG_MAP = Map.of(
            "1", DEFAULT_MCTS,
            "2", DEFAULT_RHEA,
            "3", MCTS_SMALL_K,
            "4", MCTS_SMALLER_K,
            "5", MCTS_BIG_K,
            "6", MCTS_BIGGER_K
            );

    public List<PlayerConfig> getPlayerConfigsByIds(List<String> ids) {
        // By default, the player under test is the last player
        // TODO: Make this shuffleable
        int playerID = Types.TILETYPE.AGENT0.getKey() + 4;
        return  ids.stream()
                .map(id -> PLAYER_CONFIG_MAP.get(id).setPlayerId(playerID).setSeed(seed))
                .collect(Collectors.toList());
    }

    public List<PlayerConfig> getAllPlayerConfigs() {
        return  getPlayerConfigsByIds(
                PLAYER_CONFIG_MAP.keySet().stream().collect(Collectors.toList())
        );
    }

    public ArrayList<Player> buildDefaultControlPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        int playerID = Types.TILETYPE.AGENT0.getKey();

        MCTSParams mctsParams = new MCTSParams();
        mctsParams.stop_type = mctsParams.STOP_ITERATIONS;
        mctsParams.heuristic_method = mctsParams.CUSTOM_HEURISTIC;

        RHEAParams rheaParams = new RHEAParams();
        rheaParams.heurisic_type = Constants.CUSTOM_HEURISTIC;

        players.add(new MCTSPlayer(seed, playerID++, mctsParams));
        players.add(new RHEAPlayer(seed, playerID++, rheaParams));
        players.add(new SimpleEvoAgent(seed, playerID++));

        return players;
    }
}
