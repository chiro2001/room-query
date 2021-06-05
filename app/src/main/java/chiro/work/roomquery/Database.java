package chiro.work.roomquery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
  public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
    super(context, name, factory, version);
  }

  public void reBase(SQLiteDatabase db) {
    // 创建数据库sql语句并执行
    String sql = null;
    sql = "drop table if exists class_node;";
    db.execSQL(sql);
    sql = "drop table if exists building_list;";
    db.execSQL(sql);
    sql = "create table class_node(" +
            "id integer primary key autoincrement," +
            "CDDM varchar(20)," +
            "JYBJ varchar(20)," +
            "PKJYBJ varchar(20)," +
            "JYSB varchar(20)," +
            "PKSB varchar(20)," +
            "building integer," +
            "XQJ integer," +
            "XJ integer)";
    db.execSQL(sql);
    sql = "create table building_list(" +
            "id integer primary key autoincrement," +
            "MC varchar(32)," +
            "DM varchar(20))";
    db.execSQL(sql);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    reBase(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}

