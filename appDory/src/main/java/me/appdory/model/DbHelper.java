package me.appdory.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.appdory.model.DbContract.ArtistTable;
import me.appdory.model.DbContract.ConcertTable;
import me.appdory.model.DbContract.MyRegionTable;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AppDory.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ARTIST_TABLE);
        db.execSQL(SQL_CREATE_CONCERT_TABLE);
        db.execSQL(SQL_CREATE_MY_REGIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ARTIST_TABLE);
        db.execSQL(SQL_DELETE_CONCERT_TABLE);
        db.execSQL(SQL_DELETE_MY_REGIONS_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ARTIST_TABLE);
        db.execSQL(SQL_DELETE_CONCERT_TABLE);
        db.execSQL(SQL_DELETE_MY_REGIONS_TABLE);
        onCreate(db);
    }

    private static final String SQL_CREATE_ARTIST_TABLE = "CREATE TABLE "
            + ArtistTable.TABLE_NAME + " (" + ArtistTable._ID
            + " INTEGER PRIMARY KEY," + ArtistTable.COLUMN_NAME_ARTIST_NAME
            + " TEXT," + ArtistTable.COLUMN_NAME_ARTIST_COUNT + " INT )";

    private static final String SQL_DELETE_ARTIST_TABLE = "DROP TABLE IF EXISTS "
            + ArtistTable.TABLE_NAME;

    private static final String SQL_CREATE_CONCERT_TABLE = "CREATE TABLE "
            + ConcertTable.TABLE_NAME + " (" + ConcertTable._ID
            + " INTEGER PRIMARY KEY," + ConcertTable.COLUMN_NAME_DATE
            + " TEXT," + ConcertTable.COLUMN_NAME_SOURCE + " TEXT,"
            + ConcertTable.COLUMN_NAME_ID + " INT,"
            + ConcertTable.COLUMN_NAME_IMAGE + " TEXT,"
            + ConcertTable.COLUMN_NAME_LINK + " TEXT,"
            + ConcertTable.COLUMN_NAME_PLACE + " TEXT,"
            + ConcertTable.COLUMN_NAME_REGION + " TEXT,"
            + ConcertTable.COLUMN_NAME_REGION_ENG + " TEXT,"
            + ConcertTable.COLUMN_NAME_TITLE + " TEXT,"
            + ConcertTable.COLUMN_NAME_BITMAP_PATH + " TEXT,"
            + ConcertTable.COLUMN_NAME_VIEWED + " BOOL )";

    private static final String SQL_DELETE_CONCERT_TABLE = "DROP TABLE IF EXISTS "
            + ConcertTable.TABLE_NAME;

    private static final String SQL_CREATE_MY_REGIONS_TABLE = "CREATE TABLE "
            + MyRegionTable.TABLE_NAME + " (" + MyRegionTable._ID
            + " INTEGER PRIMARY KEY," + MyRegionTable.COLUMN_NAME_REGION
            + " TEXT )";

    private static final String SQL_DELETE_MY_REGIONS_TABLE = "DROP TABLE IF EXISTS "
            + MyRegionTable.TABLE_NAME;

}
