package com.example.elmina.equations;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Rating extends Activity {

    ListView records;
    DB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        records = (ListView)findViewById(R.id.listView);

        dbHelper = new DB(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<String[]> res = dbHelper.read();
        String[][] data = res.toArray(new String[res.size()][2]);;
        String[] s = new String[res.size()];
        int i = 0;
        for (String[] s1: data){
            s[i] = s1[0] + ": " + s1[1];
            i++;
        }
        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, s);

        // присваиваем адаптер списку
        records.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rating, menu);
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
}
