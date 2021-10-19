package groupAD.testsuite;


import groupAD.players.PlayerConfig;
import groupAD.players.Players;
import players.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestSuite {

    public static void main(String[] args) {
        long gameSeed = System.currentTimeMillis();
        // Game parameters
        long[] seeds = new long[]{93988, 19067, 64416, 83884, 55636, 27599, 44350, 87872, 40815, 11772};
        List<String> playerConfigIds = getFlags(args, "player_config_ids");
        List<String> experimentConfigIds = getFlags(args, "experiment_config_ids");
        System.out.printf("Player Config Ids: %s%n", playerConfigIds);
        System.out.printf("Experiment Config Ids: %s%n", experimentConfigIds);

        Players playersHelper = new Players(gameSeed);
        List<PlayerConfig> playerConfigsToTest;
        if (!playerConfigIds.isEmpty()) {
            playerConfigsToTest = playersHelper.getPlayerConfigsByIds(playerConfigIds);
        } else {
            playerConfigsToTest = playersHelper.getAllPlayerConfigs();
        }

        Experiments experimentsHelper = new Experiments();
        List<ExperimentConfig> experiments;
        if (!experimentConfigIds.isEmpty()) {
            experiments = experimentsHelper.getExperimentsByIds(experimentConfigIds);
        } else {
            experiments = experimentsHelper.getAllExperiments();
        }


        for (ExperimentConfig experiment : experiments) {
            for (PlayerConfig playerConfig : playerConfigsToTest) {
                String message = String.format(
                        "Running experiment: (%s) with player config: (%s)",
                        experiment.getTitle(),
                        playerConfig.getTitle());
                System.out.println(message);

                experiment.reset()
                        .setControlPlayers(playersHelper.buildDefaultControlPlayers())
                        .setPlayerUnderTest(playerConfig.buildPlayer())
                        .setGameSeed(gameSeed)
                        .run(seeds);
            }
        }
    }

    public static List<String> getFlags(String[] args, String flagKey) {
        String key = String.format("--%s=", flagKey);

        return Arrays.stream(Arrays.stream(args)
                        .map(String::trim)
                        .filter(arg -> arg.startsWith(key))
                        .findFirst()
                        .map(arg -> arg.split("=")[1])
                        .orElse("")
                        .split(","))
                .filter(arg -> !arg.isEmpty())
                .collect(Collectors.toList());
    }
}
