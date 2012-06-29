package team2.reversi;

//import team2.reversi.Direction;
//import team2.reversi.GameLogic;

public class GameLogicImpl implements GameLogic {

	// /////////////////////// PRIVATE FIELDS //////////////////////////

	/**
	 * The matrix
	 */
	private Board board;
	
	
	/**
	 * Just a helper.
	 */
	private MatrixChecker matrixChecker;

	/**
	 * The player that has to play
	 */
	private int currentPlayer = GameLogic.PLAYER_ONE;

	// /////////////////////// LIFETIME ////////////////////////////////
	/**
	 * initializes the matrix
	 */
	public GameLogicImpl(Board board) {
		this.board = board;
		this.matrixChecker = new MatrixChecker(board);
		
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

	/**
	 * Informs if the game is finished
	 * @return
	 */
	public boolean isFinished() {
		
		return (isBlockedPlayer(PLAYER_ONE) && isBlockedPlayer(PLAYER_TWO));
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
		this.currentPlayer = PLAYER_ONE;
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

	

	

	
	

	
	

}