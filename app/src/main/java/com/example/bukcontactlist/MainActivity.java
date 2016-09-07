package com.example.bukcontactlist;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ListView listView;
    private ItemArrayAdapter itemArrayAdapter;
    EditText editSearch;
    CheckBox allCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b_load = (Button) findViewById(R.id.button_load);
        b_load.setOnClickListener(mClickListener);
        Button b_delete = (Button) findViewById(R.id.button_delete);
        b_delete.setOnClickListener(mClickListener);

        listView = (ListView) findViewById(R.id.listView);
        itemArrayAdapter = new ItemArrayAdapter(getApplicationContext(), R.layout.item_layout);

        Parcelable state = listView.onSaveInstanceState();
        listView.setAdapter(itemArrayAdapter);
        listView.onRestoreInstanceState(state);

        InputStream inputStream = getResources().openRawResource(R.raw.contacts);
        CSVFile csvFile = new CSVFile(inputStream);
        List<String[]> scoreList = csvFile.read();

        itemArrayAdapter.initChecked(scoreList.size());

        for (String[] scoreData : scoreList) {
            itemArrayAdapter.add(scoreData);
        }

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemArrayAdapter.setChecked(position);
                itemArrayAdapter.notifyDataSetChanged();
            }
        });

        allCheck = (CheckBox) findViewById(R.id.allcheck);
        allCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    itemArrayAdapter.setAllChecked(true);
                } else {
                    itemArrayAdapter.setAllChecked(false);
                }
                itemArrayAdapter.notifyDataSetChanged();
            }
        });

        editSearch = (EditText) findViewById(R.id.search);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                itemArrayAdapter.getFilter().filter(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int cntInserted, cntDeleted;
            int tempSize;
            switch (v.getId()) {
                case R.id.button_load:
                    cntInserted = 0;
                    tempSize = itemArrayAdapter.getCount();
                    for (int a = 1; a < tempSize; a++) {
                        String[] tempitems = itemArrayAdapter.getItem(a);

                        if (itemArrayAdapter.getChecked(a)) {
                            if (!contactExists(getApplicationContext(), tempitems[1])) {
                                addContact(getApplicationContext(), tempitems[0], "", tempitems[1], "",
                                        "", "", tempitems[2], tempitems[3]);
                                cntInserted++;
                            }
                        }
                    }

                    Toast.makeText(getApplicationContext(), "Complete : " + cntInserted + " inserted.",
                            Toast.LENGTH_SHORT).show();
                    break;

                case R.id.button_delete:
                    cntDeleted = 0;
                    tempSize = itemArrayAdapter.getCount();
                    for (int a = 1; a < tempSize; a++) {
                        String[] tempitems = itemArrayAdapter.getItem(a);

                        if (itemArrayAdapter.getChecked(a)) {
                            if (contactExists(getApplicationContext(), tempitems[1])) {
                                deleteContact(getApplicationContext(), tempitems[1], tempitems[0]);
                                cntDeleted++;
                            }
                        }
                    }

                    Toast.makeText(getApplicationContext(), "Complete : " + cntDeleted + " deleted",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void addContact(Context ctx, String displayname, String homenumber, String mobilenumber,
                            String worknumber, String homeemail, String workemail, String companyname,
                            String jobtitle) {
        String DisplayName = displayname;
        String MobileNumber = homenumber;
        String HomeNumber = mobilenumber;
        String WorkNumber = worknumber;
        String homeemailID = homeemail;
        String workemailID = workemail;
        String company = companyname;
        String jobTitle = jobtitle;
        ArrayList<ContentProviderOperation> contentProviderOperation = new ArrayList<ContentProviderOperation>();

        contentProviderOperation
                .add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        // ------------------------------------------------------ Names
        if (DisplayName != null) {
            contentProviderOperation
                    .add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                    DisplayName)
                            .build());
        }

        // ------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            contentProviderOperation
                    .add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());
        }

        // ------------------------------------------------------ Home Numbers
        if (HomeNumber != null) {
            contentProviderOperation
                    .add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                            .build());
        }

        // ------------------------------------------------------ Work Numbers
        if (WorkNumber != null) {
            contentProviderOperation
                    .add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                            .build());
        }

        // ------------------------------------------------------ workEmail
        if (workemailID != null) {
            contentProviderOperation
                    .add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, workemailID)
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                                    ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                            .build());
        }
        // ------------------------------------------------------ homeEmail
        if (homeemailID != null) {
            contentProviderOperation
                    .add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, homeemailID)
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                                    ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                            .build());
        }
        // ------------------------------------------------------ Organization
        if (!company.equals("") && !jobTitle.equals("")) {
            contentProviderOperation.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
                            ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
                            ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }
        // Asking the Contact provider to create a new contact
        try {
            ctx.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
                    contentProviderOperation);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ctx, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean contactExists(Context context, String number) {
        Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] mPhoneNumberProjection = {PhoneLookup._ID, PhoneLookup.NUMBER,
                PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null,
                null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    public static boolean deleteContact(Context ctx, String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME))
                            .equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(
                                cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI,
                                lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);
                        return true;
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return false;
    }
}
