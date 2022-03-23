package entities;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import finals.Finals;

import java.awt.*;
import java.util.ArrayList;

public class GameBoard {

    public int size;
    private final int width;
    private final int height;
    public Hexagon[][] hexagons;
    public int[] playerHexagons;
    public int playerTurn;
    private int numOfHexagons;
    public static int radius = 50;
    public static int padding = 5;

    @Inject
    public GameBoard(@Assisted("size") int size, @Assisted("width") int width, @Assisted("height") int height) {
        super();
        this.size = size;
        this.hexagons = new Hexagon[size + 4][size + 4];
        this.playerHexagons = new int[2];
        this.playerTurn = Finals.PLAYER1;
        this.width = width;
        this.height = height;
    }

    public void initial() {
        // The Formula: (3n^2)/4

        double ang30 = Math.toRadians(30);
        double xOff = Math.cos(ang30) * (radius + padding);
        double yOff = Math.sin(ang30) * (radius + padding);
        Point origin = new Point(this.width / 2, this.height / 2);
        int half = size / 2, count = 0;
        for (int row = 0; row < size; row++) {
            int cols = size - java.lang.Math.abs(row - half);
            for (int col = 0; col < cols; col++) {
                count++;
                int storageCol = row < half ? col - row + (size / 2) + 2 : col - half + (size / 2) + 2;
                int storageRow = row - half + (size / 2) + 2;
                int x = (int) (origin.x + xOff * (col * 2 + 1 - cols));
                int y = (int) (origin.y + yOff * (row - half) * 3);
                Hexagon hex = new Hexagon(x, y, radius, storageCol, storageRow);

                // Placing the initial pieces
                if (storageRow == 2 && col == 0) {
                    hex.setPlayer(Finals.PLAYER1);
                } else if (storageRow == 2 && col == cols - 1) {
                    hex.setPlayer(Finals.PLAYER2);
                } else if (storageCol == 2 && col == 0 && row == half) {
                    hex.setPlayer(Finals.PLAYER2);
                } else if (row == half && col == cols - 1) {
                    hex.setPlayer(Finals.PLAYER1);
                } else if (storageRow == size + 1 && col == 0) {
                    hex.setPlayer(Finals.PLAYER1);
                } else if (storageRow == size + 1 && col == cols - 1) {
                    hex.setPlayer(Finals.PLAYER2);
                }

                this.hexagons[storageRow][storageCol] = hex;
            }
        }
        this.playerHexagons[Finals.PLAYER1 - 1] = 3;
        this.playerHexagons[Finals.PLAYER2 - 1] = 3;
        this.playerTurn = Finals.PLAYER1;
        this.numOfHexagons = count;
    }

    public GameBoard clone() {
        GameBoard cloneBoard = new GameBoard(this.size, this.width, this.height);
        cloneBoard.initial();
        for (int i = 0; i < hexagons.length; i++) {
            for (int j = 0; j < hexagons.length; j++) {
                if (hexagons[i][j] == null)
                    cloneBoard.hexagons[i][j] = null;
                else
                    cloneBoard.hexagons[i][j].setPlayer(this.hexagons[i][j].getPlayer());
            }
        }
        cloneBoard.playerHexagons[Finals.PLAYER1 - 1] = this.playerHexagons[Finals.PLAYER1 - 1];
        cloneBoard.playerHexagons[Finals.PLAYER2 - 1] = this.playerHexagons[Finals.PLAYER2 - 1];
        cloneBoard.playerTurn = this.playerTurn;
        return cloneBoard;
    }

    public boolean isGameOver() {
        if (playerHexagons[Finals.PLAYER1 - 1] + playerHexagons[Finals.PLAYER2 - 1] == this.numOfHexagons) {
            return true;
        }
        boolean canNextPlayerMove = false;
        for (int i = 0; i < hexagons.length; i++) {
            for (int j = 0; j < hexagons.length; j++) {
                if (hexagons[i][j] != null && hexagons[i][j].getPlayer() == playerTurn) {
                    if (canHexDuplicate(hexagons[i][j]) || canHexJump(hexagons[i][j])) {
                        canNextPlayerMove = true;
                    }
                }
            }
        }
        if (canNextPlayerMove) {
            return false;
        }
        return true;
    }

