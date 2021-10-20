package groupAD.testsuite;

import utils.Types;

import java.util.*;
import java.util.stream.Collectors;

public class Experiments {
    private static final ExperimentConfig FFA_FULL_VISIBILITY = new ExperimentConfig(
            "FFA Full Visibility", Types.GAME_MODE.FFA, -1);
    private static final ExperimentConfig TEAM_FULL_VISIBILITY = new ExperimentConfig(
            "Team Full Visibility", Types.GAME_MODE.TEAM, -1);
    private static final ExperimentConfig FFA_TWO_VISIBILITY = new ExperimentConfig(
            "FFA 2 Visibility", Types.GAME_MODE.FFA, 2);
    private static final ExperimentConfig TEAM_TWO_VISIBILITY = new ExperimentConfig(
            "Team 2 Visibility", Types.GAME_MODE.TEAM, 2);
    private static final Map<String, ExperimentConfig> EXPERINMENT_CONFIG_MAP = Map.of(
            "1", FFA_FULL_VISIBILITY,
            "2", TEAM_FULL_VISIBILITY,
            "3", FFA_TWO_VISIBILITY,
            "4", TEAM_TWO_VISIBILITY
    );

    public List<ExperimentConfig> getExperimentsByIds(List<String> ids) {
        return ids.stream().map(id -> EXPERINMENT_CONFIG_MAP.get(id)).collect(Collectors.toList());
    }

    public List<ExperimentConfig> getAllExperiments() {
        return EXPERINMENT_CONFIG_MAP
                .values()
                .stream()
                .collect(Collectors.toList());
    }
}
