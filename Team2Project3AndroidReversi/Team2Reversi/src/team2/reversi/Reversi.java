package team2.reversi;

import team2.reversi.GameEventsListener;
import team2.reversi.GameLogicImpl;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import team2.reversi.Settings;
import team2.reversi.Statistics;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MenuInflater;
import android.widget.TextView;


public class Reversi extends Activity implements GameEventsListener,
		OnClickListener {
	// ///////////////////// PRIVATE FIELDS //////////////////////////////
	/**
	 * The game board
	 */
	private GameFacade gameFacade = null;
	
	private long startTime=0;
	
	/**
	 * Used to invoke the GUI operations (UI thread)
	 */
	private Handler handler;
	
	private List<Statistics> highScores = new ArrayList<Statistics>();

	// ///////////////////////// LIFETIME /////////////////////////////////
	/**
	 * Constructor
	 */
	public Reversi() {
		this.handler = new Handler();
	}
	
	String difficultyString = "NA";
	String playerColorString = "Black (goes second)";
	// ///////////////////////// EVENTS ///////////////////////////////////

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
//VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV CREATION BLOCK VVVVVVVVVVVVVVVVVVVVVVVVVVVVV///
		super.startActivity(new Intent(this, Settings.class));


		this.setTitle("Team 2 Reversi");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Reversi.this);
		difficultyString = prefs.getString("difficulty_level","NA");
		playerColorString = prefs.getString("player_color", "Black (goes first)");	
		this.setContentView(R.layout.main);
		// retrieving the old facade if any
		// trying to recover the last version
		this.gameFacade = (GameFacade) this.getLastNonConfigurationInstance();
		// if is the first time...
		if (gameFacade == null) {
			this.gameFacade = new GameFacadeImpl();
			//this.gameFacade.setPlayerColor(playerColorString);
			this.gameFacade.setGameLogic(new GameLogicImpl(new Board()));
		
			this.gameFacade.setMachineOpponent(Settings.getIsDroidOpponent(getBaseContext()));
			this.gameFacade.setDifficulty(difficultyString);
			//this.gameFacade.setPlayerColor(playerColorString);
		} else {
			this.refreshCounters();
		}
		// caution. "this" has been re-constructed after an orientation
		// change... so need to
		// subscribe as listener again

		this.gameFacade.setGameEventsListener(this);

		GameBoard gameBoard = (GameBoard) this.findViewById(R.id.gameBoard);
		gameBoard.setGameFacade(this.gameFacade);
		startTime = System.currentTimeMillis();
		displayDifficulty();

	}
	

	
	private void getPrefs(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Reversi.this);
		difficultyString = prefs.getString("difficulty_level","NA");
		playerColorString = prefs.getString("player_color", "Black (goes first)");
	}
	

//VVVVVVVVVVVVVVVVVVVVVVVVVVV BEGIN MENU BLOCK VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV///
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
			GameBoard gameBoard = (GameBoard) this.findViewById(R.id.gameBoard);
			gameBoard.invalidate();
			break;
		case R.id.redo:
			this.gameFacade.redo();
			GameBoard gBoard = (GameBoard) this.findViewById(R.id.gameBoard);
			gBoard.invalidate();
			break;
		case R.id.stats:
		    //********************this is a HIGH priority task*****************************
		    //TODO Implement a statsHander class.
			//We need to read the stats.txt file from the SD Card and display the high
		    //scores for each difficulty of play.  The stats for each record setting game should
		    //include {Difficulty, Winning Differential, Length of Game, Date/Time}
			//See also: onGameFinished() for notes on writing high scores to SD Card
		    //********************this is a HIGH priority task*****************************
			this.showHighScores();
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
	
//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^END MENU BLOCK ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^///
	
