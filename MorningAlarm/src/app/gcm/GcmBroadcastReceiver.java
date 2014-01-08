package app.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import app.utils.Constants;

/**
 * clasa ce se apeleaza cind este primit un GCM
 *
 * @author ALEXANDR
 */

public class GcmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String newMessage = intent.getExtras().getString(Constants.EXTRA_MESSAGE);
        String type = intent.getExtras().getString(Constants.EXTRA_MESSAGE_TYPE);

        if (type.equals(Constants.TYPE_REGISTRATION)) {
            Toast.makeText(context, "Registration succeded", Toast.LENGTH_LONG).show();
        } else if (type.equals(Constants.TYPE_MESSAGE)){
            Toast.makeText(context, newMessage, Toast.LENGTH_LONG).show();
        } else if (type.equals(Constants.TYPE_ALARM)){

        }

        Log.d(Constants.TAG, "Received an gcm");
    }
}