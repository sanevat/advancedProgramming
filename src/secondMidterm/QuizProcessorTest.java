package secondMidterm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.IntStream;

class QuizesNotSameSizeException extends Exception {
    public QuizesNotSameSizeException(String message) {
        super(message);
    }
}

class Quiz {
    String id;
    List<String> answers;
    List<String> correctAnswers;

    public Quiz(String id, List<String> answers, List<String> correctAnswers) {
        this.id = id;
        this.answers = answers;
        this.correctAnswers = correctAnswers;
    }

    public double calculateTotalPoints() throws QuizesNotSameSizeException {
        if (correctAnswers.size() != answers.size())
            throw new QuizesNotSameSizeException("A quiz must have same number of correct and selected answers");
        return IntStream.range(0, answers.size())
                .mapToDouble(i -> answers.get(i).equals(correctAnswers.get(i)) ? 1 : -0.25)
                .sum();
    }
}

class QuizProcessor {
    static Map<String, Double> processAnswers(InputStream is) {
        Map<String, Double> pointsDistribution = new TreeMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        br.lines().forEach(line -> {
            Quiz q = generateQuiz(line);
            try {
                pointsDistribution.put(q.id, q.calculateTotalPoints());
            } catch (QuizesNotSameSizeException e) {
                System.out.println(e.getMessage());
            }
        });
        return pointsDistribution;
    }

    public static Quiz generateQuiz(String line) {
        String[] parts = line.split(";");
        String id = parts[0];
        List<String> answers = new ArrayList<>(Arrays.asList(parts[1].split(",\\s+")));
        List<String> correct = new ArrayList<>(Arrays.asList(parts[2].split(",\\s+")));
        return new Quiz(id, answers, correct);
    }
}

public class QuizProcessorTest {
    public static void main(String[] args) {
        QuizProcessor.processAnswers(System.in).forEach((id, points) ->
                System.out.printf("%s -> %.2f%n", id, points)
        );
    }
}