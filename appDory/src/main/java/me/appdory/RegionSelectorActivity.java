package me.appdory;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

public class RegionSelectorActivity extends Activity {

    MultiAutoCompleteTextView mTextView;

    static final String[] REGIONS = {"lel", "pinkie pie", "rerite",
            "twilight sparkle", "inkie pie"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_selector);

        mTextView = (MultiAutoCompleteTextView) findViewById(R.id.region_selector_text);

        ArrayAdapter<String> autocompAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, REGIONS);

        mTextView.setAdapter(autocompAdapter);
        mTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

    }
}
