import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class RaceTime{
    private int hour;
    private int minute;
    private int second;

    public RaceTime(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d",hour,minute,second);
    }
}
class Partcicipant implements Comparable<Partcicipant> {
    private long id;
    List<RaceTime> times;

    public Partcicipant(long id, List<RaceTime> times) {
        this.id = id;
        this.times = times;
    }
    public static Partcicipant createLine(String line){
        String[]parts=line.split("\\s+");
        long id=Long.parseLong(parts[0]);

        List<RaceTime>raceTimes=new ArrayList<>();
        Arrays.stream(parts)
                .skip(1)
                .forEach(part->{
                    String[]times=part.split(":");
                    raceTimes.add(new RaceTime(Integer.parseInt(times[0]),
                            Integer.parseInt(times[1]),
                            Integer.parseInt(times[2])));
                });
        return new Partcicipant(id,raceTimes);
    }
    public int totalRunTime(){
        int t2=times.get(1).getHour()*60*60+times.get(1).getMinute()*60+times.get(1).getSecond();
        int t1=times.get(0).getHour()*60*60+times.get(0).getMinute()*60+times.get(0).getSecond();
        return t2-t1;

    }
    public static RaceTime totalRunTimeRace(int totalSeconds){
        int hours=totalSeconds/3600;
        int minutes=(totalSeconds-hours*3600)/60;
        int seconds=totalSeconds-minutes*60-hours*3600;
        return new RaceTime(hours,minutes,seconds);
    }

    @Override
    public String toString() {
        return id+" "+totalRunTimeRace(totalRunTime()).toString();
    }

    @Override
    public int compareTo(Partcicipant o) {
        return Integer.compare(this.totalRunTime(),o.totalRunTime());
    }
}
class TeamRace{
    List<Partcicipant>participants;
    public static void findBestTeam(InputStream is, OutputStream os){
        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        List<Partcicipant>participants;
        participants=br.lines()
                .map(Partcicipant::createLine)
                .collect(Collectors.toList());
        PrintWriter pw=new PrintWriter(os);
        participants=participants.stream().sorted().collect(Collectors.toList());
        long totalSeconds=0;
        for (int i = 0; i < 4; i++) {
            pw.println(participants.get(i).toString());
             totalSeconds+=participants.get(i).totalRunTime();
        }
        pw.println(Partcicipant.totalRunTimeRace((int) totalSeconds).toString());

        pw.flush();
    }
}
public class RaceTest {
    public static void main(String[] args) {

            TeamRace.findBestTeam(System.in, System.out);

    }
}
