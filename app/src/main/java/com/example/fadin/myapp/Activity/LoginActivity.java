package com.example.fadin.myapp.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fadin.myapp.R;
import com.example.fadin.myapp.common.DataBaseHelper;
import com.example.fadin.myapp.common.FPWDDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     *
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private final String login_url = "http://188.131.169.241:8080/Project/Account/Login";
    // UI references.
    private AutoCompleteTextView usernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button btn_reg;
    private Button btn_fpw;
    OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        try {
            DataBaseHelper dbHelper5 = new DataBaseHelper(LoginActivity.this, "test_db");
            SQLiteDatabase db5 = dbHelper5.getReadableDatabase();
            //创建游标对象
            Cursor cursor = db5.query("user", new String[]{"name","password"}, null, null, null, null, null);
            //利用游标遍历所有数据对象
            Log.d("READ","RRRRRRRRR");
            while(cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String password = cursor.getString(cursor.getColumnIndex("password"));
                //日志打印输出
                Log.i("READDB","query-->"+name+" "+password);
//                showProgress(true);
                mAuthTask = new UserLoginTask(name, password);
                mAuthTask.execute((Void) null);
            }

        }catch (Exception e){
            DataBaseHelper dbHelper1 = new DataBaseHelper(LoginActivity.this, "test_db");
            SQLiteDatabase db2 = dbHelper1.getReadableDatabase();

            Log.e("TARRR","DatabaseERROR");
        }
        setContentView(R.layout.activity_login);
        // Set up the login form.

        populateAutoComplete();

        init();
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
//        mEmailSignInButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
////                startActivity(intent);
//
//            }
//        });
        OnClickListener onclick = new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.email_sign_in_button:
                        Log.d("LOGIN","press sign in");
                        attemptLogin();
                        break;
                    case R.id.forget:
                        Intent i = new Intent();
                        i.setClass(LoginActivity.this, ForgetPwdActivity.class);
                        startActivityForResult(i,2);
                        break;
                    case R.id.register:
                        Intent in = new Intent();
                        in.setClass(LoginActivity.this, RegisterActivity.class);
                        startActivityForResult(in,1);
                        break;

                }
            }
        };
        btn_reg.setOnClickListener(onclick);
        btn_fpw.setOnClickListener(onclick);
        mEmailSignInButton.setOnClickListener(onclick);

    }

    public void init(){
        mPasswordView = (EditText) findViewById(R.id.password);
        usernameView = (AutoCompleteTextView) findViewById(R.id.si_username);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        btn_reg = findViewById(R.id.register);
        btn_fpw = findViewById(R.id.forget);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
            if (result==null){
                return;
            }
            usernameView.setText(result);
            Log.i("Return", result);
        }catch (Exception ee){
            ee.printStackTrace();
        }


    }
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(usernameView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        usernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = usernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if(TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            usernameView.setError(getString(R.string.error_invalid_email));
            focusView = usernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);

        }
    }
    private boolean isUsernameValid(String username) {
        return username.length() > 2 && username.equals(stringFilter(username));
    }
    public static String stringFilter(String str)throws PatternSyntaxException { // 只允许字母、数字和汉字      
        String regEx ="[^a-zA-Z0-9\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return  m.replaceAll("").trim();
    }
    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        usernameView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        String res = "";
        String getCallback(String url) throws IOException {
//        RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.code() != 200){
                    Looper.prepare();
                    Toast.makeText(LoginActivity.this,"NetWork Error,Please Check Your NetWork.",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return "";
                }
                return response.body().string();
            }
        }
        UserLoginTask(String username, String password) {
            mUsername = username;

            mPassword = password;

//            Runnable networktask = new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            };
//           new Thread(networktask).start();
        }

        @Override
        protected Boolean doInBackground(Void... params) {


            try {
                String login_url1 =login_url + "?username="+mUsername+"&password="+mPassword;
                res = getCallback(login_url1);
                Log.e("MRES", res);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
                // Simulate network access.


//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    return pieces[1].equals(mPassword);
//                }
//            }
            if (res.contains("-1")||res.equals("")){
                return false;
            }else {
                return true;

            }


        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                ContentValues values = new ContentValues();
                //像ContentValues中存放数据
                values.put("password", mPassword);
                values.put("name", mUsername);

                DataBaseHelper dbHelper3 = new DataBaseHelper(LoginActivity.this, "test_db");
                SQLiteDatabase db3 = dbHelper3.getWritableDatabase();
                dbHelper3.deleteAll(db3);
                //数据库执行插入命令
                db3.insert("user", null, values);

                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

