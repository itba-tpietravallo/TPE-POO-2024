package backend.model;

public interface Figure extends Movable {
    boolean pointBelongs(Point p);
    void move(double diffX, double diffY);
}
