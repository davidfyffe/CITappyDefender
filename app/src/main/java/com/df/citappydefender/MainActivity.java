package com.df.citappydefender;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //prepare to load the fastest time
        SharedPreferences preferences;
        SharedPreferences.Editor editor;

        preferences = getSharedPreferences("HiScores", MODE_PRIVATE);

        final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
        final TextView textFastestTime = (TextView) findViewById(R.id.textHighScore);

        buttonPlay.setOnClickListener(this);

        long fastestTime = preferences.getLong("fastestTime", 1000000);
        textFastestTime.setText("Fastest Time:" + fastestTime);
    }

    @Override
    public void onClick(View view) {
        //create intent ob ject
        //can onlyn be one button pressed on the main screem

        Intent i = new Intent(this, GameActivity.class);

        startActivity(i);

        //shut this activity Down
        finish();
    }
}
