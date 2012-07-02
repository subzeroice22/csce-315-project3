package team2.reversi;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;

/**
 * Updates the Gui. It is a runnable to be able to set it from another thread
 * Based upon Android Open Source code from Fernando Cherchi
 * Team2 Coders: Laramie Goode, Curtis Franks, Chris Voss
 *
 */
public class GuiUpdater implements Runnable{
	private int score1;
	private int score2;
	private Activity view;

	/**
	 * Constructs the updater
	 * @param difficulty
	 * @param score1
	 * @param score2
	 * @param view
	 */
	public GuiUpdater (int score1, int score2, Activity view) {
		this.score1 = score1;
		this.score2 = score2;
		this.view = view;
	}
	
	/**
	 * The run method
	 */
	@Override
	public void run() {
		this.setPlayersCounters(score1, score2);
		displayDifficulty();
		GameBoard board = (GameBoard) this.view.findViewById(R.id.gameBoard);
		board.drawPositions();
		board.invalidate();
		displayDifficulty();//a second time just to make sure.
		
	}

	/**
	 * Displays the difficulty level
	 */
	private void displayDifficulty() {

	    //********************this is a lower priority task*****************************
	    //TODO Display Difficulty Level on main screen.
	    //This is working somewhat.  I haven't had time to test it much, but I wanted
		//to have it so that the user couldn't change difficulty during the game.  If the 
		//user selected a difficulty before any moves were made, then I wanted the change
		//to implement.  I'm not sure if it is working perfectly.
	    //********************this is a lower priority task*****************************
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(view);
		TextView txtP2 = (TextView) this.view.findViewById(R.id.difficulty_level);
		txtP2.setText(String.format(" %s", prefs.getString("difficulty_level","NA")));
		
	}

	/**
	 * Draws the scores
	 */
	private void setPlayersCounters(int p1Score, int p2Score) {

		TextView txtP1 = (TextView) this.view.findViewById(R.id.txtPlayer1Counter);
		txtP1.setText(String.format(" %d", p1Score));
		TextView txtP2 = (TextView) this.view.findViewById(R.id.txtPlayer2Counter);
		txtP2.setText(String.format(" %d", p2Score));
		
	}
}
