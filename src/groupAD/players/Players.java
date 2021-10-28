package groupAD.players;

import players.mcts.MCTSParams;
import players.mcts.MCTSPlayer;
import players.rhea.RHEAPlayer;
import players.rhea.utils.RHEAParams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Players {
    private static final PlayerConfig DEFAULT_MCTS = new PlayerConfig(
            "Default MCTS",
            MCTSPlayer.class,
            new MCTSParams());
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
    private static final PlayerConfig DEFAULT_RHEA = new PlayerConfig(
            "Default RHEA",
            RHEAPlayer.class,
            new RHEAParams());
    private static final PlayerConfig RHEA_1_MC_REPEATS = new PlayerConfig(
            "RHEA (mc_rollouts = true, mc_rollouts_repeat = 1)",
            RHEAPlayer.class,
            new RHEAParams(),
            Map.of(
                    "mc_rollouts", true,
                    "mc_rollouts_repeat", 1
            )
    );
    private static final PlayerConfig RHEA_3_MC_REPEATS = new PlayerConfig(
            "RHEA (mc_rollouts = true, mc_rollouts_repeat = 3)",
            RHEAPlayer.class,
            new RHEAParams(),
            Map.of(
                    "mc_rollouts", true,
                    "mc_rollouts_repeat", 3
            )
    );
    private static final PlayerConfig RHEA_5_MC_REPEATS = new PlayerConfig(
            "RHEA (mc_rollouts = true, mc_rollouts_repeat = 5)",
            RHEAPlayer.class,
            new RHEAParams(),
            Map.of(
                    "mc_rollouts", true,
                    "mc_rollouts_repeat", 5
            )
    );
    private static final PlayerConfig RHEA_ELITISM_5_POPSIZE = new PlayerConfig(
            "RHEA (elitism = true, population_size = 5)",
            RHEAPlayer.class,
            new RHEAParams(),
            Map.of(
                    "elitism", false,
                    "population_size", 5
            )
    );
    private static final PlayerConfig RHEA_NO_ELITISM_5_POPSIZE = new PlayerConfig(
            "RHEA (elitism = false, population_size = 5)",
            RHEAPlayer.class,
            new RHEAParams(),
            Map.of(
                    "elitism", false,
                    "population_size", 5
            )
    );

    /** Mapping of player_config_id to Player Config.
     * Used in determining what player configs to run through the test suite. */
    private static final Map<String, PlayerConfig> PLAYER_CONFIG_MAP = Map.of(
            "1", DEFAULT_MCTS,
            "2", MCTS_SMALL_K,
            "3", MCTS_SMALLER_K,
            "4", MCTS_BIG_K,
            "5", MCTS_BIGGER_K,
//            "6", DEFAULT_RHEA,
            "7", RHEA_1_MC_REPEATS,
            "8", RHEA_3_MC_REPEATS,
            "9", RHEA_5_MC_REPEATS,
            "10", RHEA_NO_ELITISM_5_POPSIZE,
            "11", RHEA_ELITISM_5_POPSIZE
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
        MCTSParams mctsParamsOne = new MCTSParams();
        mctsParamsOne.stop_type = mctsParamsOne.STOP_ITERATIONS;
        PlayerConfig<MCTSPlayer> controlMCTSOne = new PlayerConfig(
                "Control MCTS One",
                MCTSPlayer.class,
                mctsParamsOne,
                Map.of("heuristic_method", mctsParamsOne.CUSTOM_HEURISTIC)
        );

        MCTSParams mctsParamsTwo = new MCTSParams();
        mctsParamsTwo.stop_type = mctsParamsOne.STOP_ITERATIONS;
        PlayerConfig<MCTSPlayer> controlMCTSTwo = new PlayerConfig(
                "Control MCTS Two",
                MCTSPlayer.class,
                mctsParamsTwo,
                Map.of("heuristic_method", mctsParamsTwo.CUSTOM_HEURISTIC)
        );

        MCTSParams mctsParamsThree = new MCTSParams();
        mctsParamsThree.stop_type = mctsParamsThree.STOP_ITERATIONS;
        PlayerConfig<MCTSPlayer> controlMCTSThree = new PlayerConfig(
                "Control MCTS Three",
                MCTSPlayer.class,
                mctsParamsThree,
                Map.of("heuristic_method", mctsParamsThree.CUSTOM_HEURISTIC)
        );


        return List.of(controlMCTSOne, controlMCTSTwo, controlMCTSThree);
    }
}
