package computerAi;

import java.util.ArrayList;

import board.GameBoard;
import board.Hexagon;
import board.Move;

public class Ai implements Runnable {

	private GameBoard currentBoard;
	private int call;
	private int prunned;
	private Move bestMove;
	private int depth = 1;
	public boolean isRunning;

	public Ai(GameBoard currentBoard) {
		this.currentBoard = currentBoard;
		this.call = 0;
		this.prunned = 0;
		this.bestMove = null;
		this.isRunning = true;
	}

	/**
	 * Make a computer move.
	 */
	public void computerMove() {
		GameBoard cloneBoard = this.currentBoard.clone();
		int alphabeta = alphaBetaPrunning(cloneBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		System.out.println("alphabeta = " + alphabeta + "\nprunned " + this.prunned + "\ncall " + this.call + " times");
		if (bestMove != null) {
			GameBoard.doMove(bestMove, currentBoard);
			System.out.println(bestMove.toString());
		} else {
			System.out.println("No best move found");
		}
	}

	/**
	 * Gets the best Move to play.
	 * 
	 * @param tempBoard The board to play on
	 * @param depth The number of turns checks look forward.
	 * @param alpha The Max evaluate of the player
	 * @param beta The Min evaluate of the enemy
	 * @param returnBestMove 
	 * @return The score of the evaluation.
	 */
	public int alphaBetaPrunning(GameBoard tempBoard, int depth, int alpha, int beta, boolean returnBestMove) {

		// Checking if the game ended or depth is zero
		if (tempBoard.isGameOver() || depth == 0) {
			return evaluate(tempBoard);
		}
		// Getting all the valid Moves
		ArrayList<Move> validMoves = tempBoard.getAllValidMoves(tempBoard.playerTurn);

		if (returnBestMove) {
			prunned = 0;
			call = 0;
			this.bestMove = null;
			if (validMoves.size() > 0) {
				this.bestMove = validMoves.get(0);
			}
		}

		// Checking if the function has been called too much...
		if (call >= 1000000) {
			return evaluate(tempBoard);
		}

		if (tempBoard.playerTurn == Hexagon.COMPUTER) {
			for (int i = 0; i < validMoves.size(); i++) {
				GameBoard copy = tempBoard.clone();
				Move move = validMoves.get(i);
				GameBoard.doMove(move, copy);
				call++;
				int score = alphaBetaPrunning(copy, depth - 1, alpha, beta, false);

				if (score > alpha) {
					alpha = score;
					if (returnBestMove) {
						this.bestMove = move;
					}
				}
				if (alpha >= beta) {
					prunned++;
					return alpha;
				}
			}
			return alpha;
		} else {
			for (int i = 0; i < validMoves.size(); i++) {
				GameBoard copy = tempBoard.clone();
				Move move = validMoves.get(i);
				GameBoard.doMove(move, copy);
				call++;

				int score = alphaBetaPrunning(copy, depth - 1, alpha, beta, false);
				copy.playerTurn = Hexagon.PLAYER1;
				if (score < beta) {
					beta = score;
				}
				if (alpha >= beta) {
					prunned++;
					return beta;
				}
			}
			return beta;
		}
	}


	/**
	 * 
	 * @param board The Board to evaluate
	 * @return The evaluation score.
	 */
	public int evaluate(GameBoard board) {
		return board.playerHexagons[Hexagon.COMPUTER - 1] - board.playerHexagons[Hexagon.PLAYER1 - 1];
	}

	@Override
	public void run() {
		if (this.isRunning) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			computerMove();
			System.out.println("First Player: " + this.currentBoard.playerHexagons[0] + "\nSecond Player: "
					+ this.currentBoard.playerHexagons[1]);

			this.isRunning = false;
		}
		
	}
}
