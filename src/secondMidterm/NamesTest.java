package secondMidterm;

import java.util.*;
import java.util.stream.Collectors;

import java.util.*;
import java.util.stream.Collectors;

class Names {
    private List<String> names;
    private Map<String, Integer> namesCount;
    private Map<String, Integer> uniqueLetters;

    public Names() {
        this.names = new ArrayList<>();
        this.namesCount = new TreeMap<>();
        this.uniqueLetters = new HashMap<>();
    }

    public int getUniqueLetters(String word) {
        word = word.toLowerCase();
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : word.toCharArray()) {
            uniqueChars.add(c);
        }
        return uniqueChars.size();
    }

    public String findName(int len, int x) {
        List<String> filteredNames = namesCount.keySet().stream()
                .filter(name -> name.length() < len)
                .sorted()
                .collect(Collectors.toList());

        if (filteredNames.isEmpty()) {
            return "No names found";
        }
        int index = x % filteredNames.size();
        return filteredNames.get(index);
    }

    public void addName(String name) {
        names.add(name);
        namesCount.put(name, namesCount.getOrDefault(name, 0) + 1);
        uniqueLetters.put(name, getUniqueLetters(name));
    }

    public void printN(int n) {
        namesCount.entrySet().stream()
                .filter(entry -> entry.getValue() >= n)
                .forEach(entry -> System.out.printf("%s (%d) %d%n",
                        entry.getKey(), entry.getValue(), uniqueLetters.get(entry.getKey())));
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
