package team2.reversi;

//import Board;

public interface GameLogic {
	
	/***
	 * Board dimensions
	 */
	public static int COLS = 8;

	public static int ROWS = 8;
	
	/**
	 * players
	 */
	public static int PLAYER_ONE = 1;
	
	public static int PLAYER_TWO = 2;
	
	public static int EMPTY = 0;
	
	/**
	 * Informs if a given player can set a chip in a given cell
	 * @param player
	 * @param col
	 * @param row
	 * @return
	 */
	boolean canSet (int player, int col, int row);
	
	/**
	 * returns the string equivalent of the current difficulty level
	 */
//	public String getDifficulty();
	
	/**
	 * sets the difficulty of the game.
	 * @param difficulty
	 */
//	public void setDifficulty(String difficulty);
	
	/**
	 * Sets the given chip in the given cell
	 * @param player
	 * @param col
	 * @param row
	 */
	void setStone (int player, int col, int row);
	
	/**
	 * Informs if the player is blocked (no moves are allowed for him)
	 * @param player
	 * @return
	 */
	boolean isBlockedPlayer(int player);
	
	
	/**
	 * Inform if the game is finished
	 * @return
	 */
	boolean isFinished();

	/**
	 * Gets an array of the allowed positions for the given player 
	 * @param player
	 * @return
	 */
	int[][] getAllowedPositionsForPlayer(int player);

	/**
	 * Gets the player that has to play
	 * @return
	 */
	int getCurrentPlayer();
	
	/**
	 * Sets the current player
	 * @param player
	 */
	void setCurrentPlayer (int player);
	

	/**
	 * Gets the current status
	 * @return
	 */
	int[][] getGameMatrix();

	/**
	 * Starting on the given column and row, conquers all possible positions
	 * (in all directions), which means, changing all opponent chips to the player color
	 * @param player
	 * @param column
	 * @param row
	 */
	void conquerPosition(int player, int column, int row);

	/**
	 * Gets the counter for the given player
	 * @param player
	 */
	int getCounterForPlayer(int player);

	/**
	 * Starts a new game
	 */
	void initialize();
	
	/**
	 * Gets the board of this game
	 * @return
	 */
	Board getBoard();

	/**
	 * Gets the number of movements for the given player
	 */
	int getMobilityForPlayer(int player);
}