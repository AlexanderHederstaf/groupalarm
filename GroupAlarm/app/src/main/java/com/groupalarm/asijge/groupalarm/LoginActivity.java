package com.groupalarm.asijge.groupalarm;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via username/password.
 *
 * @author asijge
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    private static final String TAG = "LoginActivity";

    // Keep track of the login task to ensure we can cancel it if requested.
    private UserLoginTask mAuthTask = null;
    private UserRegistrationTask mRegTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mMessageBox;
    private Button mEmailSignInButton;
    private Button mEmailRegisterButton;


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mMessageBox = (TextView) findViewById(R.id.sign_in_messagebox);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(true);
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(true);
            }
        });

        mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(false);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Check if there is an active current user
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // If there is, step straight to MainActivity
            launchMainActivity();
        }
    }

    /**
     * Get the LoaderManager associated with this Activity to populate the autocomplete list
     */
    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(Boolean isLogin) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
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
            if (isLogin) {
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
            } else {
                mRegTask = new UserRegistrationTask(email, password);
                mRegTask.execute((Void) null);
            }
        }
    }

    /**
     * Check if email address is valid.
     *
     * @param email, the email string to check
     * @return true if string is at least 2 characters, false otherwise
     */
    private boolean isEmailValid(String email) {
        return email.length() > 2;
    }

    /**
     * Check if password is valid.
     *
     * @param password, the password string to check
     * @return true if length of password is at least 4 characters, false otherwise
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        // Set up autocomplete list for email addresses
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);

        // Check if there is an internet connection
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Log.d(TAG, "Internet: " + isConnected);

        if (!isConnected) {
            mEmailRegisterButton.setEnabled(false);
            mEmailSignInButton.setEnabled(false);
            mMessageBox.setText("You don't have an internet connection. Please check and retry login when connected!");
            mMessageBox.setError("");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // Required for the implementation of the LoaderCallbacks<Cursor> interface
        // Does nothing at moment
    }

    /**
     * Contacts user profile query interface.
     */
    private interface ProfileQuery {
        // Extract set of columns from the profile query results
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Add an email address collection to the email UI element.
     * For auto-complete functionality.
     *
     * @param emailAddressCollection
     */
    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            Log.d(TAG, mEmail + mPassword);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                ParseUser.logIn(mEmail, mPassword);
                return true;
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            // After asynchronus task has been executed
            if (success) {
                launchMainActivity();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private ParseUser newUser;

        UserRegistrationTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            newUser = new ParseUser();
            newUser.setUsername(mEmail);
            newUser.setPassword(mPassword);

            try {
                newUser.signUp();
                Log.d(TAG, "Sign up completed.");
                return true;
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, "Sign up error.");
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegTask = null;
            showProgress(false);

            if (success) {
                mEmailView.setText(mEmail);
                mPasswordView.setText(mPassword);
                mMessageBox.setText("Your registration was successful! You can now log on.");
                mMessageBox.setError(null);
            } else {
                mMessageBox.setText("Oops! Something went wrong with your registration.");
                mMessageBox.setError("");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Launches the applications Main Activity.
     *
     * Before Main activity is launched the installation of the app is associated
     * with the current logged in user to enable targeted notifications.
     */
    private void launchMainActivity() {
        // Associate the device with a user
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", ParseUser.getCurrentUser());

        try {
            installation.save();
            Log.d(TAG, "User added to installation");
        } catch (ParseException e) {
            Log.d(TAG, "Struggling to add user to installation!");
        }
        Log.d(TAG, "Installation user: " + ParseInstallation.getCurrentInstallation().get("user"));

        // Start MainActivity
        this.finish();
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(myIntent);
    }

    /**
     * Displays the Progress bar.
     *
     * @param show Show progress bar if true, don't show if false.
     */
    private void showProgress(boolean show) {
        ViewHelper.showProgress(show, mProgressView, mLoginFormView, this);
    }
}