package team2.reversi;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Settings manager
 * Based upon Android Open Source code from Fernando Cherchi
 * Team2 Coders: Laramie Goode, Curtis Franks, Chris Voss
 *
 */
public class Settings extends PreferenceActivity {

	// ///////////////////// CONSTANTS ///////////////////////////////////////
	/**
	 * The key for the show allowed position value
	 */
	private static final String SHOW_ALLOWED_POS = "show_allowed_positions";

	/**
	 * This key is used to know if is selected 1 or 2 players
	 */
	private static final String IS_DROID_OPPONENT = "is_droid_opponent";

	/**
	 * This key is used to know the difficulty of the AI
	 */
	
	private static final String DIFFICULTY_LEVEL = "difficulty_level";
	
	// ///////////////////// OVERRIDES //////////////////////////////////////
	/**
	 * adding preferences from res
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.addPreferencesFromResource(R.xml.settings);
	}

	// ///////////////////// ACCESSORS ///////////////////////////////////////

	/**
	 * Get the 'show allowed positions' setting
	 */
	public static boolean getShowAllowedPositions(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(SHOW_ALLOWED_POS, true);
	}

	/**
	 * Gets the setting to know if the opponent is Android
	 * @param context
	 * @return
	 */
	public static boolean getIsDroidOpponent(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(IS_DROID_OPPONENT, true);
	}
	
    //********************this is a low priority task*****************************
    //TODO we might need setter and getters for difficultyLevel
	//I'm not sure how this is supposed to work, but I also have a displayDifficulty()
	//in GuiUpdater.java which is attempting to get the Difficulty and set the 
	//@string/difficulty_level all in the same function.
    //I think the one in GuiUpdater.java is closer to correct.
    //********************this is a low priority task*****************************
	public static String getDifficulty(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(DIFFICULTY_LEVEL,"0");
	}
	

}
