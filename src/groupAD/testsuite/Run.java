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
                Types.RESULT[] results = g.run(true);

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

            RunResult runResult = new RunResult()
                    .setWins(winCount[pIdx])
                    .setTies(tieCount[pIdx])
                    .setLosses(lossCount[pIdx])
                    .setOverTimes(overtimeCount[pIdx])
                    .setPlayerId(playerId)
                    .setTotalGames(totalNgames);

            results[pIdx] = runResult;
        }

        //Done, show stats
        System.out.println("N \tWin \tTie \tLoss \tPlayer (overtime average)");
        for (int pIdx = 0; pIdx < numPlayers; pIdx++) {
            String player = g.getPlayers().get(pIdx).getClass().toString().replaceFirst("class ", "");

            double winPerc = winCount[pIdx] * 100.0 / (double)totalNgames;
            double tiePerc = tieCount[pIdx] * 100.0 / (double)totalNgames;
            double lossPerc = lossCount[pIdx] * 100.0 / (double)totalNgames;
            double overtimesAvg = overtimeCount[pIdx] / (double)totalNgames;

            System.out.println(totalNgames + "\t" + winPerc + "%\t" + tiePerc + "%\t" + lossPerc + "%\t" + player + " (" + overtimesAvg + ")" );
        }
        return List.copyOf(Arrays.asList(results));
    }
}

