package com.digitalsocietyschool.dss_team_brainwash.dss_migraine_dnd.do_not_disturb;

import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.System;
import android.telephony.SmsManager;
import android.view.ContextThemeWrapper;
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
import android.widget.Toast;
import android.support.v4.content.ContextCompat;
import android.widget.Switch;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity {

    private int brightness;

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    private int originalBrightness;

    private boolean dndModeEnabled = false;

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
        setContentView(R.layout.activity_main);

//        Settings.System.putInt(getContentResolver(),
//                Settings.System.SCREEN_BRIGHTNESS_MODE,
//                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
//
//        try {
//            brightness = System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//        } catch (android.provider.Settings.SettingNotFoundException e) {
//
//        }

        originalBrightness = System.getInt(getContentResolver(), System.SCREEN_BRIGHTNESS, 0);

        // prompt the user for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                Log.d("dnd", "got system permissions!");
                // Do stuff here
//                try {
//                    brightness = System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//                } catch (android.provider.Settings.SettingNotFoundException e) {
//
//                }
            } else {
                Log.d("dnd", "no sys permissions!");
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
//                intent.setData(Uri.parse("package:" + this.getClass().getSimpleName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

//                ActivityCompat.requestPermissions(MainActivity.this,
//                        new String[]{Manifest.permission.READ_CONTACTS},
//                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

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

        Switch toggle = (Switch) findViewById(R.id.switch1);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Log.d("dnd", "enabled");
                    dndModeEnabled = true;
                    toggleDoNotDisturb();
                } else {
                    // The toggle is disabled
                    Log.d("dnd", "disabled");
                    dndModeEnabled = false;
                    toggleDoNotDisturb();
                }
            }
        });



//        System.putInt(getContentResolver(), System.SCREEN_BRIGHTNESS, brightness);
//        LayoutParams layoutpars = getWindow().getAttributes();
//        layoutpars.screenBrightness = brightness / (float)255;
//        getWindow().setAttributes(layoutpars);
    }

    protected void toggleDoNotDisturb() {

        int currentBrightness = System.getInt(getContentResolver(), System.SCREEN_BRIGHTNESS, 0);

        // set the brightness to 0
        // TODO

        if(originalBrightness != 0 && currentBrightness != 0) {
            int brightness = 0;
            System.putInt(getContentResolver(), System.SCREEN_BRIGHTNESS, brightness);
        } else {
            System.putInt(getContentResolver(), System.SCREEN_BRIGHTNESS, originalBrightness);
        }

        // set the phone to "do not disturb mode"
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        int ringerMode = mAudioManager.getRingerMode();
        Log.d("dnd", "Ringer Mode:");
        Log.d("dnd", Integer.toString(ringerMode));

        // Check if the notification policy access has been granted for the app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            } else {

                if(dndModeEnabled) {
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
                    Resources res = getResources();
//                    int color = res.getColor(R.color.colorDark);
                    int color = ContextCompat.getColor(getApplicationContext(), R.color.colorDark);
                    ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
//                    mainLayout.setBackgroundColor(color);

                    ContextThemeWrapper ctx = new ContextThemeWrapper(getApplicationContext(), android.R.style.Theme_Black);
                    ctx.setTheme(android.R.style.Theme_Black);
                    recreate();

                } else { // dnd mode is not enabled
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                    if (ringerMode == 0) {
                        try {
                            mAudioManager.setRingerMode(1);
                        } catch (Exception e) {
                            Log.e("dnd", "Fatal error. Couldn't set ringer mode.");
                        }
                    }
                    // Set the background color
                    Resources res = getResources();
//                    int color = res.getColor(R.color.colorLight);
                    int color = ContextCompat.getColor(getApplicationContext(), R.color.colorLight);
                    ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
//                    mainLayout.setBackgroundColor(color);

                    ContextThemeWrapper ctx = new ContextThemeWrapper(getApplicationContext(), android.R.style.Theme_Light);
                    ctx.setTheme(R.style.Theme_DND_Light);
                    recreate();
                }
//                setTheme(android.R.style.Theme_Black);
//                recreate();
            }
        }

        // block all incoming phone calls


    }
}