    public Hexagon[] getCloseNeighbors(Hexagon current) {
        return getCloseNeighbors(current, this.hexagons);
    }

    public static Hexagon[] getCloseNeighbors(Hexagon current, Hexagon[][] allHexagons) {
        Hexagon[] neighbors = new Hexagon[6];
        neighbors[0] = allHexagons[current.getStorageRow() - 1][current.getStorageCol()];
        neighbors[1] = allHexagons[current.getStorageRow() - 1][current.getStorageCol() + 1];
        neighbors[2] = allHexagons[current.getStorageRow()][current.getStorageCol() + 1];
        neighbors[3] = allHexagons[current.getStorageRow() + 1][current.getStorageCol()];
        neighbors[4] = allHexagons[current.getStorageRow()][current.getStorageCol() - 1];
        neighbors[5] = allHexagons[current.getStorageRow() + 1][current.getStorageCol() - 1];
        return neighbors;
    }

    public Hexagon[] getFarNeighbors(Hexagon current) {
        return getFarNeighbors(current, this.hexagons);
    }

    public static Hexagon[] getFarNeighbors(Hexagon current, Hexagon[][] allHexagons) {
        Hexagon[] neighbors = new Hexagon[12];
        neighbors[0] = allHexagons[current.getStorageRow()][current.getStorageCol() - 2];
        neighbors[1] = allHexagons[current.getStorageRow() + 1][current.getStorageCol() - 2];
        neighbors[2] = allHexagons[current.getStorageRow() + 2][current.getStorageCol() - 2];
        neighbors[3] = allHexagons[current.getStorageRow() + 2][current.getStorageCol() - 1];
        neighbors[4] = allHexagons[current.getStorageRow() + 2][current.getStorageCol()];
        neighbors[5] = allHexagons[current.getStorageRow() + 1][current.getStorageCol() + 1];
        neighbors[6] = allHexagons[current.getStorageRow()][current.getStorageCol() + 2];
        neighbors[7] = allHexagons[current.getStorageRow() - 1][current.getStorageCol() + 2];
        neighbors[8] = allHexagons[current.getStorageRow() - 2][current.getStorageCol() + 2];
        neighbors[9] = allHexagons[current.getStorageRow() - 2][current.getStorageCol() + 1];
        neighbors[10] = allHexagons[current.getStorageRow() - 1][current.getStorageCol() - 1];
        neighbors[11] = allHexagons[current.getStorageRow() - 2][current.getStorageCol()];
        return neighbors;
    }

    public boolean canHexDuplicate(Hexagon hex) {
        Hexagon[] closeNeighbors = getCloseNeighbors(hex);
        for (int i = 0; i < closeNeighbors.length; i++) {
            if (closeNeighbors[i] != null && closeNeighbors[i].getPlayer() == Finals.EMPTY) {
                return true;
            }
        }
        return false;
    }

