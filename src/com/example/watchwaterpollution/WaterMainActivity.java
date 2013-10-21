package com.example.watchwaterpollution;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

public class WaterMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.left_menu_layout);
 /*       
        final LinearLayout left=(LinearLayout)findViewById(R.id.left_menu);
        final LinearLayout right=(LinearLayout)findViewById(R.id.right_menu);
        right.setVisibility(View.GONE);
        left.setVisibility(View.VISIBLE);
        
        ScrollLayout mScrollLayout=(ScrollLayout)findViewById(R.id.my_scrollLayout);
        mScrollLayout.setScrollSideChangedListener(new ScrollLayout.OnScrollSideChangedListener() {
            @Override
            public void onScrollSideChanged(View v, boolean leftSide) {
                if(leftSide)
                {
                    right.setVisibility(View.GONE);
                    left.setVisibility(View.VISIBLE);
                }else
                {
                    right.setVisibility(View.VISIBLE);
                    left.setVisibility(View.GONE);
                }
            }
        });*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_water_main, menu);
		return true;
	}

}
