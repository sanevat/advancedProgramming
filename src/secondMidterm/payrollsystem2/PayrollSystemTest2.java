package secondMidterm.payrollsystem2;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

class BonusNotAllowedException extends Exception {
    public BonusNotAllowedException(String message) {
        super(message);
    }
}

abstract class Employee {
    String id;
    String level;
    double percentageBonus;
    double wholeBonus;

    public Employee(String id, String level) {
        this.id = id;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setPercentageBonus(double percentageBonus) {
        this.percentageBonus = percentageBonus;
    }

    public void setWholeBonus(double wholeBonus) {
        this.wholeBonus = wholeBonus;
    }

    abstract void calculateBonus();

    abstract double getBonus();

    abstract int getTickets();

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s", id, level);
    }
}

class HourlyEmployee extends Employee {
    double hours;
    double salary;
    double bonus;
    double overTimeSalary;

    public HourlyEmployee(String id, String level, double hours) {
        super(id, level);
        this.hours = hours;
    }

    public void setSalary(double salary) {
        this.salary = hours > 40 ? 40 * salary + (hours - 40) * 1.5 * salary : hours * salary;
    }

    public void setOverTimeSalary(double salary) {
        this.overTimeSalary = (hours - 40) * 1.5 * salary;
    }

    public double getOvertimeSalary() {
        return overTimeSalary;
    }

    public void calculateBonus() {
        if (percentageBonus == 0)
            this.bonus = wholeBonus;
        else
            this.bonus = percentageBonus / 100.0 * salary;
        this.salary += bonus;
    }

    public int getTickets() {
        return 0;
    }

    @Override
    double getBonus() {
        return bonus;
    }

    @Override
    public String toString() {
        String s = super.toString() + String.format(" Salary: %.2f Regular hours: %.2f Overtime hours: %.2f", salary, (hours > 40) ? 40 : hours, (hours > 40) ? hours - 40 : 0);
        if (getBonus() > 0) s += String.format(" Bonus: %.2f", getBonus());
        return s;
    }
}

class FreelanceEmployee extends Employee {
    List<Integer> ticketPoints;
    double salary;
    double bonus;

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

    public void calculateBonus() {
        if (percentageBonus == 0)
            this.bonus = wholeBonus;
        else
            this.bonus = salary * percentageBonus / 100.0;
        this.salary += bonus;
    }

    public int getTickets() {
        return ticketPoints.size();
    }

    @Override
    double getBonus() {
        return bonus;
    }

    @Override
    public String toString() {
        String s = super.toString() + String.format(" Salary: %.2f Tickets count: %d Tickets points: %d", salary, ticketPoints.size(), sumTickets());
        if (getBonus() > 0) s += String.format(" Bonus: %.2f", getBonus());
        return s;
    }
}

class PayrollSystem {
    Map<String, Employee> employees;

    public PayrollSystem() {
        this.employees = new TreeMap<>();
    }

    public Employee createEmployee(String line) throws BonusNotAllowedException {
        String[] bonusEmployee = line.split("\\s+");
        double percentageBonus = 0, wholeBonus = 0;

        if (bonusEmployee.length == 2) {
            String bonus = bonusEmployee[1];
            if (bonus.endsWith("%")) percentageBonus = Double.parseDouble(bonus.replace("%", ""));
            else wholeBonus = Double.parseDouble(bonus);
        }

        if (percentageBonus > 20)
            throw new BonusNotAllowedException(String.format("Bonus of %.2f%% is not allowed", percentageBonus));
        if (wholeBonus > 1000)
            throw new BonusNotAllowedException(String.format("Bonus of %.0f$ is not allowed", wholeBonus));

        String[] parts = bonusEmployee[0].split(";");
        Employee e = parts[0].charAt(0) == 'F' ?
                new FreelanceEmployee(parts[1], parts[2], Arrays.stream(parts).skip(3).map(Integer::parseInt).collect(Collectors.toList())) :
                new HourlyEmployee(parts[1], parts[2], Double.parseDouble(parts[3]));

        employees.put(e.id, e);
        e.setPercentageBonus(percentageBonus);
        e.setWholeBonus(wholeBonus);
        return e;
    }