    public boolean canHexJump(Hexagon hex) {
        Hexagon[] farNeighbors = getFarNeighbors(hex);
        for (int i = 0; i < farNeighbors.length; i++) {
            if (farNeighbors[i] != null && farNeighbors[i].getPlayer() == Finals.EMPTY) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checking if the Move is a jump move.
     *
     * @param move The move that needs to check.
     * @return if the Move is a jump move.
     */
    public static boolean isJumpMove(Move move, GameBoard boardToCheck) {
        Hexagon[] farNeighbors = getFarNeighbors(move.getHexFrom(), boardToCheck.hexagons);
        for (int i = 0; i < farNeighbors.length; i++) {
            if (farNeighbors[i] != null && farNeighbors[i].getStorageRow() == move.getHexTo().getStorageRow()
                    && farNeighbors[i].getStorageCol() == move.getHexTo().getStorageCol()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checking if the Move is a duplicate move.
     *
     * @param move The move that needs to check.
     * @return Checking if the Move is a duplicate move.
     */
    public static boolean isDuplicateMove(Move move, GameBoard boardToCheck) {
        Hexagon[] closeNeighbors = getCloseNeighbors(move.getHexFrom(), boardToCheck.hexagons);
        for (int i = 0; i < closeNeighbors.length; i++) {
            if (closeNeighbors[i] != null && closeNeighbors[i].getStorageRow() == move.getHexTo().getStorageRow()
                    && closeNeighbors[i].getStorageCol() == move.getHexTo().getStorageCol()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Doing the move on the board (Jump/Duplicate), convert the neighbors and
     * update the turn.
     *
     * @param move The move to perform.
     * @return If the move has been done correctly.
     */
    public static boolean doMove(Move move, GameBoard boardToMove) {
        if (isDuplicateMove(move, boardToMove)) {
            boardToMove.hexagons[move.getHexTo().getStorageRow()][move.getHexTo().getStorageCol()].setPlayer(boardToMove.playerTurn);
            boardToMove.playerHexagons[boardToMove.playerTurn - 1]++;
            move.getHexTo().setPlayer(boardToMove.playerTurn);
        } else if (isJumpMove(move, boardToMove)) {
            boardToMove.hexagons[move.getHexTo().getStorageRow()][move.getHexTo().getStorageCol()].setPlayer(boardToMove.playerTurn);
            boardToMove.hexagons[move.getHexFrom().getStorageRow()][move.getHexFrom().getStorageCol()].setPlayer(Finals.EMPTY);
            move.getHexTo().setPlayer(boardToMove.playerTurn);
            move.getHexFrom().setPlayer(Finals.EMPTY);
        } else {
            return false;
        }
        convertNeighbors(move.getHexTo(), boardToMove);
        boardToMove.playerTurn = boardToMove.playerTurn == Finals.PLAYER1 ? Finals.PLAYER2 : Finals.PLAYER1;
        return true;
    }

    /**
     * Convert the neighbors.
     *
     * @param hex The Hexagon that convert
     */
    public static void convertNeighbors(Hexagon hex, GameBoard boardToMove) {
        Hexagon[] currentHexCloseNeighbors = boardToMove.getCloseNeighbors(hex);
        for (int i = 0; i < currentHexCloseNeighbors.length; i++) {
            if (currentHexCloseNeighbors[i] != null && currentHexCloseNeighbors[i].getPlayer() != boardToMove.playerTurn
                    && currentHexCloseNeighbors[i].getPlayer() != 0) {
                currentHexCloseNeighbors[i]
                        .setPlayer(hex.getPlayer() == Finals.PLAYER1 ? Finals.PLAYER1 : Finals.PLAYER2);
                boardToMove.playerHexagons[hex.getPlayer() - 1]++;
                boardToMove.playerHexagons[hex.getPlayer() == Finals.PLAYER1 ? 1 : 0]--;
            }
        }
    }

    /**
     * Getting all the hexagons of the player.
     *
     * @param player The player that his hexagons are wanted.
     * @return An ArrayList with all the hexagons of the player.
     */
    public ArrayList<Hexagon> getHexagonOfPlayer(int player) {
        ArrayList<Hexagon> allHexagons = new ArrayList<Hexagon>();
        for (int i = 2; i < hexagons.length - 2; i++) {
            for (int j = 2; j < hexagons.length - 2; j++) {
                if (this.hexagons[i][j] != null && this.hexagons[i][j].getPlayer() == player) {
                    allHexagons.add(this.hexagons[i][j]);
                }
            }
        }
        return allHexagons;
    }

    public ArrayList<Move> getAllValidMoves(int player) {
        ArrayList<Move> allMoves = new ArrayList<Move>();
        ArrayList<Hexagon> playerHexagons = getHexagonOfPlayer(player);
        for (int i = 0; i < playerHexagons.size(); i++) {
            Hexagon[] closeNeighbors = getCloseNeighbors(playerHexagons.get(i));
            for (int j = 0; j < closeNeighbors.length; j++) {
                if (closeNeighbors[j] != null && closeNeighbors[j].getPlayer() == Finals.EMPTY) {
                    allMoves.add(new Move(playerHexagons.get(i), closeNeighbors[j]));
                }

            }
            Hexagon[] farNeighbors = getFarNeighbors(playerHexagons.get(i));
            for (int j = 0; j < farNeighbors.length; j++) {
                if (farNeighbors[j] != null && farNeighbors[j].getPlayer() == Finals.EMPTY) {
                    allMoves.add(new Move(playerHexagons.get(i), farNeighbors[j]));
                }
            }
        }
        return allMoves;
    }
}
