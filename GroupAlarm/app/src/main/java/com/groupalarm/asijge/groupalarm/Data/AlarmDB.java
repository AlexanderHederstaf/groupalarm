package com.groupalarm.asijge.groupalarm.Data;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.*;
import java.sql.*;



/**
 * Created by GabriellaHallams on 2015-04-29.
 * Updated by IsabelAzcarate on 2015-05-06
 * http://www.techotopia.com/index.php/An_Android_Studio_SQLite_Database_Tutorial
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

    public static final String COLUMN_SNOOZE = "Snooze";

    public boolean[] tmp;

    private static AlarmDB instance;
    public static void initiate(Context context) {
        if (instance == null) {
            instance = new AlarmDB(context);
        }
    }

    public static AlarmDB getInstance() {
        if (instance == null) {
            throw new InstantiationError("Not instantiated, call initiate().");
        }
        return instance;
    }

    /**
     * A constructor.
     * @param context
     */
    private AlarmDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates a new Id that does not collide with other
     * elements of the database.
     * @return Integer representing the new Id.
     */
    public int getNewId() {
        String GET_ID_ALARMS = "SELECT max(" + COLUMN_ID + ") FROM " + TABLE_ALARMS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(GET_ID_ALARMS, null);

        // If database is empty, return 0 for the first Alarm.
        int value = 0;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(0) + 1;
        }

        cursor.close();
        db.close();
        return value;
    }

    /**
     * A method for saving this Alarm in the database.
     * @param alarm An alarm to add to the database.
     */
    public void addAlarm(Alarm alarm) {
        tmp = alarm.getDays();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, alarm.getId());
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
        values.put(COLUMN_SNOOZE, alarm.getSnoozeInterval().getValue());

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
                + COLUMN_SUNDAY + " BOOLEAN" + COLUMN_SNOOZE + "INTEGER)";
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
     * Returns all the Alarms in the database as a list.
     *
     */
    public List<Alarm> getAlarms() throws SQLException {
        List<Alarm> alarmList = new LinkedList<Alarm>();
        String query = "SELECT *" + " FROM " + TABLE_ALARMS;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Alarm alarm = getAlarmFromCursor(cursor);

                alarmList.add(alarm);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return alarmList;
    }
    /**
     * Get the Alarm object for a given ID
     */
    public Alarm getAlarm(int ID) throws SQLException {
        String query = "SELECT * FROM " + TABLE_ALARMS + "WHERE " + COLUMN_ID + "=" + ID;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Alarm alarm = null;

        if (cursor != null && cursor.moveToFirst()) {
            alarm = getAlarmFromCursor(cursor);
        }

        cursor.close();
        db.close();
        return alarm;
    }

    private Alarm getAlarmFromCursor(Cursor cursor) {
        Alarm alarm = new Alarm(cursor.getInt(0));
        alarm.setMessage(cursor.getString(1));

        String alarmTime = cursor.getString(2);
        int hour = Integer.valueOf(alarmTime.substring(0, 2));
        int minute = Integer.valueOf(alarmTime.substring(5));
        alarm.setTime(hour, minute);

        alarm.setActive(Boolean.valueOf(cursor.getString(3)));

        for (int i = 4; i < 11; i++) {
            alarm.setDay(i-4, Boolean.valueOf(cursor.getString(i)));
        }

        switch (cursor.getInt(11)) {
            case 0:
                alarm.setSnoozeInterval(Alarm.Snooze.NO_SNOOZE);
                break;
            case 5:
                alarm.setSnoozeInterval(Alarm.Snooze.FIVE);
                break;
            case 10:
                alarm.setSnoozeInterval(Alarm.Snooze.TEN);
                break;
            case 15:
                alarm.setSnoozeInterval(Alarm.Snooze.FIFTEEN);
                break;
            default:
                alarm.setSnoozeInterval(Alarm.Snooze.NO_SNOOZE);
                break;
        }
        return alarm;
    }

    /**
     * Deletes an Alarm from the Alarm database.
     * @param id The Alarms unique ID.
     * @return boolean true if the deletion succeeded and false if the deletion failed.
     */
    public void deleteAlarm(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ALARMS, COLUMN_ID + " = ?", new String[] { "" + id });

        db.close();
    }
}
