import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class AmountNotAllowedException extends Exception {
    public AmountNotAllowedException(long amount) {
        super(String.format("Receipt with amount %d is not allowed to be scanned", amount));
    }
}

enum TaxType {
    A, B, V
}

class Item {
    private int price;
    private TaxType type;
    private static final double DDV = 0.15;

    public Item(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setType(TaxType type) {
        this.type = type;
    }

    public double taxReturn() {
        if (type.equals(TaxType.A))
            return DDV * 0.18 * price;
        else if (type.equals(TaxType.B))
            return DDV * 0.05 * price;
        else return 0;
    }
}

class Receipt {
    private String id;
    private List<Item> items;

    public Receipt(String id, List<Item> items) {
        this.id = id;
        this.items = items;
    }

    public static Receipt createReceipt(String line) throws AmountNotAllowedException {
        String[] parts = line.split("\\s+");
        String id = parts[0];
        List<Item> items = new ArrayList<>();
        Arrays.stream(parts)
                .skip(1)
                .forEach(i -> {
                    if (Character.isDigit(i.charAt(0)))
                        items.add(new Item(Integer.parseInt(i)));
                    else
                        items.get(items.size() - 1).setType(TaxType.valueOf(i));
                });

        Receipt receipt = new Receipt(id, items);
        if (receipt.totalAmount() > 30000) throw new AmountNotAllowedException(receipt.totalAmount());
        return receipt;
    }

    public long totalAmount() {
        return items.stream().mapToInt(Item::getPrice).sum();
    }

    public double totalTaxReturn() {
        return items.stream().mapToDouble(Item::taxReturn).sum();
    }

    @Override
    public String toString() {
        return String.format("%s %d %.2f", id, totalAmount(), totalTaxReturn());
    }
}

class MojDDV {
    private List<Receipt> receipts;

    public MojDDV() {
        this.receipts = new ArrayList<>();
    }

    public void readRecords(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        receipts = br.lines()
                .map(line -> {
                    try {
                        return Receipt.createReceipt(line);
                    } catch (AmountNotAllowedException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void printTaxReturns(PrintStream out) {
        PrintWriter pw = new PrintWriter(out);
        receipts.forEach(receipt -> pw.println(receipt.toString()));
        pw.flush();
    }
}

public class MojDDVTest {

    public static void main(String[] args) {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);
        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

    }
}