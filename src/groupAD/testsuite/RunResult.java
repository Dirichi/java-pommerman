package groupAD.testsuite;

public class RunResult {
    private double winPercentage;
    private double tiePercentage;
    private double lossPercentage;
    private double overTimeAverage;
    private int playerId;

    public RunResult setLossPercentage(double lossPercentage) {
        this.lossPercentage = lossPercentage;
        return this;
    }

    public RunResult setWinPercentage(double winPercentage) {
        this.winPercentage = winPercentage;
        return this;
    }

    public RunResult setTiePercentage(double tiePercentage) {
        this.tiePercentage = tiePercentage;
        return this;
    }

    public RunResult setOverTimeAverage(double overTimeAverage) {
        this.overTimeAverage = overTimeAverage;
        return this;
    }

    public RunResult setPlayerId(int id) {
        this.playerId = id;
        return this;
    }

    public String asString() {
        return String.format(
                "WIN PERCENT = %s, TIE PERCENT = %s, LOSS PERCENT = %s, OVERTIME AVG = %s",
                winPercentage,
                tiePercentage,
                lossPercentage,
                overTimeAverage
        );
    }

    public int getPlayerId() {
        return playerId;
    }
}
