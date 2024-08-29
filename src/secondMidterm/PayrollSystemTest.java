package secondMidterm;

import java.io.*;
import java.util.*;


abstract class Employee {
    String id;
    String level;

    public Employee(String id, String level) {
        this.id = id;
        this.level = level;

    }

    public String getId() {
        return id;
    }

    abstract double getSalary();

    public String getLevel() {
        return level;
    }
}

class HourlyEmployee extends Employee {
    double hours;
    double salary;

    public HourlyEmployee(String id, String level, double hours) {
        super(id, level);
        this.hours = hours;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = hours > 40 ? 40 * salary + (hours - 40) * 1.5 * salary : hours * salary;
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f Regular hours: %.2f Overtime hours: %.2f",
                id, level, salary, (hours > 40) ? 40 : hours, (hours > 40) ? hours - 40 : 0);
    }
}

class FreelanceEmployee extends Employee {
    List<Integer> ticketPoints;
    double salary;

    public FreelanceEmployee(String id, String level, List<Integer> ticketPoints) {
        super(id, level);
        this.ticketPoints = ticketPoints;
    }

    public int sumTickets() {
        return ticketPoints.stream().mapToInt(i -> i).sum();
    }

    public void setSalary(double salary) {
        this.salary = sumTickets() * salary;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f Tickets count: %d Tickets points: %d",
                id, level, salary, ticketPoints.size(), sumTickets());
    }
}

class PayrollSystem {
    Map<String, Employee> employees;

    public PayrollSystem() {
        this.employees = new TreeMap<>();
    }

    public Employee createEmployee(String line) {
        String[] parts = line.split(";");
        char type = parts[0].charAt(0);
        String id = parts[1];
        String level = parts[2];
        if (type == 'F') {
            List<Integer> ticketPoints = new ArrayList<>();
            Arrays.stream(parts).skip(3).forEach(part -> ticketPoints.add(Integer.parseInt(part)));
            return new FreelanceEmployee(id, level, ticketPoints);
        }
        double hours = Double.parseDouble(parts[3]);
        return new HourlyEmployee(id, level, hours);
    }

    public void readEmployees(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        br.lines().forEach(line -> {
            Employee e = createEmployee(line);
            employees.put(e.id, e);
        });
    }

    public void setSalaries(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        employees.values().forEach(employee -> {
            if (employee instanceof HourlyEmployee) {
                double salary = hourlyRateByLevel.get(employee.level);
                ((HourlyEmployee) employee).setSalary(salary);
            } else {
                double salary = ticketRateByLevel.get(employee.level);
                ((FreelanceEmployee) employee).setSalary(salary);
            }
        });
    }

    Map<String, Set<Employee>> printEmployeesByLevels(Set<String> levels) {
        Map<String, Set<Employee>> employeesByLevels = new TreeMap<>();
        employees.values().forEach(employee -> {
            String level = employee.getLevel();
            if (levels.contains(level)) {
                employeesByLevels.putIfAbsent(level, new TreeSet<>(Comparator.comparing(Employee::getSalary).reversed()));
                employeesByLevels.get(level).add(employee);
            }
        });
        return employeesByLevels;
    }

}

public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem();

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees(System.in);
        payrollSystem.setSalaries(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i = 5; i <= 10; i++) {
            levels.add("level" + i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: " + level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
            System.out.println("------------");
        });
    }
}