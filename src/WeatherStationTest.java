import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

class Condition implements Comparable<Condition> {
    private double temperature;
    private double humidity;
    private double wind;
    private double visibility;
    Date time;

    public Condition(double temperature, double humidity, double wind, double visibility, Date time) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
        this.visibility = visibility;
        this.time = time;
    }

    public Date getDate() {
        return time;
    }

    public double getTemperature() {
        return temperature;
    }

    @Override
    public String toString() {
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return String.format("%.1f %.1f km/h %.1f%% %.1f km %s", temperature, wind, humidity, visibility
                , df.format(time));
    }

    @Override
    public int compareTo(Condition o) {
        return this.getDate().compareTo(o.getDate());
    }
}

class WeatherStation {

    private List<Condition> conditions;
    private int days;

    public WeatherStation(int days) {
        this.days = days;
        this.conditions = new ArrayList<>();
    }

    public int total() {
        return conditions.size();
    }

    public void deleteDates(Date date) {
        long maxDays = days * 24L * 60 * 60 * 1000;
        for (int i = conditions.size() - 1; i >= 0; i--) {
            if (date.getTime() - conditions.get(i).getDate().getTime() > maxDays)
                conditions.remove(i);
        }
    }

    public boolean isValidDate(Date date) {
        return conditions.stream()
                .noneMatch(cond -> date.getTime() - cond.getDate().getTime() <= 2.5 * 60 * 1000);
    }

    public void addMeasurment(float temp, float wind, float hum, float vis, Date date) {
        if (isValidDate(date)) {
            deleteDates(date);
            conditions.add(new Condition(temp, hum, wind, vis, date));
        }

    }

    public double average() {
        return conditions.stream().mapToDouble(Condition::getTemperature).average().orElse(0.0);
    }

    public void status(Date from, Date to) {
        conditions = conditions.stream()
                .sorted()
                .filter(cond -> !cond.getDate().before(from) && !cond.getDate().after(to))
                .collect(Collectors.toList());

        if (conditions.isEmpty()) throw new RuntimeException();

        conditions.forEach(System.out::println);
        System.out.printf("Average temperature: %.2f", average());

    }
}

public class WeatherStationTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        int n = scanner.nextInt();
        scanner.nextLine();
        WeatherStation ws = new WeatherStation(n);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }
            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);
            line = scanner.nextLine();
            Date date = df.parse(line);
            ws.addMeasurment(temp, wind, hum, vis, date);
        }
        String line = scanner.nextLine();
        Date from = df.parse(line);
        line = scanner.nextLine();
        Date to = df.parse(line);
        scanner.close();
        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}