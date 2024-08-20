package secondMidterm;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

class Item {
    private final String reason;
    private final int price;

    public Item(String reason, int price) {
        this.reason = reason;
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("%s %d", reason, price);
    }

    public int getPrice() {
        return price;
    }
}

class OnlinePayments {
    private final Map<String, List<Item>> studentPayments = new HashMap<>();

    public void readItems(InputStream in) {
        new BufferedReader(new InputStreamReader(in))
                .lines()
                .forEach(line -> {
                    String[] parts = line.split(";");
                    String studentId = parts[0];
                    String reason = parts[1];
                    int price = Integer.parseInt(parts[2]);

                    studentPayments.computeIfAbsent(studentId, k -> new ArrayList<>())
                            .add(new Item(reason, price));
                });
    }

    public void printStudentReport(String studentId, OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        List<Item> items = studentPayments.get(studentId);

        if (items == null) {
            System.out.printf("Student %s not found!%n", studentId);
            return;
        }

        int net = calculateNet(items);
        int fee = calculateFee(net);

        pw.printf("Student: %s Net: %d Fee: %d Total: %d%nItems:%n", studentId, net, fee, net + fee);
        AtomicInteger itemNumber = new AtomicInteger(1);

        items.stream()
                .sorted(Comparator.comparing(Item::getPrice).reversed())
                .forEach(item -> pw.printf("%d. %s%n", itemNumber.getAndIncrement(), item));

        pw.flush();
    }

    private int calculateNet(List<Item> items) {
        return items.stream().mapToInt(Item::getPrice).sum();
    }

    private int calculateFee(int net) {
        int fee = (int) Math.round(net * 1.14 / 100.0);
        return Math.min(300, Math.max(3, fee));
    }
}

public class OnlinePaymentsTest {
    public static void main(String[] args) {
        OnlinePayments onlinePayments = new OnlinePayments();
        onlinePayments.readItems(System.in);

        IntStream.range(151020, 151025)
                .mapToObj(String::valueOf)
                .forEach(id -> onlinePayments.printStudentReport(id, System.out));
    }
}
