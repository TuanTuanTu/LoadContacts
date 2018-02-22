package com.training.vungoctuan.contentproviderdemo;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.training.vungoctuan.contentproviderdemo.fragments.DetailsDialogFragment;

import static android.provider.ContactsContract.Contacts;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    public static final int CONTACT_LOADER_ID = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    public static final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 3;
    private SimpleCursorAdapter mAdapter;
    private ListView lvContacts;
    private long mContactId;
    private String mContactKey;
    private Uri mContactUri;
    private FragmentManager fm;
    private DetailsDialogFragment dialogFragment;
    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRequestPermissions();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define the columns to retrieve
        if (id == CONTACT_LOADER_ID) {
            String[] projectionFields = new String[]{Contacts._ID,
                Contacts.DISPLAY_NAME_PRIMARY,
                Contacts.Photo.PHOTO_URI};
            // Construct the loader
            CursorLoader cursorLoader = new CursorLoader(MainActivity.this,
                Contacts.CONTENT_URI, // URI
                projectionFields, // projection fields
                null, // the selection criteria
                null, // the selection args
                null // the sort order
            );
            // Return the loader for use
            return cursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "Permission READ_CONTACTS Granted!Thanks",
                        Toast.LENGTH_SHORT)
                        .show();
                    initContacts();
                } else {
                    Toast.makeText(
                        this,
                        "Permission READ_CONTACTS Denied!",
                        Toast.LENGTH_SHORT)
                        .show();
                    onRequestPermissions();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_PHONE_CALL: {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "Permission PHONE_CALL Granted!Thanks",
                        Toast.LENGTH_SHORT)
                        .show();
                    initContacts();
                } else {
                    Toast.makeText(
                        this,
                        "Permission PHONE_CALL Denied!",
                        Toast.LENGTH_SHORT)
                        .show();
                    onRequestPermissions();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
// Get the Cursor
        Cursor cursor = ((SimpleCursorAdapter) parent.getAdapter()).getCursor();
        // Move to the selected contact
        cursor.moveToPosition(position);
        // Get the _ID value
        mContactId = cursor.getLong(CONTACT_ID_INDEX);
        // Get the selected LOOKUP KEY
        mContactKey = cursor.getString(LOOKUP_KEY_INDEX);
        // Create the contact's content Uri
        Bundle args = new Bundle();
        args.putString("name", mContactKey);
        args.putString("phone", getContactPhoneNumber());
        dialogFragment.setArguments(args);
        dialogFragment.show(fm, "dialogFragment");
    }

    private void setupCursorAdapter() {
        String[] uiBindFrom = {
            Contacts.DISPLAY_NAME_PRIMARY,
            Contacts.Photo.PHOTO_URI};
        // View IDs which will have the respective column data inserted
        int[] uiBindTo = {R.id.tvName, R.id.ivImage};
        // Create the simple cursor adapter to use for our list
        // specifying the template to inflate (item_contact),
        mAdapter = new SimpleCursorAdapter(
            this,
            R.layout.item_contact,
            null,
            uiBindFrom,
            uiBindTo,
            0);
    }

    private void initContacts() {
        setContentView(R.layout.activity_main);
        setupCursorAdapter();
        //Init listview
        lvContacts = findViewById(R.id.lvContacts);
        lvContacts.setAdapter(mAdapter);
        lvContacts.setOnItemClickListener(this);
        getLoaderManager().initLoader(CONTACT_LOADER_ID
            , null, this);
        initFragment();
    }

    private void initFragment() {
        fm = getSupportFragmentManager();
        dialogFragment = DetailsDialogFragment.newInstance();
    }

    //Request Permission
    void onRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CALL_PHONE},
                MY_PERMISSIONS_REQUEST_PHONE_CALL);
        } else initContacts();
    }

    private String getContactPhoneNumber() {
        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone =
            getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                    ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{String.valueOf(mContactId)},
                null);
        if (cursorPhone.moveToFirst()) {
            return cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds
                .Phone.NUMBER));
        }
        cursorPhone.close();
        return "No Number";
    }
}
