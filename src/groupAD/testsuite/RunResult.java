package groupAD.testsuite;

public class RunResult {
    private int wins;
    private int ties;
    private int losses;
    private int overTimes;
    private int totalGames;
    private int playerId;

    public RunResult setWins(int wins) {
        this.wins = wins;
        return this;
    }

    public RunResult setTies(int ties) {
        this.ties = ties;
        return this;
    }

    public RunResult setLosses(int losses) {
        this.losses = losses;
        return this;
    }

    public RunResult setOverTimes(int overTimes) {
        this.overTimes = overTimes;
        return this;
    }

    public RunResult setPlayerId(int id) {
        this.playerId = id;
        return this;
    }

    public RunResult setTotalGames(int totalGames) {
        this.totalGames = totalGames;
        return this;
    }

    public double getWinPercentage() {
        return wins * 100 / totalGames;
    }

    public double getTiePercentage() {
        return ties * 100 / totalGames;
    }

    public double getOverTimeAverage() {
        return overTimes / totalGames;
    }

    public double getLossPercentage() {
        return losses * 100 / totalGames;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getTies() {
        return ties;
    }

    public String asString() {
        return String.format(
                "WIN PERCENT = %s, TIE PERCENT = %s, LOSS PERCENT = %s, OVERTIME AVG = %s, TOTAL GAMES = %s",
                getWinPercentage(),
                getTiePercentage(),
                getLossPercentage(),
                getOverTimeAverage(),
                totalGames
        );
    }

    public RunResult copy() {
        return new RunResult()
                .setPlayerId(playerId)
                .setWins(wins)
                .setTies(ties)
                .setLosses(losses)
                .setTotalGames(totalGames);
    }

    public RunResult combine(RunResult other) {
        if (other == null) {
            return copy();
        }

        int combinedTotalGames = totalGames + other.getTotalGames();
        int combinedWins = wins + other.getWins();
        int combinedTies = ties + other.getTies();
        int combinedLosses = losses + other.getLosses();

        return new RunResult()
                .setPlayerId(playerId)
                .setWins(combinedWins)
                .setTies(combinedTies)
                .setLosses(combinedLosses)
                .setTotalGames(combinedTotalGames);
    }

}
