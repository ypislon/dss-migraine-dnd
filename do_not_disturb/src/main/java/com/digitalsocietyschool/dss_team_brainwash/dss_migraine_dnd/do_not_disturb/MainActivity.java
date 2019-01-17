package com.digitalsocietyschool.dss_team_brainwash.dss_migraine_dnd.do_not_disturb;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.System;
import android.telephony.SmsManager;
import android.view.ContextThemeWrapper;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.content.Intent;
import android.os.Build;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.view.View;
import android.content.Context;
import android.media.AudioManager;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.content.ContextCompat;
import android.widget.Switch;
import android.widget.CompoundButton;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private int brightness;

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    private int originalBrightness;

    private boolean dndModeEnabled = false;

    private boolean checked = false;

    private SharedPreferences mPrefs;


    private BroadcastReceiver listener = new BroadcastReceiver() {

        @Override
        public void onReceive( Context context, Intent intent ) {

            String data = intent.getStringExtra("DATA");

            Log.d("Received data 123: ", data);

        }

    };


//    private static void showBrightnessPermissionDialog(final Context context) {
//
//        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setCancelable(true);
//        final AlertDialog alert = builder.create();
//        builder.setMessage("Please give the permission to change brightness. \n Thanks ")
//                .setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
//                        intent.setData(Uri.parse("package:" + context.getPackageName()));
//                        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                        alert.dismiss();
//                    }
//                });
//        alert.show();
//    }
//    int PERMISSION_REQUEST_READ_PHONE_STATE;
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_READ_PHONE_STATE: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Permission NOT granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences mPrefs = this.getSharedPreferences("dnd_mode", MODE_PRIVATE);

        boolean mCurDndMode = mPrefs.getBoolean("headache_mode", false);
        Log.d("dnd", "Local storage onCreate says " + mCurDndMode);

        setContentView(R.layout.activity_main);

        Bundle extras = this.getIntent().getExtras();
        if(extras != null) {
            if(extras.getBoolean("TURN_OFF_HEADACHE_MODE")) {
                SharedPreferences.Editor edd = mPrefs.edit();
                edd.putBoolean("headache_mode", false);
                edd.apply();
                Log.d("dnd", "eyyyy");
            }
        }

        Switch toggle = (Switch) findViewById(R.id.switch1);
        toggle.setChecked(mCurDndMode);
        toggleDoNotDisturb(mCurDndMode);

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
//                new IntentFilter("custom-event-name"));

        LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter("TURN_OFF_HEADACHE_MODE"));

        Bundle extr = this.getIntent().getExtras();
        if(extr != null) {
            if(extr.getBoolean("headache_mode")) {
                SharedPreferences.Editor edd = mPrefs.edit();
                edd.putBoolean("headache_mode", false);
                edd.apply();
                Log.d("dnd", "eyyyy");
            }
        }

        // prompt the user for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                originalBrightness = System.getInt(getContentResolver(), System.SCREEN_BRIGHTNESS, 0);
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }

            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
                requestPermissions(permissions, 1);
            }

