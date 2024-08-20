
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

class NonExistingItemException extends Exception {
    public NonExistingItemException(int id) {
        super(String.format("Item with id %d doesn't exist.", id));
    }
}

abstract class Archive {
    protected int id;
    protected LocalDate dateArchived;

    public Archive(int id) {
        this.id = id;
        this.dateArchived = LocalDate.now();
    }

    public void setDateArchived(LocalDate dateArchived) {
        this.dateArchived = dateArchived;
    }

    public int getId() {
        return id;
    }

    public abstract String tryToOpen(LocalDate l);
}

class LockedArchive extends Archive {
    private final LocalDate dateToOpen;

    public LockedArchive(int id, LocalDate dateToOpen) {
        super(id);
        this.dateToOpen = dateToOpen;
    }

    @Override
    public String tryToOpen(LocalDate l) {
        if (l.isBefore(dateToOpen)) return ("Item " + getId() + " cannot be opened before " + dateToOpen.toString());
        else return ("Item " + getId() + " opened at " + l);
    }
}

class SpecialArchive extends Archive {
    private final int maxOpen;
    private int timesOpen;

    public SpecialArchive(int id, int maxOpen) {
        super(id);
        this.maxOpen = maxOpen;
        timesOpen = 0;
    }

    @Override
    public String tryToOpen(LocalDate l) {
        if (timesOpen == maxOpen) {
            return ("Item " + getId() + " cannot be opened more than " + maxOpen + " times");
        } else {
            timesOpen++;
            return ("Item " + getId() + " opened at " + l);

        }
    }
}


class ArchiveStore {
    private List<Archive> archives;
    private final StringBuilder log;

    public ArchiveStore() {
        this.archives = new ArrayList<>();
        log = new StringBuilder();
    }

    public void archiveItem(Archive item, LocalDate date) {
        archives.add(item);
        item.setDateArchived(date);
        log.append("Item ").append(item.getId()).append(" archived at ").append(date).append('\n');
    }

    public void openItem(int id, LocalDate date) throws NonExistingItemException {
        if (archives.stream().noneMatch(arch -> arch.getId() == id))
            throw new NonExistingItemException(id);
        Optional<Archive> archive = archives.stream().filter(arch -> arch.getId() == id).findAny();
        log.append(archive.get().tryToOpen(date)).append('\n');
    }

    public String getLog() {
        return log.toString();
    }


}

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        LocalDate date = LocalDate.of(2013, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();

            LocalDate dateToOpen = date.atStartOfDay().plusSeconds(days * 24 * 60 * 60).toLocalDate();
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while (scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch (NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}