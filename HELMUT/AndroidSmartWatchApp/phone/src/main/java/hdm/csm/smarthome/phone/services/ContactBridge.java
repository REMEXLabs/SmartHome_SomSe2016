package hdm.csm.smarthome.phone.services;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import hdm.csm.smarthome.common.Constants;
import hdm.csm.smarthome.common.WearCommunicationBridge;
import hdm.csm.smarthome.phone.database.DbManager;
import hdm.csm.smarthome.phone.database.DbSchema;

/**
 * Created by Michael on 09.06.2016.
 */
public class ContactBridge {
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }


    public void sendContactsToWatch(){
        DbManager dbManager = DbManager.getInstance(context);
             Cursor kontakte =
                dbManager.getWritableDatabase().query(DbSchema.TABLE_NAME,  new String[]{
                        DbSchema.ID,
                        DbSchema.NAME,
                        DbSchema.MOBILNUMMER
                }, null, null, DbSchema.MOBILNUMMER, null, null, null);
        ArrayList mArrayList = new ArrayList<String>();
        kontakte.moveToFirst();
        JsonObject jsonContacts = new JsonObject();
        JsonArray myArray = new JsonArray();
        for(kontakte.moveToFirst(); !kontakte.isAfterLast(); kontakte.moveToNext()) {
            // The Cursor is now set to the right position
            myArray.add(kontakte.getString(kontakte.getColumnIndex(DbSchema.MOBILNUMMER)));

             }
        jsonContacts.add(Constants.WEAR_CONTACTLIST,myArray);

        Log.d("TEST", "Hallo Bridge");
        Log.d("TEST", jsonContacts.toString());


        WearCommunicationBridge.getInstance(context).sendMessage(Constants.WEAR_CONTACTLIST, jsonContacts);
    }

}


