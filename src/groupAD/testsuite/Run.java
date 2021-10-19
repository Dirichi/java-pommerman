package groupAD.testsuite;

import core.Game;
import players.*;
import utils.*;

import java.util.*;

public class Run {

    public static List<RunResult> runGames(Game g, long seeds[], int repetitions, boolean useSeparateThreads, boolean verbose) {
        int numPlayers = g.getPlayers().size();
        int[] winCount = new int[numPlayers];
        int[] tieCount = new int[numPlayers];
        int[] lossCount = new int[numPlayers];

        int[] overtimeCount = new int[numPlayers];

        int numSeeds = seeds.length;
        int totalNgames = numSeeds * repetitions;

        for(int s = 0; s < numSeeds; s++) {
            long seed = seeds[s];

            for (int i = 0; i < repetitions; i++) {
                long playerSeed = System.currentTimeMillis();

                //Verbose output
                if (verbose) {
                    System.out.println( playerSeed + ", " + seed + ", " + (s*repetitions + i) + "/" + totalNgames + ", ");
                }

                g.reset(seed);
                EventsStatistics.REP = i;
                GameLog.REP = i;

                // Set random seed for players and reset them
                ArrayList<Player> players = g.getPlayers();
                for (int p = 0; p < g.nPlayers(); p++) {
                    players.get(p).reset(playerSeed, p + Types.TILETYPE.AGENT0.getKey());
                }
                Types.RESULT[] results = g.run(useSeparateThreads);

                for (int pIdx = 0; pIdx < numPlayers; pIdx++) {
                    switch (results[pIdx]) {
                        case WIN:
                            winCount[pIdx]++;
                            break;
                        case TIE:
                            tieCount[pIdx]++;
                            break;
                        case LOSS:
                            lossCount[pIdx]++;
                            break;
                    }
                }

                int[] overtimes = g.getPlayerOvertimes();
                for(int j = 0; j < overtimes.length; ++j)
                    overtimeCount[j] += overtimes[j];
            }
        }

        RunResult[] results = new RunResult[numPlayers];
        for (int pIdx = 0; pIdx < numPlayers; pIdx++) {
            int playerId = g.getPlayers().get(pIdx).getPlayerID();

            double winPerc = winCount[pIdx] * 100.0 / (double)totalNgames;
            double tiePerc = tieCount[pIdx] * 100.0 / (double)totalNgames;
            double lossPerc = lossCount[pIdx] * 100.0 / (double)totalNgames;
            double overtimesAvg = overtimeCount[pIdx] / (double)totalNgames;

            RunResult runResult = new RunResult()
                    .setWinPercentage(winPerc)
                    .setTiePercentage(tiePerc)
                    .setLossPercentage(lossPerc)
                    .setOverTimeAverage(overtimesAvg)
                    .setPlayerId(playerId);

            results[pIdx] = runResult;
        }
        return List.copyOf(Arrays.asList(results));
    }
}

