package secondMidterm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

class Article{
    int oldPrice;
    int newPrice;

    public Article(int oldPrice, int newPrice) {
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }
    public int percentOfDiscount(){
        return (int)(100-(double)newPrice/oldPrice*100);
    }
    public int absoluteDiscount(){
        return oldPrice-newPrice;
    }

    @Override
    public String toString() {
        return String.format("%d%% %d/%d",percentOfDiscount(),newPrice,oldPrice);
    }
}
class Store{
    String name;
    List<Article>articles;

    public Store(String name, List<Article> articles) {
        this.name = name;
        this.articles = articles;
    }

    public static Store createStore(String line){
        String[]parts=line.split("\\s+");
        String name=parts[0];
        List<Article>articles=new ArrayList<>();
        Arrays.stream(parts).skip(1).forEach(part->{
            String[]prices=part.split(":");
            articles.add(new Article(Integer.parseInt(prices[1]),Integer.parseInt(prices[0])));
        });
        return new Store(name,articles);
    }
    public double averageDiscount(){
       return articles.stream().mapToInt(Article::percentOfDiscount).average().orElse(0.0);
    }
    public int totalDiscount(){
        return articles.stream().mapToInt(Article::absoluteDiscount).sum();
    }
    public void sort(){
        articles=articles.stream().sorted(Comparator.comparing(Article::percentOfDiscount)
                .thenComparing(Article::absoluteDiscount).reversed()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        sort();
       StringBuilder sb=new StringBuilder();
       sb.append(String.format("%s\nAverage discount: %.1f%%\nTotal discount: %d\n",name,averageDiscount(),totalDiscount()));
       articles.forEach(article -> sb.append(article.toString()).append("\n"));
       return sb.toString();
    }
}

class Discounts{
    Map<String,Store>stores;

    public Discounts() {
        this.stores=new TreeMap<>();
    }
    public int readStores(InputStream inputStream){
        BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
        br.lines().forEach(line->{
            Store s=Store.createStore(line);
            stores.put(s.name,s);
        });
        return stores.keySet().size();
    }
    public List<Store>byAverageDiscount(){
        return stores.values().stream()
                .sorted(Comparator.comparing(Store::averageDiscount).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }
    public List<Store>byTotalDiscount(){
        return stores.values().stream()
                .sorted(Comparator.comparing(Store::totalDiscount))
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