import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
class MinMax<T extends Comparable<T>> {
    private T min;
    private T max;
    private List<T> list;

    public MinMax() {
        this.list=new ArrayList<>();
    }

    public void update(T elem){
        list.add(elem);
        this.min=list.stream()
                .min(Comparator.naturalOrder())
                .orElse(elem);
        this.max=list.stream()
                .max(Comparator.naturalOrder())
                .orElse(elem);
    }

    public T min() {
        return min;
    }

    public T max() {
        return max;
    }

    @Override
    public String toString() {
        long numDiff = list.stream().filter(elem -> !elem.equals(min) && !elem.equals(max)).count();
        return min+" "+max+" "+numDiff;
    }
}

public class MinAndMax {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        MinMax<String> strings = new MinMax<String>();
        for(int i = 0; i < n; ++i) {
            String s = scanner.next();
            strings.update(s);
        }
        System.out.println(strings);
        MinMax<Integer> ints = new MinMax<Integer>();
        for(int i = 0; i < n; ++i) {
            int x = scanner.nextInt();
            ints.update(x);
        }
        System.out.println(ints);
    }
}