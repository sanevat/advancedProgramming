import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;


class Line {
    Double coeficient;
    Double x;
    Double intercept;

    public Line(Double coeficient, Double x, Double intercept) {
        this.coeficient = coeficient;
        this.x = x;
        this.intercept = intercept;
    }

    public static Line createLine(String line) {
        String[] parts = line.split("\\s+");
        return new Line(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2])
        );
    }

    public double calculateLine() {
        return coeficient * x + intercept;
    }

    @Override
    public String toString() {
        return String.format("%.2f * %.2f + %.2f", coeficient, x, intercept);
    }
}

class Equation<IN, OUT> {
    private Supplier<IN> supplier;
    private Function<IN, OUT> function;

    public Equation(Supplier<IN> supplier, Function<IN, OUT> function) {
        this.supplier = supplier;
        this.function = function;
    }

    public Optional<OUT> calculate() {
        return Optional.of(function.apply(supplier.get()));
    }
}

class EquationProcessor {
    public static <IN, OUT> void process(List<IN> input, List<Equation<IN, OUT>> equations) {
        input.forEach(in -> System.out.println("Input: " + in));
        equations.forEach(eq -> {
            if (eq.calculate().isPresent())
                System.out.println("Result: " + eq.calculate().get());
        });
    }

}

public class EquationTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase == 1) { // Testing with Integer, Integer
            List<Equation<Integer, Integer>> equations1 = new ArrayList<>();
            List<Integer> inputs = new ArrayList<>();
            while (sc.hasNext()) {
                inputs.add(Integer.parseInt(sc.nextLine()));
            }

            equations1.add(new Equation<>(() -> inputs.get(2), num -> num + 1000));

            equations1.add(new Equation<>(() -> inputs.get(3), num -> Math.max(num, 100)));
            EquationProcessor.process(inputs, equations1);

        } else { // Testing with Line, Integer
            List<Equation<Line, Double>> equations2 = new ArrayList<>();
            List<Line> inputs = new ArrayList<>();
            while (sc.hasNext()) {
                inputs.add(Line.createLine(sc.nextLine()));
            }

            equations2.add(new Equation<>(() -> inputs.get(1), Line::calculateLine));

            equations2.add(new Equation<>(() -> inputs.get(0), line -> inputs.stream().filter(in -> line.calculateLine() < in.calculateLine()).mapToDouble(Line::calculateLine).sum()));
            EquationProcessor.process(inputs, equations2);
        }
    }
}
