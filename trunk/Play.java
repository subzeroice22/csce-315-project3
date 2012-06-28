package csce315.game.reversi;

public class Play {

	Pair action;
	int UtilityValue;
	
	public Play(Pair act, int Util) {
		action = act;
		UtilityValue = Util;
	}
	
	public Pair first() {
		return action;
	}
	
	public int second() {
		return UtilityValue;
	}
	
	public void setFirst(Pair input) {
		action = input;
	}
	
	public void setSecond(int val) {
		UtilityValue = val;
	}
	
}
