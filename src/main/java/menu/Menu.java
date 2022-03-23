package menu;

import javax.swing.*;
import java.awt.*;

public abstract class Menu extends JFrame {

    public Menu(String title) throws HeadlessException {
        super(title);
    }

    boolean quit(){
        int confirmed = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Exit",
                JOptionPane.YES_NO_OPTION);
        return confirmed == JOptionPane.YES_OPTION;
    }

    abstract boolean displayOptions();

    public abstract void start(int i);
}
