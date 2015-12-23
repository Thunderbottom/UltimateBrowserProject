package io.github.UltimateBrowserProject.Unit;

import io.github.UltimateBrowserProject.Database.Record;

public class RecordUnit {

    public static final String

            TABLE_BOOKMARKS = "BOOKMARKS",
            TABLE_HISTORY   = "HISTORY",
            TABLE_WHITELIST = "WHITELIST",
            TABLE_GRID      = "GRID",

            COLUMN_TITLE    = "TITLE",
            COLUMN_URL      = "URL",
            COLUMN_TIME     = "TIME",
            COLUMN_DOMAIN   = "DOMAIN",
            COLUMN_FILENAME = "FILENAME",
            COLUMN_ORDINAL  = "ORDINAL",


            CREATE_HISTORY
                    = "CREATE TABLE "
                    + TABLE_HISTORY
                    + " ("
                    + " " + COLUMN_TITLE + " text,"
                    + " " + COLUMN_URL   + " text,"
                    + " " + COLUMN_TIME  + " integer"
                    + ")",

            CREATE_BOOKMARKS
                    = "CREATE TABLE "
                    + TABLE_BOOKMARKS
                    + " ("
                    + " " + COLUMN_TITLE + " text,"
                    + " " + COLUMN_URL   + " text,"
                    + " " + COLUMN_TIME  + " integer"
                    + ")",

            CREATE_WHITELIST
                    = "CREATE TABLE "
                    + TABLE_WHITELIST
                    + " ("
                    + " " + COLUMN_DOMAIN + " text"
                    + ")",

            CREATE_GRID
                    = "CREATE TABLE "
                    + TABLE_GRID
                    + " ("
                    + " " + COLUMN_TITLE    + " text,"
                    + " " + COLUMN_URL      + " text,"
                    + " " + COLUMN_FILENAME + " text,"
                    + " " + COLUMN_ORDINAL  + " integer"
                    + ")"

                    ;

    private static Record holder;
    public  static Record getHolder() { return holder; }
    public synchronized static void setHolder(Record record) { holder = record; }

}
