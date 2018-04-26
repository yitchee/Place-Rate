package c15390501.placerate;

/**
 * Created by YitChee on 04/11/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database {
    public static final String KEY_LROWID = "_id";
    public static final String KEY_LUSERNAME = "username";
    public static final String KEY_LLOCNAME = "locName";
    public static final String KEY_LLOCSTARS  = "locStars";
    public static final String KEY_LLOCTYPE  = "locType";
    public static final String KEY_LLOCADDRESS = "locAddress";
    public static final String KEY_LLATITUDE  = "latitude";
    public static final String KEY_LLONGTITUDE  = "longitude";

    public static final String KEY_UUSERNAME = "username";
    public static final String KEY_UPASSWORD = "password";
    public static final String KEY_UNUMRATED = "numPlacesRated";

    public static final String KEY_IUSERNAME = "username";
    public static final String KEY_IIMAGE = "image";
    public static final String KEY_IROWID = "_id";

    private static final String DBNAME = "Locations";
    private static final String USERTABLE = "User";
    private static final String LOCATIONTABLE = "LocationsDetails";
    private static final String IMAGETABLE = "UserImage";
    private static final int DBVERSION = 1;

    private static final String CREATE_LOCATION_TABLE =
            "create table " + LOCATIONTABLE + "(" +
                    KEY_LROWID + " integer primary key autoincrement, " +
                    KEY_LUSERNAME + " text not null, " +
                    KEY_LLOCNAME + " text not null, " +
                    KEY_LLOCSTARS + " integer not null, " +
                    KEY_LLOCTYPE + " text not null, " +
                    KEY_LLOCADDRESS + " text not null, " +
                    KEY_LLATITUDE + " real not null, " +
                    KEY_LLONGTITUDE + " real not null, "+
                    "FOREIGN KEY(username) REFERENCES User(username));";

    private static final String CREATE_USER_TABLE =
            "create table " + USERTABLE + "(" +
                    KEY_UUSERNAME + " text primary key, " +
                    KEY_UPASSWORD + " text not null, " +
                    KEY_UNUMRATED + " integer not null);";

    private static final String CREATE_IMAGE_TABLE =
            "create table " + IMAGETABLE + "(" +
                    KEY_IROWID + " integer primary key autoincrement, " +
                    KEY_IUSERNAME + " text not null, " +
                    KEY_IIMAGE + " blob, " +
                    "FOREIGN KEY(username) REFERENCES User(username));";

    private Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public Database(Context c) {
        context = c;
        DBHelper = new DatabaseHelper(context);
    }

    public Database open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        DBHelper.close();
    }

    public long createUser(String username, String password) {
        Log.i("INSERTS",username + " " + password);
        ContentValues initVal = new ContentValues();
        initVal.put(KEY_UUSERNAME, username);
        initVal.put(KEY_UPASSWORD, password);
        initVal.put(KEY_UNUMRATED, "0");

        db.execSQL("insert into " + IMAGETABLE + " (username, image) values " + "('" + username +"', null)", new String[]{});
        return db.insert(USERTABLE, null, initVal);
    }

    public long addLocation(String username, LocationInfo newLoc) {
        ContentValues initVal = new ContentValues();
        initVal.put(KEY_LUSERNAME, username);
        initVal.put(KEY_LLOCADDRESS, newLoc.getAddress());
        initVal.put(KEY_LLOCNAME, newLoc.getName());
        initVal.put(KEY_LLOCTYPE, newLoc.getType());
        initVal.put(KEY_LLOCSTARS, newLoc.getRating());
        initVal.put(KEY_LLATITUDE, newLoc.getLatitude());
        initVal.put(KEY_LLONGTITUDE, newLoc.getLongitude());

        db.execSQL("update " + USERTABLE + " set " + KEY_UNUMRATED + "=" + KEY_UNUMRATED +"+1" + " where " + KEY_UUSERNAME +
                " = '" + username + "'", new String[]{});
        return db.insert(LOCATIONTABLE, null, initVal);
    }

    public Cursor getUser(String username, String password) {
        return db.rawQuery("select " + KEY_UUSERNAME + " from " + USERTABLE + " where username = '" + username + "' and password = '" +
                password+"'", new String[] {});
    }

    public Cursor getUserInfo(String username) {
        return db.rawQuery("select " + KEY_UNUMRATED + " from " + USERTABLE + " where username = '" + username +
                "'", new String[] {});
    }

    //get list of locations
    public Cursor getLocations(String username) {
        return db.rawQuery("select * from " + LOCATIONTABLE + " where username like '" + username + "'", new String[] {});
    }

    //get individual location info
    public Cursor getLocationDetails(int id) {
        return db.rawQuery("select * from " + LOCATIONTABLE + " where _id = '" + id + "'", new String[] {});
    }

    public void deleteLocation(int id, String username) {
        //decreases user stats of number of places rated
        db.execSQL("update " + USERTABLE + " set " + KEY_UNUMRATED + "=" + KEY_UNUMRATED + "-1" + " where " + KEY_UUSERNAME +
                " = '" + username + "'", new String[]{});
        db.delete(LOCATIONTABLE, KEY_LROWID + " = ?", new String[] {String.valueOf(id)});
    }

    public void insertPicture(String username, byte[] photo) {
        ContentValues initVal = new ContentValues();
        initVal.put(KEY_IUSERNAME, username);
        initVal.put(KEY_IIMAGE, photo);

        //remove previous image and replaces with new
        db.delete(IMAGETABLE, KEY_IUSERNAME + " = ?", new String[] {String.valueOf(username)});
        db.insert(IMAGETABLE, null, initVal);
    }

    public Cursor getPicture(String username) {
        return db.rawQuery("select * from " + IMAGETABLE + " where " + KEY_IUSERNAME + " like '"
                + username + "'", new String[]{});
    }

    public void updateLocation(LocationInfo newLoc) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_LLOCNAME, newLoc.getName());
        cv.put(KEY_LLOCSTARS, newLoc.getRating());
        cv.put(KEY_LLOCTYPE, newLoc.getType());

        db.update(LOCATIONTABLE, cv, "_id=" + newLoc.getId(), null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context)
        {
            super(context, DBNAME, null, DBVERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_USER_TABLE);
            db.execSQL(CREATE_LOCATION_TABLE);
            db.execSQL(CREATE_IMAGE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //only used for other versions
        }
    }
}
