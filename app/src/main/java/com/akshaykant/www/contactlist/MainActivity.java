package com.akshaykant.www.contactlist;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    List<Contact> contactList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView list = (TextView) findViewById(R.id.list);
        long startTime = System.currentTimeMillis();
        contactList = getContactList();
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        list.setText(elapsedTime/1000.0 + " seconds");


    }

    private List<Contact> getContactList() {

        List<Contact> contactsList = null;
        Contact contact;
        Boolean isMobile;

        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

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

                        //TODO: Check for duplicate numbers by removing extra space and characters other than +. Also check numbers with 0 and +232022
                        //Log
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
        //Close the cursor
        cursor.close();

        return contactsList;
    }

}