    public void setSalaries(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        employees.values().forEach(employee -> {
            if (employee instanceof HourlyEmployee) {
                double salary = hourlyRateByLevel.get(employee.level);
                ((HourlyEmployee) employee).setSalary(salary);
                ((HourlyEmployee) employee).setOverTimeSalary(salary);
            } else {
                double salary = ticketRateByLevel.get(employee.level);
                ((FreelanceEmployee) employee).setSalary(salary);
            }
            employee.calculateBonus();
        });
    }

    public Map<String, Double> getOvertimeSalaryForLevels() {
        Map<String, Double> overtimeSalary = new HashMap<>();
        employees.values().stream()
                .filter(employee -> employee instanceof HourlyEmployee && ((HourlyEmployee) employee).hours > 40)
                .forEach(employee -> {
                    overtimeSalary.putIfAbsent(employee.level, 0.0);
                    overtimeSalary.put(employee.level, overtimeSalary.get(employee.level) + ((HourlyEmployee) employee).getOvertimeSalary());
                });
        return overtimeSalary;
    }

    public void printStatisticsForOvertimeSalary() {
        DoubleSummaryStatistics dss = new DoubleSummaryStatistics();
        employees.values().stream()
                .filter(employee -> employee instanceof HourlyEmployee)
                .mapToDouble(employee -> ((HourlyEmployee) employee).getOvertimeSalary())
                .forEach(dss);
        System.out.printf("Statistics for overtime salary: Min: %.2f Average: %.2f Max: %.2f Sum: %.2f%n",
                dss.getMin(), dss.getAverage(), dss.getMax(), dss.getSum());

    }

    public Map<String, Integer> ticketsDoneByLevel() {
        Map<String, Integer> ticketsByLevel = new HashMap<>();
        employees.values().stream()
                .filter(employee -> employee instanceof FreelanceEmployee)
                .forEach(employee -> {
                    ticketsByLevel.putIfAbsent(employee.level, 0);
                    ticketsByLevel.put(employee.level, ticketsByLevel.get(employee.level) + employee.getTickets());
                });
        return ticketsByLevel;
    }

    public Collection<Employee> getFirstNEmployeesByBonus(int i) {
        return employees.values().stream().sorted(Comparator.comparing(Employee::getBonus).reversed())
                .limit(i)
                .collect(Collectors.toList());
    }
}


public class PayrollSystemTest2 {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 11 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5.5 + i * 2.5);
        }

        Scanner sc = new Scanner(System.in);

        int employeesCount = Integer.parseInt(sc.nextLine());

        PayrollSystem ps = new PayrollSystem();
        Employee emp = null;
        for (int i = 0; i < employeesCount; i++) {
            try {
                emp = ps.createEmployee(sc.nextLine());
            } catch (BonusNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }
        ps.setSalaries(hourlyRateByLevel, ticketRateByLevel);

        int testCase = Integer.parseInt(sc.nextLine());

        switch (testCase) {
            case 1: //Testing createEmployee
                if (emp != null)
                    System.out.println(emp);
                break;
            case 2: //Testing getOvertimeSalaryForLevels()
                ps.getOvertimeSalaryForLevels().forEach((level, overtimeSalary) ->
                        System.out.printf("Level: %s Overtime salary: %.2f\n", level, overtimeSalary));
                break;
            case 3: //Testing printStatisticsForOvertimeSalary()
                ps.printStatisticsForOvertimeSalary();
                break;
            case 4: //Testing ticketsDoneByLevel
                ps.ticketsDoneByLevel().forEach((level, overtimeSalary) ->
                        System.out.printf("Level: %s Tickets by level: %d\n", level, overtimeSalary));
                break;
            case 5: //Testing getFirstNEmployeesByBonus (int n)
                ps.getFirstNEmployeesByBonus(Integer.parseInt(sc.nextLine())).forEach(System.out::println);
                break;
        }

    }
}