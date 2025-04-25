package com.example.testmobilecomp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "taskDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ORGANIZER = "organizer";
    private static final String COLUMN_VOLUNTEER_COUNT = "volunteerCount";
    private static final String COLUMN_STATUS = "status";

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_ORGANIZER + " TEXT, " +
                COLUMN_VOLUNTEER_COUNT + " INTEGER, " +
                COLUMN_STATUS + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public void insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, task.getId());
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_ORGANIZER, task.getOrganizer());
        values.put(COLUMN_VOLUNTEER_COUNT, task.getVolunteerCount());
        values.put(COLUMN_STATUS, task.getStatus());

        db.insert(TABLE_TASKS, null, values);
        db.close();
    }
}
