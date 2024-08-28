package secondMidterm;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

enum Type {
    INFO, WARN, ERROR
}

class Log {
    String service;
    String microservice;
    Type type;
    String message;
    int timestamp;

    public Log(String service, String microservice, Type type, String message, int timestamp) {
        this.service = service;
        this.microservice = microservice;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getSeverity() {
        int severity = 0;
        if (type == Type.WARN) {
            severity = message.contains("might cause error") ? 2 : 1;
        } else if (type == Type.ERROR) {
            severity = 3 + (message.contains("fatal") ? 2 : 0) + (message.contains("exception") ? 3 : 0);
        }
        return severity;
    }

    @Override
    public String toString() {
        return String.format("%s|%s [%s] %s %d T:%d", service, microservice, type, message, timestamp, timestamp);
    }
}

class LogCollector {
    Map<String, Map<String, Set<Log>>> logs;

    public LogCollector() {
        this.logs = new TreeMap<>(Comparator.reverseOrder());
    }

    public Log generateLog(String log) {
        String[] parts = log.split("\\s+");
        String service = parts[0];
        String microservice = parts[1];
        Type type = Type.valueOf(parts[2]);
        int timestamp = Integer.parseInt(parts[parts.length - 1]);
        StringBuilder message = new StringBuilder();
        for (int i = 3; i < parts.length - 1; i++)
            message.append(parts[i]).append(" ");
        return new Log(service, microservice, type, message.toString(), timestamp);
    }

    public void addLog(String log) {
        Log l = generateLog(log);
        logs.putIfAbsent(l.service, new LinkedHashMap<>());
        logs.get(l.service).putIfAbsent(l.microservice, new LinkedHashSet<>());
        logs.get(l.service).get(l.microservice).add(l);
    }

    public int totalLogsInService(String service) {
        return logs.get(service).values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()).size();
    }

    public double averageSeverity(String service) {
        return logs.get(service).values()
                .stream()
                .flatMap(Collection::stream)
                .mapToInt(Log::getSeverity)
                .average()
                .orElse(0.0);
    }

    public double averagePerMicroservice(String service) {
        AtomicInteger sum = new AtomicInteger(0);
        logs.get(service).forEach((key, value) -> sum.addAndGet(value.size()));
        int numLogs = logs.get(service).keySet().size();
        return sum.get() / (double) numLogs;
    }

    public void printServicesBySeverity() {
        logs.entrySet().stream().filter(entry -> entry.getValue() != null)
                .sorted((entry1, entry2) -> {
                    double avgSeverity1 = averageSeverity(entry1.getKey());
                    double avgSeverity2 = averageSeverity(entry2.getKey());
                    return Double.compare(avgSeverity2, avgSeverity1);
                })
                .forEach(entry -> {
                    System.out.printf("Service name: %s", entry.getKey());
                    System.out.printf(" Count of microservices: %d", entry.getValue().size());
                    System.out.printf(" Total logs in service: %d", totalLogsInService(entry.getKey()));
                    System.out.printf(" Average severity for all logs: %.2f", averageSeverity(entry.getKey()));
                    System.out.printf(" Average number of logs per microservice: %.2f\n", averagePerMicroservice(entry.getKey()));
                });

    }

    public Map<Integer, Integer> getSeverityDistribution(String service, String microservice) {
        Set<Log> logsSet = (microservice == null)
                ? logs.get(service).values().stream().flatMap(Collection::stream).collect(Collectors.toSet())
                : new HashSet<>(logs.get(service).get(microservice));

        Map<Integer, Integer> severityDistribution = new TreeMap<>();
        logsSet.forEach(log -> {
            severityDistribution.putIfAbsent(log.getSeverity(), 0);
            severityDistribution.put(log.getSeverity(), severityDistribution.get(log.getSeverity()) + 1);
        });
        return severityDistribution;
    }

    public Comparator<Log> getComparator(String order) {
        Comparator<Log> c;
        switch (order) {
            case "NEWEST_FIRST" -> c = getComparator("OLDEST_FIRST").reversed();
            case "OLDEST_FIRST" -> c = Comparator.comparing(Log::getTimestamp);
            case "MOST_SEVERE_FIRST" ->
                    c = Comparator.comparing(Log::getSeverity).thenComparing(Log::getTimestamp).reversed();
            case "LEAST_SEVERE_FIRST" ->
                    c = getComparator("MOST_SEVERE_FIRST").reversed().thenComparing(Log::getTimestamp);
            case null, default -> {
                return null;
            }
        }
        return c;
    }

    public void displayLogs(String service, String microservice, String order) {
        Comparator<Log> comparator = getComparator(order);
        Set<Log> logsSet = microservice == null
                ? logs.get(service).values().stream().flatMap(Collection::stream).collect(Collectors.toSet())
                : new HashSet<>(logs.get(service).get(microservice));

        System.out.println("displayLogs " + service + " " + (microservice == null ? "" : microservice + " ") + order);
        logsSet.stream().sorted(comparator).forEach(System.out::println);
    }
}

public class LogsTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LogCollector collector = new LogCollector();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith("addLog")) {
                collector.addLog(line.substring(7));  // "addLog " length is 7
            } else if (line.startsWith("printServicesBySeverity")) {
                collector.printServicesBySeverity();
            } else if (line.startsWith("getSeverityDistribution")) {
                String[] parts = line.split("\\s+");
                String service = parts[1], microservice = (parts.length == 3) ? parts[2] : null;
                collector.getSeverityDistribution(service, microservice).forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
            } else if (line.startsWith("displayLogs")) {
                String[] parts = line.split("\\s+");
                String service = parts[1], microservice = (parts.length == 4) ? parts[2] : null, order = (parts.length == 4) ? parts[3] : parts[2];
                collector.displayLogs(service, microservice, order);
            }
        }
    }
}
