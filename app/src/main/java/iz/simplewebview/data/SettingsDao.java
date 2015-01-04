package iz.simplewebview.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by tono on 2015/01/02.
 */
public class SettingsDao {
    static final String CREATE = new StringBuilder()
            .append("CREATE TABLE Settings (")
            .append(" homeUrl TEXT,")
            .append(" password TEXT")
            .append(")")
            .toString();

    public Settings selectBy(Context context) {
        final Settings settings = new Settings();
        Cursor c = DaoHelper.getReadableDB(context).rawQuery("SELECT * FROM Settings", new String[]{});
        if (c.moveToFirst()) {
            settings.homeUrl = c.getString(0);
            settings.password = c.getString(1);
        }
        return settings;
    }

    public void merge(Context context, Settings settings) {
        DaoHelper.getWritableDB(context).delete("Settings", null, null);
        final ContentValues values = new ContentValues();
        values.put("homeUrl", settings.homeUrl);
        values.put("password", settings.password);
        DaoHelper.getWritableDB(context).insert("Settings", null, values);
    }

}
