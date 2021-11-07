package groupAD.players.amafmcts;

import core.GameState;
import players.heuristics.AdvancedHeuristic;
import players.heuristics.CustomHeuristic;
import players.heuristics.StateHeuristic;
import utils.*;

import java.util.*;
import java.util.stream.Collectors;

public class CustomSingleTreeNode
{
    public CustomMCTSParams params;

    private CustomSingleTreeNode parent;
    private CustomSingleTreeNode[] children;
    private double totValue;
    private double totValueSquared;
    private double amafValue;
    private int nVisits;
    private int amafVisits;
    private Random m_rnd;
    private int m_depth;
    private double[] bounds = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
    private double[] squaredBounds = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
    private double[] amafBounds = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
    private int childIdx;
    private int fmCallsCount;

    private int num_actions;
    private Types.ACTIONS[] actions;

    private GameState rootState;
    private StateHeuristic rootStateHeuristic;

    CustomSingleTreeNode(CustomMCTSParams p, Random rnd, int num_actions, Types.ACTIONS[] actions) {
        this(p, null, -1, rnd, num_actions, actions, 0, null);
    }

    private CustomSingleTreeNode(CustomMCTSParams p, CustomSingleTreeNode parent, int childIdx, Random rnd, int num_actions,
                                 Types.ACTIONS[] actions, int fmCallsCount, StateHeuristic sh) {
        this.params = p;
        this.fmCallsCount = fmCallsCount;
        this.parent = parent;
        this.m_rnd = rnd;
        this.num_actions = num_actions;
        this.actions = actions;
        children = new CustomSingleTreeNode[num_actions];
        totValue = 0.0;
        totValueSquared = 0.0;
        amafValue = 0.0;
        amafVisits = 0;
        this.childIdx = childIdx;
        if(parent != null) {
            m_depth = parent.m_depth + 1;
            this.rootStateHeuristic = sh;
        }
        else
            m_depth = 0;
    }

    void setRootGameState(GameState gs) {
        this.rootState = gs;
        if (params.heuristic_method == params.CUSTOM_HEURISTIC)
            this.rootStateHeuristic = new CustomHeuristic(gs);
        else if (params.heuristic_method == params.ADVANCED_HEURISTIC) // New method: combined heuristics
            this.rootStateHeuristic = new AdvancedHeuristic(gs, m_rnd);
    }


    void mctsSearch(ElapsedCpuTimer elapsedTimer) {

        double avgTimeTaken;
        double acumTimeTaken = 0;
        long remaining;
        int numIters = 0;

        int remainingLimit = 5;
        boolean stop = false;

        while(!stop){

            GameState state = rootState.copy();
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            CustomSingleTreeNode selected = treePolicy(state);
            Pair<Double, ArrayList<Integer>> rollOutResult = selected.rollOut(state);
            backUpRolloutResult(selected, rollOutResult);

            //Stopping condition
            if(params.stop_type == params.STOP_TIME) {
                numIters++;
                acumTimeTaken += (elapsedTimerIteration.elapsedMillis()) ;
                avgTimeTaken  = acumTimeTaken/numIters;
                remaining = elapsedTimer.remainingTimeMillis();
                stop = remaining <= 2 * avgTimeTaken || remaining <= remainingLimit;
            } else if(params.stop_type == params.STOP_ITERATIONS) {
                numIters++;
                stop = numIters >= params.num_iterations;
            } else if(params.stop_type == params.STOP_FMCALLS) {
                fmCallsCount += params.rollout_depth;
                stop = (fmCallsCount + params.rollout_depth) > params.num_fmcalls;
            }
        }
//        System.out.println(" ITERS " + numIters);
    }

    private void backPropAMAFResults(Pair<Double, ArrayList<Integer>> rollOutResult) {
        Set<Integer> childrenIdsToReward = new HashSet<>(rollOutResult.second);
        for (int childId: childrenIdsToReward) {
            if (children[childId] != null) {
                singleAmafBackUp(children[childId], rollOutResult.first);
            }
        }
    }

    private CustomSingleTreeNode treePolicy(GameState state) {

        CustomSingleTreeNode cur = this;

        while (!state.isTerminal() && cur.m_depth < params.rollout_depth) {
            if (cur.notFullyExpanded()) {
                return cur.expand(state);

            } else {
                cur = cur.maxChildByUct(state);
            }
        }

        return cur;
    }

