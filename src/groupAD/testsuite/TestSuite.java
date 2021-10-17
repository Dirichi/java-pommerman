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
        // long[] seeds = new long[] {93988, 19067, 64416, 83884, 55636, 27599, 44350, 87872, 40815, 11772};
        long[] seeds = new long[]{93988};
        List<String> testPlayerConfigIds = getFlags(args, "playerConfigIds");
        List<String> experimentConfigIds = getFlags(args, "experimentConfigIds");
        System.out.printf("Player Config Ids: %s%n", testPlayerConfigIds);
        System.out.printf("Experiment Config Ids: %s%n", experimentConfigIds);

        Players playersHelper = new Players(gameSeed);
        List<Player> defaultControlPlayers = playersHelper.buildDefaultControlPlayers();

        List<PlayerConfig> playerConfigsToTest;
        if (!testPlayerConfigIds.isEmpty()) {
            playerConfigsToTest = playersHelper.getPlayerConfigsByIds(testPlayerConfigIds);
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
            for (PlayerConfig playerConfig: playerConfigsToTest) {
                String message = String.format(
                        "Running experiment: (%s) with player config: (%s)",
                        experiment.getTitle(),
                        playerConfig.getTitle());
                System.out.println(message);

                experiment.setControlPlayers(defaultControlPlayers)
                        .setPlayerUnderTest(playerConfig.buildPlayer())
                        .setGameSeed(gameSeed)
                        .run(seeds);
            }
        }
    }

    public static List<String> getFlags(String[] args, String flagKey) {
        String key = String.format("--%s=", flagKey);
        return Arrays.stream(Arrays.stream(args)
                .filter(arg -> arg.startsWith(key))
                .findFirst()
                .orElse("=")
                .split("=")[1]
                .split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
