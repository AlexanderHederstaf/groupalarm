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
 *
 * @author asijge
 */
public class AlarmDB extends SQLiteOpenHelper {

    private static final String TAG = "AlarmDB";

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "alarmDB.db";
    private static final String TABLE_ALARMS = "Alarms";
    private static final String TABLE_PARSE_ALARMS = "ParseAlarms";

    /**
     * The database key for the column Id.
     */
    public static final String COLUMN_ID = "_id";
    /**
     * The database key for the column ParseID.
     */
    public static final String COLUMN_PARSE_ID = "ParseID";
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
     * A private constructor creating the database helper by calling
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
        return value;
    }

    /**
     * Add a new Alarm to the database. This saves all the data.
     * To retrieve the Alarm as an Alarm object again use getAlarm()
     * with the ID of the alarm, or use getAlarms() for all Alarms.
     *
     * @param alarm An alarm to add to the database.
     */
    protected void addAlarm(Alarm alarm) {
        // All the values from the alarm must be added to the ContentValues
        // or the data will not be written to the database.

        boolean[] tmp = alarm.getDays();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, alarm.getId());
        values.put(COLUMN_PARSE_ID, alarm.getParseID());
        values.put(COLUMN_MESSAGE, alarm.getMessage());
        values.put(COLUMN_TIME, alarm.toString());
        values.put(COLUMN_STATUS, alarm.getStatus());
        values.put(COLUMN_SNOOZE, alarm.getSnoozeInterval().getValue());
        values.put(COLUMN_MONDAY, tmp[0]);
        values.put(COLUMN_TUESDAY, tmp[1]);
        values.put(COLUMN_WEDNESDAY, tmp[2]);
        values.put(COLUMN_THURSDAY, tmp[3]);
        values.put(COLUMN_FRIDAY, tmp[4]);
        values.put(COLUMN_SATURDAY, tmp[5]);
        values.put(COLUMN_SUNDAY, tmp[6]);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_ALARMS, null, values);
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
     *
     * @return A list of all alarms in the database as Alarm objects.
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
        return alarm;
    }

    /**
     * Given a cursor of a row in the database containing alarm table data an Alarm object representing the
     * database tables are returned.
     * @param cursor A cursor for a row from the alarm database table.
     * @return An Alarm object representing the database row.
     */
    private Alarm getAlarmFromCursor(Cursor cursor) {
        // The get functions from the cursor depend on the
        // relative locations of the data in the table
        // If more columns are added this must be updated.
        Alarm alarm = new Alarm(cursor.getInt(0));

        alarm.setGroupAlarm(cursor.getString(1));

        alarm.setMessage(cursor.getString(2));

        String alarmTime = cursor.getString(3);
        int hour = Integer.valueOf(alarmTime.substring(0, 2));
        int minute = Integer.valueOf(alarmTime.substring(5));
        alarm.setTime(hour, minute);

        // Using Integer value instead of boolean as DB saves 1 or 0.
        // Boolean.valueOf returns false for both these values.
        alarm.setActive(Integer.valueOf(cursor.getString(4)) == 1);

        // Set the status of the alarm using an enum. The status is not stored in the
        // database as an enum and the switch case is required.
        switch (cursor.getInt(5)) {
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

        // Set the repeating days status for all days.
        for (int i = 6; i <= 12; i++) {
            alarm.setDay(i-6, Integer.valueOf(cursor.getString(i)) == 1);
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
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_PARSE_ID + " TEXT,"
                + COLUMN_MESSAGE + " TEXT,"
                + COLUMN_TIME + " TEXT,"
                + COLUMN_STATUS + " TEXT,"
                + COLUMN_SNOOZE + " INTEGER,"
                + COLUMN_MONDAY + " BOOLEAN," + COLUMN_TUESDAY + " BOOLEAN,"
                + COLUMN_WEDNESDAY + " BOOLEAN," + COLUMN_THURSDAY + " BOOLEAN,"
                + COLUMN_FRIDAY + " BOOLEAN," + COLUMN_SATURDAY + " BOOLEAN,"
                + COLUMN_SUNDAY + " BOOLEAN" + ")";
        db.execSQL(CREATE_ALARM_TABLE);

        String CREATE_PARSE_TABLE = "CREATE TABLE " +
                TABLE_PARSE_ALARMS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_PARSE_ID
                + " TEXT" + ")";
        db.execSQL(CREATE_PARSE_TABLE);
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
