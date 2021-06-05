package chiro.work.roomquery;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

import static chiro.work.roomquery.RQApplication.defaultSP;

public class Api {
  private boolean login;
  private HashMap<String, String> cookies;
  private Map<String, String> defaultRequestHeader;
  private int timeout;
  private String hostName = "http://jw.hitsz.edu.cn";

  public String getHostName() {
    return hostName;
  }


  public Api() {
    login = false;
    timeout = 3000;
    cookies = new HashMap<>();
    defaultRequestHeader = new HashMap<String, String>();
    defaultRequestHeader.put("Accept", "*/*");
    defaultRequestHeader.put("Connection", "keep-alive");
    defaultRequestHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");

  }

  public void logOut() {
    login = false;
    cookies.clear();
    if (defaultSP != null) defaultSP.edit().putString("jw_cookie", null).apply();
  }

  public boolean login(String username, String password) throws Exceptions {
    try {
      Connection hc = Jsoup.connect("http://jw.hitsz.edu.cn/cas").headers(defaultRequestHeader);
      cookies.clear();
      cookies.putAll(hc.execute().cookies());
      String lt = null, execution = null, eventId = null;
      Document d = hc.cookies(cookies).get();
      lt = d.select("input[name=lt]").first().attr("value");
      execution = d.select("input[name=execution]").first().attr("value");
      eventId = d.select("input[name=_eventId]").first().attr("value");
      Connection c2 = Jsoup.connect("https://sso.hitsz.edu.cn:7002/cas/login?service=http%3A%2F%2Fjw.hitsz.edu.cn%2FcasLogin")
              .cookies(cookies)
              .headers(defaultRequestHeader)
              .ignoreContentType(true);
      Document page = c2.cookies(cookies)
              .data("username", username)
              .data("password", password)
              .data("lt", lt)
              .data("rememberMe", "on")
              .data("execution", execution)
              .data("_eventId", eventId).post();
      cookies.putAll(c2.response().cookies());
      login = page.toString().contains("qxdm");
      if (login) {
        if (defaultSP == null) return login;
        SharedPreferences.Editor edt = defaultSP.edit();
        edt.putString("jw_cookie", new Gson().toJson(cookies));
        edt.putString(username + ".password", password);
        edt.apply();
      } else logOut();
      return login;
    } catch (Exception e) {
      throw Exceptions.getConnectErrorExpection();
    }
  }
}
