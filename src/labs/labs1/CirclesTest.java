package labs.labs1;

import java.util.Arrays;
import java.util.Scanner;

enum TYPE {
    POINT,
    CIRCLE
}

enum DIRECTION {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

class ObjectCanNotBeMovedException extends Exception {
    Movable m;

    public ObjectCanNotBeMovedException(Movable m) {
        this.m = m;
    }

    public void message() {
        if (m.getType() == TYPE.POINT) {
            System.out.println(String.format("Point (%d,%d) is out of bounds", m.getCurrentXPosition(), m.getCurrentYPosition()));
        } else {
            System.out.println(String.format("Circle (%d,%d) is out of bounds", m.getCurrentXPosition(), m.getCurrentYPosition()));
        }
    }
}

class MovableObjectNotFittableException extends Exception {
    Movable m;

    public MovableObjectNotFittableException(Movable m) {
        this.m = m;
    }

    public void message() {
        if (m.getType().equals(TYPE.CIRCLE)) {

            System.out.println(String.format("Movable circle with center (%d,%d) and radius %d can not be fitted into the collection",
                    m.getCurrentXPosition(), m.getCurrentYPosition(), ((MovableCircle) m).getRadius()));
        } else {
            System.out.println(String.format("Point (%d,%d) is out of bounds\n", m.getCurrentXPosition(), m.getCurrentYPosition()));
        }
    }
}

interface Movable {
    void moveUp() throws ObjectCanNotBeMovedException;

    void moveDown() throws ObjectCanNotBeMovedException;

    void moveLeft() throws ObjectCanNotBeMovedException;

    void moveRight() throws ObjectCanNotBeMovedException;

    TYPE getType();

    int getCurrentXPosition();

    int getCurrentYPosition();

}

class MovablePoint implements Movable {
    int x;
    int y;
    int xSpeed;
    int ySpeed;

    public MovablePoint(int x, int y, int xSpeed, int ySpeed) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public void isObjectMovable(DIRECTION dir) throws ObjectCanNotBeMovedException {
        if (dir == DIRECTION.UP) {
            if (y + ySpeed > MovablesCollection.maxY || y + ySpeed < 0)
                throw new ObjectCanNotBeMovedException(new MovablePoint(x, y + ySpeed, xSpeed, ySpeed));
        } else if (dir == DIRECTION.DOWN) {
            if (y - ySpeed > MovablesCollection.maxY || y - ySpeed < 0)
                throw new ObjectCanNotBeMovedException(new MovablePoint(x, y - ySpeed, xSpeed, ySpeed));
        } else if (dir == DIRECTION.RIGHT) {
            if (x + xSpeed > MovablesCollection.maxX || x + xSpeed < 0)
                throw new ObjectCanNotBeMovedException(new MovablePoint(x + xSpeed, y, xSpeed, ySpeed));
        } else {
            if (x - xSpeed > MovablesCollection.maxX || x - xSpeed < 0)
                throw new ObjectCanNotBeMovedException(new MovablePoint(x - xSpeed, y, xSpeed, ySpeed));
        }
    }

    @Override
    public void moveUp() throws ObjectCanNotBeMovedException {
        isObjectMovable(DIRECTION.UP);
        y += ySpeed;
    }

    @Override
    public void moveDown() throws ObjectCanNotBeMovedException {
        isObjectMovable(DIRECTION.DOWN);
        y -= ySpeed;
    }

    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException {
        isObjectMovable(DIRECTION.LEFT);
        x -= xSpeed;
    }

    @Override
    public void moveRight() throws ObjectCanNotBeMovedException {
        isObjectMovable(DIRECTION.RIGHT);
        x += xSpeed;
    }

    @Override
    public TYPE getType() {
        return TYPE.POINT;
    }

    @Override
    public int getCurrentXPosition() {
        return this.x;
    }

    @Override
    public int getCurrentYPosition() {
        return this.y;
    }

    @Override
    public String toString() {
        return String.format("Movable point with coordinates (%d,%d)\n", x, y);
    }
}

class MovableCircle implements Movable {
    int radius;
    MovablePoint center;

    public MovableCircle(int radius, MovablePoint center) {
        this.radius = radius;
        this.center = center;
    }

    @Override
    public void moveUp() throws ObjectCanNotBeMovedException {
        center.moveUp();
    }

