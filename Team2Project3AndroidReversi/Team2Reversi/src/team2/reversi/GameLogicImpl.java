package team2.reversi;

//import team2.reversi.Direction;
//import team2.reversi.GameLogic;
import java.util.Stack;

public class GameLogicImpl implements GameLogic {

	// /////////////////////// PRIVATE FIELDS //////////////////////////
	/***
	 * Board dimensions
	 */
	public static int COLS = 8;

	public static int ROWS = 8;
	
	public static int EMPTY = 0;
	
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


	/**
	 * players
	 */
	public static int player_one = 1;//normally human
	public static int player_two = 2;//normally AI

	public int getPlayerOne(){
		
		return player_one;
	}

	public int getPlayerTwo() {
		return player_two;
	}
	
	/**
	 * The matrix
	 */
	private Board board;

	Stack<Board> boardHistory;
	Stack<Board> redoStack;
	
	/**
	 * Just a helper.
	 */
	private MatrixChecker matrixChecker;

	/**
	 * The player that has to play
	 */
	private int currentPlayer = player_one;

	// /////////////////////// LIFETIME ////////////////////////////////
	/**
	 * initializes the matrix
	 */
	public GameLogicImpl(Board board) {
		this.board = board;
		this.matrixChecker = new MatrixChecker(board);
		this.boardHistory= new Stack<Board>();
		this.redoStack = new Stack<Board>();
	}

	// /////////////////////// PUBLIC METHODS //////////////////////////

	/**
	 * Returns the game status
	 */
	@Override
	public int[][] getGameMatrix() {

		return this.board.getMatrix();
	}

	@Override
	public boolean canSet(int player, int col, int row) {
		return this.matrixChecker.canSet(player, col, row);
	}
	

	/**
	 * Calculates if the given player is blocked
	 * @param player
	 * @return
	 */
	@Override
	public boolean isBlockedPlayer(int player) {
		
		int allowed[][] = this.getAllowedPositionsForPlayer(player);
		int allowCount = 0;
		for (int col = 0; col < COLS; col++) {
			for (int row = 0; row < ROWS; row++) {
				if (allowed[col][row] == player) {
					allowCount ++;
				}
			}
		}
		return allowCount == 0;
	}
	
	/****************************************************
	 * TODO: this was an attempt to allow the human to chose
	 * white or black.  Didn't have time to see it through.
	 * Something broke after I tried to implement and I had to
	 * backtrack a bunch and comment much out.
	 * @param color
	 */
/*	public static int setAIColor(String color, String player){
		if(color.equals("White (goes second)")&&player.equals("player_one")){
			return 2;
		}else if(color.equals("White (goes second)")&&player.equals("player_two")){
			return 1;
		}else if(color.equals("Black (goes first)")&&player.equals("player_one")){
			return 1;
		}else if(color.equals("Black (goes first)")&&player.equals("player_two")){
			return 2;
		}
	}*/////changed the setter below to handle this
	/**
	 * Informs if the game is finished
	 * @return
	 */
	public boolean isFinished() {
		
		return (isBlockedPlayer(player_one) && isBlockedPlayer(player_two));
	}

	/**
	 * Sets a chip in a given place
	 */
	@Override
	public void setStone(int player, int col, int row) {

		if (col < COLS && row < ROWS) {
			this.board.setStone(col, row, player);
		}
	}

	/**
	 * Returns all the allowed positions for a player
	 */
	@Override
	public int[][] getAllowedPositionsForPlayer(int player) {

		int[][] allowedPositions = new int[COLS][ROWS];

		// scanning the grid
		for (int col = 0; col < COLS; col++) {
			for (int row = 0; row < ROWS; row++) {
				// if player can set
				if (this.matrixChecker.canSet(player, col, row)) {
					allowedPositions[col][row] = player;
				}
			}
		}
		return allowedPositions;
	}

	/**
	 * Gets the current mobility (possible options to play) for the given player
	 */
	@Override
	public int getMobilityForPlayer(int player) {
		
		int mobility = 0;
		
		// scanning the grid
		for (int col = 0; col < COLS; col++) {
			for (int row = 0; row < ROWS; row++) {
				// if player can set
				if (this.matrixChecker.canSet(player, col, row)) {
					mobility ++;
				}
			}
		}
		return mobility;
	}
	
	public void movementDone(){
		redoStack.clear();
		boardHistory.push(this.board.clone());
	}
	
