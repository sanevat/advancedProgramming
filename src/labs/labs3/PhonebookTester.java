package labs.labs3;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

class InvalidNameException extends Exception {
    public String name;

    public InvalidNameException(String name) {
        super();
        this.name = name;
    }
}

class MaximumSizeExceddedException extends Exception {
    public MaximumSizeExceddedException() {
    }

}

class InvalidNumberException extends Exception {
    public InvalidNumberException() {
    }
}

class InvalidFormatException extends Exception {
    public InvalidFormatException() {
    }
}

class Contact implements Comparable<Contact> {
    String name;
    String[] numbers;

    public boolean isValidName(String name) {
        if (name.length() <= 4 || name.length() > 10)
            return false;
        for (Character c : name.toCharArray()) {
            if (!Character.isLetterOrDigit(c))
                return false;
        }
        return true;
    }

    public boolean isValidNumber(String number) {
        for (Character c : number.toCharArray()) {
            if (!Character.isDigit(c))
                return false;
        }
        return number.length() == 9 && number.startsWith("07") && (number.charAt(2) != '3' &&
                number.charAt(2) != '4' && number.charAt(2) != '9');
    }

    public Contact(String name, String... contacts) throws InvalidNameException, MaximumSizeExceddedException, InvalidNumberException {
        if (!isValidName(name))
            throw new InvalidNameException(name);
        for (String c : contacts) {
            if (!isValidNumber(c))
                throw new InvalidNumberException();
        }
        if (contacts.length > 5)
            throw new MaximumSizeExceddedException();
        this.name = name;

        this.numbers = Arrays.copyOf(contacts, contacts.length);
    }

    public String getName() {
        return name;
    }

    public String[] getNumbers() {
        Arrays.sort(numbers);
        return numbers;
    }

    public void addNumber(String phoneNumber) throws MaximumSizeExceddedException, InvalidNumberException {
        if (numbers.length == 5)
            throw new MaximumSizeExceddedException();
        if (!isValidNumber(phoneNumber))
            throw new InvalidNumberException();
        numbers = Arrays.copyOf(numbers, numbers.length + 1);
        numbers[numbers.length - 1] = phoneNumber;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb
                .append(name)
                .append("\n")
                .append(numbers.length)
                .append("\n");

        Arrays.stream(numbers).sorted().forEach(i -> sb.append(i).append("\n"));

        return sb.toString();
    }

    public static Contact valueOf(String s) throws InvalidFormatException {
        try {
            return new Contact(s);
        } catch (Exception e) {
            throw new InvalidFormatException();
        }
    }

    public boolean hasNumber(String s) {
        return Arrays.stream(numbers).anyMatch(i -> i.startsWith(s));
    }

    @Override
    public int compareTo(Contact o) {
        return name.compareTo(o.name);
    }
}

class PhoneBook {
    Contact[] contacts;

    public PhoneBook() {
        this.contacts = new Contact[0];
    }

    public void addContact(Contact c) throws MaximumSizeExceddedException, InvalidNameException {
        if (contacts.length >= 250)
            throw new MaximumSizeExceddedException();
        if (hasContact(c)) {
            throw new InvalidNameException(c.name);
        }
        contacts = Arrays.copyOf(contacts, contacts.length + 1);
        contacts[contacts.length - 1] = c;
    }

    private boolean hasContact(Contact c) {
        return Arrays.stream(contacts).anyMatch(i -> i.getName().equals(c.getName()));
    }

    public Contact getContactForName(String name) {
        return Arrays.stream(contacts).filter(i -> i.getName().equals(name)).findFirst().orElse(null);
    }

    public int numberOfContacts() {
        return contacts.length;
    }

    public Contact[] getContacts() {
        return Arrays.stream(contacts).sorted(Comparator.comparing(Contact::getName)).toArray(Contact[]::new);
    }

    public boolean removeContact(String name) {
        for (Contact c : contacts) {
            if (c.getName().equals(name)) {
                contacts = IntStream.range(0, contacts.length).filter(i -> !contacts[i].getName().equals(name)).mapToObj(i -> contacts[i]).toArray(Contact[]::new);
                return true;
            }
        }

        return false;
    }

    public static boolean saveAsTextFile(PhoneBook pb, String path) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(pb);
            oos.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static PhoneBook loadFromTextFile(String file) throws InvalidFormatException {
        PhoneBook pb = null;

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            pb = (PhoneBook) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new InvalidFormatException();
        }

        return pb;
    }

    public Contact[] getContactsForNumber(String number) {
        return Arrays.stream(contacts).filter(i -> i.hasNumber(number)).toArray(Contact[]::new);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Arrays.stream(contacts).sorted().forEach(i -> sb.append(i).append("\n"));

        return sb.toString();
    }

}

public class PhonebookTester {

    public static void main(String[] args) throws Exception {
        Scanner jin = new Scanner(System.in);
        String line = jin.nextLine();
        switch (line) {
            case "test_contact":
                testContact(jin);
                break;
            case "test_phonebook_exceptions":
                testPhonebookExceptions(jin);
                break;
            case "test_usage":
                testUsage(jin);
                break;
        }
    }

