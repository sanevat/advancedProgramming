package secondMidterm;

import java.util.*;

//TODO: fix printCourses method

class OperationNotAllowedException extends Exception {
    public OperationNotAllowedException(String id, int term) {
        super(String.format("Student %s already has 3 grades in term %d", id, term));
    }

    public OperationNotAllowedException(int term, String id) {
        super(String.format("Term %d is not possible for student with ID %s", term, id));
    }
}

class Term {
    int termID;
    Map<String, List<Integer>> subjects;

    public Term(int termID) {
        this.termID = termID;
        this.subjects = new HashMap<>();
    }

    public void addSubject(String name, int grade) {
        subjects.putIfAbsent(name, new ArrayList<>());
        subjects.get(name).add(grade);
    }

    public int subjectsPassed() {
        return subjects.size();
    }
    public double averageGrade() {
        return subjects.values().stream()
                .flatMap(Collection::stream)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(5.0);
    }
}

class StudentFaculty {
    String id;
    int yearOfStudies;
    Map<Integer, Term> terms;

    public StudentFaculty(String id, int yearOfStudies) {
        this.yearOfStudies = yearOfStudies;
        this.terms = new HashMap<>(yearOfStudies * 2);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int totalSubjectsPassed() {
        return terms.values().stream().mapToInt(Term::subjectsPassed).sum();
    }

    public double averageGrade() {
        int totalGrades = terms.values().stream()
                .mapToInt(term -> term.subjects.values().stream()
                        .flatMap(Collection::stream)
                        .mapToInt(Integer::intValue)
                        .sum())
                .sum();
        int totalSubjects = terms.values().stream()
                .mapToInt(term -> term.subjects.values().stream()
                        .mapToInt(List::size)
                        .sum())
                .sum();
        return totalSubjects == 0 ? 5.0 : totalGrades / (double) totalSubjects;
    }

    public String termsToString() {
        StringBuilder sb = new StringBuilder();
        Set<String> subjects = new TreeSet<>();
        for (Term term : terms.values()) {
            subjects.addAll(term.subjects.keySet());
        }
        subjects.forEach(sub -> sb.append(sub).append(","));
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Student: %s Courses passed: %d Average grade: %.2f",
                id, totalSubjectsPassed(), averageGrade());
    }
}

class Faculty {
    Map<String, StudentFaculty> students;
    List<String> logs;
    Map<String, Integer> numberOfStudentsPerSubject;

    public Faculty() {
        this.students = new HashMap<>();
        this.logs = new ArrayList<>();
        this.numberOfStudentsPerSubject = new HashMap<>();
    }

    void addStudent(String id, int yearsOfStudies) {
        students.put(id, new StudentFaculty(id, yearsOfStudies));
    }

    void addGradeToStudent(String studentId, int term, String courseName, int grade) throws OperationNotAllowedException {
        StudentFaculty s = students.get(studentId);
        if (s == null) {
            throw new IllegalArgumentException("Student not found");
        }
        if (s.yearOfStudies == 3 && term > 6 || s.yearOfStudies == 4 && term > 8) {
            throw new OperationNotAllowedException(term, studentId);
        }
        s.terms.putIfAbsent(term, new Term(term));

        if (s.terms.get(term).subjects.values().stream().mapToInt(List::size).sum() >= 3) {
            throw new OperationNotAllowedException(studentId, term);
        }

        s.terms.get(term).addSubject(courseName, grade);
        numberOfStudentsPerSubject.putIfAbsent(courseName, 0);
        numberOfStudentsPerSubject.put(courseName, numberOfStudentsPerSubject.get(courseName) + 1);

        detectGraduation(studentId);
    }

    public double getAverageGradeOfSubject(String subject) {
        int totalGrades = 0;
        int totalStudents = 0;

        for (StudentFaculty student : students.values()) {
            for (Term term : student.terms.values()) {
                if (term.subjects.containsKey(subject)) {
                    totalGrades += term.subjects.get(subject).stream().mapToInt(Integer::intValue).sum();
                    totalStudents += term.subjects.get(subject).size();
                }
            }
        }

        if (totalStudents == 0) {
            return 5.0;
        }

        return totalGrades / (double) totalStudents;
    }

    public void detectGraduation(String id) {
        StudentFaculty student = students.get(id);
        if (student == null) {
            return;
        }
        int coursesPassed = student.totalSubjectsPassed();
        if (coursesPassed >= 18 && student.yearOfStudies == 3 ||
                coursesPassed >= 24 && student.yearOfStudies == 4) {
            logs.add(String.format("Student with ID %s graduated with average grade %.2f in %d years",
                    id, student.averageGrade(), student.yearOfStudies));
            students.remove(id);
        }
    }

    String getFacultyLogs() {
        StringBuilder sb = new StringBuilder();
        logs.forEach(log -> sb.append(log).append(".\n"));
        return sb.toString();
    }

