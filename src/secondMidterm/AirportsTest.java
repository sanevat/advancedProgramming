package secondMidterm;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

class Airport{
    String name;
    String country;
    String code;
    int passengers;

    public Airport(String name, String country, String code, int passengers) {
        this.name = name;
        this.country = country;
        this.code = code;
        this.passengers = passengers;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)\n%s\n%d",name,code,country,passengers);
    }
}
class Flight{
    String from;
    String to;
    int time;
    int duration;

    public Flight(String from, String to, int time, int duration) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.duration = duration;
    }

    public int getTime() {
        return time;
    }
    public int takeOffHours(){
        return time/60;
    }
    public int takeOffMinutes(){
        return time-takeOffHours()*60;
    }
    public int landingHours(){
        return ((time+duration)-landingDays()*24*60)/60;
    }
    public int landingMinutes(){
        return (time+duration)-landingDays()*24*60-landingHours()*60;
    }
    public int landingDays(){
        if(time+duration>24*60)
            return (time+duration)/(24*60);
        return 0;
    }

    public int getDuration() {
        return duration;
    }

    public String getTo() {
        return to;
    }
    public int getDurationMinutes(){
        return duration-getDurationHours()*60;
    }
    public int getDurationHours(){
        return duration/60;
    }

    @Override
    public String toString() {
        String days="";
        days+="+"+landingDays()+"d ";
        return String.format("%s-%s %02d:%02d-%02d:%02d %s%dh%02dm",from,to,takeOffHours(),takeOffMinutes(),
                landingHours(),landingMinutes(),(landingDays()!=0)?days:"",getDurationHours(),getDurationMinutes());
    }
}
class Airports{
    Map<String,Airport>airportsByCode;
    Map<String,Set<Flight>>takeOffs;
    Map<String,Set<Flight>>landings;

    public Airports() {
        this.airportsByCode=new TreeMap<>();
        this.takeOffs=new HashMap<>();
        this.landings=new HashMap<>();
    }
    public void addAirport(String name, String country, String code, int passengers){
        Airport a=new Airport(name, country, code, passengers);
        airportsByCode.put(code,a);
        takeOffs.putIfAbsent(code,new TreeSet<>(Comparator.comparing(Flight::getTo).thenComparing(Flight::getTime)));
        landings.putIfAbsent(code,new TreeSet<>(Comparator.comparing(Flight::getTo).thenComparing(Flight::getTime).thenComparing(Flight::getDuration)));
    }
    public void addFlights(String from, String to, int time, int duration){
        takeOffs.get(from).add(new Flight(from, to, time, duration));
        landings.get(to).add(new Flight(from, to, time, duration));
    }
    public void showFlightsFromAirport(String code){
        Airport a=airportsByCode.get(code);
        System.out.println(a);
        AtomicInteger i= new AtomicInteger(1);
        takeOffs.get(code).forEach(flight-> System.out.println((i.getAndIncrement())+". " +flight));

    }
    public void showDirectFlightsFromTo(String from, String to){
        takeOffs.get(from).stream().filter(flight -> flight.getTo().equals(to))
                .forEach(System.out::println);
        if(takeOffs.get(from).stream().filter(flight -> flight.getTo().equals(to)).collect(Collectors.toList()).isEmpty()){
            System.out.println("No flights from "+from+" to "+to);
        }
    }
    public void showDirectFlightsTo(String to){
        landings.get(to).forEach(System.out::println);
    }
}

public class AirportsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Airports airports = new Airports();
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] codes = new String[n];
        for (int i = 0; i < n; ++i) {
            String al = scanner.nextLine();
            String[] parts = al.split(";");
            airports.addAirport(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
            codes[i] = parts[2];
        }
        int nn = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < nn; ++i) {
            String fl = scanner.nextLine();
            String[] parts = fl.split(";");
            airports.addFlights(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        int f = scanner.nextInt();
        int t = scanner.nextInt();
        String from = codes[f];
        String to = codes[t];
        System.out.printf("===== FLIGHTS FROM %S =====\n", from);
        airports.showFlightsFromAirport(from);
        System.out.printf("===== DIRECT FLIGHTS FROM %S TO %S =====\n", from, to);
        airports.showDirectFlightsFromTo(from, to);
        t += 5;
        t = t % n;
        to = codes[t];
        System.out.printf("===== DIRECT FLIGHTS TO %S =====\n", to);
        airports.showDirectFlightsTo(to);
    }
}
