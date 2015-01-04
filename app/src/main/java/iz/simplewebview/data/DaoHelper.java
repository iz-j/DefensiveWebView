package iz.simplewebview.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by tono on 2015/01/02.
 */
public final class DaoHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "SimpleWebView.db";
    private static final int DB_VER = 1;

    private static DaoHelper INSTANCE;

    synchronized public static DaoHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DaoHelper(context);
        }
        return INSTANCE;
    }

    public static SQLiteDatabase getWritableDB(Context context) {
        return getInstance(context).getWritableDatabase();
    }

    public static SQLiteDatabase getReadableDB(Context context) {
        return getInstance(context).getReadableDatabase();
    }

    public static void beginTransaction(Context context) {
        getInstance(context).getWritableDatabase().beginTransaction();
    }

    public static void commitTransaction(Context context) {
        getInstance(context).getWritableDatabase().setTransactionSuccessful();
        getInstance(context).getWritableDatabase().endTransaction();
    }

    public static SQLiteStatement compileStatement(Context context, String sql) {
        return getInstance(context).getWritableDatabase().compileStatement(sql);
    }

    private DaoHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FavoriteDao.CREATE);
        db.execSQL(SettingsDao.CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //onCreate(db);
    }
}

