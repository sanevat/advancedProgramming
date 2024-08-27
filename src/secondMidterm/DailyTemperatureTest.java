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

    public double getTemp() {
        return temp;
    }

    public void convertInC(char c) {
        if (scale == c) return;
        if (scale == 'F') {
            this.temp = (temp - 32) * 5 / 9.0;
            scale = 'C';
        } else if (scale == 'C') {
            this.temp = (temp * 9) / 5.0 + 32;
            scale = 'F';
        }

    }
}

class DailyTemperatures {
    Map<Integer, List<Temperature>> temperaturesPerDay;

    public DailyTemperatures() {
        this.temperaturesPerDay = new TreeMap<>();
    }

    public void readTemperatures(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        br.lines()
                .forEach(line -> {
                    String[] parts = line.split("\\s+");
                    int day = Integer.parseInt(parts[0]);
                    List<Temperature> temperatures = new ArrayList<>();
                    Arrays.stream(parts)
                            .skip(1)
                            .forEach(part -> temperatures.add(
                                    new Temperature(Integer.parseInt(part.substring(0, part.length() - 1)), part.charAt(part.length() - 1))
                            ));

                    temperaturesPerDay.putIfAbsent(day, new ArrayList<>());
                    temperaturesPerDay.put(day, temperatures);
                });
    }

    public double avgTemperature(List<Temperature> temps) {
        return temps.stream().mapToDouble(Temperature::getTemp).average().orElse(0.0);
    }

    public void writeDailyStats(OutputStream out, char c) {
        PrintWriter pw = new PrintWriter(out);
        temperaturesPerDay.forEach((key, value) -> {
            value.forEach(val -> val.convertInC(c));
            List<Temperature> sortedValue = value.stream()
                    .sorted(Comparator.comparing(Temperature::getTemp))
                    .toList();
            pw.printf("%3d: Count: %3d Min: %6.2f%c Max: %6.2f%c Avg: %6.2f%c\n",
                    key, value.size(), sortedValue.get(0).temp, c, sortedValue.get(sortedValue.size() - 1).temp,
                    c, avgTemperature(value), c);
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

