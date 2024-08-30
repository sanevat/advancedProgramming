package secondMidterm;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


interface ILocation {
    double getLongitude();

    double getLatitude();

    LocalDateTime getTimestamp();
}
class UserIdAlreadyExistsException extends  Exception{
    public UserIdAlreadyExistsException(String message) {
        super(message);
    }
}
class AppUser{
    String id;
    String name;
    List<ILocation>locations;
    LocalDateTime timeRegisteredSick;
    boolean isSick;

    public AppUser(String id, String name) {
        this.id = id;
        this.name = name;
        this.locations=new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void registerSick(LocalDateTime time){
        this.isSick=true;
        this.timeRegisteredSick=time;
    }
    public double euclidianDistance(ILocation l1,  ILocation l2){
        return Math.sqrt(Math.pow(l1.getLongitude()-l2.getLongitude(),2)+
                Math.pow(l1.getLatitude()-l2.getLatitude(),2));
    }

    public LocalDateTime getTimeRegisteredSick() {
        return timeRegisteredSick;
    }

    public int numberOfContacts(AppUser other){
        AtomicInteger counter= new AtomicInteger();
        locations.forEach(location1 ->
                other.locations.forEach(location2->{
                double euclidianDistance=euclidianDistance(location1,location2);
                long timeDuration=Duration.between(location1.getTimestamp(),location2.getTimestamp()).toSeconds();
                if(euclidianDistance<=2 && Math.abs(timeDuration)<=300){
                    counter.getAndIncrement();
            }
        }));
        return counter.get();
    }

    @Override
    public String toString() {
        return name+" "+id+" "+timeRegisteredSick;
    }
}

class StopCoronaApp{
    Map<String,AppUser>users;

    public StopCoronaApp() {
        this.users=new TreeMap<>();
    }

    public void addUser(String name, String id) throws UserIdAlreadyExistsException {
        if(users.containsKey(id))
            throw new UserIdAlreadyExistsException("User with id "+id+" already exists");
        users.put(id,new AppUser(id,name));
    }
    public void addLocations (String id, List<ILocation> iLocations){
        users.get(id).locations.addAll(iLocations);
    }
    public void detectNewCase (String id, LocalDateTime timestamp){
        users.get(id).registerSick(timestamp);
    }
    public  Map<AppUser, Integer> getDirectContacts (AppUser u){
        Map<AppUser,Integer>directContacts=new LinkedHashMap<>();
        users.values().stream()
                .filter(user->user!=u)
                .forEach(user->{
                    if(u.numberOfContacts(user)>0){
                        directContacts.put(user,u.numberOfContacts(user));
                    }
                });
        return directContacts;
    }
    public Collection<AppUser> getIndirectContacts (AppUser u){
        Set<AppUser>directContacts=getDirectContacts(u).keySet();
        Set<AppUser>indirectContacts=new HashSet<>();
        directContacts.forEach(dirCont->
                users.values().stream()
                        .filter(user->user!=dirCont &&user!=u&& !directContacts.contains(user))
                        .forEach(user->{
                            if(user.numberOfContacts(dirCont)>0)
                                indirectContacts.add(user);
        }));
        return indirectContacts;
    }
    public void printDirectContacts(AppUser u){
        Map<AppUser, Integer> directContacts= getDirectContacts(u);
        directContacts.entrySet().stream()
                .sorted(Map.Entry.<AppUser, Integer>comparingByValue().reversed())
                .forEach(entry-> System.out.println(entry.getKey().name+" "+entry.getKey().id.substring(0,4)+"*** "+entry.getValue()));

        System.out.println("Count of direct contacts: "+ directContacts.values().stream().mapToInt(Integer::intValue).sum());
    }
    public void printIndirectContacts(AppUser u){
        getIndirectContacts(u).stream()
                .sorted(Comparator.comparing(AppUser::getName).thenComparing(AppUser::getId))
                .forEach(indirectContact->{
                    System.out.println(indirectContact.name+" "+ indirectContact.id.substring(0,4)+"***");
        });
        System.out.println("Count of indirect contacts: "+getIndirectContacts(u).size());
    }
    public double averageDirectContacts() {
        return users.values().stream()
                .filter(user -> user.isSick)
                .mapToInt(user -> getDirectContacts(user).values().stream()
                        .mapToInt(Integer::intValue)
                        .sum())
                .average()
                .orElse(0.0);
    }
    public double averageIndirectContacts(){
        return users.values().stream().filter(user->user.isSick)
                .mapToInt(user->getIndirectContacts(user).size())
                .average()
                .orElse(0.0);
    }
    public void createReport (){
        users.values().stream()
                .filter(user->user.isSick)
                .sorted(Comparator.comparing(AppUser::getTimeRegisteredSick))
                .forEach(user->{
                     System.out.println(user);
                     System.out.println("Direct contacts:");
                     printDirectContacts(user);
                     System.out.println("Indirect contacts:");
                     printIndirectContacts(user);
        });
        System.out.printf("Average direct contacts: %.4f\n",averageDirectContacts());
        System.out.printf("Average indirect contacts: %.4f\n",averageIndirectContacts());
    }
}

public class StopCoronaTest {

    public static double timeBetweenInSeconds(ILocation location1, ILocation location2) {
        return Math.abs(Duration.between(location1.getTimestamp(), location2.getTimestamp()).getSeconds());
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StopCoronaApp stopCoronaApp = new StopCoronaApp();

        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            switch (parts[0]) {
                case "REG": //register
                    String name = parts[1];
                    String id = parts[2];
                    try {
                        stopCoronaApp.addUser(name, id);
                    }
                     catch (UserIdAlreadyExistsException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "LOC": //add locations
                    id = parts[1];
                    List<ILocation> locations = new ArrayList<>();
                    for (int i = 2; i < parts.length; i += 3) {
                        locations.add(createLocationObject(parts[i], parts[i + 1], parts[i + 2]));
                    }
                    stopCoronaApp.addLocations(id, locations);

                    break;
                case "DET": //detect new cases
                    id = parts[1];
                    LocalDateTime timestamp = LocalDateTime.parse(parts[2]);
                    stopCoronaApp.detectNewCase(id, timestamp);

                    break;
                case "REP": //print report
                    stopCoronaApp.createReport();
                    break;
                default:
                    break;
            }
        }
    }

    private static ILocation createLocationObject(String lon, String lat, String timestamp) {
        return new ILocation() {
            @Override
            public double getLongitude() {
                return Double.parseDouble(lon);
            }

            @Override
            public double getLatitude() {
                return Double.parseDouble(lat);
            }

            @Override
            public LocalDateTime getTimestamp() {
                return LocalDateTime.parse(timestamp);
            }
        };
    }
}
