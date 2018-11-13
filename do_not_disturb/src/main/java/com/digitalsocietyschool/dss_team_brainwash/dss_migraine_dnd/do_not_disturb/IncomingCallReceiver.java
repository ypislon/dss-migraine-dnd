package com.digitalsocietyschool.dss_team_brainwash.dss_migraine_dnd.do_not_disturb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import java.lang.reflect.Method;
import com.android.internal.telephony.ITelephony;
import android.telephony.SmsManager;

public class IncomingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ITelephony telephonyService;
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Method m = tm.getClass().getDeclaredMethod("getITelephony");

                    m.setAccessible(true);
                    telephonyService = (ITelephony) m.invoke(tm);

                    if ((number != null)) {
                        telephonyService.endCall();
                        Toast.makeText(context, "Ending the call from: " + number, Toast.LENGTH_SHORT).show();
                        SmsManager manager = SmsManager.getDefault();
                        try {
                            // Permission is not granted
                            manager.sendTextMessage(number, null, "I have a migraine attack right now. Please contact me later.", null, null);
                        } catch (Exception e) {
                            Log.e("dnd", "Fatal: couldn't send SMS.");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(context, "Ring " + number, Toast.LENGTH_SHORT).show();

            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                Toast.makeText(context, "Answered " + number, Toast.LENGTH_SHORT).show();
            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){
                Toast.makeText(context, "Idle "+ number, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
