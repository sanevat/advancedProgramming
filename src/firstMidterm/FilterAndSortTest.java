import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class Person implements Comparable<Person> {
    String id;
    List<Integer> grades;

    public Person(String id, List<Integer> grades) {
        this.id = id;
        this.grades = grades;
    }

    public double average() {
        return grades.stream().mapToDouble(i -> i).average().orElse(0.0);
    }

    public int getYear() {
        return (24 - Integer.parseInt(id.substring(0, 2)));
    }

    public int totalCourses() {
        return Math.min(getYear() * 10, 40);
    }

    public double labAssistantPoints() {
        return average() * ((double) grades.size() / totalCourses()) * (0.8 + ((getYear() - 1) * 0.2) / 3.0);
    }

    @Override
    public int compareTo(Person o) {
        return Comparator.comparing(Person::labAssistantPoints)
                .thenComparing(Person::average)
                .compare(this, o);
    }

    @Override
    public String toString() {
        return String.format("Student %s (%d year) - %d/%d passed exam, average grade %.2f.\nLab assistant points: %.2f", id, getYear(), grades.size(), totalCourses(), average(), labAssistantPoints());
    }
}

class EmptyResultException extends Exception {
    public EmptyResultException(String message) {
        super(message);
    }
}

class FilterAndSort {
    public static <T extends Comparable<T>> List<T> execute(List<T> list, Predicate<T> predicate) throws EmptyResultException {
        list = list.stream()
                .filter(predicate)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        if (list.isEmpty()) throw new EmptyResultException("Empty list");
        list.forEach(System.out::println);
        return list;
    }
}

public class FilterAndSortTest {
    public static void main(String[] args)  {
        Scanner sc = new Scanner(System.in);
        int testCase = Integer.parseInt(sc.nextLine());
        int n = Integer.parseInt(sc.nextLine());

        if (testCase == 1) { // students
            int studentScenario = Integer.parseInt(sc.nextLine());
            List<Person> students = new ArrayList<>();
            while (n > 0) {

                String line = sc.nextLine();
                String[] parts = line.split("\\s+");
                String id = parts[0];
                List<Integer> grades = Arrays.stream(parts).skip(1).map(Integer::parseInt).collect(Collectors.toList());
                students.add(new Person(id, grades));
                --n;
            }

            if (studentScenario == 1) {
                try {
                    FilterAndSort.execute(students,
                            stud -> stud.labAssistantPoints() >= 8.0
                                    && stud.getYear() >= 3);
                } catch (EmptyResultException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                try {
                    FilterAndSort.execute(students,
                            stud -> stud.average() >= 9.0 &&
                                    (double) stud.grades.size() / stud.totalCourses() >0.9);
                } catch (EmptyResultException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else { //integers
            List<Integer> integers = new ArrayList<>();
            while (n > 0) {
                integers.add(Integer.parseInt(sc.nextLine()));
                --n;
            }

            try {
                FilterAndSort.execute(integers, integer -> integer % 2 == 0 && integer % 15 == 0);
            } catch (EmptyResultException e) {
                System.out.println(e.getMessage());;
            }
        }

    }
}
