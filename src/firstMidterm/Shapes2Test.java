import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class IrregularWindowException extends Exception {
    public IrregularWindowException(String id, long maxArea) {
        super(String.format(" Canvas %s has a shape with area larger than %d.", id, maxArea));
    }
}

abstract class Shape {
    protected int size;

    public Shape(int size) {
        this.size = size;
    }

    abstract double area();

}

class Square extends Shape {
    public Square(int size) {
        super(size);
    }

    @Override
    double area() {
        return size * size;
    }
}

class Circle extends Shape {
    public Circle(int size) {
        super(size);
    }

    @Override
    double area() {
        return Math.PI * size * size;
    }
}

class Window implements Comparable<Window> {
    private String id;
    private List<Shape> shapes;

    public Window(String id, List<Shape> shapes) {
        this.id = id;
        this.shapes = shapes;
    }

    public static Window createWindow(String line, long maxArea) throws IrregularWindowException {
        String[] parts = line.split("\\s+");
        String id = parts[0];
        List<Shape> shapes = new ArrayList<>();

        for (int i = 1; i < parts.length; i += 2) {
            int size = Integer.parseInt(parts[i + 1]);
            Shape s = (parts[i].equals("C") ? new Circle(size) : new Square(size));

            if (s.area() >= maxArea)
                throw new IrregularWindowException(id, maxArea);

            shapes.add(s);
        }
        return new Window(id, shapes);
    }

    public double totalArea() {
        return shapes.stream().mapToDouble(Shape::area).sum();
    }

    @Override
    public int compareTo(Window o) {
        return Double.compare(this.totalArea(), o.totalArea());
    }

    @Override
    public String toString() {
        int totalCircles = (int) shapes.stream().filter(s -> s instanceof Circle).count();
        int totalSquares = (int) shapes.stream().filter(s -> s instanceof Square).count();
        double minArea = shapes.stream().mapToDouble(Shape::area).min().orElse(0);
        double maxArea = shapes.stream().mapToDouble(Shape::area).max().orElse(0);
        double avgArea = shapes.stream().mapToDouble(Shape::area).average().orElse(0.0);
        return String.format("%s %d %d %d %.2f %.2f %.2f", id, shapes.size(), totalCircles, totalSquares, minArea, maxArea, avgArea);
    }
}

class ShapesApplication2 {
    private long maxArea;
    private List<Window> windows;

    public ShapesApplication2(long maxArea) {
        this.maxArea = maxArea;
        this.windows = new ArrayList<>();
    }

    public void readCanvases(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        windows = br.lines()
                .map((String line) -> {
                    try {
                        return Window.createWindow(line, maxArea);
                    } catch (IrregularWindowException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void printCanvases(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        windows.stream().sorted(Comparator.reverseOrder())
                .forEach(wind -> pw.println(wind.toString()));
        pw.flush();
    }
}

public class Shapes2Test {
    public static void main(String[] args) {

        ShapesApplication2 shapesApplication = new ShapesApplication2(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");

        shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);
    }
}