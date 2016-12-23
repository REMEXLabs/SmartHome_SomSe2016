package hdm.csm.smarthome.phone.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.*;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.Toolbar;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import hdm.csm.smarthome.R;
import hdm.csm.smarthome.phone.OpenHabBridge;
import hdm.csm.smarthome.phone.database.DbSchema;
import hdm.csm.smarthome.phone.services.ContactBridge;


/**
 * Created by Michael on 29.05.2016.
 */
public class ContactsView extends ListActivity implements AppCompatCallback {
    static final int PICK_CONTACT=1;
    private hdm.csm.smarthome.phone.database.DbManager dbManager;
    public ArrayAdapter<String> mKontaktAdapter;
    private String[] NAMEN = new String[5];
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private SimpleCursorAdapter adapter;

    private static final String[] ANZEIGEN_SPALTEN = new String[]{
        DbSchema.NAME,
        DbSchema.MOBILNUMMER
    };

    private static final String[] DB_SUCHSPALTEN = new String[]{
            DbSchema.ID,
            DbSchema.NAME,
            DbSchema.MOBILNUMMER
    };

    private AppCompatDelegate delegate;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //let's create the delegate, passing the activity at both arguments (Activity, AppCompatCallback)
        delegate = AppCompatDelegate.create(this, this);

        //we need to call the onCreate() of the AppCompatDelegate
        delegate.onCreate(icicle);

        //we use the delegate to inflate the layout
        delegate.setContentView(R.layout.emercency_contacts_list);

        //Finally, let's add the Toolbar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_1);
        delegate.setSupportActionBar(toolbar);
        //dbManager = DbManager.getInstance(this);
        showEmergencyContacts();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
       // client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void showEmergencyContacts() {

        dbManager = hdm.csm.smarthome.phone.database.DbManager.getInstance(this);
         Cursor kontakte =
                dbManager.getWritableDatabase().query(DbSchema.TABLE_NAME, DB_SUCHSPALTEN, null, null, DbSchema.MOBILNUMMER, null, null, null);
        Log.d("QUERY", String.valueOf(kontakte.getCount()));
        startManagingCursor(kontakte);

        adapter = new SimpleCursorAdapter(
                this,android.R.layout.simple_list_item_2,kontakte,ANZEIGEN_SPALTEN, new int[]{android.R.id.text1, android.R.id.text2});

        setListAdapter(adapter);
        ContactBridge myContactBridge = new ContactBridge();
        myContactBridge.setContext(this);
        myContactBridge.sendContactsToWatch();
       adapter.notifyDataSetChanged();



    }
    public boolean validCellPhone(String number)
    {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }



    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d("LIST", "ONCKLICK");
       // View curr = l.getChildAt((int) id);
        TextView c = (TextView)v.findViewById(android.R.id.text2);
        String mobil = c.getText().toString();
      if(validCellPhone(mobil)){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobil));
        startActivity(intent);}
        else {
          Log.d("CALL", "NOT A VALID NUMBER");
      }
        //  Toast.makeText(Settings.this,playerChanged, Toast.LENGTH_SHORT).show();

    }

    public void sendInput (View view){


        EditText nameEdit = (EditText)findViewById(R.id.editText);
        EditText mobilEdit = (EditText)findViewById(R.id.mobil);
    String name   = nameEdit.getText().toString();
    String mobil   = mobilEdit.getText().toString();
        if(checkInputs(name, mobil)){
    insertContactInDb(name,mobil,1);}
        else {
            Log.d("ADD", "NOT A VALID!");
        }
}

    public boolean checkInputs (String name, String mobil){
        if(name!=""&& validCellPhone(mobil))
        {return true;}
        else {return false;}
    }

    public void dropTable(View view){
        dbManager = hdm.csm.smarthome.phone.database.DbManager.getInstance(this);
        dbManager.getWritableDatabase().execSQL(DbSchema.SQL_DROP);
        dbManager.getWritableDatabase().execSQL(DbSchema.SQL_CREATE);
        //dbManager.onCreate(db);
    showEmergencyContacts();
    }

public void insertContactInDb (String name, String mobilnummer, int priority)
{
    dbManager = hdm.csm.smarthome.phone.database.DbManager.getInstance(this);
    ContentValues values = new ContentValues();
    values.put(DbSchema.NAME, name);
    values.put(DbSchema.MOBILNUMMER, mobilnummer);
    values.put(DbSchema.PRIORITY, 1);
    Long result = dbManager.getWritableDatabase().insert(DbSchema.TABLE_NAME, null, values);
    Log.d("DB", result.toString());
    showEmergencyContacts();
}


        public void pickContact(View v)
        {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        }




    @Override public void onActivityResult(int reqCode, int resultCode, Intent data){ super.onActivityResult(reqCode, resultCode, data);
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String cNumber = "noNumber";
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones.getColumnIndex("data1"));
                            System.out.println("number is:"+cNumber);
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        insertContactInDb(name,cNumber,1);

                    }
                }
                break;
        }
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
        //let's leave this empty, for now
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
        // let's leave this empty, for now
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
}
