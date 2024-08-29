package secondMidterm;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class WrongDateException extends Exception {
    final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("eee MMM dd HH:mm:ss 'UTC' yyyy");

    public WrongDateException(LocalDateTime dateTime) {
        super(String.format("Wrong date: %s", dateTime.format(dtf)));
    }
}
class Event{
    String name;
    String location;
    LocalDateTime date;
    final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM, yyy HH:mm");

    public Event(String name, String location, LocalDateTime date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDate() {
        return date;
    }
    @Override
    public String toString() {
        return String.format("%s at %s, %s", date.format(dtf), location, name);
    }
}
class EventCalendar{
    int year;
    Map<LocalDateTime,Set<Event>>events;

    public EventCalendar(int year){
        this.year=year;
        this.events=new HashMap<>();
    }
    public void addEvent(String name, String location, LocalDateTime date) throws WrongDateException {
        if(date.getYear()!=year)
            throw new WrongDateException(date);
        events.putIfAbsent(date.toLocalDate().atStartOfDay(),new HashSet<>());
        events.get(date.toLocalDate().atStartOfDay()).add(new Event(name, location, date));
    }
    public void listEvents(LocalDateTime date){
        if(!events.containsKey(date.toLocalDate().atStartOfDay())){
            System.out.println("No events on this day!");
            return;
        }

        events.get(date.toLocalDate().atStartOfDay()).stream()
                .sorted(Comparator.comparing(Event::getDate).thenComparing(Event::getName))
                .forEach(System.out::println);
    }
    public void listByMonth(){
        Map<Integer,Integer>eventsByMonth=new TreeMap<>();
        for(int i=1;i<13;i++){
            eventsByMonth.put(i,0);
        }
        events.values().stream().flatMap(Collection::stream).forEach(event->
                eventsByMonth.put(event.date.getMonthValue(),eventsByMonth.get(event.date.getMonthValue())+1));
        eventsByMonth.forEach((key,value)-> System.out.println(key+" : "+value));
    }



}
public class EventCalendarTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        int year = scanner.nextInt();
        scanner.nextLine();
        EventCalendar eventCalendar = new EventCalendar(year);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            String name = parts[0];
            String location = parts[1];
            LocalDateTime date = LocalDateTime.parse(parts[2], dtf);
            try {
                eventCalendar.addEvent(name, location, date);
            } catch (WrongDateException e) {
                System.out.println(e.getMessage());
            }
        }
        String nl = scanner.nextLine();
        LocalDateTime date = LocalDateTime.parse(nl, dtf);
        eventCalendar.listEvents(date);
        eventCalendar.listByMonth();
    }
}