package team2.reversi;



public interface GameFacade {
	
	int NONE = 0;
	

	/**
	 * starts a new game
	 */
	void restart();
	
	void undo();
	void redo();
	void movementDone();
	
	/**
	 * Sets a chip in the given position
	 * @param player
	 * @param col
	 * @param row
	 */
	void set (int player, int col, int row);
	
	int getPlayerOne();
	int getPlayerTwo();
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

	public void setDifficulty(String difficulty);
	public void setPlayerColor(String playerColorString);

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


	public void setStartTime();
	public long getStartTime();
	public void setStopTime();
	public long getStopTime();
	public int getWinningDifferential();
	public void setWinningDifferential(int winningDifferential);
	public void setGameTime();
	public long getGameTime();


	
}