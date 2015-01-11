package com.getpebble.pebblekitexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;

public class SettingsActivity extends Activity {

    //Set us up some strings.
    public String phoneNumber1;
    public String phoneNumber2;
    public String messageText1;
    public String messageText2;

    public static final String MY_PREFS_NAME = "settings";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Restore preferences
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        //editor.putString("name", "Elena");
        //editor.putInt("idName", 12);
        //editor.commit();
    }

    public void writeSettings(){
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("phoneNumber1", phoneNumber1);
        editor.putString("phoneNumber2", phoneNumber2);
        editor.putString("message1", messageText1);
        editor.putString("message2", messageText2);
        editor.commit();
    }

    public void getSettings(){
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        phoneNumber1 = prefs.getString("phoneNumber1", "No number defined");
        phoneNumber2 = prefs.getString("phoneNumber2", "No number defined");
        messageText1 = prefs.getString("message1", "Enter a message.");
        messageText2 = prefs.getString("message2", "Enter a message.");

    }

    public void logOutStrings(){
        Log.v("String Settings: Phone 1:", phoneNumber1);
        Log.v("String Settings: Phone 2:",phoneNumber1);
        Log.v("String Settings: Message 1:",messageText1);
        Log.v("String Settings: Message 2:",messageText2);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
