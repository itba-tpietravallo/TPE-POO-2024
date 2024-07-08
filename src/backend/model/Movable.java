package backend.model;

public interface Movable {
    double getX();
    double getY();

    // Move relative to current position
    default void move(double diffX, double diffY) {
        moveTo(getX() + diffX, getY() + diffY);
    }
    // Move in absolute terms
    void moveTo(double x, double y);
}
