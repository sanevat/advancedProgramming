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

abstract class Object {
    String id;
    String name;
    Location location;

    public Object(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }
}

class DeliveryPerson extends Object {
    List<Double> orders;

    public DeliveryPerson(String id, String name, Location location) {
        super(id, name, location);
        this.orders = new ArrayList<>();
    }

    public int distanceToRestaurant(Location r) {
        return location.distance(r);
    }

    public void addOrder(double amount) {
        this.orders.add(amount);
    }

    public int getNumDeliveries() {
        return orders.size();
    }

    public void setLocation(Location l) {
        this.location = l;
    }

    public double totalDeliveryFee() {
        return orders.stream().mapToDouble(Double::doubleValue).sum();
    }

    public double averageDeliveryFee() {
        return orders.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total delivery fee: %.2f Average delivery fee: %.2f",
                id, name, orders.size(), totalDeliveryFee(), averageDeliveryFee());
    }

}

class Restaurant extends Object {
    List<Double> orders;

    public Restaurant(String id, String name, Location location) {
        super(id, name, location);
        this.orders = new ArrayList<>();
    }

    public void addOrder(double amount) {
        this.orders.add(amount);
    }

    public double totalAmountEarned() {
        return orders.stream().mapToDouble(Double::doubleValue).sum();
    }

    public double averageAmountEarned() {
        return orders.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f",
                id, name, orders.size(), totalAmountEarned(), averageAmountEarned());
    }
}

class User extends Object {
    Map<String, Address> addresses;
    List<Double> orders;

    public User(String id, String name) {
        super(id, name, null);
        this.addresses = new HashMap<>();
        this.orders = new ArrayList<>();
    }

    public void addOrder(double amount) {
        this.orders.add(amount);
    }

    public double totalAmountSpent() {
        return orders.stream().mapToDouble(Double::doubleValue).sum();
    }

    public double averageAmountSpent() {
        return orders.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f",
                id, name, orders.size(), totalAmountSpent(), averageAmountSpent());
    }
}

class Address extends Object {
    public Address(String name, Location location) {
        super("", name, location);
    }
}

class DeliveryApp {
    String name;
    Map<String, DeliveryPerson> deliveryPersons;
    Map<String, Restaurant> restaurants;
    Map<String, User> users;

    public DeliveryApp(String name) {
        this.name = name;
        this.deliveryPersons = new HashMap<>();
        this.restaurants = new HashMap<>();
        this.users = new HashMap<>();
    }

    public void registerDeliveryPerson(String id, String name, Location currLocation) {
        deliveryPersons.put(id, new DeliveryPerson(id, name, currLocation));
    }

    public void addRestaurant(String id, String name, Location location) {
        restaurants.put(id, new Restaurant(id, name, location));
    }

    public void addUser(String id, String name) {
        users.put(id, new User(id, name));
    }

    public void addAddress(String id, String addressName, Location location) {
        users.get(id).addresses.put(addressName, new Address(addressName, location));
    }

    public String getClosestDeliverer(String restaurantId) {
        Location restourantLocation = restaurants.get(restaurantId).location;
        return deliveryPersons.values().stream().min(Comparator
                        .comparing((DeliveryPerson deliverer) -> deliverer.distanceToRestaurant(restourantLocation))
                        .thenComparing(DeliveryPerson::getNumDeliveries))
                .get()
                .id;
    }

    public float calculateFee(Location userAddress, Location restaurantAddress) {
        double distance=userAddress.distance(restaurantAddress);
        return  90 + ((int)( distance / 10))*10;
    }

    public void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        DeliveryPerson chosenDeliverer = deliveryPersons.get(getClosestDeliverer(restaurantId));
        User user = users.get(userId);
        Restaurant restaurant = restaurants.get(restaurantId);
        Location userAddress = user.addresses.get(userAddressName).location;

        chosenDeliverer.addOrder(calculateFee(chosenDeliverer.location, restaurant.location));
        chosenDeliverer.setLocation(userAddress);
        user.addOrder(cost);
        restaurant.addOrder(cost);
    }

    public void printUsers() {
        users.values().stream()
                .sorted(Comparator.comparing(User::totalAmountSpent).reversed())
                .forEach(System.out::println);
    }

    public void printRestaurants() {
        restaurants.values().stream()
                .sorted(Comparator.comparing(Restaurant::averageAmountEarned))
                .forEach(System.out::println);
    }

    public void printDeliveryPeople() {
        deliveryPersons.values().stream()
                .sorted(Comparator.comparing(DeliveryPerson::totalDeliveryFee).reversed())
                .forEach(System.out::println);
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
