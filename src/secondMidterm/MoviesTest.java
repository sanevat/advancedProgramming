package secondMidterm;

import java.util.*;
import java.util.stream.Collectors;

class Movie{
    String title;
    List<Integer> ratings;

    public Movie(String title, List<Integer> ratings) {
        this.title = title;
        this.ratings = ratings;
    }

    public String getTitle() {
        return title;
    }

    public double ratingGrade(){
        return ratings.stream().mapToInt(rat->rat).average().orElse(0.0);
    }

    public int ratingsSize() {
        return ratings.size();
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f) of %d ratings",title, ratingGrade(),ratings.size());
    }
}
class MoviesList{
    List<Movie>movies;

    public MoviesList() {
        this.movies=new ArrayList<>();
    }
    public void addMovie(String title, int[]ratings){
        Movie m=new Movie(title, Arrays.stream(ratings).boxed().collect(Collectors.toList()));
        movies.add(m);
    }
    public List<Movie>top10ByAvgRating(){
        return movies.stream()
                .sorted(Comparator.comparing(Movie::ratingGrade)
                        .reversed()
                        .thenComparing(Movie::getTitle))
                .limit(10)
                .collect(Collectors.toList());
    }
    public double ratingCoef(Movie m){
        int maxMovieRatingsSize= movies.stream().max(Comparator.comparing(Movie::ratingsSize)).get().ratingsSize();
        return m.ratingGrade() * m.ratings.size() /  maxMovieRatingsSize;
    }
    public List<Movie>top10ByRatingCoef(){
        return movies.stream()
                .sorted(Comparator
                        .comparingDouble(this::ratingCoef)
                        .reversed()
                        .thenComparing(Movie::getTitle))
                .limit(10)
                .collect(Collectors.toList());
    }
}
public class MoviesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoviesList moviesList = new MoviesList();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int x = scanner.nextInt();
            int[] ratings = new int[x];
            for (int j = 0; j < x; ++j) {
                ratings[j] = scanner.nextInt();
            }
            scanner.nextLine();
            moviesList.addMovie(title, ratings);
        }
        scanner.close();
        List<Movie> movies = moviesList.top10ByAvgRating();
        System.out.println("=== TOP 10 BY AVERAGE RATING ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        movies = moviesList.top10ByRatingCoef();
        System.out.println("=== TOP 10 BY RATING COEFFICIENT ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }
}
