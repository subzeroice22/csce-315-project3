package team2.reversi;

import android.content.Context;
//import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Settings manager
 * 
 * @author Fernando Cherchi
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
	 * This key is used to know if is selected 1 or 2 players
	 */
//	private static final String DIFFICULTY = "levels";

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

	
/*	public enum Difficulty {
	        Easy(5),
	        Medium(3),
	        Hard(1);
	        
	        private final int depth;
	        
	        Difficulty(int maxdepth){
	        	depth=maxdepth;
	        }
	        public int getDepth(){
	        	return depth;
	        }
	}*/
	

    
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
/*    public static Difficulty getDifficulty(Context context) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        final String diffic = preferences
                .getString(DIFFICULTY, DEFAULT_DIFFICULTY.toString());
        return Difficulty.valueOf(diffic);
    }
    
    public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.Medium;*/

}
