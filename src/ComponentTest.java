import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class InvalidPositionException extends Exception{
    public InvalidPositionException(int pos) {
        super(String.format("Invalid position %d, already taken!",pos));
    }
}
class Component implements Comparable<Component>{
    private String color;
    private int weight;
    private int pos;
    List<Component> components;

    public Component(String color, int weight) {
        this.color = color;
        this.weight = weight;
        this.components=new ArrayList<>();
        pos=-1;
    }
    public void addComponent(Component component){
        components.add(component);
        sort();
    }
    public int getPos() {
        return pos;
    }

    public Component setPos(int pos) {
        this.pos = pos;
        return this;
    }
    public void sort(){
        components = components.stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    public String getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }
    public void changeColor(int weight,String color){
        if(this.weight<weight)this.color = color;
        if(!components.isEmpty()){
            components.forEach(c->c.changeColor(weight,color));
        }
        sort();
    }

    @Override
    public int compareTo(Component o) {
        return Comparator.comparing(Component::getWeight)
                .thenComparing(Component::getColor)
                .compare(this,o);
    }

    public String getComponentString(int intend){
        StringBuilder sb = new StringBuilder();
        sb.append("---".repeat(Math.max(0, intend)));
        sb.append(String.format("%d:%s\n",weight,color));
        components.forEach(c->sb.append(c.getComponentString(intend+1)));
        return sb.toString();
    }
}
class ComponentLibrary{
    private String name;
    private List<Component>components;

    public ComponentLibrary(String name) {
        this.name = name;
        this.components=new ArrayList<>();
    }
    public void addComponent(int position,Component component) throws InvalidPositionException {
        if(components.isEmpty()){
            components.add(component.setPos(position));
        }else {
            for (Component value : components) {
                if (value.getPos() == position)
                    throw new InvalidPositionException(position);
            }
            components.add(component.setPos(position));
            sort();
        }
    }
    public void sort(){
        components = components.stream()
                .sorted(Comparator.comparingInt(Component::getWeight))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WINDOW ").append(name).append('\n');
        components.forEach(c->sb.append(c.getPos()).append(":").append(c.getComponentString(0)));
        return sb.toString();
    }
    public void changeColor(int weight, String color){
        for (Component component : components) {
            component.changeColor(weight, color);
        }
        sort();
    }
    public void switchComponents(int pos1, int pos2){
        Component tmp1=null,tmp2=null;
        for (Component component : components) {
            if (component.getPos() == pos1) {
                tmp1 = component;
            } else if (component.getPos() == pos2) {
                tmp2 = component;
            }
        }
        if(tmp1!=null&&tmp2!=null) {
            int tmp = tmp1.getPos();
            tmp1.setPos(tmp2.getPos());
            tmp2.setPos(tmp);
            sort();
        }

    }
}



public class ComponentTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        ComponentLibrary componentLibrary = new ComponentLibrary(name);
        Component prev = null;
        while (true) {
            try {
                int what = scanner.nextInt();
                scanner.nextLine();
                if (what == 0) {
                    int position = scanner.nextInt();
                    componentLibrary.addComponent(position, prev);
                } else if (what == 1) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev = component;
                } else if (what == 2) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                    prev = component;
                } else if (what == 3) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                } else if(what == 4) {
                    break;
                }

            } catch (InvalidPositionException e) {
                System.out.println(e.getMessage());
            }
            scanner.nextLine();
        }

        System.out.println("=== ORIGINAL WINDOW ===");
        System.out.println(componentLibrary);
        int weight = scanner.nextInt();
        scanner.nextLine();
        String color = scanner.nextLine();
        componentLibrary.changeColor(weight, color);
        System.out.printf("=== CHANGED COLOR (%d, %s) ===%n", weight, color);
        System.out.println(componentLibrary);
        int pos1 = scanner.nextInt();
        int pos2 = scanner.nextInt();
        System.out.printf("=== SWITCHED COMPONENTS %d <-> %d ===%n", pos1, pos2);
        componentLibrary.switchComponents(pos1, pos2);
        System.out.println(componentLibrary);
    }
}

