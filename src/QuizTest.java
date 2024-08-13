import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

class InvalidOperationException extends Exception {
    public InvalidOperationException(String answer) {
        super(String.format("%s is not allowed option for this question", answer));
    }

    public InvalidOperationException() {
        super("Answers and questions must be of same length!");
    }
}

enum TypeQuestion {
    MC, TF
}

abstract class Question implements Comparable<Question> {
    protected String text;
    protected int points;
    protected String answer;

    public Question(String text, int points, String answer) {
        this.text = text;
        this.points = points;
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public int compareTo(Question o) {
        return Integer.compare(this.points, o.points);
    }
    abstract TypeQuestion getType();
}
class MultipleChoiceQuestion extends Question {
    TypeQuestion type;

    public MultipleChoiceQuestion(String text, int points, String answer) {
        super(text, points, answer);
        this.type = TypeQuestion.MC;
    }

    @Override
    public String toString() {
        return "Multiple Choice Question: " + text + " Points " + points + " Answer: " + answer;
    }

    public TypeQuestion getType() {
        return type;
    }
}

class TrueFalseQuestion extends Question {
    TypeQuestion type;

    public TrueFalseQuestion(String text, int points, String answer) {
        super(text, points, answer);
        this.type = TypeQuestion.TF;
    }

    @Override
    public String toString() {
        return "True/False Question: " + text + " Points: " + points + " Answer: " + answer;
    }

    public TypeQuestion getType() {
        return type;
    }
}

class Quiz {
    List<Question> questions;

    public Quiz() {
        this.questions = new ArrayList<>();
    }

    public void addQuestion(String s) throws InvalidOperationException {
        String[] parts = s.split(";");
        String text = parts[1];
        int points = Integer.parseInt(parts[2]);
        String answer = parts[3];
        Question q = ((parts[0].equals("MC")) ?
                new MultipleChoiceQuestion(text, points, answer) :
                new TrueFalseQuestion(text, points, answer));

        if (q.getType().equals(TypeQuestion.MC) && !checkValidQuestion(q)) {
            throw new InvalidOperationException(q.getAnswer());
        }

        questions.add(q);

    }
    public boolean checkValidQuestion(Question q) {
        return Arrays.stream(new String[]{"A", "B", "C", "D", "E"})
                .anyMatch(el -> el.equals(q.getAnswer()));
    }

    public void printQuiz(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        questions.stream().sorted(Comparator.reverseOrder())
                .forEach(pw::println);
        pw.flush();
    }

    public void answerQuiz(List<String> answers, OutputStream out) throws InvalidOperationException {
        PrintWriter pw = new PrintWriter(out);
        if (answers.size() != questions.size()) throw new InvalidOperationException();
        double total = 0;
        for (int i = 0; i < questions.size();i++) {
            Question q = questions.get(i);
            String a = answers.get(i);

            double points = q.answer.equals(a)
                    ? q.points
                    : (q instanceof TrueFalseQuestion) ? 0 : -0.2 * q.points;

            pw.println(String.format("%d. %.2f", (i+1), (double) q.points));
            total += q.points;
        }

        pw.println(String.format("Total points: %.2f", total));
        pw.flush();
    }
}

public class QuizTest {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Quiz quiz = new Quiz();

        int questions = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < questions; i++) {
            try {
                quiz.addQuestion(sc.nextLine());
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
                ;
            }
        }

        List<String> answers = new ArrayList<>();

        int answersCount = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < answersCount; i++) {
            answers.add(sc.nextLine());
        }

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase == 1) {
            quiz.printQuiz(System.out);
        } else if (testCase == 2) {
            try {
                quiz.answerQuiz(answers, System.out);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}
