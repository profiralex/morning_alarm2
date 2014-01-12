package app.gcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import app.morningalarm.R;
import app.network.ServerRequestComposer;
import app.utils.Constants;

/**
 * Created by alexandr on 1/12/14.
 */
public class GcmActivity extends Activity{

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(Constants.TAG, "activitate pornita");
        Intent intent = getIntent();

        String type = intent.getExtras().getString(Constants.EXTRA_MESSAGE_TYPE);


        if (type.equals(Constants.TYPE_REQUEST)) {

            final String senderEmail = intent.getExtras().getString("sender_email");
            String groupMessage = intent.getExtras().getString("message");
            final String groupId = intent.getExtras().getString("group_id");

            new AlertDialog.Builder(this)
                    .setTitle("You recived an request to register for alarm from " + senderEmail)
                    .setMessage(groupMessage)
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendResponseInBackground(senderEmail, groupId, "ok");
                            dialog.cancel();
                            GcmActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Refuse", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendResponseInBackground(senderEmail, groupId, "no");
                            dialog.cancel();
                            GcmActivity.this.finish();
                        }
                    })
                    .setIcon(R.drawable.clock1)
                    .show();
        }
    }

    private void sendResponseInBackground(final String senderEmail, final String groupId, final String response){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    ServerRequestComposer.sendResponseToPerson(senderEmail, groupId, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String v) {
                //TODO
            }
        }.execute(null, null, null);
    }
}
