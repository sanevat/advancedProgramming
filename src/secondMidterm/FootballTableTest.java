package secondMidterm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class Team {
    String name;
    int playedGames;
    int wins;
    int loses;
    int draws;
    int points;
    int totalGoalsScored;
    int totalGoalsReceived;

    public Team(String name) {
        this.name = name;
        this.wins = 0;
        this.loses = 0;
        this.points = 0;
        this.draws = 0;
        this.totalGoalsScored = 0;
        this.totalGoalsReceived = 0;
    }

    public int totalPoints() {
        return wins * 3 + draws;
    }

    public int goalDifference() {
        return totalGoalsScored - totalGoalsReceived;
    }

    public void updateResults(int homeGoals, int awayGoals) {
        playedGames++;
        if (homeGoals > awayGoals) wins++;
        else if (homeGoals == awayGoals) draws++;
        else loses++;
        totalGoalsScored += homeGoals;
        totalGoalsReceived += awayGoals;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%-15s%5d%5d%5d%5d%5d", name, playedGames, wins, draws, loses, totalPoints());
    }
}

class FootballTable {
    Map<String, Team> teams;

    public FootballTable() {
        this.teams = new LinkedHashMap<>();
    }

    public void addGame(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        teams.putIfAbsent(homeTeam, new Team(homeTeam));
        teams.putIfAbsent(awayTeam, new Team(awayTeam));
        teams.get(homeTeam).updateResults(homeGoals, awayGoals);
        teams.get(awayTeam).updateResults(awayGoals, homeGoals);
    }

    public void printTable() {
        AtomicInteger i = new AtomicInteger(0);
        teams.values().stream()
                .sorted(Comparator.comparing(Team::totalPoints).thenComparing(Team::goalDifference).reversed().thenComparing(Team::getName))
                .forEach(team -> System.out.printf("%2d. %s\n", (i.incrementAndGet()), team.toString()));

    }

}

public class FootballTableTest {
    public static void main(String[] args) throws IOException {
        FootballTable table = new FootballTable();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines()
                .map(line -> line.split(";"))
                .forEach(parts -> table.addGame(parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
        reader.close();
        System.out.println("=== TABLE ===");
        System.out.printf("%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS");
        table.printTable();
    }
}



