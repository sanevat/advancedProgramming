package secondMidterm;

import java.util.*;

class SeatNotAllowedException extends Exception {
    public SeatNotAllowedException() {
        super("SeatNotAllowedException");
    }
}

class SeatTakenException extends Exception {
    public SeatTakenException() {
        super("SeatTakenException");
    }
}

class Sector {
    String code;
    int numPlaces;
    List<Boolean> isSeatTaken;
    List<Integer> typesOfSeats;

    public Sector(String code, int numPlaces) {
        this.code = code;
        this.numPlaces = numPlaces;
        this.isSeatTaken = new ArrayList<>();
        typesOfSeats = new ArrayList<>();
        for (int i = 0; i < numPlaces; i++) {
            isSeatTaken.add(false);
            typesOfSeats.add(0);
        }
    }

    public void buySeat(int seat, int type) throws SeatNotAllowedException {
        if (typesOfSeats.contains(2) && type == 1 || typesOfSeats.contains(1) && type == 2)
            throw new SeatNotAllowedException();
        typesOfSeats.add(seat, type);
        isSeatTaken.set(seat, true);
    }

    public int numberOfFreePlaces() {
        return (int) isSeatTaken.stream().filter(seat -> !seat).count();
    }

    public String getCode() {
        return code;
    }

    public List<Boolean> getIsSeatTaken() {
        return isSeatTaken;
    }

    @Override
    public String toString() {
        return String.format("%s\t%d/%d\t%.1f%%", code, numberOfFreePlaces(), numPlaces, (1 - numberOfFreePlaces() / (double) numPlaces) * 100);
    }
}

class Stadium {
    String name;
    Map<String, Sector> sectors;

    public Stadium(String name) {
        this.name = name;
        this.sectors = new TreeMap<>();
    }

    public void createSectors(String[] sectorNames, int[] sizes) {
        for (int i = 0; i < sectorNames.length; i++) {
            String name = sectorNames[i];
            int size = sizes[i];
            sectors.put(name, new Sector(name, size));
        }
    }

    public void buyTicket(String sectorName, int seat, int type) throws SeatTakenException {
        if (sectors.get(sectorName).getIsSeatTaken().get(seat - 1))
            throw new SeatTakenException();
        try {
            sectors.get(sectorName).buySeat(seat - 1, type);
        } catch (SeatNotAllowedException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showSectors() {
        sectors.values().stream().sorted(Comparator.comparing(Sector::numberOfFreePlaces).reversed().thenComparing(Sector::getCode))
                .forEach(System.out::println);
    }
}

public class StadiumTest {
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
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }
}