    private CustomSingleTreeNode expand(GameState state) {

        int bestAction = 0;
        double bestValue = -1;

        for (int i = 0; i < children.length; i++) {
            double x = m_rnd.nextDouble();
            if (x > bestValue && children[i] == null) {
                bestAction = i;
                bestValue = x;
            }
        }

        //Roll the state
        roll(state, actions[bestAction]);

        CustomSingleTreeNode tn = new CustomSingleTreeNode(params,this,bestAction,this.m_rnd,num_actions,
                actions, fmCallsCount, rootStateHeuristic);
        children[bestAction] = tn;
        return tn;
    }

    private void roll(GameState gs, Types.ACTIONS act) {
        int nPlayers = Types.NUM_PLAYERS;
        Types.ACTIONS[] actionsAll = new Types.ACTIONS[nPlayers];
        int playerId = gs.getPlayerId() - Types.TILETYPE.AGENT0.getKey();

        for(int i = 0; i < nPlayers; ++i) {
            if(playerId == i) {
                actionsAll[i] = act;
            }
            else {
                actionsAll[i] = getOpponentAction(gs, act, i);
            }
        }

        gs.next(actionsAll);
    }

    private CustomSingleTreeNode maxChildByUct(GameState state) {
        CustomSingleTreeNode selected = null;
        double bestValue = -Double.MAX_VALUE;
        for (CustomSingleTreeNode child : this.children)  {
            double childValue =  child.refactoredUct(state, bounds, amafBounds);

            if (childValue > bestValue) {
                selected = child;
                bestValue = childValue;
            }
        }
        checkNotNull(selected, "Warning! returning null");

        //Roll the state:
        roll(state, actions[selected.childIdx]);

        return selected;
    }

    private void checkNotNull(CustomSingleTreeNode node, String message) {
        if (node == null) {
            throw new RuntimeException(message);
        }
    }

    private double refactoredUct(GameState state, double qValueBounds[], double amafValueBounds[]) {
        double pureQ = qComponent(qValueBounds);
        double exploration = calculateExplorationTerm(state);


        double amaf = amafComponent(amafValueBounds);
        double amafAlpha = (params.amaf_rave_constant - nVisits) / (params.amaf_rave_constant + params.epsilon);
        double clippedAlpha = Math.max(0, amafAlpha);

        double uct = ((pureQ + exploration) * (1 - clippedAlpha)) + (amaf * clippedAlpha);
        //break ties randomly
        double uctWithNoise = Utils.noise(uct, params.epsilon, this.m_rnd.nextDouble());
        return uctWithNoise;
    }

    private double calculateExplorationTerm(GameState state) {
        double bias =
                params.tree_selection_policy == params.PROGRESSIVE_BIAS_TREE_SELECTION_POLICY ?
                        biasComponent(state) : 0;
        double tuningExplorationFactor =
                params.tree_selection_policy == params.UCB_ONE_TUNED_TREE_SELECTION_POLICY ?
                        tunedExplorationFactor() : 1;
        return (explorationComponent() * tuningExplorationFactor) + bias;
    }

    private double amafComponent(double[] amafValueBounds) {
        double averageAmaf = amafValue / (amafVisits + params.epsilon);
        return Utils.normalise(averageAmaf, amafValueBounds[0], amafValueBounds[1]);
    }

    private double qComponent(double[] qValueBounds) {
        double averageQ =  totValue / (nVisits + params.epsilon);
        double normalisedQ = Utils.normalise(averageQ, qValueBounds[0], qValueBounds[1]);

        return normalisedQ;
    }

    private double tunedExplorationFactor() {
        double averageQ =  totValue / (nVisits + params.epsilon);
        double averageQSquared =  totValueSquared / (nVisits + params.epsilon);
        double variance = averageQSquared - (Math.pow(averageQ, 2));

        double explorationOnFirstTRewards = Math.sqrt(2 * Math.log(parent.nVisits + 1) / (this.nVisits + params.epsilon));
        double V = variance + explorationOnFirstTRewards;

        return Math.min(0.25, V);
    }

    private double explorationComponent() {
        return params.K * Math.sqrt(Math.log(parent.nVisits + 1) / (nVisits + params.epsilon));
    }

    private double biasComponent(GameState state) {
        Types.ACTIONS action = actions[childIdx];
        return actionHeuristicValue(action, state);
    }

    // Returns the reward of the rollout and the list of actions taken
    // during the rollout
    private Pair<Double, ArrayList<Integer>> rollOut(GameState state) {
        int thisDepth = this.m_depth;
        ArrayList<Integer> rollOutActions = new ArrayList<Integer>();

        while (!finishRollout(state,thisDepth)) {
            int action = nextRolloutAction(state);
            rollOutActions.add(action);
            roll(state, actions[action]);
            thisDepth++;
        }

        return new Pair(rootStateHeuristic.evaluateState(state), rollOutActions);
    }

