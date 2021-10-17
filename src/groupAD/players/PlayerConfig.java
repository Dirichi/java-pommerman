package groupAD.players;

import players.optimisers.ParameterSet;
import players.Player;
import players.optimisers.ParameterizedPlayer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;


public class PlayerConfig<T extends  Player> {
    private final String title;
    private final Class<T> playerClass;
    private ParameterSet params;
    private Map<String, Object> paramOverrides;
    private long seed;
    private int playerId;

    public PlayerConfig(String title, Class<T> playerClass) {
        this.title = title;
        this.playerClass = playerClass;
    }

    public PlayerConfig(String title, Class<T> playerClass, ParameterSet params) {
        this.title = title;
        this.playerClass = playerClass;
        this.params = params;
    }

    public PlayerConfig(
            String title,
            Class<T> playerClass,
            ParameterSet params, Map<String, Object> paramOverrides) {
        this.title = title;
        this.playerClass = playerClass;
        this.params = params;
        this.paramOverrides = paramOverrides;
    }

    public String getTitle() {
        return title;
    }

    public PlayerConfig<T> setSeed(long seed) {
        this.seed = seed;
        return this;
    }

    public PlayerConfig<T> setPlayerId(int playerId) {
        this.playerId = playerId;
        return this;
    }

    public Player buildPlayer()  {
        Constructor<Player> playerConstructor = getPlayerConstructor();
        Player player = initPlayer(playerConstructor, seed, playerId);

        // Set parameter values if the player is a Parametrized Player
        if (ParameterizedPlayer.class.isAssignableFrom(playerClass)) {
            ParameterSet parameterSet = buildParameters();
            if (parameterSet != null) {
                ((ParameterizedPlayer) player).setParameters(params);
            }
        }
        return player;
    }

    private ParameterSet buildParameters() {
        if (params == null) {
            return null;
        }
        if (paramOverrides != null) {
            for (Map.Entry<String, Object> entry: paramOverrides.entrySet()) {
                params.setParameterValue(entry.getKey(), entry.getValue());
            }
        }
        return params;
    }

    private Player initPlayer(Constructor<Player> constructor, long playerSeed, int playerId) {
        Player player = null;
        try {
            player = constructor.newInstance(playerSeed, playerId);
        } catch (InstantiationException e) {
            throwInvalidPlayerClass(e);
        } catch (IllegalAccessException e) {
            throwInvalidPlayerClass(e);
        } catch (InvocationTargetException e) {
            throwInvalidPlayerClass(e);
        }
        return player;
    }

    private Constructor<Player> getPlayerConstructor() {
        Constructor playerConstructor = null;
        try {
            playerConstructor = playerClass.getConstructor(long.class, int.class);
        } catch (NoSuchMethodException e) {
            throwInvalidPlayerClass(e);
        }
        return playerConstructor;
    }

    private void throwInvalidPlayerClass(Exception e) {
        throw new IllegalArgumentException(String.format("%s is not a valid Player class", playerClass.toString()), e);
    }
}
