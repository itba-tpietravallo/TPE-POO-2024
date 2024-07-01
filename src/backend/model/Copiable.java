package backend.model;

import frontend.drawables.Drawable;

public interface Copiable<T> {
    T getCopy();
}