    private int nextRolloutAction(GameState state) {
        double randomVal = m_rnd.nextDouble();
        if (randomVal < params.rollout_bias_probability) {
            return heuristicallyBiasedAction(state);
        }
        return  safeRandomAction(state);
    }

    private int safeRandomAction(GameState state) {
        List<Types.ACTIONS> actionsToTry = getSafeActions(state);
        Types.ACTIONS randomSafeAction = actionsToTry.get(m_rnd.nextInt(actionsToTry.size()));

        return randomSafeAction.getKey();
    }

    private int heuristicallyBiasedAction(GameState state) {
        List<Types.ACTIONS> actionsToTry = getSafeActions(state);
        actionsToTry.sort(Comparator.comparing(action -> actionHeuristicValue(action, state)));
        return actionsToTry.get(actionsToTry.size() - 1).getKey();
    }

    private double actionHeuristicValue(Types.ACTIONS action, GameState state) {
        GameState stateCopy = state.copy();
        roll(stateCopy, action);
        return rootStateHeuristic.evaluateState(stateCopy);
    }

    private List<Types.ACTIONS> getSafeActions(GameState state) {
        List<Types.ACTIONS> allActions = List.copyOf(Types.ACTIONS.all());
        List<Types.ACTIONS> safeActions = allActions
                .stream()
                .filter(action -> isSafeAction(action, state.getPosition(), state.getBoard()))
                .collect(Collectors.toList());
        return safeActions.isEmpty() ? allActions : safeActions;
    }

    private boolean isSafeAction(Types.ACTIONS action, Vector2d position, Types.TILETYPE[][] board) {
        Vector2d dir = action.getDirection().toVec();
        Vector2d newPosition = position.add(dir);
        return isSafePosition(newPosition, board);
    }

    private boolean isSafePosition(Vector2d position, Types.TILETYPE[][] board) {
        if (!isLegalPosition(position, board)) return false;

        boolean flameTilePosition = board[position.y][position.x] == Types.TILETYPE.FLAMES;
        return !flameTilePosition;
    }

