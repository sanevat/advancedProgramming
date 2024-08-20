import java.io.OutputStream;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class InvalidOperationException1 extends Exception {
    public InvalidOperationException1(long id) {
        super(String.format("The quantity of the product with id %d can not be 0.", id));
    }

    public InvalidOperationException1() {
        super("There are no products with discount.");
    }
}

abstract class Product implements Comparable<Product> {
    protected long id;
    protected String name;
    protected int price;

    public Product(long id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    abstract double totalPrice();

    abstract double getQuantity();

    @Override
    public int compareTo(Product o) {
        return Double.compare(this.totalPrice(), o.totalPrice());
    }

    @Override
    public String toString() {
        return String.format("%d - %.2f", id, totalPrice());
    }
}

class WholeProduct extends Product {
    private int quantity;

    public WholeProduct(long id, String name, int price, int quantity) {
        super(id, name, price);
        this.quantity = quantity;
    }

    @Override
    double totalPrice() {
        return price * quantity;
    }

    public double getQuantity() {
        return quantity;
    }
}

class PiecedProduct extends Product {
    private double quantity;

    public PiecedProduct(long id, String name, int price, double quantity) {
        super(id, name, price);
        this.quantity = quantity;
    }

    @Override
    double totalPrice() {
        return price * quantity / 1000.0;
    }

    public double getQuantity() {
        return quantity;
    }
}

class ShoppingCart {
    List<Product> products;

    public ShoppingCart() {
        this.products = new ArrayList<>();
    }

    public void addItem(String s) throws InvalidOperationException1 {
        String[] parts = s.split(";");
        Product p = (parts[0].equals("PS") ?
                new PiecedProduct(Long.parseLong(parts[1]), parts[2],
                        Integer.parseInt(parts[3]), Double.parseDouble(parts[4])) :
                new WholeProduct(Long.parseLong(parts[1]), parts[2],
                        Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
        if (p.getQuantity() == 0)
            throw new InvalidOperationException1(p.id);
        else products.add(p);
    }

    public void printShoppingCart(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        products.stream().sorted(Comparator.reverseOrder()).forEach(pw::println);
        pw.flush();
    }

    public void blackFridayOffer(List<Integer> discountItems, OutputStream out) throws InvalidOperationException1 {
        PrintWriter pw = new PrintWriter(out);
        if (discountItems.isEmpty()) throw new InvalidOperationException1();

        for (Integer discountItem : discountItems) {
            for (Product p : products) {
                if (p.id == discountItem)
                    pw.println(String.format("%d - %.2f", p.id, 0.1 * p.totalPrice()));
                break;
            }
        }
        pw.flush();
    }
}

public class ShoppingCartTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ShoppingCart cart = new ShoppingCart();

        int items = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < items; i++) {
            try {
                cart.addItem(sc.nextLine());
            } catch (InvalidOperationException1 e) {
                System.out.println(e.getMessage());
            }
        }

        List<Integer> discountItems = new ArrayList<>();
        int discountItemsCount = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < discountItemsCount; i++) {
            discountItems.add(Integer.parseInt(sc.nextLine()));
        }

        int testCase = Integer.parseInt(sc.nextLine());
        if (testCase == 1) {
            cart.printShoppingCart(System.out);
        } else if (testCase == 2) {
            try {
                cart.blackFridayOffer(discountItems, System.out);
            } catch (InvalidOperationException1 e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}