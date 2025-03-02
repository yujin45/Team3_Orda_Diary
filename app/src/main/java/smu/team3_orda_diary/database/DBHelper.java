package smu.team3_orda_diary.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import smu.team3_orda_diary.model.MapMemoItem;
import smu.team3_orda_diary.model.OnePageDiary;
import smu.team3_orda_diary.model.TodoItem;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 2;
    public static final String DB_NAME = "Ordatest5.db";

    private static volatile DBHelper instance;

    private SQLiteStatement insertTodoStmt, updateTodoStmt, deleteTodoStmt;
    private SQLiteStatement insertDiaryStmt;
    private SQLiteStatement insertAlarmStmt, updateAlarmStmt;
    private SQLiteStatement insertMapMemoStmt, deleteMapMemoStmt;

    private DBHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
        prepareStatements();
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) instance = new DBHelper(context);
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS TODOLIST_TB (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL,writeDate TEXT NOT NULL ) ");
        db.execSQL("CREATE TABLE IF NOT EXISTS DIARY_TB" +
                "(DIARY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TITLE TEXT NOT NULL, " +
                "DATE TEXT NOT NULL, " +
                "FEEL TEXT NOT NULL," +
                "PICTURE_URI TEXT NOT NULL," +
                "TEXT TEXT NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS ALARM_TB (ALARM_ID INTEGER PRIMARY KEY AUTOINCREMENT,TIME TEXT NOT NULL); ");
        db.execSQL("CREATE TABLE IF NOT EXISTS MAPMEMO_TB (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "latitude REAL NOT NULL, " +
                "longitude REAL NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS MAPMEMO_TB (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT NOT NULL, " +
                    "content TEXT NOT NULL, " +
                    "latitude REAL NOT NULL, " +
                    "longitude REAL NOT NULL);");
        }
    }

    private void prepareStatements() {
        SQLiteDatabase db = getWritableDatabase();

        insertTodoStmt = db.compileStatement("INSERT INTO TODOLIST_TB (title, content, writeDate) VALUES (?, ?, ?)");
        updateTodoStmt = db.compileStatement("UPDATE TODOLIST_TB SET title = ?, content = ?, writeDate = ? WHERE id = ?");
        deleteTodoStmt = db.compileStatement("DELETE FROM TODOLIST_TB WHERE id = ?");

        insertDiaryStmt = db.compileStatement("INSERT INTO DIARY_TB (TITLE, DATE, FEEL, PICTURE_URI, TEXT) VALUES (?, ?, ?, ?, ?)");

        insertAlarmStmt = db.compileStatement("INSERT INTO ALARM_TB (TIME) VALUES (?)");
        updateAlarmStmt = db.compileStatement("UPDATE ALARM_TB SET TIME = ?");

        insertMapMemoStmt = db.compileStatement("INSERT INTO MAPMEMO_TB (title, content, latitude, longitude) VALUES (?, ?, ?, ?)");
        deleteMapMemoStmt = db.compileStatement("DELETE FROM MAPMEMO_TB WHERE id = ?");
    }

    public ArrayList<TodoItem> getTodoList(String _writeDate) {
        ArrayList<TodoItem> todoItems = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TODOLIST_TB WHERE writeDate = ?", new String[]{_writeDate});

        while (cursor.moveToNext()) {
            TodoItem todoItem = new TodoItem();
            todoItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            todoItem.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            todoItem.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
            todoItem.setWriteDate(cursor.getString(cursor.getColumnIndexOrThrow("writeDate")));
            todoItems.add(todoItem);
        }
        cursor.close();
        return todoItems;
    }

    public void insertTodo(String title, String content, String writeDate) {
        insertTodoStmt.clearBindings();
        insertTodoStmt.bindString(1, title);
        insertTodoStmt.bindString(2, content);
        insertTodoStmt.bindString(3, writeDate);
        insertTodoStmt.executeInsert();
    }

    public void updateTodo(int _id, String _title, String _content, String _writeDate, String _beforeDate) {
        updateTodoStmt.clearBindings();
        updateTodoStmt.bindString(1, _title);
        updateTodoStmt.bindString(2, _content);
        updateTodoStmt.bindString(3, _writeDate);
        updateTodoStmt.bindLong(4, _id);
        updateTodoStmt.executeUpdateDelete();
    }

    public void deleteTodo(int id) {
        deleteTodoStmt.clearBindings();
        deleteTodoStmt.bindLong(1, id);
        deleteTodoStmt.executeUpdateDelete();
    }

    public void insertDiary(String title, String date, String feel, String pictureUri, String text) {
        insertDiaryStmt.clearBindings();
        insertDiaryStmt.bindString(1, title);
        insertDiaryStmt.bindString(2, date);
        insertDiaryStmt.bindString(3, feel);
        insertDiaryStmt.bindString(4, pictureUri);
        insertDiaryStmt.bindString(5, text);
        insertDiaryStmt.executeInsert();
    }

    public ArrayList<OnePageDiary> getDiaryList() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<OnePageDiary> onePageDiaries = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM DIARY_TB", null);
        while (cursor.moveToNext()) {
            OnePageDiary onePageDiary = new OnePageDiary(cursor.getInt(0),
                    cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5));
            onePageDiaries.add(onePageDiary);
        }
        cursor.close();
        return onePageDiaries;
    }

    public void updateAlarm(String time) {
        updateAlarmStmt.clearBindings();
        updateAlarmStmt.bindString(1, time);
        updateAlarmStmt.executeUpdateDelete();
    }

    public void insertAlarm(String time) {
        insertAlarmStmt.clearBindings();
        insertAlarmStmt.bindString(1, time);
        insertAlarmStmt.executeInsert();
    }

    public String getAlarmTime() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT TIME FROM ALARM_TB LIMIT 1", null);
        String time = cursor.moveToFirst() ? cursor.getString(0) : null;
        cursor.close();
        return time;
    }

    public void deleteAlarm(String time) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM ALARM_TB WHERE TIME = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1, time);
        stmt.executeUpdateDelete();
        stmt.close();
    }

    public void insertMapMemo(String title, String content, double latitude, double longitude) {
        insertMapMemoStmt.clearBindings();
        insertMapMemoStmt.bindString(1, title);
        insertMapMemoStmt.bindString(2, content);
        insertMapMemoStmt.bindDouble(3, latitude);
        insertMapMemoStmt.bindDouble(4, longitude);
        insertMapMemoStmt.executeInsert();
    }

    public ArrayList<MapMemoItem> getAllMapMemos() {
        ArrayList<MapMemoItem> mapMemos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MAPMEMO_TB", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

            MapMemoItem memo = new MapMemoItem(id, title, content, latitude, longitude);
            mapMemos.add(memo);
        }
        cursor.close();
        return mapMemos;
    }

    public void deleteMapMemo(int id) {
        deleteMapMemoStmt.clearBindings();
        deleteMapMemoStmt.bindLong(1, id);
        deleteMapMemoStmt.executeUpdateDelete();
    }
}
