package hdm.csm.smarthome.phone.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Michael on 29.05.2016.
 */
public class DbManager extends SQLiteOpenHelper {
    private static final String DATENBANK_NAME = "emercency_contacts.db";
    private static final int DATENBANK_VERSION = 1;
    private static DbManager sINSTANCE;
    private static Object sLOCK ="";



    public static DbManager getInstance(Context context){
        if(sINSTANCE==null){
            synchronized (sLOCK){
                if(sINSTANCE==null) sINSTANCE = new DbManager(context);
            }
        }

        return sINSTANCE;
    }

    private DbManager(Context context){
        super(context, DATENBANK_NAME, null,DATENBANK_VERSION);
            }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(DbSchema.SQL_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DbSchema.SQL_DROP);
        onCreate(db);
    }


}
