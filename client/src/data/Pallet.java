package data;

import java.awt.*;

public enum Pallet {
    BACKGROUND(new Color(30, 31, 34)),
    FOREGROUND(new Color(223, 225, 229)),
    CHAT(new Color(67, 69, 74)),
    SERVER(new Color(192, 132, 103)),
    CLIENT(new Color(196, 123, 184)),
    HELP(new Color(106, 171, 115)),
    BAN(new Color(247, 84, 100)),
    MESSAGE(new Color(223, 225, 229));

    private final Color color;
    Pallet(Color color) {
        this.color = color;
    }

    public Color value() {
        return color;
    }
}
