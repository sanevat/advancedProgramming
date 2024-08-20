import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class FileNameExistsException extends Exception {
    public FileNameExistsException(String file, String folder) {
        super(String.format("There is already a file named %s in the folder %s", file, folder));
    }
}

interface IFile {
    String getFileName();

    long getFileSize();

    String getFileInfo(int i);

    void sortBySize();

    long findLargestFile();
}

class File implements IFile {
    private final String name;
    private final long size;

    public File(String name, long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        return size;
    }

    @Override
    public String getFileInfo(int intend) {
        StringBuilder sb = new StringBuilder();
        sb.append("   ".repeat(Math.max(0, intend)));
        sb.append(String.format("File name: %10s File size: %10d\n", name, size));
        return sb.toString();
    }

    @Override
    public void sortBySize() {

    }

    @Override
    public long findLargestFile() {
        return size;
    }
}

class Folder extends File {
    private List<IFile> files;

    public Folder(String name) {
        super(name, 0);
        this.files = new ArrayList<>();
    }

    public void addFile(IFile file) throws FileNameExistsException {
        if (files.stream().anyMatch(file1 -> file1.getFileName().equals(file.getFileName()))) {
            throw new FileNameExistsException(file.getFileName(), getFileName());
        }
        files.add(file);

    }


    @Override
    public long getFileSize() {
        return files.stream()
                .mapToLong(IFile::getFileSize)
                .sum();
    }

    @Override
    public String getFileInfo(int intend) {
        StringBuilder sb = new StringBuilder();
        sb.append("    ".repeat(Math.max(0, intend)));
        sb.append(String.format("Folder name: %10s Folder size: %10d\n", getFileName(), getFileSize()));
        files.forEach(f -> sb.append(f.getFileInfo(intend + 1)));
        return sb.toString();
    }

    @Override
    public void sortBySize() {
        files = files.stream().sorted(Comparator.comparingLong(IFile::getFileSize))
                .collect(Collectors.toList());
        files.stream().sorted(Comparator.comparingLong(IFile::getFileSize))
                .forEach(IFile::sortBySize);
    }

    @Override
    public long findLargestFile() {
        return files.stream().mapToLong(IFile::getFileSize).max().getAsLong();
    }
}

class FileSystem {
    private final Folder rootDirectory;

    public FileSystem() {
        rootDirectory = new Folder("root");
    }

    public void addFile(IFile file) throws FileNameExistsException {
        rootDirectory.addFile(file);
    }

    public long findLargestFile() {
        return rootDirectory.findLargestFile();
    }

    public void sortBySize() {
        rootDirectory.sortBySize();
    }

    @Override
    public String toString() {
        return rootDirectory.getFileInfo(0);
    }


}


public class FileSystemTest {
    public static Folder readFolder(Scanner sc) {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < totalFiles; i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String[] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
        System.out.println(fileSystem.findLargestFile());

    }
}