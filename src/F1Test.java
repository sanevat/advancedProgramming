import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class TimeClass implements Comparable<TimeClass> {
    private int minutes;
    private int seconds;
    private int miliseconds;

    public TimeClass(int minutes, int seconds, int miliseconds) {
        this.minutes = minutes;
        this.seconds = seconds;
        this.miliseconds = miliseconds;
    }

    public long totalMiliSeconds() {
        return minutes * 60 * 1000 + seconds * 1000 + miliseconds;
    }

    @Override
    public int compareTo(TimeClass o) {
        return Long.compare(this.totalMiliSeconds(), o.totalMiliSeconds());
    }

    @Override
    public String toString() {
        return String.format("%d:%02d:%03d", minutes, seconds, miliseconds);
    }
}

class Driver implements Comparable<Driver> {
    private String name;
    private List<TimeClass> times;

    public Driver(String name, List<TimeClass> times) {
        this.name = name;
        this.times = times;
    }

    public static Driver createDriver(String line) {
        String[] parts = line.split("\\s+");
        String name = parts[0];

        List<TimeClass> times = new ArrayList<>();
        Arrays.stream(parts)
                .skip(1)
                .forEach(timeInput -> {
                    String[] timeParts = timeInput.split(":");
                    int minute = Integer.parseInt(timeParts[0]);
                    int second = Integer.parseInt(timeParts[1]);
                    int mili = Integer.parseInt(timeParts[2]);
                    times.add(new TimeClass(minute, second, mili));
                });

        return new Driver(name, times);
    }

    public TimeClass getMaxTime() {
        times = times.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        return times.get(0);
    }

    @Override
    public String toString() {
        return String.format("%-10s %s", name, getMaxTime().toString());
    }

    @Override
    public int compareTo(Driver o) {
        return Long.compare(this.getMaxTime().totalMiliSeconds(), o.getMaxTime().totalMiliSeconds());
    }
}

class F1Race {
    List<Driver> drivers;

    public F1Race() {
        this.drivers = new ArrayList<>();
    }

    public void readResults(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        drivers = br.lines()
                .map(Driver::createDriver)
                .collect(Collectors.toList());
    }

    public void printSorted(PrintStream out) {
        PrintWriter pw = new PrintWriter(out);
        drivers.sort(Comparator.naturalOrder());
        int id = 1;
        for (Driver d : drivers) {
            pw.println(id + ". " + d.toString());
            id++;
        }
        pw.flush();
    }
}

public class F1Test {

    public static void main(String[] args) {
        F1Race f1Race = new F1Race();
        f1Race.readResults(System.in);
        f1Race.printSorted(System.out);
    }
}
