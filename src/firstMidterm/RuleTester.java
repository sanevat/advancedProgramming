import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class Student {
    String id;
    List<Integer> grades;

    public Student(String id, List<Integer> grades) {
        this.id = id;
        this.grades = grades;
    }

    public static Student create(String line) {
        String[] parts = line.split("\\s+");
        String id = parts[0];
        List<Integer> grades = Arrays.stream(parts).skip(1).map(Integer::parseInt).collect(Collectors.toList());
        return new Student(id, grades);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", grades=" + grades +
                '}';
    }
}

class Rule<IN, OUT> {
    private Predicate<IN> predicate;
    private Function<IN, OUT> function;

    public Rule(Predicate<IN> predicate, Function<IN, OUT> function) {
        this.predicate = predicate;
        this.function = function;
    }

    public Optional<OUT> apply(IN input) {
        if (predicate.test(input))
            return Optional.of(function.apply(input));
        return Optional.empty();
    }
}

class RuleProcessor {

    public static <IN, OUT> void process(List<IN> inputs, List<Rule<IN, OUT>> rules) {
        inputs.forEach(in -> {
            System.out.println("Input:" + in);
            rules.forEach(rule -> {
                Optional<OUT> result = rule.apply(in);
                if (result.isPresent())
                    System.out.println("Result: " + result.get());
                else
                    System.out.println("Condition not met");
            });
        });
    }
}

public class RuleTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase == 1) { //Test for String,Integer
            List<Rule<String, Integer>> rules = new ArrayList<>();

            rules.add(new Rule<>(str -> str.contains("NP"), str -> str.indexOf("NP")));
            rules.add(new Rule<>(str -> str.startsWith("NP"), String::length));


            List<String> inputs = new ArrayList<>();
            while (sc.hasNext()) {
                inputs.add(sc.nextLine());
            }

            RuleProcessor.process(inputs, rules);


        } else { //Test for Student, Double
            List<Rule<Student, Double>> rules = new ArrayList<>();

            rules.add(new Rule<>(student -> student.grades.size() >= 3, student -> student.grades.stream().mapToDouble(gr -> gr).max().getAsDouble()));

            rules.add(new Rule<>(student -> student.id.startsWith("20"), student -> student.grades.stream().mapToDouble(grad -> grad).average().orElse(5.0)));

            List<Student> students = new ArrayList<>();
            while (sc.hasNext()) {
                students.add(Student.create(sc.nextLine()));
            }

            RuleProcessor.process(students, rules);
        }
    }
}
