package secondMidterm;

import java.util.*;
import java.util.stream.Collectors;


class Names {
    Map<String, Integer> names;

    public Names() {
        this.names = new TreeMap<>();
    }

    public int findUniqueLetters(String word) {
        Set<Character> chars = new HashSet<>();
        word = word.toLowerCase();
        for (Character c : word.toCharArray())
            chars.add(c);
        return chars.size();

    }

    public void addName(String name) {
        names.putIfAbsent(name, 0);
        names.put(name, names.get(name) + 1);
    }

    public void printN(int n) {
        names.entrySet().stream()
                .filter(entry -> entry.getValue() >= n)
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.println(String.format("%s (%d) %d", entry.getKey(), entry.getValue(),
                        findUniqueLetters(entry.getKey()))));
    }

    public String findName(int len, int x) {
        List<String> namesShorterThanLen = names.keySet().stream()
                .filter(key -> key.length() < len)
                .collect(Collectors.toList());
        int newX = (x > namesShorterThanLen.size()) ? (x % namesShorterThanLen.size()) : x;
        return namesShorterThanLen.get(newX);
    }
}

public class NamesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        Names names = new Names();
        for (int i = 0; i < n; ++i) {
            String name = scanner.nextLine();
            names.addName(name);
        }
        n = scanner.nextInt();
        System.out.printf("===== PRINT NAMES APPEARING AT LEAST %d TIMES =====\n", n);
        names.printN(n);
        System.out.println("===== FIND NAME =====");
        int len = scanner.nextInt();
        int index = scanner.nextInt();
        System.out.println(names.findName(len, index));
        scanner.close();

    }
}