//            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//
//                // Permission is not granted
//                SmsManager manager = SmsManager.getDefault();
//
//                manager.sendTextMessage(number, null, "I have a migraine attack right now. Please contact me later.", null, null);
//            }
        }

        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // Check if the notification policy access has been granted for the app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }

        // Save the timestamps only on click of the button!
        // We programmatically change the "isChecked" attribute of the switch, so we
        // have to double-check if the user ended the mode manually
        toggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentTime = Calendar.getInstance().getTime();

                if (((CompoundButton) v).isChecked() && mPrefs.getBoolean("headache_mode", false)) {

                    // Save the timestamp of the start of headache attack
                    Log.d("dnd", "headache started at " + currentTime);
                }
                else if(!mPrefs.getBoolean("headache_mode", false)) {

                    // Save the timestamp of the end of headache attack
                    Log.d("dnd", "headache ended at " + currentTime);
                }
            }
        });

        final SharedPreferences.Editor ed = mPrefs.edit();

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // The toggle is enabled
                    ed.putBoolean("headache_mode", true);
                    ed.apply();
                    toggleDoNotDisturb(true);
                } else {
                    // The toggle is disabled
                    ed.putBoolean("headache_mode", false);
                    ed.apply();
                    toggleDoNotDisturb(false);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences mPrefs = this.getSharedPreferences("dnd_mode", MODE_PRIVATE);
//        boolean mCurDndMode = mPrefs.getBoolean("headache_mode", false);

//        SharedPreferences.Editor ed = mPrefs.edit();
//        ed.putBoolean("headache_mode", dndModeEnabled);
//        ed.apply();
        Log.d("dnd", "Local storage says " + mPrefs.getBoolean("headache_mode", false));
    }

    @Override
    protected  void onResume() {
        super.onResume();

//        Intent intentt = new Intent("TURN_HEADACHE_MODE_OFF");
//        LocalBroadcastManager.getInstance(this).registerReceiver(listener, intentt);

        LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter("TURN_OFF_HEADACHE_MODE"));

        Bundle extras = this.getIntent().getExtras();
        if(extras != null) {
            if(extras.getBoolean("TURN_OFF_HEADACHE_MODE")) {
                SharedPreferences.Editor edd = mPrefs.edit();
                edd.putBoolean("headache_mode", false);
                edd.apply();
                Log.d("dnd", "eyyyy");
            }
        }

    }

    protected void toggleDoNotDisturb(boolean enabled) {

        int currentBrightness = System.getInt(getContentResolver(), System.SCREEN_BRIGHTNESS, 0);

        // set the phone to "do not disturb mode"
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);


        int ringerMode = mAudioManager.getRingerMode();

        // Check if the notification policy access has been granted for the app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }

            SharedPreferences mPrefs = this.getSharedPreferences("dnd_mode", MODE_PRIVATE);

            if(mPrefs.getBoolean("headache_mode", false) && enabled) {
                // Dim the screen
                int brightness = 0;
                System.putInt(getContentResolver(), System.SCREEN_BRIGHTNESS, brightness);

                // Set media playback to silent
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

                // Change the global notification policy to prevent all notifications
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                if (ringerMode != 0) {
                    try {
                        mAudioManager.setRingerMode(0);
                    } catch (Exception e) {
                        Log.e("dnd", "Fatal error. Couldn't set ringer mode.");
                    }
                }
                // Set the background color
                int color = ContextCompat.getColor(getApplicationContext(), R.color.colorDark);
                ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
                mainLayout.setBackgroundColor(color);
                // Set the text color
                TextView text = (TextView) findViewById(R.id.textView);
                text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorLight));
                // Set the switch text color
                Switch toggle = (Switch) findViewById(R.id.switch1);
                toggle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorLight));

                // Put notification up to comfort user and disable headache mode
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "123";
                    String description = "12345";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("default", name, importance);
                    channel.setDescription(description);
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

                String textTitle = "123";
                String textContent = "a littleb it of content";

                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent intent = PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                // TODO
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
//                Intent turnOffIntent = new Intent("TURN_OFF_HEADACHE_MODE");
//                not needed, because we give the pending intent to the notification
//                localBroadcastManager.sendBroadcast(turnOffIntent);

                Intent turnOffIntent = new Intent(this, MainActivity.class);
                turnOffIntent.putExtra("TURN_OFF_HEADACHE_MODE", true);
                PendingIntent turnOffHeadacheModeIntent = PendingIntent.getBroadcast(this, 0, turnOffIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                        .setAutoCancel(true)
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .setContentTitle(textTitle)
                        .setContentText(textContent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(intent)
                        .addAction(android.R.drawable.ic_delete, "Turn off headache mode", turnOffHeadacheModeIntent);

                // Enable the notification which comforts the user
                mNotificationManager.notify(1, mBuilder.build());

            } else if (!mPrefs.getBoolean("headache_mode", false) && !enabled) { // dnd mode is not enabled
                // Set the brightness up again
                System.putInt(getContentResolver(), System.SCREEN_BRIGHTNESS, 70);

                // Set media playback to loud
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2), 0);

                // TODO unblock all notification
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);

                // Set alarm and call volume up
                if (ringerMode == 0) {
                    try {
                        mAudioManager.setRingerMode(1);
                    } catch (Exception e) {
                        Log.e("dnd", "Fatal error. Couldn't set ringer mode.");
                    }
                }

                // Set the background color to bright
                int color = ContextCompat.getColor(getApplicationContext(), R.color.colorLight);
                ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
                mainLayout.setBackgroundColor(color);

                // Set the text color to dark
                TextView text = (TextView) findViewById(R.id.textView);
                text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDark));
                // Set the switch text color to dark
                Switch toggle = (Switch) findViewById(R.id.switch1);
                toggle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDark));

                // Cancel the notification to turn off headache mode
                mNotificationManager.cancel(1);
            }
        }

        // block all incoming phone calls


    }
}
