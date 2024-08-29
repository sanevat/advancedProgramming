package secondMidterm;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

enum COMPARATOR_TYPE {
    NEWEST_FIRST,
    OLDEST_FIRST,
    LOWEST_PRICE_FIRST,
    HIGHEST_PRICE_FIRST,
    MOST_SOLD_FIRST,
    LEAST_SOLD_FIRST
}

class ProductNotFoundException extends Exception {
    ProductNotFoundException(String message) {
        super(message);
    }
}

class Product {
    String category;
    String id;
    String name;
    LocalDateTime createdAt;
    double price;
    int quantity;

    public Product(String category, String id, String name, LocalDateTime createdAt, double price) {
        this.category = category;
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.price = price;
        this.quantity = 0;
    }

    public void setQuantity(int quantity) {
        this.quantity += quantity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", price=" + price +
                ", quantitySold=" + quantity +
                '}';
    }
}

class OnlineShop {
    Map<String, Product> products;
    Map<String, Set<Product>> productsPerCategory;

    public OnlineShop() {
        this.productsPerCategory = new HashMap<>();
        this.products = new HashMap<>();
    }

    public void addProduct(String category, String id, String name, LocalDateTime createdAt, double price) {
        productsPerCategory.putIfAbsent(category, new HashSet<>());
        Product p = new Product(category, id, name, createdAt, price);
        productsPerCategory.get(category).add(p);
        products.put(id, p);
    }

    public double buyProduct(String id, int quantity) throws ProductNotFoundException {
        if (!products.containsKey(id))
            throw new ProductNotFoundException(String.format("Product with id %s does not exist in the online shop!", id));
        products.get(id).setQuantity(quantity);
        return products.get(id).price * quantity;
    }

    public Comparator<Product> getComparator(COMPARATOR_TYPE type) {
        return switch (type) {
            case OLDEST_FIRST -> Comparator.comparing(Product::getCreatedAt);
            case NEWEST_FIRST -> Comparator.comparing(Product::getCreatedAt).reversed();
            case LEAST_SOLD_FIRST -> Comparator.comparing(Product::getQuantity);
            case MOST_SOLD_FIRST -> Comparator.comparing(Product::getQuantity).reversed();
            case LOWEST_PRICE_FIRST -> Comparator.comparing(Product::getPrice);
            case HIGHEST_PRICE_FIRST -> Comparator.comparing(Product::getPrice).reversed();
        };
    }

    public List<List<Product>> listProducts(String category, COMPARATOR_TYPE comparatorType, int pageSize) {
        Comparator<Product> comparator = getComparator(comparatorType);
        Set<Product> productsSet = (category == null)
                ? productsPerCategory.values().stream().flatMap(Collection::stream).collect(Collectors.toSet())
                : new HashSet<>(productsPerCategory.getOrDefault(category, Collections.emptySet()));

        List<List<Product>> productsByPages = new ArrayList<>();
        AtomicReference<List<Product>> productsInPage = new AtomicReference<>(new ArrayList<>());
        AtomicInteger i = new AtomicInteger(1);

        productsSet.stream()
                .sorted(comparator)
                .forEach(product -> {
                    if (pageSize == i.get()) {
                        productsInPage.get().add(product);
                        i.set(0);
                        productsByPages.add(productsInPage.get());
                        productsInPage.set(new ArrayList<>());
                    } else {
                        productsInPage.get().add(product);
                    }
                    i.getAndIncrement();
                });

        if (!productsInPage.get().isEmpty())
            productsByPages.add(productsInPage.get());
        return productsByPages;
    }
}


public class OnlineShopTest {

    public static void main(String[] args) {
        OnlineShop onlineShop = new OnlineShop();
        double totalAmount = 0.0;
        Scanner sc = new Scanner(System.in);
        String line;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equalsIgnoreCase("addproduct")) {
                String category = parts[1];
                String id = parts[2];
                String name = parts[3];
                LocalDateTime createdAt = LocalDateTime.parse(parts[4]);
                double price = Double.parseDouble(parts[5]);
                onlineShop.addProduct(category, id, name, createdAt, price);
            } else if (parts[0].equalsIgnoreCase("buyproduct")) {
                String id = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                try {
                    totalAmount += onlineShop.buyProduct(id, quantity);
                } catch (ProductNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                String category = parts[1];
                if (category.equalsIgnoreCase("null"))
                    category = null;
                String comparatorString = parts[2];
                int pageSize = Integer.parseInt(parts[3]);
                COMPARATOR_TYPE comparatorType = COMPARATOR_TYPE.valueOf(comparatorString);
                printPages(onlineShop.listProducts(category, comparatorType, pageSize));
            }
        }
        System.out.println("Total revenue of the online shop is: " + totalAmount);
    }

    private static void printPages(List<List<Product>> listProducts) {
        for (int i = 0; i < listProducts.size(); i++) {
            System.out.println("PAGE " + (i + 1));
            listProducts.get(i).forEach(System.out::println);
        }
    }

}
