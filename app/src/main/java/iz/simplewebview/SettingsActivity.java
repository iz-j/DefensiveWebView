package iz.simplewebview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import iz.simplewebview.data.FavoriteDao;
import iz.simplewebview.data.Settings;
import iz.simplewebview.data.SettingsDao;


public class SettingsActivity extends ActionBarActivity {

    private EditText mPasswordEdit;
    private Button mUpdateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        final SettingsDao dao = new SettingsDao();
        final Settings settings = dao.selectBy(getApplicationContext());

        mPasswordEdit = (EditText) findViewById(R.id.edtPassword);
        mPasswordEdit.setText(settings.password);

        mUpdateButton = (Button) findViewById(R.id.btnUpdate);
        mUpdateButton.setOnClickListener(mOkButtonOnClickListener);
    }

    private final View.OnClickListener mOkButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String password = mPasswordEdit.getText().toString();
            if (password == null || password.length() != 4) {
                Toast.makeText(getApplicationContext(), R.string.password_require_4, Toast.LENGTH_LONG).show();
                return;
            }

            final SettingsDao dao = new SettingsDao();
            final Settings settings = dao.selectBy(getApplicationContext());

            if (settings.password != null && settings.password.length() > 0) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle(R.string.confirm)
                        .setMessage(R.string.confirm_delete_importants)
                        .setPositiveButton(getResources().getText(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new FavoriteDao().deleteImportants(getApplicationContext());
                                settings.password = password;
                                dao.merge(getApplicationContext(), settings);
                                setResult(RESULT_OK);
                                finish();
                            }
                        })
                        .setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .create().show();
            } else {
                settings.password = password;
                dao.merge(getApplicationContext(), settings);
                setResult(RESULT_OK);
                finish();
            }
        }
    };

}
