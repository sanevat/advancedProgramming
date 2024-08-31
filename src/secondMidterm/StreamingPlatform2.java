package secondMidterm;

import java.util.*;
import java.util.stream.Collectors;

class CosineSimilarityCalculator2 {

    public static double cosineSimilarity(Collection<Integer> c1, Collection<Integer> c2) {
        int[] array1;
        int[] array2;
        array1 = c1.stream().mapToInt(i -> i).toArray();
        array2 = c2.stream().mapToInt(i -> i).toArray();
        double up = 0.0;
        double down1 = 0, down2 = 0;

        for (int i = 0; i < c1.size(); i++) {
            up += (array1[i] * array2[i]);
        }

        for (int i = 0; i < c1.size(); i++) {
            down1 += (array1[i] * array1[i]);
        }

        for (int i = 0; i < c1.size(); i++) {
            down2 += (array2[i] * array2[i]);
        }

        return up / (Math.sqrt(down1) * Math.sqrt(down2));
    }
}

abstract class Obj {
    String id;
    String name;

    public Obj(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return Integer.parseInt(id);
    }
}

class User2 extends Obj {
    Map<String, Integer> ratingOfMovie;

    public User2(String id, String name) {
        super(id, name);
        this.ratingOfMovie = new HashMap<>();
    }

    public void addRating(String id, int rating) {
        ratingOfMovie.put(id, rating);
    }

    public List<String> getIdOfFavouriteMovie() {
        int maxRating = ratingOfMovie.values().stream()
                .max(Integer::compareTo)
                .orElse(0);
        return ratingOfMovie.entrySet().stream()
                .filter(entry -> entry.getValue() == maxRating)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public double getSimilarity(User2 u) {
        Set<String> allMovieIds = new HashSet<>(ratingOfMovie.keySet());
        allMovieIds.addAll(u.ratingOfMovie.keySet());
        allMovieIds.forEach(id -> {
            this.ratingOfMovie.putIfAbsent(id, 0);
            u.ratingOfMovie.putIfAbsent(id, 0);
        });
        return CosineSimilarityCalculator2.cosineSimilarity(this.ratingOfMovie.values(), u.ratingOfMovie.values());
    }

    @Override
    public String toString() {
        return String.format("User ID: %s Name: %s", id, name);
    }
}



class Movie2 extends Obj {
    List<Integer> ratings;

    public Movie2(String id, String name) {
        super(id, name);
        this.ratings = new ArrayList<>();
    }

    public double averageRating() {
        return ratings.stream().mapToInt(i->i).average().orElse(0.0);
    }


    @Override
    public String toString() {
        return String.format("Movie ID: %s Title: %s Rating: %.2f", id, name, averageRating());
    }
}

class StreamingPlatform {
    Map<String, User2> users;
    Map<String, Movie2> movies;

    public StreamingPlatform() {
        this.movies = new TreeMap<>();
        this.users = new TreeMap<>();
    }

    public void addMovie(String id, String name) {
        movies.put(id, new Movie2(id, name));
    }

    public void addUser(String id, String username) {
        users.put(id, new User2(id, username));
    }

    public void addRating(String userId, String movieId, int rating) {
        movies.get(movieId).ratings.add(rating);
        users.get(userId).addRating(movieId, rating);
    }

    public void topNMovies(int n) {
        movies.values().stream()
                .sorted(Comparator.comparing(Movie2::averageRating)
                        .reversed())
                .limit(n)
                .forEach(System.out::println);
    }

    public void favouriteMoviesForUsers(List<String> userIds) {
        users.values().stream()
                .filter(user -> userIds.contains(user.id))
                .sorted(Comparator.comparing(User2::getId))
                .forEach(user -> {
                    System.out.println(user);

                    user.getIdOfFavouriteMovie().stream()
                            .map(id -> movies.get(id))
                            .sorted(Comparator.comparing(Movie2::averageRating).reversed())
                            .forEach(System.out::println);

                    System.out.println();
                });
    }

    public void similarUsers(String userId) {
        User2 u = users.get(userId);
        users.values().stream()
                .filter(user -> !user.equals(u))
                .sorted(Comparator.comparing((User2 user) -> u.getSimilarity(user)).reversed())
                .forEach(user -> System.out.println(user + " " + u.getSimilarity(user)));
    }

}


public class StreamingPlatform2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StreamingPlatform sp = new StreamingPlatform();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            switch (parts[0]) {
                case "addMovie" -> {
                    String id = parts[1];
                    String name = Arrays.stream(parts).skip(2).collect(Collectors.joining(" "));
                    sp.addMovie(id, name);
                }
                case "addUser" -> {
                    String id = parts[1];
                    String name = parts[2];
                    sp.addUser(id, name);
                }
                case "addRating" -> {
                    //String userId, String movieId, int rating
                    String userId = parts[1];
                    String movieId = parts[2];
                    int rating = Integer.parseInt(parts[3]);
                    sp.addRating(userId, movieId, rating);
                }
                case "topNMovies" -> {
                    int n = Integer.parseInt(parts[1]);
                    System.out.println("TOP " + n + " MOVIES:");
                    sp.topNMovies(n);
                }
                case "favouriteMoviesForUsers" -> {
                    List<String> users = Arrays.stream(parts).skip(1).collect(Collectors.toList());
                    System.out.println("FAVOURITE MOVIES FOR USERS WITH IDS: " + String.join(", ", users));
                    sp.favouriteMoviesForUsers(users);
                }
                case "similarUsers" -> {
                    String userId = parts[1];
                    System.out.println("SIMILAR USERS TO USER WITH ID: " + userId);
                    sp.similarUsers(userId);
                }
            }
        }
    }
}
