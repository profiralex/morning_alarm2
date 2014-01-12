package app.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import app.database.AlarmDbUtilities;
import app.morningalarm.R;
import app.utils.Constants;
import app.utils.Person;

/**
 * clasa ce se apeleaza cind este primit un GCM
 *
 * @author ALEXANDR
 */

public class GcmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent gcmIntent) {
        String newMessage = gcmIntent.getStringExtra(Constants.EXTRA_MESSAGE);
        String type = gcmIntent.getStringExtra(Constants.EXTRA_MESSAGE_TYPE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        if (type.equals(Constants.TYPE_REGISTRATION)) {
            //Toast.makeText(context, "Registration succeded", Toast.LENGTH_LONG).show();
        } else if (type.equals(Constants.TYPE_MESSAGE)) {
            //Toast.makeText(context, newMessage, Toast.LENGTH_LONG).show();
        } else if (type.equals(Constants.TYPE_REQUEST)) {
            newMessage = "Request from " + gcmIntent.getStringExtra("sender_email");
            Intent intent = new Intent(context, GcmIntentService.class);
            intent.putExtras(gcmIntent.getExtras());

            PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pi);
        }else if(type.equals("response")){
            String response = gcmIntent.getStringExtra("response");
            String email = gcmIntent.getStringExtra("sender_email");
            String groupId = gcmIntent.getStringExtra("group_id");
            newMessage = email +" accepted your request for alarm";
            Person person = AlarmDbUtilities.fetchPerson(context, email, groupId);
            if(response.equals("ok")){
                person.setAccepted(true);
                AlarmDbUtilities.updatePerson(context, person);
            }else{
                AlarmDbUtilities.removePerson(context, email, person.getGroupId());
            }

        }

        mBuilder.setSmallIcon(R.drawable.clock1)
                .setContentTitle(type)
                .setContentText(newMessage)
                .setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

        Log.d(Constants.TAG, "Received an gcm");
    }
}