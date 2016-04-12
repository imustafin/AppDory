package me.appdory.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import me.appdory.Intents;
import me.appdory.R;
import me.appdory.model.Concert;

public class ConcertListAdapter extends ArrayAdapter<Concert> {

    Context mContext;

    public ConcertListAdapter(Context context, int resource) {
        super(context, resource);
        context = mContext;
    }

    public ConcertListAdapter(Context context, int resource, List<Concert> items) {
        super(context, resource, items);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.concert_list_item, parent, false);
        }

        final Concert concert = getItem(position);

        ImageButton buyButton = (ImageButton) view.findViewById(R.id.buy_button);
        ImageButton addCalendarButton = (ImageButton) view
                .findViewById(R.id.calendar_button);
        ImageView image = (ImageView) view.findViewById(R.id.concert_image);
        image.setVisibility(View.VISIBLE);
        LinearLayout layout = (LinearLayout) view
                .findViewById(R.id.concert_layout);

        layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                layoutClicked(concert);
            }
        });


        buyButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                buyButtonClicked(concert);
            }
        });

        addCalendarButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addCalendarClicked(concert);
            }
        });

        if (concert != null) {
            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView placeView = (TextView) view.findViewById(R.id.place);
            TextView timeView = (TextView) view.findViewById(R.id.time);

            titleView.setText(concert.title);
            placeView.setText(concert.place);
            timeView.setText(concert.date);
        }

        Picasso.with(mContext).load(new File(concert.bitmapPath)).into(image);

        return view;
    }

    void buyButtonClicked(Concert concert) {
        mContext.startActivity(Intents.openWebpage(concert.link));
    }

    void addCalendarClicked(Concert concert) {
        mContext.startActivity(Intents.addCalendarEvent(concert));
    }

    void layoutClicked(Concert concert) {
        mContext.startActivity(Intents.concertPage(mContext, concert));
    }

}