    @Override
    public void moveDown() throws ObjectCanNotBeMovedException {
        center.moveDown();
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException {
        center.moveLeft();
    }

    @Override
    public void moveRight() throws ObjectCanNotBeMovedException {
        center.moveRight();
    }

    @Override
    public TYPE getType() {
        return TYPE.CIRCLE;
    }

    @Override
    public int getCurrentXPosition() {
        return center.getCurrentXPosition();
    }

    @Override
    public int getCurrentYPosition() {
        return center.getCurrentYPosition();
    }

    @Override
    public String toString() {
        return String.format("Movable circle with center coordinates (%d,%d) and radius %d\n",
                center.x, center.y, radius);
    }
}

class MovablesCollection {
    Movable[] movable;
    int size;
    static int maxX;
    static int maxY;

    public MovablesCollection(int x_MAX, int y_MAX) {
        maxX = x_MAX;
        maxY = y_MAX;
        movable = new Movable[0];
        size = 0;
    }

    public static void setxMax(int i) {
        maxX = i;
    }

    public static void setyMax(int i) {
        maxY = i;
    }

    public boolean isObjectFittable(Movable m) {
        if (m.getCurrentXPosition() < 0 || m.getCurrentXPosition() > maxX) {
            if (m.getCurrentYPosition() < 0 || m.getCurrentYPosition() > maxY) {
                return false;
            }
        }
        if (m.getType().equals(TYPE.CIRCLE)) {
            int radius = ((MovableCircle) m).getRadius();
            int rightX = m.getCurrentXPosition() + radius;
            int leftX = m.getCurrentXPosition() - radius;
            int upY = m.getCurrentYPosition() + radius;
            int downY = m.getCurrentYPosition() - radius;
            if (rightX > maxX || leftX < 0 || upY > maxY || downY < 0) return false;
        }
        return true;
    }

    public void addMovableObject(Movable m) throws MovableObjectNotFittableException {
        if (!isObjectFittable(m)) throw new MovableObjectNotFittableException(m);
        movable = Arrays.copyOf(movable, size + 1);
        movable[size++] = m;
    }

    public void moveObjectsFromTypeWithDirection(TYPE type, DIRECTION direction) {
        Arrays.stream(movable).filter(mov -> mov.getType().equals(type))
                .forEach(mov -> {
                    try {
                        if (direction.equals(DIRECTION.UP))
                            mov.moveUp();
                        else if (direction.equals(DIRECTION.DOWN))
                            mov.moveDown();
                        else if (direction.equals(DIRECTION.LEFT))
                            mov.moveLeft();
                        else mov.moveRight();
                    } catch (ObjectCanNotBeMovedException e) {
                        e.message();
                    }
                });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Collection of movable objects with size ").append(size).append(":");
        sb.append("\n");
        Arrays.stream(movable).forEach(sb::append);
        return sb.toString();
    }
}

public class CirclesTest {

    public static void main(String[] args) {

        System.out.println("===COLLECTION CONSTRUCTOR AND ADD METHOD TEST===");
        MovablesCollection collection = new MovablesCollection(100, 100);
        Scanner sc = new Scanner(System.in);
        int samples = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < samples; i++) {
            String inputLine = sc.nextLine();
            String[] parts = inputLine.split(" ");

            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int xSpeed = Integer.parseInt(parts[3]);
            int ySpeed = Integer.parseInt(parts[4]);

            if (Integer.parseInt(parts[0]) == 0) { //point
                try {
                    collection.addMovableObject(new MovablePoint(x, y, xSpeed, ySpeed));
                } catch (MovableObjectNotFittableException e) {
                    e.message();
                }
            } else { //circle
                int radius = Integer.parseInt(parts[5]);
                try {
                    collection.addMovableObject(new MovableCircle(radius, new MovablePoint(x, y, xSpeed, ySpeed)));
                } catch (MovableObjectNotFittableException e) {
                    e.message();
                }
            }

        }
        System.out.println(collection.toString());

        System.out.println("MOVE POINTS TO THE LEFT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.LEFT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES DOWN");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.DOWN);
        System.out.println(collection.toString());

        System.out.println("CHANGE X_MAX AND Y_MAX");
        MovablesCollection.setxMax(90);
        MovablesCollection.setyMax(90);

        System.out.println("MOVE POINTS TO THE RIGHT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.RIGHT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES UP");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.UP);
        System.out.println(collection.toString());


    }


}
