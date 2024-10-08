package secondMidterm;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
class File implements Comparable<File>{
    String name;
    Integer size;
    LocalDateTime createdAt;

    public File(String name, Integer size, LocalDateTime createdAt) {
        this.name = name;
        this.size = size;
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public Integer getSize() {
        return size;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("%-10s %5dB %s",name,size,createdAt);
    }

    @Override
    public int compareTo(File o) {
        return Comparator.comparing(File::getCreatedAt)
                .thenComparing(File::getName)
                .thenComparing(File::getSize)
                .compare(this,o);
    }
}
class FileSystem{
    Map<Character, Set<File>>folderToFiles;
    Map<Integer,Set<File>>byYear;
    Map<String,Long>sizeByMonthAndDay;

    public FileSystem() {
        this.folderToFiles=new HashMap<>();
        this.byYear=new HashMap<>();
        this.sizeByMonthAndDay=new HashMap<>();
    }
    public void addFile(char folder, String name, int size, LocalDateTime createdAt){
        File f=new File(name,size,createdAt);

        folderToFiles.putIfAbsent(folder,new TreeSet<>());
        folderToFiles.get(folder).add(f);

        byYear.putIfAbsent(createdAt.getYear(),new HashSet<>());
        byYear.get(createdAt.getYear()).add(f);

        String dayAndMonth=createDayAndMonth(createdAt);

        sizeByMonthAndDay.putIfAbsent(dayAndMonth, 0L);
        sizeByMonthAndDay.put(dayAndMonth,sizeByMonthAndDay.get(dayAndMonth)+size);
    }
    public String createDayAndMonth(LocalDateTime createdAt){
        StringBuilder sb=new StringBuilder();
        sb.append(createdAt.getMonth()).append("-").append(createdAt.getDayOfMonth());
        return sb.toString();
    }
    public List<File>findAllHiddenFilesWithSizeLessThen(int size){
        List<File>hiddenFiles=new ArrayList<>();
        folderToFiles.forEach((key,value)->{
             value.stream().filter(file->file.name.startsWith("."))
                     .filter(file->file.size<size)
                     .forEach(hiddenFiles::add);
         });
        return hiddenFiles;
    }
    public int totalSizeOfFilesFromFolders(List<Character> folders){
       AtomicInteger total= new AtomicInteger(0);
         folderToFiles.forEach((key,value)->{
                    if(folders.contains(key)){
                        total.addAndGet((value.stream().mapToInt(File::getSize).sum()));
                    }
                });
        return total.get();

    }
    public Map<Integer, Set<File>> byYear(){
        return byYear;
    }
    public Map<String, Long> sizeByMonthAndDay(){
        return sizeByMonthAndDay;
    }
}

public class FileSystemTest {
    public static void main(String[] args) {
        FileSystem fileSystem = new FileSystem();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            fileSystem.addFile(parts[0].charAt(0), parts[1],
                    Integer.parseInt(parts[2]),
                    LocalDateTime.of(2016, 12, 29, 0, 0, 0).minusDays(Integer.parseInt(parts[3]))
            );
        }
        int action = scanner.nextInt();
        if (action == 0) {
            scanner.nextLine();
            int size = scanner.nextInt();
            System.out.println("== Find all hidden files with size less then " + size);
            List<File> files = fileSystem.findAllHiddenFilesWithSizeLessThen(size);
            files.forEach(System.out::println);
        } else if (action == 1) {
            scanner.nextLine();
            String[] parts = scanner.nextLine().split(":");
            System.out.println("== Total size of files from folders: " + Arrays.toString(parts));
            int totalSize = fileSystem.totalSizeOfFilesFromFolders(Arrays.stream(parts)
                    .map(s -> s.charAt(0))
                    .collect(Collectors.toList()));
            System.out.println(totalSize);
        } else if (action == 2) {
            System.out.println("== Files by year");
            Map<Integer, Set<File>> byYear = fileSystem.byYear();
            byYear.keySet().stream().sorted()
                    .forEach(key -> {
                        System.out.printf("Year: %d\n", key);
                        Set<File> files = byYear.get(key);
                        files.stream()
                                .sorted()
                                .forEach(System.out::println);
                    });
        } else if (action == 3) {
            System.out.println("== Size by month and day");
            Map<String, Long> byMonthAndDay = fileSystem.sizeByMonthAndDay();
            byMonthAndDay.keySet().stream().sorted()
                    .forEach(key -> System.out.printf("%s -> %d\n", key, byMonthAndDay.get(key)));
        }
        scanner.close();
    }
}