package chiro.work.roomquery;

import java.util.HashMap;

public class Utils {
  static public String getCookieString(HashMap<String, String> cookies) {
    StringBuilder result = new StringBuilder();
    for (String key : cookies.keySet()) {
      result.append(String.format("%s=%s; ", key, cookies.get(key)));
    }
    return result.toString();
  }
}
