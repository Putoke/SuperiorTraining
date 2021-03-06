package com.training.superior.superiortraining.Controllers;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;

import com.training.superior.superiortraining.R;
import com.training.superior.superiortraining.Models.UserLoginTask;
import com.training.superior.superiortraining.Views.LoginView;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private LoginView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        view = new LoginView(this);
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(String email, String password) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        view.resetErrors();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            view.passwordError(getString(R.string.error_invalid_password));
            focusView = view.getPasswordView();
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            view.emailError(getString(R.string.error_field_required));
            focusView = view.getEmailView();
            cancel = true;
        } else if (!isEmailValid(email)) {
            view.emailError(getString(R.string.error_invalid_email));
            focusView = view.getEmailView();
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            view.showProgress(true, this);
            mAuthTask = new UserLoginTask(email, password, this);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public void loginResponse(boolean success) {
        mAuthTask = null;
        view.showProgress(false, this);

        if (success) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            view.emailError(getString(R.string.error_incorrect_password));
            view.getPasswordView().requestFocus();
        }
    }

    public void loginCancelled() {
        mAuthTask = null;
        view.showProgress(false, this);
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

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        view.setEmailAdapter(adapter);
    }
}