	public void undo(){
		if(boardHistory.size()<3){return;}
		redoStack.push(boardHistory.pop());
		redoStack.push(boardHistory.pop());
		this.board = boardHistory.peek().clone();
		matrixChecker.setMatrix(this.board.getMatrix());
	}
	public void redo(){
		if(redoStack.size()<2){return;}
		boardHistory.push(redoStack.pop());
		boardHistory.push(redoStack.pop());
		this.board = boardHistory.peek().clone();
		matrixChecker.setMatrix(this.board.getMatrix());
	}
	
	
	/**
	 * Conquers the positions in all directions (if the player is enclosing opponent pieces, 
	 * just converting them to player color) 
	 */
	@Override
	public void conquerPosition(int player, int column, int row) {
		
		//in each direction, if we are enclosing opponent chips, conquering...
		if (this.matrixChecker.isEnclosingUpwards(player, column, row)) {
			this.conquer(player, column, row, Direction.X.NONE, Direction.Y.UP);
		}
		if (this.matrixChecker.isEnclosingDownwards(player, column, row)) {
			this.conquer(player, column, row, Direction.X.NONE, Direction.Y.DOWN);
		}
		if (this.matrixChecker.isEnclosingRight(player, column, row)) {
			this.conquer(player, column, row, Direction.X.RIGHT, Direction.Y.NONE);
		}
		if (this.matrixChecker.isEnclosingLeft(player, column, row)) {
			this.conquer(player, column, row, Direction.X.LEFT, Direction.Y.NONE);
		}
		if (this.matrixChecker.isEnclosingLeftAndDown(player, column, row)) {
			this.conquer(player, column, row, Direction.X.LEFT, Direction.Y.DOWN);
		}
		if (this.matrixChecker.isEnclosingLeftAndUp(player, column, row)) {
			this.conquer(player, column, row, Direction.X.LEFT, Direction.Y.UP);
		}
		if (this.matrixChecker.isEnclosingRightAndDown(player, column, row)) {
			this.conquer(player, column, row, Direction.X.RIGHT, Direction.Y.DOWN);
		}
		if (this.matrixChecker.isEnclosingRightAndUp(player, column, row)) {
			this.conquer(player, column, row, Direction.X.RIGHT, Direction.Y.UP);
		}

}
	
	/**
	 * Gets the number of stones for a given player
	 * @return 
	 */
	@Override
	public int getCounterForPlayer(int player) {
		
		return this.board.getCounterForPlayer(player);
		
	}
	
	@Override
	public void initialize() {
		this.currentPlayer = player_one;
		this.board = new Board();
		this.matrixChecker = new MatrixChecker(this.board);
	}

	// /////////////////////// PRIVATE METHODS //////////////////////////

	/**
	 * Changes all the opponent chips to the player color in the given direction
	 * @param player
	 * the current player
	 * @param column
	 * the column where the player sets the chip (starting point of the conquer)
	 * @param row
	 * the row where the player sets the chip 
	 * @param xDirection 
	 * the direction in the x axis (left or right or none )
	 * @param yDirection
	 * the direction in the y axis (up or down or none) 
	 */
	private void conquer (int player, int column, int row, int xDirection, int yDirection) {
		
		int x = column;
		int y = row;
		boolean ownChip = false;
		
		int[][] gameMatrix = this.board.getMatrix();
		
		//conquer until an own chip is found
		while ( !ownChip ) {
			//advancing in the given direction
			x += xDirection;
			y += yDirection;
			
			//if is not an own chip
			if (gameMatrix[x][y] != player) {
				this.setStone(player, x, y);
			} else {
				ownChip = true;
			}
		}
	}
	

	// /////////////////////// ACCESSORS ////////////////////////////
	
	/**
	 * Gets the current player
	 * @return the currentPlayer
	 */
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Sets the current player
	 */
	@Override
	public void setCurrentPlayer(int player) {
		this.currentPlayer = player;
	}

	/**
	 * Returns the board
	 * @return
	 */
	public Board getBoard() {
		return this.board;
	}
	
	private String Difficulty;

	private static String playerColorChoice;
	
	public void setDifficulty(String difficulty){
		this.Difficulty = difficulty;
	}
	public String getDifficulty(){
		return this.Difficulty;
	}
	public void setPlayerColor(String playerColorString){
		if(playerColorString.equals("White (goes second)")){
			player_one=2;
			player_two=1;
		}else if(playerColorString.equals("Black (goes first)")){
			player_one = 1;
			player_two = 2;
			
		}else{
			player_one = 1;
			player_two = 2;
		}
	}
	public String getPlayerColor(){
		return GameLogicImpl.playerColorChoice;
	}



}