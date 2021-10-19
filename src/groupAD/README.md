# GROUP AD Pommerman Assignment

## Introduction

This package includes code for custom agents as well 
as code for running experiments on with custom or modified agents.

## Running an Experiment

Running an experiment consists of selecting ExperimentConfig(s) (i.e. the configuration of the Game
in terms of game mode and vision range), and selecting the PlayerConfig(s) to
test in this config.


All ExperimentConfigs are listed in `groupAd.testsuite.Experiments` 
in the `EXPERIMENT_CONFIG_MAP` property, while all PlayerConfig(s) are listed in `groupAd.players.Players` 
in the `PLAYER_CONFIG_MAP` property.

To run an experiment, run the `TestSuite.java` file with the arguments for the PlayerConfig(s) and ExperimentConfig(s)
you would like to run.

For example, arguments:

```shell
--experiment_config_ids=1 --player_config_ids=1
```

will run the `DEFAULT_MCTS` player in the `FFA_FULL_VISIBILITY` configuration.

The results of each experiment will be displayed at the end of its run.

## Adding new PlayerConfigs

A PlayerConfig is declared as follows:

```java
    private static final PlayerConfig MCTS_BIGGER_K = new PlayerConfig(
            "MCTS (K = sqrt(2) * 4)", // Human friendly description
            MCTSPlayer.class, // Player Class
            new MCTSParams(), // Default params for Player
            Map.of("K", Math.sqrt(2) * 4) // Overrides of default params
        );
```

To add a new one to the list of available PlayerConfigs, 
- Declare a PlayerConfig in `groupAd.players.Players` as shown above.
- Add an entry in the `PLAYER_CONFIG_MAP` Map with an unique id
  - e.g. `"UNIQUE_ID", MCTS_BIGGER_K`
- Then run experiments on this new PlayerConfig:
  - e.g. ```shell --experiment_config_ids=1 --player_config_ids=UNIQUE_ID```