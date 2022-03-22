package game;

import board.GameBoard;
import board.Hexagon;
import board.Move;
import computerAi.Ai;
import finals.Finals;
import graphicalComponent.GraphicalComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Game extends JPanel implements MouseListener, Runnable {

	private static final long serialVersionUID = 1L;
	public static final long HumanVsHuman = 0;
	public static final long HumanVsComputer = 1;
	private final Logger logger;
	private int widthFrame;
	private int heightFrame;
	private int size;
	private GameBoard board;
	private int gameType;
	private Hexagon oldClickedHex;
	public boolean isRunning;
	public Ai AiEnemy;

	public Game(int width, int height, int size, int gameType) {
		this.logger = (Logger) LogManager.getLogger(Game.class);
		this.widthFrame = width;
		this.heightFrame = height;
		this.gameType = gameType;
		this.size = size;
		this.board = new GameBoard(this.size, width, height);
		this.isRunning = true;
		addMouseListener(this);
		this.board.initial();
		this.oldClickedHex = null;
	}

	@Override
	public void paint(Graphics g) {
		Image img = createImage(widthFrame, heightFrame);
		Graphics gImage = img.getGraphics();
		ImageIcon background = new ImageIcon(Finals.BACKGROUND_IMG);
		background.paintIcon(null, gImage, 0, 0);
		if (this.board.playerHexagons[0] == Hexagon.EMPTY || this.board.playerHexagons[1] == Hexagon.EMPTY) {
		} else {
			for (int i = 2; i < this.board.hexagons.length - 2; i++) {
				for (int j = 2; j < this.board.hexagons.length - 2; j++) {
					Hexagon currentHex = this.board.hexagons[i][j];
					if (currentHex != null) {
						currentHex.paint(gImage, 0, true);
						currentHex.paint(gImage, 5, false);
					}
				}
			}
		}
		
		// Drawing Score String
		String ScoreStr = "Player 1 = " + this.board.playerHexagons[Hexagon.PLAYER1 - 1] + "\nPlayer 2 = "
				+ this.board.playerHexagons[Hexagon.PLAYER2 - 1];

		gImage.setFont(new Font("Calibri", Font.PLAIN, 18));
		
		// If it's the computer turn
		if (this.gameType == HumanVsComputer && this.board.playerTurn == Hexagon.COMPUTER) {
			String ComputerStr = "The computer is thinking...\n";
			gImage.setColor(Color.YELLOW);
			drawString(gImage, ComputerStr, 10, 10);
		}
		gImage.setColor(Color.GREEN);
		drawString(gImage, ScoreStr, this.getWidth() - 100, this.getHeight() - 80);
		g.drawImage(img, 0, 0, this);
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
	 *         Hexagons, return -1
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

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		boolean turnDone = false;
		Hexagon currentHex = checkIfHexagonClicked(new Point(x, y));
		if (gameType == HumanVsHuman || (gameType == HumanVsComputer && this.board.playerTurn != Hexagon.COMPUTER)) {
			if (oldClickedHex != null) {
				if (currentHex != null && oldClickedHex.getPlayer() == this.board.playerTurn
						&& currentHex.getPlayer() == Hexagon.EMPTY) {
					Move moveToDo = new Move(oldClickedHex, currentHex);
					if (GameBoard.doMove(moveToDo, this.board)) {
						System.out.println(moveToDo.toString());
						repaint();
						turnDone = true;
					}
				}

				// Paint the old neighbors (close and far) line in black.
				Hexagon[] oldHexCloseNeighbors = this.board.getCloseNeighbors(oldClickedHex);
				for (int i = 0; i < oldHexCloseNeighbors.length; i++) {
					if (oldHexCloseNeighbors[i] != null) {
						oldHexCloseNeighbors[i].setLineColor(Color.black);
					}
				}
				Hexagon[] oldHexFarNeighbors = this.board.getFarNeighbors(oldClickedHex);
				for (int i = 0; i < oldHexFarNeighbors.length; i++) {
					if (oldHexFarNeighbors[i] != null) {
						oldHexFarNeighbors[i].setLineColor(Color.black);
					}
				}

			}
			if (turnDone) {
				if (gameType == HumanVsComputer) {
				}
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
				for (int i = 0; i < currentHexCloseNeighbors.length; i++) {
					if (currentHexCloseNeighbors[i] != null) {
						currentHexCloseNeighbors[i].setLineColor(Color.green);
					}
				}

				// If the turn is not done, coloring the far neighbors line in yellow.
				Hexagon[] currentHexFarNeighbors = this.board.getFarNeighbors(currentHex);
				for (int i = 0; i < currentHexFarNeighbors.length; i++) {
					if (currentHexFarNeighbors[i] != null && currentHexFarNeighbors[i].getPlayer() != 0) {
						currentHexFarNeighbors[i] = null;
					}
				}
				for (int i = 0; i < currentHexFarNeighbors.length; i++) {
					if (currentHexFarNeighbors[i] != null) {
						currentHexFarNeighbors[i].setLineColor(Color.yellow);
					}
				}
			}
			oldClickedHex = currentHex;
		} else {
			oldClickedHex = null;
		}
		repaint();

	}

	@Override
	public void run() {
		while (isRunning) {

			// Checks if the game ended.
			if (this.board.isGameOver()) {



				int winner = 0;
				// Checks who won.
				if (this.board.playerHexagons[Hexagon.PLAYER1 - 1] > this.board.playerHexagons[Hexagon.PLAYER2 - 1]) {
					winner = Hexagon.PLAYER1;
					System.out.println("Congrats Player 1");
					this.logger.info("Congrats Player 1");
				} else if (this.board.playerHexagons[Hexagon.PLAYER1 - 1] < this.board.playerHexagons[Hexagon.PLAYER2
						- 1]) {
					winner = Hexagon.PLAYER2;
					//System.out.println("Congrats Player 2");
					this.logger.info("Congrats Player 2");
				} else {
					System.out.println("Tie");
				}

				GraphicalComponent frame = (GraphicalComponent) SwingUtilities.windowForComponent(this);
				frame.newGame(winner);
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}

			// The computer plays if it's his turn
			if (gameType == HumanVsComputer && this.board.playerTurn == Hexagon.COMPUTER) {

				if (this.AiEnemy == null || !this.AiEnemy.isRunning) {
					this.AiEnemy = new Ai(this.board);
					new Thread(this.AiEnemy).start();

					oldClickedHex = null;
				}
			}
			repaint();
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

}
