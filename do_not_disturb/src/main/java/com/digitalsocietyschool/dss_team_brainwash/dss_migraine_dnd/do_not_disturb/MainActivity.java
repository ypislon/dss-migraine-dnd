package com.digitalsocietyschool.dss_team_brainwash.dss_migraine_dnd.do_not_disturb;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.System;
import android.telephony.SmsManager;
import android.text.Layout;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mPrefs;

    private BroadcastReceiver phoneBlocker;

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

        final SharedPreferences.Editor ed = mPrefs.edit();
        if(!mPrefs.contains("headache_mode_c_sms")) {
            ed.putBoolean("headache_mode_c_sms", false);
        }
        if(!mPrefs.contains("headache_mode_c_block_phone")) {
            ed.putBoolean("headache_mode_c_block_phone", false);
        }
        if(!mPrefs.contains("headache_mode_c_brightness")) {
            ed.putBoolean("headache_mode_c_brightness", false);
        }
        if(!mPrefs.contains("headache_mode_c_silent")) {
            ed.putBoolean("headache_mode_c_silent", false);
        }
        ed.apply();

        setContentView(R.layout.activity_main);

        Switch toggle = (Switch) findViewById(R.id.switch1);
        toggle.setChecked(mCurDndMode);

        Switch toggleCSms = findViewById(R.id.switchSMS);
        toggleCSms.setChecked(mPrefs.getBoolean("headache_mode_c_sms", false));
        Switch toggleCBlockCalls = findViewById(R.id.switchBlockCalls);
        toggleCBlockCalls.setChecked(mPrefs.getBoolean("headache_mode_c_block_phone", false));
        Switch toggleCSilent = findViewById(R.id.switchSilent);
        toggleCSilent.setChecked(mPrefs.getBoolean("headache_mode_c_silent", false));
        Switch toggleCBrightness = findViewById(R.id.switchBrightness);
        toggleCBrightness.setChecked(mPrefs.getBoolean("headache_mode_c_brightness", false));

