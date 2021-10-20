package groupAD.players;

import players.SimpleEvoAgent;
import players.mcts.MCTSParams;
import players.mcts.MCTSPlayer;
import players.rhea.RHEAPlayer;
import players.rhea.utils.Constants;
import players.rhea.utils.RHEAParams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Players {
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
        return  ids.stream()
                .map(id -> PLAYER_CONFIG_MAP.get(id))
                .collect(Collectors.toList());
    }

    public List<PlayerConfig> getAllPlayerConfigs() {
        return  getPlayerConfigsByIds(
                PLAYER_CONFIG_MAP.keySet().stream().collect(Collectors.toList())
        );
    }

    public List<PlayerConfig> getDefaultControlPlayerConfigs() {
        MCTSParams mctsParams = new MCTSParams();
        mctsParams.stop_type = mctsParams.STOP_ITERATIONS;
        PlayerConfig<MCTSPlayer> controlMCTS = new PlayerConfig(
                "Control MCTS",
                MCTSPlayer.class,
                mctsParams,
                Map.of("heuristic_method", mctsParams.CUSTOM_HEURISTIC)
        );

        PlayerConfig<RHEAPlayer> controlRhea = new PlayerConfig(
                "Control RHEA",
                RHEAPlayer.class,
                new RHEAParams(),
                Map.of("heuristic_type", Constants.CUSTOM_HEURISTIC)
        );

        PlayerConfig<SimpleEvoAgent> controlSimpleEvo = new PlayerConfig(
                "Control Simple Evo",
                SimpleEvoAgent.class
        );

        return List.of(controlMCTS, controlRhea, controlSimpleEvo);
    }
}
