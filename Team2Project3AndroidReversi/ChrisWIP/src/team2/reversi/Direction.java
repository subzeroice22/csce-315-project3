package team2.reversi;

/**
 * Just an enumeration to access the direction in a clear and readable mode
 * @author Fernando Cherchi
 *
 */
public class Direction {
	
	/**
	 * Directions in X axis
	 */
	public static class X {
		public static int LEFT = -1;
		public static int RIGHT = 1;
		public static int NONE = 0;
	}
	
	/**
	 * Directions in Y axis
	 */
	public static class Y {
		public static int UP = -1;
		public static int DOWN = 1;
		public static int NONE = 0;
	}

}
