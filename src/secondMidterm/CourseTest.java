package secondMidterm;

import java.util.*;
import java.util.stream.Collectors;

class StudentInCourse{
    String index;
    String name;
    int pointsFirst;
    int pointsSecond;
    int labs;


    public StudentInCourse(String index, String name) {
        this.index = index;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPointsFirst(int pointsFirst) {
        this.pointsFirst = pointsFirst;
    }

    public void setPointsSecond(int pointsSecond) {
        this.pointsSecond = pointsSecond;
    }

    public void setLabs(int labs) {
        this.labs = labs;
    }

    public double totalPoints(){
        return pointsFirst*0.45+pointsSecond*0.45+labs;
    }
    public int getGrade(){
        return Math.max(5,(int) (totalPoints()/10)+1);
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s First midterm: %d Second midterm %d Labs: %d Summary points: %.2f Grade: %d",
                index,name,pointsFirst,pointsSecond,labs,totalPoints(),getGrade());
    }
}
class AdvancedProgrammingCourse{
     Map<String,StudentInCourse>students;

    public AdvancedProgrammingCourse() {
        this.students=new HashMap<>();
    }
    public void addStudent(StudentInCourse s){
        students.put(s.index,s);
    }
    public void updateStudent(String idNumber, String activity, int points){
        if(points>100|| points<0) throw new RuntimeException();
        StudentInCourse s= students.get(idNumber);
        switch (activity) {
            case "midterm1" -> s.setPointsFirst(points);
            case "midterm2" -> s.setPointsSecond(points);
            case "labs" -> s.setLabs(points);
        }
    }
    public List<StudentInCourse> getFirstNStudents(int n){
       return students.values().stream()
                .sorted(Comparator.comparing(StudentInCourse::totalPoints).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }
    public Map<Integer, Integer>getGradeDistribution(){
        Map<Integer,Integer>gradeDistribution=new HashMap<>();
        for(int i=5;i<11;i++){
            gradeDistribution.put(i,0);
        }
        students.values().stream()
                .mapToInt(StudentInCourse::getGrade)
                .forEach(grade-> gradeDistribution.put(grade,gradeDistribution.get(grade)+1));
        return gradeDistribution;
    }
    public void printStatistics(){
        DoubleSummaryStatistics dss=new DoubleSummaryStatistics();
        students.values().stream()
                .filter(stud->stud.totalPoints()>=50)
                .mapToDouble(StudentInCourse::totalPoints)
                .forEach(dss);

        System.out.printf("Count: %d Min: %.2f Average: %.2f Max: %.2f\n",
                dss.getCount(),dss.getMin(),dss.getAverage(),dss.getMax());
    }
}

public class CourseTest {

    public static void printStudents(List<StudentInCourse> students) {
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

            switch (command) {
                case "addStudent" -> {
                    String id = parts[1];
                    String name = parts[2];
                    advancedProgrammingCourse.addStudent(new StudentInCourse(id, name));
                }
                case "updateStudent" -> {
                    String idNumber = parts[1];
                    String activity = parts[2];
                    int points = Integer.parseInt(parts[3]);
                    advancedProgrammingCourse.updateStudent(idNumber, activity, points);
                }
                case "getFirstNStudents" -> {
                    int n = Integer.parseInt(parts[1]);
                    printStudents(advancedProgrammingCourse.getFirstNStudents(n));
                }
                case "getGradeDistribution" -> printMap(advancedProgrammingCourse.getGradeDistribution());
                default -> advancedProgrammingCourse.printStatistics();
            }
        }
    }
}
