package groupAD.players.amafmcts;

import players.optimisers.ParameterSet;
import utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class CustomMCTSParams implements ParameterSet {

    // Constants
    public final double HUGE_NEGATIVE = -1000;
    public final double HUGE_POSITIVE =  1000;

    public final int STOP_TIME = 0;
    public final int STOP_ITERATIONS = 1;
    public final int STOP_FMCALLS = 2;

    public final int CUSTOM_HEURISTIC = 0;
    public final int ADVANCED_HEURISTIC = 1;

    public final int DO_NOTHING_OPPONENT_MODEL = 0;
    public final int RANDOM_OPPONENT_MODEL = 1;
    public final int MINIMIZER_OPPONENT_MODEL = 2;
    public final int MAXIMIZING_OPPONENT_MODEL = 3;
    public final int MIRROR_OPPONENT_MODEL = 4;
    public final int SAME_OPPONENT_MODEL = 5;


    public final int DEFAULT_UCT_TREE_SELECTION_POLICY = 0;
    public final int UCB_ONE_TUNED_TREE_SELECTION_POLICY = 1;
    public final int PROGRESSIVE_BIAS_TREE_SELECTION_POLICY = 2;


    public double epsilon = 1e-6;

    // Parameters
    public double K = Math.sqrt(2);
    public int rollout_depth = 8;//10;
    public int heuristic_method = CUSTOM_HEURISTIC;
    public double amaf_rave_constant = 0;
    public double reward_decay_factor = 0;
    public double rollout_bias_probability = 0;
    public int opponent_model = RANDOM_OPPONENT_MODEL;
    public int tree_selection_policy = DEFAULT_UCT_TREE_SELECTION_POLICY;


    // Budget settings
    public int stop_type = STOP_TIME;
    public int num_iterations = 200;
    public int num_fmcalls = 2000;
    public int num_time = 40;

    @Override
    public void setParameterValue(String param, Object value) {
        switch(param) {
            case "K": K = (double) value; break;
            case "rollout_depth": rollout_depth = (int) value; break;
            case "heuristic_method": heuristic_method = (int) value; break;
            case "amaf_rave_constant": amaf_rave_constant = (double) value; break;
            case "reward_decay_factor": reward_decay_factor = (double) value; break;
            case "rollout_bias_probability": rollout_bias_probability = (double) value; break;
            case "opponent_model": opponent_model = (int) value; break;
            case "tree_selection_policy": tree_selection_policy = (int) value; break;
        }
    }

    @Override
    public Object getParameterValue(String param) {
        switch(param) {
            case "K": return K;
            case "rollout_depth": return rollout_depth;
            case "heuristic_method": return heuristic_method;
            case "amaf_rave_constant": return  amaf_rave_constant;
            case "reward_decay_factor": return  reward_decay_factor;
            case "rollout_bias_probability": return rollout_bias_probability;
            case "opponent_model": return opponent_model;
            case "tree_selection_policy": return tree_selection_policy;
        }
        return null;
    }

    @Override
    public ArrayList<String> getParameters() {
        ArrayList<String> paramList = new ArrayList<>();
        paramList.add("K");
        paramList.add("rollout_depth");
        paramList.add("heuristic_method");
        paramList.add("amaf_rave_constant");
        paramList.add("reward_decay_factor");
        paramList.add("rollout_bias_probability");
        paramList.add("opponent_model");
        paramList.add("tree_selection_policy");
        return paramList;
    }

    @Override
    public Map<String, Object[]> getParameterValues() {
        HashMap<String, Object[]> parameterValues = new HashMap<>();
        parameterValues.put("K", new Double[]{Math.sqrt(2)});
        parameterValues.put("rollout_depth", new Integer[]{10});
        parameterValues.put("heuristic_method", new Integer[]{CUSTOM_HEURISTIC});
        parameterValues.put("amaf_rave_constant", new Double[]{0.0, 10.0});
        parameterValues.put("reward_decay_factor", new Double[]{0.0, 0.1});
        parameterValues.put("rollout_bias_probability", new Double[]{0.0, 0.4, 0.8});
        parameterValues.put("opponent_model", new Integer[]{RANDOM_OPPONENT_MODEL, MINIMIZER_OPPONENT_MODEL});
        parameterValues.put("tree_selection_policy", new Integer[]{UCB_ONE_TUNED_TREE_SELECTION_POLICY, PROGRESSIVE_BIAS_TREE_SELECTION_POLICY});
        return parameterValues;
    }

    @Override
    public Pair<String, ArrayList<Object>> getParameterParent(String parameter) {
        return null;  // No parameter dependencies
    }

    @Override
    public Map<Object, ArrayList<String>> getParameterChildren(String root) {
        return new HashMap<>();  // No parameter dependencies
    }

    @Override
    public Map<String, String[]> constantNames() {
        HashMap<String, String[]> names = new HashMap<>();
        names.put("heuristic_method", new String[]{"CUSTOM_HEURISTIC", "ADVANCED_HEURISTIC"});
        names.put("opponent_model", new String[]{"DO_NOTHING_OPPONENT_MODEL", "RANDOM_OPPONENT_MODEL", "MINIMIZER_OPPONENT_MODEL"});
        names.put("tree_selection_policy", new String[]{"DEFAULT_UCT_TREE_SELECTION_POLICY", "UCB_ONE_TUNED_TREE_SELECTION_POLICY", "PROGRESSIVE_BIAS_TREE_SELECTION_POLICY"});
        return names;
    }
}
