package org.xdevs23.ui.dialog.templates;

import android.content.DialogInterface;

public class KillAppMessageDialogButton implements DialogInterface.OnClickListener {
	
	@Override
	public void onClick(DialogInterface dialog, int id) {
		dialog.dismiss();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

	public static final String     buttonText = "Exit";

}
