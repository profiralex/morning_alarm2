package app.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import app.morningalarm.AlarmListActivity;
import app.morningalarm.R;
import app.utils.Constants;

/**
 * clasa ce se apeleaza cind este primit un GCM
 *
 * @author ALEXANDR
 */

public class GcmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent gcmIntent) {
        String newMessage = gcmIntent.getExtras().getString(Constants.EXTRA_MESSAGE);
        String type = gcmIntent.getExtras().getString(Constants.EXTRA_MESSAGE_TYPE);

        if (type.equals(Constants.TYPE_REGISTRATION)) {
            Toast.makeText(context, "Registration succeded", Toast.LENGTH_LONG).show();
        } else if (type.equals(Constants.TYPE_MESSAGE)){
            Toast.makeText(context, newMessage, Toast.LENGTH_LONG).show();
        } else if (type.equals(Constants.TYPE_ALARM)){

        }

        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.clock1)
                .setContentTitle(type)
                .setContentText(newMessage)
                .setAutoCancel(true);
        Intent intent = new Intent(context, GcmIntentService.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,intent,Intent.FLAG_ACTIVITY_NEW_TASK);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

        Log.d(Constants.TAG, "Received an gcm");
    }
}