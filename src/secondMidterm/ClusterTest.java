package secondMidterm;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;


interface hasID{
     long getId();
     double getDistance(Point2D o);
     String toString(Point2D o);
}
class Cluster<T extends hasID>{
    Map<Long,T> elements;

    public Cluster() {
        elements = new HashMap<>();
    }
    void addItem(T element){
        elements.put(element.getId(),element);
    }
    void near(long id, int top){
        AtomicInteger c = new AtomicInteger(1);
        elements.values().stream()
                .filter(t->t.getId()!=id)
                .sorted(Comparator.comparing(i->i.getDistance((Point2D) elements.get(id))))
                .limit(top)
                .forEach(t-> System.out.println((c.getAndIncrement())+". "+t.toString((Point2D) elements.get(id))));
    }
}
class Point2D implements hasID {
    private long id;
    private float x;
    private float y;

    public Point2D(long id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public double getDistance(Point2D oth) {
        return Math.sqrt(Math.pow(x-oth.x,2)+Math.pow(y-oth.y,2));
    }

    public String toString(Point2D o) {
        return String.format("%d -> %.3f",id,getDistance(o));
    }
}
public class ClusterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Cluster<Point2D> cluster = new Cluster<>();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            long id = Long.parseLong(parts[0]);
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            cluster.addItem(new Point2D(id, x, y));
        }
        int id = scanner.nextInt();
        int top = scanner.nextInt();
        cluster.near(id, top);
        scanner.close();
    }
}