//        Switch toggleSwitch =

        // prompt the user for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                int originalBrightness = System.getInt(getContentResolver(), System.SCREEN_BRIGHTNESS, 0);
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

            if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, 1);
            }

            // TODO SMS
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

        toggleCSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    ed.putBoolean("headache_mode_c_sms", true);
                    ed.apply();
                } else {
                    // The toggle is disabled
                    ed.putBoolean("headache_mode_c_sms", false);
                    ed.apply();
                }
            }
        });

        toggleCBlockCalls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    ed.putBoolean("headache_mode_c_sms", true);
                    ed.apply();
                } else {
                    // The toggle is disabled
                    ed.putBoolean("headache_mode_c_sms", false);
                    ed.apply();
                }
            }
        });

        toggleCSilent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    ed.putBoolean("headache_mode_c_silent", true);
                    ed.apply();
                } else {
                    // The toggle is disabled
                    ed.putBoolean("headache_mode_c_silent", false);
                    ed.apply();
                }
            }
        });


        toggleCBrightness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    ed.putBoolean("headache_mode_c_brightness", true);
                    ed.apply();
                } else {
                    // The toggle is disabled
                    ed.putBoolean("headache_mode_c_brightness", false);
                    ed.apply();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences mPrefs = this.getSharedPreferences("dnd_mode", MODE_PRIVATE);
        Log.d("dnd", "Local storage says " + mPrefs.getBoolean("headache_mode", false));

        if(!mPrefs.getBoolean("headache_mode", false)) {
            if (phoneBlocker != null) {
                unregisterReceiver(phoneBlocker);
                phoneBlocker = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        SharedPreferences mPrefs = this.getSharedPreferences("dnd_mode", MODE_PRIVATE);
        Log.d("dnd", "Local storage says " + mPrefs.getBoolean("headache_mode", false));

        if (phoneBlocker != null) {
            unregisterReceiver(phoneBlocker);
            phoneBlocker = null;
        }

        super.onDestroy();
    }

    @Override
    protected  void onResume() {
        super.onResume();

        Bundle extras = this.getIntent().getExtras();
        SharedPreferences mPrefs = this.getSharedPreferences("dnd_mode", MODE_PRIVATE);
        Log.d("dnd", "Local storage in resume says " + mPrefs.getBoolean("headache_mode", false));

        if(extras != null) {
            if(extras.getBoolean("TURN_OFF_HEADACHE_MODE")) {
                SharedPreferences.Editor edd = mPrefs.edit();
                edd.putBoolean("headache_mode", false);
                edd.apply();
                Switch toggle = (Switch) findViewById(R.id.switch1);
                toggle.setChecked(false);
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

                //// Change up UI
                Switch toggleCSms = findViewById(R.id.switchSMS);
                toggleCSms.setEnabled(false);
                Switch toggleCBlockCalls = findViewById(R.id.switchBlockCalls);
                toggleCBlockCalls.setEnabled(false);
                Switch toggleCSilent = findViewById(R.id.switchSilent);
                toggleCSilent.setEnabled(false);
                Switch toggleCBrightness = findViewById(R.id.switchBrightness);
                toggleCBrightness.setEnabled(false);

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
                    CharSequence name = "Default";
                    String description = "Default notification group for the headache app.";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("default", name, importance);
                    channel.setDescription(description);
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

                String textTitle = "Feel any better yet?";
                String textContent = "Remember to switch off the headache mode when you are recovered.";

                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent intent = PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent turnOffIntent = new Intent(this, MainActivity.class);
                turnOffIntent.putExtra("TURN_OFF_HEADACHE_MODE", true);
                PendingIntent turnOffHeadacheModeIntent = PendingIntent.getActivity(this, 0, turnOffIntent, PendingIntent.FLAG_CANCEL_CURRENT);

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

                if(mPrefs.getBoolean("headache_mode_c_brightness", false)) {
                    // Dim the screen
                    int brightness = 0;
                    System.putInt(getContentResolver(), System.SCREEN_BRIGHTNESS, brightness);
                }

                if(mPrefs.getBoolean("headache_mode_c_silent", false)) {
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
                }

                if(mPrefs.getBoolean("headache_mode_c_block_phone", false)) {
                    // Block phone calls
                    phoneBlocker = new IncomingCallReceiver();
                    IntentFilter phoneBlFilter = new IntentFilter();
                    phoneBlFilter.addAction("android.intent.action.PHONE_STATE");
                    registerReceiver(phoneBlocker, phoneBlFilter);
                }

            } else if (!mPrefs.getBoolean("headache_mode", false) && !enabled) { // dnd mode is not enabled

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

                Switch toggleCSms = findViewById(R.id.switchSMS);
                toggleCSms.setEnabled(true);
                Switch toggleCBlockCalls = findViewById(R.id.switchBlockCalls);
                toggleCBlockCalls.setEnabled(true);
                Switch toggleCSilent = findViewById(R.id.switchSilent);
                toggleCSilent.setEnabled(true);
                Switch toggleCBrightness = findViewById(R.id.switchBrightness);
                toggleCBrightness.setEnabled(true);

                // Cancel the notification to turn off headache mode
                mNotificationManager.cancel(1);

                if (phoneBlocker != null) {
                    unregisterReceiver(phoneBlocker);
                    phoneBlocker = null;
                }

                if(mPrefs.getBoolean("headache_mode_c_brightness", false)) {
                    // Set the brightness up again
                    System.putInt(getContentResolver(), System.SCREEN_BRIGHTNESS, 70);
                }

                if(mPrefs.getBoolean("headache_mode_c_silent", false)) {
                    // Set media playback to loud
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2), 0);

                    // Set alarm and call volume up
                    if (ringerMode == 0) {
                        try {
                            mAudioManager.setRingerMode(1);
                        } catch (Exception e) {
                            Log.e("dnd", "Fatal error. Couldn't set ringer mode.");
                        }
                    }

                    // TODO unblock all notification
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                }
            }
        }

        // block all incoming phone calls



    }

    public void testHttpCall(View v) {
        // TODO

        Log.d("dnd", "http called");

        final TextView mButtonTextView = (TextView) findViewById(R.id.button1);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://www.google.com";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mButtonTextView.setText("Response is: "+ response.substring(0,20));
                        Log.d("dnd", response.substring(0,10));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mButtonTextView.setText("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

//        OkHttpClient client = new OkHttpClient();
//
//        TextView txtString;
//
//        String url = "https://reqres.in/api/user/2";
//
//        String run(url) throws IOException {
//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
//
//            try (Response response = client.newCall(request).execute()) {
////                return response.body().string();
//                return;
//            } catch (IOException e) {
//                Log.e("dnd", "IOException!" + e);
//            }
//        }
    }
}
