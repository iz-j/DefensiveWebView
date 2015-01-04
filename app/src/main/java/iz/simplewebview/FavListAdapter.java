package iz.simplewebview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import iz.simplewebview.data.Favorite;
import iz.simplewebview.data.FavoriteDao;
import iz.simplewebview.data.Settings;
import iz.simplewebview.data.SettingsDao;

/**
 * Created by tono on 2015/01/02.
 */
public class FavListAdapter extends ArrayAdapter<Favorite> {
    private final LayoutInflater layoutInflater;
    private final FavoriteDao favoriteDao = new FavoriteDao();
    private final SettingsDao settingsDao = new SettingsDao();

    public FavListAdapter(Context context, int resource) {
        super(context, resource);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void load() {
        clear();

        final Settings settings = settingsDao.selectBy(getContext());
        Favorite.HOME.url = settings.homeUrl;

        add(Favorite.HOME);
        addAll(favoriteDao.selectBy(getContext()));

        Log.d("APP", "Favorite loaded. size = " + getCount());
    }

    private class ViewHolder {
        private final ImageView imgFav;
        private final TextView txtFav;

        private ViewHolder(View view) {
            imgFav = (ImageView) view.findViewById(R.id.imgFav);
            txtFav = (TextView) view.findViewById(R.id.txtFav);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Favorite fav = (Favorite) getItem(position);

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fav_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (fav == Favorite.HOME) {
            viewHolder.imgFav.setImageResource(R.drawable.ic_home);
        } else {
            if (fav.important) {
                viewHolder.imgFav.setImageResource(R.drawable.ic_fav_important);
            } else {
                viewHolder.imgFav.setImageResource(R.drawable.ic_fav_normal);
            }
        }
        viewHolder.txtFav.setText(fav.name);

        return convertView;
    }
}
