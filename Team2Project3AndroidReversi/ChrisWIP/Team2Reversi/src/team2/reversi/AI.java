package team2.reversi;

import java.util.List;

public class AI {

	// ///////////////////// CONSTANTS ///////////////////////////////////

	/**
	 * The importance of the mobility in the analysis of a position
	 */
	private static final int MOBILITY_COEFF = 10;

	/**
	 * The importance of the positions values in the analysis of the global
	 * position
	 */
	private static final int POSITIONS_COEFF = 6;

	// ///////////////////// PRIVATE FIELDS //////////////////////////////

	/**
	 * The values of the initial board
	 */
	private int[][] values = new int[][] { 
			{ 50, -1, 5, 2, 2, 5, -1, 50 },
			{ -1, -10, 1, 1, 1, 1, -10, -1 }, 
			{ 5, 1, 1, 1, 1, 1, 1, 5 },
			{ 2, 1, 1, 0, 0, 1, 1, 2 }, 
			{ 2, 1, 1, 0, 0, 1, 1, 2 },
			{ 5, 1, 1, 1, 1, 1, 1, 5 }, 
			{ -1, -10, 1, 1, 1, 1, -10, -1 },
			{ 50, -1, 5, 2, 2, 5, -1, 50 } };

	/**
	 * The number of movements the machine will go further
	 */
    //********************this is a HIGH priority task*****************************
    //TODO Adjust the AI difficulty based on the ListPreferences user selection.
	//While displaying the correct difficulty level on the main.xml is cool, it is 
    //more important that we figure out how to set this depth integer to the values
    //of 0, 1, or 2 depending on what the user selects from the ListPreferences
	//menu.  See GameFacadeImpl.java for my attempt at getters and setters for a
	//game difficultyLevel
    //********************this is a HIGH priority task*****************************
	private int depth = 2;

	/**
	 * The best move
	 */
	private Movement bestMove = null;

	/**
	 * The board (initially a clone of the real one)
	 */
	private Board board;

	private int[] sign = new int[] { 1, -1 };

	public AI(Board board,int Depth) {
		this.depth=Depth;
		this.board = board;
		System.out.println("***************************** RANDOM " + POSITIONS_COEFF);
	}

	// /////////////////////// PUBLIC METHODS //////////////////////////
	
	/**
	 * Return the best movement for the given player
	 * 
	 * @param player
	 * @return
	 */
	public Movement getBestMove(int player) {

		int color = player - 1;
		int currDepth = 0;
		this.negaMax(this.board, currDepth, color);
		return this.bestMove;
		
	}

	/**
	 * Negamax algorithm to look forward
	 * 
	 * @param board
	 * @param currentDepth
	 * @param color
	 * @return
	 */
	private int negaMax(Board board, int currentDepth, int color) {

		int player = color + 1;
		boolean isFinished = false;

		if (isFinished || currentDepth > this.depth) {
			return sign[color] * analysis(board, color);
		}
		// max play value of all movements
		int max = Integer.MIN_VALUE + 1;

		List<Movement> movements = GameUtils.getAllowedMovementsForPlayer(
				player, board);
		if (movements.size() == 0) {
			isFinished = true;
		}
		for (Movement movement : movements) {

			Board newMovementBoard = board.clone();
			GameLogic gl = new GameLogicImpl(newMovementBoard);

			gl.setStone(movement.getPlayer(), movement.getColumn(), movement
					.getRow());
			gl.conquerPosition(movement.getPlayer(), movement.getColumn(),
					movement.getRow());
			int x = -negaMax(newMovementBoard, currentDepth + 1, 1 - color);
			if (x > max) {
				max = x;
				// it only must return "depth 0" movements
				if (currentDepth == 0) {
					this.bestMove = movement;
				}
			}
		}
		return max;
	}

	/**
	 * Analyze the situation regarding mobility.
	 * 
	 * @param board
	 * @param color
	 * @return
	 */
	private int analysis(Board board, int color) {

		int player = color + 1;
		int points;

		GameLogic logic = new GameLogicImpl(board);
		int mobility = logic.getMobilityForPlayer(player);
		int positions = this.evaluateStrategicPosition(board, player);

		points = mobility * MOBILITY_COEFF + positions * POSITIONS_COEFF;

		return points;

	}

	/**
	 * Returns the addition of the values of each position from the perspective
	 * of the player
	 * 
	 * @param board
	 * @param player
	 * @return
	 */
	private int evaluateStrategicPosition(Board board, int player) {

		int total = 0;
		for (int i = 0; i < GameLogic.COLS; i++) {
			for (int j = 0; j < GameLogic.ROWS; j++) {
				if (board.getMatrix()[i][j] == player) {
					total += this.values[i][j];
				}
			}
		}

		return total;
	}

	
}
