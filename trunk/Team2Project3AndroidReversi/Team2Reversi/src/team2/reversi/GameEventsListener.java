package team2.reversi;

/**
 * This listener will be notified when the score changes
 * Based upon Android Open Source code from Fernando Cherchi
 * Team2 Coders: Laramie Goode, Curtis Franks, Chris Voss
 *
 */
public interface GameEventsListener {
	
	/**
	 * Invoked when the score changes
	 * @param difficulty
	 * @param p1Score
	 * @param p2Score
	 */
	void onScoreChanged(int p1Score, int p2Score);
	
	
//	void onDifficultyChanged(String difficulty);	
	/**
	 * Invoked when the game has been finished
	 * @param winner
	 * The winner of the game (can be none in case of 'equals') 
	 */
	void onGameFinished(int winner);
}