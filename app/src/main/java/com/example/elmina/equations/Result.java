package com.example.elmina.equations;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Result extends Activity{
    TextView countTxt;
    EditText name;
    Button save;
    int score = 0;
    DB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        countTxt = (TextView)findViewById(R.id.textView2);
        name = (EditText)findViewById(R.id.editText);
        save = (Button)findViewById(R.id.button);
        Button mainM = (Button)findViewById(R.id.button2);
        dbHelper = new DB(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.addResult(name.getText().toString(), score);
            }
        });
        mainM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Result.this, MainActivity.class);
                startActivity(intent);
            }
        });
        boolean isWinner = (boolean) getIntent().getSerializableExtra("ISWINNER");
        TextView resultG = (TextView)findViewById(R.id.textView);
        resultG.setText(isWinner ? "Победа!" : "Поражение");
        score = (int) getIntent().getSerializableExtra("POINTS");
        countTxt.setText("Вы набрали: " + score + " очков");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.rating) {
            Intent intent = new Intent(Result.this, Rating.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
