package secondMidterm;

import java.util.*;

interface Location {
    int getX();

    int getY();

    default int distance(Location other) {
        int xDiff = Math.abs(getX() - other.getX());
        int yDiff = Math.abs(getY() - other.getY());
        return xDiff + yDiff;
    }
}

class LocationCreator {
    public static Location create(int x, int y) {

        return new Location() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}

abstract class AppObject {
    String name;
    Location location;

    public AppObject(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }
}

class DeliveryPerson extends AppObject {
    Location destination;
    List<Double> deliveries;


    public DeliveryPerson(String name, Location location) {
        super(name, location);
        deliveries = new ArrayList<>();
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public double distance() {
        return location.distance(destination);
    }

    public void updateLocation(Location location) {
        this.location = location;
    }

    public List<Double> getDeliveries() {
        return deliveries;
    }

    public void addDeliveryFee(double delivery) {
        deliveries.add(delivery);
    }

    public double totalFee() {
        return deliveries.stream().mapToDouble(i -> i).sum();
    }

    public double avgFee() {
        return deliveries.stream().mapToDouble(i -> i).average().orElse(0.00);
    }

    @Override
    public String toString() {
        return String.format(" Name: %s Total deliveries: %d Total delivery fee: %.2f Average delivery fee: %.2f", name, deliveries.size(),
                totalFee(), avgFee());
    }
}

class Address extends AppObject {

    public Address(String name, Location location) {
        super(name, location);
    }
}

class User {
    String name;
    List<Address> addresses;
    List<Double> orders;

    public User(String name) {
        this.name = name;
        addresses = new ArrayList<>();
        orders = new ArrayList<>();
    }

    public void addOrder(double order) {
        orders.add(order);
    }

    public double totalSpent() {
        return orders.stream().mapToDouble(i -> i).sum();
    }

    double avgSpent() {
        return orders.stream().mapToDouble(i -> i).average().orElse(0.00);
    }

    @Override
    public String toString() {
        return String.format(" Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f", name, orders.size(),
                totalSpent(), avgSpent());
    }

    public String getName() {
        return name;
    }
}

class Restaurant extends AppObject {
    List<Double> orders;

    public Restaurant(String name, Location location) {
        super(name, location);
        orders = new ArrayList<>();
    }

    public void addOrder(double order) {
        orders.add(order);
    }

    public double averageEarned() {
        return orders.stream().mapToDouble(i -> i).average().orElse(0.00);
    }

    public double totalEarned() {
        return orders.stream().mapToDouble(i -> i).sum();
    }

    @Override
    public String toString() {
        return String.format(" Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f", name, orders.size(),
                totalEarned(), averageEarned());
    }
}

class DeliveryApp {
    String name;
    Map<String, DeliveryPerson> deliveryPeople;
    Map<String, User> users;
    Map<String, Restaurant> restaurants;

    public DeliveryApp(String name) {
        this.name = name;
        this.deliveryPeople = new HashMap<>();
        this.users = new HashMap<>();
        this.restaurants = new HashMap<>();
    }

    void registerDeliveryPerson(String id, String name, Location currentLocation) {
        deliveryPeople.put(id, new DeliveryPerson(name, currentLocation));
    }

    void addRestaurant(String id, String name, Location location) {
        restaurants.put(id, new Restaurant(name, location));
    }

    void addUser(String id, String name) {
        users.put(id, new User(name));
    }

    void addAddress(String id, String addressName, Location location) {
        users.get(id).addresses.add(new Address(addressName, location));
    }

    void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        User user = users.get(userId);
        Restaurant restaurant = restaurants.get(restaurantId);
        Address address = user.addresses.stream()
                .filter(a -> a.getName().equals(userAddressName))
                .findFirst()
                .orElse(null);

        deliveryPeople.values().forEach(del -> del.setDestination(restaurant.location));

        DeliveryPerson chosen = deliveryPeople.values().stream()
                .min(Comparator.comparing(DeliveryPerson::distance).thenComparing(del -> del.getDeliveries().size()))
                .orElse(null);

        if (chosen == null || address == null) return;

        double distance = chosen.distance();
        chosen.updateLocation(address.getLocation());
        chosen.addDeliveryFee(90 + ((int) (distance / 10)) * 10);
        user.addOrder(cost);
        restaurant.addOrder(cost);

    }

    void printUsers() {
        users.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(User::totalSpent).thenComparing(User::getName).reversed()))
                .forEach(entry -> System.out.println("ID: " + entry.getKey() + entry.getValue().toString()));
    }

    void printDeliveryPeople() {
        deliveryPeople.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(DeliveryPerson::totalFee).thenComparing(DeliveryPerson::getName).reversed()))
                .forEach(entry -> System.out.println("ID: " + entry.getKey() + entry.getValue().toString()));
    }

    void printRestaurants() {
        restaurants.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(Restaurant::averageEarned).thenComparing(Restaurant::getName).reversed()))
                .forEach(entry -> System.out.println("ID: " + entry.getKey() + entry.getValue().toString()));
    }
}

public class DeliveryAppTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DeliveryApp app = new DeliveryApp(sc.nextLine());

        while (sc.hasNextLine()) {
            String[] parts = sc.nextLine().split(" ");
            switch (parts[0]) {
                case "addUser" -> app.addUser(parts[1], parts[2]);
                case "registerDeliveryPerson" -> app.registerDeliveryPerson(parts[1], parts[2],
                        LocationCreator.create(Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
                case "addRestaurant" -> app.addRestaurant(parts[1], parts[2],
                        LocationCreator.create(Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
                case "addAddress" -> app.addAddress(parts[1], parts[2],
                        LocationCreator.create(Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
                case "orderFood" -> app.orderFood(parts[1], parts[2], parts[3], Float.parseFloat(parts[4]));
                case "printUsers" -> app.printUsers();
                case "printRestaurants" -> app.printRestaurants();
                default -> app.printDeliveryPeople();
            }
        }
    }
}
