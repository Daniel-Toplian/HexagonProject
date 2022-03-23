package menu;

import entities.GameBoard;
import finals.Finals;
import frames.GameGraphicalComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class StartMenu extends Menu {

    private static final long serialVersionUID = 1L;
    private Logger logger;
    private int size;
    private int width;
    private int height;
    private int gameType;
    private GameGraphicalComponent graphicalComponent;

    public StartMenu() {
        super(Finals.GAME_TITLE);
        this.logger = (Logger) LogManager.getLogger(StartMenu.class);

        // Changing the logo image.
        ImageIcon logo = new ImageIcon(Finals.LOGO_ICON);
        this.setIconImage(logo.getImage());

        // Stop the program when closing the window.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (quit())
                    System.exit(0);
                else
                    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
            }
        });
    }

    public void start(int winner) {
        try {
            this.setVisible(false);
            this.graphicalComponent.isRunning = false;
            displayGameResult(winner);
            this.remove(this.graphicalComponent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Opening screen. The user choose the size.
        boolean continueFlag = displayOptions();
        if (!continueFlag) {
            System.exit(0);
        }

        this.setSize(width, height);
        this.graphicalComponent = new GameGraphicalComponent(width, height, size, gameType);

        this.add(this.graphicalComponent);

        new Thread(this.graphicalComponent).start();

        // The size cannot be changed by the user.
        this.setResizable(false);

        // Puts the windows in the center.
        this.setLocationRelativeTo(null);

        this.setVisible(true);

    }

    public boolean displayOptions() {
        ImageIcon icon = new ImageIcon(Finals.LOGO_IMG);

        String[] gameOptions = {"Human VS Human", "Human VS Computer"};
        JComboBox<String> gameType = new JComboBox<String>(gameOptions);
        gameType.setSelectedIndex(1);

        String[] sizeOptions = {"Huge", "Big", "Medium", "Small", "Tiny"};
        JComboBox<String> sizeType = new JComboBox<String>(sizeOptions);
        sizeType.setSelectedIndex(2);

        Object[] message = {"Game Type:", gameType, "Choose the map size:", sizeType};
        int option = JOptionPane.showConfirmDialog(null, message, "Hexagon", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, icon);

        // If the user wants to quit
        if (option != JOptionPane.OK_OPTION) {
            if (quit())
                return false;
            else
                return displayOptions();
        }

        // Updating the game type
        this.gameType = gameType.getSelectedIndex();

        // Updating the size
        switch (Objects.requireNonNull(sizeType.getSelectedItem()).toString()) {
            case "Huge":
                this.size = 13;
                this.height = 1000;
                this.width = 1600;
                GameBoard.radius = 40;
                GameBoard.padding = 3;
                break;
            case "Big":
                this.size = 11;
                this.height = 1000;
                this.width = 1400;
                break;
            case "Medium":
                this.size = 9;
                this.height = 1000;
                this.width = 1000;
                break;
            case "Small":
                this.size = 7;
                this.height = 800;
                this.width = 800;
                break;
            case "Tiny":
                this.size = 5;
                this.height = 800;
                this.width = 800;
                break;
            default:
                break;
        }
        return true;
    }

    public boolean quit() {
        int confirmed = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Exit",
                JOptionPane.YES_NO_OPTION);
        return confirmed == JOptionPane.YES_OPTION;
    }

    private void displayGameResult(int winner) {
        ImageIcon icon = new ImageIcon(Finals.WINNER_IMG);
        String message = "Congrats Player " + winner + "\nYou Won!";
        if (winner == 0) {
            message = "Tie";
            icon = new ImageIcon(Finals.GAMEOVER_IMG);
        } else if (this.graphicalComponent.getGameType() == GameGraphicalComponent.HumanVsComputer && winner == Finals.COMPUTER) {
            message = "The Computer Won";
            icon = new ImageIcon(Finals.GAMEOVER_IMG);
        }
        JOptionPane.showConfirmDialog(null, message, "THE END", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, icon);

    }
}
