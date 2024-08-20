import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum TimeFormat1 {
    FORMAT_24, FORMAT_AMPM
}

class UnsupportedFormatException1 extends Exception {
    public UnsupportedFormatException1(String message) {
        super(message);
    }
}

class InvalidTimeException1 extends Exception {
    public InvalidTimeException1(String message) {
        super(message);
    }
}

class TimeTable1 {
    List<LocalTime> times;
    private final DateTimeFormatter f1 = DateTimeFormatter.ofPattern("H:mm");
    private final DateTimeFormatter f2 = DateTimeFormatter.ofPattern("H.mm");
    private final DateTimeFormatter f3 = DateTimeFormatter.ofPattern("h:mm a");

    public TimeTable1() {
        this.times = new ArrayList<>();
    }

    public void readTimes(InputStream in) throws UnsupportedFormatException1, InvalidTimeException1 {
        Scanner sc = new Scanner(in);
        while (sc.hasNext()) {
            String timeInput = sc.next();
            LocalTime time;

            if (timeInput.contains(":"))
                time = LocalTime.parse(timeInput, f1);
            else if (timeInput.contains("."))
                time = LocalTime.parse(timeInput, f2);
            else throw new UnsupportedFormatException1(timeInput);

            if (!isValidTime(time)) throw new InvalidTimeException1(timeInput);
            times.add(time);
        }
    }

    public boolean isValidTime(LocalTime time) {
        int hour = time.getHour();
        int minute = time.getMinute();
        return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
    }

    public void printFormatted(PrintWriter pw, DateTimeFormatter f, LocalTime time) {
        String newTime = f.format(time);
        String hours = newTime.split(":")[0];
        if (hours.length() < 2)
            pw.println(" " + newTime);
        else
            pw.println(newTime);
    }

    public void writeTimes(OutputStream out, TimeFormat1 format) {
        PrintWriter pw = new PrintWriter(out);
        DateTimeFormatter formatter = (format.equals(TimeFormat1.FORMAT_24) ? f1 : f3);
        times.stream()
                .sorted()
                .forEach(time -> printFormatted(pw, formatter, time));
        pw.flush();
    }
}

public class TimesTest1 {
    public static void main(String[] args) {
        TimeTable1 timeTable = new TimeTable1();
        try {
            timeTable.readTimes(System.in);
        } catch (UnsupportedFormatException1 e) {
            System.out.println("UnsupportedFormatException: " + e.getMessage());
        } catch (InvalidTimeException1 e) {
            System.out.println("InvalidTimeException: " + e.getMessage());
        }
        System.out.println("24 HOUR FORMAT");
        timeTable.writeTimes(System.out, TimeFormat1.FORMAT_24);
        System.out.println("AM/PM FORMAT");
        timeTable.writeTimes(System.out, TimeFormat1.FORMAT_AMPM);
    }
}