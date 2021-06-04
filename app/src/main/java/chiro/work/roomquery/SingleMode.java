package chiro.work.roomquery;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class SingleMode extends AppWidgetProvider {
  static String TAG = "SingleMode";
  static String ACTION_CLICK_BACK = "chiro.work.SingleMode.ACTION_CLICK_BACK";

  static public String GetRoom(Context context, int appWidgetId) {
    // 获取SharedPreferences对象
    SharedPreferences sp = context.getSharedPreferences("SingleMode", Context.MODE_PRIVATE);
    String room = sp.getString("room_" + appWidgetId, "Null");
    Log.w("RQ", "Room Read " + room);
    return room;
  }

  static class WidgetInfo {
    String room = "Null";

    public void load(Context context, int appWidgetId) {
      this.room = GetRoom(context, appWidgetId);
    }
  }

  WidgetInfo info = new WidgetInfo();
  int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    String action = intent.getAction();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      int old = mAppWidgetId;
      mAppWidgetId = extras.getInt(
              AppWidgetManager.EXTRA_APPWIDGET_ID,
              AppWidgetManager.INVALID_APPWIDGET_ID);
      Log.w(TAG, "mAppWidgetId " + old + " => " + mAppWidgetId);
    }
    Log.w(TAG, String.format("Action(%d): %s", mAppWidgetId, action));
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.single_mode);
    if (action.equals(AppWidgetManager.ACTION_APPWIDGET_DELETED)) {
      if (mAppWidgetId != 0)
        SingleModeActivity.UpdateRoom(context, mAppWidgetId, null);
    } else if (action.equals(ACTION_CLICK_BACK)) {
      Log.w(TAG, "Clicking");
      context.getApplicationContext()
              .startActivity(new Intent(context, SingleModeActivity.class)
                      .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                      .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                      .putExtra("editing", true));
    } else if (action.equals(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED) ||
            action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
      if (mAppWidgetId == 0) return;
      Log.w(TAG, "Updating id=" + mAppWidgetId);
      info.load(context, mAppWidgetId);
      Log.w(TAG, "Setting text to " + info.room);
      views.setTextViewText(R.id.textViewRoom, info.room);
      Log.w(TAG, "Intent -> " + mAppWidgetId);
      Intent myIntent = new Intent(context, getClass())
              .setAction(ACTION_CLICK_BACK)
              .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
      PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
      // views.setOnClickPendingIntent(R.id.linearLayoutBack, pendingIntent);
      views.setOnClickPendingIntent(R.id.linearLayoutBack, pendingIntent);
      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      appWidgetManager.updateAppWidget(mAppWidgetId, views);
    }
  }

  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                              int appWidgetId) {

    Log.w(TAG, String.format("updateAppWidget(%d)", appWidgetId));
    // Construct the RemoteViews object
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.single_mode);
    views.setTextViewText(R.id.textViewRoom, GetRoom(context, appWidgetId));
    views.setOnClickPendingIntent(R.id.linearLayoutBack,
            PendingIntent.getBroadcast(context, 0,
                    new Intent(context, SingleMode.class)
                            .setAction(ACTION_CLICK_BACK)
                            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId), 0));

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // There may be multiple widgets active, so update all of them
    for (int appWidgetId : appWidgetIds) {
      Log.w(TAG, "onUpdate_" + appWidgetId);
//      // 获取SharedPreferences对象
//      SharedPreferences sp = context.getSharedPreferences("SingleMode", Context.MODE_PRIVATE);
//      String room = sp.getString("room_" + appWidgetId, "Null");
//      Log.w("RQ", room);
      info.load(context, appWidgetId);
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.single_mode);
      views.setTextViewText(R.id.textViewRoom, info.room);
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }

  @Override
  public void onEnabled(Context context) {
    // Enter relevant functionality for when the first widget is created
  }

  @Override
  public void onDisabled(Context context) {
    // Enter relevant functionality for when the last widget is disabled
  }
}