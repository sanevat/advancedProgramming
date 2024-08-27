package secondMidterm;

import java.io.*;
import java.util.*;
class Person{
    String id;
    String program;
    List<Integer>grades;

    public Person(String id, String program, List<Integer> grades) {
        this.id = id;
        this.program = program;
        this.grades = grades;
    }
    public double averageGrade(){
        return grades.stream().mapToInt(i->i).average().orElse(0.0);
    }


    @Override
    public String toString() {
        return String.format("%s %.2f",id,averageGrade());
    }

    public String getId() {
        return id;
    }
}
class PersonRecords{
    Map<String,Person>students;
    Map<String,List<Person>>studentsPerProgram;
    Map<String,Map<Integer,Integer>>distributinMap;

    public PersonRecords() {
        students=new HashMap<>();
        studentsPerProgram=new TreeMap<>();
        distributinMap=new TreeMap<>();
    }

    public int readRecords(InputStream in) {
        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        br.lines().forEach(this::readLine);
        return students.keySet().size();
    }
    public void readLine(String line){
        String[]parts=line.split("\\s+");
        String id=parts[0];
        String program=parts[1];
        List<Integer>grades=new ArrayList<>();
        Arrays.stream(parts)
                .skip(2)
                .forEach(part->grades.add(Integer.parseInt(part)));
        Person p=new Person(id,program,grades);

        if(!students.containsKey(id)){
            students.put(id,null);
        }
        if(!studentsPerProgram.containsKey(program)){
            studentsPerProgram.put(program,new ArrayList<>());
        }
        if(!distributinMap.containsKey(program)){
            distributinMap.put(program,new HashMap<>());
        }
        students.put(id,p);
        studentsPerProgram.get(program).add(p);
    }
    public void distribution(){
        for (Map<Integer, Integer> map : distributinMap.values()) {
            for (int i = 6; i <= 10; i++) {
                map.putIfAbsent(i, 0);
            }
        }

        for (Person person : students.values()) {
            Map<Integer, Integer> map = distributinMap.get(person.program);
            for (int grade : person.grades) {
                map.put(grade, map.getOrDefault(grade, 0) + 1);
            }
        }
    }

    public void writeTable(OutputStream out) {
        PrintWriter pw=new PrintWriter(out);
        studentsPerProgram.forEach((key, value) -> {
            pw.println(key);
            value.stream()
                    .sorted(Comparator.comparing(Person::averageGrade).reversed().thenComparing(Person::getId))
                    .forEach(pw::println);
        });
        pw.flush();
    }

    public void writeDistribution(PrintStream out) {
        PrintWriter pw=new PrintWriter(out);
        distribution();
        List<Map.Entry<String, Map<Integer, Integer>>> sortedPrograms = new ArrayList<>(distributinMap.entrySet());

        sortedPrograms.sort(Comparator.comparingInt((Map.Entry<String, Map<Integer, Integer>> entry) ->
                entry.getValue().getOrDefault(10, 0)).reversed());

        sortedPrograms.forEach(entry -> {
            String program = entry.getKey();
            Map<Integer, Integer> distribution = entry.getValue();
            pw.println(program);
            distribution.forEach((grade, count) -> {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("%2d", grade));
                sb.append(" | ");
                sb.append("*".repeat(Math.max(0, (count%10==0)?(count / 10):(count/10+1))));
                sb.append("(").append(count).append(")");
                pw.println(sb);
            });
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