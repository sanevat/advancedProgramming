package secondMidterm;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

interface MergeStrategy<V> {
    V execute(V a, V v);
}

class MapOps {
    static <T extends Comparable<T>, V> Map<T, V> merge(Map<T, V> map1, Map<T, V> map2, MergeStrategy<V> strategy) {
        Map<T, V> newMap = new TreeMap<>();
        map2.forEach((key, value) -> {
            if (map1.containsKey(key)) {
                newMap.put(key,  strategy.execute(value, map1.get(key)));
            }
        });
        map1.forEach(newMap::putIfAbsent);
        map2.forEach(newMap::putIfAbsent);
        return newMap;
    }
}

public class GenericMapTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase == 1) {
            Map<Integer, Integer> mapLeft = new HashMap<>();
            Map<Integer, Integer> mapRight = new HashMap<>();
            readIntMap(sc, mapLeft);
            readIntMap(sc, mapRight);

            MergeStrategy<Integer> mergeStrategy = Integer::sum;

            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        } else if (testCase == 2) {
            Map<String, String> mapLeft = new HashMap<>();
            Map<String, String> mapRight = new HashMap<>();
            readStrMap(sc, mapLeft);
            readStrMap(sc, mapRight);

            MergeStrategy<String> mergeStrategy = (a, b) -> b.concat(a);

            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        } else if (testCase == 3) {
            Map<Integer, Integer> mapLeft = new HashMap<>();
            Map<Integer, Integer> mapRight = new HashMap<>();
            readIntMap(sc, mapLeft);
            readIntMap(sc, mapRight);

            MergeStrategy<Integer> mergeStrategy = Math::max;

            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        } else if (testCase == 4) {
            Map<String, String> mapLeft = new HashMap<>();
            Map<String, String> mapRight = new HashMap<>();
            readStrMap(sc, mapLeft);
            readStrMap(sc, mapRight);


            MergeStrategy<String> mergeStrategy = (str1, str2) -> str2.replaceAll(str1, IntStream.range(0,str1.length()).mapToObj(i->"*").collect(Collectors.joining("")));
            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));

        }
    }

    private static void readIntMap(Scanner sc, Map<Integer, Integer> map) {
        int n = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < n; i++) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            int k = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);
            map.put(k, v);
        }
    }

    private static void readStrMap(Scanner sc, Map<String, String> map) {
        int n = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < n; i++) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            map.put(parts[0], parts[1]);
        }
    }

    private static void printMap(Map<?, ?> map) {
        map.forEach((k, v) -> System.out.printf("%s -> %s%n", k.toString(), v.toString()));
    }
}
