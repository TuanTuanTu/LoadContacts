package com.training.vungoctuan.contentproviderdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int CONTACT_LOADER_ID = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRequestPermissions();
    }

    private void setupCursorAdapter() {
        String[] uiBindFrom = {
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Data.DATA1};
        // View IDs which will have the respective column data inserted
        int[] uiBindTo = {R.id.tvName, R.id.ivImage, R.id.tvPhone};
        // Create the simple cursor adapter to use for our list
        // specifying the template to inflate (item_contact),
        mAdapter = new SimpleCursorAdapter(
            this, R.layout.item_contact,
            null, uiBindFrom, uiBindTo,
            0);
    }

    private LoaderManager.LoaderCallbacks<Cursor> contactsLoader =
        new LoaderManager.LoaderCallbacks<Cursor>() {
            // Create and return the actual cursor loader for the contacts data
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                // Define the columns to retrieve
                String[] projectionFields = new String[]{ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_URI,
                    ContactsContract.Data.DATA1};
                // Construct the loader
                CursorLoader cursorLoader = new CursorLoader(MainActivity.this,
                    ContactsContract.Data.CONTENT_URI, // URI
                    projectionFields, // projection fields
                    null, // the selection criteria
                    null, // the selection args
                    null // the sort order
                );
                // Return the loader for use
                return cursorLoader;
            }

            // When the system finishes retrieving the Cursor through the CursorLoader,
            // a call to the onLoadFinished() method takes place.
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                // The swapCursor() method assigns the new Cursor to the adapter
                mAdapter.swapCursor(cursor);
            }

            // This method is triggered when the loader is being reset
            // and the loader data is no longer available. Called if the data
            // in the provider changes and the Cursor becomes stale.
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                // Clear the Cursor we were using with another call to the swapCursor()
                mAdapter.swapCursor(null);
            }
        };

    //Request Permission
    void onRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else initContacts();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "Permission Granted!Thanks", Toast.LENGTH_SHORT).show();
                    initContacts();
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    onRequestPermissions();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void initContacts() {
        setContentView(R.layout.activity_main);
        setupCursorAdapter();
        ListView lvContacts = findViewById(R.id.lvContacts);
        lvContacts.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(CONTACT_LOADER_ID
            , new Bundle(), contactsLoader);
    }
}
