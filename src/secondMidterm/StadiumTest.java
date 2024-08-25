package secondMidterm;

import java.util.*;
class SeatNotAllowedException extends Exception{
    public SeatNotAllowedException() {
    }


}
class SeatTakenException extends Exception{
    public SeatTakenException() {
    }
}
class Seat{
    boolean taken;
    int type;

    public Seat(boolean taken, int type) {
        this.taken = taken;
        this.type = type;
    }

    public Seat(boolean taken) {
        this.taken = taken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return taken == seat.taken && type == seat.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taken, type);
    }
}
class Sector implements  Comparable<Sector>{
    String code;
    int numPlaces;
    List<Seat> seats;

    public Sector(String code, int numPlaces) {
        this.code = code;
        this.numPlaces = numPlaces;
        seats=new ArrayList<>(numPlaces);
        for(int i=0;i<numPlaces;i++){
            seats.add(new Seat(false));
        }
    }

    int numFreePlaces(){
        return (int) seats.stream().filter(seat -> seat.taken==Boolean.FALSE).count();
    }
    public void takeSeat(int num,int type) throws SeatNotAllowedException {
        if(seats.contains(new Seat(Boolean.TRUE,2)) && type==1 ||
        seats.contains(new Seat(Boolean.TRUE,1))&& type==2)
            throw new SeatNotAllowedException();
        seats.set(num,new Seat(true,type));
    }

    public String getCode() {
        return code;
    }

    @Override
    public int compareTo(Sector o) {
        return Comparator.comparing(Sector::numFreePlaces)
                .reversed()
                .thenComparing(Sector::getCode)
                .compare(this,o);
    }

    @Override
    public String toString() {
        return String.format("%s\t%d/%d\t%.1f%%",code,numFreePlaces(),numPlaces,(1-numFreePlaces()/(double)numPlaces)*100);
    }
}
class Stadium{
    String name;
    Set<Sector> sectors;
    Map<String,Sector>sectorsByName;
    public Stadium(String name) {
        this.name = name;
        this.sectors=new HashSet<>();
        this.sectorsByName=new TreeMap<>();
    }
    public void createSectors(String[] sectorNames, int[] sizes){
        for(int i=0;i<sectorNames.length;i++){
            Sector s=new Sector(sectorNames[i],sizes[i]);
            sectors.add(s);
            sectorsByName.put(sectorNames[i],s);
        }

    }
    public void buyTicket(String sectorName, int seat, int type) throws SeatTakenException, SeatNotAllowedException {
        Sector sector=sectorsByName.get(sectorName);
        if(sector.seats.get(seat-1).taken)
            throw new SeatTakenException();
        sector.takeSeat(seat-1,type);
    }
    public void showSectors(){
        sectors.stream()
                .sorted()
                .forEach(System.out::println);
    }

}

public class StadiumTest{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] sectorNames = new String[n];
        int[] sectorSizes = new int[n];
        String name = scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            sectorNames[i] = parts[0];
            sectorSizes[i] = Integer.parseInt(parts[1]);
        }
        Stadium stadium = new Stadium(name);
        stadium.createSectors(sectorNames, sectorSizes);
        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            try {
                stadium.buyTicket(parts[0], Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            } catch (SeatNotAllowedException e) {
                System.out.println("SeatNotAllowedException");
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }
}
