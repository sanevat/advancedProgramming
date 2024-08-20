package secondMidterm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

class QuizProcessor {

    public static Map<String, Double> processAnswers(InputStream in) {
        Map<String, Double> pointsPerStudent = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            br.lines().forEach(line -> processLine(line, pointsPerStudent));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pointsPerStudent;
    }

    private static void processLine(String line, Map<String, Double> pointsPerStudent) {
        String[] parts = line.split(";");
        String id = parts[0];

        if (parts[1].length() != parts[2].length()) {
            throw new IllegalArgumentException("Answer lengths don't match");
        }

        String[] correctAnswers = parts[1].split(",");
        String[] userAnswers = parts[2].split(",");
        double totalPoints = calculateTotalPoints(correctAnswers, userAnswers);

        pointsPerStudent.put(id, totalPoints);
    }

    private static double calculateTotalPoints(String[] correctAnswers, String[] userAnswers) {
        double totalPoints = 0;
        for (int i = 0; i < correctAnswers.length; i++) {
            totalPoints += correctAnswers[i].equals(userAnswers[i]) ? 1 : -0.25;
        }
        return totalPoints;
    }
}

public class QuizProcessorTest {
    public static void main(String[] args) {
        QuizProcessor.processAnswers(System.in).forEach((id, points) ->
                System.out.printf("%s -> %.2f%n", id, points)
        );
    }
}
