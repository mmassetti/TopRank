package tdp.miranking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DataBase adapter used to store items composed of a name and a rating
 *
 * @author Matias Massetti
 * @version Jan-2017
 */

public class DBAdapter {

    /**
     * Local variables
     */

    private static final String KEY_ROWID = "id";
    private static final String KEY_ENTITY = "entity";
    private static final String KEY_VALUATION = "valuation";
    private static final String DATABASE_NAME = "EntitiesDB";
    private static final String DATABASE_TABLE = "entities";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE =
            "create table if not exists entities (id integer primary key autoincrement, "
                    + "entity VARCHAR not null, valuation VARCHAR not null);";
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    /**
     * Creates the database adapter
     */
    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    /**
     * Opens the database
     */

    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    /**
     * Closes the database
     */

    public void close() {
        DBHelper.close();
    }

    /**
     * Inserts a new record into the database
     */

    public long insertRecord(String entity, String valuation) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ENTITY, entity);
        initialValues.put(KEY_VALUATION, valuation);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Deletes a record specified by its Id
     */

    public boolean deleteRecord(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Returns a record specified by its Id
     */

    public Cursor getRecord(long rowId) throws SQLException {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[]{KEY_ROWID,
                                KEY_ENTITY, KEY_VALUATION},
                        KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Returns all the records of the database
     */
    public Cursor getAllRecords() {
        return db.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_ENTITY,
                KEY_VALUATION}, null, null, null, null, null);
    }

    /**
     * Updates an existing record of the database
     */
    public boolean updateRecord(long rowId, String entity, String valuation) {
        ContentValues args = new ContentValues();
        args.put(KEY_ENTITY, entity);
        args.put(KEY_VALUATION, valuation);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Deletes the entire database
     */

    public void deleteDataBase() {
        context.deleteDatabase(DATABASE_NAME);

    }

    /**
     * Returns a Cursor object, which contains the results of the query performed by the string passed by parameter
     */

    public Cursor retrieve(String sequence) {
        String[] columns = {KEY_ROWID, KEY_ENTITY, KEY_VALUATION};
        Cursor c;

        if (sequence != null && sequence.length() > 0) {
            String sql = "SELECT * FROM " + DATABASE_TABLE + " WHERE " + KEY_ENTITY + " LIKE '%" + sequence + "%'";
            c = db.rawQuery(sql, null);
            return c;
        }
        c = db.query(DATABASE_TABLE, columns, null, null, null, null, null);
        return c;

    }

    /**
     * Returns a Cursor object, which contains the results of the query performed to get the elements of the base from highest to lowest valuation
     */

    public Cursor sortBestToWorst() {
        String[] valuation = new String[]{KEY_ROWID, KEY_ENTITY, KEY_VALUATION};
        Cursor c = db.query(DATABASE_TABLE, valuation, null, null, null, null, KEY_VALUATION + " DESC");
        return c;
    }

    /**
     * Returns a Cursor object, which contains the results of the query performed to get the elements of the base from lowest to highest valuation
     */

    public Cursor sortWorstToBest() {
        String[] valuation = new String[]{KEY_ROWID, KEY_ENTITY, KEY_VALUATION};
        Cursor c = db.query(DATABASE_TABLE, valuation, null, null, null, null, KEY_VALUATION + " ASC");
        return c;
    }

    /**
     * Auxiliary class to manage the database
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
}
