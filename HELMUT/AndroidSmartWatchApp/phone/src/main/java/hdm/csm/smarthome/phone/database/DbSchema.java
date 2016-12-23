package hdm.csm.smarthome.phone.database;

/**
 * Created by Michael on 29.05.2016.
 */
public final class DbSchema implements DbSchemaInterface {
    public static final String TABLE_NAME = "emergency_contacts";

    public static final String SQL_CREATE = "CREATE TABLE emergency_contacts ("+
            "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "name TEXT NOT NULL,"+
            "lookup_key TEXT,"+
            "mobilnummer TEXT,"+
            "priority INT"+
            ");";

    public static final String DEFAULT_SORT_ORDER = PRIORITY + "ASC";

    public static final String STMT_KONTAKT_INSERT =
            "INSERT INTO emercency_contacts "+
            "(name, mobilnummer)"+
            "VALUES (?,?)";

    public static final String STMT_KONTAKT_DELETE_BY_ID =
            "DELETE emercency_contacts "+
            "WHERE _id= ?";

    public static final String SQL_DROP = "DROP TABLE IF EXISTS "+ TABLE_NAME;

    private DbSchema(){

    }
}
