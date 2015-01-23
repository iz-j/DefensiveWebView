package iz.simplewebview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import iz.simplewebview.data.Favorite;
import iz.simplewebview.data.FavoriteDao;
import iz.simplewebview.data.Settings;
import iz.simplewebview.data.SettingsDao;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private ListView mDrawerList;
    private FavListAdapter mFavListAdapter;

    private GestureDetector mGestureDetector;

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Gesture
        mGestureDetector = new GestureDetector(this, mGestureListener);

        // お気に入りリスト
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mFavListAdapter = new FavListAdapter(getApplicationContext(), 0);
        mFavListAdapter.load();
        mDrawerList.setAdapter(mFavListAdapter);
        mDrawerList.setOnItemClickListener(mItemClickListener);
        mDrawerList.setOnItemLongClickListener(mItemLongClickListener);

        // WebView
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (mWebView.getUrl() == null || mWebView.getUrl().length() == 0) {
            mWebView.loadUrl(mFavListAdapter.getItem(0).url);
        }

        // ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFavListAdapter.load();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_add_fav:
                addFavorite();
                return true;
            case R.id.action_settings:
                final Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(i, 0);
                return true;
            case R.id.action_close:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addFavorite() {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.fav_add_dlg, null);

        final EditText editText = (EditText) view.findViewById(R.id.edtName);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setText(mWebView.getTitle());
        editText.selectAll();

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.cbLock);

        final AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)
                .setTitle(getResources().getString(R.string.enter_name))
                .setView(view)
                .setPositiveButton(getResources().getText(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String name = editText.getText().toString().trim();
                        if (name.length() == 0) {
                            return;
                        }

                        final Settings settings = new SettingsDao().selectBy(getApplicationContext());
                        if (checkBox.isChecked() && settings.password == null) {
                            Toast.makeText(getApplicationContext(), R.string.required_password, Toast.LENGTH_LONG).show();
                            return;
                        }

                        final Favorite f = new Favorite();
                        f.name = name;
                        f.url = mWebView.getUrl();
                        f.important = checkBox.isChecked();
                        new FavoriteDao().insert(getApplicationContext(), f);

                        mFavListAdapter.load();
                    }
                })
                .setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        dlg.show();
    }

    private final WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.endsWith(".mp3")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "audio/*");
                view.getContext().startActivity(intent);
                return true;
            } else if (url.endsWith(".mp4") || url.endsWith(".3gp")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "video/*");
                view.getContext().startActivity(intent);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            getSupportActionBar().setTitle(Html.fromHtml("<small>Loading...</small>"));
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            getSupportActionBar().setTitle("");
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String url) {
            Toast.makeText(MainActivity.this, R.string.page_error, Toast.LENGTH_LONG).show();
            getSupportActionBar().setTitle(Html.fromHtml("<small>Error</small>"));
        }
    };

    private interface OnPasswordMatchListner {
        void onMatch();
    }

    private void confirmPassword(final OnPasswordMatchListner listner) {
        final EditText editText = new EditText(getApplicationContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = new InputFilter.LengthFilter(4);
        editText.setFilters(inputFilters);
        editText.setTextColor(getResources().getColor(android.R.color.black));

        final AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.password)
                .setView(editText)
                .setPositiveButton(getResources().getText(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String password = editText.getText().toString().trim();
                        if (password == null || password.length() != 4) {
                            Toast.makeText(getApplicationContext(), R.string.password_wrong, Toast.LENGTH_LONG).show();
                            return;
                        }

                        final Settings settings = new SettingsDao().selectBy(getApplicationContext());
                        if (!password.equals(settings.password)) {
                            Toast.makeText(getApplicationContext(), R.string.password_wrong, Toast.LENGTH_LONG).show();
                            return;
                        }

                        listner.onMatch();
                    }
                })
                .setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        dlg.show();
    }

    private final AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Favorite fav = mFavListAdapter.getItem(position);
            Log.d("APP", "Selected fav = " + fav.toString());
            if (fav == null) {
                return;
            }

            if (fav.important) {
                confirmPassword(new OnPasswordMatchListner() {
                    @Override
                    public void onMatch() {
                        mWebView.loadUrl(fav.url);
                        mDrawerLayout.closeDrawers();
                    }
                });
            } else {
                mWebView.loadUrl(fav.url);
                mDrawerLayout.closeDrawers();
            }
        }
    };

    private final AdapterView.OnItemLongClickListener mItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final Favorite fav = mFavListAdapter.getItem(position);
            if (fav == null) {
                return false;
            }

            if (fav == Favorite.HOME) {
                final SettingsDao dao = new SettingsDao();
                final Settings settings = dao.selectBy(getApplicationContext());
                settings.homeUrl = mWebView.getUrl();
                dao.merge(getApplicationContext(), settings);
                Toast.makeText(getApplicationContext(), R.string.set_home, Toast.LENGTH_LONG).show();
                mFavListAdapter.load();
                return true;
            }

            if (fav.important) {
                confirmPassword(new OnPasswordMatchListner() {
                    @Override
                    public void onMatch() {
                        final Intent i = new Intent(getApplicationContext(), FavActivity.class);
                        i.putExtra("id", fav.id);
                        startActivityForResult(i, 0);
                    }
                });
            } else {
                final Intent i = new Intent(getApplicationContext(), FavActivity.class);
                i.putExtra("id", fav.id);
                startActivityForResult(i, 0);
            }

            return true;
        }
    };

    private final GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float dx = Math.abs(velocityX);
            float dy = Math.abs(velocityY);

            if (dx < dy) {
                return false;
            }

            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                return false;
            }

            if(dx > 2500) {
                if(velocityX > 0) {
                    mDrawerLayout.openDrawer(mDrawerList);
                } else {
                    finish();
                }
                return true;
            }
            return false;
        }
    };
}
