public class Pair {

	private	int _x;
	private int _y;

	
	public Pair(int x, int y) {
		_x = x;
		_y = y;	
	}
	
	public void Set(int x, int y) {
		_x = x;
		_y = y;
	}
	
	public int GetX() {
		return _x;
	}
	
	public int GetY() {
		return _y;
	}
	
	public boolean isEqual(Pair input) {
		if(input.GetX() == _x && input.GetY() == _y) {
			return true;
		} else {
			return false;
		}
	}

}
