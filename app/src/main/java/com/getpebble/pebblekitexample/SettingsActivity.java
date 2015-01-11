package com.getpebble.pebblekitexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity {

    //Set us up some strings.
    public String phoneNumber1;
    public String panicNumber;
    public String messageText1;
    public String callNumber;

    public static final String MY_PREFS_NAME = "settings";


    //Setup the text boxes objects.
    //EditText editTextPhoneNumber1 = (EditText) findViewById(R.id.editTextTextNumber);
    //EditText editTextPanicNumber = (EditText) findViewById(R.id.editTextPanicCall);
    //EditText editTextMessageText1 = (EditText) findViewById(R.id.editTextMessageText);

    EditText editTextPhoneNumberOne;
    EditText editTextPanicNumber;
    EditText editTextMessageText;
    EditText editTextCallNumber;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextPhoneNumberOne = (EditText) findViewById(R.id.editTextTextNumber);
        editTextPanicNumber = (EditText) findViewById(R.id.editTextPanicCall);
        editTextMessageText = (EditText) findViewById(R.id.editTextMessageText);
        editTextCallNumber = (EditText) findViewById(R.id.editTextSelfCall);



        getSettings();

        Button saveSettingsButton = (Button)findViewById(R.id.buttonSave);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                writeSettings();

               }

        });
    }

    public void writeSettings(){
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        //grab the settings from the EditText boxes.
        phoneNumber1 = editTextPhoneNumberOne.getText().toString();
        messageText1 = editTextMessageText.getText().toString();
        panicNumber = editTextPanicNumber.getText().toString();
        callNumber = editTextCallNumber.getText().toString();
        logOutStrings();

        editor.putString("phoneNumber1", phoneNumber1);
        editor.putString("message1", messageText1);
        editor.putString("panicNumber", panicNumber);
        editor.commit();
        Log.v("write setings","just wrote the settings");
        logOutStrings();

    }

    public void getSettings(){
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        phoneNumber1 = prefs.getString("phoneNumber1", "Enter a Number.");
        messageText1 = prefs.getString("message1", "Enter a Message.");
        panicNumber = prefs.getString("panicNumber", "Enter a Number.");
        callNumber = prefs.getString("callNumber", "Enter a Number.");

        Log.v("read setings","just read the settings");
        logOutStrings();

        //write the settings you grabbed to the EditText objects.
        //editTextPhoneNumber1.setText(phoneNumber1);
        //editTextPanicNumber.setText(panicNumber);
        //editTextMessageText1.setText(messageText1);

        editTextPhoneNumberOne.setText(phoneNumber1);
        editTextMessageText.setText(messageText1);
        editTextPanicNumber.setText(panicNumber);
        editTextCallNumber.setText(callNumber);

    }



    public void logOutStrings(){
        Log.v("String Settings: Phone 1:", phoneNumber1);
        Log.v("String Settings: Panic Number:",panicNumber);
        Log.v("String Settings: Message Text:",messageText1);
        Log.v("string Settings: Self Call Number:", callNumber);


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
