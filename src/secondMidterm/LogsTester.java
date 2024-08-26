package secondMidterm;

import java.util.*;

enum Type {
    INFO, WARN, ERROR
}

class Log {
    String serviceName, microServiceName, message;
    Type type;
    int timestamp;

    public Log(String serviceName, String microServiceName, Type type, String message, int timestamp) {
        this.serviceName = serviceName;
        this.microServiceName = microServiceName;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int severity() {
        int severity = switch (type) {
            case WARN -> message.contains("might cause error") ? 2 : 1;
            case ERROR -> {
                int baseSeverity = 3;
                baseSeverity += message.contains("fatal") ? 2 : 0;
                baseSeverity += message.contains("exception") ? 3 : 0;
                yield baseSeverity;
            }
            default -> 0;
        };
        return severity;
    }

    @Override
    public String toString() {
        return String.format("%s|%s [%s] %sT:%d", serviceName, microServiceName, type, message, timestamp);
    }
}

class LogCollector {
    List<Log> logs = new ArrayList<>();
    Map<String, List<Log>> logsByService = new TreeMap<>(Comparator.reverseOrder());

    public void addLog(String log) {
        String[] parts = log.split("\\s+");
        String serviceName = parts[0], microService = parts[1], message = String.join(" ", Arrays.copyOfRange(parts, 3, parts.length));
        Type type = Type.valueOf(parts[2]);
        int timestamp = Integer.parseInt(parts[parts.length - 1]);

        Log newLog = new Log(serviceName, microService, type, message, timestamp);
        logs.add(newLog);
        logsByService.computeIfAbsent(serviceName, k -> new ArrayList<>()).add(newLog);
    }

    public Map<String, List<Log>> groupByMicroService(List<Log> logs) {
        Map<String, List<Log>> groupedLogs = new TreeMap<>();
        logs.forEach(log -> groupedLogs.computeIfAbsent(log.microServiceName, k -> new ArrayList<>()).add(log));
        return groupedLogs;
    }

    public double averageLogsPerMicroService(List<Log> logs) {
        return groupByMicroService(logs).values().stream().mapToInt(List::size).average().orElse(0.0);
    }

    public void printServicesBySeverity() {
        logsByService.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().stream().mapToInt(Log::severity).average().orElse(0.0)))
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    String serviceName = entry.getKey();
                    List<Log> serviceLogs = logsByService.get(serviceName);
                    System.out.printf("Service: %s | Microservices: %d | Total Logs: %d | Avg Severity: %.2f | Avg Logs/Microservice: %.2f%n",
                            serviceName, groupByMicroService(serviceLogs).size(), serviceLogs.size(), entry.getValue(), averageLogsPerMicroService(serviceLogs));
                });
    }

    public Map<Integer, Integer> getSeverityDistribution(String service, String microservice) {
        List<Log> targetLogs = microservice == null ? logsByService.get(service) : groupByMicroService(logsByService.get(service)).get(microservice);
        Map<Integer, Integer> severityDistribution = new TreeMap<>();
        targetLogs.forEach(log -> severityDistribution.merge(log.severity(), 1, Integer::sum));
        return severityDistribution;
    }

    public Comparator<Log> getLogComparator(String order) {
        return switch (order) {
            case "NEWEST_FIRST" -> Comparator.comparingInt(Log::getTimestamp).reversed();
            case "OLDEST_FIRST" -> Comparator.comparingInt(Log::getTimestamp);
            case "MOST_SEVERE_FIRST" -> Comparator.comparingInt(Log::severity).reversed().thenComparingInt(Log::getTimestamp);
            default -> Comparator.comparingInt(Log::severity).thenComparingInt(Log::getTimestamp);
        };
    }

    public void displayLogs(String service, String microservice, String order) {
        List<Log> targetLogs = microservice == null ? logsByService.get(service) : groupByMicroService(logsByService.get(service)).get(microservice);
        targetLogs.stream().sorted(getLogComparator(order)).forEach(System.out::println);
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
