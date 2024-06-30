package backend.model;

public interface Movable {
    // Move relative to current position
    void move(double diffX, double diffY);
    // Move in absolute terms
    void moveTo(double x, double y);
}
