package io.github.UltimateBrowserProject.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import io.github.UltimateBrowserProject.Unit.RecordUnit;

public class RecordHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "UltimateBrowserProject3.db";
    private static final int DATABASE_VERSION = 1;

    public RecordHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // You don't need to check if the tables already exist.
    // They are created if they don't exist, and if they already exist,
    // no action is taken.
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(RecordUnit.CREATE_BOOKMARKS);
        database.execSQL(RecordUnit.CREATE_HISTORY);
        database.execSQL(RecordUnit.CREATE_WHITELIST);
        database.execSQL(RecordUnit.CREATE_GRID);
    }

    // UPGRADE ATTENTION!!!
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // Usually this can be ignored, is just to see version changes
    }

    // UPGRADE ATTENTION!!!
    private boolean isTableExist(@NonNull String tableName) {
        return false;
    }

    // Do you mean that?
    /**
     * Check if a table exists
     * @param tableName The table to check
     * @param db The readable database which should be checked
     * @return <b>True</b> if the table is found, <br />
     *         <b>False</b> if not or an exception was thrown.
     */
    public boolean isTableExist(@NonNull String tableName, @NonNull SQLiteDatabase db) {
        boolean tableExist = false;     // Initial value
        try {
            Cursor c = db.rawQuery(
                    String.format(
                            "SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '%s'", tableName
                    ), null);       // Get table (if not available returns null or getCount is 0)
            if(c != null) { tableExist = (c.getCount() > 0); c.close(); }   // Check if table exists

        } catch(Exception ex) {
            /* tableExist stays false */
        }
        return tableExist;
    }

}
