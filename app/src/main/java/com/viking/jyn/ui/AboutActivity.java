package com.viking.jyn.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.widget.TextView;

import com.viking.jyn.BuildConfig;
import com.viking.jyn.R;

import java.util.Calendar;

public class AboutActivity extends AppCompatActivity {

    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Set up arrow to close the activity
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_about);

        //Let's set the copyright and app version dynamically
        TextView appVersion = findViewById(R.id.versionTxt);
        TextView openSourceInfo = findViewById(R.id.opensource_info_tv);

        openSourceInfo.setText(getString(R.string.open_source_info, "https://gitlab.com/vikingden7/GYN", "GNU AGPLv3"));

        //Let's build the copyright text using String builder
        StringBuilder copyRight = new StringBuilder();
        copyRight.append("Copyright &copy; Viking Den 2017-")
                .append(Calendar.getInstance().get(Calendar.YEAR))
                .append("\n");

        copyRight.append(getResources().getString(R.string.app_name))
                .append(" V")
                .append(BuildConfig.VERSION_NAME);
        //set the text as html to get copyright symbol
        appVersion.setText(fromHtml(copyRight.toString()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //finish this activity and return to parent activity
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
