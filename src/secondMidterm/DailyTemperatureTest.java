package secondMidterm;

import java.io.*;
import java.util.*;

class Temperature {
    double temp;
    char scale;

    public Temperature(double temp, char scale) {
        this.temp = temp;
        this.scale = scale;
    }

    public void changeScale(char c) {
        if (scale == c) return;
        if (scale == 'F') {
            temp = (temp - 32) * 5 / 9;
            scale = 'C';
        } else if (scale == 'C') {
            temp = temp * 9 / 5 + 32;
            scale = 'F';
        }
    }
}

class DailyTemperatures {
    Map<Integer, List<Temperature>> dailyTemperatures;

    public DailyTemperatures() {
        this.dailyTemperatures = new TreeMap<>();
    }

    public void readTemperatures(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        br.lines().forEach(line -> {
            String[] parts = line.split("\\s+");
            int day = Integer.parseInt(parts[0]);
            List<Temperature> temperatures = new ArrayList<>();
            Arrays.stream(parts).skip(1)
                    .forEach(part -> {
                        int temp = Integer.parseInt(part.substring(0, part.length() - 1));
                        char c = part.charAt(part.length() - 1);
                        temperatures.add(new Temperature(temp, c));
                    });
            dailyTemperatures.putIfAbsent(day, new ArrayList<>());
            dailyTemperatures.get(day).addAll(temperatures);
        });
    }

    public void changeScale(char c) {
        dailyTemperatures.values().stream().flatMap(Collection::stream)
                .forEach(temp -> temp.changeScale(c));
    }

    public void writeDailyStats(OutputStream out, char c) {
        PrintWriter pw = new PrintWriter(out);
        changeScale(c);
        dailyTemperatures.forEach((key, value) -> {
            DoubleSummaryStatistics dss = new DoubleSummaryStatistics();
            value.forEach(temp -> dss.accept(temp.temp));
            pw.println(String.format("%3d: Count: %3d Min: %6.2f%c Max: %6.2f%c Avg: %6.2f%c",
                    key, dss.getCount(), dss.getMin(), c, dss.getMax(), c, dss.getAverage(), c));
        });
        pw.flush();
    }
}

public class DailyTemperatureTest {
    public static void main(String[] args) {
        DailyTemperatures dailyTemperatures = new DailyTemperatures();
        dailyTemperatures.readTemperatures(System.in);
        System.out.println("=== Daily temperatures in Celsius (C) ===");
        dailyTemperatures.writeDailyStats(System.out, 'C');
        System.out.println("=== Daily temperatures in Fahrenheit (F) ===");
        dailyTemperatures.writeDailyStats(System.out, 'F');
    }
}

