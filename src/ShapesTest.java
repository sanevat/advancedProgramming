import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

enum Color{
    RED,GREEN,BLUE
}
interface Scalable{
    void scale(float scaleFactor);
}
interface Stackable{
    float weight();
}
abstract class Figure implements Scalable,Stackable, Comparable<Figure>{
    protected String id;
    protected Color color;

    public Figure(String id, Color color) {
        this.id = id;
        this.color = color;
    }

    @Override
    public int compareTo(Figure o) {
        return Float.compare(this.weight(),o.weight());
    }

    public String getId() {
        return id;
    }
}
class Ring extends Figure{
    private float radius;

    public Ring(String id, Color color, float radius) {
        super(id, color);
        this.radius = radius;
    }

    @Override
    public void scale(float scaleFactor) {
        this.radius*=scaleFactor;
    }

    @Override
    public float weight() {
        return (float) (Math.PI*radius*radius);
    }

    @Override
    public String toString() {
        return String.format("R: %-5s %-10s %10.2f",id,color,weight());
    }
}
class Rectangle extends Figure{
    private float width;
    private float height;

    public Rectangle(String id, Color color, float width, float height) {
        super(id, color);
        this.width = width;
        this.height = height;
    }

    @Override
    public void scale(float scaleFactor) {
        this.height*=scaleFactor;
        this.width*=scaleFactor;
    }

    @Override
    public float weight() {
        return width*height;
    }

    @Override
    public String toString() {
        return String.format("R: %-5s %-10s %10.2f",id,color,weight());
    }
}
class Figures{
    List<Figure> figures;

    public Figures() {
        this.figures=new ArrayList<>();
    }
    public void sort(){
        figures=figures.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }
    public void add(String id, Color color, float radius){
        figures.add(new Ring(id,color,radius));
        sort();
    }
    public void add(String id, Color color, float width, float height){
        figures.add(new Rectangle(id, color, width, height));
        sort();
    }
    public void scale(String id, float scaleFactor){
        for(Figure f:figures)
            if(f.getId().equals(id))
                f.scale(scaleFactor);
        sort();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        figures.forEach(fig -> result.append(fig.toString()).append("\n"));
        return result.toString();
    }
}

public class ShapesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Figures canvas = new Figures();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            int type = Integer.parseInt(parts[0]);
            String id = parts[1];
            if (type == 1) {
                Color color = Color.valueOf(parts[2]);
                float radius = Float.parseFloat(parts[3]);
                canvas.add(id, color, radius);
            } else if (type == 2) {
                Color color = Color.valueOf(parts[2]);
                float width = Float.parseFloat(parts[3]);
                float height = Float.parseFloat(parts[4]);
                canvas.add(id, color, width, height);
            } else if (type == 3) {
                float scaleFactor = Float.parseFloat(parts[2]);
                System.out.println("ORIGINAL:");
                System.out.print(canvas);
                canvas.scale(id, scaleFactor);
                System.out.printf("AFTER SCALING: %s %.2f\n", id, scaleFactor);
                System.out.print(canvas);
            }

        }
    }
}