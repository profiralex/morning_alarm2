package app.morningalarm;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import app.database.AlarmDbUtilities;

/**
 * widget pentru ecran ce afiseaza starea alarmelor
 * @author ALEXANDR
 *
 */
public class MorningAlarmWidgetProvider extends AppWidgetProvider {

/**
* metoda creaza activitatea AlarmListActivity atuni
* cind widgetul de pe ecran a fost tastat.
*/
  @Override
  public void onReceive(Context context, Intent intent){
	  if(intent.getAction()==null){
		  Intent i = new Intent(context, AlarmListActivity.class);
		  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		  context.startActivity(i);
	  }else{
		  super.onReceive(context, intent);
	  }
  }
  
  /**
   * metoda se apeleaza atunci cind widgetul este creat 
   */
  @Override 
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
	  context.startService(new Intent(context, ToggleService.class));
  }
  /**
   * 
   * @param context
   * @param enabled
   * 
   * metoda este apelata pentru a seta starea imaginii din widget
   */
  public static void updateWidget(Context context, boolean enabled){
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.home_screen_widget);
		if(enabled==true){
			remoteViews.setImageViewResource(R.id.home_screen_iv, R.drawable.clock1);
		}else{
			remoteViews.setImageViewResource(R.id.home_screen_iv, R.drawable.clock2);
		}
		ComponentName thisWidget = new ComponentName(context, MorningAlarmWidgetProvider.class);
		appWidgetManager.updateAppWidget(thisWidget, remoteViews);
	}
  
  /**
   * 
   * @author ALEXANDR
   *Service-ul care va initializa widgetul
   */
  public static class ToggleService extends IntentService{

	/**
	 * serviciu ce se activeaza la startul widgetului
	 */
	public ToggleService() {
		super("MorningAlarmWidgetProvider$ToggleService");
	}

	@Override
	/**
	 * metoda ce face handle la serviciu
	 */
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		ComponentName me = new ComponentName(this,MorningAlarmWidgetProvider.class);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		appWidgetManager.updateAppWidget(me, buildUpdate(this));
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 * returneaza remoteView ce trebuie actualizat si seteaza onclicklistener pentru remoteView
	 */
	private RemoteViews buildUpdate(Context context){
		RemoteViews updateViews = new RemoteViews(context.getPackageName(),R.layout.home_screen_widget);
		int enabledAlarms = AlarmDbUtilities.fetchEnabledAlarms(this).size();
		Log.d("DEBUG_TAG", enabledAlarms+ "");
		if( enabledAlarms == 0){
			updateViews.setImageViewResource(R.id.home_screen_iv, R.drawable.clock2);
		}else{
			updateViews.setImageViewResource(R.id.home_screen_iv, R.drawable.clock1);
		}
		Intent i = new Intent(this, MorningAlarmWidgetProvider.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		updateViews.setOnClickPendingIntent(R.id.home_screen_iv, pi);
		
		return updateViews;
	}
	  
  }
}
