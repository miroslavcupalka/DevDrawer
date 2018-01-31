package com.owentech.DevDrawer.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.owentech.DevDrawer.R;
import com.owentech.DevDrawer.adapters.LegacyListAdapter;

import butterknife.ButterKnife;
import butterknife.BindView;

public class LegacyDialog extends Activity {

    @BindView(R.id.legacyListView) ListView listView;
    LegacyListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legacy_dialog);
       ButterKnife.bind(this);
        listAdapter = new LegacyListAdapter(this);
        listView.setAdapter(listAdapter);
    }
}