    private static void testFile(Scanner jin) throws Exception {
        PhoneBook phonebook = new PhoneBook();
        while (jin.hasNextLine())
            phonebook.addContact(new Contact(jin.nextLine(), jin.nextLine().split("\\s++")));
        String text_file = "phonebook.txt";
        PhoneBook.saveAsTextFile(phonebook, text_file);
        PhoneBook pb = PhoneBook.loadFromTextFile(text_file);
        if (!pb.equals(phonebook)) System.out.println("Your file saving and loading doesn't seem to work right");
        else System.out.println("Your file saving and loading works great. Good job!");
    }

    private static void testUsage(Scanner jin) throws Exception {
        PhoneBook phonebook = new PhoneBook();
        while (jin.hasNextLine()) {
            String command = jin.nextLine();
            switch (command) {
                case "add":
                    phonebook.addContact(new Contact(jin.nextLine(), jin.nextLine().split("\\s++")));
                    break;
                case "remove":
                    phonebook.removeContact(jin.nextLine());
                    break;
                case "print":
                    System.out.println(phonebook.numberOfContacts());
                    System.out.println(Arrays.toString(phonebook.getContacts()));
                    System.out.println(phonebook.toString());
                    break;
                case "get_name":
                    System.out.println(phonebook.getContactForName(jin.nextLine()));
                    break;
                case "get_number":
                    System.out.println(Arrays.toString(phonebook.getContactsForNumber(jin.nextLine())));
                    break;
            }
        }
    }

    private static void testPhonebookExceptions(Scanner jin) {
        PhoneBook phonebook = new PhoneBook();
        boolean exception_thrown = false;
        try {
            while (jin.hasNextLine()) {
                phonebook.addContact(new Contact(jin.nextLine()));
            }
        } catch (InvalidNameException e) {
            System.out.println(e.name);
            exception_thrown = true;
        } catch (Exception e) {
        }
        if (!exception_thrown) System.out.println("Your addContact method doesn't throw InvalidNameException");
        /*
		exception_thrown = false;
		try {
		phonebook.addContact(new Contact(jin.nextLine()));
		} catch ( MaximumSizeExceddedException e ) {
			exception_thrown = true;
		}
		catch ( Exception e ) {}
		if ( ! exception_thrown ) System.out.println("Your addContact method doesn't throw MaximumSizeExcededException");
        */
    }

    private static void testContact(Scanner jin) throws Exception {
        boolean exception_thrown = true;
        String names_to_test[] = {"And\nrej", "asd", "AAAAAAAAAAAAAAAAAAAAAA", "Ð�Ð½Ð´Ñ€ÐµÑ˜A123213", "Andrej#", "Andrej<3"};
        for (String name : names_to_test) {
            try {
                new Contact(name);
                exception_thrown = false;
            } catch (InvalidNameException e) {
                exception_thrown = true;
            }
            if (!exception_thrown) System.out.println("Your Contact constructor doesn't throw an InvalidNameException");
        }
        String numbers_to_test[] = {"+071718028", "number", "078asdasdasd", "070asdqwe", "070a56798", "07045678a", "123456789", "074456798", "073456798", "079456798"};
        for (String number : numbers_to_test) {
            try {
                new Contact("Andrej", number);
                exception_thrown = false;
            } catch (InvalidNumberException e) {
                exception_thrown = true;
            }
            if (!exception_thrown)
                System.out.println("Your Contact constructor doesn't throw an InvalidNumberException");
        }
        String nums[] = new String[10];
        for (int i = 0; i < nums.length; ++i) nums[i] = getRandomLegitNumber();
        try {
            new Contact("Andrej", nums);
            exception_thrown = false;
        } catch (MaximumSizeExceddedException e) {
            exception_thrown = true;
        }
        if (!exception_thrown)
            System.out.println("Your Contact constructor doesn't throw a MaximumSizeExceddedException");
        Random rnd = new Random(5);
        Contact contact = new Contact("Andrej", getRandomLegitNumber(rnd), getRandomLegitNumber(rnd), getRandomLegitNumber(rnd));
        System.out.println(contact.getName());
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact.toString());
        contact.addNumber(getRandomLegitNumber(rnd));
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact.toString());
        contact.addNumber(getRandomLegitNumber(rnd));
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact.toString());
    }

    static String[] legit_prefixes = {"070", "071", "072", "075", "076", "077", "078"};
    static Random rnd = new Random();

    private static String getRandomLegitNumber() {
        return getRandomLegitNumber(rnd);
    }

    private static String getRandomLegitNumber(Random rnd) {
        StringBuilder sb = new StringBuilder(legit_prefixes[rnd.nextInt(legit_prefixes.length)]);
        for (int i = 3; i < 9; ++i)
            sb.append(rnd.nextInt(10));
        return sb.toString();
    }


}
