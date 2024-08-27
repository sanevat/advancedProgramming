package secondMidterm;

import java.util.*;

class DuplicateNumberException extends Exception{
    public DuplicateNumberException(String number) {
        super(String.format("Duplicate number: %s.",number));
    }
}

class Contact{
    String name;
    String number;

    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return name+" "+number;
    }
}

class PhoneBook{
    Map<String,Contact> contactsByNumber;
    Map<String, Set<Contact>>contactsByName;
    Map<String ,Set<Contact>>contactsBySubStrings;

    public PhoneBook() {
        this.contactsByNumber=new HashMap<>();
        this.contactsByName=new LinkedHashMap<>();
        this.contactsBySubStrings=new HashMap<>();
    }

    public void addContact(String name, String number) throws DuplicateNumberException {
        if(contactsByNumber.containsKey(number))
            throw new DuplicateNumberException(number);
        Contact c=new Contact(name,number);
        contactsByNumber.put(number,c);
        contactsByName.putIfAbsent(name,new TreeSet<>(Comparator.comparing(Contact::getNumber)));
        contactsByName.get(name).add(c);

        generateSubstringsFromNumber(number,name);

    }
    public void generateSubstringsFromNumber(String number,String name){
        for(int i=0;i<number.length()-2;i++){
            for(int j=i+2;j<number.length();j++){
                String substring=number.substring(i,j+1);
                contactsBySubStrings.putIfAbsent(substring,new TreeSet<>(Comparator.comparing(Contact::getName).thenComparing(Contact::getNumber)));
                contactsBySubStrings.get(substring).add(new Contact(name,number));
            }
        }
    }
    public void contactsByNumber(String part) {
        if(!contactsBySubStrings.containsKey(part)){
            System.out.println("NOT FOUND");
            return;
        }
        contactsBySubStrings.get(part).forEach(System.out::println);
    }
    public void contactsByName(String name){
        if(!contactsByName.containsKey(name)){
            System.out.println("NOT FOUND");
            return;
        }
        contactsByName.get(name).forEach(System.out::println);
    }
}

public class PhoneBookTest {

    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            try {
                phoneBook.addContact(parts[0], parts[1]);
            } catch (DuplicateNumberException e) {
                System.out.println(e.getMessage());
            }
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            String[] parts = line.split(":");
            if (parts[0].equals("NUM")) {
                phoneBook.contactsByNumber(parts[1]);
            } else {
                phoneBook.contactsByName(parts[1]);
            }
        }
    }

}


