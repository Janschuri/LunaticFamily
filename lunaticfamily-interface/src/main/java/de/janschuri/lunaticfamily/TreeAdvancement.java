package de.janschuri.lunaticfamily;

import java.io.Serializable;

public interface TreeAdvancement extends Serializable {

    String getKey();
    String getTitle();
    String getDescription();
    String getSkinUrl();
    float getX();
    TreeAdvancement setX(float x);
    float getY();
    TreeAdvancement getParent();
    Side getSide();

    enum Side {
        LEFT,
        RIGHT,
        CENTER,
    }
}

