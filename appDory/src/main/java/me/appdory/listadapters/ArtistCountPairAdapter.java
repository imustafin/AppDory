package me.appdory.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.appdory.R;
import me.appdory.model.ArtistCountPair;

public class ArtistCountPairAdapter extends ArrayAdapter<ArtistCountPair> {

    public ArtistCountPairAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ArtistCountPairAdapter(Context context, int resource,
                                  List<ArtistCountPair> items) {

        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.artists_list_item, parent, false);
        }

        String songCount = view.getResources().getString(R.string.song_count);

        ArtistCountPair pair = getItem(position);

        if (pair != null) {
            TextView artistView = (TextView) view.findViewById(R.id.artist);
            TextView countView = (TextView) view.findViewById(R.id.count);

            artistView.setText(pair.artist);
            countView.setText(songCount + ": " + pair.count);
        }

        return view;
    }

}