    String getDetailedReportForStudent(String id) {
        detectGraduation(id);
        StringBuilder sb = new StringBuilder();
        sb.append("Student: ").append(id).append("\n");
        StudentFaculty s = students.get(id);

        if (s == null) {
            return "Student not found.";
        }

        for (int i = 1; i <= s.yearOfStudies * 2; i++) {
            sb.append("Term ").append(i).append("\n");

            if (s.terms.containsKey(i)) {
                Term term = s.terms.get(i);
                sb.append("Courses: ").append(term.subjects.size()).append("\n");
                sb.append("Average grade for term: ")
                        .append(String.format("%.2f", term.averageGrade()))
                        .append("\n");
            } else {
                sb.append("Courses: 0\n");
                sb.append("Average grade for term: 5.00\n");
            }
        }

        sb.append("Average grade: ").append(String.format("%.2f", s.averageGrade())).append("\n");
        sb.append("Courses attended: ").append(s.termsToString()).append("\n");

        return sb.toString();
    }

    void printFirstNStudents(int n) {
        students.values().stream()
                .sorted(Comparator.comparing(StudentFaculty::totalSubjectsPassed)
                        .thenComparing(StudentFaculty::averageGrade)
                        .thenComparing(StudentFaculty::getId).reversed())
                .limit(n)
                .forEach(System.out::println);
    }

    void printCourses() {
        numberOfStudentsPerSubject.entrySet().stream()
                .sorted((entry1, entry2) -> {
                    int compareAverage = entry1.getValue().compareTo(entry2.getValue());
                    if (compareAverage == 0) {
                        return Double.compare(getAverageGradeOfSubject(entry1.getKey()), getAverageGradeOfSubject(entry2.getKey()));
                    }
                    return compareAverage;
                })
                .forEach(entry -> System.out.println(String.format("%s %d %.2f", entry.getKey(), entry.getValue(), getAverageGradeOfSubject(entry.getKey()))));
    }
}

public class FacultyTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();

        if (testCase == 1) {
            System.out.println("TESTING addStudent AND printFirstNStudents");
            Faculty faculty = new Faculty();
            for (int i = 0; i < 10; i++) {
                faculty.addStudent("student" + i, (i % 2 == 0) ? 3 : 4);
            }
            faculty.printFirstNStudents(10);

        } else if (testCase == 2) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            try {
                faculty.addGradeToStudent("123", 7, "NP", 10);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
            try {
                faculty.addGradeToStudent("1234", 9, "NP", 8);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        } else if (testCase == 3) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("123", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("1234", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else if (testCase == 4) {
            System.out.println("Testing addGrade for graduation");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            int counter = 1;
            for (int i = 1; i <= 6; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("123", i, "course" + counter, (i % 2 == 0) ? 7 : 8);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            counter = 1;
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("1234", i, "course" + counter, (j % 2 == 0) ? 7 : 10);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            System.out.println("LOGS");
            System.out.print(faculty.getFacultyLogs());
            System.out.println("PRINT STUDENTS (there shouldn't be anything after this line!");
            faculty.printFirstNStudents(2);
        } else if (testCase == 5 || testCase == 6 || testCase == 7) {
            System.out.println("Testing addGrade and printFirstNStudents (not graduated student)");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), i % 5 + 6);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            if (testCase == 5)
                faculty.printFirstNStudents(10);
            else if (testCase == 6)
                faculty.printFirstNStudents(3);
            else
                faculty.printFirstNStudents(20);
        } else if (testCase == 8 || testCase == 9) {
            System.out.println("TESTING DETAILED REPORT");
            Faculty faculty = new Faculty();
            faculty.addStudent("student1", ((testCase == 8) ? 3 : 4));
            int grade = 6;
            int counterCounter = 1;
            for (int i = 1; i < ((testCase == 8) ? 6 : 8); i++) {
                for (int j = 1; j < 3; j++) {
                    try {
                        faculty.addGradeToStudent("student1", i, "course" + counterCounter, grade);
                    } catch (OperationNotAllowedException e) {
                        e.printStackTrace();
                    }
                    grade++;
                    if (grade == 10)
                        grade = 5;
                    ++counterCounter;
                }
            }
            System.out.println(faculty.getDetailedReportForStudent("student1"));
        } else if (testCase==10) {
            System.out.println("TESTING PRINT COURSES");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            faculty.printCourses();
        } else if (testCase==11) {
            System.out.println("INTEGRATION TEST");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 2 : 3); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }

            }

            for (int i=11;i<15;i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= 3; k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            System.out.println("LOGS");
            System.out.print(faculty.getFacultyLogs());
            System.out.println("DETAILED REPORT FOR STUDENT");
            System.out.print(faculty.getDetailedReportForStudent("student2"));
            try {
                System.out.println(faculty.getDetailedReportForStudent("student11"));
                System.out.println("The graduated students should be deleted!!!");
            } catch (NullPointerException e) {
                System.out.println("The graduated students are really deleted");
            }
            System.out.println("FIRST N STUDENTS");
            faculty.printFirstNStudents(10);
            System.out.println("COURSES");
            faculty.printCourses();
        }
    }
}
