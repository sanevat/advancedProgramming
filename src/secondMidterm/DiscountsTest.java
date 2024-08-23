package secondMidterm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
class Item{
    int oldPrice;
    int newPrice;

    public Item(int oldPrice, int newPrice) {
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }
    public int calculatePercentageDscount(){
        return (int)(100-(double)oldPrice/newPrice*100);
    }
    public int calculateTotal(){
        return Math.abs(oldPrice-newPrice);
    }
}
class Store{
    String name;
    List<Item> products;

    public Store(String name, List<Item> products) {
        this.name = name;
        this.products = products;
    }

    public static Store createStore(String line){
        String []parts=line.split("\\s+");
       List<Item>products=new ArrayList<>();
        String name=parts[0];
        Arrays.stream(parts)
                .skip(1)
                .forEach(part->{
                    String[]prices= part.split(":");
                    products.add(new Item(Integer.parseInt(prices[0]),Integer.parseInt(prices[1])));
                });
        return new Store(name,products);
    }
    public double calculateAverageDiscount(){
       return products.stream().mapToInt(Item::calculatePercentageDscount).average().orElse(0.0);
    }

    public long totalDiscount(){
        return products.stream().mapToLong(Item::calculateTotal).sum();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%s%nAverage discount: %.1f%%%nTotal discount: %d%n",
                name, calculateAverageDiscount(), totalDiscount()));

        products.stream()
                .sorted(Comparator.comparing(Item::calculatePercentageDscount)
                        .thenComparing(Item::calculateTotal)
                        .reversed())
                .forEach(product -> sb.append(String.format("%2d%% %s/%s%n",
                        product.calculatePercentageDscount(),
                        product.oldPrice,
                        product.newPrice)));

        return sb.toString();
    }
}
class Discounts{
    List<Store> stores;

    public Discounts() {
        this.stores=new ArrayList<>();
    }
    public int readStores(InputStream inputStream){
        BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
        br.lines()
                .forEach(line->
                       stores.add(Store.createStore(line)));
        return stores.size();
    }
    public List<Store> byAverageDiscount(){
        return stores.stream()
                .sorted(Comparator.comparing(Store::calculateAverageDiscount).thenComparing(Store::getName).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }
    public List<Store> byTotalDiscount(){
        return stores.stream()
                .sorted(Comparator.comparing(Store::totalDiscount).thenComparing(Store::getName))
                .limit(3)
                .collect(Collectors.toList());
    }

}

public class DiscountsTest {
    public static void main(String[] args) {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores(System.in);
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::print);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::print);
    }
}