package client.data;

import java.awt.*;

public enum Pallet {
    BACKGROUND(new Color(255, 245, 209)),
    FOREGROUND(new Color(121, 81, 62)),
    CHAT(new Color(224, 205, 182));

    private final Color color;
    Pallet(Color color) {
        this.color = color;
    }

    public Color value() {
        return color;
    }
}
