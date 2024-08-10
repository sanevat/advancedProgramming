import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class Canvas implements Comparable<Canvas> {
    private String id;
    List<Integer> sizes;

    public Canvas(String id, List<Integer> sizes) {
        this.id = id;
        this.sizes = sizes;
    }

    public int perimetar() {
        return 4 * sizes.stream().mapToInt(i -> i).sum();
    }

    public List<Integer> getSizes() {
        return sizes;
    }

    public static Canvas createLine(String line) {
        String[] parts = line.split("\\s+");
        String id = parts[0];
        List<Integer> sizes = new ArrayList<>();
        Arrays.stream(parts)
                .skip(1)
                .forEach(size -> sizes.add(Integer.valueOf(size)));
        return new Canvas(id, sizes);
    }

    @Override
    public String toString() {
        return id + " " + (sizes.size()) + " " + perimetar();
    }

    @Override
    public int compareTo(Canvas o) {
        return Integer.compare(this.perimetar(), o.perimetar());
    }
}

class ShapesApplication {
    private List<Canvas> canvases;

    public ShapesApplication() {
        this.canvases = new ArrayList<>();
    }

    public int readCanvases(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        canvases = br.lines()
                .map(Canvas::createLine)
                .collect(Collectors.toList());
        return canvases.stream()
                .mapToInt(canvas -> canvas.getSizes().size()).sum();
    }

    public void printLargestCanvasTo(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        Canvas largestCanvas = Collections.max(canvases);
        pw.println(largestCanvas);
        pw.flush();
    }
}

public class Shapes1Test {
    public static void main(String[] args) {
        ShapesApplication shapesApplication = new ShapesApplication();

        System.out.println("===READING SQUARES FROM INPUT STREAM===");
        System.out.println(shapesApplication.readCanvases(System.in));
        System.out.println("===PRINTING LARGEST CANVAS TO OUTPUT STREAM===");
        shapesApplication.printLargestCanvasTo(System.out);
    }
}