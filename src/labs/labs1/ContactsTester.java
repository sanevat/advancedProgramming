package labs.labs1;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

enum Operator {
    VIP, ONE, TMOBILE
}

abstract class Contact {
    LocalDate date;
    public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Contact(String date) {
        this.date = LocalDate.parse(date, dtf);
    }

    public boolean isNewerThan(Contact c) {
        return date.isAfter(c.date);
    }

    abstract String getType();
}

class EmailContact extends Contact {
    String email;

    public EmailContact(String date, String email) {
        super(date);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    String getType() {
        return "Email";
    }

    @Override
    public String toString() {
        return "\"" + email + "\"";
    }
}

class PhoneContact extends Contact {
    String phone;
    Operator operator;

    public PhoneContact(String date, String phone) {
        super(date);
        this.phone = phone;
        if (phone.charAt(2) == '0' || phone.charAt(2) == '1' || phone.charAt(2) == '2')
            this.operator = Operator.TMOBILE;
        else if (phone.charAt(2) == '5' || phone.charAt(2) == '6')
            this.operator = Operator.ONE;
        else this.operator = Operator.VIP;
    }

    public String getPhone() {
        return phone;
    }

    public Operator getOperator() {
        return this.operator;
    }

    @Override
    String getType() {
        return "Phone";
    }

    @Override
    public String toString() {
        return "\"" + phone + "\"";
    }
}

class Student {
    String firstName;
    String lastName;
    String city;
    int age;
    long index;
    Contact[] contacts;
    int size;

    public Student(String firstName, String lastName, String city, int age, long index) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.age = age;
        this.index = index;
        this.size = 0;
        contacts = new Contact[0];
    }

    public void addEmailContact(String date, String email) {
        contacts = Arrays.copyOf(contacts, size + 1);
        contacts[size++] = new EmailContact(date, email);
    }

    public void addPhoneContact(String date, String phone) {
        contacts = Arrays.copyOf(contacts, size + 1);
        contacts[size++] = new PhoneContact(date, phone);
    }

    public Contact[] getEmailContacts() {
        return Arrays.stream(contacts).filter(c -> c.getType().equals("Email"))
                .toArray(Contact[]::new);
    }

    public Contact[] getPhoneContacts() {
        return Arrays.stream(contacts).filter(c -> c.getType().equals("Phone"))
                .toArray(Contact[]::new);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getCity() {
        return city;
    }

    public long getIndex() {
        return index;
    }

    public Contact getLatestContact() {
        Contact latest = contacts[0];
        for (Contact c : contacts)
            if (c.isNewerThan(latest))
                latest = c;
        return latest;
    }

    @Override
    public String toString() {
        return "{" +
                "\"ime\":\"" + firstName + "\"" +
                ", \"prezime\":\"" + lastName + "\"" +
                ", \"vozrast\":" + age +
                ", \"grad\":\"" + city + "\"" +
                ", \"indeks\":" + index +
                ", \"telefonskiKontakti\":" + Arrays.toString(getPhoneContacts()) +
                ", \"emailKontakti\":" + Arrays.toString(getEmailContacts()) +
                '}';
    }
}

class Faculty {
    String name;
    Student[] students;

    public Faculty(String name, Student[] students) {
        this.name = name;
        this.students = Arrays.copyOf(students, students.length);
    }

    public int countStudentsFromCity(String cityName) {
        return (int) Arrays.stream(students).filter(s -> s.getCity().equals(cityName)).count();
    }

    public Student getStudent(long index) {
        return Arrays.stream(students).filter(s -> s.getIndex() == index).findFirst().get();
    }

    public double getAverageNumberOfContacts() {
        return Arrays.stream(students).mapToInt(stud -> stud.contacts.length).average().orElse(0.0);
    }

    public Student getStudentWithMostContacts() {
        return Arrays.stream(students)
                .max(Comparator.comparing((Student stud) -> stud.contacts.length)
                        .thenComparing(Student::getIndex)).get();
    }

    @Override
    public String toString() {
        return "{\"fakultet\":\"" + name +"\""+
                ", \"studenti\":" + Arrays.toString(students) +
                '}';
    }
}


public class ContactsTester {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int tests = scanner.nextInt();
        Faculty faculty = null;

        int rvalue = 0;
        long rindex = -1;

        DecimalFormat df = new DecimalFormat("0.00");

        for (int t = 0; t < tests; t++) {

            rvalue++;
            String operation = scanner.next();

            switch (operation) {
                case "CREATE_FACULTY": {
                    String name = scanner.nextLine().trim();
                    int N = scanner.nextInt();

                    Student[] students = new Student[N];

                    for (int i = 0; i < N; i++) {
                        rvalue++;

                        String firstName = scanner.next();
                        String lastName = scanner.next();
                        String city = scanner.next();
                        int age = scanner.nextInt();
                        long index = scanner.nextLong();

                        if ((rindex == -1) || (rvalue % 13 == 0))
                            rindex = index;

                        Student student = new Student(firstName, lastName, city,
                                age, index);
                        students[i] = student;
                    }

                    faculty = new Faculty(name, students);
                    break;
                }

                case "ADD_EMAIL_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String email = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addEmailContact(date, email);
                    break;
                }

                case "ADD_PHONE_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String phone = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addPhoneContact(date, phone);
                    break;
                }

                case "CHECK_SIMPLE": {
                    System.out.println("Average number of contacts: "
                            + df.format(faculty.getAverageNumberOfContacts()));

                    rvalue++;

                    String city = faculty.getStudent(rindex).getCity();
                    System.out.println("Number of students from " + city + ": "
                            + faculty.countStudentsFromCity(city));

                    break;
                }

                case "CHECK_DATES": {

                    rvalue++;

                    System.out.print("Latest contact: ");
                    Contact latestContact = faculty.getStudent(rindex)
                            .getLatestContact();
                    if (latestContact.getType().equals("Email"))
                        System.out.println(((EmailContact) latestContact)
                                .getEmail());
                    if (latestContact.getType().equals("Phone"))
                        System.out.println(((PhoneContact) latestContact)
                                .getPhone()
                                + " ("
                                + ((PhoneContact) latestContact).getOperator()
                                .toString() + ")");

                    if (faculty.getStudent(rindex).getEmailContacts().length > 0
                            && faculty.getStudent(rindex).getPhoneContacts().length > 0) {
                        System.out.print("Number of email and phone contacts: ");
                        System.out
                                .println(faculty.getStudent(rindex)
                                        .getEmailContacts().length
                                        + " "
                                        + faculty.getStudent(rindex)
                                        .getPhoneContacts().length);

                        System.out.print("Comparing dates: ");
                        int posEmail = rvalue
                                % faculty.getStudent(rindex).getEmailContacts().length;
                        int posPhone = rvalue
                                % faculty.getStudent(rindex).getPhoneContacts().length;

                        System.out.println(faculty.getStudent(rindex)
                                .getEmailContacts()[posEmail].isNewerThan(faculty
                                .getStudent(rindex).getPhoneContacts()[posPhone]));
                    }

                    break;
                }

                case "PRINT_FACULTY_METHODS": {
                    System.out.println("Faculty: " + faculty.toString());
                    System.out.println("Student with most contacts: "
                            + faculty.getStudentWithMostContacts().toString());
                    break;
                }

            }

        }

        scanner.close();
    }
}
