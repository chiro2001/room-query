package chiro.work.roomquery;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static chiro.work.roomquery.RQApplication.api;
import static chiro.work.roomquery.RQApplication.defaultSP;

/**
 * Implementation of App Widget functionality.
 */
public class SingleMode extends AppWidgetProvider {
  static String TAG = "SingleMode";
  static String ACTION_CLICK_BACK = "chiro.work.SingleMode.ACTION_CLICK_BACK";
  static String ACTION_CLICK_UPDATE = "chiro.work.SingleMode.ACTION_CLICK_UPDATE";

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
    if (action.equals(ACTION_CLICK_UPDATE)) {
      Log.w(TAG, "ACTION_CLICK_UPDATE");
      new Thread(() -> {
        try {
          boolean hasLogin = api.login("200110619", "1352040930lxr#*");
          String cookiesJsonString = defaultSP.getString("jw_cookie", "{}");
          if (hasLogin) {
            Log.e(TAG, "Login: " + cookiesJsonString);
          } else {
            Log.e(TAG, "Login Error " + cookiesJsonString);
            return;
          }
          Data.CookieJson cookieJson = new Gson().fromJson(cookiesJsonString, Data.CookieJson.class);
          HashMap<String, String> cookieMap = cookieJson.toHashMap();
          String cookieString = Utils.getCookieString(cookieMap);
          Retrofit retrofit = new Retrofit.Builder()
                  .baseUrl("http://jw.hitsz.edu.cn/")
                  .addConverterFactory(GsonConverterFactory.create())
                  .build();

          ApiInterface apiInterface = retrofit.create(ApiInterface.class);

          Call<List<Data.BuildingNode>> buildingListData = apiInterface.getBuildingList(cookieString);
          buildingListData.enqueue(new Callback<List<Data.BuildingNode>>() {
            @Override
            public void onResponse(@NonNull Call<List<Data.BuildingNode>> call, @NonNull Response<List<Data.BuildingNode>> response) {
              List<Data.BuildingNode> buildingList = response.body();
              if (buildingList == null) {
                Log.e(TAG, "Cannot get building list");
                return;
              }
              Database dbHelper = new Database(context, "rq.db", null, 1);
              SQLiteDatabase db = dbHelper.getWritableDatabase();
              dbHelper.reBase(db);
              db.delete("building_list", "", new String[]{});
              for (Data.BuildingNode d : buildingList) {
                // Log.w(TAG, d.CDDM);
                ContentValues values = d.toContentValues();
                db.insert("building_list", null, values);
              }

              // pxn=2020-2021&pxq=2&dmmc=&xiaoqu=&jxl=14&cdlb=&zc=0000000000000000100000000000000000&wpksfxs=0&qsjsz=16&pageNum=1&pageSize=19
              for (Data.BuildingNode buildingNode : buildingList) {
                int building = Integer.parseInt(buildingNode.DM);
                Call<List<Data.ClassNode>> buildingData = apiInterface.getBuildingData(
                        // "2020-2021", 14, "0000000000000000100000000000000000",
                        "2020-2021", building, "1111111111111111111111111111111111111111111",
                        1, 160, 2, "", "", "",
                        1, 16);
                buildingData.enqueue(new Callback<List<Data.ClassNode>>() {
                  @Override
                  public void onResponse(@NonNull Call<List<Data.ClassNode>> call, @NonNull Response<List<Data.ClassNode>> response) {
                    List<Data.ClassNode> data = response.body();
                    if (data == null || data.isEmpty()) {
                      Log.w(TAG, "Got null response");
                      return;
                    }
                    Log.w(TAG, String.format("Building %d total %d classes", building, data.size()));
                    Database dbHelper = new Database(context, "rq.db", null, 1);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("class_node", "building = ?", new String[]{"" + building,});
                    for (Data.ClassNode d : data) {
                      // Log.w(TAG, d.CDDM);
                      ContentValues values = d.toContentValues();
                      db.insert("class_node", null, values);
                    }
                  }

                  @Override
                  public void onFailure(@NonNull Call<List<Data.ClassNode>> call, @NonNull Throwable t) {
                    Log.e(TAG, t.toString());
                  }
                });
              }
            }

            @Override
            public void onFailure(@NonNull Call<List<Data.BuildingNode>> call, @NonNull Throwable t) {
              Log.e(TAG, t.toString());
            }
          });
        } catch (Exceptions exceptions) {
          exceptions.printStackTrace();
          Log.e(TAG, exceptions.toString());
        }
      }).start();
    } else if (action.equals(AppWidgetManager.ACTION_APPWIDGET_DELETED)) {
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
    PendingIntent pendingIntentUpdate = PendingIntent
            .getBroadcast(context, 0, new Intent(context, SingleMode.class)
                    .setAction(ACTION_CLICK_UPDATE), 0);
    views.setOnClickPendingIntent(R.id.button2, pendingIntentUpdate);
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