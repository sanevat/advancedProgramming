package labs.labs3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

interface Item{
    int getPrice();
    String getType();
}
class InvalidPizzaTypeException extends Exception{
    public InvalidPizzaTypeException() {
    }
}
class ArrayIndexOutOfBoundsException extends Exception{
    public ArrayIndexOutOfBoundsException(int idx) {
        super(String.format("Index %d out of bonds",idx));
    }
}
class InvalidExtraTypeException extends Exception{
    public InvalidExtraTypeException() {
    }
}
class EmptyOrder extends Exception{
    public EmptyOrder() {
    }
}
class OrderLockedException extends Exception{
    public OrderLockedException() {
    }
}
class ItemOutOfStockException extends Exception{
    Item i;

    public ItemOutOfStockException(Item i) {
        this.i = i;
    }
    public void message(){
        System.out.printf("Item %s is out of stock\n",i.getType());
    }
}
class ExtraItem implements Item{
    String type;

    public ExtraItem(String type) throws InvalidExtraTypeException {
        if(!type.equals("Coke")&&!type.equals("Ketchup"))
            throw new InvalidExtraTypeException();
        this.type =type;
    }

    @Override
    public int getPrice() {
        if(type.equals("Coke")) return 5;
        return 3;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtraItem extraItem = (ExtraItem) o;
        return Objects.equals(type, extraItem.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
class PizzaItem implements Item{
    String type;

    public PizzaItem(String type) throws InvalidPizzaTypeException {
        if(!type.equals("Standard")&&!type.equals("Pepperoni")&&!type.equals("Vegetarian"))
            throw new InvalidPizzaTypeException();
        this.type = type;
    }

    @Override
    public int getPrice() {
        if(type.equals("Standard"))
            return 10;
        else if(type.equals("Pepperoni"))
            return 12;
        return 8;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PizzaItem pizzaItem = (PizzaItem) o;
        return Objects.equals(type, pizzaItem.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
class Product{
    private final Item item;
    private int count;

    public Product(Item item, int count) {
        this.item = item;
        this.count = count;
    }

    public Item getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public int price(){
        return count*item.getPrice();
    }
}
class Order{
    List<Product> products;
    boolean isLocked;

    public Order() {
        this.products=new ArrayList<>();
        isLocked=false;
    }
    public void addItem(Item item, int count) throws ItemOutOfStockException, OrderLockedException {
        if(count>10) throw new ItemOutOfStockException(item);
        if(isLocked) throw new OrderLockedException();
        for(Product p:products){
            if(p.getItem().equals(item)){
                p.setCount(count);
                return;
            }
        }
        products.add(new Product(item,count));
    }
    public int getPrice(){
       return products.stream().mapToInt(Product::price).sum();
    }
    public void displayOrder(){
        StringBuilder sb=new StringBuilder();
        AtomicInteger i= new AtomicInteger();
        products.forEach(prod->{
            sb.append(String.format("%3d.",(i.incrementAndGet())));
            sb.append(String.format("%-15sx",prod.getItem().getType()));
            sb.append(String.format("%2d",prod.getCount()));
            sb.append(String.format("%5d$",prod.price()));
            sb.append("\n");

        });
        sb.append(String.format("%-22s%5d$", "Total:", getPrice()));
        System.out.println(sb);
    }
    public void removeItem(int idx) throws OrderLockedException, ArrayIndexOutOfBoundsException {
        if(isLocked)throw new OrderLockedException();
        if(idx<0 || idx>products.size()) throw new ArrayIndexOutOfBoundsException(idx);
        products.remove(idx);
    }
    public void lock() throws EmptyOrder {
        if(products.isEmpty())throw new EmptyOrder();
        this.isLocked=true;
    }

}
public class PizzaOrderTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) { //test Item
            try {
                String type = jin.next();
                String name = jin.next();
                Item item = null;
                if (type.equals("Pizza")) item = new PizzaItem(name);
                else item = new ExtraItem(name);
                System.out.println(item.getPrice());
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
        if (k == 1) { // test simple order
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 2) { // test order with removing
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (jin.hasNextInt()) {
                try {
                    int idx = jin.nextInt();
                    order.removeItem(idx);
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 3) { //test locking & exceptions
            Order order = new Order();
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.addItem(new ExtraItem("Coke"), 1);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.removeItem(0);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
    }

}