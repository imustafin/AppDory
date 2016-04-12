package me.appdory;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import me.appdory.model.Concert;

public class ConcertPageActivity extends Activity {

    static public final String EXTRA_CONCERT = "me.appdory.EXTRA_CONCERT";

    Concert mConcert;
    ImageView mImage;
    TextView mText;
    ImageButton mBuy;
    ImageButton mCalendar;

    public ConcertPageActivity() {
        // Required empty public constructor
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert_page);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        mConcert = extras.getParcelable(EXTRA_CONCERT);

        mImage = (ImageView) findViewById(R.id.concert_page_image);
        mText = (TextView) findViewById(R.id.concert_page_concert_info);
        mBuy = (ImageButton) findViewById(R.id.buy_button);
        mCalendar = (ImageButton) findViewById(R.id.calendar_button);

        mText.setText(getInfo());

        Picasso.with(this).load(new File(mConcert.bitmapPath)).into(mImage);

        mBuy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBuyClicked();
            }
        });

        mCalendar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onCalendarClicked();
            }
        });

    }

    ;

    void onBuyClicked() {
        startActivity(Intents.openWebpage(mConcert.link));
    }

    void onCalendarClicked() {
        startActivity(Intents.addCalendarEvent(mConcert));
    }

    static final String INFO_FORMAT = "<h1>%s</h1>%s<br><b>%s</b>";

    Spanned getInfo() {
        String string = String.format(INFO_FORMAT, mConcert.title, mConcert.place,
                mConcert.date);
        return Html.fromHtml(string);
    }

}
