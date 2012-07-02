package team2.reversi;

import java.util.Date;

public interface GameFacade {
	
	/**
	 * players
	 */
	public static int PLAYER_ONE = 1;
	
	public static int PLAYER_TWO = 2;
	
	public static int NONE = 0;
	
	public static long START_TIME = 0;
	
	public static long STOP_TIME = 0;
	
	/**
	 * starts a new game
	 */
	void restart();
	
	/**
	 * Sets a chip in the given position
	 * @param player
	 * @param col
	 * @param row
	 */
	void set (int player, int col, int row);
	
	
	/**
	 * Gets the current player
	 * @return
	 */
	int getCurrentPlayer();


	/**
	 * Gets the matrix of allowed positions of the player that has to play
	 * @return
	 */
	int[][] getAllowedPositionsForPlayer();

	
	/**
	 * Gets the current matrix of the game
	 * @return
	 */
	int[][] getGameMatrix();
	
	/**
	 * @param gameLogic the gameLogic to set
	 */
	void setGameLogic(GameLogic gameLogic);


	/**
	 * The given listener will be notified when the score changes
	 * @param listener
	 * The listener
	 */
	void setGameEventsListener(GameEventsListener listener);

	/**
	 * Returns the score for the given player
	 * @param player
	 * @return
	 */
	int getScoreForPlayer(int player);
	
	/**
	 * If true the game is against droid, otherwise against human
	 * @param isDroid
	 */
	public void setMachineOpponent(boolean machineOpponent);
	
	/**
	 * Gets if the opponent is droid
	 * @return
	 */
	public boolean getMachineOpponent();

	public void setDifficulty(String difficulty);
	public String getDifficulty();
	public void setStartTime();
	public long getStartTime();
	public void setStopTime();
	public long getStopTime();
	public int getWinningDifferential();
	public void setWinningDifferential(int winningDifferential);
	public void setGameTime();
	public String getGameTime();
}