package iz.simplewebview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import iz.simplewebview.data.Favorite;
import iz.simplewebview.data.FavoriteDao;


public class FavActivity extends ActionBarActivity {

    private long mId;

    private EditText mNameEdit;
    private CheckBox mImportantCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        getSupportActionBar().hide();

        mId = getIntent().getLongExtra("id", 0L);
        final Favorite f = new FavoriteDao().selectBy(getApplicationContext(), mId);

        mNameEdit = (EditText) findViewById(R.id.edtName);
        mNameEdit.setText(f.name);

        mImportantCheckBox = (CheckBox) findViewById(R.id.cbImportant);
        mImportantCheckBox.setChecked(f.important);

        ((Button) findViewById(R.id.btnUpdate)).setOnClickListener(mUpdateButtonClickListener);
        ((Button) findViewById(R.id.btnDelete)).setOnClickListener(mDeleteButtonClickListener);
    }

    private final View.OnClickListener mUpdateButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String name = mNameEdit.getText().toString();

            if (name.length() == 0) {
                Toast.makeText(getApplicationContext(), R.string.name_required, Toast.LENGTH_LONG).show();
                return;
            }

            final FavoriteDao dao = new FavoriteDao();
            final Favorite f = dao.selectBy(getApplicationContext(), mId);
            f.name = name;
            f.important = mImportantCheckBox.isChecked();
            dao.update(getApplicationContext(), f);

            setResult(RESULT_OK);
            finish();
        }
    };

    private final View.OnClickListener mDeleteButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final FavoriteDao dao = new FavoriteDao();
            dao.deleteBy(getApplicationContext(), mId);

            setResult(RESULT_OK);
            finish();
        }
    };
}
