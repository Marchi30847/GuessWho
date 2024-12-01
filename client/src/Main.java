import presenter.GamePresenter;
import ui.ViewManager;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GamePresenter(new ViewManager()));
    }
}
