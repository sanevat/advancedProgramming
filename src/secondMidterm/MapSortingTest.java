package secondMidterm;

import java.util.*;
import java.util.stream.Collectors;
import java.util.*;

public class MapSortingTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        List<String> l = readMapPairs(scanner);
        if(n==1){
            Map<String, Integer> map = new HashMap<>();
            fillStringIntegerMap(l, map);
            SortedSet<Map.Entry<String, Integer>> s = entriesSortedByValues(map);
            System.out.println(s);
        } else {
            Map<Integer, String> map = new HashMap<>();
            fillIntegerStringMap(l, map);
            SortedSet<Map.Entry<Integer, String>> s = entriesSortedByValues(map);
            System.out.println(s);
        }

    }

    private static <T,V extends Comparable<V>> SortedSet<Map.Entry<T, V>> entriesSortedByValues(Map<T, V> map) {
        System.out.println(map);
        Comparator<Map.Entry<T,V>> comparator = (a,b)->{
            if (a.getValue().compareTo(b.getValue())==0)return 1;
            else return a.getValue().compareTo(b.getValue());
        };
        SortedSet<Map.Entry<T,V>>sortedSet=new TreeSet<>(comparator.reversed());
                map.entrySet().forEach(sortedSet::add);
                return sortedSet;

    }

    private static List<String> readMapPairs(Scanner scanner) {
        String line = scanner.nextLine();
        String[] entries = line.split("\\s+");
        return Arrays.asList(entries);
    }

    static void fillStringIntegerMap(List<String> l, Map<String,Integer> map) {
        l.forEach(s -> map.put(s.substring(0, s.indexOf(':')), Integer.parseInt(s.substring(s.indexOf(':') + 1))));
    }

    static void fillIntegerStringMap(List<String> l, Map<Integer, String> map) {
        l.forEach(s -> map.put(Integer.parseInt(s.substring(0, s.indexOf(':'))), s.substring(s.indexOf(':') + 1)));
    }


}