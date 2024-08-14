import java.util.Scanner;

class ZeroDenominatorException extends Exception {
    public ZeroDenominatorException(String message) {
        super(message);
    }
}

class GenericFraction<T extends Number, U extends Number> {
    private T numerator;
    private U denominator;

    public GenericFraction(T numerator, U denominator) throws ZeroDenominatorException {
        this.numerator = numerator;
        if (denominator.equals(0)) {
            throw new ZeroDenominatorException("Denominator cannot be zero");
        }
        this.denominator = denominator;
    }

    GenericFraction<Double, Double> add(GenericFraction<? extends Number, ? extends Number> gf) throws ZeroDenominatorException {
        int lcd = calculateLCD(denominator.intValue(), gf.denominator.intValue());

        int adjustedNumerator1 = lcd / denominator.intValue() * numerator.intValue();
        int adjustedNumerator2 = lcd / gf.denominator.intValue() * gf.numerator.intValue();

        double sumNumerator = adjustedNumerator1 + adjustedNumerator2;
        double sumDenominator = lcd;

        return simplifyFraction(sumNumerator, sumDenominator);
    }

    public GenericFraction<Double, Double> simplifyFraction(double numerator, double denominator) throws ZeroDenominatorException {
        for (int i = (int) Math.max(numerator, denominator); i >= 2; i--) {
            if (numerator % i == 0 && denominator % i == 0) {
                numerator /= i;
                denominator /= i;
            }
        }
        return new GenericFraction<>(numerator, denominator);
    }

    public int calculateLCD(int num, int dem) {
        int newDem = Math.max(num, dem);
        for (int i = newDem; i <= num * dem; i++) {
            if (i % num == 0 && i % dem == 0) {
                return i;
            }
        }
        return newDem;
    }

    public double toDouble() {
        return (double) numerator.intValue() / denominator.intValue();
    }

    @Override
    public String toString() {
        return String.format("%.2f / %.2f", numerator.doubleValue(), denominator.doubleValue());
    }
}

public class GenericFractionTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double n1 = scanner.nextDouble();
        double d1 = scanner.nextDouble();
        float n2 = scanner.nextFloat();
        float d2 = scanner.nextFloat();
        int n3 = scanner.nextInt();
        int d3 = scanner.nextInt();
        try {
            GenericFraction<Double, Double> gfDouble = new GenericFraction<Double, Double>(n1, d1);
            GenericFraction<Float, Float> gfFloat = new GenericFraction<Float, Float>(n2, d2);
            GenericFraction<Integer, Integer> gfInt = new GenericFraction<Integer, Integer>(n3, d3);
            System.out.printf("%.2f\n", gfDouble.toDouble());
            System.out.println(gfDouble.add(gfFloat));
            System.out.println(gfInt.add(gfFloat));
            System.out.println(gfDouble.add(gfInt));
            gfInt = new GenericFraction<Integer, Integer>(n3, 0);
        } catch (ZeroDenominatorException e) {
            System.out.println(e.getMessage());
        }

        scanner.close();
    }

}