////VVVVVVVVVVVVVVVVVVVVVVVVVVVVVV GUI UPDATE BLOCK VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV///
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
		int p1 = this.gameFacade.getScoreForPlayer(GameLogicImpl.player_one);
		int p2 = this.gameFacade.getScoreForPlayer(GameLogicImpl.player_two);
		
		if (winner == GameLogicImpl.player_one) {
			playerName = getResources().getString(R.string.p1);
			this.gameFacade.setWinningDifferential(p1-p2);
		} else {
			playerName = getResources().getString(R.string.p2);
			this.gameFacade.setWinningDifferential(p2-p1);
		}

		this.handler.post(new MessageBoxShower(String.format(super.getResources()
				.getString(R.string.game_finished, playerName)), this, this));
		writeScores();
	    //********************this is a HIGH priority task*****************************
	    //TODO We need to read the high scores and compare the scores at this game's Difficulty
	    //to see if a new record has been made.  If so, we need to replace the old record
	    //with the new one in the stats.txt file.  My idea is to have six High Scores in the
		//stats.txt, a Piece Differential Champ, and a Speed Champ for each level of 
		//difficulty.
	    //********************this is a HIGH priority task*****************************
	}
	/********************************************************************
	 * TODO: There is a method in Settings.java to getDifficulty(), but I haven't 
	 * had time to test it and for now, I found it easier to get the difficulty with the 
	 * method above, here in Reversi.java.  There's also a displayDifficulty in GuiUpdater.java
	 * that uses this same code from within that class.  
	 * The below code was an attempt to alter the depth of the AI based on the 
	 * difficulty level.  It didn't work at all.
	 */
	private void displayDifficulty() {
		TextView txtP2 = (TextView) this.findViewById(R.id.difficulty_level);
		txtP2.setText(String.format(" %s", difficultyString));
	}
////////////////////////////////BEGIN PRIVATE METHODS ///////////////////////////////

	/**
	 * Restarts the facade and the graphics
	 */
	private void restart() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Reversi.this);
		difficultyString = prefs.getString("difficulty_level","NA");
		playerColorString = prefs.getString("player_color", "Black (goes first)");
		startTime = System.currentTimeMillis();
		this.gameFacade.restart();
		this.gameFacade.setMachineOpponent(Settings.getIsDroidOpponent(getBaseContext()));
		this.gameFacade.setDifficulty(difficultyString);
		this.refreshCounters();
		GameBoard gameBoard = (GameBoard) this.findViewById(R.id.gameBoard);
		gameBoard.invalidate();
		displayDifficulty();

	}

	/**
	 * Refreshes the counters after an orientation changing
	 */
	private void refreshCounters() {

		int p1 = this.gameFacade.getScoreForPlayer(this.gameFacade.getPlayerOne());
		int p2 = this.gameFacade.getScoreForPlayer(this.gameFacade.getPlayerOne());
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
	private void showHighScores() {
		Date gameTime = new Date(System.currentTimeMillis()-startTime);
		SimpleDateFormat speedFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
		SimpleDateFormat gameDateFormat = new SimpleDateFormat("yyyy-MM-dd ", Locale.US);	
		TextView myView = new TextView(getApplicationContext());
		myView.setText(	"DIFFICULTY\tSCORE DIFFERENCE\tSPEED\t\t\tDATE\n"+
				difficultyString + "\t\t\t" +this.gameFacade.getWinningDifferential()+"\t\t\t\t"+ 
				speedFormat.format(gameTime)+"\t"+gameDateFormat.format(System.currentTimeMillis()));
		myView.setTextSize(10);
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle("High Scores");
		helpBuilder.setView(myView);
		helpBuilder.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
		
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing but close the dialog
					}
		 		});
		
		  // Remember, create doesn't show the dialog
		  AlertDialog helpDialog = helpBuilder.create();
		  helpDialog.show();
		  }
//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ END GUI UPDATE BLOCK ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^//////	
	
	
///VVVVVVVVVVVVVVVVVVVVVVVVVVVVV HIGH SCORE METHODS  VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV//
	public List<Statistics> getHighScores() {
		return highScores;
	}

	public void setHighScores(List<Statistics> highScores) {
		this.highScores = highScores;
	}
	public void writeScores() {
		try {
//			File scorefile = new File("reversiScores.txt");
			long gameTime = System.currentTimeMillis()-startTime;
			FileWriter write = new FileWriter("reversiScores.txt", true);
			PrintWriter text = new PrintWriter(write);
			text.println( 	"difficulty NA"+ " " + 
							this.gameFacade.getWinningDifferential()+" "+
							gameTime+" "+
							System.currentTimeMillis());
			text.flush();
			write.close();

		} catch (IOException ioe)
		// writes name from textfield(username) and score from board class
		// into text document text.txt
		{
			ioe.printStackTrace();
		}
	}
///////////////////////////////END HIGH SCORE METHODS///////////////////////////////////
}