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


	// /////////////////////// PUBLIC METHODS //////////////////////////

	/**
	 * Starts a new game
	 */
	@Override
	public void restart() {

		this.gameLogic.initialize();
	}
	
	public void undo(){
		this.gameLogic.undo();
	}
	
	public void redo(){
		this.gameLogic.redo();
	}
	

	/**
	 * Sets the chip for the given player in a given position
	 */
	@Override
	public void set(int player, int col, int row) {

		boolean playerHasChanged;
		//if the next player has no move then doMove will perform the move
		//and then return false so that playerhasChanged becomes false.
		//otherwise, playerHasChanged will become true.
		playerHasChanged = this.doMovement(player, col, row);
		// if is the machine moment...
		if (this.isMachineOpponent && playerHasChanged && 
				this.gameLogic.getCurrentPlayer() == GameLogic.PLAYER_TWO) {
			
				//TODO launch second thread
			
			MachineThread secondThread = new MachineThread();
			secondThread.setGameEventsListener(this.gameEventsListener);
			secondThread.setGameLogic(this.gameLogic);
			
			ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
			threadExecutor.execute(secondThread);	
			
		}

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
		this.notifyChanges();
		return changePlayer;
	}

	/**
	 * Notifies the listener for the changes occured in the game
	 */
	public void notifyChanges() {
		if (this.gameEventsListener != null) {
			int p1 = this.gameLogic.getCounterForPlayer(PLAYER_ONE);
			int p2 = this.gameLogic.getCounterForPlayer(PLAYER_TWO);
			String difficulty = this.getDifficulty();
			this.gameEventsListener.onScoreChanged(p1, p2);

			if (this.gameLogic.isFinished()) {
				int winner = NONE;
				if (p1 > p2) {
					winner = GameLogic.PLAYER_ONE;
				} else if (p2 > p1) {
					winner = GameLogic.PLAYER_TWO;
				}

				this.gameEventsListener.onGameFinished(winner);
			}

		}
	}

	private String getDifficulty() {
		// TODO Auto-generated method stub
		return null;
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
		//this.gameLogic.initialize();
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

	@Override
	public void setDifficulty(String difficulty) {
		// TODO Auto-generated method stub
		
	}


	
}