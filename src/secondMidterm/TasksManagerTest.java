package secondMidterm;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

class DeadlineNotValidException extends Exception {
    public DeadlineNotValidException(String message) {
        super(message);
    }
}

class Task {
    String category;
    String name;
    String description;
    LocalDateTime deadline;
    int priority;

    public Task(String category, String name, String description, LocalDateTime deadline, int priority) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
    }

    public Task(String category, String name, String description, LocalDateTime deadline) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.priority = Integer.MAX_VALUE;
    }

    public Task(String category, String name, String description, int priority) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.deadline = LocalDateTime.MAX;
    }

    public Task(String category, String name, String description) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.priority = Integer.MAX_VALUE;
        this.deadline = LocalDateTime.MAX;
    }

    public String getName() {
        return name;
    }

    public long timeDifference() {
        return Duration.between(deadline, LocalDateTime.now()).toSeconds();
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Task{").append("name='").append(name).append('\'').append(", description='")
                .append(description).append('\'');
        if (deadline != LocalDateTime.MAX)
            sb.append(", deadline=").append(deadline);
        if (priority != Integer.MAX_VALUE) {
            sb.append(", priority=").append(priority);
        }
        sb.append('}');
        return sb.toString();
    }
}

class TaskManager {
    Map<String, Set<Task>> tasksByCategory;
    Set<Task> tasks;

    public TaskManager() {
        this.tasksByCategory = new TreeMap<>();
        this.tasks = new LinkedHashSet<>();
    }

    public void readTasks(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        br.lines().forEach(line -> {
            Task t;
            try {
                t = createTask(line);
                tasksByCategory.putIfAbsent(t.category, new LinkedHashSet<>());
                tasksByCategory.get(t.category).add(t);
                tasks.add(t);
            } catch (DeadlineNotValidException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    public Task createTask(String line) throws DeadlineNotValidException {
        String[] parts = line.split(",");
        String category = parts[0];
        String name = parts[1];
        String description = parts[2];

        if (parts.length == 3) {
            return new Task(category, name, description);
        }

        if (parts.length == 5) {
            LocalDateTime ldt = LocalDateTime.parse(parts[3]);
            int priority = Integer.parseInt(parts[4]);
            if (ldt.isBefore(LocalDateTime.of(2020, 6, 2, 23, 59, 59))) {
                throw new DeadlineNotValidException("The deadline " + ldt + " has already passed");
            }
            return new Task(category, name, description, ldt, priority);
        }

        if (parts[3].contains("-")) {
            LocalDateTime deadline = LocalDateTime.parse(parts[3]);
            if (deadline.isBefore(LocalDateTime.of(2020, 6, 2, 23, 59, 59))) {
                throw new DeadlineNotValidException("The deadline " + deadline + " has already passed");
            }
            return new Task(category, name, description, deadline);
        } else {
            int priority = Integer.parseInt(parts[3]);
            return new Task(category, name, description, priority);
        }
    }

    public void printTasks(OutputStream out, boolean includePriority, boolean includeCategory) {
        PrintWriter pw = new PrintWriter(out);
        Comparator<Task> c;
        if (includePriority) {
            c = Comparator.comparing(Task::getPriority).thenComparing(Comparator.comparing(Task::timeDifference).reversed());
        } else {
            c = Comparator.comparing(Task::timeDifference).reversed();
        }
        if (includeCategory) {
            tasksByCategory.forEach((key, value) -> {
                pw.println(key.toUpperCase());
                value.stream().sorted(c).forEach(pw::println);
            });
        } else {
            tasks.stream().sorted(c).forEach(pw::println);
        }
        pw.flush();
    }
}

public class TasksManagerTest {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks(System.in);
        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");

    }
}
