package frames;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import computerAi.Ai;
import entities.GameBoard;
import entities.Hexagon;
import entities.Move;
import finals.Finals;
import guiceModule.GameBoardFactory;
import menu.Menu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameGraphicalComponent extends JPanel implements MouseListener, Runnable {

    public static final long HumanVsHuman = 0;
    public static final long HumanVsComputer = 1;
    private final int widthFrame;
    private final int heightFrame;
    private final int size;
    private final int gameType;
    private final GameBoard board;
    private final Logger logger;
    private Hexagon oldClickedHex;
    private boolean gameStatus;
    public Ai AiEnemy;

    @Inject
    public GameGraphicalComponent(@Assisted("width") int width, @Assisted("height") int height, @Assisted("size") int size, @Assisted("gameType") int gameType,
                                  GameBoardFactory gameBoardFactory) {
        this.logger = (Logger) LogManager.getLogger(GameGraphicalComponent.class);
        this.widthFrame = width;
        this.heightFrame = height;
        this.gameType = gameType;
        this.size = size;
        this.board = gameBoardFactory.create(size, width, height);
        this.board.initial();
        this.gameStatus = true;
        addMouseListener(this);
        this.oldClickedHex = null;
    }

    @Override
    public void paint(Graphics graphics) {
        Image img = createImage(widthFrame, heightFrame);
        Graphics imageGraphics = img.getGraphics();
        ImageIcon background = new ImageIcon(Finals.BACKGROUND_IMG);
        background.paintIcon(null, imageGraphics, 0, 0);

        if (this.board.playerHexagons[0] != Finals.EMPTY && this.board.playerHexagons[1] != Finals.EMPTY) {
            for (int i = 2; i < this.board.hexagons.length - 2; i++) {
                for (int j = 2; j < this.board.hexagons.length - 2; j++) {
                    Hexagon currentHex = this.board.hexagons[i][j];
                    if (currentHex != null) {
                        currentHex.paint(imageGraphics, 0, true);
                        currentHex.paint(imageGraphics, 5, false);
                    }
                }
            }
        }

        // Drawing Score String
        String ScoreStr = "Player 1 = " + this.board.playerHexagons[Finals.PLAYER1 - 1] + "\nPlayer 2 = " + this.board.playerHexagons[Finals.PLAYER2 - 1];

        imageGraphics.setFont(new Font("Calibri", Font.PLAIN, 18));

        // If it's the computer turn
        if (this.gameType == HumanVsComputer && this.board.playerTurn == Finals.COMPUTER) {
            String ComputerStr = "The computer is thinking...\n";
            imageGraphics.setColor(Color.YELLOW);
            drawString(imageGraphics, ComputerStr, 10, 10);
        }
        imageGraphics.setColor(Color.GREEN);
        drawString(imageGraphics, ScoreStr, this.getWidth() - 100, this.getHeight() - 80);
        graphics.drawImage(img, 0, 0, this);
    }

    private void drawString(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n"))
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }

    /**
     * Checks if the mouse clicked one of the Hexagons.
     *
     * @param clicked The Point where the mouse clicked.
     * @return The number of the Hexagon. If the mouse dosen't clicked any of the
     * Hexagons, return -1
     */
    private Hexagon checkIfHexagonClicked(Point clicked) {
        for (int i = 2; i < this.board.hexagons.length - 2; i++) {
            for (int j = 2; j < this.board.hexagons.length - 2; j++) {
                if (this.board.hexagons[i][j] != null) {
                    if (this.board.hexagons[i][j].contains(clicked))
                        return this.board.hexagons[i][j];
                }
            }
        }
        return null;
    }

    public int getGameType() {
        return gameType;
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        boolean turnDone = false;
        Hexagon currentHex = checkIfHexagonClicked(new Point(x, y));
        if (gameType == HumanVsHuman || (gameType == HumanVsComputer && this.board.playerTurn != Finals.COMPUTER)) {
            if (oldClickedHex != null) {
                if (currentHex != null && oldClickedHex.getPlayer() == this.board.playerTurn
                        && currentHex.getPlayer() == Finals.EMPTY) {
                    Move moveToDo = new Move(oldClickedHex, currentHex);
                    if (GameBoard.doMove(moveToDo, this.board)) {
                        System.out.println(moveToDo.toString());
                        repaint();
                        turnDone = true;
                    }
                }

                // Paint the old neighbors (close and far) line in black.
                Hexagon[] oldHexCloseNeighbors = this.board.getCloseNeighbors(oldClickedHex);
                for (Hexagon oldHexCloseNeighbor : oldHexCloseNeighbors) {
                    if (oldHexCloseNeighbor != null) {
                        oldHexCloseNeighbor.setLineColor(Color.black);
                    }
                }

                Hexagon[] oldHexFarNeighbors = this.board.getFarNeighbors(oldClickedHex);
                for (Hexagon oldHexFarNeighbor : oldHexFarNeighbors) {
                    if (oldHexFarNeighbor != null) {
                        oldHexFarNeighbor.setLineColor(Color.black);
                    }
                }

            }
            if (turnDone) {
                repaint();
                System.out.println("First Player: " + this.board.playerHexagons[0] + "\nSecond Player: "
                        + this.board.playerHexagons[1]);
                return;
            }

            // If the turn is not done, coloring the neighbors.
            if (currentHex != null && currentHex.getPlayer() == this.board.playerTurn) {
                // If the turn is not done, coloring the close neighbors line in green.
                Hexagon[] currentHexCloseNeighbors = this.board.getCloseNeighbors(currentHex);

                for (int i = 0; i < currentHexCloseNeighbors.length; i++) {
                    if (currentHexCloseNeighbors[i] != null && currentHexCloseNeighbors[i].getPlayer() != 0) {
                        currentHexCloseNeighbors[i] = null;
                    }
                }

                for (Hexagon currentHexCloseNeighbor : currentHexCloseNeighbors) {
                    if (currentHexCloseNeighbor != null) {
                        currentHexCloseNeighbor.setLineColor(Color.green);
                    }
                }

                // If the turn is not done, coloring the far neighbors line in yellow.
                Hexagon[] currentHexFarNeighbors = this.board.getFarNeighbors(currentHex);
                for (int i = 0; i < currentHexFarNeighbors.length; i++) {
                    if (currentHexFarNeighbors[i] != null && currentHexFarNeighbors[i].getPlayer() != 0) {
                        currentHexFarNeighbors[i] = null;
                    }
                }

                for (Hexagon currentHexFarNeighbor : currentHexFarNeighbors) {
                    if (currentHexFarNeighbor != null) {
                        currentHexFarNeighbor.setLineColor(Color.yellow);
                    }
                }
            }
            oldClickedHex = currentHex;
        } else {
            oldClickedHex = null;
        }
        repaint();

    }

    public boolean isGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(boolean gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void run() {
        while (gameStatus) {

            if (this.board.isGameOver()) {
                int winner = 0;

                // Checks who won.
                if (this.board.playerHexagons[Finals.PLAYER1 - 1] > this.board.playerHexagons[Finals.PLAYER2 - 1]) {
                    winner = Finals.PLAYER1;
                    System.out.println("Congrats Player 1");
                    this.logger.info("Congrats Player 1");
                } else if (this.board.playerHexagons[Finals.PLAYER1 - 1] < this.board.playerHexagons[Finals.PLAYER2 - 1]) {
                    winner = Finals.PLAYER2;
                    //System.out.println("Congrats Player 2");
                    this.logger.info("Congrats Player 2");
                } else {
                    System.out.println("Tie");
                }

                Menu menu = (Menu) SwingUtilities.windowForComponent(this);
                menu.start(winner);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }

            // The computer plays if it's his turn
            if (gameType == HumanVsComputer && this.board.playerTurn == Finals.COMPUTER) {

                if (this.AiEnemy == null || !this.AiEnemy.isRunning) {
                    this.AiEnemy = new Ai(this.board);
                    new Thread(this.AiEnemy).start();

                    oldClickedHex = null;
                }
            }
            repaint();
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }
}
