package com.getpebble.pebblekitexample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

public class MainActivity extends Activity {

    //The orig from the demo.
	//private static final UUID WATCHAPP_UUID = UUID.fromString("6092637b-8f58-4199-94d8-c606b1e45040");

    private static final UUID WATCHAPP_UUID = UUID.fromString("728a5d46-18fb-4e37-bfae-803b7367decd");

	private static final int
		KEY_BUTTON = 0,
		KEY_VIBRATE = 1,
		BUTTON_UP = 0,
		BUTTON_SELECT = 1,
		BUTTON_DOWN = 2;

    //Let us define some static doodads for our app...
        static final int OPTION_1 = 0;
        static final int OPTION_2 = 1;
        static final int OPTION_3 = 2;

    /**************** MESSAGES TO SEND *****************/
	private Handler handler = new Handler();
	private PebbleDataReceiver appMessageReciever;
	private TextView whichButtonView;
    private TextView whichActionView;

    private String phoneNumber1 = "18569049398";
    private String message1= "OMG LULZ I'M ON A BAD DATE CALL ME!";

    private String phoneNumber2 = "18569049398";
    private String message2 = "TOP KEK I'M BEING KIDNAPPED!";

    /****** The intent for the settings menu. ***********/
    public Intent menuSettingsIntent;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    	    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                Log.v("Creepr","Call Menu Settings");
                menuSettingsIntent= new Intent(this,SettingsActivity.class);
                startActivity(menuSettingsIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
        }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        // Keep the app awake.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // Customize ActionBar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Creepr");
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_orange)));
		
		// Add Install Button behavior
//		Button installButton = (Button)findViewById(R.id.button_install);
//		installButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				//Install
//				Toast.makeText(getApplicationContext(), "Installing watchapp...", Toast.LENGTH_SHORT).show();
//				sideloadInstall(getApplicationContext(), WATCHAPP_FILENAME);
//			}
//
//		});

		// Add vibrate Button behavior

        Button vibrateButton = (Button)findViewById(R.id.button_vibrate);
		vibrateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Send KEY_VIBRATE to Pebble
				//PebbleDictionary out = new PebbleDictionary();
				//out.addInt32(KEY_VIBRATE, 0);
				//PebbleKit.sendDataToPebble(getApplicationContext(), WATCHAPP_UUID, out);
                //creepr_text_send();
                creepr_call_police();
			}
			
		});
		
		// Add output TextView behavior
		whichButtonView = (TextView)findViewById(R.id.which_button);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Define AppMessage behavior
		if(appMessageReciever == null) {
			appMessageReciever = new PebbleDataReceiver(WATCHAPP_UUID) {
				
				@Override
				public void receiveData(Context context, int transactionId, PebbleDictionary data) {
					// Always ACK
					PebbleKit.sendAckToPebble(context, transactionId);
					
					// What message was received?
					if(data.getInteger(KEY_BUTTON) != null) {
						// KEY_BUTTON was received, determine which button
						final int button = data.getInteger(KEY_BUTTON).intValue();
						
						// Update UI on correct thread
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								switch(button) {
								case OPTION_1:
                                    Log.v("Creeper","UP BUTTON");
									whichButtonView.setText("UP");
                                    creepr_text_send(phoneNumber1,message1);
									break;
								case OPTION_2:
                                    Log.v("Creeper","MIDDLE BUTTON");
                                    whichButtonView.setText("SELECT");
                                    creepr_text_send(phoneNumber2,message2);
									break;
								case OPTION_3:
                                    Log.v("Creeper", "DOWN BUTTON");
                                    whichButtonView.setText("DOWN");
                                    creepr_text_send("18569049398","Creeper 3");

									break;
								default:
									Toast.makeText(getApplicationContext(), "Unknown button: " + button, Toast.LENGTH_SHORT).show();
									break;
								}
							}
							
						});
					} 
				}
			};
		
			// Add AppMessage capabilities
			PebbleKit.registerReceivedDataHandler(this, appMessageReciever);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// Unregister AppMessage reception
		if(appMessageReciever != null) {
			unregisterReceiver(appMessageReciever);
			appMessageReciever = null;
		}
	}
	
	/**
     * Alternative sideloading method
     * Source: http://forums.getpebble.com/discussion/comment/103733/#Comment_103733 
     */
    public static void sideloadInstall(Context ctx, String assetFilename) {
        try {
            // Read .pbw from assets/
        	Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(ctx.getExternalFilesDir(null), assetFilename);
            InputStream is = ctx.getResources().getAssets().open(assetFilename);
            OutputStream os = new FileOutputStream(file);
            byte[] pbw = new byte[is.available()];
            is.read(pbw);
            os.write(pbw);
            is.close();
            os.close();
             
            // Install via Pebble Android app
            intent.setDataAndType(Uri.fromFile(file), "application/pbw");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(ctx, "App install failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void creepr_text_send(String numberToSendTextTo,String messageToSendInText){
        //Hatricks # 8569049398
        //String numberToSendTextTo = "18569049398";
        //String messageToSendInText = "Call me there is a creep here!";

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(numberToSendTextTo,null,messageToSendInText,null,null);

    }

    public void creepr_fake_call(){

    }

    public void creepr_call_police(){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:18569049398"));
        startActivity(intent);

    }

}
