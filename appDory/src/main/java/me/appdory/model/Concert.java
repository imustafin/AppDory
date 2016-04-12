package me.appdory.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import me.appdory.model.DbContract.ConcertTable;

public class Concert implements Parcelable {

    public final long id;
    public final String date;
    public final String link;
    public final String place;
    public final String title;
    public final String image;
    public final String region;

    @Override
    public String toString() {
        return "Concert [id=" + id + ", date=" + date + ", link=" + link
                + ", place=" + place + ", title=" + title + ", image=" + image
                + ", region=" + region + ", regionEng=" + regionEng + ", from="
                + from + ", bitmapPath=" + bitmapPath + "]";
    }

    public final String regionEng;
    public final String from;

    public String bitmapPath;

    public Concert(long id, String date, String link, String place,
                   String title, String image, String region, String regionEng,
                   String from) {

        this.id = id;
        this.date = date;
        this.link = link;
        this.place = place;
        this.title = title;
        this.image = image;
        this.region = region;
        this.regionEng = regionEng;
        this.from = from;
    }

    /**
     * Constructs <code>Concert</code> from <code>JSON</code> object
     *
     * @param json a <code>JSON</code> object to construct from
     * @throws JSONException
     */
    public Concert(JSONObject json) throws JSONException {
        this.id = json.getLong("id");
        this.date = json.getString("date");
        this.link = json.getString("link");
        this.place = json.getString("place");
        this.title = json.getString("title");
        this.image = json.getString("image");
        this.region = json.getString("region");
        this.regionEng = json.getString("region_eng");
        this.from = json.getString("from");
    }

    public Concert(Cursor cursor) {
        this.id = cursor.getLong(cursor
                .getColumnIndexOrThrow(ConcertTable.COLUMN_NAME_ID));

        this.date = cursor.getString(cursor
                .getColumnIndexOrThrow(ConcertTable.COLUMN_NAME_DATE));
        this.link = cursor.getString(cursor
                .getColumnIndexOrThrow(ConcertTable.COLUMN_NAME_LINK));
        this.place = cursor.getString(cursor
                .getColumnIndexOrThrow(ConcertTable.COLUMN_NAME_PLACE));
        this.title = cursor.getString(cursor
                .getColumnIndexOrThrow(ConcertTable.COLUMN_NAME_TITLE));
        this.image = cursor.getString(cursor
                .getColumnIndexOrThrow(ConcertTable.COLUMN_NAME_IMAGE));
        this.region = cursor.getString(cursor
                .getColumnIndexOrThrow(ConcertTable.COLUMN_NAME_REGION));
        this.regionEng = cursor.getString(cursor
                .getColumnIndexOrThrow(ConcertTable.COLUMN_NAME_REGION_ENG));
        this.from = cursor.getString(cursor
                .getColumnIndexOrThrow(ConcertTable.COLUMN_NAME_SOURCE));

        this.bitmapPath = cursor.getString(cursor
                .getColumnIndexOrThrow(ConcertTable.COLUMN_NAME_BITMAP_PATH));
    }

    public void putColumnsToContentValues(ContentValues values) {
        values.put(ConcertTable.COLUMN_NAME_DATE, date);
        values.put(ConcertTable.COLUMN_NAME_SOURCE, from);
        values.put(ConcertTable.COLUMN_NAME_ID, id);
        values.put(ConcertTable.COLUMN_NAME_IMAGE, image);
        values.put(ConcertTable.COLUMN_NAME_LINK, link);
        values.put(ConcertTable.COLUMN_NAME_PLACE, place);
        values.put(ConcertTable.COLUMN_NAME_REGION, region);
        values.put(ConcertTable.COLUMN_NAME_REGION_ENG, regionEng);
        values.put(ConcertTable.COLUMN_NAME_TITLE, title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // public final long id;
    // public final String date;
    // public final String link;
    // public final String place;
    // public final String title;
    // public final String image;
    // public final String region;
    // public final String regionEng;
    // public final String from;
    //
    // public String bitmapPath;

    private Concert(Parcel source) {
        id = source.readLong();
        date = source.readString();
        link = source.readString();
        place = source.readString();
        title = source.readString();
        image = source.readString();
        region = source.readString();
        regionEng = source.readString();
        from = source.readString();
        bitmapPath = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(date);
        dest.writeString(link);
        dest.writeString(place);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(region);
        dest.writeString(regionEng);
        dest.writeString(from);
        dest.writeString(bitmapPath);
    }

    public static final Parcelable.Creator<Concert> CREATOR = new Creator<Concert>() {

        @Override
        public Concert[] newArray(int size) {
            return new Concert[size];
        }

        @Override
        public Concert createFromParcel(Parcel source) {
            return new Concert(source);
        }
    };

}
