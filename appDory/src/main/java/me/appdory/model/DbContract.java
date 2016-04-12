package me.appdory.model;

import android.provider.BaseColumns;

public final class DbContract {

    public DbContract() {
    }

    public static abstract class ArtistTable implements BaseColumns {
        public static final String TABLE_NAME = "my_songs";
        public static final String COLUMN_NAME_ARTIST_NAME = "artist_name";
        public static final String COLUMN_NAME_ARTIST_COUNT = "artist_count";
    }

	/*
	public final long id;
	public final String date;
	public final String link;
	public final String place;
	public final String title;
	public final String image;
	public final String region;
	public final String regionEng;
	public final String from;
	 */

    public static abstract class ConcertTable implements BaseColumns {
        public static final String TABLE_NAME = "my_concerts";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_PLACE = "place";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_REGION = "region";
        public static final String COLUMN_NAME_REGION_ENG = "region_eng";
        public static final String COLUMN_NAME_SOURCE = "source";

        public static final String COLUMN_NAME_BITMAP_PATH = "bitmap_path";

        public static final String COLUMN_NAME_VIEWED = "viewed";
    }

    public static abstract class MyRegionTable implements BaseColumns {
        public static final String TABLE_NAME = "my_regions";
        public static final String COLUMN_NAME_REGION = "region";
    }

}
