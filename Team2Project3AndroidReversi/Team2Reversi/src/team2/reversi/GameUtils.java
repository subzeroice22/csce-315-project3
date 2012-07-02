package team2.reversi;

import java.util.ArrayList;
import java.util.List;

//import GameLogic;

public class GameUtils {

	public static int counter = 0;

	/**
	 * Gets the list of movements for the given player
	 * 
	 * @param player
	 * @param board
	 * @return
	 */
	public static List<Movement> getAllowedMovementsForPlayer(int player,
			Board board) {

		MatrixChecker matrixChecker = new MatrixChecker(board);
		List<Movement> list = new ArrayList<Movement>();

		// scanning the grid
		for (int col = 0; col < GameLogicImpl.COLS; col++) {
			for (int row = 0; row < GameLogicImpl.ROWS; row++) {
				// if player can set
				if (matrixChecker.canSet(player, col, row)) {
					list.add(new Movement(col, row, player));
				}
			}
		}
		return list;
	}

	/**
	 * Given a player, returns its opponent
	 * 
	 * @param player
	 * @return
	 */
	public static int opponent(int player) {
		if (player == GameLogicImpl.player_one) {
			return GameLogicImpl.player_two;
		} else if (player == GameLogicImpl.player_two) {
			return GameLogicImpl.player_one;
		} else {
			return 0;
		}
	}

	/**
	 * Calculates the power of a number
	 * 
	 * @param base
	 * @param power
	 * @return
	 */
	public static int pow(int base, int power) {
		if (power >= 1)
			return base * (pow(base, power - 1));
		else
			return 1;
	}
}
