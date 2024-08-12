import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class UnsupportedFormatException extends Exception {
    public UnsupportedFormatException(String message) {
        super(message);
    }
}

class InvalidTimeException extends Exception {
    public InvalidTimeException(String message) {
        super(message);
    }
}

enum TimeFormat {
    FORMAT_24, FORMAT_AMPM
}

class Time implements Comparable<Time> {
    int hour;
    int minute;

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public long totalMinutes() {
        return hour * 60 + minute;
    }

    @Override
    public String toString() {
        return String.format("%2d:%02d", hour, minute);
    }

    @Override
    public int compareTo(Time o) {
        return Long.compare(this.totalMinutes(), o.totalMinutes());
    }
}

class TimeTable {
    List<Time> times;

    public TimeTable() {
        this.times = new ArrayList<>();
    }

    public void readTimes(InputStream in) throws UnsupportedFormatException, InvalidTimeException {
        Scanner sc = new Scanner(in);
        while (sc.hasNext()) {
            String read = sc.next();
            String[] parts;
            if (read.contains(":")) {
                parts = read.split(":");
            } else if (read.contains(".")) {
                parts = read.split("\\.");
            } else
                throw new UnsupportedFormatException(read);

            int hour = Integer.parseInt(parts[0]), minute = Integer.parseInt(parts[1]);

            if (hour < 0 || minute < 0 || minute > 59 || hour > 23)
                throw new InvalidTimeException(read);

            times.add(new Time(hour, minute));
        }
    }

    public void convertAMPM(PrintWriter pw) {
        for (Time t : times) {
            int minute = t.getMinute(), hour = t.getHour();
            if (hour == 0) pw.println(new Time(12, minute) + " AM");
            else if (hour == 12) pw.println(t + " PM");
            else if (hour >= 13 && hour < 24) pw.println(new Time(hour - 12, minute) + " PM");
            else if (hour >= 1 && hour < 12) pw.println(t + " AM");
        }
    }

    public void writeTimes(OutputStream out, TimeFormat format) {
        times.sort(Comparator.naturalOrder());
        PrintWriter pw = new PrintWriter(out);
        if (format.equals(TimeFormat.FORMAT_AMPM)) convertAMPM(pw);
        else times.forEach(time -> pw.println(time.toString()));
        pw.flush();
    }
}

public class TimesTest {
    public static void main(String[] args) {
        TimeTable timeTable = new TimeTable();
        try {
            timeTable.readTimes(System.in);
        } catch (UnsupportedFormatException e) {
            System.out.println("UnsupportedFormatException: " + e.getMessage());
        } catch (InvalidTimeException e) {
            System.out.println("InvalidTimeException: " + e.getMessage());
        }
        System.out.println("24 HOUR FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_24);
        System.out.println("AM/PM FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_AMPM);
    }
}