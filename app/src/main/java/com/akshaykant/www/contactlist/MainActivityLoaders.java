package com.akshaykant.www.contactlist;

import android.content.ContentResolver;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.akshaykant.www.contactlist.entity.Contact;
import com.akshaykant.www.contactlist.entity.ContactNumber;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static android.R.attr.id;
import static android.R.attr.name;

public class MainActivityLoaders extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView mRecyclerView;
    ContactsAdapter mContactsAdapter;

    TextView list;

    private int mPosition = RecyclerView.NO_POSITION;

    /*
   * This number will uniquely identify our Loader and is chosen arbitrarily. You can change this
   * to any number you like, as long as you use the same variable name.
   */
    private static final int CONTACTS_LOADER_ID = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_loaders);



        list = (TextView) findViewById(R.id.list);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvContacts);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);

          /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);
        mContactsAdapter = new ContactsAdapter(this);


        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mContactsAdapter);


        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(CONTACTS_LOADER_ID, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // This is called when a new Loader needs to be created.

          /*
     * Returns a new CursorLoader. The arguments are similar to
     * ContentResolver.query(), except for the Context argument, which supplies the location of
     * the ContentResolver to use.
     */
        return new CursorLoader(getApplicationContext(),
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //The framework will take care of closing the
        // old cursor once we return.


        List<Contact> contactList = getContactList(cursor);
        mContactsAdapter.swapCursor(contactList);

        // If mPosition equals RecyclerView.NO_POSITION, set it to 0
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        // Smooth scroll the RecyclerView to mPosition
        mRecyclerView.smoothScrollToPosition(mPosition);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        Log.i("f", loader.toString());
        mContactsAdapter.swapCursor(null);
    }


    private List<Contact> getContactList(Cursor cursor) {

        List<Contact> contactsList = null;
        Contact contact;
        Boolean isMobile;

        ContentResolver resolver = getContentResolver();

        if (cursor.getCount() > 0) {

            //Instantiating the list
            contactsList = new ArrayList<>();


            while (cursor.moveToNext()) {

                //Instantiating sets for every cursor
                contact = new Contact();

                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                String hasPhoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                //If mobile number is there for the contact
                if (hasPhoneNumber.equals("1")) {
                    Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =? ", new String[]{contactId}, null);

                    //Log
                    Log.i("MY INFO", id + " = " + name + " = " + imageUri + " = " + hasPhoneNumber);

                    HashSet<String> mobileNoSet = new HashSet<>();
                    HashSet<ContactNumber> contactNoSet = new HashSet<>();

                    String mobileNumber = null;
                    String countryCode = null;
                    String number = null;
                    String numberType = null;

                    //Set as false, for every phone cursor
                    isMobile = false;

                    while (phoneCursor.moveToNext()) {
                        //Set the values of the number for every phone cursor
                        mobileNumber = null;
                        countryCode = null;
                        number = null;
                        numberType = null;


                        ContactNumber contactNumber = new ContactNumber();

                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        Log.i("MY INFO PHONE", phoneNumber);

                        try {
                            PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
                            Phonenumber.PhoneNumber pn = pnu.parse(phoneNumber, Locale.getDefault().getCountry());
                            numberType = pnu.getNumberType(pn).name();
                            mobileNumber = pnu.format(pn, PhoneNumberUtil.PhoneNumberFormat.E164);
                            countryCode = "+" + pn.getCountryCode();
                            number = "" + pn.getNationalNumber();

                            if (numberType != null && numberType.equalsIgnoreCase("MOBILE")) {

                                isMobile = true;

                                contactNumber.setMobileNumber(mobileNumber);
                                contactNumber.setCountryCode(countryCode);
                                contactNumber.setNumber(number);

                                if (!mobileNoSet.contains(mobileNumber)) {
                                    mobileNoSet.add(mobileNumber);
                                    contactNoSet.add(contactNumber);
                                }


                            }


                        } catch (NumberParseException e) {
                            e.printStackTrace();
                        }

                    }
                    HashSet<String> emailSet = new HashSet<>();

                    //Get the email address only if phone number is valid and is of mobile type:
                    //Check for the numberHashset not to be null
                    if (!contactNoSet.isEmpty()) {
                        Cursor emailCursor = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " =? ", new String[]{contactId}, null);

                        while (emailCursor.moveToNext()) {

                            //Set the email adrress for every email cursor value
                            String emailAddress = null;

                            emailAddress = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)).trim();
                            if (!emailSet.contains(emailAddress)) {
                                emailSet.add(emailAddress);
                            }
                            //Log
                            Log.i("MY INFO EMAIL", emailAddress);
                            Log.i("------------", "-----------");
                        }
                        //close cursor
                        emailCursor.close();
                    }
                    //If cursor has any mobile number, then only add it to the list
                    if (isMobile) {

                        contact.setContactId(contactId);
                        contact.setDisplayName(displayName);
                        contact.setImageUri(imageUri);
                        contact.setContactNumber(contactNoSet);
                        contact.setEmail(emailSet);

                        contactsList.add(contact);
                    }
                    //cursor close
                    phoneCursor.close();
                }
            }
            //Log
            Log.i("MYLIST LIST", contactsList.toString());
        }

        return contactsList;
    }
}
