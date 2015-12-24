package org.xdevs23.ui.view.listview;

import android.content.Context;
import android.widget.ArrayAdapter;

import io.github.UltimateBrowserProject.R;

/**
 * Custom ListView
 * 
 *
 */
public class XDListView {
	
	public static ArrayAdapter<String> create(Context context, String[] content) {
		int resource = R.layout.listview_activity;
		
		return new ArrayAdapter<String>(context, resource, content);
	}
	
}
