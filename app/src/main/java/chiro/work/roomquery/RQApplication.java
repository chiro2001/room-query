package chiro.work.roomquery;

import android.app.Application;
import android.content.SharedPreferences;

public class RQApplication extends Application {
  public static SharedPreferences defaultSP;
  public static Api api;

  @Override
  public void onCreate() {
    super.onCreate();
    defaultSP = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
    api = new Api();
  }
}
