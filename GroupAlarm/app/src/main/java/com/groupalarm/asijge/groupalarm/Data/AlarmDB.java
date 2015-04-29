package com.groupalarm.asijge.groupalarm.Data;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;


/**
 * Created by gabriellahallams on 2015-04-29.
 * http://www.techotopia.com/index.php/An_Android_Studio_SQLite_Database_Tutorial
 * Jag har ingen aning om vad jag håller på med.
 */
public class AlarmDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "alarmDB.db";
    private static final String TABLE_ALARMS = "Alarms";

    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_MESSAGE = "Message";
    public static final String COLUMN_TIME = "Time";
    public static final String COLUMN_STATUS = "Status";

    public static final String COLUMN_MONDAY = "Monday";
    public static final String COLUMN_TUESDAY = "Tuesday";
    public static final String COLUMN_WEDNESDAY = "Wednesday";
    public static final String COLUMN_THURSDAY = "Thursday";
    public static final String COLUMN_FRIDAY = "Friday";
    public static final String COLUMN_SATURDAY = "Saturday";
    public static final String COLUMN_SUNDAY = "Sunday";
    public boolean[] tmp;

    /**
     * A constructor.
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public AlarmDB(Context context, String name,
                   SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    /**
     * A method for saving this Alarm in the database.
     * @param alarm An alarm to add to the database.
     */
    public void addAlarm(Alarm alarm) {
        tmp = alarm.getDays();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, alarm.hashCode());
        values.put(COLUMN_MESSAGE, alarm.getMessage());
        values.put(COLUMN_TIME, alarm.toString());
        values.put(COLUMN_STATUS, alarm.getStatus());
        values.put(COLUMN_MONDAY, tmp[0]);
        values.put(COLUMN_TUESDAY, tmp[1]);
        values.put(COLUMN_WEDNESDAY, tmp[2]);
        values.put(COLUMN_THURSDAY, tmp[3]);
        values.put(COLUMN_FRIDAY, tmp[4]);
        values.put(COLUMN_SATURDAY, tmp[5]);
        values.put(COLUMN_SUNDAY, tmp[6]);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_ALARMS, null, values);
        db.close();
    }

    /**@Override
     * Creates the Table in the Alarm database.
    * @param db A database to which the Table is added.
     */
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALARM_TABLE = "CREATE TABLE " +
                TABLE_ALARMS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_MESSAGE
                + " TEXT," + COLUMN_TIME + " TEXT" + COLUMN_STATUS + " TEXT"
                + COLUMN_MONDAY + " BOOLEAN " + COLUMN_TUESDAY + " BOOLEAN" + COLUMN_WEDNESDAY + " BOOLEAN"
                + COLUMN_THURSDAY + " BOOLEAN" + COLUMN_FRIDAY + " BOOLEAN" + COLUMN_SATURDAY + " BOOLEAN"
                + COLUMN_SUNDAY + " BOOLEAN" + ")";
        db.execSQL(CREATE_ALARM_TABLE);
    }

    /**
     * Upgrades the Table in the Alarm database.
     * @param db A database to which the Table is added.
     * @param oldVersion The old Tables.
     * @param newVersion The new Tables replacing the old ones.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        onCreate(db);
    }


    /**
     * Deletes an Alarm from the Alarm database.
     * @param id The Alarms unique ID.
     * @return boolean true if the deletion succeeded and false if the deletion failed.
     */
    public boolean deleteAlarm(int id) {
        boolean result = false;
        String query = "Select * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_MESSAGE + " =  \"" + id + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Alarm alarm = new Alarm();

        if (cursor.moveToFirst()) {
            db.delete(TABLE_ALARMS, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(alarm.hashCode()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }
}
