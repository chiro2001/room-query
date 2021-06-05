package chiro.work.roomquery;

import android.content.ContentValues;

import java.util.HashMap;

public class Data {
  /*
  * {
    XQJ: 4,
    XJ: 6,
    CDDM: 'T2413',
    JYBJ: null,
    PKJYBJ: '0',
    PKBJ: '排',
    JYSB: '0',
    PKSB: '0'
  }
  * */
  public static class ClassNode {
    int XQJ;
    int XJ;
    int building;
    String CDDM;  // 场地地名
    String JYBJ;  // 占用备注
    String PKJYBJ;
    String JYSB;
    String PKSB;

    ContentValues toContentValues() {
      ContentValues values = new ContentValues();
      values.put("CDDM", CDDM);
      values.put("JYBJ", JYBJ);
      values.put("PKJYBJ", PKJYBJ);
      values.put("JYSB", JYSB);
      values.put("PKSB", PKSB);
      values.put("XQJ", XQJ);
      values.put("XJ", XJ);
      values.put("building", building);
      return values;
    }
  }

  public static class BuildingNode {
    String MC;
    String DM;

    ContentValues toContentValues() {
      ContentValues values = new ContentValues();
      values.put("MC", MC);
      values.put("DM", DM);
      return values;
    }
  }

  public static class CookieJson {
    String JSESSIONID;
    String route;
    String CASPRIVACY;
    String CASTGC;

    public HashMap<String, String> toHashMap() {
      HashMap<String, String> res = new HashMap<>();
      res.put("JSESSIONID", JSESSIONID);
      res.put("route", route);
      res.put("CASPRIVACY", CASPRIVACY);
      res.put("CASTGC", CASTGC);
      return res;
    }
  }
}
