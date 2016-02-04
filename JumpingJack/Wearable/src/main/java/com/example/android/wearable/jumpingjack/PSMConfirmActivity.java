package com.example.android.wearable.jumpingjack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class PSMConfirmActivity extends Activity {

    public static String MSG_SELECTED_IMG_ID = "MSG_SELECTED_IMG_ID";
    public static String MSG_PSM_IS_CONFIRMED = "MSG_PSM_IS_CONFIRMED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psmconfirm);

        Intent intent = getIntent();

        int imgId = intent.getIntExtra(MSG_SELECTED_IMG_ID, R.drawable.fish_normal017);
        ImageView imgView = (ImageView)findViewById(R.id.psmConfirmImageView);
/*
        int dim = imgView.getWidth();
        imgView.setLayoutParams(new GridView.LayoutParams(dim,dim));
        imgView.setMaxHeight(imgView.getMaxWidth());
*/

        imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Drawable d = getResources().getDrawable(imgId);
        imgView.setImageDrawable(d);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_psmconfirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCancelClick(View v) {
        finish();
    }

    public void onSubmitClick(View v) {
        Intent data = new Intent();
        data.putExtra("MSG_PSM_IS_CONFIRMED", true);

        if(getParent() == null) {
            setResult(Activity.RESULT_OK, data);
        } else {
            getParent().setResult(Activity.RESULT_OK, data);
        }

        finish();
    }
}
