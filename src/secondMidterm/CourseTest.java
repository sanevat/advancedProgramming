package secondMidterm;

import java.util.*;
import java.util.stream.Collectors;

class Student{
    String index;
    String name;
    int firstMid;
    int secondMid;
    int labExercises;

    public Student(String index, String name) {
        this.index = index;
        this.name = name;
    }

    public void setFirstMid(int firstMid) {
        this.firstMid = firstMid;
    }

    public void setSecondMid(int secondMid) {
        this.secondMid = secondMid;
    }

    public void setLabExercises(int labExercises) {
        this.labExercises = labExercises;
    }
    public double calculateTotalPoints(){
        return firstMid*0.45+secondMid*0.45+labExercises;
    }
    public int calculateGrade(){
        double totalPoints=calculateTotalPoints();
        if(totalPoints<50)return 5;
        else if(totalPoints<60) return 6;
        else if(totalPoints<70)return 7;
        else if(totalPoints<80)return 8;
        else if(totalPoints<90)return 9;
        else return 10;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s First midterm: %d Second midterm %d Labs: %d Summary points: %.2f Grade: %d",
                index,name,firstMid,secondMid,labExercises,calculateTotalPoints(),calculateGrade());
    }
}
class AdvancedProgrammingCourse{
    Set<Student>students;
    Map<String,Student>studentsMap;

    public AdvancedProgrammingCourse() {
        this.students=new HashSet<>();
        studentsMap=new HashMap<>();
    }
    public void addStudent (Student s){
        students.add(s);
        studentsMap.put(s.index,s);
    }
    public void updateStudent (String idNumber, String activity, int points){
        if(points>100||points<0)throw new RuntimeException();

        Student studentToUpdate= studentsMap.get(idNumber);

        switch (activity) {
            case "midterm1" -> studentToUpdate.setFirstMid(points);
            case "midterm2" -> studentToUpdate.setSecondMid(points);
            case "labs" -> studentToUpdate.setLabExercises(points);
        }
    }

    public List<Student>getFirstNStudents(int n){
        return students.stream()
                .sorted(Comparator.comparing(Student::calculateTotalPoints).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }
    public Map<Integer, Integer> getGradeDistribution(){
        Map<Integer,Integer>gradeDistribution=new HashMap<>();
        for(int grade=5;grade<=10;grade++){
            gradeDistribution.put(grade,0);
        }
        students.forEach(student->{
            int grade=student.calculateGrade();
           gradeDistribution.put(grade,gradeDistribution.get(grade)+1);
        });
        return gradeDistribution;
    }
    public void printStatistics(){
        DoubleSummaryStatistics doubleSummaryStatistics=new DoubleSummaryStatistics();

       students.stream().filter(student->student.calculateTotalPoints()>49)
               .forEach(student -> doubleSummaryStatistics.accept(student.calculateTotalPoints()));
       System.out.printf("Count: %d Min: %.2f Average: %.2f Max: %.2f",
                doubleSummaryStatistics.getCount(),
                doubleSummaryStatistics.getMin(),
                doubleSummaryStatistics.getAverage(),
                doubleSummaryStatistics.getMax());
    }
}
public class CourseTest {

    public static void printStudents(List<Student> students) {
        students.forEach(System.out::println);
    }

    public static void printMap(Map<Integer, Integer> map) {
        map.forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
    }

    public static void main(String[] args) {
        AdvancedProgrammingCourse advancedProgrammingCourse = new AdvancedProgrammingCourse();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            String command = parts[0];

            if (command.equals("addStudent")) {
                String id = parts[1];
                String name = parts[2];
                advancedProgrammingCourse.addStudent(new Student(id, name));
            } else if (command.equals("updateStudent")) {
                String idNumber = parts[1];
                String activity = parts[2];
                int points = Integer.parseInt(parts[3]);
                advancedProgrammingCourse.updateStudent(idNumber, activity, points);
            } else if (command.equals("getFirstNStudents")) {
                int n = Integer.parseInt(parts[1]);
                printStudents(advancedProgrammingCourse.getFirstNStudents(n));
            } else if (command.equals("getGradeDistribution")) {
                printMap(advancedProgrammingCourse.getGradeDistribution());
            } else {
                advancedProgrammingCourse.printStatistics();
            }
        }
    }
}
