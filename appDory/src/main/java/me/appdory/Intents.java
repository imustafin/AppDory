package me.appdory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

import java.util.GregorianCalendar;

import me.appdory.model.Concert;

public class Intents {

    public static Intent openWebpage(String link) {
        Uri webpage = Uri.parse(link);
        Intent webpageIntent = new Intent(Intent.ACTION_VIEW, webpage);
        return webpageIntent;
    }

    public static Intent addCalendarEvent(Concert concert) {
        String[] dateTime = concert.date.split(" ");

        String[] dateParts = dateTime[0].split("-");

        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);

        String[] timeParts = dateTime[1].split(":");

        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        GregorianCalendar calDate = new GregorianCalendar(year, month, day);
        calDate.set(GregorianCalendar.HOUR_OF_DAY, hour);
        calDate.set(GregorianCalendar.MINUTE, minute);

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        calDate.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        calDate.getTimeInMillis())
                .putExtra(Events.TITLE, concert.title)
                .putExtra(Events.DESCRIPTION, "Концерт из " + concert.from)
                .putExtra(Events.EVENT_LOCATION, concert.place);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    public static Intent concertPage(Context context, Concert concert) {
        Intent intent = new Intent().setClass(context, ConcertPageActivity.class)
                .putExtra(ConcertPageActivity.EXTRA_CONCERT, concert);

        return intent;
    }

}
