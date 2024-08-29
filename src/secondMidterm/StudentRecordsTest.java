package secondMidterm;

import java.io.*;
import java.util.*;

class Person {
    String id;
    String direction;
    List<Integer> grades;

    public Person(String id, String direction, List<Integer> grades) {
        this.id = id;
        this.direction = direction;
        this.grades = grades;
    }

    public double gpa() {
        return grades.stream().mapToInt(Integer::intValue).average().orElse(5.0);
    }

    public int countNumberOfTens() {
        return (int) grades.stream().filter(grade -> grade == 10).count();
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("%s %.2f", id, gpa());
    }
}

class PersonRecords {
    Map<String, Set<Person>> students;

    public PersonRecords() {
        this.students = new TreeMap<>();
    }

    public int readRecords(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        br.lines().forEach(line -> {
            Person p = createPerson(line);
            students.putIfAbsent(p.direction, new HashSet<>());
            students.get(p.direction).add(p);
        });
        return students.values().stream().flatMap(Collection::stream).toList().size();
    }

    public Person createPerson(String line) {
        String[] parts = line.split("\\s+");
        String id = parts[0];
        String direction = parts[1];
        List<Integer> grades = new ArrayList<>();
        Arrays.stream(parts)
                .skip(2)
                .forEach(part -> grades.add(Integer.parseInt(part)));
        return new Person(id, direction, grades);
    }

    public void writeTable(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        students.forEach((key, value) -> {
            pw.println(key);
            value.stream()
                    .sorted(Comparator.comparing(Person::gpa)
                            .reversed()
                            .thenComparing(Person::getId))
                    .forEach(pw::println);
        });
        pw.flush();
    }

    public int countNumberOfTens(Set<Person> students) {
        return students.stream().mapToInt(Person::countNumberOfTens).sum();
    }

    public Map<Integer, Integer> makeDistribution(Set<Person> students) {
        Map<Integer, Integer> distributionOfGrades = new TreeMap<>();
        for (int i = 6; i < 11; i++) {
            distributionOfGrades.put(i, 0);
        }
        students.forEach(stud -> stud.grades.forEach(grade ->
                distributionOfGrades.put(grade, distributionOfGrades.get(grade) + 1)));
        return distributionOfGrades;
    }

    public String generateAsterisks(int numGrades) {
        return "*".repeat(Math.max(0, (numGrades % 10 == 0) ? numGrades / 10 : numGrades / 10 + 1));
    }

    public void writeDistribution(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        students.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(countNumberOfTens(entry2.getValue()),
                        countNumberOfTens(entry1.getValue())))
                .forEach(entry -> {
                    pw.println(entry.getKey());
                    Map<Integer, Integer> distribution = makeDistribution(entry.getValue());
                    distribution.forEach((key, value) -> pw.println(String.format("%2d | %s(%d)", key, generateAsterisks(value), value)));
                });
        pw.flush();
    }
}

public class StudentRecordsTest {
    public static void main(String[] args) {
        System.out.println("=== READING RECORDS ===");
        PersonRecords studentRecords = new PersonRecords();
        int total = studentRecords.readRecords(System.in);
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable(System.out);
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution(System.out);
    }
}