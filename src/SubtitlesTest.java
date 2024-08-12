import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Subtitle {
    private int id;
    private LocalTime start;
    private LocalTime end;
    private String text;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss,SSS");

    public Subtitle(int id, LocalTime start, LocalTime end, String text) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.text = text;
    }

    public static Subtitle createSubtitle(List<String> input) {
        int id = Integer.parseInt(input.get(0));
        String[] times = input.get(1).split("\\s+-->\\s+");
        LocalTime start = LocalTime.parse(times[0], dtf);
        LocalTime end = LocalTime.parse(times[1], dtf);

        String text = input.stream()
                .skip(2)
                .collect(Collectors.joining("\n"));

        return new Subtitle(id, start, end, text);
    }

    @Override
    public String toString() {
        String startTime = dtf.format(start);
        String endTime = dtf.format(end);
        return String.format("%d\n%s-->%s\n%s\n", id, startTime, endTime, text);
    }

    public void setTime(int shift) {
        this.start = start.plusNanos(shift * 1000000L);
        this.end = end.plusNanos(shift * 1000000L);
    }
}

class Subtitles {
    private List<Subtitle> subtitles;

    public Subtitles() {
        this.subtitles = new ArrayList<>();
    }

    public int loadSubtitles(InputStream in) {
        Scanner sc = new Scanner(in);
        List<String> readSubtitle = new ArrayList<>();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!line.isEmpty()) {
                readSubtitle.add(line);
            } else {
                Subtitle s = Subtitle.createSubtitle(readSubtitle);
                subtitles.add(s);
                readSubtitle = new ArrayList<>();
            }
        }

        if (!readSubtitle.isEmpty()) {
            Subtitle s = Subtitle.createSubtitle(readSubtitle);
            subtitles.add(s);
        }

        return subtitles.size();
    }

    public void print() {
        subtitles.forEach(sub -> System.out.println(sub.toString()));
    }

    public void shift(int shift) {
        subtitles.forEach(sub -> sub.setTime(shift));
    }
}

public class SubtitlesTest {

    public static void main(String[] args) throws ParseException {
        Subtitles subtitles = new Subtitles();
        int n = subtitles.loadSubtitles(System.in);
        System.out.println("+++++ ORIGINIAL SUBTITLES +++++");
        subtitles.print();
        int shift = n * 37;
        shift = (shift % 2 == 1) ? -shift : shift;
        System.out.printf("SHIFT FOR %d ms%n", shift);
        subtitles.shift(shift);
        System.out.println("+++++ SHIFTED SUBTITLES +++++");
        subtitles.print();
    }
}
