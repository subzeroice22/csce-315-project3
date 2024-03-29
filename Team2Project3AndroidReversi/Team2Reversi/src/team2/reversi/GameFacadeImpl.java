package team2.reversi;

import team2.reversi.GameFacade;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import team2.reversi.GameEventsListener;
import team2.reversi.GameLogic;

/**
 * The implementation of the high level method used in the game
 * Based upon Android Open Source code from Fernando Cherchi
 * Team2 Coders: Laramie Goode, Curtis Franks, Chris Voss
 *
 */
public class GameFacadeImpl implements GameFacade {

	// /////////////////////// PRIVATE FIELDS //////////////////////////
	/***
	 * Board dimensions
	 */
	public static int COLS = 8;

	public static int ROWS = 8;
	
	public static int EMPTY = 0;
	
	public static boolean firstPlay = true;
	/******************************************************
	 * TODO: I moved the FINAL variables out of the IMPLEMENT classes
	 * GameFacade and GameLogic, into GameFacadeImpl and GameLogicImpl.
	 * I couldn't alter those variables while they were inside the Implement
	 * classes.  I changed PLAYER_ONE and PLAYER_TWO to lower case public 
	 * variables and changed all references throughout the package.  Then I tried 
	 * to use getSharedPreferences from within isAIFirst() to determine which color
	 * the user selected and set player_one and player_two respectively.
	 * My first attempt at this compiled but seg faulted.  I didn't have time to 
	 * debug.
	 */
/*	private static Context context = null;

	public void UserPreferences(Context context) {
		GameFacadeImpl.context = context;
    }*/

	/**
	 * players
	 */
	//public static int player_one = 1;//Settings.isAIFirst(context.getApplicationContext(), "player_one");
	//public static int player_two = 2;//Settings.isAIFirst(context.getApplicationContext(), "player_two");

	public int getPlayerOne(){
		return this.gameLogic.getPlayerOne();
	}
	public int getPlayerTwo(){
		return this.gameLogic.getPlayerTwo();
	}
	/**
	 * access to the game operations
	 */
	private GameLogic gameLogic;

	/**
	 * This is the object to be notified of the game situation
	 */
	private GameEventsListener gameEventsListener;

	/**
	 * Indicates if we are in 1 player mode or 2 player mode
	 */
	private boolean isMachineOpponent = true;
	
	/**
	 * Indicates if we are in 1 player mode or 2 player mode
	 */

	private long startTime = 0;
	
	private long stopTime = 0;
	
	private long gameTime = 0;
	
	private int winningDifferential = 0;
	
	
	public void undo(){
		this.gameLogic.undo();
	}
	public void redo(){
		this.gameLogic.redo();
	}

	// /////////////////////// PUBLIC METHODS //////////////////////////

	/**
	 * Starts a new game
	 */
	@Override
	public void restart() {

		this.gameLogic.initialize();
	}

	/**
	 * Sets the chip for the given player in a given position
	 */
	@Override
	public void set(int player, int col, int row) {
		int AI = 2;
		boolean playerHasChanged;
		//if the next player has no move then doMove will perform the move
		//and then return false so that playerhasChanged becomes false.
		//otherwise, playerHasChanged will become true.
		if(this.gameLogic.getPlayerOne()==2){
			AI = 1;
		}else{
			AI = 2;
		}
		playerHasChanged = this.doMovement(player, col, row);
		// if is the machine moment...
		if (this.isMachineOpponent && (firstPlay||playerHasChanged) && 
				this.gameLogic.getCurrentPlayer() == AI) {
			
				//TODO launch second thread
			
			MachineThread secondThread = new MachineThread();
			secondThread.setGameEventsListener(this.gameEventsListener);
			secondThread.setGameLogic(this.gameLogic);
			
			ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
			threadExecutor.execute(secondThread);	
			
		}
		firstPlay=false;

	}

	/**
	 * Gets the current allowed positions for current Player
	 */
	@Override
	public int[][] getAllowedPositionsForPlayer() {
		return this.gameLogic.getAllowedPositionsForPlayer(this
				.getCurrentPlayer());
	}

	/**
	 * Gets the current matrix of the game
	 */
    //********************this is a HIGH priority task*****************************
    //TODO Undo:Redo 
	//This getGameMatrix returns the current game board.  Perhaps the undo and
	//redo can work with a tack of these.
    //********************this is a HIGH priority task*****************************
	@Override
	public int[][] getGameMatrix() {
		return this.gameLogic.getGameMatrix();
	}

	/**
	 * Sets the event listener (is going to be notified when the game situation
	 * changes)
	 */
	@Override
	public void setGameEventsListener(GameEventsListener listener) {
		this.gameEventsListener = listener;
	}