    private boolean isLegalPosition(Vector2d position, Types.TILETYPE[][] board) {
        int width = board.length;
        int height = board[0].length;
        return position.x >= 0 && position.x < width && position.y >= 0 && position.y < height;
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean finishRollout(GameState rollerState, int depth) {
        if (depth >= params.rollout_depth)      //rollout end condition.
            return true;

        if (rollerState.isTerminal())               //end of game
            return true;

        return false;
    }

    private void backUpRolloutResult(CustomSingleTreeNode node, Pair<Double, ArrayList<Integer>> result) {
        recursiveBackUp(node, result.first);
        if (params.amaf_rave_constant > 0) {
            backPropAMAFResults(result);
        }
    }

    private void recursiveBackUp(CustomSingleTreeNode node, double result) {
        CustomSingleTreeNode n = node;
        double currentResult = result;
        while(n != null) {
            singleBackUp(n, currentResult);
            n = n.parent;
            currentResult *= (1 - params.reward_decay_factor);
        }
    }

    private void singleBackUp(CustomSingleTreeNode node, double result) {
        double resultSquared = Math.pow(result, 2);
        node.nVisits++;
        node.totValue += result;
        node.totValueSquared += resultSquared;
        if (result < node.bounds[0]) {
            node.bounds[0] = result;
        }
        if (result > node.bounds[1]) {
            node.bounds[1] = result;
        }
    }

    private void singleAmafBackUp(CustomSingleTreeNode node, double result) {
        node.amafVisits++;
        node.amafValue += result;
        if (result < node.amafBounds[0]) {
            node.amafBounds[0] = result;
        }
        if (result > node.amafBounds[1]) {
            node.amafBounds[1] = result;
        }
    }

    int mostVisitedAction() {
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;
        boolean allEqual = true;
        double first = -1;

        for (int i=0; i<children.length; i++) {

            if(children[i] != null) {
                if(first == -1)
                    first = children[i].nVisits;
                else if(first != children[i].nVisits) {
                    allEqual = false;
                }

                double childValue = children[i].nVisits;
                childValue = Utils.noise(childValue, params.epsilon, this.m_rnd.nextDouble());     //break ties randomly
                if (childValue > bestValue) {
                    bestValue = childValue;
                    selected = i;
                }
            }
        }
//        System.out.println(" MOST VISTS " + bestValue);

        if (selected == -1) {
            selected = 0;
        }
        else if (allEqual) {
            //If all are equal, we opt to choose for the one with the best Q.
            selected = bestAction();
        }

        return selected;
    }

    public int bestAction() {
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;

        for (int i=0; i<children.length; i++) {

            if(children[i] != null) {
                double childValue = children[i].totValue / (children[i].nVisits + params.epsilon);
                childValue = Utils.noise(childValue, params.epsilon, this.m_rnd.nextDouble());     //break ties randomly
                if (childValue > bestValue) {
                    bestValue = childValue;
                    selected = i;
                }
            }
        }

        if (selected == -1) {
            System.out.println("Unexpected selection!");
            selected = 0;
        }

        return selected;
    }


    private boolean notFullyExpanded() {
        for (CustomSingleTreeNode tn : children) {
            if (tn == null) {
                return true;
            }
        }

        return false;
    }

    private Types.ACTIONS getOpponentAction(GameState gs, Types.ACTIONS act, int opponentIdx) {
        if (params.opponent_model == params.RANDOM_OPPONENT_MODEL) {
            int randomAction = m_rnd.nextInt(gs.nActions());
            return Types.ACTIONS.all().get(randomAction);
        }

        if (params.opponent_model == params.MIRROR_OPPONENT_MODEL) {
            return mirrorAction(act);
        }

        if (params.opponent_model == params.MINIMIZER_OPPONENT_MODEL) {
            return minimizingOpponentAction(gs, act, opponentIdx);
        }

        if (params.opponent_model == params.MAXIMIZING_OPPONENT_MODEL) {
            return maximizingOpponentAction(gs, act, opponentIdx);
        }

        return act;
    }

    private Types.ACTIONS mirrorAction(Types.ACTIONS act) {
        switch (act){
            case ACTION_UP:
                return Types.ACTIONS.ACTION_DOWN;
            case ACTION_DOWN:
                return Types.ACTIONS.ACTION_UP;
            case ACTION_LEFT:
                return Types.ACTIONS.ACTION_RIGHT;
            case ACTION_RIGHT:
                return Types.ACTIONS.ACTION_LEFT;
            case ACTION_BOMB:
                return act;
            case ACTION_STOP:
            default:
                return Types.ACTIONS.ACTION_BOMB;
        }

    }

    private Types.ACTIONS minimizingOpponentAction(GameState state, Types.ACTIONS act, int opponentIdx) {
        int playerIdx = state.getPlayerId() - Types.TILETYPE.AGENT0.getKey();
        List<Types.ACTIONS> allActions = Types.ACTIONS.all();
        Types.ACTIONS valueMinimizingAction = null;
        double minimumValue = Double.MAX_VALUE;

        Types.ACTIONS[] simulationActions = new Types.ACTIONS[Types.NUM_PLAYERS];
        Arrays.fill(simulationActions, Types.ACTIONS.ACTION_STOP);
        simulationActions[playerIdx] = act;
        for (Types.ACTIONS action : allActions) {
            GameState stateCopy = state.copy();
            Types.ACTIONS[] nextStateActions = Arrays.copyOf(simulationActions, simulationActions.length);
            nextStateActions[opponentIdx] = action;
            stateCopy.next(nextStateActions);
            double value = rootStateHeuristic.evaluateState(stateCopy);
            double valueWithNoise = Utils.noise(value, params.epsilon, this.m_rnd.nextDouble());

            if (valueWithNoise <= minimumValue) {
                minimumValue = valueWithNoise;
                valueMinimizingAction = action;
            }
        }

        return valueMinimizingAction;
    }

    private Types.ACTIONS maximizingOpponentAction(GameState state, Types.ACTIONS act, int opponentIdx) {
        int playerIdx = state.getPlayerId() - Types.TILETYPE.AGENT0.getKey();
        List<Types.ACTIONS> allActions = Types.ACTIONS.all();
        Types.ACTIONS valueMaximizingAction = null;
        double maximumValue = -Double.MAX_VALUE;

        Types.ACTIONS[] simulationActions = new Types.ACTIONS[Types.NUM_PLAYERS];
        Arrays.fill(simulationActions, Types.ACTIONS.ACTION_STOP);
        simulationActions[playerIdx] = act;
        for (Types.ACTIONS action : allActions) {
            GameState stateCopy = state.copy();
            Types.ACTIONS[] nextStateActions = Arrays.copyOf(simulationActions, simulationActions.length);
            nextStateActions[opponentIdx] = action;
            stateCopy.next(nextStateActions);
            double value = rootStateHeuristic.evaluateState(stateCopy);
            double valueWithNoise = Utils.noise(value, params.epsilon, this.m_rnd.nextDouble());

            if (valueWithNoise >= maximumValue) {
                maximumValue = valueWithNoise;
                valueMaximizingAction = action;
            }
        }

        return valueMaximizingAction;
    }

}
