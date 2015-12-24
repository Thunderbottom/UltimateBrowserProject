package org.xdevs23.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;

import org.xdevs23.ui.dialog.templates.PositiveButtonOK;

public class MessageDialog {
	
	private static DialogInterface.OnClickListener defaultOnClickListener = new PositiveButtonOK();
	
	public static void showDialog(String title, String message, Context context, DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle   (title  )
						  .setMessage (message)
						  .setPositiveButton(PositiveButtonOK.buttonText, onClickListener);
		
		AppCompatDialog alertDialog = alertDialogBuilder.create();
		
		alertDialog.show();
	}
	
	public static void showDialog(String title, String message, Context context) {
		showDialog(title, message, context, defaultOnClickListener);
	}
	
}