	// /////////////////////// PRIVATE METHODS ///////////////////////

	/**
	 * makes a movement, conquer positions, update possible positions, toggles
	 * player
	 * 
	 * @param player
	 * @param col
	 * @param row
	 * @return if the turn has to change
	 */
	private boolean doMovement(int player, int col, int row) {

		boolean changePlayer = false;
		if (this.gameLogic.canSet(player, col, row)) {
			this.gameLogic.setStone(player, col, row);
			this.gameLogic.conquerPosition(player, col, row);
			changePlayer = this.togglePlayer();
		}
		this.gameLogic.movementDone();
		this.notifyChanges();
		return changePlayer;
	}

	public void movementDone(){
		this.gameLogic.movementDone();
	}
	
	/**
	 * Notifies the listener for the changes occured in the game
	 */
	private void notifyChanges() {
		if (this.gameEventsListener != null) {
			int p1 = this.gameLogic.getCounterForPlayer(this.gameLogic.getPlayerOne());
			int p2 = this.gameLogic.getCounterForPlayer(this.gameLogic.getPlayerTwo());

			this.gameEventsListener.onScoreChanged(p1, p2);

			if (this.gameLogic.isFinished()) {
				int winner = NONE;
				if (p1 > p2) {
					winner = this.gameLogic.getPlayerOne();
				} else if (p2 > p1) {
					winner = this.gameLogic.getPlayerTwo();
				}

				this.gameEventsListener.onGameFinished(winner);
			}

		}
	}

	/**
	 * Changes the player
	 * 
	 * @return if the player has been toggled (if the opponent player can play)
	 */
	private boolean togglePlayer() {

		int current = this.gameLogic.getCurrentPlayer();
		boolean toggled;
		// if the next player can play (has at least one place to put the
		// chip)
		if (!this.gameLogic.isBlockedPlayer(GameUtils.opponent(current))) {
			// just switches to the next player
			this.gameLogic.setCurrentPlayer(GameUtils.opponent(current));
			toggled = true;
		} else {//if the next player can't play
			System.out.println(String.format(
					"player %d cannot play!!!!!!!!!!!!!!!!!!!", GameUtils
							.opponent(current)));
			toggled = false;
		}
		return toggled;
	}
	

	// /////////////////////// ACCESSORS //////////////////////////

	/**
	 * @param gameLogic
	 *            the gameLogic to set
	 */
	public void setGameLogic(GameLogic gameLogic) {
		this.gameLogic = gameLogic;
	}

	/**
	 * @return the gameLogic
	 */
	public GameLogic getGameLogic() {
		return gameLogic;
	}

	/**
	 * Gets the player that has to play
	 */
	@Override
	public int getCurrentPlayer() {
		return this.gameLogic.getCurrentPlayer();
	}

	/**
	 * Gets the score for the given player
	 */
	@Override
	public int getScoreForPlayer(int player) {
		return this.gameLogic.getCounterForPlayer(player);
	}

	/**
	 * If true is playing against droid
	 */
	@Override
	public void setMachineOpponent(boolean machineOpponent) {
		this.isMachineOpponent = machineOpponent;
	}

	/**
	 * gets if the opponent is droid
	 */
	@Override
	public boolean getMachineOpponent() {
		return this.isMachineOpponent;
	}
    //********************this is a HIGH priority task*****************************
    //TODO we might need a setter and getter for difficultyLevel
	//I thought that maybe we needed a setter and getter in here, but I'm not even
    //sure if these are necessary or correct.
	//Since GameFacadeImpl.java implements GameFacade.java I had to declare the
	//below functions in GameFacade.java
    //********************this is a HIGH priority task*****************************
	/**
	 * sets the difficulty level in the gameLogic
	 */
	
	public void setStartTime(){
		
		startTime = System.currentTimeMillis();
	}

	public long getStartTime(){
		return startTime;
	}
	public void setStopTime(){
		stopTime = System.currentTimeMillis();
	}
	public long getStopTime(){
		return stopTime;
	}
	
	public void setGameTime(){
		gameTime = getStopTime()-getStartTime();
	}
	
	public long getGameTime(){
	    return gameTime;
	}

	public int getWinningDifferential() {
		return winningDifferential;
	}

	public void setWinningDifferential(int winningDifferential) {
		this.winningDifferential = winningDifferential;
	}
	
	public void setDifficulty(String difficulty){
		this.gameLogic.setDifficulty(difficulty);
	}

	@Override
	public void setPlayerColor(String playerColorString) {
		this.gameLogic.setPlayerColor(playerColorString);
	}
	
	

}