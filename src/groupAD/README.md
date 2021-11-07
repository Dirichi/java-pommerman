# GROUP AD Pommerman Assignment

## Introduction

This package includes code with modifications to the default implementation of the baseline Monte Carlo Tree Search
Agent in the Java implementation of the Pommerman benchmark.

It also includes a testsuite against which all variations of the MCTS agent are run.

## Instantiating Our Champion
- Todo

## Implemented Enhancements

The enhancements we have implemented include

1. Tree Selection Policy:  
   1. Tree selection with progressive bias
      1. Adds a bias term to the default UCT equation, which is dependent on a heuristically determined quality of the action under consideration.
   2. Tree selection with UCB1-tuned 
      1. Adds a bias term to the default UCT equation, which is dependent on the variance of the rewards received at a particular node.
2. Opponent Modelling
   1. Minimizer Opponent Model: 
      1. Uses One-Step look ahead to select the opponent action which minimizes the utility of a given action for the agent in the current state.
   2. Maximizer Opponent Model: 
      1. Uses One-Step look ahead to select the opponent action which maximizes the utility of a given action for the agent in the current state.
   3. Do Nothing Opponent Model: 
      1. A No-Op model.
   4. Mirror Opponent Model: 
      1. Executes the mirror action of the agent. If the agent selects action UP, the opponent will select action DOWN. If the agent selects action RIGHT, the opponent will select action LEFT. Vice-versa.
   5. Same Opponent Model:
      1. Executes the exact same action as the agent.
3. Reward Decay:
   1. Multiplies the back-propagated reward value by a value K ^ N for each node, where K is a constant value less than 1, and N is the number of nodes between the current node, and the node from which the reward was propagated. 
4. Epsilon-Greedy Roll-out Bias:
   1. Biases action selection in the rollout phase towards actions which yield a higher reward, with a probability of epsilon.

## Running an Experiment

Running an experiment consists of selecting ExperimentConfig(s) (i.e. the configuration of the Game
in terms of game mode and vision range), and selecting the PlayerConfig(s) to
test.


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