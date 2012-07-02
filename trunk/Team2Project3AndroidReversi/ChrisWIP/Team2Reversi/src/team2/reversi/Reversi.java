package team2.reversi;

//import team2.reversi.GameEventsListener;
//import GameFacade;
//import GameLogic;
//import Board;
//import GameFacadeImpl;
//import GameLogicImpl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuInflater;
import android.widget.TextView;

public class Reversi extends Activity implements GameEventsListener,
		OnClickListener {
	// ///////////////////// PRIVATE FIELDS //////////////////////////////
	/**
	 * The game board
	 */
	private GameFacade gameFacade = null;
	
	/**
	 * Used to invoke the GUI operations (UI thread)
	 */
	private Handler handler;

	// ///////////////////////// LIFETIME /////////////////////////////////
	
	/**
	 * Constructor
	 */
	public Reversi() {
		this.handler = new Handler();
	}

	// ///////////////////////// EVENTS ///////////////////////////////////

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.setTitle("Team 2 Reversi");
		this.setContentView(R.layout.main);

		// retrieving the old facade if any
		// trying to recover the last version
		this.gameFacade = (GameFacade) this.getLastNonConfigurationInstance();
		// if is the first time...
		if (gameFacade == null) {
			this.gameFacade = new GameFacadeImpl();
			this.gameFacade.setMachineOpponent(Settings.getIsDroidOpponent(getBaseContext()));
			this.gameFacade.setGameLogic(new GameLogicImpl(new Board()));

		} else {
			this.refreshCounters();
		}
		// caution. "this" has been re-constructed after an orientation
		// change... so need to
		// subscribe as listener again
		this.gameFacade.setGameEventsListener(this);

		GameBoard gameBoard = (GameBoard) this.findViewById(R.id.gameBoard);
		gameBoard.setGameFacade(this.gameFacade);

	}

	/**
	 * Occurs when the user presses the menu key
	 */
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = super.getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * Occurs when the user clicks in a menu option
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			super.startActivity(new Intent(this, Settings.class));
			break;
		case R.id.restart:
			this.showNewGameConfirmation(super.getResources().getString(
					R.string.new_game_msg));
			break;
		case R.id.exit:
			finish();
		case R.id.undo:
			this.gameFacade.undo();
			this.gameFacade.notifyChanges();
		    //********************this is a HIGH priority task*****************************
		    //TODO Implement an undo function.
			//We need to set the gameBoard one level back if playing against another human (no AI)
		    //or set the gameBoard back two versions if playing against the AI as the AI will have
		    //already made another board...
		    //********************this is a HIGH priority task*****************************
			break;
		case R.id.redo:
			this.gameFacade.redo();
			this.gameFacade.notifyChanges();
		    //********************this is a HIGH priority task*****************************
		    //TODO Implement a redo function.
			//We need to set the gameBoard forward one level, reseting to the gameBoard before
		    //the undo.  If playing against the AI we need to set the gameBoard forward the two
		    //levels we had undone.
		    //********************this is a HIGH priority task*****************************
			break;
		case R.id.stats:
		    //********************this is a HIGH priority task*****************************
		    //TODO Implement a statsHander class.
			//We need to read the stats.txt file from the SD Card and display the high
		    //scores for each difficulty of play.  The stats for each record setting game should
		    //include {Difficulty, Winning Differential, Length of Game, Date/Time}
			//See also: onGameFinished() for notes on writing high scores to SD Card
		    //********************this is a HIGH priority task*****************************
			break;
		default:
			return false;
		}
		return true;
	}

	/**
	 * Occurs when the user closes a dialog
	 * 
	 * @param dialog
	 * @param which
	 */
	public void onClick(DialogInterface dialog, int which) {
		if (which == AlertDialog.BUTTON_POSITIVE) {
			restart();
		}
	}

	/**
	 * Provides the data to remember (if the screen changes orientation, the
	 * full class is rebuilt)
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {

		return this.gameFacade;
	}

	/**
	 * Occurs when the score has changed... so refreshing counters
	 */
	@Override
	public void onScoreChanged(int p1Score, int p2Score) {
		GuiUpdater updater = new GuiUpdater(p1Score, p2Score, this);
		this.handler.post(updater);
	}
	
	/**
	 * Occurs when the game has finished
	 */
	@Override
	public void onGameFinished(int winner) {
		
		String playerName;
		if (winner == GameLogic.PLAYER_ONE) {
			playerName = getResources().getString(R.string.p1);
		} else {
			playerName = getResources().getString(R.string.p2);
		}
		
		this.handler.post(new MessageBoxShower(String.format(super.getResources()
				.getString(R.string.game_finished, playerName)), this, this));
	    //********************this is a HIGH priority task*****************************
	    //TODO We need to read the high scores and compare the scores at this game's Difficulty
	    //to see if a new record has been made.  If so, we need to replace the old record
	    //with the new one in the stats.txt file.  My idea is to have six High Scores in the
		//stats.txt, a Piece Differential Champ, and a Speed Champ for each level of 
		//difficulty.
	    //********************this is a HIGH priority task*****************************
	}

	
	// ///////////////////////// PRIVATE METHODS ///////////////////////////////

	/**
	 * Restarts the facade and the graphics
	 */
	private void restart() {
		this.gameFacade.restart();
		this.gameFacade.setMachineOpponent(Settings.getIsDroidOpponent(getBaseContext()));
//		this.gameFacade.setDifficulty(Settings.getDifficulty(getBaseContext()));
		this.refreshCounters();
		GameBoard gameBoard = (GameBoard) this.findViewById(R.id.gameBoard);
		gameBoard.invalidate();
	}

	/**
	 * Refreshes the counters after an orientation changing
	 */
	private void refreshCounters() {

		int p1 = this.gameFacade.getScoreForPlayer(GameFacade.PLAYER_ONE);
		int p2 = this.gameFacade.getScoreForPlayer(GameFacade.PLAYER_TWO);
		this.setPlayersCounters(p1, p2);
	}

	/**
	 * Draws the scores
	 */
	private void setPlayersCounters(int p1Score, int p2Score) {

		TextView txtP1 = (TextView) this.findViewById(R.id.txtPlayer1Counter);
		txtP1.setText(String.format(" %d", p1Score));
		TextView txtP2 = (TextView) this.findViewById(R.id.txtPlayer2Counter);
		txtP2.setText(String.format(" %d", p2Score));
		
	}
	

	/**
	 * shows the dialog for confirmation of the restart
	 */
	private void showNewGameConfirmation(String message) {

		ConfirmationDialog cd = new ConfirmationDialog(this);
		cd.showConfirmation(this, message);
	}

}