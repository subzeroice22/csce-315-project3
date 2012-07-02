package team2.reversi;

//import GameLogic;

public class Board {

	// /////////////////////// CONSTANTS ///////////////////////////////
	/***
	 * Board dimensions
	 */
	public static int COLS = 8;

	public static int ROWS = 8;
	

	// /////////////////////// PRIVATE FIELDS //////////////////////////

	/**
	 * The matrix
	 */
	private int[][] gameMatrix = new int[COLS][ROWS];
	
	/**
	 * The situation of all the columns and rows. 
	 * The integer is an unique value that identifies the column status
	 */
	
	/**
	 * the scores for both players
	 */
	private int[] scores = new int[2];
	
	
	// /////////////////////// LIFETIME ///////////////////////////////

	public Board() {
		this.initializeMatrix();
	}

	// /////////////////////// PUBLIC METHODS //////////////////////////

	/**
	 * Sets the given stone in the given coordinate and calculates mobility and
	 * location values
	 */
	public void setStone(int col, int row, int player) {

		//if the position was occupied by other player, 
		//reducing the score
		if (this.gameMatrix[col][row] != GameLogicImpl.EMPTY) {
			scores[this.gameMatrix[col][row]-1]--;
		}
		this.gameMatrix[col][row] = player;
		if (player != GameLogicImpl.EMPTY) {
			scores[player - 1]++;
		}
	}
	
	

	/**
	 * Returns a new representation that is an exact clone of this board
	 */
	public Board clone() {
		Board cloned = new Board();
		
		int[][] clonedMatrix = new int[GameLogicImpl.COLS][GameLogicImpl.ROWS];
		
		for(int i = 0; i < GameLogicImpl.COLS; i++) {
			for (int j = 0; j < GameLogicImpl.ROWS; j++) {
				clonedMatrix[i][j] = this.gameMatrix[i][j];
			}
		}
		
		//cloned.setMatrix(this.gameMatrix.clone());
		cloned.setMatrix(clonedMatrix);
		cloned.setCounterForPlayer(GameLogicImpl.player_one, this.scores[0]);
		cloned.setCounterForPlayer(GameLogicImpl.player_two, this.scores[1]);
		//System.out.println(GameHelper.counter++);
		return cloned;
	}
	
	/**
	 * Returns a string representation of the matrix
	 */
	public String toString() {
		
		StringBuffer str = new StringBuffer();
		str.append("\n");
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				str.append(String.format("|%d|", this.gameMatrix[col][row]));
			}
			str.append("\n");
		}
		
		return str.toString();
	}
	
	// /////////////////////// PRIVATE METHODS //////////////////////////
	/**
	 * initializes the game matrix
	 */
	private void initializeMatrix() {
		for (int col = 0; col < COLS; col++) {
			for (int row = 0; row < ROWS; row++) {
				this.gameMatrix[col][row] =	GameLogicImpl.EMPTY;
			}
		}
		this.setStone(3, 3, GameLogicImpl.player_one);
		this.setStone(4, 4, GameLogicImpl.player_one);
		this.setStone(3, 4, GameLogicImpl.player_two);
		this.setStone(4, 3, GameLogicImpl.player_two);
		
	}
	
	
	
	/**
	 * Stores the update position in the north west axis (\)
	 * @param col
	 * @param row
	 * @param player
	 */

	
	/**
	 * Stores the update position in the north east axis (/)
	 * @param col
	 * @param row
	 * @param player
	 */

	

	// /////////////////////// ACCESSORS //////////////////////////
	/**
	 * Gets the matrix (read only)
	 */
	public int[][] getMatrix() {
		return this.gameMatrix.clone();
	}
	
	/**
	 * Sets the given matrix 
	 * @param matrix
	 */
	public void setMatrix(int[][] matrix) {
		this.gameMatrix = matrix;
	}

	/**
	 * Gets the current score for the given player
	 * 
	 * @param player
	 * @return
	 */
	public int getCounterForPlayer(int player) {

		return scores[player - 1];
	}

	/**
	 * Sets the given score for the given player
	 * @param player
	 * @param score
	 */
	public void setCounterForPlayer(int player, int score) {
		scores[player - 1] = score;
	}
}