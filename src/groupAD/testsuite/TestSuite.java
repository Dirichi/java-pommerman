package groupAD.testsuite;


import groupAD.players.PlayerConfig;
import groupAD.players.Players;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestSuite {

    public static void main(String[] args) {
        long gameSeed = System.currentTimeMillis();
        // Game parameters
        long[] seeds = new long[]{93988, 19067, 64416, 83884, 55636, 27599, 44350, 87872, 40815, 11772};
//        long[] seeds = new long[]{93988};

        List<String> playerConfigIds = getListFlag(args, "player_config_ids", List.of("1"));
        List<String> experimentConfigIds = getListFlag(args, "experiment_config_ids", List.of("1"));
        int repetitions = getIntFlag(args, "repetitions", 1);
        System.out.printf("Player Config Ids: %s%n", playerConfigIds);
        System.out.printf("Experiment Config Ids: %s%n", experimentConfigIds);
        System.out.printf("Repetitions: %s%n", repetitions);

        Players playersHelper = new Players();
        List<PlayerConfig> playerConfigsToTest = playersHelper.getPlayerConfigsByIds(playerConfigIds);

        Experiments experimentsHelper = new Experiments();
        List<ExperimentConfig> experiments = experimentsHelper.getExperimentsByIds(experimentConfigIds);


        ArrayList<String> experimentResults = new ArrayList();
        for (ExperimentConfig experiment : experiments) {
            for (PlayerConfig playerConfig : playerConfigsToTest) {
                String message = String.format(
                        "Running experiment: (%s) with player config: (%s) with %s seed(s) and %s repetition(s)",
                        experiment.getTitle(),
                        playerConfig.getTitle(),
                        seeds.length,
                        repetitions);
                System.out.println(message);

                String result = experiment.reset()
                        .setControlPlayerConfigs(playersHelper.getDefaultControlPlayerConfigs())
                        .setPlayerConfig(playerConfig)
                        .setGameSeed(gameSeed)
                        .run(seeds, repetitions);
                experimentResults.add(result);
            }
        }

        for (String result: experimentResults) {
            System.out.println(result);
        }
    }

    private static List<String> getListFlag(String[] args, String flagKey, List<String> defaultValue) {
        String rawValue = parseFlag(args, flagKey, "");

        return Arrays.stream(rawValue.split(","))
                .filter(arg -> !arg.isEmpty())
                .collect(Collectors.toList());
    }

    private static int getIntFlag(String[] args, String flagKey, int defaultValue) {
        String rawValue = parseFlag(args, flagKey, "");
        return rawValue.isEmpty() ? defaultValue : Integer.parseInt(rawValue);
    }

    private static String parseFlag(String[] args, String flagKey, String defaultFlagValue) {
        String key = String.format("--%s=", flagKey);

        return Arrays.stream(args)
                .map(String::trim)
                .filter(arg -> arg.startsWith(key))
                .findFirst()
                .map(arg -> arg.split("=")[1])
                .orElse(defaultFlagValue);
    }
}
