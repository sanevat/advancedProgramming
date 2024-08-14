import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

abstract class Stream implements Comparable<Stream> {
    protected String name;
    protected List<String> genres;

    public Stream(String name, List<String> genres) {
        this.name = name;
        this.genres = genres;
    }

    abstract double totalRating();

    @Override
    public int compareTo(Stream o) {
        return Double.compare(this.totalRating(), o.totalRating());
    }
}

class Movie extends Stream {
    private List<Integer> ratings;

    public Movie(String name, List<String> genres, List<Integer> ratings) {
        super(name, genres);
        this.ratings = ratings;
    }

    @Override
    double totalRating() {
        double averageRating = ratings.stream().mapToInt(r -> r).average().orElse(0.0);
        return averageRating * Math.min(ratings.size() / 20.0, 1.0);
    }

    @Override
    public String toString() {
        return String.format("Movie %s %.4f", name, totalRating());
    }
}

class Episode implements Comparable<Episode> {
    private String name;
    private List<Integer> ratings;

    public Episode(String name, List<Integer> ratings) {
        this.name = name;
        this.ratings = ratings;
    }

    double totalRating() {
        double averageRating = ratings.stream().mapToInt(r -> r).average().orElse(0.0);
        return averageRating * Math.min(ratings.size() / 20.0, 1.0);
    }

    @Override
    public int compareTo(Episode o) {
        return Double.compare(this.totalRating(), o.totalRating());
    }

}

class Series extends Stream {
    private List<Episode> episodes;

    public Series(String name, List<String> genres, List<Episode> episodes) {
        super(name, genres);
        this.episodes = episodes;
    }

    @Override
    double totalRating() {
      return episodes.stream()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .mapToDouble(Episode::totalRating)
                .average()
                .orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("TV Show %s %.4f (%d episodes)", name, totalRating(), episodes.size());
    }
}

class StreamingPlatform {
    List<Stream> streams;

    public StreamingPlatform() {
        this.streams = new ArrayList<>();
    }

    public void addItem(String data) {
        String[] parts = data.split(";");
        if (parts.length == 3)
            streams.add(createMovie(parts));
        else
            streams.add(createSeries(parts));
    }

    public static Movie createMovie(String[] parts) {
        String name = parts[0];

        List<String> genres = Arrays.asList(parts[1].split(","));
        List<Integer> ratings = new ArrayList<>();
        Arrays.stream(parts[2].split("\\s+"))
                .forEach(part -> ratings.add(Integer.parseInt(part)));

        return new Movie(name, genres, ratings);
    }

    public static Series createSeries(String[] parts) {

        String name = parts[0];

        List<String> genres = new ArrayList<>();
        Arrays.stream(parts[1].split(","))
                .forEach(genres::add);

        List<Episode> episodes = new ArrayList<>();
        Arrays.stream(parts)
                .skip(2)
                .forEach(part -> {
                    Episode e=createEpisode(part);
                    episodes.add(e);
                });

        return new Series(name, genres, episodes);
    }
    public static Episode createEpisode(String epInput){
        String[] parts = epInput.split("\\s+");
        List<Integer> ratings = new ArrayList<>();
        String episodeName = parts[0];
        Arrays.stream(parts)
                .skip(1)
                .forEach(rat -> ratings.add(Integer.parseInt(rat)));
        return new Episode(episodeName,ratings);

    }

    public void listAllItems(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        streams.stream()
                .sorted(Comparator.reverseOrder())
                .forEach(str -> pw.println(str.toString()));

        pw.flush();
    }

    public void listFromGenre(String genre, OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        streams.stream()
                .sorted(Comparator.reverseOrder())
                .forEach(str -> str.genres.stream()
                        .filter(g -> g.equals(genre))
                        .forEach(g -> pw.println(str)));
        pw.flush();
    }
}

public class StreamingPlatformTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StreamingPlatform sp = new StreamingPlatform();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");
            String method = parts[0];
            String data = Arrays.stream(parts).skip(1).collect(Collectors.joining(" "));
            if (method.equals("addItem")) {
                sp.addItem(data);
            } else if (method.equals("listAllItems")) {
                sp.listAllItems(System.out);
            } else if (method.equals("listFromGenre")) {
                System.out.println(data);
                sp.listFromGenre(data, System.out);

            }
        }

    }
}