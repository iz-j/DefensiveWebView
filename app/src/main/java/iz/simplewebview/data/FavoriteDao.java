package iz.simplewebview.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tono on 2015/01/02.
 */
public class FavoriteDao {
    static final String CREATE = new StringBuilder()
            .append("CREATE TABLE Favorite (")
            .append(" id INTEGER PRIMARY KEY AUTOINCREMENT,")
            .append(" name TEXT,")
            .append(" url TEXT,")
            .append(" important INTEGER")
            .append(")")
            .toString();

    public List<Favorite> selectBy(Context context) {
        final List<Favorite> favorites = new ArrayList<>();
        Cursor c = DaoHelper.getReadableDB(context).rawQuery("SELECT * FROM Favorite ORDER BY name", new String[]{});
        while (c.moveToNext()) {
            favorites.add(mapRow(c));
        }
        return favorites;
    }

    public long insert(Context context, Favorite f) {
        final ContentValues values = new ContentValues();
        values.put("name", f.name);
        values.put("url", f.url);
        values.put("important",f.important);
        final long id = DaoHelper.getWritableDB(context).insert("Favorite", null, values);
        f.id = id;
        Log.d("APP", "Favorite inserted. " + f.toString());
        return id;
    }

    public void deleteImportants(Context context) {
        DaoHelper.getWritableDB(context).delete("Favorite", "important = 1", null);
    }

    public void deleteBy(Context context, long id) {
        DaoHelper.getWritableDB(context).delete("Favorite", "id = ?", new String[]{String.valueOf(id)});
    }

    public Favorite selectBy(Context context, long id) {
        Cursor c = DaoHelper.getReadableDB(context).rawQuery("SELECT * FROM Favorite WHERE id = ?", new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            return mapRow(c);
        }
        return null;
    }

    public void update(Context context, Favorite f) {
        final ContentValues values = new ContentValues();
        values.put("name", f.name);
        values.put("url", f.url);
        values.put("important",f.important);
        DaoHelper.getWritableDB(context).update("Favorite", values, "id = ?", new String[]{String.valueOf(f.id)});
    }

    private Favorite mapRow(Cursor c) {
        final Favorite f = new Favorite();
        f.id = c.getLong(0);
        f.name = c.getString(1);
        f.url = c.getString(2);
        f.important = c.getInt(3) == 1;
        return f;
    }
}
