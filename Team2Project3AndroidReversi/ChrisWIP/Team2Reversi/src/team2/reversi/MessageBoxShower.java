package team2.reversi;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class MessageBoxShower implements Runnable {

	private String message;
	private OnClickListener listener;
	private Context context;

	public MessageBoxShower (String message, OnClickListener listener, Context context) {
		this.message = message;
		this.listener = listener;
		this.context = context;
	}
	
	/**
	 * Just for showing message box in a separate thread
	 */
	@Override
	public void run() {
		
		ConfirmationDialog cd = new ConfirmationDialog(listener);
		cd.showConfirmation(context, message);
	}


}
