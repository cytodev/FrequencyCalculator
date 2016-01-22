package com.cytodev.freqalc.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cytodev.freqalc.R;

/**
 * com.cytodev.freqalc.activities "Frequency Calculator"
 * 2016/01/22 @ 16:50
 *
 * @author Roel Walraven <cytodev@gmail.com>
 */
public class CytoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_cyto);

        findViewById(R.id.github).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent github = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/cytodev/"));
                github.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(github);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
