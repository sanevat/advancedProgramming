package secondMidterm;

import java.util.*;
import java.util.stream.Collectors;

class Student{
    String id;
    List<Integer> pointsLabs;

    public Student(String id, List<Integer> pointsLabs) {
        this.id = id;
        this.pointsLabs = pointsLabs;
    }

    public String getId() {
        return id;
    }

    public double totalPoints(){
        return pointsLabs.stream().mapToInt(Integer::valueOf).sum()/(double)10;
    }
    public boolean hasSignature(){
        return pointsLabs.size()>=8;
    }
    public int getYearOfStudies(){
        return 20-Integer.parseInt(id.substring(0,2));
    }

    @Override
    public String toString() {
        return String.format("%s %s %.2f",id,(pointsLabs.size()>=8)?"YES":"NO",totalPoints());
    }
}
class LabExercises{
    List<Student> students;

    public LabExercises() {
        students=new ArrayList<>();
    }
    public void addStudent(Student s){
        students.add(s);
    }
    public void printByAveragePoints(boolean ascending, int n){
        Comparator<Student> c=Comparator.comparing(Student::totalPoints).thenComparing(Student::getId);
        if(!ascending)
            c=c.reversed();

        students.stream()
                .sorted(c)
                .limit(n)
                .forEach(System.out::println);
    }
    public List<Student> failedStudents (){
        return students.stream()
                .filter(stud->!stud.hasSignature())
                .sorted(Comparator.comparing(Student::getId).thenComparing(Student::totalPoints))
                .collect(Collectors.toList());
    }
    public int countStudentsByYear(int year){
        return (int) students.stream()
                .filter(stud->stud.getYearOfStudies()==year && stud.hasSignature())
                .count();
    }
    public Map<Integer,Double> getStatisticsByYear(){
        Map<Integer,Double>statistics=new HashMap<>();
        students.stream()
                .filter(Student::hasSignature)
                .forEach(stud->{
                    int year=stud.getYearOfStudies();
                    statistics.putIfAbsent(year,0.0);
                    statistics.put(year,statistics.get(year)+stud.totalPoints());
                });
        statistics.forEach((key,value)->statistics.put(key,value/countStudentsByYear(key)));
        return statistics;
    }

}

public class LabExercisesTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LabExercises labExercises = new LabExercises();
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            String index = parts[0];
            List<Integer> points = Arrays.stream(parts).skip(1)
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());

            labExercises.addStudent(new Student(index, points));
        }

        System.out.println("===printByAveragePoints (ascending)===");
        labExercises.printByAveragePoints(true, 100);
        System.out.println("===printByAveragePoints (descending)===");
        labExercises.printByAveragePoints(false, 100);
        System.out.println("===failed students===");
        labExercises.failedStudents().forEach(System.out::println);
        System.out.println("===statistics by year");
        labExercises.getStatisticsByYear().entrySet().stream()
                .map(entry -> String.format("%d : %.2f", entry.getKey(), entry.getValue()))
                .forEach(System.out::println);

    }
}