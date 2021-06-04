package chiro.work.roomquery;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;

import java.security.Signature;

public class SingleModeActivity extends AppCompatActivity {

  static public void UpdateRoom(Context context, int appWidgetId, String room) {
    SharedPreferences sp = context.getSharedPreferences("SingleMode", MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    editor.putString("room_" + appWidgetId, room);
    editor.apply();
    Log.w("RQ", "Room Write " + room);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_single_mode);

    EditText editText = findViewById(R.id.editTextText);
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    int mAppWidgetId = extras != null ? extras.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID) : AppWidgetManager.INVALID_APPWIDGET_ID;
    SharedPreferences sp = getSharedPreferences("SingleMode", MODE_PRIVATE);
    if (!sp.getString("room_" + mAppWidgetId, "Null").equals("Null")) {
      editText.setText(sp.getString("room_" + mAppWidgetId, "Null"));
    } else editText.setText("");
    Button button = findViewById(R.id.button);
    button.setOnClickListener(view -> {
      String text = editText.getText().toString();
      Context context = view.getContext();
      Intent mIntent = getIntent();
      Bundle mExtras = mIntent.getExtras();
      if (mExtras == null) return;
      boolean editing = mExtras.getBoolean("editing");
      UpdateRoom(context, mAppWidgetId, text);
      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      RemoteViews views = new RemoteViews(context.getPackageName(),
              R.layout.single_mode);
      appWidgetManager.updateAppWidget(mAppWidgetId, views);
      if (editing) {
        SingleMode.updateAppWidget(getApplicationContext(), AppWidgetManager.getInstance(this), mAppWidgetId);
      } else {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
      }
      finish();
    });
  }
}