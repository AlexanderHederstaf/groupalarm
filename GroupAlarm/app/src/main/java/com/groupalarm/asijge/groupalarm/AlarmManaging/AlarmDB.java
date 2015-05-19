package com.groupalarm.asijge.groupalarm.AlarmManaging;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.groupalarm.asijge.groupalarm.Data.Alarm;

import java.util.*;


/**
 * The AlarmDB is a database helper class for the database containing all alarms
 * the app saves on the phone.
 *
 * The AlarmDB is a Singleton, first call initiate(Context) then it can be reached through
 * getInstance().
 *
 * The database saves the Alarm objects and not the system service that actually sets alarms.
 * @see Alarm
 */
public class AlarmDB extends SQLiteOpenHelper {

    private static final String TAG = "AlarmDB";

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "alarmDB.db";
    private static final String TABLE_ALARMS = "Alarms";

    /**
     * The database key for the column Id.
     */
    public static final String COLUMN_ID = "_id";
    /**
     * The database key for the column Message.
     */
    public static final String COLUMN_MESSAGE = "Message";
    /**
     * The database key for the column Time.
     */
    public static final String COLUMN_TIME = "Time";
    /**
     * The database key for the column Status.
     */
    public static final String COLUMN_STATUS = "Status";
    /**
     * The database key for the column Snooze.
     */
    public static final String COLUMN_SNOOZE = "Snooze";

    /**
     * The database key for the column Monday.
     */
    public static final String COLUMN_MONDAY = "Monday";
    /**
     * The database key for the column Tuesday.
     */
    public static final String COLUMN_TUESDAY = "Tuesday";
    /**
     * The database key for the column Wednesday.
     */
    public static final String COLUMN_WEDNESDAY = "Wednesday";
    /**
     * The database key for the column Thursday.
     */
    public static final String COLUMN_THURSDAY = "Thursday";
    /**
     * The database key for the column Friday.
     */
    public static final String COLUMN_FRIDAY = "Friday";
    /**
     * The database key for the column Saturday.
     */
    public static final String COLUMN_SATURDAY = "Saturday";
    /**
     * The database key for the column Sunday.
     */
    public static final String COLUMN_SUNDAY = "Sunday";

    /**
     * Singleton instance of the AlarmDB.
     */
    private static AlarmDB instance;

    /**
     * Initiates the database to a context.
     * @param context The Context of the application.
     */
    public static void initiate(Context context) {
        if (instance == null) {
            instance = new AlarmDB(context);
        }
    }

    /**
     * Provides an instance of the database helper AlarmDB.
     *
     * @return The instance object of the AlarmDB.
     */
    protected static AlarmDB getInstance() {
        if (instance == null) {
            throw new InstantiationError("Not instantiated, call initiate().");
        }
        return instance;
    }

    /**
     * A private constructor creating the database helper by callin
     * SQLiteOpenHelper's constructor.
     *
     * @param context The Application Context.
     */
    private AlarmDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "Constructor for Database, version = " + DATABASE_VERSION);
    }

    /**
     * Creates a new Id that does not collide with other elements of the database.
     * The returned Id be a higher number than any other Id.
     *
     * @assume There are less than INT_MAX items in the database.
     * @return Integer representing the new Id.
     */
    protected int getNewId() {
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
     * Add a new Alarm to the database.
     * @param alarm An alarm to add to the database.
     */
    protected void addAlarm(Alarm alarm) {
        boolean[] tmp = alarm.getDays();
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

    /**
     * Sets the Alarm with ID = Id to active in the database.
     * Any UI or rescheduling of alarms must be done separately
     *
     * @param ID The unique Id of the alarm.
     * @param active The status the alarm should be set to.
     */
    protected void setActive(int ID, boolean active) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, active);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(TABLE_ALARMS, values, COLUMN_ID + "=" + ID, null);
    }

    /**
     * Returns all the Alarms in the database as a list.
     *
     * Modifying the alarms in this list does not change the data of the database.
     */
    protected List<Alarm> getAlarms() {
        List<Alarm> alarmList = new LinkedList<Alarm>();
        String query = "SELECT "+  "*" + " FROM " + TABLE_ALARMS;

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
     * Finds the Alarm with the given ID, if it exists, and return the Alarm.
     *
     * @param ID The ID of the alarm to find.
     * @return The Alarm with id ID, if found. Else, null.
     */
    protected Alarm getAlarm(int ID) {
        String query = "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COLUMN_ID + "=" + ID;

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

    // Provides the alarms from a cursor from the database.
    private Alarm getAlarmFromCursor(Cursor cursor) {
        Alarm alarm = new Alarm(cursor.getInt(0));
        alarm.setMessage(cursor.getString(1));

        String alarmTime = cursor.getString(2);
        int hour = Integer.valueOf(alarmTime.substring(0, 2));
        int minute = Integer.valueOf(alarmTime.substring(5));
        alarm.setTime(hour, minute);

        // Using Integer value instead as DB saves 1 or 0.
        // Boolean.valueOf returns false for both these values.
        alarm.setActive(Integer.valueOf(cursor.getString(3)) == 1);

        for (int i = 4; i < 11; i++) {
            alarm.setDay(i-4, Integer.valueOf(cursor.getString(i)) == 1);
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
     * Deletes the Alarm with id ID from the Alarm database. If the ID does not exists
     * this method does nothing.
     *
     * @param id The unique ID of the Alarm.
     */
    protected void deleteAlarm(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ALARMS, COLUMN_ID + " = ?", new String[]{"" + id});

        db.close();
    }

    /**
     * {@inheritDoc}
     *
     * Creates the Alarms Table in the database.
     * @param db A database to which the Table is added.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALARM_TABLE = "CREATE TABLE " +
                TABLE_ALARMS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_MESSAGE
                + " TEXT," + COLUMN_TIME + " TEXT," + COLUMN_STATUS + " TEXT,"
                + COLUMN_MONDAY + " BOOLEAN," + COLUMN_TUESDAY + " BOOLEAN," + COLUMN_WEDNESDAY + " BOOLEAN,"
                + COLUMN_THURSDAY + " BOOLEAN," + COLUMN_FRIDAY + " BOOLEAN," + COLUMN_SATURDAY + " BOOLEAN,"
                + COLUMN_SUNDAY + " BOOLEAN," + COLUMN_SNOOZE + " INTEGER" + ")";
        db.execSQL(CREATE_ALARM_TABLE);
    }

    /**
     * Drops the Alarms table in the Alarm database, and calls onCreate().
     * 
     * @param db The database that has been upgraded.
     * @param oldVersion The old Version number.
     * @param newVersion The new Version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        onCreate(db);
    }
}
