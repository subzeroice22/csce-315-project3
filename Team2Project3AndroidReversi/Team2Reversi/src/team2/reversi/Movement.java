package team2.reversi;

/**
 * This class represents a movement (which is column, row and player)
 * @author Fernando Cherchi
 *
 */
public class Movement {
	
	/**
	 * The column of the movement
	 */
	private int column;
	/**
	 * The row of the movement
	 */
	private int row;
	/**
	 * The player of the movement
	 */
	private int player;
	
	/**
	 * Constructs the movement
	 * @param column
	 * @param row
	 * @param player
	 */
	public Movement(int column, int row, int player) {
		this.column = column;
		this.row = row;
		this.player = player;
	}
	
	/**
	 * Just returns a string representation of the movement 
	 */
	public String toString() {
		return String.format("(%d, %d) P%d", column, row, player); 
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}
}