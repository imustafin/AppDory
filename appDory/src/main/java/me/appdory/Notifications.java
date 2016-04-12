package me.appdory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import me.appdory.ConcertPageActivity;
import me.appdory.Intents;
import me.appdory.R;
import me.appdory.model.Concert;

public class Notifications {

    static final int SMALL_ICON = R.drawable.ic_launcher;
    static final String CONTENT_TITLE = "AppDory";

    public static final int ID_SYNC_MESSAGE = 0;
    public static final int ID_DEBUG_MESSAGE = 1;

    public static NotificationCompat.Builder getDoryBuilder(Context context) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);

        builder.setSmallIcon(SMALL_ICON);
        builder.setContentTitle(CONTENT_TITLE);

        return builder;

    }

    @SuppressWarnings("deprecation")
    static Notification getDoryWithContentText(Context context, String text) {
        return getDoryBuilder(context).setContentText(text).getNotification();
    }

    public static void showDoryNotificationWithText(Context context, String text,
                                                    int id) {
        Notification notification = getDoryWithContentText(context, text);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

    public static void showDoryNotificationWithLongText(Context context, String text,
                                                        int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle(text)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text));

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());

    }

    public static void showConcertNotification(Context context, Concert concert) {

        PendingIntent piWebpage = PendingIntent.getActivity(context,
                (int) concert.id, Intents.openWebpage(concert.link), 0);

        PendingIntent piCalendar = PendingIntent.getActivity(context,
                (int) concert.id, Intents.addCalendarEvent(concert), 0);

        Intent concertPageIntent = new Intent(context,
                ConcertPageActivity.class).putExtra(
                ConcertPageActivity.EXTRA_CONCERT, concert);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ConcertPageActivity.class);
        stackBuilder.addNextIntent(concertPageIntent);
        PendingIntent clickPendingIntent = stackBuilder.getPendingIntent(
                (int) concert.id, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        builder.setContentIntent(clickPendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.new_concert_))
                .setContentText(concert.title)
                .setStyle(
                        new NotificationCompat.BigTextStyle()
                                .bigText(concert.place + "\n==\n"
                                        + concert.date))
                .addAction(R.drawable.ic_attach_money_black_24dp,
                        context.getString(R.string.to_webpage), piWebpage)
                .addAction(R.drawable.ic_event_black_24dp,
                        context.getString(R.string.calendar), piCalendar);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) concert.id, builder.build());
    }
}
